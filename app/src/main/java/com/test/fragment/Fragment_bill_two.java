package com.test.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.test.courier.R;

/**
 * Created by Administrator on 2019/12/12.
 */

//-----------------账单  配送费入账--------------
public class Fragment_bill_two extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private View view;//该fragment的视图
    private ListView listView;
    private Myadapter myadapter;//listview的适配器
    private SwipeRefreshLayout refresh_layout;//刷新控件
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bill_two,container,false);
        init();
        return view;
    }

    private void init() {//初始化
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
            convertView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_bill_two_item,parent,false);





            return convertView;
        }
    }
}
