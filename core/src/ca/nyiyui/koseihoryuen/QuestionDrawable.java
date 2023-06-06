package ca.nyiyui.koseihoryuen;

import ca.nyiyui.koseihoryuen.data.Question;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;
import java.util.List;

/**
 * Names: Ivy & Ken
 * Teacher: Ms. Krasteva
 * Date: Jun 9th, 2023
 * Purpose: A fullscreen question using Telop.
 * Contribution: Ivy --> visuals in draw methods and comments, Ken --> everything else
 */
public class QuestionDrawable extends BaseDrawable implements Disposable {
    /**
     * Telop where question and options are drawn
     */
    private final Telop optsT;
    /**
     *
     */
    private String descText;
    private Label descLabel;
    private BitmapFont descFont;
    private List<KeyPair<String>> keyPairs;
    private Question question;
    State state;
    /**
     * image shown when user gets question right or wrong.
     */
    private Texture beeWrong, beeCorrect;


    enum State {
        DISABLED,
        ASKING,
        CORRECT,
        WRONG
    }

    QuestionDrawable(Koseihoryuen game, Telop optsT) {
        super();
        state = State.DISABLED;
        this.optsT = optsT;
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.color = new Color(0xffffffff);
        param.size = 25;
        param.borderColor = Color.BLACK;
        param.borderWidth = 3;
        descFont = game.font.generateFont(param);
        param.size = 18;
        param.borderWidth = 0;
        optsT.setBodyFont(game.font.generateFont(param));
        Label.LabelStyle ls = new Label.LabelStyle();
        ls.font = descFont;
        descLabel = new Label(descText, ls);
        descLabel.setWrap(true);
        beeCorrect = new Texture(Gdx.files.internal("images/beeExit.png"));
        beeWrong = new Texture(Gdx.files.internal("images/beeWrong.png"));
    }

    public void setDescText(String descText) {
        this.descText = descText;
    }

    public String getDescText() {
        return descText;
    }

    public void setDescFont(BitmapFont descFont) {
        this.descFont = descFont;
    }

    public BitmapFont getDescFont() {
        return descFont;
    }

    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {
        switch (state) {
            case ASKING:
                descLabel.setText(descText);
                descLabel.setAlignment(Align.left);
                break;
            case CORRECT:
                batch.draw(beeCorrect, (width - beeCorrect.getWidth() / 2) / 2, (height) / 2 - beeCorrect.getHeight() / 4);
                descLabel.setText("Correct!");
                descLabel.setAlignment(Align.center);
                break;
            case WRONG:
                batch.draw(beeWrong, (width - beeWrong.getWidth() / 2) / 2, (height) / 2 - beeWrong.getHeight() / 4);
                descLabel.setText("Wrong! L BOZO!");
                descLabel.setAlignment(Align.center);
        }

        final float padding = 20;
        optsT.draw(batch, x, y, width, 200);
        descLabel.setWidth(width / 2 - 2 * padding);
        descLabel.setHeight(height - 2 * padding);
        descLabel.setX(x + 2 * padding);
        descLabel.setY(y + 3 * padding);
        descLabel.draw(batch, 1);
    }

    public void handleInput() {
        if (state != State.ASKING) return;
        for (KeyPair<String> p : keyPairs) {
            if (Gdx.input.isKeyPressed(p.key)) {
                handleChoice(p.value);
            }
        }
    }

    public void handleChoice(String key) {
        if (question.answer == null) {
            throw new RuntimeException("no answer specified in daishi");
        }
        state = key.equals(question.answer) ? State.CORRECT : State.WRONG;
    }

    public void loadQuestion(Question q) {
        state = State.ASKING;
        question = q;
        optsT.setTenText("Question!");
        StringBuilder opts = new StringBuilder();
        keyPairs = new ArrayList<>(q.options.size());
        for (String key : q.options.keySet()) {
            opts.append(key);
            opts.append(" - ");
            opts.append(q.options.get(key));
            opts.append(("\n"));
            int keycode;
            switch (key) {
                case "A":
                    keycode = Input.Keys.A;
                    break;
                case "B":
                    keycode = Input.Keys.B;
                    break;
                case "C":
                    keycode = Input.Keys.C;
                    break;
                case "D":
                    keycode = Input.Keys.D;
                    break;
                default:
                    keycode = Input.Keys.SPACE;
                    System.out.printf("unknown keycode for key %s; using SPACE for now", key);
            }
            keyPairs.add(new KeyPair<>(keycode, key));
        }
        optsT.setBodyText(opts.toString());
        setDescText(q.question);
    }

    /**
     * Releases all resources of this object.
     */
    @Override
    public void dispose() {
        beeWrong.dispose();
        beeCorrect.dispose();
        descFont.dispose();
    }
}
