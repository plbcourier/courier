package com.test.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.test.courier.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/12/10.
 */

//------------运单------------------
public class Fragment_waybill extends Fragment implements RadioGroup.OnCheckedChangeListener,ViewPager.OnPageChangeListener{
    private View view;//当前fragment的视图
    private List<Fragment> fragmentList;//存放子fragment的集合
    private FgPagerAdapter fgPagerAdapter;//fragmentpageradapter
    private ViewPager viewPager;
    private RadioGroup radioGroup;//单选按钮组
    private RadioButton radio1,radio2,radio3;//单选按钮，待接单，待取货，待送达
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_waybill,container,false);
        init();//初始化
        return view;
    }

    private void init() {//初始化
        fragmentList = new ArrayList<>();//实例化集合并按顺序添加fragment

        Fragment_waybill_one fragment_waybill_one = new Fragment_waybill_one();
        Fragment_waybill_two fragment_waybill_two = new Fragment_waybill_two();
        Fragment_waybill_three fragment_waybill_three = new Fragment_waybill_three();
        fragmentList.add(fragment_waybill_one);//待接单fragment
        fragmentList.add(fragment_waybill_two);//待取货fragment
        fragmentList.add(fragment_waybill_three);//待送达fragment

        viewPager = view.findViewById(R.id.viewpager);
        fgPagerAdapter = new FgPagerAdapter(getChildFragmentManager());
        radioGroup = view.findViewById(R.id.radiogroup);
        radio1 = view.findViewById(R.id.radio1);//待接单按钮
        radio2 = view.findViewById(R.id.radio2);//待取货按钮
        radio3 = view.findViewById(R.id.radio3);//待送达按钮
        radio1.setChecked(true);//默认待接单界面
        viewPager.setAdapter(fgPagerAdapter);//设置适配器
        viewPager.addOnPageChangeListener(this);//viewpager的状态改变监听
        radioGroup.setOnCheckedChangeListener(this);//单选按钮组的点击监听

        radio1.setText("待接单"+"("+fragment_waybill_one.getJiewaybills().size()+")");
        radio2.setText("待取货"+"("+fragment_waybill_two.getQuwaybills().size()+")");
        radio3.setText("待送达"+"("+fragment_waybill_three.getSongwaybills().size()+")");
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {//viewpager滑动状态监听
        if (state==2){//状态2表示滑动完毕，改变单选按钮
            switch (viewPager.getCurrentItem()){
                case 0:
                    radio1.setChecked(true);
                    break;
                case 1:
                    radio2.setChecked(true);
                    break;
                case 2:
                    radio3.setChecked(true);
                    break;
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {//单选按钮点击监听
        switch (checkedId){//单选按钮的点击事件，改变viewpager
            case R.id.radio1:
                viewPager.setCurrentItem(0);
                break;
            case R.id.radio2:
                viewPager.setCurrentItem(1);
                break;
            case R.id.radio3:
                viewPager.setCurrentItem(2);
                break;
        }
    }

    private class FgPagerAdapter extends FragmentPagerAdapter {
        public FgPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {//返回fragment
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {//返回fragment的个数
            return fragmentList.size();
        }
    }
}
