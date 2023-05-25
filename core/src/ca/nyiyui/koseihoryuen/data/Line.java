package ca.nyiyui.koseihoryuen.data;

import java.util.Objects;

public class Line implements Cloneable {
    public String label;
    public String persona;
    public String headVariant;
    public String ten;
    public String body;
    public String sound;
    /**
     * Level-specfic action to perform for this line. Mutually exclusive with speech-related fields.
     */
    public String action;
    /**
     * Switches to the next line immediately after. Not inherited.
     */
    public boolean chain;
    /**
     * Disables default application to and from this line.
     */
    public boolean noDefault;
    /**
     * Disables default application for this line only.
     */
    public boolean noInherit;
    /**
     * Jumps to the line label. Not inherited. Chain takes precedence over this.
     */
    public String jump;
    /**
     * Question to ask, if not null. Not inherited.
     */
    public Question question;

    @Override
    public String toString() {
        return "Line@" + label +
                "{p='" + persona + '\'' +
                ", hV='" + headVariant + '\'' +
                ", t='" + ten + '\'' +
                ", b='" + body + '\'' +
                ", s='" + sound + '\'' +
                ", a='" + action + '\'' +
                ", c=" + chain +
                ", nD=" + noDefault +
                ", j=" + jump +
                ", q=" + Objects.toString(question) +
                '}';
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public void applyDefault(Line line) {
        if (noInherit) return;
        if (noDefault || line.noDefault) return;
        if (label == null) label = line.label;
        if (persona == null) persona = line.persona;
        if (headVariant == null) headVariant = line.headVariant;
        if (ten == null) ten = line.ten;
        if (body == null) body = line.body;
        if (sound == null) sound = line.sound;
        if (action == null) action = line.action;
    }
}
