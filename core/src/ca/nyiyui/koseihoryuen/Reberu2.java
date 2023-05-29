package ca.nyiyui.koseihoryuen;

import ca.nyiyui.koseihoryuen.data.Line;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
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
     * background image
     */
    private Texture bg;
    /**
     * item for the city.
     */
    private Item city;
    /**
     * item for greenhouse gas emissions
     */
    private Item gas;
    /**
     * image of pesticide warning sign.
     */
    private Item pest;

    /**
     * x- and y- coordinates of the player.
     */

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
        city = new Item(new Texture(Gdx.files.internal("images/stage2-city.png")), 100, 390);
        gas = new Item(new Texture(Gdx.files.internal("images/stage2-greenhouse-gas.png")), 120, 50);
        pest = new Item(new Texture(Gdx.files.internal("images/stage2-pesticide-sign.png")), 620, 190);
        playerX = game.camera.viewportWidth / 2;
        playerY = game.camera.viewportHeight / 2;
        switchLine(0);
        state = State.INSTRUCTIONS;
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
                        if (state != State.EXPLORE && questionDrawable.state != QuestionDrawable.State.ASKING) {
                            switchLine(curLineIndex + 1);
                            if (curLineIndex >= daishi.lines.size()) {
                                playScreen.invokePause();
                                throw new RuntimeException("not impld yet");
                            }
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
            case EXPLORE:
                game.batch.draw(city.image, city.x, city.y);
                game.batch.draw(pest.image, pest.x, pest.y);
                game.batch.draw(gas.image, gas.x, gas.y);
                handleMovement(delta);
                game.batch.draw(playerSpriteSmall, playerX, playerY);

                if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
                    checkItemInteraction();
                }
                break;
            case INSTRUCTIONS:
                Sprite s = new Sprite(playerSpriteLarge);
                s.setX(game.camera.viewportWidth / 4 - playerSpriteLarge.getWidth() / 2);
                s.setY(game.camera.viewportWidth / 2 - playerSpriteLarge.getHeight() / 2);
                s.flip(true, false);
                s.setScale(0.8f);
                s.draw(game.batch);
                break;
            case COMPLETE:
        }
        if (curLine().question != null) renderQuestion();
        game.batch.end();
    }

    private void checkItemInteraction() {
        // check if player is on city
        if (playerX >= city.x && playerX <= city.x + city.image.getWidth() && playerY >= city.y && playerY <= city.y + city.image.getHeight()) {
            System.out.println("on city");
            //TODO: add questions
            int j = DaishiUtils.findLabel(daishi, "city");
            switchLine(j);
        }
        // check if player is on pesticide warning sign
        else if (playerX >= pest.x && playerX <= pest.x + pest.image.getWidth() && playerY >= pest.y && playerY <= pest.y + pest.image.getHeight()) {
            System.out.println("on pest");
        }
        // check if player is on greenhouse gas emissions
        else if (playerX >= gas.x && playerX <= gas.x + gas.image.getWidth() && playerY >= gas.y && playerY <= gas.y + gas.image.getHeight()) {
            System.out.println("on greenhouse gas");
        }
    }

    @Override
    protected void handleLineSwitch() {
        Line cl = curLine();
        switch (cl.action) {
            case "":
                state = State.INSTRUCTIONS;
                break;
            case "explore":
                state = State.EXPLORE;
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        bg.dispose();
        gas.image.dispose();
        city.image.dispose();
        pest.image.dispose();
    }

    @Override
    public void closingScreen(float delta) {

    }


    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    class Item {
        Texture image;
        float x, y;
        boolean answeredCorrect;

        public Item(Texture image, float x, float y) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.answeredCorrect = false;
        }
    }
}
