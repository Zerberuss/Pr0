package com.pr0gramm.app;

import android.app.Activity;
import android.content.Context;

import com.pr0gramm.app.ui.base.BaseAppCompatActivity;

/**
 * Provides dagger injection points/components
 */
public class Dagger {
    private Dagger() {
    }

    public static AppComponent appComponent(Context context) {
        return Pr0grammApplication.get(context).appComponent.get();
    }

    public static ActivityComponent activityComponent(Activity activity) {
        if (activity instanceof BaseAppCompatActivity) {
            // create or reuse the graph
            return ((BaseAppCompatActivity) activity).getActivityComponent();
        } else {
            return newActivityComponent(activity);
        }
    }

    public static ActivityComponent newActivityComponent(Activity activity) {
        return appComponent(activity).activiyComponent(new ActivityModule(activity));
    }
}
