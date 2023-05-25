package ca.nyiyui.koseihoryuen;

import ca.nyiyui.koseihoryuen.data.Daishi;
import ca.nyiyui.koseihoryuen.data.Line;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public abstract class Reberu extends ScreenAdapter2 implements PlayableScreen {
    protected Daishi daishi;
    protected Telop telop;
    protected PlayScreen playScreen;
    /**
     * Index of line to currently show.
     */
    protected int curLineIndex;

    public Reberu(Koseihoryuen game) {
        super(game);
    }

    protected void loadDaishi() throws IOException {
        ObjectMapper om = new ObjectMapper();
        daishi = om.readValue(Gdx.files.internal("daishi/reberu1.json").read(), Daishi.class);
        for (int i = 1; i < daishi.lines.size(); i++)
            daishi.lines.get(i).applyDefault(daishi.lines.get(i - 1));
    }

    @Override
    public void setPlayScreen(PlayScreen ps) {
        playScreen = ps;
    }

    protected Line curLine() {
        return daishi.lines.get(curLineIndex);
    }

    protected void switchLine(int newLineIndex) {
        curLineIndex = newLineIndex;
        Line cl = curLine();
        telop = new Telop(game);
        telop.setBodyText(cl.body);
        telop.setTenText(cl.ten);
        handleLineSwitch();
        if (cl.chain) switchLine(curLineIndex + 1);
        if (cl.jump != null && cl.jump.length() != 0) {
            int i = DaishiUtils.findLabel(daishi, cl.jump);
            switchLine(i);
        }
    }

    protected abstract void handleLineSwitch();

    public Daishi getDaishi() {
        return daishi;
    }

    public void setDaishi(Daishi daishi) {
        this.daishi = daishi;
    }

    @Override
    public abstract void dispose();
}
