package ca.nyiyui.koseihoryuen.data;

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
    public boolean chain;

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
        if (label == null) label= line.label;
        if (persona == null) persona = line.persona;
        if (headVariant == null) headVariant = line.headVariant;
        if (ten == null) ten = line.ten;
        if (body == null) body = line.body;
        if (sound == null) sound = line.sound;
        if (action==null)action=line.action;
    }
}
