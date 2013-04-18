package com.example.origami;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-4-18
 * Time: 下午5:43
 * To change this template use File | Settings | File Templates.
 */
public class AnimationResultViewController extends ResultViewController {

    ResultOrigamiView resultOrigamiView;

    public AnimationResultViewController(Context context, FrameLayout targetView, ResultCallback callback) {
        super(context, targetView, callback);

        resultOrigamiView=new ResultOrigamiView(context,targetView);
        targetView.addView(resultOrigamiView);
    }

    @Override
    public void close() {
        resultOrigamiView.startAnimation(false,callback);
    }

    @Override
    public void open() {
        resultOrigamiView.startAnimation(true,callback);
    }
}
