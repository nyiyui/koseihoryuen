package ca.nyiyui.koseihoryuen;

import ca.nyiyui.koseihoryuen.data.Daishi;
import ca.nyiyui.koseihoryuen.data.Line;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;

public class Reberu1 extends ScreenAdapter2 implements PlayableScreen {
    private static final float NPC_INTERACTION_RADIUS = 70;
    private static final float MOVEMENT_COEFF = 10;
    private Box2DDebugRenderer debugRenderer;
    private Stage stage;
    private PlayScreen playScreen;
    private Daishi daishi;
    /**
     * Index of line to currently show.
     */
    private int curLineIndex;
    private Texture background;
    private Texture pathway;
    private Texture playerSpriteSmall;
    private Texture playerSpriteLarge;
    private Texture spriteBeeNPC;
    private Music music;
    private Telop telop;
    private final static int STATE_INST = 1;
    private final static int STATE_EXPLORE = 2;
    private final static int STATE_COMPLETE = 3;
    private int state = STATE_INST;
    private float playerX = 0;
    private float playerY = 0;
    private double weightedAngle = 0;
    private boolean playerSpriteIsLarge;
    private ArrayList<NPC> npcs;
    /**
     * Index of latest NPC interacted with.
     */
    private int latestNPC = -1;
    /**
     * Makes player have no interaction with NPCs until it is NPC_INTERACTION_RADIUS away.
     */
    private boolean playerNoInteraction;

    Reberu1(Koseihoryuen game) {
        super(game);
        stage = new Stage(new FillViewport(game.camera.viewportWidth, game.camera.viewportHeight, game.camera), game.batch);
        try {
            loadDaishi();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("loading daishi failed");
        }
        background = new Texture(Gdx.files.internal("images/stage1-bg.png"));
        pathway = new Texture(Gdx.files.internal("images/stage1-pathway.png"));
        playerSpriteSmall = new Texture(Gdx.files.internal("images/player-sprite-small.png"));
        playerSpriteLarge = new Texture(Gdx.files.internal("images/player-sprite-large.png"));
        spriteBeeNPC = new Texture(Gdx.files.internal("images/beeNPC.png"));
        npcs = new ArrayList<>();
        npcs.add(new NPC(380, 700, "idobee1"));
        npcs.add(new NPC(850, 550, "bee2"));
        npcs.add(new NPC(520, 260, "immabee3"));
        switchLine(0);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                switch (keycode) {
                    case Input.Keys.SPACE:
                    case Input.Keys.ENTER:
                        if (state != STATE_EXPLORE) {
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
                case "":
                    playerX = game.camera.viewportWidth * 3 / 4 - playerSpriteLarge.getWidth() / 2;
                    playerY = game.camera.viewportWidth / 2 - playerSpriteLarge.getHeight() / 2;
                    playerSpriteIsLarge = true;
                    state = STATE_INST;
                    break;
                case "explore":
                    playerNoInteraction = true;
                    playerSpriteIsLarge = false;
                    switch (latestNPC) {
                        case -1:
                            playerX = 50;
                            playerY = 50;
                            break;
                        default:
                            NPC npc = npcs.get(latestNPC);
                            playerX = npc.x;
                            playerY = npc.y;
                    }
                    state = STATE_EXPLORE;
                    break;
            }
        if (cl.chain) switchLine(curLineIndex + 1);
        if (cl.jump != null && cl.jump.length() != 0) {
            int i = DaishiUtils.findLabel(daishi, cl.jump);
            switchLine(i);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();
        if (state == STATE_EXPLORE) {
            game.batch.draw(pathway, 0, 0);
            boolean w = Gdx.input.isKeyPressed(Input.Keys.W);
            boolean a = Gdx.input.isKeyPressed(Input.Keys.A);
            boolean s = Gdx.input.isKeyPressed(Input.Keys.S);
            boolean d = Gdx.input.isKeyPressed(Input.Keys.D);
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
                playerX = clamp(playerX, game.camera.viewportWidth, 0);
                playerY = clamp(playerY, game.camera.viewportHeight, 0);
            }
        } else {
            game.batch.draw(background, 0, 0);
        }
        if (curLine().body != null && !curLine().body.equals(""))
            telop.draw(game.batch, 0, 0, game.camera.viewportWidth, 200);
        game.batch.draw(playerSpriteIsLarge ? playerSpriteLarge : playerSpriteSmall, playerX - playerSpriteSmall.getWidth() / 2, playerY - playerSpriteSmall.getHeight() / 2);
        renderText(game.debugFont, String.format("%f,%f", playerX, playerY), game.camera.viewportWidth / 2, 10);
        renderText(game.debugFont, curLine().toString(), game.camera.viewportWidth / 2, 20);
        for (int i = 0; i < npcs.size(); i++) {
            NPC npc = npcs.get(i);
            game.batch.draw(spriteBeeNPC, npc.x - spriteBeeNPC.getWidth() / 2, npc.y - spriteBeeNPC.getHeight() / 2);
        }
        if (state == STATE_EXPLORE)
            checkNPCInteraction();
        game.batch.end();
    }

    private void checkNPCInteraction() {
        boolean inRadius = false;
        for (int i = 0; i < npcs.size(); i++) {
            NPC npc = npcs.get(i);
            if (Math.sqrt(Math.pow(Math.abs(playerX - npc.x), 2) + Math.pow(Math.abs(playerY - npc.y), 2)) < NPC_INTERACTION_RADIUS) {
                inRadius = true;
                if (!playerNoInteraction) {
                    int j = DaishiUtils.findLabel(daishi, npc.label);
                    latestNPC = i;
                    switchLine(j);
                    return;
                }
            }
        }
        if (!inRadius && playerNoInteraction)
            playerNoInteraction = false;
    }

    private float clamp(float n, float upper, float lower) {
        if (n > upper) return upper;
        if (n < lower) return lower;
        return n;
    }

    public Daishi getDaishi() {
        return daishi;
    }

    public void setDaishi(Daishi daishi) {
        this.daishi = daishi;
    }

    @Override
    public void dispose() {
        debugRenderer.dispose();
    }
}

class NPC {
    public float x;
    public float y;
    public String label;

    public NPC(float x, float y, String label) {
        this.x = x;
        this.y = y;
        this.label = label;
    }
}