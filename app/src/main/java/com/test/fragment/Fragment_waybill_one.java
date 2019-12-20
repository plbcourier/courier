package com.test.fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
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
import com.test.entity.Jiewaybill;
import com.test.sqlite.UserinfoDBUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
public class Fragment_waybill_one extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private View view;//该fragment的视图
    private ListView listView;
    private Myadapter myadapter;//listview的适配器
    private SwipeRefreshLayout refresh_layout;//下拉刷新控件
    private Constant constant;//常量类
    private List<Jiewaybill> jiewaybills = new ArrayList<>();//当前列表的数据集合
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
        String jsonstr = null;
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()//传入经纬度
                .add("longitude","112.586483")
                .add("latitude","26.828654")
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
        JSONArray jsonArray = new JSONArray(jsonstr);//解析json数组对象
        for (int i=0;i<jsonArray.length();i++){//解析数据并存入临时集合
            JSONObject itemObject = jsonArray.getJSONObject(i);
            String orderid = itemObject.getString("orderid");//订单号
            String freight = itemObject.getString("freight");//配送费
            String distance = itemObject.getString("distance");//距离A
            String goodsName = itemObject.getString("goodsName");//货品名
            String marketName = itemObject.getString("marketName");//起点，取货点
            String address = itemObject.getString("address");//终点，送货点
            jiewaybillList.add(new Jiewaybill(orderid,freight,distance,goodsName,marketName,address));
        }
        return jiewaybillList;
    }

    private void init() {//初始化
        listView = view.findViewById(R.id.listview);
        myadapter = new Myadapter();
        listView.setAdapter(myadapter);
        refresh_layout = view.findViewById(R.id.refresh_layout);
        refresh_layout.setOnRefreshListener(this);//下拉刷新监听
        constant = new Constant();//实例化常量类
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
