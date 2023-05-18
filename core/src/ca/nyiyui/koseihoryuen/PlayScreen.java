package ca.nyiyui.koseihoryuen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FillViewport;

public class PlayScreen extends ScreenAdapter2 {
    private static final int GAME_STATE_RUNNING = 1;
    private static final int GAME_STATE_PAUSED = 2;
    private int gameState = GAME_STATE_RUNNING;
    private PauseScreen pauseScreen;
    private PlayableScreen gameScreen;

    public PlayScreen(Koseihoryuen game, PlayableScreen gameScreen) {
        super(game);
        pauseScreen = new PauseScreen(game);
        this.gameScreen = gameScreen;
        gameScreen.setPlayScreen(this);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        switch (gameState) {
            case GAME_STATE_RUNNING:
                gameScreen.render(delta);
                break;
            case GAME_STATE_PAUSED:
                pauseScreen.render(delta);
                break;
            default:
                throw new RuntimeException(String.format("invalid state %d", gameState));
        }
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
}