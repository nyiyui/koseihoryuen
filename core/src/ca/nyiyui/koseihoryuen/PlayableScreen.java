package ca.nyiyui.koseihoryuen;

import com.badlogic.gdx.Screen;

/**
 * Names: Ivy & Ken
 * Teacher: Ms Krasteva
 * Date: June 9, 2023
 * Purpose: interface so we can use Screens that don't inherit from Reberu in PlayScreen
 * Contributions: Ken -> everything
 */

public interface PlayableScreen extends Screen {
    void setPlayScreen(PlayScreen ps);
}
