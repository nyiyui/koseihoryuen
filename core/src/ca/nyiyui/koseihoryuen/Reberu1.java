package ca.nyiyui.koseihoryuen;

import ca.nyiyui.koseihoryuen.data.Line;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.ArrayList;

public class Reberu1 extends Reberu {
    private static final float NPC_INTERACTION_RADIUS = 70;
    private final Texture background;
    private final Texture pathway;
    private final Texture spriteBeeNPC;
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
    /**
     * NPCs encountered with. Used to determine whether to allow the player to end the level now.
     */
    private boolean npcInteractions[];

    Reberu1(Koseihoryuen game) {
        super(game);
        DAISHI_PATH = "daishi/reberu1.json";
        try {
            loadDaishi();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("loading daishi failed");
        }
        background = new Texture(Gdx.files.internal("images/stage1-bg.png"));
        pathway = new Texture(Gdx.files.internal("images/stage1-pathway.png"));
        spriteBeeNPC = new Texture(Gdx.files.internal("images/beeNPC.png"));
        playerX = 0;
        playerY = 0;
        npcs = new ArrayList<>();
        npcs.add(new NPC(380, 700, "idobee1"));
        npcs.add(new NPC(850, 550, "bee2"));
        npcs.add(new NPC(520, 260, "immabee3"));
        npcs.add(new NPC(50, 240, "exit"));
        npcInteractions = new boolean[npcs.size()];
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
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();
        switch (state) {
            case INSTRUCTIONS:
                game.batch.draw(background, 0, 0);
                Sprite s = new Sprite(playerSpriteLarge);
                s.setX(game.camera.viewportWidth / 4 - playerSpriteLarge.getWidth() / 2);
                s.setY(game.camera.viewportWidth / 2 - playerSpriteLarge.getHeight() / 2);
                s.flip(true, false);
                s.setScale(0.8f);
                s.draw(game.batch);
                s.setTexture(spriteBeeNPC);
                s.setX(game.camera.viewportWidth * 3 / 4 - playerSpriteLarge.getWidth() / 2);
                s.setY(game.camera.viewportWidth / 2 - playerSpriteLarge.getHeight() / 2);
                s.flip(true, false);
                s.setScale(0.8f);
                s.draw(game.batch);
                break;
            case EXPLORE:
                game.batch.draw(pathway, 0, 0);
                for (int i = 0; i < npcs.size(); i++) {
                    NPC npc = npcs.get(i);
                    game.batch.draw(spriteBeeNPC, npc.x - spriteBeeNPC.getWidth() / 2, npc.y - spriteBeeNPC.getHeight() / 2);
                }
                handleMovement(delta);
                checkNPCInteraction();
                game.batch.draw(playerSpriteIsLarge ? playerSpriteLarge : playerSpriteSmall, playerX - playerSpriteSmall.getWidth() / 2, playerY - playerSpriteSmall.getHeight() / 2);
                break;
            case COMPLETE:
                game.batch.draw(background, 0, 0);
                closingScreen(delta);
                break;
        }
        if (curLine().body != null && !curLine().body.equals(""))
            telop.draw(game.batch, 0, 0, game.camera.viewportWidth, 200);
        Line cl = curLine();
        if (cl.question != null) renderQuestion();
        renderDebug();
        game.batch.end();
    }

    private void renderDebug() {
        renderText(game.debugFont, String.format("%f,%f", playerX, playerY), game.camera.viewportWidth / 2, 10);
        renderText(game.debugFont, curLine().toString(), game.camera.viewportWidth / 2, 20);
    }

    /**
     * Whether player completed level requirements.
     *
     * @return whether player completed level requirements
     */
    private boolean canExit() {
        // NOTE: ignore last NPC as it is the exit NPC.
        for (int i = 0; i < npcInteractions.length - 1; i++) {
            if (!npcInteractions[i]) return false;
        }
        return true;
    }

    private void checkNPCInteraction() {
        boolean inRadius = false;
        for (int i = 0; i < npcs.size(); i++) {
            NPC npc = npcs.get(i);
            if (Math.sqrt(Math.pow(Math.abs(playerX - npc.x), 2) + Math.pow(Math.abs(playerY - npc.y), 2)) < NPC_INTERACTION_RADIUS) {
                inRadius = true;
                if (!playerNoInteraction && Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
                    npcInteractions[i] = true;
                    int j = DaishiUtils.findLabel(daishi, npc.label);
                    latestNPC = i;
                    switchLine(j);
                    return;
                }
            }
        }
        if (!inRadius && playerNoInteraction) playerNoInteraction = false;
    }

    @Override
    protected void handleLineSwitch() {
        Line cl = curLine();
        if (cl.action != null) switch (cl.action) {
            case "":
                playerX = game.camera.viewportWidth * 3 / 4 - playerSpriteLarge.getWidth() / 2;
                playerY = game.camera.viewportWidth / 2 - playerSpriteLarge.getHeight() / 2;
                playerSpriteIsLarge = true;
                state = State.INSTRUCTIONS;
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
                state = State.EXPLORE;
                break;
            case "exit":
                if (canExit()) {
                    state = State.COMPLETE;
                } else {
                    int i = DaishiUtils.findLabel(daishi, "exit-nok");
                    switchLine(i);
                }
        }
    }

    @Override
    public void dispose() {
        background.dispose();
        pathway.dispose();
        playerSpriteSmall.dispose();
        playerSpriteLarge.dispose();
        spriteBeeNPC.dispose();
        titleFont.dispose();
        subtitleFont.dispose();
    }

    /**
     * Shows the congratulations screen.
     *
     * @param delta render delta time
     */
    @Override
    public void closingScreen(float delta) {
        elapsedToExit += delta;
        renderText(titleFont, "Congratulations!", game.camera.viewportWidth / 2, game.camera.viewportHeight / 2);
        renderText(subtitleFont, "You finished this level.", game.camera.viewportWidth / 2, game.camera.viewportHeight / 2 - 50);
        if (elapsedToExit > 4f)
            game.setScreen(new TitleScreen(game));
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
