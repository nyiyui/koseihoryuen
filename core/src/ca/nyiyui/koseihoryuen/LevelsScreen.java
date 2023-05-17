package ca.nyiyui.koseihoryuen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class LevelsScreen extends SelectScreen{
    LevelsScreen(Koseihoryuen game) {
        super(game);
        options = new String[]{
                "Learning",
                "Testing",
                "Gaming",
        };
    }

    @Override
    protected void act() {
        throw new RuntimeException("L bozo");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();
        super.render(delta);
        game.batch.end();
    }
}
