package ca.nyiyui.koseihoryuen;

import ca.nyiyui.koseihoryuen.data.Daishi;
import ca.nyiyui.koseihoryuen.data.Line;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Names: Ivy & Ken
 * Teacher: Ms Krasteva
 * Date: May 23, 2023
 * Purpose: runs when user plays the Testing stage.
 * Contributions: Ivy
 */

public class Reberu2 extends ScreenAdapter2 implements PlayableScreen {
    private Stage stage;
    private static final float MOVEMENT_COEFF = 10;
    private PlayScreen playScreen;
    /**
     * images for background, player, and interactable objects.
     */
    private Texture bg, player, playerL, itemCity, itemGas, itemPest;
    private boolean exploring;
    /**
     * stage dialogue
     */
    private Daishi daishi;
    /**
     * Index of line to currently show.
     */
    private int curLineIndex;
    private Telop telop;
    /**
     * x- and y- coordinates of the player.
     */
    private float playerX, playerY;
    private double weightedAngle = 0;

    public Reberu2(Koseihoryuen game) {
        super(game);
        stage = new Stage(new FillViewport(game.camera.viewportWidth, game.camera.viewportHeight, game.camera), game.batch);
//        try {
//            loadDaishi();
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException("that's a youproblem ;)");
//        }
//        switchLine(0);
        bg = new Texture(Gdx.files.internal("images/stage2-bg.png"));
        player = new Texture(Gdx.files.internal("images/player-sprite-small.png"));
        playerL = new Texture(Gdx.files.internal("images/player-sprite-large.png"));
        itemCity = new Texture(Gdx.files.internal("images/stage2-city.png"));
        itemGas = new Texture(Gdx.files.internal("images/stage2-greenhouse-gas.png"));
        itemPest = new Texture(Gdx.files.internal("images/stage2-pesticide-sign.png"));
        exploring = true;
    }

    @Override
    public void setPlayScreen(PlayScreen ps) {
        playScreen = ps;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                switch (keycode) {
                    case Input.Keys.SPACE:
                    case Input.Keys.ENTER:
                        switchLine(curLineIndex + 1);
                        if (curLineIndex >= daishi.lines.size()) {
                            playScreen.invokePause();
                            throw new RuntimeException("not impld yet");
                        }
                        break;
                    case Input.Keys.ESCAPE:
                        playScreen.invokePause();
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();
        game.batch.draw(bg, 0, 0);
        if (exploring) {
            game.batch.draw(itemCity, 100, 390);
            game.batch.draw(itemPest, 150, 50);
            game.batch.draw(itemGas, 460, 50);
            boolean w = Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP);
            boolean a = Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT);
            boolean s = Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN);
            boolean d = Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);
            double angle = Math.sqrt(-1);
            boolean moved = true;
            if (w && a)
                angle = (Math.PI * 7 / 4);
            else if (a && s)
                angle = (Math.PI * 5 / 4);
            else if (s && d)
                angle = (Math.PI * 3 / 4);
            else if (d && w)
                angle = (Math.PI * 1 / 4);
            else if (w)
                angle = 0;
            else if (d)
                angle = (Math.PI / 2);
            else if (s)
                angle = (Math.PI);
            else if (a)
                angle = (Math.PI * 3 / 2);
            else
                moved = false;
            if (moved) {
                weightedAngle = weightedAngle * 0.7 + angle * 0.3;
                playerX += Math.sin(weightedAngle) * MOVEMENT_COEFF;
                playerY += Math.cos(weightedAngle) * MOVEMENT_COEFF;
                playerX = clamp(playerX, game.camera.viewportWidth - player.getWidth(), 0);
                playerY = clamp(playerY, game.camera.viewportHeight - player.getHeight(), 0);
            }
            game.batch.draw(player, playerX, playerY);
//            System.out.println(playerX + " " + playerY);
        } else {

        }


        game.batch.end();
    }

    private Line curLine() {
        return daishi.lines.get(curLineIndex);
    }

    private void switchLine(int newLineIndex) {
        curLineIndex = newLineIndex;
        Line cl = curLine();
        telop = new Telop(game);
        telop.setBodyText(cl.body);
        telop.setTenText(cl.ten);
        if (cl.action != null)
            switch (cl.action) {
                case "":

                    break;
                case "explore":
                    exploring = true;
                    break;
            }
        if (cl.chain) switchLine(curLineIndex + 1);
    }

    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    private void loadDaishi() throws IOException {
        ObjectMapper om = new ObjectMapper();
        daishi = om.readValue(Gdx.files.internal("daishi/reberu2.json").read(), Daishi.class);
        for (int i = 1; i < daishi.lines.size(); i++)
            daishi.lines.get(i).applyDefault(daishi.lines.get(i - 1));
    }

    private float clamp(float n, float upper, float lower) {
        if (n > upper) return upper;
        if (n < lower) return lower;
        return n;
    }
}
