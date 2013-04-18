package com.example.origami;

import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-4-9
 * Time: 下午7:54
 * To change this template use File | Settings | File Templates.
 */
public class SwitchViewController implements View.OnClickListener {
    protected List<ViewUnit> viewUnits = new ArrayList<ViewUnit>();

    protected ViewUnit lastChooseViewUnit;

    private OrigamiCallback callback;

    protected boolean blockCallback;

    public void add(ViewUnit viewUnit) {
        viewUnits.add(viewUnit);
        viewUnit.titleView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        for (ViewUnit unit : viewUnits) {
            if (view == unit.titleView) {
                if(!blockCallback){
                    if(unit.contentView.getVisibility() == View.GONE){
                        callback.onOrigamiOpened(unit.contentView);
                    }else{
                        callback.onOrigamiClosed(unit.contentView);
                    }
                }
                unit.contentView.setVisibility(unit.contentView.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                lastChooseViewUnit=unit;
            } else {
                unit.contentView.setVisibility(View.GONE);
            }
        }
    }

    public void addOrigamiCallback(final OrigamiCallback callback) {
        this.callback=callback;
    }

    interface OrigamiCallback {

        void onOrigamiOpened(View targetView);

        void onOrigamiClosed(View targetView);
    }
}
