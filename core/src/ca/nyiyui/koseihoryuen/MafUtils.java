package ca.nyiyui.koseihoryuen;

/**
 * Names: Ivy & Ken
 * Teacher: Ms Krasteva
 * Date: May 23, 2023
 * Purpose: utilities for math
 * Contributions: Ken did all of this class
 */

public class MafUtils {
    /**
     * Implementes the sigmoid function S(x). Formula is unspecified.
     * @param x x variable of S(x)
     * @return S(x)
     */
    static float sigmoid(float x) {
        return (float) (1/(1+Math.pow(Math.E,-x)));
    }
}
