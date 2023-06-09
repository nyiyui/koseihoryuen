package ca.nyiyui.koseihoryuen;

/**
 * Names: Ivy & Ken
 * Teacher: Ms Krasteva
 * Date: June 9, 2023
 * Purpose: dataclass for a tuple with int and generic type
 * Contributions: Ken -> everything
 */

public class KeyPair<T> {
    public int key;
    public T value;

    public KeyPair(int key, T value) {
        this.key = key;
        this.value = value;
    }
}
