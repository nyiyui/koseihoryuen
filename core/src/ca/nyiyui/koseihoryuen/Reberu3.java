package ca.nyiyui.koseihoryuen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

public class Reberu3 extends Reberu implements PlayableScreen {
    World world;

    public Reberu3(Koseihoryuen game) {
        super(game);
        world = new World(new Vector2(0f, -1f), true);
        setupDeadBodies();
    }

    private void setupDeadBodies() {
        BodyDef bd = new BodyDef();
        bd.type= BodyDef.BodyType.KinematicBody;
        Body body=world.createBody(bd);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        stepPhysics();
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
