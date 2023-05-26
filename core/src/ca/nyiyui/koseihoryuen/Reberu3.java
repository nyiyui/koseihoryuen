package ca.nyiyui.koseihoryuen;

public class Reberu3 extends Reberu implements PlayableScreen {
    public Reberu3(Koseihoryuen game) {
        super(game);
    }

    @Override
    protected void handleLineSwitch() {
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void closingScreen(float delta) {
        throw new RuntimeException("not yet implemented");
    }
}
