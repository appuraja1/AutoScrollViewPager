package com.github.appuraja;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Interpolator;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.github.appuraja.adapter.InfinitePagerAdapter;
import com.github.appuraja.library.R;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

/**
 * Modern, smooth, leak-free auto-scrolling ViewPager for banners and galleries.
 */
public class AutoScrollViewPager extends ViewPager {

    public static final int DIRECTION_RIGHT = 1;
    public static final int DIRECTION_LEFT = 0;

    private static final int DEFAULT_SLIDE_INTERVAL = 5000;
    private static final int DEFAULT_SLIDE_DURATION = 800;
    private static final int MSG_SCROLL = 1001;

    private int slideInterval = DEFAULT_SLIDE_INTERVAL;
    private int slideDuration = DEFAULT_SLIDE_DURATION;
    private int direction = DIRECTION_RIGHT;

    private boolean stopWhenTouch = true;
    private boolean cycle = true;
    private boolean isAutoScroll = false;
    private boolean isStoppedWhenTouch = false;

    private AutoScrollHandler mHandler;
    private CustomDurationScroller mScroller;

    public AutoScrollViewPager(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public AutoScrollViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mHandler = new AutoScrollHandler(this);
        applyCustomScroller();

        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.AutoScrollViewPager, 0, 0);
            try {
                slideInterval = array.getInt(R.styleable.AutoScrollViewPager_slideInterval, DEFAULT_SLIDE_INTERVAL);
                slideDuration = array.getInt(R.styleable.AutoScrollViewPager_slideDuration, DEFAULT_SLIDE_DURATION);
                direction = array.getInt(R.styleable.AutoScrollViewPager_slideDirection, DIRECTION_RIGHT);
                stopWhenTouch = array.getBoolean(R.styleable.AutoScrollViewPager_stopWhenTouch, true);
                cycle = array.getBoolean(R.styleable.AutoScrollViewPager_cycle, true);
            } finally {
                array.recycle();
            }
        }
        if (mScroller != null) {
            mScroller.setCustomDuration(slideDuration);
        }
    }

    @Override
    public void setAdapter(@Nullable PagerAdapter adapter) {
        super.setAdapter(adapter);
        if (adapter instanceof InfinitePagerAdapter && adapter.getCount() > 1) {
            InfinitePagerAdapter infiniteAdapter = (InfinitePagerAdapter) adapter;
            int total = infiniteAdapter.getCount();
            int realCount = infiniteAdapter.getItemCount();
            if (realCount > 0) {
                int midPos = ((total - 2) / 2) - (((total - 2) / 2) % realCount) + 1;
                setCurrentItem(midPos, false);
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        addOnPageChangeListener(mOnPageChangeListener);
        if (isAutoScroll) {
            sendScrollMessage(slideInterval);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeOnPageChangeListener(mOnPageChangeListener);
        stopAutoScrollInternal();
    }

    private void scroll() {
        PagerAdapter adapter = getAdapter();
        if (adapter == null || adapter.getCount() <= 1) {
            return;
        }

        int currentItem = getCurrentItem();
        int totalCount = adapter.getCount();
        int nextItem = (direction == DIRECTION_RIGHT) ? currentItem + 1 : currentItem - 1;

        if (!(adapter instanceof InfinitePagerAdapter)) {
            if (cycle) {
                if (nextItem < 0) {
                    setCurrentItem(totalCount - 1, true);
                } else if (nextItem >= totalCount) {
                    setCurrentItem(0, true);
                } else {
                    setCurrentItem(nextItem, true);
                }
            } else if (nextItem >= 0 && nextItem < totalCount) {
                setCurrentItem(nextItem, true);
            }
        } else {
            setCurrentItem(nextItem, true);
        }
    }

    private void applyCustomScroller() {
        try {
            Field scrollerField = ViewPager.class.getDeclaredField("mScroller");
            scrollerField.setAccessible(true);
            Field interpolatorField = ViewPager.class.getDeclaredField("sInterpolator");
            interpolatorField.setAccessible(true);

            mScroller = new CustomDurationScroller(getContext(), (Interpolator) interpolatorField.get(null));
            mScroller.setCustomDuration(slideDuration);
            scrollerField.set(this, mScroller);
        } catch (Exception ignored) {
            // Graceful fallback: Default ViewPager scroller remains active
        }
    }

    public void startAutoScroll() {
        isAutoScroll = true;
        sendScrollMessage(slideInterval);
    }

    public void startAutoScroll(int delayTime) {
        this.slideInterval = delayTime;
        isAutoScroll = true;
        sendScrollMessage(delayTime);
    }

    public void stopAutoScroll() {
        isAutoScroll = false;
        stopAutoScrollInternal();
    }

    private void stopAutoScrollInternal() {
        if (mHandler != null) {
            mHandler.removeMessages(MSG_SCROLL);
        }
    }

    private void sendScrollMessage(long delayTime) {
        if (mHandler != null) {
            mHandler.removeMessages(MSG_SCROLL);
            mHandler.sendEmptyMessageDelayed(MSG_SCROLL, delayTime);
        }
    }

    private final SimpleOnPageChangeListener mOnPageChangeListener = new SimpleOnPageChangeListener() {
        @Override
        public void onPageScrollStateChanged(int state) {
            super.onPageScrollStateChanged(state);
            if (state == SCROLL_STATE_IDLE) {
                PagerAdapter adapter = getAdapter();
                if (adapter instanceof InfinitePagerAdapter) {
                    int cur = getCurrentItem();
                    int lastReal = adapter.getCount() - 2;
                    if (cur == 0) {
                        setCurrentItem(lastReal, false);
                    } else if (cur > lastReal) {
                        setCurrentItem(1, false);
                    }
                }
            }
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (stopWhenTouch) {
            int action = ev.getActionMasked();
            if (action == MotionEvent.ACTION_DOWN) {
                if (isAutoScroll) {
                    isStoppedWhenTouch = true;
                    stopAutoScrollInternal();
                }
            } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                if (isStoppedWhenTouch) {
                    isStoppedWhenTouch = false;
                    if (isAutoScroll) {
                        sendScrollMessage(slideInterval);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private static class AutoScrollHandler extends Handler {
        private final WeakReference<AutoScrollViewPager> hostRef;

        public AutoScrollHandler(AutoScrollViewPager host) {
            super(Looper.getMainLooper());
            this.hostRef = new WeakReference<>(host);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            AutoScrollViewPager host = hostRef.get();
            if (host != null && host.isAutoScroll) {
                host.scroll();
                host.sendScrollMessage(host.slideInterval);
            }
        }
    }

    // --- Getters & Setters ---

    public int getSlideInterval() {
        return slideInterval;
    }

    public void setSlideInterval(int slideInterval) {
        this.slideInterval = slideInterval;
    }

    public int getSlideDuration() {
        return slideDuration;
    }

    public void setSlideDuration(int slideDuration) {
        this.slideDuration = slideDuration;
        if (mScroller != null) {
            mScroller.setCustomDuration(slideDuration);
        }
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public boolean isStopWhenTouch() {
        return stopWhenTouch;
    }

    public void setStopWhenTouch(boolean stopWhenTouch) {
        this.stopWhenTouch = stopWhenTouch;
    }

    public boolean isCycle() {
        return cycle;
    }

    public void setCycle(boolean cycle) {
        this.cycle = cycle;
    }
}