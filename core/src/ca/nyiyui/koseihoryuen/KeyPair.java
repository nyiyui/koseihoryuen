package ca.nyiyui.koseihoryuen;

public class KeyPair<T> {
    public int key;
    public T value;

    public KeyPair(int key, T value) {
        this.key = key;
        this.value = value;
    }
}
