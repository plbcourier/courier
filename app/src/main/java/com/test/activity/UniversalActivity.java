package com.test.activity;

import android.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.test.courier.R;

import org.w3c.dom.Text;

//设置=====>通用
public class UniversalActivity extends AppCompatActivity {
    ImageView imageView;
    TextView clearn;
    Switch sw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_universal);
        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        actionBar.hide();
        imageView=findViewById(R.id.back_img);
        clearn=findViewById(R.id.clearn);
        sw=findViewById(R.id.sw);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        clearn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(UniversalActivity.this,"清理成功!",Toast.LENGTH_SHORT).show();
            }
        });
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    Toast.makeText(UniversalActivity.this,"正在安装!",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(UniversalActivity.this,"取消安装!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
