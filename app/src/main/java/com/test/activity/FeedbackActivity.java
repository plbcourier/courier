package com.test.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.courier.MainActivity;
import com.test.courier.R;

//---------------建议反馈-----------------
public class FeedbackActivity extends AppCompatActivity {
    ImageView backtrack;
    Button submit;
    private EditText opinion_edit;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        backtrack = findViewById(R.id.back_img);//获取返回按钮的控件
        submit = findViewById(R.id.submit);//获取提交按钮的控件
        ActionBar actionBar = getSupportActionBar();//获取标题栏
        actionBar.hide();//隐藏AppCompatActivity的标题栏
        opinion_edit = findViewById(R.id.opinion_edit);
        //为返回按钮做点击事件
        backtrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //为提交按钮做点击事件
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (opinion_edit.getText().toString().length()>0){
                    showNormalDialog();
                }
            }
        });
    }
    private void showNormalDialog(){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog = new AlertDialog.Builder(FeedbackActivity.this);
        normalDialog.setTitle("提交成功");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        // 显示
        normalDialog.show();
    }
}
