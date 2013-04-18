package com.example.origami;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Rect;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-4-9
 * Time: 下午8:40
 * To change this template use File | Settings | File Templates.
 */
public class SwitchViewWithAnimationController extends SwitchViewController implements View.OnTouchListener {
    protected OrigamiView origamiView;

    protected ViewGroup targetViewGroup;

    boolean outside;

    Handler handler = new Handler();

    int minUpTime=200;

    public SwitchViewWithAnimationController(ViewGroup targetViewGroup, OrigamiView origamiView) {
        this.targetViewGroup = targetViewGroup;
        this.origamiView = origamiView;
        this.blockCallback=true;
    }

    public void init() {
        /**
         * 一次性加载界面的截图
         */
        this.targetViewGroup.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                SwitchViewWithAnimationController.this.targetViewGroup.removeOnLayoutChangeListener(this);
                View targetView = SwitchViewWithAnimationController.this.targetViewGroup;

                for (ViewUnit unit : viewUnits) {
                    unit.snapViews();
                    unit.hideContentView(true);

                    unit.titleView.setOnTouchListener(SwitchViewWithAnimationController.this);
                }

                //将target view group长宽调整为正常情况
                ViewGroup group = (ViewGroup) targetView.getParent();
                targetView.setLayoutParams(new FrameLayout.LayoutParams(targetView.getWidth(), group.getHeight()));
            }
        });
    }

    @Override
    public void add(ViewUnit viewUnit) {
        super.add(viewUnit);
        viewUnit.contentView.setVisibility(View.VISIBLE);
        targetViewGroup.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 5000));
    }

    @Override
    public void onClick(final View view) {
        origamiView.startAnimation(this.viewUnits, view);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SwitchViewWithAnimationController.super.onClick(view);
            }
        }, origamiView.getDuration() * (3 / 4));
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Object[] result = isOpen();
            boolean open = (Boolean) result[0];

            if (open) {
                ViewUnit unit = (ViewUnit) result[1];
                unit.snapViews();
            }

            origamiView.snapTargetView(viewUnits);
            outside = false;
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE) {

            Rect rect = new Rect();
            view.getDrawingRect(rect);
            if (!outside && !rect.contains((int) event.getX(), (int) event.getY())) {
                outside = true;
                origamiView.clear();
            }
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            long time= (event.getEventTime()-event.getDownTime());
            if(time<minUpTime){
                try {
                    Thread.sleep(minUpTime-time);
                } catch (InterruptedException e) {

                }
            }
        }

        return false;
    }

    public void addOrigamiCallback(final OrigamiCallback callback) {
        origamiView.setAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Object[] result = isOpen();
                boolean open = (Boolean) result[0];
                if (open) {
                    ViewUnit unit = (ViewUnit) result[1];
                    callback.onOrigamiOpened(unit.contentView);
                } else {
                    callback.onOrigamiClosed(lastChooseViewUnit.contentView);
                }
            }
        });
    }

    private Object[] isOpen() {
        for (ViewUnit unit : viewUnits) {
            if (unit.contentView.getVisibility() == View.VISIBLE) {
                return new Object[]{true, unit};
            }
        }
        return new Object[]{false};
    }
}
