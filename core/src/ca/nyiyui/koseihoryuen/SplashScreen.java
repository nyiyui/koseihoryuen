package ca.nyiyui.koseihoryuen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Texture;

public class SplashScreen extends ScreenAdapter2 {
    private Texture splash;

    public SplashScreen(Koseihoryuen game) {
        super(game);
        splash = new Texture(Gdx.files.internal("splash.jpg"));
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
        game.batch.begin();
        game.batch.draw(splash, 0, 0);
        game.batch.end();
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }
}
