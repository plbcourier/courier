package com.test.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.test.courier.ClearEditText;
import com.test.courier.R;
import com.test.entity.Constant;
import com.test.sqlite.UserinfoDBUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.service.JCommonService;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//---------------用户登录---------------
public class LoginActivity extends Activity implements View.OnClickListener {
    private TextView register_text,forget_password;//注册、忘记密码
    private ImageView back_wode,user_login;//返回、登入
    private ClearEditText user_phone,user_password;//用户手机号、用户密码
    private Constant constant;//常量类
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();//初始化
    }

    private void init() {//初始化
        register_text=findViewById(R.id.user_register);
        back_wode=findViewById(R.id.back_wode_img);
        user_login=findViewById(R.id.user_login);
        user_phone=findViewById(R.id.user_phone);
        user_password=findViewById(R.id.user_pwd);
        forget_password=findViewById(R.id.forget_passwrod);
        constant = new Constant();

        register_text.setOnClickListener(this);
        back_wode.setOnClickListener(this);
        user_login.setOnClickListener(this);
        forget_password.setOnClickListener(this);

        user_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {//当用户名（手机号）输入框有11位，关闭软键盘
                if (s.length()==11){
                    hideAllInput(LoginActivity.this);
                }
            }
        });
    }

    private static void hideAllInput(Context context){//关闭软键盘
        InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager.isActive()){
            manager.toggleSoftInput(0,InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.user_register://注册按钮
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));//跳转至注册页
                break;
            case R.id.back_wode_img://返回按钮
                finish();
                break;
            case R.id.user_login://登录按钮
                LoginTask loginTask = new LoginTask();//实例化登录操作线程，并传入手机号、密码
                loginTask.execute(user_phone.getText().toString(),user_password.getText().toString());
                break;
            case R.id.forget_passwrod://忘记密码
                startActivity(new Intent(LoginActivity.this,ForgetActivity.class));
                break;
        }
    }

    private class LoginTask extends AsyncTask<String,Void,String>{//登录操作线程

        @Override
        protected String doInBackground(String... strings) {//子线程
            String jsonstr = null;
            try {
                jsonstr = userLogin(strings[0],strings[1]);//登录方法，传入手机号、密码
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonstr;//返回网络操作结果
        }

        @Override
        protected void onPostExecute(String jsonstr) {//获取到网络操作结果，解析json
            super.onPostExecute(jsonstr);
            if (jsonstr!=null && !"".equals(jsonstr)){//登录成功
                try {
                    jsonExtract(jsonstr);//解析json
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                finish();
            }else{
                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void jsonExtract(String jsonstr) throws JSONException {//解析json
        JSONObject jsonObject = new JSONObject(jsonstr);
        String id = jsonObject.getString("id");//骑手id
        String name = jsonObject.getString("name");//骑手名字
        String phone = jsonObject.getString("phone");//骑手手机号
        String status = jsonObject.getString("status");//骑手上下班状态
        String leftMoney = jsonObject.getString("leftMoney");//账户余额
        String password = jsonObject.getString("password");//密码
        String icon = jsonObject.getString("icon");//头像
        UserinfoDBUtil userinfoDBUtil = new UserinfoDBUtil();//数据库工具类
        SQLiteDatabase database = userinfoDBUtil.getSqLiteDatabase(LoginActivity.this);//获取用户信息数据库
        String sql = null;
        //查询本地数据库，有没有当前登录的用户信息
        Cursor cursor = database.query("userinfo",null,"userid = ?",new String[]{id},null,null,null);
        if (cursor.getCount()==0){//如果本地数据库没有当前用户，则插入一条数据
            sql = "insert into userinfo values(null,?,?,?,?,?,?,?)";//插入登录的骑手的数据
            database.execSQL(sql,new String[]{id,phone,password,name,status,leftMoney,icon});//传入参数
        }else{//否则修改数据
            sql = "update userinfo set phone = ? , password = ? , name = ? , status = ? ,leftmoney = ? , imagepath = ?" +
                    "where userid = ?";
            database.execSQL(sql,new String[]{phone,password,name,status,leftMoney,icon,id});
        }

        sql = "update userinfo set userid = ? where _id = 1";//更新userinfo表_id为0的数据，将userid列的值，设为登录的用户的id
        database.execSQL(sql,new String[]{id});//传入参数
        database.close();//关闭数据库
    }

    private String userLogin(String phone,String password) throws IOException {//用户登录方法
        String jsonstr = null;
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()//传入请求参数，手机号、密码
                .add("phone",phone)
                .add("password",password)
                .add("registrationID",JPushInterface.getRegistrationID(LoginActivity.this))
                .build();
        Request request = new Request.Builder()//请求对象
                .url(constant.PREFIX+constant.LOGIN)
                .method("POST",requestBody)
                .build();
        Response response = client.newCall(request).execute();//响应对象
        if (response.isSuccessful()){//响应成功
            jsonstr = response.body().string();//返回的json数据
        }
        return jsonstr;
    }

}