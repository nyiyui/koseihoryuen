package ca.nyiyui.koseihoryuen;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Player extends Actor {
    private Koseihoryuen game;
    private Body body;
    private Texture texture;

    Player(Koseihoryuen game) {
        this.game=game;
        texture = game.assetManager.get("images/player-sprite-small.png");
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.draw(texture, 8, 8);
    }
}
