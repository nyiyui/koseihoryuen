package ca.nyiyui.koseihoryuen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

/**
 * Names: Ivy & Ken
 * Teacher: Ms Krasteva
 * Date: June 9, 2023
 * Purpose: screen shown to user exits the program
 * Contributions: Ivy --> everything
 */

public class ExitScreen extends ScreenAdapter2 {

    private Texture bee, backdrop;
    private int xPos;
    private BitmapFont byeFont;

    public ExitScreen(Koseihoryuen game) {
        super(game);
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 40;
        param.color = new Color(240, 237, 63, 1);
        byeFont = game.font.generateFont(param);
        bee = game.assetManager.get("images/beeExit.png");
        backdrop = game.assetManager.get("images/pause-bg.png");
        xPos = (int) game.camera.viewportWidth;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();
        game.batch.draw(backdrop, 0, 0);
        renderText(byeFont, "We'll BEE expecting you back soon!", game.camera.viewportWidth / 2, game.camera.viewportHeight / 4);
        if (xPos > game.camera.viewportWidth / 5 * 3 - bee.getWidth() && xPos < game.camera.viewportWidth / 5 * 4 - bee.getWidth()) {
            xPos -= (game.camera.viewportWidth / 960 * 2) * (delta * 60.0f);
        } else if (xPos <= -bee.getWidth()) {
            Gdx.app.exit();
            System.exit(0);
        } else {
            xPos -= (game.camera.viewportWidth / 960 * 50) * (delta * 60.0f);
        }
        game.batch.draw(bee, xPos, game.camera.viewportHeight / 2);
        game.batch.end();
    }

    @Override
    public void dispose() {
        bee.dispose();
        backdrop.dispose();
        byeFont.dispose();
    }
}
