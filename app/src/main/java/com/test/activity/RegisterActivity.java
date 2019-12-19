package com.test.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.test.courier.ClearEditText;
import com.test.courier.R;
import com.test.entity.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//--------------用户注册---------------
public class RegisterActivity extends Activity implements View.OnClickListener {
    private ImageView backLoginPage;
    private ClearEditText editText;
    private ClearEditText editText1;
    private ClearEditText editText2;
    private Button zhuce;
    private Constant constant;
    private String name1;
    private Button identifying;
    private TimeCount time;
    private Button submit;//短信验证按钮
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);
        editText=findViewById(R.id.name);//获取注册名字
        editText1=findViewById(R.id.number);//获取注册手机号码
        editText2=findViewById(R.id.pwd);//获取注册密码
        zhuce=findViewById(R.id.zhuce);//注册按钮
        zhuce.setOnClickListener(this);//注册按钮点击事件
        identifying=findViewById(R.id.up_codebt);//获取验证码按钮
        identifying.setOnClickListener(this);
        time=new TimeCount(60000,1000);
        constant=new Constant();//实力换Constant类
        init();
    }

    private void init() {
        backLoginPage=findViewById(R.id.back_login_img);
        backLoginPage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_login_img:
                finish();
                break;
            case R.id.zhuce:
                String editText_name=editText.getText().toString();
                String editText_number=editText1.getText().toString();
                String editText_pwd=editText2.getText().toString();
                registerWithOkHttp(editText_name,editText_number,editText_pwd);
                break;
            case R.id.up_codebt:
                time.start();//开始计时
        }
    }
    public void registerWithOkHttp(final String name, String number, String pwd){
        OkHttp(name, number, pwd, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseData=response.body().string();
                try {
                    JSONObject jsonObject=new JSONObject(responseData);
                    name1=jsonObject.getString("result");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (name1.equals("success")){
                            Toast.makeText(RegisterActivity.this,"注册成功"+responseData,Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(RegisterActivity.this,"注册失败"+responseData,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
    public void OkHttp(String name,String number,String pwd,okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        RequestBody body=new FormBody.Builder()
                .add("name",name)
                .add("phone",number)
                .add("password",pwd)
                .build();
        Request request=new Request.Builder()
                .url(constant.PREFIX+constant.REGISTER)
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }

    /*短信验证按钮倒计时*/
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        /*倒计时过程*/
        public void onTick(long millisUntilFinished) {
            identifying.setClickable(false);//防止重复点击
            identifying.setText(millisUntilFinished / 1000 + "s");
            identifying.setBackgroundResource(R.drawable.bg_click);
        }

        @Override
        /*倒计时结束*/
        public void onFinish() {
            identifying.setText("获取验证码");
            identifying.setClickable(true);
            identifying.setBackgroundResource(R.drawable.bg_message);
        }
    }


    }
