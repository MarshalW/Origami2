package com.example.origami;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-4-25
 * Time: 下午2:38
 * To change this template use File | Settings | File Templates.
 */
public class MyActivity3 extends Activity implements View.OnClickListener {

    Button searchButton, closeButton;

    View titleView, contentView;

    SearchController controller;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main2);

        searchButton = (Button) this.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(this);
        closeButton = (Button) this.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(this);

        titleView = findViewById(R.id.titleView);
        contentView = findViewById(R.id.contentView);

        Log.d("origami", ">>>>content view: " + contentView);


        SearchController.Callback callback = new SearchController.Callback() {
            @Override
            public void opened() {
                Toast.makeText(MyActivity3.this, "已打开。", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void closed() {
                Toast.makeText(MyActivity3.this, "已关闭。", Toast.LENGTH_SHORT).show();
            }
        };

        /**
         * 不带动画的版本
         */
//        controller = new SearchController(titleView, contentView, callback);

        /**
         * 带动画的版本
         */
        controller = new AnimationSearchController(titleView, contentView, callback);
    }

    @Override
    public void onClick(View view) {
        if (view == closeButton) {
            controller.close();
        } else {
            controller.open();
        }
    }
}