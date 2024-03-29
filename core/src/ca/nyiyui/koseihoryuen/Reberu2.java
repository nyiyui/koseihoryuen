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
 * Date: June 9, 2023
 * Purpose: runs when user plays the Testing stage.
 * Contributions: Ivy --> everything
 */


public class Reberu2 extends Reberu implements PlayableScreen {

    private Stage stage;
    /**
     * background image
     */
    private Texture bg;
    /**
     * on screen instructions for how to move interact with items.
     */
    private Texture inst;
    /**
     * item for the city.
     */
    private Reberu2Item city;
    /**
     * item for greenhouse gas emissions
     */
    private Reberu2Item gas;
    /**
     * image of pesticide warning sign.
     */
    private Reberu2Item pest;
    /**
     * keeps track of which question the player is currently answering
     */
    private String curQuestion;
    /**
     * image for progress bar
     */
    private Texture progressBar, correctButton, wrongButton;

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
        progressBar = game.assetManager.get("images/stage2-rectangle.png");
        correctButton = game.assetManager.get("images/stage2-correct-button.png");
        wrongButton = game.assetManager.get("images/stage2-wrong-button.png");
        inst = game.assetManager.get("images/instruction1.png");
        bg = game.assetManager.get("images/stage2-bg.png");
        city = new Reberu2Item(game.assetManager.<Texture>get("images/stage2-city.png"), 100, 450);
        gas = new Reberu2Item(game.assetManager.<Texture>get("images/stage2-greenhouse-gas.png"), 160, 30);
        pest = new Reberu2Item(game.assetManager.<Texture>get("images/stage2-pesticide-sign.png"), 570, 190);
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
        if (state == State.COMPLETE || state == State.CUSTOM)
            return;
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                switch (keycode) {
                    case Input.Keys.SPACE:
                    case Input.Keys.ENTER:
                        if (state == State.INSTRUCTIONS && questionDrawable.state != QuestionDrawable.State.ASKING) {
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
        switch (state) {
            case EXPLORE:
                game.batch.draw(bg, 0, 0);
                game.batch.draw(inst, game.camera.viewportWidth - inst.getWidth() - 10, game.camera.viewportHeight - inst.getHeight() - 10);
                game.batch.draw(city.image, city.getX(), city.getY());
                game.batch.draw(pest.image, pest.getX(), pest.getY());
                game.batch.draw(gas.image, gas.getX(), gas.getY());
                handleMovement(delta);
                game.batch.draw(playerSpriteSmall, playerX, playerY);
                renderProgressBar();
                if (Gdx.input.isKeyPressed(Input.Keys.ENTER) || Gdx.input.isKeyPressed(Input.Keys.SPACE))
                    checkItemInteraction();
                break;
            case INSTRUCTIONS:
                game.batch.draw(bg, 0, 0);
                Sprite s = new Sprite(playerSpriteLarge);
                s.setX(game.camera.viewportWidth / 4 - playerSpriteLarge.getWidth() / 2);
                s.setY(game.camera.viewportWidth / 2 - playerSpriteLarge.getHeight() / 2);
                s.flip(true, false);
                s.setScale(0.8f);
                s.draw(game.batch);
                if (curLine().body != null && !curLine().body.equals(""))
                    telop.draw(game.batch, 0, 0, game.camera.viewportWidth, 200);
                break;
            case CUSTOM:
                game.batch.draw(bg, 0, 0);
                Line cl = curLine();
                if (cl.question != null) {
                    renderQuestion();
                }
                // check if question if correct
                if (questionDrawable.state == QuestionDrawable.State.CORRECT) {
                    if (curQuestion.equals("city")) {
                        city.answeredCorrect = true;
                    } else if (curQuestion.equals("pest")) {
                        pest.answeredCorrect = true;
                    } else if (curQuestion.equals("gas")) {
                        gas.answeredCorrect = true;
                    }
                    goBackToExplore(delta);

                } else if (questionDrawable.state == QuestionDrawable.State.WRONG) {
                    goBackToExplore(delta);
                }
                break;
            case COMPLETE:
                game.batch.draw(bg, 0, 0);
                closingScreen(delta);
                break;
        }
        checkAllAnswers();
        game.batch.end();
    }

    /**
     * checks if player coordinates are on an item/object. If they are, initiates question dialogue.
     */
    private void checkItemInteraction() {
        if (state == State.COMPLETE)
            return;
        // check if player is on city
        if (playerX >= city.getX() && playerX <= city.getX() + city.image.getWidth()
                && playerY >= city.getY() && playerY <= city.getY() + city.image.getHeight()) {
            if (city.answeredCorrect) {
                return;
            }
            int j = DaishiUtils.findLabel(daishi, "city");
            switchLine(j);
            state = State.CUSTOM;
            curQuestion = "city";
        }
        // check if player is on pesticide warning sign
        else if (playerX >= pest.getX() && playerX <= pest.getX() + pest.image.getWidth()
                && playerY >= pest.getY() && playerY <= pest.getY() + pest.image.getHeight()) {
            if (pest.answeredCorrect) {
                return;
            }
            int j = DaishiUtils.findLabel(daishi, "pesticide");
            switchLine(j);
            state = State.CUSTOM;
            curQuestion = "pest";
        }
        // check if player is on greenhouse gas emissions
        else if (playerX >= gas.getX() && playerX <= gas.getX() + gas.image.getWidth()
                && playerY >= gas.getY() && playerY <= gas.getY() + gas.image.getHeight()) {
            if (gas.answeredCorrect) {
                return;
            }
            int j = DaishiUtils.findLabel(daishi, "badgas");
            switchLine(j);
            state = State.CUSTOM;
            curQuestion = "gas";
        }
    }

    /**
     * checks if all questions have been answered correctly.
     * If all questions were answered correctly, sets the stage state to COMPLETE.
     */
    private void checkAllAnswers() {
        if (city.answeredCorrect && pest.answeredCorrect && gas.answeredCorrect) {
            state = State.COMPLETE;
        }
    }

    /**
     * Waits for 4 seconds before going back to exploration mode
     * Used after user answers a question.
     */
    private void goBackToExplore(float delta) {
        elapsedToExit += delta;
        if (elapsedToExit >= 2.5f) {
            elapsedToExit = 0;
            state = State.EXPLORE;
        }
    }

    /**
     * renders the rectangle in the bottom-right corner which contains which questions the user has answered correctly.
     */
    private void renderProgressBar() {
        float x = game.camera.viewportWidth - progressBar.getWidth() - 20, y = 20;
        game.batch.draw(progressBar, x, y);
        if (city.answeredCorrect)
            game.batch.draw(correctButton, x + progressBar.getWidth() / 4 - progressBar.getWidth() / 8, y + (progressBar.getHeight() - 55) / 2);
        else
            game.batch.draw(wrongButton, x + progressBar.getWidth() / 4 - progressBar.getWidth() / 8, y + (progressBar.getHeight() - 55) / 2);
        if (gas.answeredCorrect)
            game.batch.draw(correctButton, x + progressBar.getWidth() / 4 * 2 - progressBar.getWidth() / 8, y + (progressBar.getHeight() - 55) / 2);
        else
            game.batch.draw(wrongButton, x + progressBar.getWidth() / 4 * 2 - progressBar.getWidth() / 8, y + (progressBar.getHeight() - 55) / 2);
        if (pest.answeredCorrect)
            game.batch.draw(correctButton, x + progressBar.getWidth() / 4 * 3 - progressBar.getWidth() / 8, y + (progressBar.getHeight() - 55) / 2);
        else
            game.batch.draw(wrongButton, x + progressBar.getWidth() / 4 * 3 - progressBar.getWidth() / 8, y + (progressBar.getHeight() - 55) / 2);
    }

    @Override
    protected void handleLineSwitch() {
        Line cl = curLine();
        if (cl.action != null) switch (cl.action) {
            case "":
                state = State.INSTRUCTIONS;
                break;
            case "explore":
                state = State.EXPLORE;
                break;
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
        elapsedToExit += delta;
        game.batch.draw(bg, 0, 0);
        renderText(titleFont, "Congratulations!", game.camera.viewportWidth / 2, game.camera.viewportHeight / 2 + 25);
        renderText(subtitleFont, "You're a BEE-nius", game.camera.viewportWidth / 2, game.camera.viewportHeight / 2 - 25);
        if (elapsedToExit >= 4f)
            game.setScreen(new TitleScreen(game));
    }


    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

}

class Reberu2Item {
    Texture image;
    boolean answeredCorrect;
    private float x, y;

    public Reberu2Item(Texture image, float x, float y) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.answeredCorrect = false;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
