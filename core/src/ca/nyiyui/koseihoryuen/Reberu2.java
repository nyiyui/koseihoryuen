package ca.nyiyui.koseihoryuen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FillViewport;

/**
 * Names: Ivy & Ken
 * Teacher: Ms Krasteva
 * Date: May 23, 2023
 * Purpose: runs when user plays the Testing stage.
 * Contributions: Ivy
 */

public class Reberu2 extends ScreenAdapter2 implements PlayableScreen {
    private Stage stage;
    private PlayScreen playScreen;
    private Texture bg;

    public Reberu2(Koseihoryuen game) {
        super(game);
        stage = new Stage(new FillViewport(game.camera.viewportWidth, game.camera.viewportHeight, game.camera), game.batch);
        bg = new Texture(Gdx.files.internal("images/stage2-bg.png"));
    }

    @Override
    public void setPlayScreen(PlayScreen ps) {
        playScreen = ps;
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();
        game.batch.draw(bg, 0, 0);
        // TODO: put instructions here when dialogue is written


        game.batch.end();
    }
}
