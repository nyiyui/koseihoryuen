package ca.nyiyui.koseihoryuen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class TitleScreen extends ScreenAdapter2 {
    BitmapFont optionFont;
    BitmapFont optionSelFont;
    int optionSel = 0;
    final int OPTION_SEL_LEN = 3;
    private Texture backdrop;
    public TitleScreen(Koseihoryuen game) {
        super(game);
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size=36;
        param.borderColor = new Color(0x86cecbff);
        param.borderWidth = 3;
        param.color = new Color(0x137a7fff);
        optionSelFont =game.font.generateFont(param);
        param.color = new Color(0x666666ff);
        param.borderColor = new Color(0xbec8d1ff);
        optionFont =game.font.generateFont(param);
        backdrop = new Texture(Gdx.files.internal("title.jpg"));
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                switch (keycode) {
                    case Input.Keys.SPACE:
                    case Input.Keys.ENTER:
                        game.setScreen(new SplashScreen(game));
                    case Input.Keys.UP:
                        optionSel --;
                        optionSel+=OPTION_SEL_LEN;
                        optionSel%=OPTION_SEL_LEN;
                        break;
                    case Input.Keys.DOWN:
                        optionSel ++;
                        optionSel+=OPTION_SEL_LEN;
                        optionSel%=OPTION_SEL_LEN;
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();
        game.batch.draw(backdrop,0,0);
        final int rightSide = 800;
        final int baseY = 200;
        renderText(optionSel == 0 ? optionSelFont : optionFont,"Play", rightSide, baseY+70*2);
        renderText(optionSel == 1 ? optionSelFont : optionFont,"Practice", rightSide, baseY+70);
        renderText(optionSel == 2 ? optionSelFont : optionFont,"Game Start", rightSide, baseY);
        game.batch.end();
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }
}
