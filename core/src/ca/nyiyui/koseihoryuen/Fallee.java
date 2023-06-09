package ca.nyiyui.koseihoryuen;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import java.util.Random;

/**
 * Names: Ivy & Ken
 * Teacher: Ms Krasteva
 * Date: May 23, 2023
 * Purpose: stage 3 fallee (items like hands and pollen)
 * Contributions: Ken did all of this class
 */

/**
 * A single item (e.g. a hand) that falls.
 */
public class Fallee extends Actor {
    private final Koseihoryuen game;
    public boolean visible;
    Kind kind;
    Body body;
    private Texture texture;
    private Sprite sprite;

    enum Kind {
        FLYCATCHER,
        HAND,
        POLLEN,
        DDT
    }

    public Fallee(Koseihoryuen game, Body body) {
        this.body = body;
        this.game = game;
        visible = true;
        kind = chooseKind();
        loadTexture();
        sprite=new Sprite(texture);
        body.setUserData(this);
    }

    /**
     * Returns the points added/removed from the player on contact.
     *
     * @return delta point value
     */
    float pointDelta() {
        return 1;
    }

    private void loadTexture() {
        switch (kind) {
            case FLYCATCHER:
                texture = game.assetManager.get("images/flycatcher.png");
                break;
            case HAND:
                texture = game.assetManager.get("images/human-hand.png");
                break;
            case POLLEN:
                texture = game.assetManager.get("images/pollen.png");
                break;
            case DDT:
                texture = game.assetManager.get("images/pesticides.png");
                break;
            default:
                texture = game.assetManager.get("images/stage3-ananas.png");
        }
    }

    private Kind chooseKind() {
        switch (new Random().nextInt(4)) {
            case 0:
                return Kind.FLYCATCHER;
            case 1:
                return Kind.HAND;
            case 2:
                return Kind.POLLEN;
            case 3:
                return Kind.DDT;
            default:
                throw new RuntimeException("unexpected random value");
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (!visible) return;
        Vector2 pos = body.getPosition();

        sprite.setBounds(pos.x - .25f,pos.y-.25f,.5f,.5f);
//        sprite.setRotation((float) Math.toDegrees(body.getTransform().getRotation()));
        sprite.draw(batch);
    }
}
