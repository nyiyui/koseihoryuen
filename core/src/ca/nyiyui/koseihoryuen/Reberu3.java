package ca.nyiyui.koseihoryuen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class Reberu3 extends Reberu implements PlayableScreen {
    private final Player player;
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
        stage = new Stage(new ExtendViewport(16f , 13f, cam), game.batch);
        world = new World(new Vector2(0f, -2f), true);
        debugRenderer = new Box2DDebugRenderer();
        setupDeadBodies();
        physicsDelta = new CumulativeDelta(1f / PHYSICS_FPS);
        spawnDelta = new CumulativeDelta(1f/10f);
        player=new Player(game);
        stage.addActor(player);
        new Timer().scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                cleanupBodies();
            }
        }, 1, 1, -1);
        textureAnanas = new Texture(Gdx.files.internal("images/stage3-ananas.png"));
    }

    private void setupDeadBodies() {
        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.DynamicBody;
        bd.position.set(16 / 2, 13 / 2);
        body = world.createBody(bd);
        body.applyForceToCenter(0,-2f,true);
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
        bd.position.set(8f, 13f);
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
        stage.getCamera().update();
        stage.act(delta);
        stage.draw();
        game.batch.begin();
        debugRenderer.render(world, cam.combined);
        spawn();
        System.out.println(stage.getActors().size);
//        renderText(game.debugFont, "body", body.getPosition().x, body.getPosition().y);
        game.batch.end();
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
        System.out.println(bodies.size);
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
    protected void handleLineSwitch() {
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
}
