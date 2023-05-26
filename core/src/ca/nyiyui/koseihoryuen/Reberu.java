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

    protected final static int STATE_INST = 1;
    protected final static int STATE_EXPLORE = 2;
    protected final static int STATE_COMPLETE = 3;

    /**
     * counts how many seconds have elapsed since player has cleared a stage.
     */
    protected float elapsedToExit = 0;
    /**
     * fonts
     */
    protected final BitmapFont titleFont;
    protected final BitmapFont subtitleFont;
    protected float playerX, playerY;
    protected double weightedAngle = 0;
    protected static final float MOVEMENT_COEFF = 0xff;
    protected Texture playerSpriteSmall;
    protected Texture playerSpriteLarge;

    public Reberu(Koseihoryuen game) {
        super(game);
        playerSpriteSmall = new Texture(Gdx.files.internal("images/player-sprite-small.png"));
        playerSpriteLarge = new Texture(Gdx.files.internal("images/player-sprite-large.png"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.color = new Color(0x222222aa);
        param.size = 52;
        titleFont = game.font.generateFont(param);
        param.size = 36;
        subtitleFont = game.font.generateFont(param);
    }

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

    protected Line curLine() {
        return daishi.lines.get(curLineIndex);
    }

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
    }

    protected abstract void handleLineSwitch();

    public Daishi getDaishi() {
        return daishi;
    }

    public void setDaishi(Daishi daishi) {
        this.daishi = daishi;
    }

    @Override
    public abstract void dispose();

    public void closingScreen(float delta) {
        elapsedToExit += delta;
        renderText(titleFont, "Congratulations!", game.camera.viewportWidth / 2, game.camera.viewportHeight / 2);
        renderText(subtitleFont, "You finished this level.", game.camera.viewportWidth / 2, game.camera.viewportHeight / 2 - 50);
        if (elapsedToExit > 4f)
            game.setScreen(new TitleScreen(game));
    }

    protected float clamp(float n, float upper, float lower) {
        if (n > upper) return upper;
        if (n < lower) return lower;
        return n;
    }


}
