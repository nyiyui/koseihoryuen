package ca.nyiyui.koseihoryuen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class SplashScreen extends ScreenAdapter2 {
    private Texture logoVi;
    private Texture logo;
    BitmapFont presentsFont;
    private long elapsed = 0;

    public SplashScreen(Koseihoryuen game) {
        super(game);
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 24;
        param.color = new Color(0xffffffff);
        presentsFont = game.font.generateFont(param);
        logoVi = new Texture(Gdx.files.internal("images/logo-vi.png"));
        logo = new Texture(Gdx.files.internal("images/logo.png"));
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.SPACE)
                    game.setScreen(new TitleScreen(game));
                return true;
            }
        });
    }

    @Override
    public void render(float delta) {
        elapsed += (long) (delta * 1000);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        game.batch.begin();
        if (elapsed < 2000) {
            Sprite logoSprite = new Sprite(logoVi);
            if (elapsed > 1000) {
                float factor = 1.0f - (elapsed - 1000) / 1000.0f;
                logoSprite.setAlpha(factor);
                presentsFont.setColor(1, 1, 1, factor);
            }
            logoSprite.setX(200);
            logoSprite.setY(game.camera.viewportHeight / 2 - logoVi.getHeight() / 2);
            logoSprite.draw(game.batch);
            renderText(presentsFont, "proudly! presents", logoSprite.getX() + 200 + logoVi.getWidth(), game.camera.viewportHeight / 2);
            // (elapsed - 1000) / 1000.0f
            if (elapsed > 1000) {
                presentsFont.setColor(1, 1, 1, 1);
            }
        } else if (elapsed < 3000) {
        } else if (elapsed < 6000) {
            Sprite s = new Sprite(logo);
            if (elapsed < 4000) {
                s.setAlpha((elapsed - 3000) / 1000.0f);
            } else if (elapsed > 5000) {
                s.setAlpha(1.0f - (elapsed - 5000) / 1000.0f);
            }
            s.setX(game.camera.viewportWidth / 2 - logo.getWidth() / 2);
            s.setY(game.camera.viewportHeight / 2 - logo.getHeight() / 2);
            s.draw(game.batch);
        } else if (elapsed >= 7000) {
            game.setScreen(new TitleScreen(game));
        }
        game.batch.end();
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }
}
