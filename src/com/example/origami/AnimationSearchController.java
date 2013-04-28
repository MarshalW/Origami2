package com.example.origami;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-4-27
 * Time: 上午9:04
 * To change this template use File | Settings | File Templates.
 */
public class AnimationSearchController extends SearchController {

    SearchOrigamiView origamiView;

    Handler handler;

    long duration = 400;

    boolean isOpen;

    boolean animating;

    public SearchOrigamiView getOrigamiView() {
        return origamiView;
    }

    public AnimationSearchController(final View titleView, final View contentView, Callback callback) {
        super(titleView, contentView, callback);
        this.init();
    }

    public AnimationSearchController(View titleView, View contentView, Callback callback, boolean horizon) {
        super(titleView, contentView, callback, horizon);
        this.init();
    }

    private void init() {
        SearchOrigamiView.StartEndCallback callback = new SearchOrigamiView.StartEndCallback() {
            @Override
            public void start() {
                titleView.setVisibility(View.INVISIBLE);
                if (contentView != null) {
                    contentView.setVisibility(View.INVISIBLE);
                }
                animating = true;
            }

            @Override
            public void end() {
                if (contentView != null) {
                    contentView.setVisibility(View.VISIBLE);
                }

                if (isOpen) {
                    AnimationSearchController.super.open();
                } else {
                    AnimationSearchController.super.close();
                }
                animating = false;
            }
        };

        if (isHorizon) {
            this.origamiView = new SearchHorizonOrigamiView(titleView.getContext(), duration, callback);
        } else {
            this.origamiView = new SearchOrigamiView(titleView.getContext(), duration, callback);
        }

        getParentViewGroup().addView(this.origamiView);
        handler = new Handler();
    }

    protected ViewGroup getParentViewGroup() {
        return (ViewGroup) titleView.getParent();
    }

    @Override
    public void close() {
        if (titleView.getVisibility() != View.VISIBLE) {
            return;
        }

        if (animating) {
            return;
        }

        isOpen = false;
        this.origamiView.startAnimation(false, getTitleBitmaps(), getContentBitmap());
    }

    @Override
    public void open() {
        if (titleView.getVisibility() == View.VISIBLE) {
            return;
        }

        if (animating) {
            return;
        }

        isOpen = true;
        this.origamiView.startAnimation(true, getTitleBitmaps(), getContentBitmap());
    }

    private Bitmap[] getTitleBitmaps() {
        Bitmap[] titleBitmaps = new Bitmap[2];

        titleView.setDrawingCacheEnabled(true);
        Bitmap bitmap = titleView.getDrawingCache();

        if (isHorizon) {
            titleBitmaps[0] = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth() / 2,
                    bitmap.getHeight());
            titleBitmaps[1] = Bitmap.createBitmap(bitmap, bitmap.getWidth() / 2, 0,
                    bitmap.getWidth() / 2, bitmap.getHeight());
        } else {
            titleBitmaps[0] = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight() / 2);
            titleBitmaps[1] = Bitmap.createBitmap(bitmap, 0, bitmap.getHeight() / 2,
                    bitmap.getWidth(), bitmap.getHeight() / 2);
        }

        titleView.setDrawingCacheEnabled(false);
        return titleBitmaps;
    }

    private Bitmap getContentBitmap() {
        if (contentView == null) {
            Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
            Bitmap bitmap = Bitmap.createBitmap(1, 1, conf); // this creates a MUTABLE bitmap
            return bitmap;
        }
        contentView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(contentView.getDrawingCache());
        contentView.setDrawingCacheEnabled(false);
        return bitmap;
    }
}
