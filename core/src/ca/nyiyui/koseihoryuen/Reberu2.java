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
    /**
     * images for background, player, and interactable objects.
     */
    private Texture bg, itemCity, itemGas, itemPest;
    /**
     * x- and y- coordinates of the player.
     */
    private float playerX, playerY;
    private double weightedAngle = 0;
    private int state = STATE_INST;

    public Reberu2(Koseihoryuen game) {
        super(game);
        DAISHI_PATH = "daishi/reberu2.json";
        stage = new Stage(new FillViewport(game.camera.viewportWidth, game.camera.viewportHeight, game.camera), game.batch);
        try {
            loadDaishi();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("loading daishi failed");
        }
        bg = new Texture(Gdx.files.internal("images/stage2-bg.png"));
        itemCity = new Texture(Gdx.files.internal("images/stage2-city.png"));
        itemGas = new Texture(Gdx.files.internal("images/stage2-greenhouse-gas.png"));
        itemPest = new Texture(Gdx.files.internal("images/stage2-pesticide-sign.png"));
        playerX = game.camera.viewportWidth / 2;
        playerY = game.camera.viewportHeight / 2;
        switchLine(0);
        state = STATE_EXPLORE;
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
        switch (state) {
            case STATE_EXPLORE:
                game.batch.draw(itemCity, 100, 390);
                game.batch.draw(itemPest, 120, 50);
                game.batch.draw(itemGas, 620, 190);
                handleMovement(delta);
                game.batch.draw(playerSpriteSmall, playerX, playerY);

                if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
                    // check if player is on city
                    if (playerX >= 100 && playerX <= 100 + itemCity.getWidth() && playerY >= 390 && playerY <= 390 + itemCity.getHeight()) {
                        System.out.println("on city");
                        //TODO: add questions
                    }
                    // check if player is on pesticide warning sign
                    else if (playerX >= 120 && playerX <= 120 + itemPest.getWidth() && playerY >= 50 && playerY <= 50 + itemPest.getHeight()) {
                        System.out.println("on pest");
                    }
                    // check if player is on greenhouse gas emissions
                    else if (playerX >= 6 && playerX <= 620 + itemGas.getWidth() && playerY >= 190 && playerY <= 190 + itemGas.getHeight()) {
                        System.out.println("on greenhouse gas");
                    }
                }
                break;
            case STATE_INST:
                break;
            case STATE_COMPLETE:

                break;
        }
        game.batch.end();
    }

    @Override
    protected void handleLineSwitch() {
        Line cl = curLine();
        switch (cl.action) {
            case "":
                state = STATE_INST;
            case "explore":
                state = STATE_EXPLORE;
        }
    }

    @Override
    public void dispose() {
        bg.dispose();
        playerSpriteLarge.dispose();
        playerSpriteSmall.dispose();
        itemCity.dispose();
        itemGas.dispose();
        itemPest.dispose();
    }

    @Override
    public void closingScreen(float delta) {

    }

    private void handleMovement(float delta) {
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
            weightedAngle = weightedAngle * 0.7 + angle * 0.3;
            playerX += Math.sin(weightedAngle) * MOVEMENT_COEFF * delta;
            playerY += Math.cos(weightedAngle) * MOVEMENT_COEFF * delta;
            playerX = clamp(playerX, game.camera.viewportWidth - playerSpriteSmall.getWidth(), 0);
            playerY = clamp(playerY, game.camera.viewportHeight - playerSpriteSmall.getHeight(), 0);
        }
    }

    public void hide() {
        Gdx.input.setInputProcessor(null);
    }
}
