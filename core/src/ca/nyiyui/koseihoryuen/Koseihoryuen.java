package ca.nyiyui.koseihoryuen;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g3d.decals.SimpleOrthoGroupStrategy;
import com.badlogic.gdx.utils.ScreenUtils;

public class Koseihoryuen extends Game {
    OrthographicCamera camera;
    SpriteBatch batch;
    Texture img;
    FreeTypeFontGenerator font;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 960, 780);
        batch = new SpriteBatch();
        img = new Texture("badlogic.jpg");
        font = new FreeTypeFontGenerator(Gdx.files.internal("fonts/RobotoMono/RobotoMono-VariableFont_wght.ttf"));
//        setScreen(new SplashScreen(this));
        setScreen(new PlayScreen(this, new Reberu1(this)));
    }
//
//    @Override
//    public void render() {
//        ScreenUtils.clear(1, 0, 0, 1);
//        batch.begin();
//        batch.draw(img, 0, 0);
//        batch.end();
//    }

    @Override
    public void dispose() {
        batch.dispose();
        img.dispose();
        font.dispose();
    }
}
