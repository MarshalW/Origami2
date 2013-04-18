package com.example.origami;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-4-18
 * Time: 下午4:52
 * To change this template use File | Settings | File Templates.
 */
public class MyActivity2 extends Activity implements View.OnClickListener {

    Button searchButton, closeButton;

    ResultViewController controller;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main1);

        searchButton = (Button) this.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(this);
        closeButton = (Button) this.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(this);

        /**
         * 不带动画的版本
         */
//        controller = new ResultViewController(
//                this,
//                (FrameLayout) findViewById(R.id.contentLayout),
//                new ResultViewController.ResultCallback() {
//                    @Override
//                    public void opened() {
//                        Toast.makeText(MyActivity2.this, "打开后", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void closed() {
//                        Toast.makeText(MyActivity2.this, "关闭后", Toast.LENGTH_SHORT).show();
//                    }
//                });

        /**
         * 带动画的版本
         */
        controller = new AnimationResultViewController(
                this,
                (FrameLayout) findViewById(R.id.contentLayout),
                new ResultViewController.ResultCallback() {
                    @Override
                    public void opened() {
                        Toast.makeText(MyActivity2.this, "打开后", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void closed() {
                        Toast.makeText(MyActivity2.this, "关闭后", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Override
    public void onClick(View view) {
        if (view == searchButton) {
            controller.open();
        } else {
            controller.close();
        }
    }
}
