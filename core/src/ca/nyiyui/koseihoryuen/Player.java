package ca.nyiyui.koseihoryuen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Player extends Actor {
    private static final float MOVEMENT_COEFF = 200;
    private final Reberu3 reberu3;
    private Koseihoryuen game;
    private Body body;
    private Texture texture;
    /**
     * Points this player has.
     */
    float point;

    Player(Koseihoryuen game, final Reberu3 reberu3) {
        this.game = game;
        this.reberu3 = reberu3;
        texture = game.assetManager.get("images/player-sprite-small.png");
        setupBody();
        body.setUserData(this);
    }

    private void setupBody() {
        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.KinematicBody;
        bd.position.set(16 / 2, 13 / 2);
        body = reberu3.world.createBody(bd);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(.5f, .5f);
        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        fd.density = 1f;
        Fixture f = body.createFixture(fd);
        shape.dispose();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        Vector2 pos = body.getPosition();
        batch.draw(texture, pos.x - .5f, pos.y - .5f, 1, 1);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (reberu3.state == Reberu.State.EXPLORE)
            handleMovement(delta);
    }

    private void handleMovement(float delta) {
        boolean w = Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean a = Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean s = Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN);
        boolean d = Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        double angle = Math.sqrt(-1);
        boolean moved = true;
        if (w && a) angle = (Math.PI * 7 / 4);
        else if (a && s) angle = (Math.PI * 5 / 4);
        else if (s && d) angle = (Math.PI * 3 / 4);
        else if (d && w) angle = (Math.PI * 1 / 4);
        else if (w) angle = 0;
        else if (d) angle = (Math.PI / 2);
        else if (s) angle = (Math.PI);
        else if (a) angle = (Math.PI * 3 / 2);
        else moved = false;
        if (moved) {
            float x = (float) (Math.sin(angle) * MOVEMENT_COEFF * delta);
            float y = (float) (Math.cos(angle) * MOVEMENT_COEFF * delta);
            body.setLinearVelocity(x, y);
        } else {
            body.setLinearVelocity(new Vector2());
        }
    }
}
