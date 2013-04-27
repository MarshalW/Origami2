package com.example.origami;

import android.view.View;
import android.widget.FrameLayout;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-4-26
 * Time: 下午8:38
 * To change this template use File | Settings | File Templates.
 */
public class SearchController {

    protected View titleView, contentView;

    protected Callback callback;

    public SearchController(View titleView, View contentView, Callback callback) {
        this.contentView = contentView;
        this.titleView = titleView;
        this.callback = callback;
    }

    public void open() {
        titleView.setVisibility(View.VISIBLE);

        if(contentView!=null){
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(contentView.getWidth(),
                    contentView.getHeight());
            layoutParams.setMargins(0, titleView.getHeight(), 0, 0);
            contentView.setLayoutParams(layoutParams);
        }

        if (callback != null) {
            callback.opened();
        }
    }

    public void close() {
        titleView.setVisibility(View.INVISIBLE);

        if(contentView!=null){
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(contentView.getWidth(),
                    contentView.getHeight());
            contentView.setLayoutParams(layoutParams);
        }

        if (callback != null) {
            callback.closed();
        }
    }

    interface Callback {

        void opened();

        void closed();
    }
}
