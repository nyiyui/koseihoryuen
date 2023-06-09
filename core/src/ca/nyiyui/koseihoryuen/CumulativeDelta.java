package ca.nyiyui.koseihoryuen;

import com.badlogic.gdx.Gdx;

/**
 * Names: Ivy & Ken
 * Teacher: Ms Krasteva
 * Date: June 9, 2023
 * Purpose: helper class to accumulate delta time (between frames) and take action (e.g. run physics simulation step every 1/30th of a second)
 * Contributions: Ken --> everything
 */

/**
 * Utility class to make threshold-based cumulative delta times.
 */
public class CumulativeDelta {
    private float delta = 0;
    private float threshold;

    CumulativeDelta(float threshold) {
        this.threshold = threshold;
    }

    /**
     * Update using Gdx.graphics.getDeltaTime().
     */
    public void update() {
        update(Gdx.graphics.getDeltaTime());
    }

    public void update(float delta) {
        this.delta += delta;
    }

    /**
     * Resets and returns the ready value before reset.
     *
     * @return ready value before reset.
     */
    public boolean step() {
        boolean ready = ready();
        if (ready) reset();
        return ready;
    }

    /**
     * Whether cumulative delta time is ready to flush.
     *
     * @return whether cumulative delta is over the threshold
     */
    public boolean ready() {
        return delta > threshold;
    }

    public float delta() {
        return delta;
    }

    /**
     * Resets the cumulative delta counter.
     */
    public void reset() {
        delta = 0;
    }
}
