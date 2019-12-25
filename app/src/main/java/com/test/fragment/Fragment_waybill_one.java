package com.test.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.test.activity.WaybillOneActivity;
import com.test.courier.CoordsUtil;
import com.test.courier.R;
import com.test.entity.Constant;
import com.test.entity.Coords;
import com.test.entity.Jiewaybill;
import com.test.sqlite.UserinfoDBUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

/**
 * Created by Administrator on 2019/12/11.
 */

//--------------待接单-----------------
public class Fragment_waybill_one extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {
    private View view;//该fragment的视图
    private ListView listView;
    private Myadapter myadapter;//listview的适配器
    private SwipeRefreshLayout refresh_layout;//下拉刷新控件
    private Constant constant;//常量类
    private List<Jiewaybill> jiewaybills = new ArrayList<>();//当前列表的数据集合
    private Coords coords;//坐标数据
    private CoordsUtil coordsUtil;//定位工具类
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_waybill_one,container,false);
        init();//初始化
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();//刷新listview
    }

    public List<Jiewaybill> getJiewaybills() {
        return jiewaybills;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.e("tag", "Fragment_waybill_one--setUserVisibleHint: "+isVisibleToUser);
        if (isVisibleToUser){
            final Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(100);
                        refreshData();//刷新listview
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
    }

    private void refreshData() {//刷新listview
        UserinfoDBUtil userinfoDBUtil = new UserinfoDBUtil();//userinfo数据库工具类
        SQLiteDatabase database = userinfoDBUtil.getSqLiteDatabase(getActivity());//获取用户信息数据库
        //查询数据库获取当前登录的用户的userid
        Cursor cursor = database.query("userinfo",null,null,null,null,null,null);
        String deliverymanId = null;
        cursor.moveToFirst();
        deliverymanId = cursor.getString(1);
        database.close();
        cursor.close();
        if (deliverymanId.equals("0")){//如果当前无用户登录，return
            return;
        }
        RefreshDataTask refreshDataTask = new RefreshDataTask();
        refreshDataTask.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle bundle = new Bundle();
        bundle.putString("address",jiewaybills.get(position).getAddress());//收货地址
        bundle.putString("freightInsurance",jiewaybills.get(position).getFreightInsurance());//货物保险费
        bundle.putString("leaveMessage",jiewaybills.get(position).getLeaveMessage());//消息
        bundle.putString("money",jiewaybills.get(position).getMoney());//运单金额
        bundle.putString("orderid",jiewaybills.get(position).getOrderid());//运单id
        bundle.putString("orderTime",jiewaybills.get(position).getOrderTime());//运单时间
        bundle.putString("phone",jiewaybills.get(position).getPhone());//发货人电话
        bundle.putString("freight",jiewaybills.get(position).getFreight());//配送费
        bundle.putString("marketName",jiewaybills.get(position).getMarketName());//发货地址
        bundle.putString("longitude",jiewaybills.get(position).getLongitude());//发货地址经度
        bundle.putString("latitude",jiewaybills.get(position).getLatitude());//发货地址纬度
        bundle.putString("distance",jiewaybills.get(position).getDistance());//我的位置与发货点距离
        bundle.putString("goodsName",jiewaybills.get(position).getGoodsName());//货物名
        bundle.putString("distanceEnd",jiewaybills.get(position).getDistanceEnd());//发货点与收货点距离
        bundle.putString("goodUrl",jiewaybills.get(position).getGoodUrl());//图片链接
        bundle.putString("number",jiewaybills.get(position).getNumber());//货物数量

        Intent intent = new Intent();
        intent.setClass(getActivity(), WaybillOneActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private class RefreshDataTask extends AsyncTask<Void,Void,List<Jiewaybill>>{//刷新数据操作线程
        @Override
        protected List<Jiewaybill> doInBackground(Void... voids) {
            String jsonstr = null;
            List<Jiewaybill> jiewaybillList = new ArrayList<>();//获取网络数据的临时集合
            try {
                jsonstr = getJson();//获取josn数据
                jiewaybillList = jsonExtract(jsonstr);//将json数据解析成集合
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jiewaybillList;
        }//刷新数据线程

        @Override
        protected void onPostExecute(List<Jiewaybill> jiewaybillList) {
            jiewaybills = jiewaybillList;//将临时集合存入listview的数据集合
            myadapter.notifyDataSetChanged();//通知适配器数据已更新
            super.onPostExecute(jiewaybillList);
        }
    }

    private String getJson() throws IOException {//获取待接单数据
        coordsUtil.getLongitude(getActivity());
        String jsonstr = null;
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()//传入经纬度
                .add("longitude",coords.getLongitude()+"")
                .add("latitude",coords.getLatitude()+"")
                .build();
        Request request = new Request.Builder()//请求对象
                .url(constant.PREFIX+constant.JIEWAYBILL)
                .method("POST",requestBody)
                .build();
        Response response = client.newCall(request).execute();//响应对象
        if (response.isSuccessful()){//响应成功
            jsonstr = response.body().string();//返回的json数据
        }
        return jsonstr;
    }

    private List<Jiewaybill> jsonExtract(String jsonstr) throws JSONException {
        List<Jiewaybill> jiewaybillList = new ArrayList<>();//存放解析后数据的临时集合
        if (jsonstr!=null||!"".equals(jsonstr)){
            JSONArray jsonArray = new JSONArray(jsonstr);//解析json数组对象
            for (int i=0;i<jsonArray.length();i++){//解析数据并存入临时集合
                JSONObject itemObject = jsonArray.getJSONObject(i);
                String address = itemObject.getString("address");//收货地址
                String freightInsurance = itemObject.getString("freightInsurance");//货物保险费
                String leaveMessage = itemObject.getString("leaveMessage");//消息
                String money = itemObject.getString("money");//运单金额
                String orderid = itemObject.getString("orderid");//运单id
                String orderTime = itemObject.getString("orderTime");//运单时间
                String phone = itemObject.getString("phone");//发货人电话
                String freight = itemObject.getString("freight");//配送费
                String marketName = itemObject.getString("marketName");//发货地址
                String longitude = itemObject.getString("longitude");//发货地址经度
                String latitude = itemObject.getString("latitude");//发货地址纬度
                String distance = itemObject.getString("distance");//我的位置与发货点距离
                String goodsName = itemObject.getString("goodsName");//货物名
                String distanceEnd = itemObject.getString("distanceEnd");//发货点与收货点距离
                String goodUrl = itemObject.getString("goodUrl");//图片链接
                String number = itemObject.getString("number");//货物数量

                jiewaybillList.add(new Jiewaybill(address,freightInsurance,leaveMessage,money,orderid,orderTime,phone
                ,freight,marketName,longitude,latitude,distance,goodsName,distanceEnd,goodUrl,number));
            }
        }
        return jiewaybillList;
    }

    private void init() {//初始化
        listView = view.findViewById(R.id.listview);
        myadapter = new Myadapter();
        listView.setAdapter(myadapter);
        listView.setOnItemClickListener(this);
        refresh_layout = view.findViewById(R.id.refresh_layout);
        refresh_layout.setOnRefreshListener(this);//下拉刷新监听
        constant = new Constant();//实例化常量类
        coords = new Coords();
        coordsUtil = new CoordsUtil();
    }

    @Override
    public void onRefresh() {//下拉刷新
        refreshData();
        refresh_layout.setRefreshing(false);//通知刷新控件刷新完毕
    }

    private class Myadapter extends BaseAdapter{//listview的适配器

        @Override
        public int getCount() {//listivew显示条数
            return jiewaybills.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_waybill_one_item,parent,false);
            TextView waybillnum_text = convertView.findViewById(R.id.waybillnum);//运单号
            TextView dist_text1 = convertView.findViewById(R.id.dist_text1);//距离1，我--取
            TextView dist_text2 = convertView.findViewById(R.id.dist_text2);//距离2，取--送
            TextView money_text = convertView.findViewById(R.id.money_text);//配送费
            TextView cargo_text = convertView.findViewById(R.id.cargo_text);//货物
            TextView startadd_text = convertView.findViewById(R.id.startadd_text);//起点
            TextView endadd_text = convertView.findViewById(R.id.endadd_text);//终点
            TextView receive_btn = convertView.findViewById(R.id.receive_btn);//接单按钮

            if (jiewaybills.size()!=0){
                waybillnum_text.setText(jiewaybills.get(position).getOrderid());
                money_text.setText(jiewaybills.get(position).getFreight());
                dist_text1.setText(jiewaybills.get(position).getDistance());
                dist_text2.setText(jiewaybills.get(position).getDistanceEnd());
                cargo_text.setText(jiewaybills.get(position).getGoodsName());
                startadd_text.setText(jiewaybills.get(position).getMarketName());
                endadd_text.setText(jiewaybills.get(position).getAddress());
            }

            receive_btn.setOnClickListener(new View.OnClickListener() {//接单按钮点击事件
                @Override
                public void onClick(View v) {
                    AddOrderTask addOrderTask = new AddOrderTask();//开启接单线程，传入运单号
                    addOrderTask.execute(jiewaybills.get(position).getOrderid());
                }
            });

            return convertView;
        }
    }

    private class AddOrderTask extends AsyncTask<String,Void,String>{//接单按钮线程

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
                refreshData();//刷新数据
                Toast.makeText(getActivity(), "接单成功", Toast.LENGTH_SHORT).show();
            }else{
                refreshData();//刷新数据
                Toast.makeText(getActivity(), "接单失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String addOrder(String orderid) throws IOException {//接单网络请求
        UserinfoDBUtil userinfoDBUtil = new UserinfoDBUtil();//实例化userinfo表工具类
        SQLiteDatabase database = userinfoDBUtil.getSqLiteDatabase(getActivity());//获取userinfo数据库
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

}
