package com.test.fragment;

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
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.test.courier.R;
import com.test.entity.AllBill;
import com.test.entity.Constant;
import com.test.entity.Quwaybill;
import com.test.sqlite.UserinfoDBUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2019/12/12.
 */

//----------------账单  全部明细---------------
public class Fragment_bill_one extends Fragment  implements SwipeRefreshLayout.OnRefreshListener {
    private View view;//该fragment的视图
    private ListView listView;
    private Myadapter myadapter;//listview的适配器
    private SwipeRefreshLayout refresh_layout;//刷新控件
    private Constant constant;//常量类
    private List<AllBill> allBills = new ArrayList<>();//listview的数据集合

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bill_one,container,false);
        init();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();//刷新listivew
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.e("tag", "Fragment_bill_one--setUserVisibleHint: "+isVisibleToUser);
    }

    private void refreshData() {//刷新listivew
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

    private class RefreshDataTask extends AsyncTask<Void,Void,List<AllBill>>{//刷新数据操作线程

        @Override
        protected List<AllBill> doInBackground(Void... voids) {//刷新数据线程
            String jsonstr = null;
            List<AllBill> allBillList = new ArrayList<>();//临时存储数据集合
            try {
                jsonstr = getJson();//获取json数据
                allBillList = jsonExtract(jsonstr);//解析json数据
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return allBillList;
        }

        @Override
        protected void onPostExecute(List<AllBill> allBillList) {
            allBills = allBillList;//将临时数据集合存入listview的数据集合
            myadapter.notifyDataSetChanged();//通知适配器数据已更新
            super.onPostExecute(allBillList);
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
                .add("deliveryId",deliverymanId)
                .build();
        Request request = new Request.Builder()//请求对象
                .url(constant.PREFIX+constant.BILLDETALIS)
                .method("POST",requestBody)
                .build();
        Response response = client.newCall(request).execute();//响应对象
        if (response.isSuccessful()){//响应成功
            jsonstr = response.body().string();//返回的json数据
        }
        return jsonstr;
    }

    private List<AllBill> jsonExtract(String jsonstr) throws JSONException {
        List<AllBill> allBillList = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(jsonstr);//解析json数组
        for (int i=0;i<jsonArray.length();i++){
            JSONObject itemObject = jsonArray.getJSONObject(i);
            String id = itemObject.getString("id");
            String deliverymanId = itemObject.getString("deliverymanId");//骑手id
            String status = itemObject.getString("status");//"配送费入账" 或 "申请提现"
            String time = itemObject.getString("time");//时间
            String nowLeftMoney = itemObject.getString("nowLeftMoney");//操作后余额
            String orderMoney = itemObject.getString("orderMoney");//操作变动金额
            allBillList.add(new AllBill(id,deliverymanId,status,time,nowLeftMoney,orderMoney));
        }
        return allBillList;
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
        myadapter.notifyDataSetChanged();//通知适配器数据已更新
        refresh_layout.setRefreshing(false);//刷新完毕
    }

    private class Myadapter extends BaseAdapter {//listview的适配器

        @Override
        public int getCount() {//listview的条数
            return allBills.size();
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
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_bill_one_item,parent,false);
            TextView billid_text = convertView.findViewById(R.id.billid_text);//账单号
            TextView billmoney_text = convertView.findViewById(R.id.billmoney_text);//变动金额
            TextView time_text = convertView.findViewById(R.id.time_text);//时间
            TextView balance_text = convertView.findViewById(R.id.balance_text);//余额

            billid_text.setText(allBills.get(position).getId());
            if ("配送费入账".equals(allBills.get(position).getStatus())){
                billmoney_text.setText("配送费入账 +"+allBills.get(position).getOrderMoney());
                billmoney_text.setTextColor(Color.parseColor("#008B02"));
            }else {
                billmoney_text.setText("申请提现 -"+allBills.get(position).getOrderMoney());
                billmoney_text.setTextColor(Color.parseColor("#E91E63"));
            }
            /*SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = allBills.get(position).getTime();
            time = simpleDateFormat.format(time);*/
            time_text.setText(allBills.get(position).getTime());
            balance_text.setText(allBills.get(position).getNowLeftMoney());

            return convertView;
        }
    }
}
