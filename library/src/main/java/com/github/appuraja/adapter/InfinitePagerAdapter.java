package com.github.appuraja.adapter;

import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.appuraja.bookboard.RecyclingPagerAdapter;

/**
 * Seamless loop adapter providing boundary wraps for infinite auto-scrolling.
 */
public abstract class InfinitePagerAdapter extends RecyclingPagerAdapter {

    private static final int INFINITE_COUNT = 400;

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup container) {
        return getItemView(getPosition(position), convertView, container);
    }

    @Override
    public int getCount() {
        int realCount = getItemCount();
        if (realCount <= 1) {
            return realCount;
        }
        return (INFINITE_COUNT - (INFINITE_COUNT % realCount)) + 2;
    }

    public abstract int getItemCount();

    public int getPosition(int position) {
        int realCount = getItemCount();
        if (realCount <= 1) {
            return 0;
        }
        if (position == 0) {
            return realCount - 1;
        } else if (position == (getCount() - 1)) {
            return 0;
        }
        return (position - 1) % realCount;
    }

    @NonNull
    public abstract View getItemView(int position, @Nullable View convertView, @NonNull ViewGroup container);
}