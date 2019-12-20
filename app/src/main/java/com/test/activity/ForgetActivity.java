package com.test.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
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

//------------忘记密码--------------
public class ForgetActivity extends Activity implements View.OnClickListener {
    ImageView back;
    private EditText user_phone,user_pwd,up_code;
    private Button submit,up_codebt;
    private Time time;//定义变量
    private Constant constant;
    String name1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
        back = findViewById(R.id.back_img);          //返回
        user_phone = findViewById(R.id.user_phone);//手机号码
        user_pwd = findViewById(R.id.user_pwd);    //获取新密码
        submit = findViewById(R.id.submit);         //提交
        up_code = findViewById(R.id.up_code);       //获取验证码
        up_codebt = findViewById(R.id.up_codebt);       //获取点击验证码
        time=new Time(60000,1000);
        constant = new Constant();
        back.setOnClickListener(this);
        submit.setOnClickListener(this);
        up_codebt.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_img:
                finish();
                break;
            case R.id.up_codebt:
                time.start();//开始计时
                break;
            case R.id.submit:
                String user,pwd;
                user = user_phone.getText().toString();
                pwd = user_pwd.getText().toString();
                reset_passwordsOkHttp(user,pwd);
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
    /*短信验证按钮倒计时*/
    class Time extends CountDownTimer {
        public Time(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        /*倒计时过程*/
        public void onTick(long millisUntilFinished) {
            up_codebt.setClickable(false);//防止重复点击
            up_codebt.setText(millisUntilFinished / 1000 + "s");
            up_codebt.setBackgroundResource(R.drawable.bg_click);
        }
        @Override
        /*倒计时结束*/
        public void onFinish() {
            up_codebt.setText("获取验证码");
            up_codebt.setClickable(true);
            up_codebt.setBackgroundResource(R.drawable.bg_message);
        }
    }
}
