package com.test.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.mob.MobSDK;
import com.test.courier.R;
import com.test.entity.Constant;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//------------忘记密码--------------
public class ForgetActivity extends Activity implements View.OnClickListener {
    private ImageView back;
    private EditText phone_edit,pwd_edit,captcha_edit;
    private Button submit_btn,captcha_btn;
    private Time time;//定义变量
    private Constant constant;//常量类
    private String name1;
    private Message message;
    private EventHandler eventHandler;//事件handler

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
        init();
        MobSDK.init(this);//短信SDK初始化
        initEventHandler();//初始化事件handler
    }

    private void initEventHandler() {//初始化事件handler
        eventHandler = new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE){//回调完成
                    if (event==SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE){//提交验证码成功
                        //存储用户数据：
                        message = new Message();
                        message.what=1;
                        handler.sendMessage(message);
                    }else if(event==SMSSDK.EVENT_GET_VERIFICATION_CODE){//获取验证码成功
                        /*message = new Message();
                        message.what=2;
                        handler.sendMessage(message);*/
                    }else if(event==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){//返回支持发送验证码的国家列表
                        /*message = new Message();
                        message.what=3;
                        handler.sendMessage(message);*/
                    }
                }else{
                    ((Throwable)data).printStackTrace();//回调出错，控制台打印错误信息
                }
            }
        };
        SMSSDK.registerEventHandler(eventHandler);//注册短信回调
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==1){//验证成功
                String phonestr=phone_edit.getText().toString();
                String pwdstr=pwd_edit.getText().toString();
                reset_passwordsOkHttp(phonestr,pwdstr);
            }else if(msg.what==2){

            }
        }
    };

    @Override
    public void onClick(View view) {
        String phone;
        switch (view.getId()){
            case R.id.back_img://返回
                finish();
                break;
            case R.id.captcha_btn://发送验证码
                phone = phone_edit.getText().toString();
                if (phone.length()>10){
                    SMSSDK.getVerificationCode("+86",phone);
                    time.start();//开始计时
                }
                break;
            case R.id.submit_btn://提交
                String pwdstr = pwd_edit.getText().toString();
                String captcha = captcha_edit.getText().toString();
                if (pwdstr.length()>5 && captcha.length()>0){
                    phone = phone_edit.getText().toString();
                    SMSSDK.submitVerificationCode("+86",phone,captcha);
                }
                break;
        }
    }

    public void reset_passwordsOkHttp(String user,String pwd){
        resetpasswordsOkHttp(user, pwd, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //在这里对异常情况进行处理
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseData = response.body().string();
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
                            Toast.makeText(ForgetActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(ForgetActivity.this,"修改失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    public void resetpasswordsOkHttp(String user,String pwd,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("phone",user)
                .add("newPwd",pwd)
                .build();
        Request request = new Request.Builder()
                .url(constant.PREFIX+constant.CHANGEPASSWORD)
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }

    private void init(){
        back = findViewById(R.id.back_img);          //返回
        phone_edit = findViewById(R.id.phone_edit);//手机号码
        pwd_edit = findViewById(R.id.pwd_edit);    //获取新密码
        submit_btn = findViewById(R.id.submit_btn);         //提交
        captcha_edit = findViewById(R.id.captcha_edit);       //获取验证码
        captcha_btn = findViewById(R.id.captcha_btn);       //获取点击验证码
        time=new Time(60000,1000);
        constant = new Constant();
        back.setOnClickListener(this);
        submit_btn.setOnClickListener(this);
        captcha_btn.setOnClickListener(this);
    }

    /*短信验证按钮倒计时*/
    class Time extends CountDownTimer {
        public Time(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        /*倒计时过程*/
        public void onTick(long millisUntilFinished) {
            captcha_btn.setClickable(false);//防止重复点击
            captcha_btn.setText(millisUntilFinished / 1000 + "s");
            captcha_btn.setBackgroundResource(R.drawable.bg_click);
        }
        @Override
        /*倒计时结束*/
        public void onFinish() {
            captcha_btn.setText("获取验证码");
            captcha_btn.setClickable(true);
            captcha_btn.setBackgroundResource(R.drawable.bg_message);
        }
    }
}
