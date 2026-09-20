package com.appuraja.bookboard;

import android.view.View;
import androidx.collection.SparseArrayCompat;

/**
 * High-performance scrap and active view recycling cache for ViewPager.
 */
public class RecycleBin {
  private View[] activeViews = new View[0];
  private int[] activeViewTypes = new int[0];

  @SuppressWarnings("unchecked")
  private SparseArrayCompat<View>[] scrapViews = new SparseArrayCompat[0];
  private int viewTypeCount;
  private SparseArrayCompat<View> currentScrapViews;

  public void setViewTypeCount(int viewTypeCount) {
    if (viewTypeCount < 1) {
      throw new IllegalArgumentException("Can't have a viewTypeCount < 1");
    }
    @SuppressWarnings("unchecked")
    SparseArrayCompat<View>[] scrap = new SparseArrayCompat[viewTypeCount];
    for (int i = 0; i < viewTypeCount; i++) {
      scrap[i] = new SparseArrayCompat<>();
    }
    this.viewTypeCount = viewTypeCount;
    this.currentScrapViews = scrap[0];
    this.scrapViews = scrap;
  }

  protected boolean shouldRecycleViewType(int viewType) {
    return viewType >= 0;
  }

  public View getScrapView(int position, int viewType) {
    if (viewTypeCount == 1) {
      return retrieveFromScrap(currentScrapViews, position);
    } else if (viewType >= 0 && viewType < scrapViews.length) {
      return retrieveFromScrap(scrapViews[viewType], position);
    }
    return null;
  }

  public void addScrapView(View scrap, int position, int viewType) {
    if (scrap == null) return;

    if (viewTypeCount == 1) {
      currentScrapViews.put(position, scrap);
    } else if (viewType >= 0 && viewType < scrapViews.length) {
      scrapViews[viewType].put(position, scrap);
    }

    scrap.setAccessibilityDelegate(null);
  }

  public void scrapActiveViews() {
    final View[] active = this.activeViews;
    final int[] types = this.activeViewTypes;
    final boolean multipleScraps = viewTypeCount > 1;

    SparseArrayCompat<View> scrap = currentScrapViews;
    final int count = active.length;

    for (int i = count - 1; i >= 0; i--) {
      final View victim = active[i];
      if (victim != null) {
        int whichScrap = types[i];

        active[i] = null;
        types[i] = -1;

        if (!shouldRecycleViewType(whichScrap)) {
          continue;
        }

        if (multipleScraps && whichScrap >= 0 && whichScrap < scrapViews.length) {
          scrap = this.scrapViews[whichScrap];
        }

        if (scrap != null) {
          scrap.put(i, victim);
        }
        victim.setAccessibilityDelegate(null);
      }
    }
    pruneScrapViews();
  }

  private void pruneScrapViews() {
    final int maxViews = activeViews.length;
    final int typeCount = this.viewTypeCount;
    for (int i = 0; i < typeCount; ++i) {
      final SparseArrayCompat<View> scrapPile = scrapViews[i];
      int size = scrapPile.size();
      final int extras = size - maxViews;
      size--;
      for (int j = 0; j < extras; j++) {
        scrapPile.removeAt(size--);
      }
    }
  }

  private static View retrieveFromScrap(SparseArrayCompat<View> scrapViews, int position) {
    if (scrapViews == null) return null;
    int size = scrapViews.size();
    if (size > 0) {
      for (int i = 0; i < size; i++) {
        int fromPosition = scrapViews.keyAt(i);
        View view = scrapViews.get(fromPosition);
        if (fromPosition == position) {
          scrapViews.removeAt(i);
          return view;
        }
      }
      int index = size - 1;
      View r = scrapViews.valueAt(index);
      scrapViews.removeAt(index);
      return r;
    }
    return null;
  }
}