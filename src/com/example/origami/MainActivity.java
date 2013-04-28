package com.example.origami;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-4-18
 * Time: 下午4:45
 * To change this template use File | Settings | File Templates.
 */
public class MainActivity extends Activity implements View.OnClickListener {
    private Button button1, button2,button3,button4,button5;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.root);

        button1 = (Button) this.findViewById(R.id.button1);
        button1.setOnClickListener(this);
        button2 = (Button) this.findViewById(R.id.button2);
        button2.setOnClickListener(this);
        button3 = (Button) this.findViewById(R.id.button3);
        button3.setOnClickListener(this);
        button4 = (Button) this.findViewById(R.id.button4);
        button4.setOnClickListener(this);
        button5 = (Button) this.findViewById(R.id.button5);
        button5.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();

        if(view==button1){
            intent.setClass(MainActivity.this, MyActivity2.class);
        }

        if (view == button2) {
            intent.setClass(MainActivity.this, MyActivity.class);
        }

        if (view == button3) {
            intent.setClass(MainActivity.this, MyActivity3.class);
        }

        if (view == button4) {
            intent.setClass(MainActivity.this, MyActivity4.class);
        }

        if (view == button5) {
            intent.setClass(MainActivity.this, MyActivity5.class);
        }

        startActivity(intent);
    }
}
