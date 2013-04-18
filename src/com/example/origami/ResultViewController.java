package com.example.origami;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-4-18
 * Time: 下午5:09
 * To change this template use File | Settings | File Templates.
 */
public class ResultViewController {

    protected FrameLayout targetView;

    protected ResultCallback callback;

    protected Context context;

    public ResultViewController(Context context, FrameLayout targetView, ResultCallback callback) {
        this.context=context;
        this.targetView = targetView;
        this.callback = callback;
    }

    public void open() {
        targetView.setVisibility(View.VISIBLE);
        if (callback != null) {
            callback.opened();
        }
    }

    public void close() {
        targetView.setVisibility(View.INVISIBLE);
        if (callback != null) {
            callback.closed();
        }
    }

    interface ResultCallback {

        void opened();

        void closed();
    }
}
