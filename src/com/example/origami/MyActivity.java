package com.example.origami;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class MyActivity extends Activity {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        /**
         * 不带动画的版本
         */

//        SwitchViewController viewController = new SwitchViewController();
//        viewController.add(new ViewUnit(findViewById(R.id.title1), findViewById(R.id.content1)));
//        viewController.add(new ViewUnit(findViewById(R.id.title2), findViewById(R.id.content2)));

        /**
         * 带动画的版本
         */
        SwitchViewWithAnimationController viewController = new SwitchViewWithAnimationController(
                (ViewGroup) findViewById(R.id.targetViewGroup),
                (OrigamiView) findViewById(R.id.origamiView));
        viewController.add(new ViewUnit(findViewById(R.id.title1), findViewById(R.id.content1)));
        viewController.add(new ViewUnit(findViewById(R.id.title2), findViewById(R.id.content2)));
        viewController.init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //TODO 当glview渲染过后，需要做onResume
    }
}
