package ca.nyiyui.koseihoryuen;

import ca.nyiyui.koseihoryuen.data.Daishi;

import java.util.Objects;

/**
 * Names: Ivy & Ken
 * Teacher: Ms Krasteva
 * Date: May 23, 2023
 * Purpose: utilities for daishi (scene files)
 * Contributions: Ken did all of this class
 */

public class DaishiUtils {
    /**
     * Finds the Line with the given label.
     *
     * @param d     daishi
     * @param label target label to find
     * @return index of found line, -1 if not found/
     */
    public static int findLabel(Daishi d, String label) {
        for (int i = 0; i < d.lines.size(); i++) {
            if (Objects.equals(d.lines.get(i).label, label))
                return i;
        }
        return -1;
    }
}
