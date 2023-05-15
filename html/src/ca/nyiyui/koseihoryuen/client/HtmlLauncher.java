package ca.nyiyui.koseihoryuen.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import ca.nyiyui.koseihoryuen.Koseihoryuen;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                // Resizable application, uses available space in browser
//                return new GwtApplicationConfiguration(true);
                // Fixed size application:
                return new GwtApplicationConfiguration(960,780);
        }

        @Override
        public ApplicationListener createApplicationListener () {
                return new Koseihoryuen();
        }
}