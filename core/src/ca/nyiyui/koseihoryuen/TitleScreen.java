package ca.nyiyui.koseihoryuen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;

/**
 * Names: Ivy & Ken
 * Teacher: Ms. Krasteva
 * Date: Jun 9th, 2023
 * Purpose: Title screen / main menu!
 * Contribution: Ken -> everything
 */

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
    public void show() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                switch (keycode) {
                    case Input.Keys.SPACE:
                    case Input.Keys.ENTER:
                        act();
                    case Input.Keys.UP:
                    case Input.Keys.LEFT:
                        optionSel--;
                        optionSel += options.length;
                        optionSel %= options.length;
                        break;
                    case Input.Keys.DOWN:
                    case Input.Keys.RIGHT:
                        optionSel++;
                        optionSel += options.length;
                        optionSel %= options.length;
                        break;
                    case Input.Keys.ESCAPE:
                        optionSel = 1;
                        break;
                }
                return true;
            }
        });
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
