package com.appuraja.bookboard;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

public abstract class RecyclingPagerAdapter extends PagerAdapter {
  public static final int IGNORE_ITEM_VIEW_TYPE = AdapterView.ITEM_VIEW_TYPE_IGNORE;

  private final RecycleBin recycleBin;

  public RecyclingPagerAdapter() {
    this(new RecycleBin());
  }

  public RecyclingPagerAdapter(@NonNull RecycleBin recycleBin) {
    this.recycleBin = recycleBin;
    this.recycleBin.setViewTypeCount(getViewTypeCount());
  }

  @Override
  public void notifyDataSetChanged() {
    recycleBin.scrapActiveViews();
    super.notifyDataSetChanged();
  }

  @NonNull
  @Override
  public final Object instantiateItem(@NonNull ViewGroup container, int position) {
    int viewType = getItemViewType(position);
    View view = null;
    if (viewType != IGNORE_ITEM_VIEW_TYPE) {
      view = recycleBin.getScrapView(position, viewType);
    }
    view = getView(position, view, container);
    container.addView(view);
    return view;
  }

  @Override
  public final void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
    View view = (View) object;
    container.removeView(view);
    int viewType = getItemViewType(position);
    if (viewType != IGNORE_ITEM_VIEW_TYPE) {
      recycleBin.addScrapView(view, position, viewType);
    }
  }

  @Override
  public final boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
    return view == object;
  }

  public int getViewTypeCount() {
    return 1;
  }

  @SuppressWarnings("UnusedParameters")
  public int getItemViewType(int position) {
    return 0;
  }

  @NonNull
  public abstract View getView(int position, @Nullable View convertView, @NonNull ViewGroup container);
}