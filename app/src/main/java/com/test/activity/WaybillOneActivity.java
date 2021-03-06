package com.test.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedImageView;
import com.test.courier.R;
import com.test.entity.Constant;
import com.test.sqlite.UserinfoDBUtil;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

//-----------待接单运单详情页--------------
public class WaybillOneActivity extends Activity implements View.OnClickListener {
    private Constant constant;//常量类
    private ImageView back_img;//返回按钮
    private String address,freightInsurance,leaveMessage,money,orderid,orderTime,phone
            ,freight,marketName,longitude,latitude,distance,goodsName,distanceEnd,goodUrl,number;
    private TextView orderid_text,freight_text,distance_text,distanceend_text//运单号，配送费，我的位置与发货点距离,发货点与收货点距离
            ,goodsName_text,marketName_text,address_text,receive_btn,freightInsurance_text//货物名，取货地址，送货地址，接单按钮，货物保险
            ,money_text,orderTime_text,number_text;//运单总金额，运单时间，物品数量
    private RoundedImageView goods_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waybill_one);
        getintentData();//获取来自intent的数据
        init();//初始化控件
        initData();//初始化填充数据
        initImage();//初始化货物图片
    }

    private void getintentData() {//获取来自intent的数据
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        address = bundle.getString("address");//收货地址
        freightInsurance = bundle.getString("freightInsurance");//货物保险费
        leaveMessage = bundle.getString("leaveMessage");//消息
        money = bundle.getString("money");//运单金额
        orderid = bundle.getString("orderid");//运单id
        orderTime = bundle.getString("orderTime");//运单时间
        phone = bundle.getString("phone");//发货人电话
        freight = bundle.getString("freight");//配送费
        marketName = bundle.getString("marketName");//发货地址
        longitude = bundle.getString("longitude");//发货地址经度
        latitude = bundle.getString("latitude");//发货地址纬度
        distance = bundle.getString("distance");//我的位置与发货点距离
        goodsName = bundle.getString("goodsName");//货物名
        distanceEnd = bundle.getString("distanceEnd");//发货点与收货点距离
        goodUrl = bundle.getString("goodUrl");//图片链接
        number = bundle.getString("number");//货物数量

        orderTime = dealDateFormat(orderTime);//处理时间格式
    }

    private void init() {//初始化控件
        constant = new Constant();
        back_img = findViewById(R.id.back_img);
        back_img.setOnClickListener(this);
        orderid_text = findViewById(R.id.orderid_text);
        freight_text = findViewById(R.id.freight_text);
        distance_text = findViewById(R.id.distance_text);
        distanceend_text = findViewById(R.id.distanceend_text);
        goodsName_text = findViewById(R.id.goodsName_text);
        marketName_text = findViewById(R.id.marketName_text);
        address_text = findViewById(R.id.address_text);
        freightInsurance_text = findViewById(R.id.freightInsurance_text);
        money_text = findViewById(R.id.money_text);
        goods_img = findViewById(R.id.goods_img);
        orderTime_text = findViewById(R.id.orderTime_text);
        number_text = findViewById(R.id.number_text);

        receive_btn = findViewById(R.id.receive_btn);
        receive_btn.setOnClickListener(this);
    }

    private void initData() {//初始化填充数据
        orderid_text.setText(orderid);
        freight_text.setText(freight);
        distance_text.setText(distance);
        distanceend_text.setText(distanceEnd);
        goodsName_text.setText(goodsName);
        marketName_text.setText(marketName);
        address_text.setText(address);
        freightInsurance_text.setText(freightInsurance);
        money_text.setText(money);
        orderTime_text.setText(orderTime);
        number_text.setText(number);
    }

    private void initImage() {//初始化货物图片
        GoodsImageTask goodsImageTask = new GoodsImageTask(goods_img);
        goodsImageTask.execute(goodUrl);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_img://返回按钮
                finish();
                break;
            case R.id.receive_btn://接单按钮
                AlertDialog.Builder builder = new AlertDialog.Builder(WaybillOneActivity.this);
                builder.setTitle("确认接单?");

                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AddOrderTask addOrderTask = new AddOrderTask();//开启接单线程，传入运单号
                        addOrderTask.execute(orderid);
                    }
                });

                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.show();
                break;
        }
    }


    private class GoodsImageTask extends AsyncTask<String,Void,Bitmap>{//一个用于下载图片的线程,传入值图片url，返回值Bitmap图片
        RoundedImageView roundedImageView;

        public GoodsImageTask(RoundedImageView roundedImageView) {
            this.roundedImageView = roundedImageView;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            String picurl = strings[0];
            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder().url(picurl).build();
            Bitmap bitmap = null;
            try {
                ResponseBody responseBody = okHttpClient.newCall(request).execute().body();
                InputStream inputStream = responseBody.byteStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            roundedImageView.setImageBitmap(bitmap);
        }
    }

    private class AddOrderTask extends AsyncTask<String,Void,String> {//接单按钮线程

        @Override
        protected String doInBackground(String... strings) {
            String jsonstr = null;
            try {
                jsonstr = addOrder(strings[0]);//接单方法
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonstr;
        }

        @Override
        protected void onPostExecute(String jsonstr) {
            super.onPostExecute(jsonstr);
            //如果返回的json数据中包含success，则刷新数据并提示接单成功
            if (jsonstr!=null && !"".equals(jsonstr) && jsonstr.indexOf("success")!=-1){
                Toast.makeText(WaybillOneActivity.this, "接单成功", Toast.LENGTH_SHORT).show();
                finish();
            }else{
                Toast.makeText(WaybillOneActivity.this, "接单失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String addOrder(String orderid) throws IOException {//接单网络请求
        UserinfoDBUtil userinfoDBUtil = new UserinfoDBUtil();//实例化userinfo表工具类
        SQLiteDatabase database = userinfoDBUtil.getSqLiteDatabase(WaybillOneActivity.this);//获取userinfo数据库
        //查询数据库获取当前登录的用户的userid
        Cursor cursor = database.query("userinfo",null,null,null,null,null,null);
        cursor.moveToFirst();
        String deliverymanId = cursor.getString(1);
        database.close();
        cursor.close();

        String jsonstr = null;
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()//请求参数，传入运单号和骑手id
                .add("orderId",orderid)
                .add("deliverymanId",deliverymanId)
                .build();
        Request request = new Request.Builder()//请求对象
                .url(constant.PREFIX+constant.ADDORDER)
                .method("POST",requestBody)
                .build();
        Response response = client.newCall(request).execute();//响应对象
        if (response.isSuccessful()){//响应成功
            jsonstr = response.body().string();//返回的json数据
        }
        return jsonstr;
    }

    public static String dealDateFormat(String oldDate) {//时间格式处理
        Date date1 = null;
        DateFormat df2 = null;
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date date = df.parse(oldDate);
            SimpleDateFormat df1 = new SimpleDateFormat ("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
            date1 = df1.parse(date.toString());
            df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        } catch (ParseException e) {

            e.printStackTrace();
        }
        return df2.format(date1);
    }
}
