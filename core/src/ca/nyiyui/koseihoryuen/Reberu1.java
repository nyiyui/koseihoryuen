package ca.nyiyui.koseihoryuen;

import ca.nyiyui.koseihoryuen.data.Daishi;
import ca.nyiyui.koseihoryuen.data.Line;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class Reberu1 extends ScreenAdapter2 implements PlayableScreen {
    private Box2DDebugRenderer debugRenderer;
    private Stage stage;
    private PlayScreen playScreen;
    private Daishi daishi;
    /**
     * Index of line to currently show.
     */
    private int curLineIndex;
    private Texture background;
    private Texture pathway;
    private Music music;
    private Telop telop;
    private final static int STATE_INST = 1;
    private final static int STATE_EXPLORE = 2;
    private final static int STATE_COMPLETE = 3;
    private int state = STATE_INST;
    private World world;
    /**
     * Frame time accumulator for Box2D timestep.
     * Used to specify a max delta time for physics simulation
     */
    private float box2dFrameAccum = 0;
    private Body ballBody;
    private Body player;

    Reberu1(Koseihoryuen game) {
        super(game);
        stage = new Stage(new FillViewport(game.camera.viewportWidth, game.camera.viewportHeight, game.camera), game.batch);
        try {
            loadDaishi();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("loading daishi failed");
        }
        switchLine(0);
        background = new Texture(Gdx.files.internal("images/stage1-bg.png"));
        pathway = new Texture(Gdx.files.internal("images/stage1-pathway.png"));
        setupBox2D();
    }

    private void setupBox2D() {
        world = new World(new Vector2(0, 10), true);
        debugRenderer = new Box2DDebugRenderer();
        debugRenderer.setDrawAABBs(true);

        // First we create a body definition
        BodyDef bodyDef = new BodyDef();
// We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
        bodyDef.type = BodyDef.BodyType.DynamicBody;
// Set our body's starting position in the world
        bodyDef.position.set(10, 100);

// Create our body in the world using our body definition
        ballBody = world.createBody(bodyDef);

// Create a circle shape and set its radius to 6
        CircleShape circle = new CircleShape();
        circle.setRadius(6f);

// Create a fixture definition to apply our shape to
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.6f; // Make it bounce a little bit

// Create our fixture and attach it to the body
        Fixture fixture = ballBody.createFixture(fixtureDef);

// Remember to dispose of any shapes after you're done with them!
// BodyDef and FixtureDef don't need disposing but shapes do.
        circle.dispose();

        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.position.set(new Vector2(0, 0));
        Body groundBody = world.createBody(groundBodyDef);
        PolygonShape groundBox = new PolygonShape();
        groundBox.setAsBox(game.camera.viewportWidth/2, game.camera.viewportHeight/2);
        groundBody.createFixture(groundBox, 0.0f);
        groundBox.dispose();

        bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(10, 400);
        player = world.createBody(bodyDef);
        circle = new CircleShape();
        circle.setRadius(50f);
        fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.6f;
        fixture = player.createFixture(fixtureDef);
        circle.dispose();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                switch (keycode) {
                    case Input.Keys.SPACE:
                    case Input.Keys.ENTER:
                        switchLine(curLineIndex + 1);
                        if (curLineIndex >= daishi.lines.size()) {
                            playScreen.invokePause();
                            throw new RuntimeException("not impld yet");
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

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    private void loadDaishi() throws IOException {
        ObjectMapper om = new ObjectMapper();
        daishi = om.readValue(Gdx.files.internal("daishi/reberu1.json").read(), Daishi.class);
        for (int i = 1; i < daishi.lines.size(); i++)
            daishi.lines.get(i).applyDefault(daishi.lines.get(i - 1));
    }

    @Override
    public void setPlayScreen(PlayScreen ps) {
        playScreen = ps;
    }

    private Line curLine() {
        return daishi.lines.get(curLineIndex);
    }

    private void switchLine(int newLineIndex) {
        curLineIndex = newLineIndex;
        Line cl = curLine();
        if (music != null) music.dispose();
        if (cl.sound != null && !cl.sound.equals("")) {
            music = Gdx.audio.newMusic(Gdx.files.internal(cl.sound));
            music.setLooping(true);
            music.play();
            // TODO: sound doesn't play
        }
        telop = new Telop(game);
        telop.setBodyText(cl.body);
        telop.setTenText(cl.ten);
        if (cl.action != null)
            switch (cl.action) {
                case "explore":
                    state = STATE_EXPLORE;
                    break;
            }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        renderBox2D(delta);
        game.batch.begin();
//        game.batch.draw(background, 0, 0);
        if (state == STATE_EXPLORE) {
//            game.batch.draw(pathway, 0, 0);
            Vector2 pos = ballBody.getPosition();
            Vector2 vel = ballBody.getLinearVelocity();
            if (Gdx.input.isKeyPressed(Input.Keys.A))
                ballBody.setLinearVelocity(-1.0f,0f);
            if (Gdx.input.isKeyPressed(Input.Keys.D))
                ballBody.setLinearVelocity(1.0f,0f);
//                ballBody.applyForce(100.0f, 0.0f, pos.x, pos.y, true);
        }
        if (curLine().body != null && !curLine().body.equals(""))
            telop.draw(game.batch, 0, 0, game.camera.viewportWidth, 200);
        Vector2 pos = ballBody.getPosition();
        renderText(game.debugFont, "ball", pos.x, pos.y);
        pos=player.getPosition();
        renderText(game.debugFont, "player", pos.x, pos.y);
        game.batch.end();
        debugRenderer.render(world, game.camera.combined);
    }

    private void renderBox2D(float delta) {
        float frameTime = Math.min(delta, 0.25f);
        box2dFrameAccum += frameTime;
        while (box2dFrameAccum >= Settings.BOX2D_TIME_STEP) {
            world.step(Settings.BOX2D_TIME_STEP, Settings.BOX2D_VELOCITY_ITERATIONS, Settings.BOX2D_POSITION_ITERATIONS);
            box2dFrameAccum -= Settings.BOX2D_TIME_STEP;
        }
        debugRenderer.render(world, game.camera.combined);
    }

    public Daishi getDaishi() {
        return daishi;
    }

    public void setDaishi(Daishi daishi) {
        this.daishi = daishi;
    }

    @Override
    public void dispose() {
        debugRenderer.dispose();
        world.dispose();
    }
}