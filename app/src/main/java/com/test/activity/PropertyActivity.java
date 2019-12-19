package com.test.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.courier.R;

//--------------我的资产-----------------
public class PropertyActivity extends Activity implements View.OnClickListener {
    private ImageView back_img;//返回按钮
    private TextView money_text;//余额显示
    private EditText money_edit;//提现金额输入框
    private Button tixian_btn;//提现按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property);
        init();//初始化
    }

    private void init() {//初始化
        back_img = findViewById(R.id.back_img);
        back_img.setOnClickListener(this);
        money_text = findViewById(R.id.money_text);
        money_edit = findViewById(R.id.money_edit);
        tixian_btn = findViewById(R.id.tixian_btn);
        tixian_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_img://返回按钮
                finish();
                break;
            case R.id.tixian_btn://提现按钮
                String balance = money_text.getText().toString();
                String timoney = money_edit.getText().toString();

                break;
        }
    }
}
