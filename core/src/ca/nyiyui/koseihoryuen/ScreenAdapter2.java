package ca.nyiyui.koseihoryuen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class ScreenAdapter2 extends ScreenAdapter {
    Koseihoryuen game;

    public ScreenAdapter2(Koseihoryuen game) {
        this.game = game;
    }

    protected void renderText(BitmapFont font, String text, float x, float y) {
        final GlyphLayout layout = new GlyphLayout(font, text);
        final float x2 = x - layout.width / 2;
        font.draw(game.batch, layout, x2, y);
    }
}
