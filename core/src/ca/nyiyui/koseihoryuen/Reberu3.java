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
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class Reberu3 extends Reberu implements PlayableScreen {
    private final LineActor lineActor;
    private final Reberu3Debug reberu3Debug;
    private final Player player;
    private final BitmapFont debugFont;
    private final ExtendViewport viewport;
    private final CumulativeDelta cleanupDelta;
    private Viewport overlayViewport;
    private Stage overlayStage;
    private Camera overlayCamera;
    private Stage stage;
    private Camera cam;
    World world;
    Box2DDebugRenderer debugRenderer;
    private Body body;
    private CumulativeDelta physicsDelta;
    private CumulativeDelta spawnDelta;
    private static int PHYSICS_FPS = 60;
    private Texture textureAnanas;

    public Reberu3(Koseihoryuen game) {
        super(game);
        cam = new OrthographicCamera(16f, 13f);
        cam.update();
        // viewports, stage, and camera for main game section
        viewport = new ExtendViewport(16f, 13f, cam);
        stage = new Stage(viewport, game.batch);
        // viewports, stage, and camera for overlay section
        overlayCamera = new OrthographicCamera();
        overlayViewport = new ExtendViewport(16 * 60, 13 * 60, overlayCamera);
        overlayStage = new Stage(overlayViewport, game.batch);
        // Box2D
        debugRenderer = new Box2DDebugRenderer();
        world = new World(new Vector2(0f, -2f), true);
        setupDeadBodies();
        physicsDelta = new CumulativeDelta(1f / PHYSICS_FPS);
        player = new Player(game);
        stage.addActor(player);
        spawnDelta = new CumulativeDelta(1f / 10f);
        cleanupDelta = new CumulativeDelta(1);
        textureAnanas = new Texture(Gdx.files.internal("images/stage3-ananas.png"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 22;
        param.color = new Color(0x000000ff);
        param.borderColor = new Color(0xffffffff);
        param.borderWidth = 2;
        debugFont = game.font.generateFont(param);
        reberu3Debug = new Reberu3Debug(this);
        overlayStage.addActor(reberu3Debug);
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
    }

    @Override
    protected void handleLineSwitch() {

    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                switch (keycode) {
                    case Input.Keys.SPACE:
                    case Input.Keys.ENTER:
                        if (state != State.EXPLORE && questionDrawable.state != QuestionDrawable.State.ASKING) {
                            switchLine(curLineIndex + 1);
                            if (curLineIndex >= daishi.lines.size()) {
                                playScreen.invokePause();
                                throw new RuntimeException("not impld yet");
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

    private void setupDeadBodies() {
        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.DynamicBody;
        bd.position.set(16 / 2, 13 / 2);
        body = world.createBody(bd);
        body.applyForceToCenter(0, -2f, true);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(5, 5);
        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        fd.density = 1f;
        Fixture f = body.createFixture(fd);
        shape.dispose();
    }

    private void spawnItem() {
        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.DynamicBody;
        bd.position.set(new Random().nextFloat(0, 16), 13f);
        Body b = world.createBody(bd);
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
        stepPhysics();
        stage.getCamera().update(); // TODO: needed?
        stage.act(delta);
        stage.draw();
        overlayStage.act(delta);
        overlayStage.draw();
        game.batch.begin();
        debugRenderer.render(world, cam.combined);
        game.batch.end();
        spawn();
        game.batch.begin();
        renderDebug();
        renderText(debugFont, "body", body.getPosition().x, body.getPosition().y);
        game.batch.end();

        // cleanup
        cleanupDelta.update();
        if (cleanupDelta.ready()) {
            cleanupDelta.reset();
            cleanupBodies();
        }
    }

    private void renderDebug() {
    }

    private void renderText(String text, float x, float y) {
        renderText(debugFont, text, x, y);
    }

    private void spawn() {
        spawnDelta.update();
        if (spawnDelta.ready()) {
            spawnItem();
            spawnDelta.reset();
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

    static class Reberu3Debug extends Actor {
        private final Reberu3 reberu3;
        private Label status;

        Reberu3Debug(Reberu3 reberu3) {
            this.reberu3 = reberu3;
            Label.LabelStyle ls = new Label.LabelStyle();
            ls.font = reberu3.debugFont;
            ls.fontColor = new Color(0xffffffff);
            status = new Label("debug status", ls);
            status.setX(0);
            status.setY(0);
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            Array<Body> bodies = new Array<>();
            reberu3.world.getBodies(bodies);
            status.setText(String.format("body%d", bodies.size));
            status.draw(batch, parentAlpha);
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
