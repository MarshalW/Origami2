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

    public void add(ViewUnit viewUnit) {
        viewUnits.add(viewUnit);
        viewUnit.titleView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        for (ViewUnit unit : viewUnits) {
            if (view == unit.titleView) {
                unit.contentView.setVisibility(unit.contentView.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            } else {
                unit.contentView.setVisibility(View.GONE);
            }
        }
    }
}
