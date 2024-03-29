package ca.nyiyui.koseihoryuen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

/**
 * Names: Ivy & Ken
 * Teacher: Ms Krasteva
 * Date: June 9, 2023
 * Purpose: screen shown to user when choosing which stage/level to play
 * Contributions: Ivy --> extra visuals (background and funky stars), Ken --> everything else
 */

public class LevelsScreen extends SelectScreen {
    private Texture bg;
    /**
     * star for unselected options
     */
    private Texture unselStar;
    /**
     * star for selected option
     */
    private Texture selStar;

    LevelsScreen(Koseihoryuen game) {
        super(game);
        options = new String[]{
                "Learning",
                "Testing",
                "Gaming",
        };
        BASE_X = 250;
        BASE_Y = 200;
        bg = game.assetManager.get("images/yukarikamome.png");
        unselStar = game.assetManager.get("images/star1.png");
        selStar = game.assetManager.get("images/star2.png");
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
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    /**
     * switches screen to PlayScreen and plays the game
     */
    @Override
    protected void act() {
        switch (optionSel) {
            case 0:
                game.setScreen(new PlayScreen(game, new Reberu1(game)));
                break;
            case 1:
                game.setScreen(new PlayScreen(game, new Reberu2(game)));
                break;
            case 2:
                game.setScreen(new PlayScreen(game, new Reberu3(game)));
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
        game.batch.draw(bg, 0, 0);
        game.batch.draw(inst, 0, 10, 450, 450);
        for (int i = 0; i < options.length; i++) {
            final GlyphLayout layout = new GlyphLayout(optionSelFont, options[i]);
            final float offset = -64;
            if (optionSel == i) {
                game.batch.draw(selStar, getX(i) - layout.width / 2 + offset, getY(i) - unselStar.getHeight() / 2);
                renderText(optionSelFont, options[i], getX(i), getY(i));
            } else {
                game.batch.draw(unselStar, getX(i) - layout.width / 2 + offset, getY(i) - unselStar.getHeight() / 2);
                renderText(optionFont, options[i], getX(i), getY(i));
            }
        }
        game.batch.end();
    }

    @Override
    public void dispose() {
        super.dispose();
        bg.dispose();
    }
}
