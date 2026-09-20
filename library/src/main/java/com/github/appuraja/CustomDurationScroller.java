package com.github.appuraja;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * Scroller with customizable transition duration for smooth banner sliding.
 */
public class CustomDurationScroller extends Scroller {

    private int customDuration = 800;

    public CustomDurationScroller(Context context) {
        super(context);
    }

    public CustomDurationScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        super.startScroll(startX, startY, dx, dy, customDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, customDuration);
    }

    public int getDurationValue() {
        return customDuration;
    }

    public void setCustomDuration(int duration) {
        this.customDuration = duration;
    }
}