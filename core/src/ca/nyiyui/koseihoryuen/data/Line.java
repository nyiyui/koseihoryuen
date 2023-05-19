package ca.nyiyui.koseihoryuen.data;

public class Line implements Cloneable {
    public String persona;
    public String headVariant;
    public String ten;
    public String body;
    public String sound;

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
        if (persona == null) persona = line.persona;
        if (headVariant == null) headVariant = line.headVariant;
        if (ten == null) ten = line.ten;
        if (body == null) body = line.body;
        if (sound == null) sound = line.sound;
    }
}
