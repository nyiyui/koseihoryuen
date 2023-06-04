package ca.nyiyui.koseihoryuen;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setForegroundFPS(60);
        config.setWindowedMode(960, 780);
        config.useVsync(true);
        config.setTitle("koseihoryuen");
        config.setForegroundFPS(60);
        config.setResizable(false);
        new Lwjgl3Application(new Koseihoryuen(), config);
    }
}
