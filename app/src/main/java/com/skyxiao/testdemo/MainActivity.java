package com.skyxiao.testdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ScreenUtils.init(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
}
