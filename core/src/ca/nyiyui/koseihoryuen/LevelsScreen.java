package ca.nyiyui.koseihoryuen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;

public class LevelsScreen extends SelectScreen {
    LevelsScreen(Koseihoryuen game) {
        super(game);
        options = new String[]{
                "Learning",
                "Testing",
                "Gaming",
        };
        BASE_X = 200;
        BASE_Y = 200;
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
                        game.setScreen(new TitleScreen(game));
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
                game.setScreen(new PlayScreen(game,new Reberu1(game)));
                break;
                default:
                throw new RuntimeException("L bozo");
        }
    }

    @Override
    protected float getX(int i) {
        return BASE_X + i * 300;
    }

    @Override
    protected float getY(int i) {
        return BASE_Y + 200 * (options.length - i - 1);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();
        for (int i = 0; i < options.length; i++) {
            // TODO: star
            renderText(optionSel == i ? optionSelFont : optionFont, options[i], getX(i), getY(i));
        }
        game.batch.end();
    }
}
