package com.korn.im.allin1.ui.controlers;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.View;

/**
 * Created by korn on 07.08.16.
 */
public abstract class Controller {
    private Activity activity;

    public Controller(Activity activity) {
        this.activity = activity;
    }

    public abstract void create();

    public abstract void restoreInstance(Bundle savedInstanceState);

    public abstract void prepare();

    public abstract void update();

    public abstract void release();

    public abstract void saveInstance(Bundle outInstanceState);

    public abstract void destroy();

    public View getView(@IdRes int viewId) {
        return getActivity().findViewById(viewId);
    }

    public Activity getActivity() {
        return activity;
    }
}
