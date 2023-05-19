package ca.nyiyui.koseihoryuen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;

/**
 * Draws an adjustable-width telop.
 * TODO: support showing character in an inset (like nightmelon)
 */
public class Telop extends BaseDrawable {
    private Texture tex1;
    private TiledDrawable tex1MiddleTile;
    private Texture tex2;
    private TiledDrawable tex2MiddleTile;
    /**
     * Text for top sub-box (ten).
     */
    private String tenText;
    /**
     * Text for main sub-box (ten).
     */
    private String bodyText;
    private BitmapFont tenFont;
    private BitmapFont bodyFont;
    private Koseihoryuen game;

    Telop(Koseihoryuen game) {
        super();
        this.game = game;
        tex1 = new Texture(Gdx.files.internal("images/telop1.png"));
        TextureRegion tex1Middle = new TextureRegion(tex1, tex1.getWidth() / 4, 0, tex1.getWidth() / 2, tex1.getHeight());
        tex1MiddleTile = new TiledDrawable(tex1Middle);
        tex2 = new Texture(Gdx.files.internal("images/telop2.png"));
        TextureRegion tex2Middle = new TextureRegion(tex2, tex2.getWidth() / 4, 0, tex2.getWidth() / 2, tex2.getHeight());
        tex2MiddleTile = new TiledDrawable(tex2Middle);
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.color = new Color(0xffffffff);
        param.size = 22;
        tenFont = game.font.generateFont(param);
        param.size = 28;
        bodyFont = game.font.generateFont(param);
    }

    public void setTenText(String tenText) {
        this.tenText = tenText;
    }

    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;
    }

    public void setTenFont(BitmapFont tenFont) {
        this.tenFont = tenFont;
    }

    public void setBodyFont(BitmapFont bodyFont) {
        this.bodyFont = bodyFont;
    }

    /**
     * @param width  width of the main box (doesn't include the title box)
     * @param height height of the main box (doesn't include the title box)
     */
    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {
        batch.draw(tex1, x, y, 0, 0, tex1.getWidth() / 2, tex1.getHeight());
        batch.draw(tex1, x + width - tex1.getWidth() / 2, y, tex1.getWidth() / 2, 0, tex1.getWidth() / 2, tex1.getHeight());
        tex1MiddleTile.draw(batch, x + tex1.getWidth() / 2, y, width - tex1.getWidth(), tex1.getHeight());
        final float tex2Y = y + 160;
        final float tex2X = x + 20;
        final float tex2Width = 400;
        batch.draw(tex2, tex2X, tex2Y, 0, 0, tex2.getWidth() / 2, tex2.getHeight());
        batch.draw(tex2, tex2X + tex2Width - tex2.getWidth() / 2, tex2Y, tex2.getWidth() / 2, 0, tex2.getWidth() / 2, tex2.getHeight());
        tex2MiddleTile.draw(batch, tex2X + tex2.getWidth() / 2, tex2Y, tex2Width - tex2.getWidth(), tex2.getHeight());
        if (tenText != null)
            tenFont.draw(batch, tenText, tex2X + 20, tex2Y + tex2.getHeight() - 26);
        if (bodyText != null)
            bodyFont.draw(batch, bodyText, tex2X + 20, tex2Y - 15);
    }
}
