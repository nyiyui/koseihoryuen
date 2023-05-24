package ca.nyiyui.koseihoryuen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

public class PlayScreen extends ScreenAdapter2 {
    private static final int GAME_STATE_RUNNING = 1;
    private static final int GAME_STATE_PAUSED = 2;
    private int gameState = GAME_STATE_RUNNING;
    private PauseScreen pauseScreen;
    private PlayableScreen gameScreen;

    public PlayScreen(Koseihoryuen game, PlayableScreen gameScreen) {
        super(game);
        pauseScreen = new PauseScreen(game);
        pauseScreen.setPlayScreen(this);
        this.gameScreen = gameScreen;
        gameScreen.setPlayScreen(this);
    }

    void invokePause() {
        gameState = GAME_STATE_PAUSED;
        game.setScreen(pauseScreen);
    }

    void invokePlay() {
        gameState = GAME_STATE_RUNNING;
        game.setScreen(gameScreen);
    }

    @Override
    public void render(float delta) {
        game.setScreen(gameScreen);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                switch (keycode) {
                    case Input.Keys.ESCAPE:
                        gameState = gameState == GAME_STATE_PAUSED ? GAME_STATE_RUNNING : GAME_STATE_PAUSED;
                }
                return true;
            }
        });
    }

    @Override
    public void dispose() {
        pauseScreen.dispose();
        gameScreen.dispose();
    }
}