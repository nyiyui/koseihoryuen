package ca.nyiyui.koseihoryuen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Names: Ivy & Ken
 * Teacher: Ms Krasteva
 * Date: June 9, 2023
 * Purpose: ScreenAdapter with renderText helper method
 * Contributions: Ken -> everything
 */

public class ScreenAdapter2 extends ScreenAdapter {
    Koseihoryuen game;

    public ScreenAdapter2(Koseihoryuen game) {
        this.game = game;
    }

    /**
     * Renders text (centred) on game.batch.
     * @param x centre x batch coordinate
     * @param y centre y batch coordinate
     */
    protected void renderText(BitmapFont font, String text, float x, float y) {
        renderText(game.batch, font, text, x, y);
    }

    /**
     * Renders text (centred) on given batch.
     * @param x centre x batch coordinate
     * @param y centre y batch coordinate
     */
    protected void renderText(SpriteBatch batch, BitmapFont font,String text,float x, float y) {
        final GlyphLayout layout = new GlyphLayout(font, text);
        final float x2 = x - layout.width / 2;
        font.draw(batch, layout, x2, y);
    }
}
