package ca.nyiyui.koseihoryuen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Reberu3 extends Reberu implements PlayableScreen {
    World world;
    Box2DDebugRenderer debugRenderer;
    private Body body;

    public Reberu3(Koseihoryuen game) {
        super(game);
        world = new World(new Vector2(0f, -10f), true);
        debugRenderer=new Box2DDebugRenderer();
        setupDeadBodies();
    }

    private void setupDeadBodies() {
        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.KinematicBody;
        bd.position.set(100,100);
        body = world.createBody(bd);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(20, 10);
        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        fd.density = 1f;
        Fixture f = body.createFixture(fd);
        shape.dispose();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        game.batch.begin();
        Gdx.gl.glClearColor(0,0,0,0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        // TODO: clamp physics step rate
        stepPhysics();
        debugRenderer.render(world,game.camera.combined);
//        renderText(game.debugFont, "body", body.getPosition().x, body.getPosition().y);
        game.batch.end();
    }

    private void stepPhysics() {
        world.step(Gdx.graphics.getDeltaTime(), 6, 2);
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
