package ca.nyiyui.koseihoryuen;

import com.badlogic.gdx.Gdx;

/**
 * Utility class to make threshold-based cumulative delta times.
 */
public class CumulativeDelta {
    private float delta=0;
    private float threshold;

    CumulativeDelta(float threshold) {
        this.threshold=threshold;
    }

    /**
     * Update using Gdx.graphics.getDeltaTime().
     */
    public void update() {
        update(Gdx.graphics.getDeltaTime());
    }

    public void update(float delta) {
        this.delta+=delta;
    }

    /**
     * Whether cumulative delta time is ready to flush.
     * @return whether cumulative delta is over the threshold
     */
    public boolean ready() {
        return delta>threshold;
    }

    public float delta() {
        return delta;
    }

    /**
     * Resets the cumulative delta counter.
     */
    public void reset() {
        delta=0;
    }
}
