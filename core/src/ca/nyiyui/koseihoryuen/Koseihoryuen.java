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

/**
 * Names: Ivy & Ken
 * Teacher: Ms Krasteva
 * Date: May 23, 2023
 * Purpose: main game class
 * Contributions: Ken edited this from an autogenerated template
 */

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

    public static boolean DEBUG_MODE = false;

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
        assetManager.load("images/stage2-bg.png", Texture.class);
        assetManager.load("images/stage2-city.png", Texture.class);
        assetManager.load("images/stage2-greenhouse-gas.png", Texture.class);
        assetManager.load("images/stage2-pesticide-sign.png", Texture.class);
        assetManager.load("images/player-sprite-small.png", Texture.class);
        assetManager.load("images/stage3-ananas.png", Texture.class);
        assetManager.load("images/stage3-bg.png", Texture.class);
        assetManager.load("images/stage3-bg2.png", Texture.class);
        assetManager.load("images/human-hand.png", Texture.class);
        assetManager.load("images/flycatcher.png", Texture.class);
        assetManager.load("images/pollen.png", Texture.class);
        assetManager.load("images/pesticides.png", Texture.class);
        assetManager.load("images/beeNPC.png", Texture.class);
        assetManager.load("images/stage2-rectangle.png", Texture.class);
        assetManager.load("images/stage2-correct-button.png", Texture.class);
        assetManager.load("images/stage2-wrong-button.png", Texture.class);
        assetManager.load("images/beeExit.png", Texture.class);
        assetManager.load("images/beeWrong.png", Texture.class);
        assetManager.load("images/pause-bg.png", Texture.class);
        assetManager.load("images/sneaky-enstars-ref.png", Texture.class);
        assetManager.load("images/stage1-bg.png", Texture.class);
        assetManager.load("images/stage1-pathway.png", Texture.class);
        assetManager.load("images/player-sprite-large.png", Texture.class);
        assetManager.load("images/yukarikamome.png", Texture.class);
        assetManager.load("images/telop1.png", Texture.class);
        assetManager.load("images/telop2.png", Texture.class);
        assetManager.load("images/star1.png", Texture.class);
        assetManager.load("images/star2.png", Texture.class);
        assetManager.finishLoading();
        Box2D.init();
        setScreen(new SplashScreen(this));
    }

    @Override
    public void dispose() {
        batch.dispose();
        img.dispose();
        font.dispose();
        debugFont.dispose();
    }
}
