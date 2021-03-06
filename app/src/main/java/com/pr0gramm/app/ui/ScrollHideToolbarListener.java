package com.pr0gramm.app.ui;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewPropertyAnimator;

import com.google.common.base.Optional;
import com.pr0gramm.app.util.AndroidUtility;

/**
 */
public class ScrollHideToolbarListener {
    private final View toolbar;
    private int toolbarMarginOffset;
    private ViewPropertyAnimator animation;

    public ScrollHideToolbarListener(View toolbar) {
        this.toolbar = toolbar;

        toolbar.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            int newHeight = bottom - top;
            int oldHeight = oldBottom - oldTop;

            if (oldHeight > 0 && newHeight != oldHeight && toolbarMarginOffset == oldHeight) {
                toolbarMarginOffset = newHeight;
                applyToolbarPosition(false);
            }
        });
    }

    private void applyToolbarPosition(boolean animated) {
        // stop any previous animation
        if (animation != null) {
            animation.cancel();
            animation = null;
        }

        boolean targetVisible = toolbar.getHeight() > toolbarMarginOffset;
        int y = -toolbarMarginOffset;
        if (animated) {
            if (targetVisible) {
                toolbar.setVisibility(View.VISIBLE);
            }

            animation = toolbar.animate()
                    .translationY(y)
                    .setDuration(250)
                    .setListener(AndroidUtility.endAction(() -> {
                        if (!targetVisible) {
                            toolbar.setVisibility(View.INVISIBLE);
                        }
                    }));

            animation.start();
        } else {
            toolbar.setTranslationY(y);
            toolbar.setVisibility(targetVisible ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public void onScrolled(int dy) {
        int abHeight = toolbar.getHeight();
        if (abHeight == 0)
            return;

        toolbarMarginOffset += dy;
        if (toolbarMarginOffset >= abHeight) {
            toolbarMarginOffset = abHeight;
        }

        if (toolbarMarginOffset < 0)
            toolbarMarginOffset = 0;

        applyToolbarPosition(false);
    }

    public void onScrollFinished(int y) {
        int abHeight = toolbar.getHeight();
        if (abHeight == 0)
            return;

        if (y < abHeight) {
            reset();
        } else {
            toolbarMarginOffset = (toolbarMarginOffset > abHeight / 2) ? abHeight : 0;
            applyToolbarPosition(true);
        }
    }

    public void reset() {
        if (toolbarMarginOffset != 0) {
            toolbarMarginOffset = 0;
            applyToolbarPosition(true);
        }
    }

    public void setToolbarAlpha(int alpha) {
        Drawable background = toolbar.getBackground();
        if (background instanceof ColorDrawable) {
            int color = ((ColorDrawable) background).getColor();
            color = Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
            toolbar.setBackgroundColor(color);
        }
    }

    public int getToolbarHeight() {
        return toolbar.getHeight();
    }

    public int getVisibleHeight() {
        return (int) (toolbar.getHeight() + toolbar.getTranslationY());
    }

    public void hide() {
        if (toolbarMarginOffset != toolbar.getHeight()) {
            toolbarMarginOffset = toolbar.getHeight();
            applyToolbarPosition(true);
        }
    }

    public interface ToolbarActivity {
        ScrollHideToolbarListener getScrollHideToolbarListener();
    }

    /**
     * This method estimates scrolling based on y value of the first element
     * in this recycler view. If scrolling could not be estimated, an empty optional
     * will be returned.
     *
     * @param recyclerView The recycler view to estimate scrolling of
     */
    public static Optional<Integer> estimateRecyclerViewScrollY(RecyclerView recyclerView) {
        Integer scrollY = null;
        View view = recyclerView.getLayoutManager().findViewByPosition(0);
        if (view != null) {
            scrollY = -(int) view.getY();
        }

        return Optional.fromNullable(scrollY);
    }
}
