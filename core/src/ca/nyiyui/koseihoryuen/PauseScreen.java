package ca.nyiyui.koseihoryuen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

/**
 * Names: Ivy & Ken
 * Teacher: Ms. Krasteva
 * Date: Jun 9th, 2023
 * Purpose: screen shown to user when game is paused
 * Contribution: Ivy --> visuals, comments, added functionality. Ken --> general class structure (most methods)
 */
public class PauseScreen extends ScreenAdapter2 implements PlayableScreen {
    private PlayScreen playScreen;
    /**
     * image for background
     */
    private Texture bg;
    /**
     * funky extra visuals (zany emoji)
     */
    private Texture bee;
    /**
     * font for text displayed to user
     */
    private BitmapFont font;

    public PauseScreen(Koseihoryuen game) {
        super(game);
        bg = new Texture(Gdx.files.internal("images/pause-bg.png"));
        bee = new Texture(Gdx.files.internal("images/sneaky-enstars-ref.png"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.color = new Color(Color.BLACK);
        param.size = 36;
        param.borderColor = new Color(Color.WHITE);
        param.borderWidth = 3;
        font = game.font.generateFont(param);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 1, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();
        game.batch.draw(bg, 0, 0);
        game.batch.draw(bee, 0, 0);
        renderText(font, "Game Paused.", game.camera.viewportWidth / 2, game.camera.viewportHeight / 2 + 100);
        renderText(font, "Press ESC to go back to the game.", game.camera.viewportWidth / 2, game.camera.viewportHeight / 2);
        renderText(font, "Press ENTER to go back to Main Menu.", game.camera.viewportWidth / 2, game.camera.viewportHeight / 2 - 100);
        game.batch.end();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                switch (keycode) {
                    case Input.Keys.ESCAPE:
                        playScreen.invokePlay();
                    case Input.Keys.ENTER:
                        game.setScreen(new TitleScreen(game));
                }
                return true;
            }
        });
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void setPlayScreen(PlayScreen ps) {
        playScreen = ps;
    }

    @Override
    public void dispose() {
        super.dispose();
        bg.dispose();
    }
}
