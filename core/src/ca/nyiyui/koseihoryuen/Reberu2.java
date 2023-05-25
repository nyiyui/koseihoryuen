package ca.nyiyui.koseihoryuen;

import ca.nyiyui.koseihoryuen.data.Line;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FillViewport;

/**
 * Names: Ivy & Ken
 * Teacher: Ms Krasteva
 * Date: May 23, 2023
 * Purpose: runs when user plays the Testing stage.
 * Contributions: Ivy
 */


public class Reberu2 extends Reberu implements PlayableScreen {

    private Stage stage;
    private static final float MOVEMENT_COEFF = 10;
    /**
     * images for background, player, and interactable objects.
     */
    private Texture bg, player, playerL, itemCity, itemGas, itemPest;
    /**
     * x- and y- coordinates of the player.
     */
    private float playerX, playerY;
    private double weightedAngle = 0;
    private State state = State.INSTRUCTIONS;

    private enum State {
        INSTRUCTIONS, EXPLORING
    }

    public Reberu2(Koseihoryuen game) {
        super(game);
        DAISHI_PATH = "daishi/reberu1.json";
        stage = new Stage(new FillViewport(game.camera.viewportWidth, game.camera.viewportHeight, game.camera), game.batch);
        try {
            loadDaishi();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("loading daishi failed");
        }
        bg = new Texture(Gdx.files.internal("images/stage2-bg.png"));
        player = new Texture(Gdx.files.internal("images/player-sprite-small.png"));
        playerL = new Texture(Gdx.files.internal("images/player-sprite-large.png"));
        itemCity = new Texture(Gdx.files.internal("images/stage2-city.png"));
        itemGas = new Texture(Gdx.files.internal("images/stage2-greenhouse-gas.png"));
        itemPest = new Texture(Gdx.files.internal("images/stage2-pesticide-sign.png"));
        switchLine(0);
        state = State.EXPLORING;
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
        if (state == State.EXPLORING) {
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

            if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
                // check if player is on city
                if (playerX >= 100 && playerX <= 100 + itemCity.getWidth() && playerY >= 390 && playerY <= 390 + itemCity.getHeight()) {
                    //TODO: question stuff
                }
            }
//            System.out.println(playerX + " " + playerY);
        }


        game.batch.end();
    }

    @Override
    protected void handleLineSwitch() {
        Line cl = curLine();
        switch (cl.action) {
            case "":
                state = State.INSTRUCTIONS;
            case "explore":
                state = State.EXPLORING;
        }
    }

    @Override
    public void dispose() {
        bg.dispose();
        player.dispose();
        playerL.dispose();
        itemCity.dispose();
        itemGas.dispose();
        itemPest.dispose();
    }


    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    private float clamp(float n, float upper, float lower) {
        if (n > upper) return upper;
        if (n < lower) return lower;
        return n;
    }
}
