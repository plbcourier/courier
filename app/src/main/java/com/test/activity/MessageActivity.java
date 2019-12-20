package com.test.activity;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.test.courier.R;

//--------------我的消息----------------
public class MessageActivity extends AppCompatActivity {
    ImageView backtrack;
    View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        ActionBar actionBar = getSupportActionBar();//获取标题栏
        actionBar.hide();//隐藏AppCompatActivity的标题栏
        view = findViewById(R.id.inform);
        backtrack = findViewById(R.id.back_img);//获取返回控件
        //为返回按钮做点击事件
        backtrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                V();
            }
        });
    }
    public void V(){
             /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog = new AlertDialog.Builder(MessageActivity.this);
        normalDialog.setTitle("这是一个来自系统的消息");
        normalDialog.setMessage("你已成功加入批零帮");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        /*normalDialog.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });*/
        // 显示
        normalDialog.show();
    }
}
