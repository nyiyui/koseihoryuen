package ca.nyiyui.koseihoryuen;

import ca.nyiyui.koseihoryuen.data.Line;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Objects;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Names: Ivy & Ken
 * Teacher: Ms Krasteva
 * Date: May 23, 2023
 * Purpose: stage 3
 * Contributions: Ken did all of level 3
 */

public class Reberu3 extends Reberu implements PlayableScreen {

    private final LineActor lineActor;
    private final Reberu3Debug reberu3Debug;
    private Player player;
    private final BitmapFont debugFont;
    private final Viewport viewport;
    private final CumulativeDelta cleanupDelta;
    /**
     * Queue of items to yeet (delete).
     */
    public Queue<Body> deleteQueue;
    private Viewport overlayViewport;
    private Stage overlayStage;
    private Camera overlayCamera;
    private Stage stage;
    private Camera cam;
    World world;
    Box2DDebugRenderer debugRenderer;
    private CumulativeDelta physicsDelta;
    private CumulativeDelta spawnDelta;
    private static int PHYSICS_FPS = 60;
    private Texture textureAnanas;
    private ContactListener contactListener;
    private CumulativeDelta yeetDelta;
    private Texture textureBg;
    private Texture textureBg2;
    private HUD hud;
    private float prevItemX = -1;
    private float prevItemY = 0;
    private static float PREV_ITEM_THRESHOLD = 0.1f;

    public Reberu3(Koseihoryuen game) {
        super(game);
        cam = new OrthographicCamera(16f, 13f);
        cam.update();
        // viewports, stage, and camera for main game section
        viewport = new FitViewport(16f, 13f, cam);
        stage = new Stage(viewport, game.batch);
        // viewports, stage, and camera for overlay section
        overlayCamera = new OrthographicCamera();
        overlayViewport = new ExtendViewport(16 * 60, 13 * 60, overlayCamera);
        overlayStage = new Stage(overlayViewport, game.batch);
        // Box2D
        debugRenderer = new Box2DDebugRenderer();
        world = new World(new Vector2(0, -3f), true);
        setupWalls();
        physicsDelta = new CumulativeDelta(1f / PHYSICS_FPS);
        player = new Player(game, this);
        stage.addActor(player);
        spawnDelta = new CumulativeDelta(1f / 2f);
        yeetDelta = new CumulativeDelta(1f / 60f);
        cleanupDelta = new CumulativeDelta(1);
        textureAnanas = new Texture(Gdx.files.internal("images/stage3-ananas.png"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 22;
        param.color = new Color(0x000000ff);
        param.borderColor = new Color(0xffffffff);
        param.borderWidth = 2;
        debugFont = game.font.generateFont(param);
        reberu3Debug = new Reberu3Debug(this);
        if (game.DEBUG_MODE)overlayStage.addActor(reberu3Debug);
        lineActor = new LineActor();
        overlayStage.addActor(lineActor);
        DAISHI_PATH = "daishi/reberu3.json";
        try {
            loadDaishi();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("loading daishi failed");
        }
        switchLine(0);
        deleteQueue = new LinkedBlockingQueue<>();
        setupContactListener();
        textureBg = game.assetManager.get("images/stage3-bg.png", Texture.class);
        textureBg2 = game.assetManager.get("images/stage3-bg2.png", Texture.class);
        hud = new HUD();
        overlayStage.addActor(hud);
    }

    private void reset() {
        System.out.println("RESET");
        player.reset();
        // TODO: reset bodies
    }

    private void checkClear() {
        if (hud.score() > 1000) {
            switchLine("clear");
        }
    }

    @Override
    protected void handleLineSwitch() {
        Line cl = curLine();
        if (cl.action != null) switch (cl.action) {
            case "":
                state = State.INSTRUCTIONS;
                break;
            case "explore":
                state = State.EXPLORE;
                break;
            case "complete":
                state = State.COMPLETE;
                player.body.setLinearVelocity(new Vector2());
                player.ghost();
                player.hp = 0; // bodge :)
                break;
            case "reset":
                game.setScreen(new TitleScreen(game));
                break;
        }
        if (Objects.equals(cl.label, "gameover-msg")) {
            telop.setBodyText(String.format("Sadge... You died. Your score was %d, btw.", hud.score()));
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                switch (keycode) {
                    case Input.Keys.SPACE:
                    case Input.Keys.ENTER:
                        switch (state) {
                            case INSTRUCTIONS:
                            case COMPLETE:
                                if (questionDrawable.state != QuestionDrawable.State.ASKING) {
                                    switchLine(curLineIndex + 1);
                                    if (curLineIndex >= daishi.lines.size()) {
                                        playScreen.invokePause();
                                        throw new RuntimeException("not impld yet");
                                    }
                                }
                        }
                        break;
                    case Input.Keys.ESCAPE:
                        playScreen.invokePause();
                        break;
                }
                return true;
            }
        });
    }

    private void setupWalls() {
//        setupWall(5,1f,4f,.1f);
        setupWall(5, 12.4f, 4f, .1f);
        setupWall(1f, 3.4f, .1f, 9);
        setupWall(8.8f, 3.4f, .1f, 9);
    }

    private void setupWall(float x, float y, float hx, float hy) {
        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.StaticBody;
        bd.position.set(x, y);
        Body body = world.createBody(bd);
        body.applyForceToCenter(0, -2f, true);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(hx, hy);
        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        fd.density = 1f;
        Fixture f = body.createFixture(fd);
        shape.dispose();
    }

    private void checkHp() {
        if (player.hp > 0) return;
        switchLine("gameover");
    }

    private float impulseMagnitude() {
        return 2 + player.score / 5f;
    }

    private void spawnItem() {
        float x, y;
        while (true) {
            x = new Random().nextFloat(1.8f, 8);
            y = new Random().nextFloat(11f, 12f);
            if (Math.abs(prevItemX - x) < PREV_ITEM_THRESHOLD || Math.abs(prevItemY - y) < PREV_ITEM_THRESHOLD) {
                continue;
            }
            Actor intersected = stage.hit(x, y, false);
            if (intersected != null) {
                continue;
            }
            break;
        }
        prevItemX = x;
        prevItemY = y;
        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.DynamicBody;
        bd.position.set(x, y);
        Body b = world.createBody(bd);
        final float impulseMagnitude = impulseMagnitude();
        b.applyLinearImpulse(new Random().nextFloat(-impulseMagnitude, impulseMagnitude), new Random().nextFloat(-impulseMagnitude, impulseMagnitude), 0, 0, true);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(.25f, .25f);
        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        fd.density = 1f;
        b.createFixture(fd);
        shape.dispose();
        Fallee item = new Fallee(game, b);
        stage.addActor(item);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();
        game.batch.draw(textureBg, 0, 0);
        game.batch.draw(textureBg2, 60, 60);
        game.batch.end();
        switch (state) {
            case EXPLORE:
            case COMPLETE:
                stepPhysics();
                stage.getCamera().update(); // TODO: needed?
                stage.act(delta);
                stage.draw();
                break;
        }
        overlayStage.act(delta);
        overlayStage.draw();
        game.batch.begin();
        if (game.DEBUG_MODE)debugRenderer.render(world, cam.combined);
        game.batch.end();
        spawn();
        game.batch.begin();
        renderDebug();
        game.batch.end();

        // cleanup
        cleanupDelta.update();
        if (cleanupDelta.ready()) {
            cleanupDelta.reset();
            cleanupBodies();
        }
        yeetDelta.update();
        if (yeetDelta.step()) {
            yeetBodies();
        }
        checkHp();
        checkClear();
    }

    private void setupContactListener() {
        contactListener = new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Fixture a = contact.getFixtureA();
                Fixture b = contact.getFixtureB();
                Object oA = a.getBody().getUserData();
                Object oB = b.getBody().getUserData();
                if (oA instanceof Player && oB instanceof Fallee) {
                    Player p = (Player) a.getBody().getUserData();
                    Fallee f = (Fallee) b.getBody().getUserData();
                    if (f.kind == Fallee.Kind.POLLEN) {
                        p.score += f.pointDelta();
                        p.pollenCount++;
                        f.visible = true;
                        deleteQueue.add(f.body);
                    } else if (f.kind == Fallee.Kind.HAND) {
                        p.hp--;
                    } else if (f.kind == Fallee.Kind.FLYCATCHER) {
                        p.hp--;
                        f.visible = true;
                        deleteQueue.add(f.body);
                    } else if (f.kind == Fallee.Kind.DDT) {
                        p.hp -= 2;
                        f.visible = true;
                        deleteQueue.add(f.body);
                    } else {
                        System.out.println("die");
                    }
                } else if (oA instanceof Fallee && oB instanceof Fallee) {
                    // ignore
                } else {
                    System.out.println("unknown body collision");
                    System.out.println(oA);
                    System.out.println(oB);
                }
            }

            @Override
            public void endContact(Contact contact) {
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
            }
        };
        world.setContactListener(contactListener);
    }

    private void renderDebug() {
    }

    private void renderText(String text, float x, float y) {
        renderText(debugFont, text, x, y);
    }

    private void spawn() {
        spawnDelta.update();
        if (spawnDelta.step() && (state == State.EXPLORE)) {
            spawnItem();
        }
    }

    private void cleanupBodies() {
        if (world.isLocked()) return;
        Array<Body> bodies = new Array<>();
        world.getBodies(bodies);
        for (int i = bodies.size - 1; i >= 0; i--) {
            Body b = bodies.get(i);
            Vector2 pos = b.getPosition();
            // NOTE: this is pretty inefficient, culling only twice the screen extremeties
            // assume bodies are not larger than the screen
            boolean byebye = pos.x < -16f || pos.x > 16f;
            byebye |= pos.x < -13f || pos.y > 13f;
            if (byebye) world.destroyBody(b); // delete the evidence :)
            bodies.removeIndex(i);
        }
    }

    /**
     * Yeet the bodies in deleteQueue.
     */
    private void yeetBodies() {
        while (deleteQueue.size() > 0)
            world.destroyBody(deleteQueue.poll());
    }

    private void stepPhysics() {
        physicsDelta.update();
        if (physicsDelta.ready()) {
            world.step(physicsDelta.delta(), 6, 2);
            physicsDelta.reset();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        world.dispose();
    }

    @Override
    public void closingScreen(float delta) {
        throw new RuntimeException("not yet implemented");
    }

    public void resize(int width, int height) {
        overlayViewport.update(width, height);
        overlayCamera.update();
    }

    class HUD extends Actor {
        private Label hpLabel;
        private Label bossHpLabel;
        private Label pollenCountLabel;
        private Label scoreLabel;

        HUD() {
            Label.LabelStyle ls = new Label.LabelStyle();
            ls.font = debugFont;
            ls.fontColor = new Color(0xffffffff);
            hpLabel = new Label("HP: ", ls);
            hpLabel.setX(11 * 60);
            hpLabel.setY(10 * 60);
            bossHpLabel = new Label("Boss HP: ", ls);
            bossHpLabel.setX(11 * 60);
            bossHpLabel.setY(10 * 60 - 30);
            pollenCountLabel = new Label("Pollen Count: ", ls);
            pollenCountLabel.setX(11 * 60);
            pollenCountLabel.setY(10 * 60 - 30 * 2);
            scoreLabel = new Label("Score: ", ls);
            scoreLabel.setX(11 * 60);
            scoreLabel.setY(10 * 60 - 30 * 3);
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            hpLabel.setText(String.format("HP: %d", player.hp));
            pollenCountLabel.setText(String.format("Pollen Count: %d", player.pollenCount));
            scoreLabel.setText(String.format("Score: %d", score()));
        }

        private int score() {
            return (int) (MafUtils.sigmoid(player.score / 1e3f) * 1e6 - 5e5);
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            super.draw(batch, parentAlpha);
            hpLabel.draw(batch, parentAlpha);
            bossHpLabel.draw(batch, parentAlpha);
            pollenCountLabel.draw(batch, parentAlpha);
            scoreLabel.draw(batch, parentAlpha);
        }
    }

    static class Reberu3Debug extends Actor {
        private final Reberu3 reberu3;
        private Label status;
        private Label fps;

        Reberu3Debug(Reberu3 reberu3) {
            this.reberu3 = reberu3;
            Label.LabelStyle ls = new Label.LabelStyle();
            ls.font = reberu3.debugFont;
            ls.fontColor = new Color(0xffffffff);
            status = new Label("debug status", ls);
            status.setX(0);
            status.setY(0);
            fps = new Label("fps", ls);
            fps.setY(reberu3.overlayViewport.getWorldHeight() - 30);
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            Array<Body> bodies = new Array<>();
            reberu3.world.getBodies(bodies);
            status.setText(String.format("body%d im%f", bodies.size, reberu3.impulseMagnitude()));
            status.draw(batch, parentAlpha);
            fps.setText(String.format("%d fps", Gdx.graphics.getFramesPerSecond()));
            fps.draw(batch, parentAlpha);
        }
    }

    class LineActor extends Actor {

        LineActor() {
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            Line cl = curLine();
            if (cl.ten != null || cl.body != null) {
                telop.draw(batch, 0, 0, overlayCamera.viewportWidth, 200);
            }
            if (cl.question != null) {
                questionDrawable.draw(batch, 0, 0, overlayCamera.viewportWidth, overlayCamera.viewportHeight);
                questionDrawable.handleInput();
            }
        }
    }
}
