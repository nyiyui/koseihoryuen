package ca.nyiyui.koseihoryuen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

/**
 * Names: Ivy & Ken
 * Teacher: Ms Krasteva
 * Date: June 9, 2023
 * Purpose: generic Screen for selecting multiple options (e.g. main menu, level select)
 * Contributions: Ken -> everything
 */

public abstract class SelectScreen extends ScreenAdapter2 {
    protected String options[];
    BitmapFont optionFont;
    BitmapFont optionSelFont;
    int optionSel = 0;
    protected int BASE_X = 800;
    protected int BASE_Y = 200;
    /**
     * tells user instruction for selecting options
     */
    protected Texture inst;

    public SelectScreen(Koseihoryuen game) {
        super(game);
        inst = game.assetManager.get("images/instruction2.png");
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 36;
        param.borderColor = new Color(0x86cecbff);
        param.borderWidth = 3;
        param.color = new Color(0x137a7fff);
        optionSelFont = game.font.generateFont(param);
        param.color = new Color(0x666666ff);
        param.borderColor = new Color(0xbec8d1ff);
        optionFont = game.font.generateFont(param);
    }

    @Override
    public abstract void show();

    abstract protected void act();

    /**
     * Returns the x coord of the ith entry.
     *
     * @param i entry index
     * @return
     */
    protected float getX(int i) {
        return BASE_X;
    }

    /**
     * Returns the y coord of the ith entry.
     *
     * @param i entry index
     * @return
     */
    protected float getY(int i) {
        return BASE_Y + 70 * (options.length - i - 1);
    }

    @Override
    public void render(float delta) {
        for (int i = 0; i < options.length; i++) {
            renderText(optionSel == i ? optionSelFont : optionFont, options[i], getX(i), getY(i));
        }
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        optionFont.dispose();
        optionSelFont.dispose();
    }
}
