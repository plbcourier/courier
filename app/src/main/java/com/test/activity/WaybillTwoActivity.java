package com.test.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
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

//-----------------待取货运单详情-------------------
public class WaybillTwoActivity extends Activity implements View.OnClickListener {
    private Constant constant;//常量类
    private ImageView back_img;//返回按钮
    private String id,orderid,date,marketName,pickCode,freight,longitude
            ,latitude,phone,address,goodsName,number,goodUrl;
    private TextView orderid_text,freight_text,date_text//运单号，配送费，时间
            ,goodsName_text,number_text,marketName_text//物品名，数量，取货点
            ,address_text,navi_btn,phone_btn//送货点，导航按钮，拨打电话按钮
            ,pick_btn,pickcode_text;//确认收货按钮，取货码
    private RoundedImageView goods_img;//货物图片
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waybill_two);
        getintentData();
        init();//初始化控件
        initData();//初始化填充数据
        initImage();//初始化货物图片
    }

    private void getintentData() {//获取来自intent的数据
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        id = bundle.getString("id");
        orderid = bundle.getString("orderid");//订单号
        date = bundle.getString("date");//时间
        marketName = bundle.getString("marketName");//发货地址
        pickCode = bundle.getString("pickCode");//取货码
        freight = bundle.getString("freight");//配送费
        longitude = bundle.getString("longitude");//收货地址经度
        latitude = bundle.getString("latitude");//收货地址纬度
        phone = bundle.getString("phone");//收货人手机号
        address = bundle.getString("address");//送货点
        goodsName = bundle.getString("goodsName");//货物名
        number = bundle.getString("number");//货物数量
        goodUrl = bundle.getString("goodUrl");//货物图片链接

        date = dealDateFormat(date);//处理时间格式
    }

    private void init(){
        constant = new Constant();
        back_img = findViewById(R.id.back_img);
        back_img.setOnClickListener(this);
        orderid_text = findViewById(R.id.orderid_text);
        freight_text = findViewById(R.id.freight_text);
        date_text = findViewById(R.id.date_text);
        goodsName_text = findViewById(R.id.goodsName_text);
        number_text = findViewById(R.id.number_text);
        marketName_text = findViewById(R.id.marketName_text);
        address_text = findViewById(R.id.address_text);
        navi_btn = findViewById(R.id.navi_btn);
        navi_btn.setOnClickListener(this);
        phone_btn = findViewById(R.id.phone_btn);
        phone_btn.setOnClickListener(this);
        pick_btn = findViewById(R.id.pick_btn);
        pick_btn.setOnClickListener(this);
        goods_img = findViewById(R.id.goods_img);
        pickcode_text = findViewById(R.id.pickcode_text);
    }

    private void initData() {//初始化填充数据
        orderid_text.setText(orderid);
        freight_text.setText(freight);
        date_text.setText(date);
        goodsName_text.setText(goodsName);
        number_text.setText(number);
        marketName_text.setText(marketName);
        address_text.setText(address);
        pickcode_text.setText(pickCode);
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
            case R.id.navi_btn://导航按钮
                Bundle bundle = new Bundle();
                bundle.putString("type","qu");
                bundle.putString("marketName",marketName);
                bundle.putString("address",address);
                Intent intent2=new Intent(WaybillTwoActivity.this, NaviActivity.class);
                intent2.putExtras(bundle);
                startActivity(intent2);
                break;
            case R.id.phone_btn://拨打电话
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + phone);
                intent.setData(data);
                startActivity(intent);
                break;
            case R.id.pick_btn://确认取货
                AlertDialog.Builder builder = new AlertDialog.Builder(WaybillTwoActivity.this);
                builder.setTitle("确认取货?");

                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PickGoodsTask pickGoodsTask = new PickGoodsTask();
                        pickGoodsTask.execute(id);
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

    private class GoodsImageTask extends AsyncTask<String,Void,Bitmap> {//一个用于下载图片的线程,传入值图片url，返回值Bitmap图片
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

    private class PickGoodsTask extends AsyncTask<String,Void,String>{//取货操作线程

        @Override
        protected String doInBackground(String... strings) {
            String jsonstr = null;
            try {
                jsonstr = pickGoods(strings[0]);//发出确认取货请求
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonstr;
        }

        @Override
        protected void onPostExecute(String jsonstr) {
            super.onPostExecute(jsonstr);
            //请求成功则刷新listview数据并提示
            if (jsonstr!=null && !"".equals(jsonstr) && jsonstr.indexOf("success")!=-1){
                Toast.makeText(WaybillTwoActivity.this, "取货成功", Toast.LENGTH_SHORT).show();
                finish();
            }else{
                Toast.makeText(WaybillTwoActivity.this, "取货失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String pickGoods(String id) throws IOException {//确认取货网络请求方法
        UserinfoDBUtil userinfoDBUtil = new UserinfoDBUtil();//userinfo数据库工具类
        SQLiteDatabase database = userinfoDBUtil.getSqLiteDatabase(WaybillTwoActivity.this);//获取数据库
        //查询数据库获取当前登录的用户的userid
        Cursor cursor = database.query("userinfo",null,null,null,null,null,null);
        cursor.moveToFirst();
        String deliverymanId = cursor.getString(1);
        database.close();
        cursor.close();

        String jsonstr = null;
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()//请求参数，传入运单id，骑手id
                .add("id",id)
                .add("deliverymanId",deliverymanId)
                .build();
        Request request = new Request.Builder()//请求对象
                .url(constant.PREFIX+constant.PICKGOODS)
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
