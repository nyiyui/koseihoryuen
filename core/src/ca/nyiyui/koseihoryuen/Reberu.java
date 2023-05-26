package ca.nyiyui.koseihoryuen;

import ca.nyiyui.koseihoryuen.data.Daishi;
import ca.nyiyui.koseihoryuen.data.Line;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public abstract class Reberu extends ScreenAdapter2 implements PlayableScreen {
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

    protected static final int MOVEMENT_COEFF = 0xff;

    State state;

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

    /**
     * counts how many seconds have elapsed since player has cleared a stage.
     */
    protected float elapsedToExit = 0;
    /**
     * fonts
     */
    protected final BitmapFont titleFont;
    protected final BitmapFont subtitleFont;

    public Reberu(Koseihoryuen game) {
        super(game);
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.color = new Color(0x222222aa);
        param.size = 52;
        titleFont = game.font.generateFont(param);
        param.size = 36;
        subtitleFont = game.font.generateFont(param);
        questionDrawableTelop = new Telop(game);
        questionDrawable = new QuestionDrawable(game, questionDrawableTelop);
        playerSpriteSmall = new Texture(Gdx.files.internal("images/player-sprite-small.png"));
        playerSpriteLarge = new Texture(Gdx.files.internal("images/player-sprite-large.png"));
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
     * @param newLineIndex new line index
     */
    protected void switchLine(int newLineIndex) {
        curLineIndex = newLineIndex;
        Line cl = curLine();
        telop = new Telop(game);
        telop.setBodyText(cl.body);
        telop.setTenText(cl.ten);
        handleLineSwitch();
        if (cl.chain) switchLine(curLineIndex + 1);
        if (cl.jump != null && cl.jump.length() != 0) {
            int i = DaishiUtils.findLabel(daishi, cl.jump);
            switchLine(i);
        }
        if (cl.question != null)
            questionDrawable.loadQuestion(cl.question);
    }

    protected abstract void handleLineSwitch();

    public Daishi getDaishi() {
        return daishi;
    }

    public void setDaishi(Daishi daishi) {
        this.daishi = daishi;
    }

    /**
     * Render the questionDrawable. Assumes questionDrawable is in a renderable state.
     */
    protected void renderQuestion() {
        questionDrawable.draw(game.batch, 0, 0, game.camera.viewportWidth, game.camera.viewportHeight);
        questionDrawable.handleInput();
    }

    /**
     * See libGDX docs.
     */
    @Override
    public void dispose() {
        playScreen.dispose();
        titleFont.dispose();
        subtitleFont.dispose();
    }

    protected float clamp(float n, float upper, float lower) {
        return n < lower ? lower : (n > upper ? upper : n);
    }

    /**
     * Shows the congratulations screen.
     *
     * @param delta render delta time
     */
    public abstract void closingScreen(float delta);
}
