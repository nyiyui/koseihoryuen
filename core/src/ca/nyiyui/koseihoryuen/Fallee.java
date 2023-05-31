package ca.nyiyui.koseihoryuen;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import java.util.Random;

/**
 * A single item (e.g. a hand) that falls.
 */
public class Fallee extends Actor {
    private final Koseihoryuen game;
    private Kind kind;
    private Body body;
    private Texture texture;

    enum Kind {
        FLYCATCHER,
        HAND,
        POLLEN,
        DDT
    }

    public Fallee(Koseihoryuen game, Body body) {
        this.body = body;
        this.game=game;
        kind = chooseKind();
        loadTexture();
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
                texture = game.assetManager.get("images/pollen.png");
                break;
            default:
                texture = game.assetManager.get("images/stage3-ananas.png");
        }
    }

    private Kind chooseKind() {
        switch (new Random().nextInt(5)) {
            case 0:
                return Kind.FLYCATCHER;
            case 1:
                return Kind.HAND;
            case 2:
                return Kind.POLLEN;
            case 3:
                return Kind.DDT;
            default:
                return Kind.HAND;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        Vector2 pos = body.getPosition();
        batch.draw(texture, pos.x - .25f, pos.y - .25f, .5f, .5f);
    }
}
