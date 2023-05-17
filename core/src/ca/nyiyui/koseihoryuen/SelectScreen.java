package ca.nyiyui.koseihoryuen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public abstract class SelectScreen extends ScreenAdapter2 {
    protected String options[];
    BitmapFont optionFont;
    BitmapFont optionSelFont;
    int optionSel = 0;

    public SelectScreen(Koseihoryuen game) {
        super(game);
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
    public void show() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                switch (keycode) {
                    case Input.Keys.SPACE:
                    case Input.Keys.ENTER:
                        act();
                    case Input.Keys.UP:
                        optionSel--;
                        optionSel += options.length;
                        optionSel %= options.length;
                        break;
                    case Input.Keys.DOWN:
                        optionSel++;
                        optionSel += options.length;
                        optionSel %= options.length;
                        break;
                }
                return true;
            }
        });
    }

    abstract protected void act();

    @Override
    public void render(float delta) {
        final int rightSide = 800;
        final int baseY = 200;
        for (int i = 0; i < options.length; i++) {
            renderText(optionSel == i ? optionSelFont : optionFont, options[i], rightSide, baseY + 70 * (options.length - i - 1));
        }
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }
}
