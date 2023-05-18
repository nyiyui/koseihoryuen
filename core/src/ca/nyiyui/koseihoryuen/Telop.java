package ca.nyiyui.koseihoryuen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GLTexture;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class Telop extends BaseDrawable {
    private Texture tex1;
    private TextureRegion tex1Middle;
    private Texture tex2;

    Telop() {
        super();
        tex1 = new Texture(Gdx.files.internal("images/telop1.png"));
        tex2 = new Texture(Gdx.files.internal("images/telop2.png"));
        tex1Middle = new TextureRegion(tex1, tex1.getWidth() / 4, 0, tex1.getWidth() / 2, tex1.getHeight());
        tex1Middle.
    }

    /**
     * @param batch
     * @param x
     * @param y
     * @param width  width of the main box (doesn't include the title box)
     * @param height height of the main box (doesn't include the title box)
     */
    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {
        batch.draw(tex1, x, y, 0, 0, tex1.getWidth() / 2, tex1.getHeight());
        batch.draw(tex1, x + width - tex1.getWidth() / 2, y, tex1.getWidth() / 2, 0, tex1.getWidth() / 2, tex1.getHeight());
        batch.draw(tex1, x + width - tex1.getWidth() / 2, y, tex1.getWidth() / 2, 0, tex1.getWidth() / 2, tex1.getHeight());
        batch.draw(tex1Middle, x + width - tex1.getWidth() / 2, y, 0, 0, width - tex1.getWidth(), tex1.getHeight(), 1, 1, 0);
        batch.draw(tex2, x + 20, y + tex1.getHeight() - 40);
    }
}
