package com.test.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
public class Fragment_waybill_three extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
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

    private void init() {//初始化
        constant = new Constant();
        listView = view.findViewById(R.id.listview);
        myadapter = new Myadapter();
        listView.setAdapter(myadapter);
        refresh_layout = view.findViewById(R.id.refresh_layout);
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
        JSONArray jsonArray = new JSONArray(jsonstr);//解析json数组
        for (int i=0;i<jsonArray.length();i++){
            JSONObject itemObject = jsonArray.getJSONObject(i);
            String id = itemObject.getString("id");
            String orderid = itemObject.getString("orderId");//订单号
            String freight = itemObject.getString("freight");//配送费
            String address = itemObject.getString("address");//送货点
            String date = itemObject.getString("date");//时间
            String longitude = itemObject.getString("longitude");//经度
            String latitude = itemObject.getString("latitude");//纬度
            String phone = itemObject.getString("phone");//手机号
            songwaybillList.add(new Songwaybill(id,orderid,freight,address,date,longitude,latitude,phone));
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

                }
            });

            consignee_btn.setOnClickListener(new View.OnClickListener() {//联系收货人，拨打电话
                @Override
                public void onClick(View v) {
                    String phone = songwaybills.get(position).getPhone();//手机号
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_CALL);
                    Uri phoneNum = Uri.parse("tel:"+ phone);
                    intent.setData(phoneNum);
                    startActivity(intent);
                }
            });

            delivery_btn.setOnClickListener(new View.OnClickListener() {//确认送达按钮
                @Override
                public void onClick(View v) {
                    String id = songwaybills.get(position).getId();
                    String orderId = songwaybills.get(position).getOrderId();//运单号
                    DeliveryTask deliveryTask = new DeliveryTask();
                    deliveryTask.execute(id,orderId);//传入参数
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
