package com.test.activity;


import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.test.courier.R;

//--------------我的消息----------------
public class MessageActivity extends AppCompatActivity {
    ImageView backtrack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        ActionBar actionBar = getSupportActionBar();//获取标题栏
        actionBar.hide();//隐藏AppCompatActivity的标题栏
        backtrack = findViewById(R.id.back_img);//获取返回控件
        //为返回按钮做点击事件
        backtrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
