package com.test.courier;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.test.entity.Constant;
import com.test.entity.Coords;
import com.test.fragment.Fragment_bill;
import com.test.fragment.Fragment_my;
import com.test.fragment.Fragment_waybill;
import com.test.sqlite.UserinfoDBUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener{
    private List<Fragment> fragmentList;//存放父fragment的集合
    private MyViewPager myViewPager;//自定义viewpager，禁止了滑动
    private RadioGroup radioGroup;//单选按钮组
    private RadioButton radio_waybill;//运单按钮
    private FgPagerAdapter fgPagerAdapter;//fragmentpageradapter
    private CoordsUtil coordsUtil;//定位工具类
    private Coords coords;//坐标数据实体类
    private Constant constant;//常量类
    private UploadLocationTask uploadLocationTask;//上传坐标线程
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();//初始化
    }

    private void init() {
        ActionBar actionBar = getSupportActionBar();//获取标题栏
        actionBar.hide();//隐藏AppCompatActivity的标题栏
        fragmentList = new ArrayList<>();//实例化集合并按顺序添加fragment
        fragmentList.add(new Fragment_waybill());//运单
        fragmentList.add(new Fragment_bill());//账单
        fragmentList.add(new Fragment_my());//我的
        myViewPager = findViewById(R.id.myviewpager);//viewpager
        radioGroup = findViewById(R.id.radiogroup);//单选按钮组
        radio_waybill = findViewById(R.id.radio_waybill);//运单按钮
        fgPagerAdapter = new FgPagerAdapter(getSupportFragmentManager());//fragmentpageradapter
        radio_waybill.setChecked(true);//默认选中运单界面
        radioGroup.setOnCheckedChangeListener(this);//单选按钮组的子项单选按钮的监听
        myViewPager.setAdapter(fgPagerAdapter);//设置适配器
        coordsUtil = new CoordsUtil();
        coordsUtil.getLongitude(MainActivity.this);
        coords = new Coords();
        constant = new Constant();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        UserinfoDBUtil userinfoDBUtil = new UserinfoDBUtil();//userinfo数据库工具类
        SQLiteDatabase database = userinfoDBUtil.getSqLiteDatabase(MainActivity.this);//获取用户信息数据库
        //查询数据库获取当前登录的用户的userid
        Cursor cursor = database.query("userinfo",null,null,null,null,null,null);
        String deliverymanId = null;
        cursor.moveToFirst();
        deliverymanId = cursor.getString(1);
        database.close();
        cursor.close();

        if (!deliverymanId.equals("0")){
            //开启上传骑手坐标线程
            uploadLocationTask = new UploadLocationTask();
            uploadLocationTask.execute();
        }

    }

    private class UploadLocationTask extends AsyncTask<String,String,String>{//上传骑手坐标线程

        @Override
        protected String doInBackground(String... strings) {
            String jsonstr = null;
                coordsUtil.getLongitude(MainActivity.this);
                UserinfoDBUtil userinfoDBUtil = new UserinfoDBUtil();//userinfo数据库工具类
                SQLiteDatabase database = userinfoDBUtil.getSqLiteDatabase(MainActivity.this);//获取用户信息数据库
                //查询数据库获取当前登录的用户的userid
                Cursor cursor = database.query("userinfo",null,null,null,null,null,null);
                String deliverymanId = null;
                cursor.moveToFirst();
                deliverymanId = cursor.getString(1);
                database.close();
                cursor.close();
                if (!deliverymanId.equals("0")){
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("id",deliverymanId)
                            .add("longitude",coords.getLongitude()+"")
                            .add("latitude",coords.getLatitude()+"")
                            .build();
                    Request request = new Request.Builder()//请求对象
                            .url(constant.PREFIX+constant.LOCATION)
                            .method("POST",requestBody)
                            .build();
                    Response response = null;

                    try {
                         response = client.newCall(request).execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (response.isSuccessful()){
                        try {
                            jsonstr = response.body().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    publishProgress(jsonstr);
                }
            return jsonstr;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(uploadLocationTask.getStatus() != null && uploadLocationTask.getStatus() ==AsyncTask.Status.RUNNING){
            uploadLocationTask.cancel(true);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {//单选按钮点击监听
        switch (checkedId){//点击单选按钮改变viewpager
            case R.id.radio_waybill:
                myViewPager.setCurrentItem(0);
                break;
            case R.id.radio_bill:
                myViewPager.setCurrentItem(1);
                break;
            case R.id.radio_my:
                myViewPager.setCurrentItem(2);
                break;
        }
    }

    private class FgPagerAdapter extends FragmentPagerAdapter {
        public FgPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {//返回一个fragment
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {//返回fragment数量
            return fragmentList.size();
        }
    }
}
