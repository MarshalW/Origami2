package com.example.origami;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import static com.example.origami.OrigamiUtils.*;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-4-9
 * Time: 下午7:12
 * To change this template use File | Settings | File Templates.
 */
public class ViewUnit{
    private static int count;

    private int index;

    View titleView, contentView;

    Rect titleRect, contentRect;
    Bitmap titleViewBitmap, contentViewTopBitmap, contentViewBottomBitmap;

    public ViewUnit(View titleView, View contentView) {
        this.titleView = titleView;
        this.contentView = contentView;

        count++;
        index = count;
    }

    public void snapViews() {
        if (titleViewBitmap == null) {
            titleViewBitmap = loadBitmapFromView(titleView, titleView.getWidth(), titleView.getHeight());

            titleRect = new Rect();
            titleView.getDrawingRect(titleRect);
            contentRect = new Rect();
            contentView.getDrawingRect(contentRect);
        }

        if (contentViewTopBitmap != null) {
            contentViewTopBitmap.recycle();
            contentViewBottomBitmap.recycle();
        }

        Bitmap contentViewBitmap = loadBitmapFromView(contentView, contentView.getWidth(), contentView.getHeight());

        contentViewTopBitmap = Bitmap.createBitmap(contentViewBitmap, 0, 0, contentViewBitmap.getWidth(),
                contentViewBitmap.getHeight() / 2);
        contentViewBottomBitmap = Bitmap.createBitmap(contentViewBitmap, 0, contentViewBitmap.getHeight() / 2,
                contentViewBitmap.getWidth(), contentViewBitmap.getHeight() / 2);

        contentViewBitmap.recycle();
    }

    public void hideContentView(boolean hide) {
        contentView.setVisibility(hide ? View.GONE : View.VISIBLE);
    }

    @Override
    public String toString() {
        return "ViewUnit{" +
                "index=" + index +
                '}';
    }
}
