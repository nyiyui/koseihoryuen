package ca.nyiyui.koseihoryuen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;

public class TitleScreen extends SelectScreen {
    private Texture backdrop;

    public TitleScreen(Koseihoryuen game) {
        super(game);
        options = new String[]{
                "Game Start",
                "Exit",
        };
//        backdrop = new Texture(Gdx.files.internal("images/title-screen-bg.png"));
    backdrop = new Texture(Gdx.files.internal("images/yukarikamome.png"));
    }

    @Override
    protected void act() {
        switch (optionSel) {
            case 0:
                game.setScreen(new LevelsScreen(game));
                break;
            case 1:
                game.setScreen(new ExitScreen(game));
//                Gdx.app.exit();
//                System.exit(0);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();
        game.batch.draw(backdrop, 0, 0);
        super.render(delta);
        game.batch.end();
    }

    @Override
    public void dispose() {
        backdrop.dispose();
    }
}
