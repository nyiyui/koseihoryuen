package ca.nyiyui.koseihoryuen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.Align;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Names: Ivy & Ken
 * Teacher: Ms Krasteva
 * Date: June 9, 2023
 * Purpose: Draws an adjustable-width telop (superimposed text in a box).
 * Contributions: Ken --> everything
 */
public class Telop extends BaseDrawable {
    private final Label.LabelStyle labelStyle;
    private Label label;
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
    /**
     * Name for persona to use.
     */
    private String personaName;
    private BitmapFont tenFont;
    private BitmapFont hintFont;
    private Koseihoryuen game;
    private Map<String, Texture> personaTexture = new HashMap<>();

    Telop(Koseihoryuen game) {
        super();
        personaTexture.put("bee", game.assetManager.<Texture>get("images/beeNPC.png"));
        this.game = game;
        tex1 = game.assetManager.get("images/telop1.png");
        TextureRegion tex1Middle = new TextureRegion(tex1, tex1.getWidth() / 4, 0, tex1.getWidth() / 2, tex1.getHeight());
        tex1MiddleTile = new TiledDrawable(tex1Middle);
        tex2 = game.assetManager.get("images/telop2.png");
        TextureRegion tex2Middle = new TextureRegion(tex2, tex2.getWidth() / 4, 0, tex2.getWidth() / 2, tex2.getHeight());
        tex2MiddleTile = new TiledDrawable(tex2Middle);
        labelStyle = new Label.LabelStyle();
        labelStyle.font = game.debugFont; // placeholder
        label = new Label(bodyText, labelStyle);
        label.setWrap(true);
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.color = new Color(0xffffffff);
        param.size = 22;
        tenFont = game.font.generateFont(param);
        param.size = 28;
        setBodyFont(game.font.generateFont(param));
        param.size = 14;
        hintFont = game.font.generateFont(param);
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

    public BitmapFont getTenFont() {
        return tenFont;
    }

    public void setBodyFont(BitmapFont bodyFont) {
        Label.LabelStyle ls = label.getStyle();
        ls.font = bodyFont;
        label.setStyle(ls);
    }

    public void setPersonaName(String personaName) {
        this.personaName = personaName;
    }

    public BitmapFont getBodyFont() {
        Label.LabelStyle ls = label.getStyle();
        return ls.font;
    }

    /**
     * @param width  width of the main box (doesn't include the title box)
     * @param height height of the main box (doesn't include the title box)
     */
    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {
        final float padding = 30;
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
        if (bodyText != null) {
            label.setText(bodyText);
            label.setPosition(x + padding, y + padding);
            label.setWidth(width - 2 * padding);
            label.setHeight(height - 2 * padding - 10);
            label.setAlignment(Align.topLeft);
            label.draw(batch, 1);
        }
        String hint = "Press [Enter] to continue";
        final GlyphLayout gl = new GlyphLayout(hintFont, hint);
        hintFont.draw(batch, hint, width - padding - gl.width, y + gl.height / 2 + padding);
        if (!Objects.equals(personaName, "")) {
            Texture tex = personaTexture.get(personaName);
            if (tex != null) {
                batch.draw(tex, 14 * 60, 3 * 60 - 20, 80, 80);
            }
        }
    }
}
