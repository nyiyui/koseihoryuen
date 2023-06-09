package ca.nyiyui.koseihoryuen;

import ca.nyiyui.koseihoryuen.data.Daishi;
import ca.nyiyui.koseihoryuen.data.Line;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Names: Ivy & Ken
 * Teacher: Ms Krasteva
 * Date: June 9, 2023
 * Purpose: screen shown to user when choosing which stage/level to play
 * Contributions: Ken --> basically everything, Ivy --> cleaning up/debugging
 *   orz Ivy how so good tyvm for cleaning up -nyiyui
 */
public abstract class Reberu extends ScreenAdapter2 implements PlayableScreen {
    protected static final int MOVEMENT_COEFF = 0xff;
    /**
     * fonts
     */
    protected final BitmapFont titleFont;
    protected final BitmapFont subtitleFont;
    protected Daishi daishi;
    protected Telop telop;
    protected PlayScreen playScreen;
    /**
     * Index of line to currently show.
     */
    protected int curLineIndex;
    /**
     * Internal path of daishi to load.
     */
    protected String DAISHI_PATH;
    /**
     * Telop for questionDrawable.
     */
    protected Telop questionDrawableTelop;
    /**
     * Draws Line Questions.
     */
    protected QuestionDrawable questionDrawable;
    /**
     * larger player sprite
     */
    protected Texture playerSpriteLarge;
    /**
     * small player sprite
     */
    protected Texture playerSpriteSmall;
    protected float playerX;
    protected float playerY;
    protected float weightedAngle = 0f;
    /**
     * counts how many seconds have elapsed since player has cleared a stage.
     */
    protected float elapsedToExit = 0;
    State state;

    public Reberu(Koseihoryuen game) {
        super(game);
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.color = new Color(0x222222aa);
        param.size = 52;
        titleFont = game.font.generateFont(param);
        param.size = 36;
        subtitleFont = game.font.generateFont(param);
        telop = new Telop(game);
        questionDrawableTelop = new Telop(game);
        questionDrawable = new QuestionDrawable(game, questionDrawableTelop);
        playerSpriteSmall = game.assetManager.get("images/player-sprite-small.png");
        playerSpriteLarge = game.assetManager.get("images/player-sprite-large.png");
    }

    /**
     * Load daishi from DAISHI_PATH
     *
     * @throws IOException if daishi reading/decoding fails
     */
    protected void loadDaishi() throws IOException {
        ObjectMapper om = new ObjectMapper();
        daishi = om.readValue(Gdx.files.internal(DAISHI_PATH).read(), Daishi.class);
        for (int i = 1; i < daishi.lines.size(); i++)
            daishi.lines.get(i).applyDefault(daishi.lines.get(i - 1));
    }

    @Override
    public void setPlayScreen(PlayScreen ps) {
        playScreen = ps;
    }

    /**
     * Returns current Line.
     *
     * @return current Line.
     */
    protected Line curLine() {
        return daishi.lines.get(curLineIndex);
    }

    /**
     * Applies state changes to change from an unspecified to this line.
     *
     * @param label new line label
     */
    protected void switchLine(String label) {
        switchLine(DaishiUtils.findLabel(daishi, label));
    }

    /**
     * Applies state changes to change from an unspecified to this line.
     *
     * @param newLineIndex new line index
     */
    protected void switchLine(int newLineIndex) {
        curLineIndex = newLineIndex;
        Line cl = curLine();
        telop = new Telop(game);
        telop.setBodyText(cl.body);
        telop.setTenText(cl.ten);
        telop.setPersonaName(cl.persona);
        handleLineSwitch();
        if (cl.chain) switchLine(curLineIndex + 1);
        if (cl.jump != null && cl.jump.length() != 0) {
            int i = DaishiUtils.findLabel(daishi, cl.jump);
            switchLine(i);
        }
        if (cl.question != null) {
            System.out.println("asking question");
            questionDrawable.loadQuestion(cl.question);
        }
    }

    /**
     * handles line switching when program reads daishi.
     */
    protected abstract void handleLineSwitch();

    /**
     * Render the questionDrawable. Assumes questionDrawable is in a renderable state.
     */
    protected void renderQuestion() {
        questionDrawable.draw(game.batch, 0, 0, game.camera.viewportWidth, game.camera.viewportHeight);
        questionDrawable.handleInput();
    }

    protected void handleMovement(float delta) {
        boolean w = Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean a = Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean s = Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN);
        boolean d = Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        double angle = Math.sqrt(-1);
        boolean moved = true;
        if (w && a) angle = (Math.PI * 7 / 4);
        else if (a && s) angle = (Math.PI * 5 / 4);
        else if (s && d) angle = (Math.PI * 3 / 4);
        else if (d && w) angle = (Math.PI * 1 / 4);
        else if (w) angle = 0;
        else if (d) angle = (Math.PI / 2);
        else if (s) angle = (Math.PI);
        else if (a) angle = (Math.PI * 3 / 2);
        else moved = false;
        if (moved) {
            weightedAngle = (float) (weightedAngle * 0.7f + angle * 0.3f);
            playerX += Math.sin(weightedAngle) * MOVEMENT_COEFF * delta;
            playerY += Math.cos(weightedAngle) * MOVEMENT_COEFF * delta;
            playerX = clamp(playerX, game.camera.viewportWidth - playerSpriteSmall.getWidth(), 0);
            playerY = clamp(playerY, game.camera.viewportHeight - playerSpriteSmall.getHeight(), 0);
        }
    }

    /**
     * See libGDX docs.
     */
    @Override
    public void dispose() {
        playScreen.dispose();
        titleFont.dispose();
        subtitleFont.dispose();
        playerSpriteLarge.dispose();
        playerSpriteSmall.dispose();
        questionDrawable.dispose();
    }

    /**
     * keeps n within the range of [lower, upper]. Used for player coordinates.
     *
     * @param n     the coordinate the method is clamping
     * @param upper the upper limit
     * @param lower the lower limit
     * @return n, if n is in between lower and upper. Else, return lower if n < lower or return upper if n > upper
     */
    protected float clamp(float n, float upper, float lower) {
        return n < lower ? lower : (n > upper ? upper : n);
    }

    /**
     * Shows the congratulations screen.
     *
     * @param delta render delta time
     */
    public abstract void closingScreen(float delta);

    enum State {
        /**
         * Show instructions.
         */
        INSTRUCTIONS,
        /**
         * Exploring the stage (only levels 1 and 2).
         */
        EXPLORE,
        /**
         * Level completed.
         */
        COMPLETE,
        /**
         * Use state defined by subclass' state variable.
         */
        CUSTOM
    }
}
