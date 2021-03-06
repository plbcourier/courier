package com.test.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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

import com.test.activity.NaviActivity;
import com.test.activity.WaybillThreeActivity;
import com.test.activity.WaybillTwoActivity;
import com.test.courier.R;
import com.test.entity.Constant;
import com.test.entity.Quwaybill;
import com.test.entity.Songwaybill;
import com.test.sqlite.UserinfoDBUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2019/12/11.
 */

//---------------待送达------------------
public class Fragment_waybill_three extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {
    private View view;//该fragment的视图
    private ListView listView;
    private Myadapter myadapter;//listview的适配器
    private SwipeRefreshLayout refresh_layout;//刷新控件
    private Constant constant;//常量类
    private List<Songwaybill> songwaybills = new ArrayList<>();//当前列表的数据集合
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_waybill_three,container,false);
        init();//初始化
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();//刷新listview
    }

    public List<Songwaybill> getSongwaybills() {
        return songwaybills;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.e("tag", "Fragment_waybill_three--setUserVisibleHint: "+isVisibleToUser);
        if (isVisibleToUser){
            final Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(100);
                        refreshData();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
    }

    private void init() {//初始化
        constant = new Constant();
        listView = view.findViewById(R.id.listview);
        myadapter = new Myadapter();
        listView.setAdapter(myadapter);
        listView.setOnItemClickListener(this);
        refresh_layout = view.findViewById(R.id.refresh_layout);
        refresh_layout.setColorSchemeColors(Color.RED);
        refresh_layout.setOnRefreshListener(this);//刷新监听
    }

    @Override
    public void onRefresh() {
        refreshData();//刷新数据
        refresh_layout.setRefreshing(false);//通知刷新控件刷新完毕
    }

    private void refreshData() {
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
        bundle.putString("id",songwaybills.get(position).getId());
        bundle.putString("orderid",songwaybills.get(position).getOrderId());//订单号
        bundle.putString("freight",songwaybills.get(position).getFreight());//配送费
        bundle.putString("address",songwaybills.get(position).getAddress());//送货点
        bundle.putString("date",songwaybills.get(position).getDate());//时间
        bundle.putString("longitude",songwaybills.get(position).getLongitude());//收货地址经度
        bundle.putString("latitude",songwaybills.get(position).getLatitude());//收货地址纬度
        bundle.putString("phone",songwaybills.get(position).getPhone());//收货人手机号
        bundle.putString("marketName",songwaybills.get(position).getMarketName());//发货地址
        bundle.putString("goodsName",songwaybills.get(position).getGoodsName());//货物名
        bundle.putString("number",songwaybills.get(position).getNumber());//货物数量
        bundle.putString("goodUrl",songwaybills.get(position).getGoodUrl());//货物图片链接


        Intent intent = new Intent();
        intent.setClass(getActivity(), WaybillThreeActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private class RefreshDataTask extends AsyncTask<Void,Void,List<Songwaybill>>{

        @Override
        protected List<Songwaybill> doInBackground(Void... voids) {
            String jsonstr = null;
            List<Songwaybill> songwaybillList = new ArrayList<>();//临时存储数据集合
            try {
                jsonstr = getJson();//获取json数据
                songwaybillList = jsonExtract(jsonstr);//解析json数据
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return songwaybillList;
        }

        @Override
        protected void onPostExecute(List<Songwaybill> songwaybillList) {
            super.onPostExecute(songwaybills);
            songwaybills = songwaybillList;//将临时数据集合存入listview的数据集合
            myadapter.notifyDataSetChanged();//通知适配器数据已更新
        }
    }

    private String getJson() throws IOException {
        String jsonstr = null;
        UserinfoDBUtil userinfoDBUtil = new UserinfoDBUtil();//userinfo数据库工具类
        SQLiteDatabase database = userinfoDBUtil.getSqLiteDatabase(getActivity());//获取用户信息数据库
        //查询数据库获取当前登录的用户的userid
        Cursor cursor = database.query("userinfo",null,null,null,null,null,null);
        String deliverymanId = null;
        cursor.moveToFirst();
        deliverymanId = cursor.getString(1);
        database.close();
        cursor.close();

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()//传入骑手id
                .add("deliverymanId",deliverymanId)
                .build();
        Request request = new Request.Builder()//请求对象
                .url(constant.PREFIX+constant.DELIVERYING)
                .method("POST",requestBody)
                .build();
        Response response = client.newCall(request).execute();//响应对象
        if (response.isSuccessful()){//响应成功
            jsonstr = response.body().string();//返回的json数据
        }
        return jsonstr;
    }

    private List<Songwaybill> jsonExtract(String jsonstr) throws JSONException {
        List<Songwaybill> songwaybillList = new ArrayList<>();
        if (jsonstr!=null||!"".equals(jsonstr)) {
            JSONArray jsonArray = new JSONArray(jsonstr);//解析json数组
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject itemObject = jsonArray.getJSONObject(i);
                String id = itemObject.getString("id");
                String orderid = itemObject.getString("orderId");//订单号
                String freight = itemObject.getString("freight");//配送费
                String address = itemObject.getString("address");//送货点
                String date = itemObject.getString("date");//时间
                String longitude = itemObject.getString("longitude");//收货地址经度
                String latitude = itemObject.getString("latitude");//收货地址纬度
                String phone = itemObject.getString("phone");//收货人手机号
                String marketName = itemObject.getString("marketName");//发货地址
                String goodsName = itemObject.getString("goodsName");//货物名
                String number = itemObject.getString("number");//货物数量
                String goodUrl = itemObject.getString("goodUrl");//货物图片链接

                songwaybillList.add(new Songwaybill(id, orderid, freight, address, date
                        , longitude, latitude, phone, marketName, goodsName, number, goodUrl));
            }
        }
        return songwaybillList;
    }

    private class Myadapter extends BaseAdapter{//listview的适配器

        @Override
        public int getCount() {//listview的条数
            return songwaybills.size();
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
            convertView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_waybill_three_item,parent,false);
            TextView waybillnum_text = convertView.findViewById(R.id.waybillnum_text);//运单号
            TextView money_text = convertView.findViewById(R.id.money_text);//配送费
            TextView endadd_text = convertView.findViewById(R.id.endadd_text);//送货点
            TextView navi_btn = convertView.findViewById(R.id.navi_btn);//导航按钮
            TextView consignee_btn = convertView.findViewById(R.id.consignee_btn);//联系收货人按钮
            TextView delivery_btn = convertView.findViewById(R.id.delivery_btn);//确认送达按钮

            if (songwaybills.size()!=0){
                waybillnum_text.setText(songwaybills.get(position).getOrderId());
                money_text.setText(songwaybills.get(position).getFreight());
                endadd_text.setText(songwaybills.get(position).getAddress());
            }

            navi_btn.setOnClickListener(new View.OnClickListener() {//导航按钮
                @Override
                public void onClick(View v) {
                    String address = songwaybills.get(position).getAddress();//送货地址
                    String longitude = songwaybills.get(position).getLongitude();//经度
                    String latitude = songwaybills.get(position).getLatitude();//纬度
                    String marketName = songwaybills.get(position).getMarketName();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        //定位权限
                        String[] locationPermission = {Manifest.permission.ACCESS_FINE_LOCATION};
                        if (ContextCompat.checkSelfPermission(getActivity(),locationPermission[0]) != PackageManager.PERMISSION_GRANTED) {
                            // 如果没有授予该权限，就去提示用户请求
                            ActivityCompat.requestPermissions(getActivity(), locationPermission, 300);
                        }
                        if (ContextCompat.checkSelfPermission(getActivity(),locationPermission[0])== PackageManager.PERMISSION_GRANTED) {
                            Bundle bundle = new Bundle();
                            bundle.putString("type","song");
                            bundle.putString("marketName",marketName);
                            bundle.putString("address",address);
                            Intent intent=new Intent(getActivity(), NaviActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    }
                }
            });

            consignee_btn.setOnClickListener(new View.OnClickListener() {//联系收货人，拨打电话
                @Override
                public void onClick(View v) {
                    String phone = songwaybills.get(position).getPhone();//手机号
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    Uri data = Uri.parse("tel:" + phone);
                    intent.setData(data);
                    startActivity(intent);
                }
            });

            delivery_btn.setOnClickListener(new View.OnClickListener() {//确认送达按钮
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("确认送达?");

                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String id = songwaybills.get(position).getId();
                            String orderId = songwaybills.get(position).getOrderId();//运单号
                            DeliveryTask deliveryTask = new DeliveryTask();
                            deliveryTask.execute(id,orderId);//传入参数
                        }
                    });

                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    builder.show();
                }
            });

            return convertView;
        }
    }

    private class DeliveryTask extends AsyncTask<String,Void,String>{//确认送达操作线程

        @Override
        protected String doInBackground(String... strings) {
            String jsonstr = null;
            try {
                jsonstr = delivery(strings[0],strings[1]);//发出确认送达请求
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
                refreshData();//刷新数据
                Toast.makeText(getActivity(), "送达成功", Toast.LENGTH_SHORT).show();
            }else{
                refreshData();//刷新数据
                Toast.makeText(getActivity(), "送达失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String delivery(String id, String orderId) throws IOException {//确认送达网络请求方法
        UserinfoDBUtil userinfoDBUtil = new UserinfoDBUtil();//userinfo数据库工具类
        SQLiteDatabase database = userinfoDBUtil.getSqLiteDatabase(getActivity());//获取数据库
        //查询数据库获取当前登录的用户的userid
        Cursor cursor = database.query("userinfo",null,null,null,null,null,null);
        cursor.moveToFirst();
        String deliverymanId = cursor.getString(1);
        database.close();
        cursor.close();

        String jsonstr = null;
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()//请求参数，传入id，运单orderid，骑手id
                .add("id",id)
                .add("orderId",orderId)
                .add("deliveryId",deliverymanId)
                .build();
        Request request = new Request.Builder()//请求对象
                .url(constant.PREFIX+constant.DELIVERY)
                .method("POST",requestBody)
                .build();
        Response response = client.newCall(request).execute();//响应对象
        if (response.isSuccessful()){//响应成功
            jsonstr = response.body().string();//返回的json数据
        }
        return jsonstr;
    }

}
