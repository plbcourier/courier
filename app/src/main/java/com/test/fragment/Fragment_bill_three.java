package com.test.fragment;

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

import com.test.courier.R;
import com.test.entity.AllBill;
import com.test.entity.Constant;
import com.test.entity.SubBill;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/12/12.
 */

//------------------账单  申请提现------------------
public class Fragment_bill_three extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private View view;//该fragment的视图
    private ListView listView;
    private Myadapter myadapter;//listview的适配器
    private SwipeRefreshLayout refresh_layout;//刷新控件
    private Constant constant;//常量类
    private List<SubBill> subBills = new ArrayList<>();//listview的数据集合

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =inflater.inflate(R.layout.fragment_bill_three,container,false);
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
        Log.e("tag", "Fragment_bill_three--setUserVisibleHint: "+isVisibleToUser);
    }

    private void refreshData() {//刷新listivew

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
            return 10;
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
            convertView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_bill_three_item,parent,false);





            return convertView;
        }
    }
}
