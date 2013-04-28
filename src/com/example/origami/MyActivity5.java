package com.example.origami;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-4-27
 * Time: 下午6:55
 * To change this template use File | Settings | File Templates.
 */
public class MyActivity5 extends Activity implements View.OnClickListener{

    Button runButton;

    View titleView, contentView;

    SearchController controller;

    boolean opened;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main4);

        runButton = (Button) findViewById(R.id.runButton);
        titleView = findViewById(R.id.titleView);
        contentView = findViewById(R.id.contentView);

        runButton.setOnClickListener(this);

        SearchController.Callback callback = new SearchController.Callback() {
            @Override
            public void opened() {
                Toast.makeText(MyActivity5.this, "已打开。", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void closed() {
                Toast.makeText(MyActivity5.this, "已关闭。", Toast.LENGTH_SHORT).show();
            }
        };

        controller = new AnimationSearchController(titleView, contentView, callback, true);
    }

    @Override
    public void onClick(View view) {
        if(opened){
            controller.close();
        }else {
            controller.open();
        }
        opened=!opened;
    }
}