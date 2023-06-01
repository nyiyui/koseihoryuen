package ca.nyiyui.koseihoryuen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.physics.box2d.Box2D;

public class Koseihoryuen extends Game {
    OrthographicCamera camera;
    SpriteBatch batch;
    Texture img;
    FreeTypeFontGenerator font;
    /**
     * Font for printing debug statements.
     */
    BitmapFont debugFont;
    AssetManager assetManager;

    public static boolean DEBUG_MODE = true;

    @Override
    public void create() {
        camera = new OrthographicCamera(16 * 60, 13 * 60);
        batch = new SpriteBatch();
        img = new Texture("badlogic.jpg");
        font = new FreeTypeFontGenerator(Gdx.files.internal("fonts/RobotoMono/RobotoMono-VariableFont_wght.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 12;
        param.color = new Color(0x000000ff);
        param.borderColor = new Color(0xffffffff);
        param.borderWidth = 2;
        debugFont = font.generateFont(param);
        assetManager = new AssetManager();
        assetManager.load("images/player-sprite-small.png", Texture.class);
        assetManager.load("images/stage3-ananas.png", Texture.class);
        assetManager.load("images/human-hand.png", Texture.class);
        assetManager.load("images/flycatcher.png", Texture.class);
        assetManager.load("images/pollen.png", Texture.class);
        if (DEBUG_MODE) assetManager.finishLoading();
        Box2D.init();
//        setScreen(new SplashScreen(this));
//        setScreen(new PlayScreen(this, new Reberu2(this)));
        setScreen(new PlayScreen(this, new Reberu3(this)));
    }

    @Override
    public void dispose() {
        batch.dispose();
        img.dispose();
        font.dispose();
        debugFont.dispose();
    }
}
