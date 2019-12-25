package com.test.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.courier.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

//------------------待送达运单详情---------------------
public class WaybillThreeActivity extends Activity implements View.OnClickListener {
    private ImageView back_img;//返回按钮
    private TextView orderid_text;//运单号
    private String id,orderid,freight,address,date,longitude
            ,latitude,phone,marketName,goodsName,number,goodUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waybill_three);
        init();
    }

    private void init(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        id = bundle.getString("id");
        orderid = bundle.getString("orderid");//订单号
        freight = bundle.getString("freight");//配送费
        address = bundle.getString("address");//送货点
        date = bundle.getString("date");//时间
        longitude = bundle.getString("longitude");//收货地址经度
        latitude = bundle.getString("latitude");//收货地址纬度
        phone = bundle.getString("phone");//收货人手机号
        marketName = bundle.getString("marketName");//发货地址
        goodsName = bundle.getString("goodsName");//货物名
        number = bundle.getString("number");//货物数量
        goodUrl = bundle.getString("goodUrl");//货物图片链接

        date = dealDateFormat(date);//处理时间格式

        back_img = findViewById(R.id.back_img);
        back_img.setOnClickListener(this);
        orderid_text = findViewById(R.id.orderid_text);
        orderid_text.setText(orderid);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_img://返回按钮
                finish();
                break;
        }
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
