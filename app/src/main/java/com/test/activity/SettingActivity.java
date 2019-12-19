package com.test.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.test.courier.R;

//------------------设置-----------------
public class SettingActivity extends Activity implements View.OnClickListener {
    private ImageView back_img;//返回图片按钮
    private TextView pay_setting,user_and_security,universal,regarding_plb;
    private Button bt_exit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        init();
    }

    private void init() {
        back_img = findViewById(R.id.back_img);
        back_img.setOnClickListener(this);
        bt_exit=findViewById(R.id.bt_exit);
        bt_exit.setOnClickListener(this);
        pay_setting=findViewById(R.id.pay_setting);
        pay_setting.setOnClickListener(this);
        user_and_security=findViewById(R.id.user_and_security);
        user_and_security.setOnClickListener(this);
        universal=findViewById(R.id.universal);
        universal.setOnClickListener(this);
        regarding_plb=findViewById(R.id.regarding_plb);
        regarding_plb.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_img://返回图片按钮
                finish();
                break;
            case R.id.bt_exit://退出账号
                break;
            case R.id.user_and_security://安全中心
                Intent intent2=new Intent(SettingActivity.this,SecurityCenterActivity.class);
                startActivity(intent2);
                break;
            case R.id.pay_setting://支付设置
                Intent intent=new Intent(SettingActivity.this,PayCenterActivity.class);
                startActivity(intent);
                break;
            case R.id.universal://通用
                Intent intent3=new Intent(SettingActivity.this,UniversalActivity.class);
                startActivity(intent3);
                break;
            case R.id.regarding_plb://关于批零帮
                Intent intent4=new Intent(SettingActivity.this,RegardingPlbActivity.class);
                startActivity(intent4);
                break;
        }
    }
}
