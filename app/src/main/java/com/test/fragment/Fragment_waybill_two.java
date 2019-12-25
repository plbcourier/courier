package com.test.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import com.test.activity.WaybillOneActivity;
import com.test.activity.WaybillTwoActivity;
import com.test.courier.R;
import com.test.entity.Constant;
import com.test.entity.Quwaybill;
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

//---------------待取货----------------
public class Fragment_waybill_two extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {
    private View view;//该fragment的视图
    private ListView listView;
    private Myadapter myadapter;//listview的适配器
    private SwipeRefreshLayout refresh_layout;//下拉刷新控件
    private Constant constant;//常量类
    private List<Quwaybill> quwaybills = new ArrayList<>();//当前列表的数据集合
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_waybill_two,container,false);
        init();//初始化
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();//刷新listview
    }

    public List<Quwaybill> getQuwaybills() {
        return quwaybills;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.e("tag", "Fragment_waybill_two--setUserVisibleHint: "+isVisibleToUser);
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
        listView = view.findViewById(R.id.listview);
        constant = new Constant();
        myadapter = new Myadapter();
        listView.setAdapter(myadapter);
        listView.setOnItemClickListener(this);
        refresh_layout = view.findViewById(R.id.refresh_layout);
        refresh_layout.setOnRefreshListener(this);//设置下拉刷新监听
    }

    @Override
    public void onRefresh() {//下拉刷新
        refreshData();//刷新数据
        refresh_layout.setRefreshing(false);//通知刷新控件刷新完毕
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
        bundle.putString("id",quwaybills.get(position).getId());
        bundle.putString("orderid",quwaybills.get(position).getOrderid());//订单号
        bundle.putString("date",quwaybills.get(position).getDate());//时间
        bundle.putString("marketName",quwaybills.get(position).getMarketName());//发货地址
        bundle.putString("pickCode",quwaybills.get(position).getPickCode());//取货码
        bundle.putString("freight",quwaybills.get(position).getFreight());//配送费
        bundle.putString("longitude",quwaybills.get(position).getLongitude());//发货地址经度
        bundle.putString("latitude",quwaybills.get(position).getLatitude());//发货地址纬度
        bundle.putString("phone",quwaybills.get(position).getPhone());//发货人手机号
        bundle.putString("address",quwaybills.get(position).getAddress());//收货地址
        bundle.putString("goodsName",quwaybills.get(position).getGoodsName());//货物名
        bundle.putString("number",quwaybills.get(position).getNumber());//货物数量
        bundle.putString("goodUrl",quwaybills.get(position).getGoodUrl());//货物图片链接

        Intent intent = new Intent();
        intent.setClass(getActivity(), WaybillTwoActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private class RefreshDataTask extends AsyncTask<Void,Void,List<Quwaybill>> {//刷新数据操作线程
        @Override
        protected List<Quwaybill> doInBackground(Void... voids) {//刷新数据线程
            String jsonstr = null;
            List<Quwaybill> quwaybillList = new ArrayList<>();//临时存储数据集合
            try {
                jsonstr = getJson();//获取json数据
                quwaybillList = jsonExtract(jsonstr);//解析json数据
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return quwaybillList;
        }

        @Override
        protected void onPostExecute(List<Quwaybill> quwaybillList) {
            quwaybills = quwaybillList;//将临时数据集合存入listview的数据集合
            myadapter.notifyDataSetChanged();//通知适配器数据已更新
            super.onPostExecute(quwaybillList);
        }
    }

    private String getJson() throws IOException {//获取列表数据
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
                .add("id",deliverymanId)
                .build();
        Request request = new Request.Builder()//请求对象
                .url(constant.PREFIX+constant.PICKING)
                .method("POST",requestBody)
                .build();
        Response response = client.newCall(request).execute();//响应对象
        if (response.isSuccessful()){//响应成功
            jsonstr = response.body().string();//返回的json数据
        }
        return jsonstr;
    }

    /**
     * 解析json数据
     * @param jsonstr json字符串
     * @return
     * @throws JSONException
     */
    private List<Quwaybill> jsonExtract(String jsonstr) throws JSONException {
        List<Quwaybill> quwaybillList = new ArrayList<>();
        if (jsonstr!=null||!"".equals(jsonstr)) {
            JSONArray jsonArray = new JSONArray(jsonstr);//解析json数组
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject itemObject = jsonArray.getJSONObject(i);
                String id = itemObject.getString("id");
                String orderid = itemObject.getString("orderId");//订单号
                String date = itemObject.getString("date");//时间
                String marketName = itemObject.getString("marketName");//发货地址
                String pickCode = itemObject.getString("pickCode");//取货码
                String freight = itemObject.getString("freight");//配送费
                String longitude = itemObject.getString("longitude");//发货地址经度
                String latitude = itemObject.getString("latitude");//发货地址纬度
                String phone = itemObject.getString("phone");//发货人手机号
                String address = itemObject.getString("address");//收货地址
                String goodsName = itemObject.getString("goodsName");//货物名
                String number = itemObject.getString("number");//货物数量
                String goodUrl = itemObject.getString("goodUrl");//货物图片链接

                quwaybillList.add(new Quwaybill(id, orderid, date, marketName, pickCode, freight, longitude, latitude, phone
                        , address, goodsName, number, goodUrl));
            }
        }
        return quwaybillList;
    }

    private class Myadapter extends BaseAdapter{//listview的适配器

        @Override
        public int getCount() {//listview条目数量
            return quwaybills.size();
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
            convertView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_waybill_two_item,parent,false);
            TextView waybillnum_text = convertView.findViewById(R.id.waybillnum_text);//运单号
            TextView money_text = convertView.findViewById(R.id.money_text);//配送费
            TextView startadd_text = convertView.findViewById(R.id.startadd_text);//取货点
            TextView navi_btn = convertView.findViewById(R.id.navi_btn);//导航按钮
            TextView shipper_btn = convertView.findViewById(R.id.shipper_btn);//联系发货人按钮
            TextView pickcode_text = convertView.findViewById(R.id.pickcode_text);//取货码
            final TextView pick_btn = convertView.findViewById(R.id.pick_btn);//确认取货

            if (quwaybills.size()!=0){
                waybillnum_text.setText(quwaybills.get(position).getOrderid());//设置运单号
                money_text.setText(quwaybills.get(position).getFreight());//设置配送费
                startadd_text.setText(quwaybills.get(position).getMarketName());//设置取货点
                pickcode_text.setText(quwaybills.get(position).getPickCode());//设置取货码
            }

            navi_btn.setOnClickListener(new View.OnClickListener() {//导航按钮
                @Override
                public void onClick(View v) {
                    String longitude = quwaybills.get(position).getLongitude();//经度
                    String latitude = quwaybills.get(position).getLatitude();//纬度
                    String marketName = quwaybills.get(position).getMarketName();//取货点
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        //定位权限
                        String[] locationPermission = {Manifest.permission.ACCESS_FINE_LOCATION};
                        if (ContextCompat.checkSelfPermission(getActivity(),locationPermission[0]) != PackageManager.PERMISSION_GRANTED) {
                            // 如果没有授予该权限，就去提示用户请求
                            ActivityCompat.requestPermissions(getActivity(), locationPermission, 300);
                        }
                        if (ContextCompat.checkSelfPermission(getActivity(),locationPermission[0])== PackageManager.PERMISSION_GRANTED) {
                            // 如果没有授予该权限，就去提示用户请求
                            Intent intent=new Intent(getActivity(), NaviActivity.class);
                            startActivity(intent);

                        }
                    }

                }
            });

            shipper_btn.setOnClickListener(new View.OnClickListener() {//联系发货人按钮，拨打电话
                @Override
                public void onClick(View v) {
                    String phone = quwaybills.get(position).getPhone();//手机号
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    Uri data = Uri.parse("tel:" + phone);
                    intent.setData(data);
                    startActivity(intent);
                }
            });

            pick_btn.setOnClickListener(new View.OnClickListener() {//确认取货
                @Override
                public void onClick(View v) {
                    String id = quwaybills.get(position).getId();
                    PickGoodsTask pickGoodsTask = new PickGoodsTask();//取货线程，传入运单id
                    pickGoodsTask.execute(id);
                }
            });

            return convertView;
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
                refreshData();//刷新数据
                Toast.makeText(getActivity(), "取货成功", Toast.LENGTH_SHORT).show();
            }else{
                refreshData();//刷新数据
                Toast.makeText(getActivity(), "取货失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String pickGoods(String id) throws IOException {//确认取货网络请求方法
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
}
