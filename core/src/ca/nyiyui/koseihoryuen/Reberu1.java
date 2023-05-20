package ca.nyiyui.koseihoryuen;

import ca.nyiyui.koseihoryuen.data.Daishi;
import ca.nyiyui.koseihoryuen.data.Line;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class Reberu1 extends ScreenAdapter2 implements PlayableScreen {
    private Stage stage;
    private PlayScreen playScreen;
    private Daishi daishi;
    /**
     * Index of line to currently show.
     */
    private int curLineIndex;
    private Texture background;
    private Texture pathway;
    private Music music;
    private Telop telop;
    private boolean exploring = false;

    Reberu1(Koseihoryuen game) {
        super(game);
        stage = new Stage(new FillViewport(game.camera.viewportWidth, game.camera.viewportHeight, game.camera), game.batch);
        try {
            loadDaishi();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("loading daishi failed");
        }
        switchLine(0);
        background = new Texture(Gdx.files.internal("images/stage1-bg.png"));
        pathway = new Texture(Gdx.files.internal("images/stage1-pathway.png"));
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
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    private void loadDaishi() throws IOException {
        ObjectMapper om = new ObjectMapper();
        daishi = om.readValue(Gdx.files.internal("daishi/reberu1.json").read(), Daishi.class);
        for (int i = 1; i < daishi.lines.size(); i++)
            daishi.lines.get(i).applyDefault(daishi.lines.get(i - 1));
    }

    @Override
    public void setPlayScreen(PlayScreen ps) {
        playScreen = ps;
    }

    private Line curLine() {
        return daishi.lines.get(curLineIndex);
    }

    private void switchLine(int newLineIndex) {
        curLineIndex = newLineIndex;
        Line cl = curLine();
        if (music != null) music.dispose();
        if (cl.sound != null && !cl.sound.equals("")) {
            music = Gdx.audio.newMusic(Gdx.files.internal(cl.sound));
            music.setLooping(true);
            music.play();
            // TODO: sound doesn't play
        }
        telop = new Telop(game);
        telop.setBodyText(cl.body);
        telop.setTenText(cl.ten);
        if (cl.action != null)
            switch (cl.action) {
                case "explore":
                    exploring = true;
                    break;
            }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();
        game.batch.draw(background, 0, 0);
        if (exploring)
            game.batch.draw(pathway, 0, 0);
        if (curLine().body != null && !curLine().body.equals(""))
            telop.draw(game.batch, 0, 0, game.camera.viewportWidth, 200);
        game.batch.end();
    }

    public Daishi getDaishi() {
        return daishi;
    }

    public void setDaishi(Daishi daishi) {
        this.daishi = daishi;
    }
}
