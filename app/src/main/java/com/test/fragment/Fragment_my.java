package com.test.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedImageView;
import com.test.activity.FeedbackActivity;
import com.test.activity.LoginActivity;
import com.test.activity.MessageActivity;
import com.test.activity.MyCenterActivity;
import com.test.activity.NotifyActivity;
import com.test.activity.PropertyActivity;
import com.test.activity.RegisterActivity;
import com.test.activity.SettingActivity;
import com.test.courier.R;
import com.test.sqlite.UserinfoDBUtil;

/**
 * Created by Administrator on 2019/12/10.
 */

//--------------我的---------------
public class Fragment_my extends Fragment implements View.OnClickListener{
    private View view;
    public Button button;//登陆
    public Button button1;//注册
    public ImageView fankui;//反馈
    public ImageView xiaoxi;//我的消息
    public ImageView tongzhi;//系统通知
    private TextView textView;//上班
    private TextView textView1;//下班
    public ImageView zhongxin;//个人中心
    public ImageView sezhi;//设置
    private ImageView zichan;//我的资产
    private ImageView tuichu;//退出
    private TextView name_text;//名字
    private TextView leftmoney_text;//余额
    private RoundedImageView head_img;//头像
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my,container,false);
        init();//初始化
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        UserinfoDBUtil userinfoDBUtil = new UserinfoDBUtil();//userinfo数据库工具类
        SQLiteDatabase database = userinfoDBUtil.getSqLiteDatabase(getActivity());//获取userinfo数据库
        //查询当前登录用户的userid
        Cursor cursor = database.query("userinfo",null,null,null,null,null,null);
        cursor.moveToFirst();
        String userid = cursor.getString(1);
        if (!"0".equals(userid)){//有用户登录
            cursor = database.query("userinfo",null,"userid = ?",new String[]{userid},null,null,null);
            cursor.moveToFirst();
            cursor.moveToNext();
            String name = cursor.getString(4);//当前用户名字
            String status = cursor.getString(5);//当前用户上下班状态，0上班  1下班
            String leftmoney = cursor.getString(6);//当前账户余额
            String imagepath = cursor.getString(7);//头像路径
            name_text.setText(name);
            leftmoney_text.setText(leftmoney);
            if (status.equals("0")){//0上班  1下班
                textView.setBackgroundResource(R.drawable.bg);
                textView1.setBackgroundResource(R.drawable.bg1);
            }else {
                textView.setBackgroundResource(R.drawable.bg1);
                textView1.setBackgroundResource(R.drawable.bg);
            }

        }

        database.close();
        cursor.close();
    }

    private void init() {//初始化
        button=view.findViewById(R.id.button);//获取【上班】控件
        button1=view.findViewById(R.id.button1);//获取【下班】控件
        fankui=view.findViewById(R.id.fankui);//获取【反馈】控件
        xiaoxi=view.findViewById(R.id.xiaoxi);//获取【我的消息】控件
        tongzhi=view.findViewById(R.id.tongzhi);//获取【通知】控件
        textView=view.findViewById(R.id.shanban);//获取【上班】控件
        textView1=view.findViewById(R.id.xiaban);//获取【下班】控件
        zhongxin=view.findViewById(R.id.zhongxin);//获取【个人中心】控件
        sezhi=view.findViewById(R.id.shezhi);//获取【设置】控件
        zichan = view.findViewById(R.id.zichan);//获取【我的资产】控件
        tuichu=view.findViewById(R.id.tuichu);//获取【退出登陆】控件
        name_text = view.findViewById(R.id.name);//名字
        leftmoney_text = view.findViewById(R.id.leftmoney_text);//余额
        head_img = view.findViewById(R.id.head_img);//头像

        button.setOnClickListener(this);
        button1.setOnClickListener(this);
        fankui.setOnClickListener(this);
        xiaoxi.setOnClickListener(this);
        tongzhi.setOnClickListener(this);
        textView.setOnClickListener(this);
        textView1.setOnClickListener(this);
        zhongxin.setOnClickListener(this);
        sezhi.setOnClickListener(this);
        zichan.setOnClickListener(this);
        tuichu.setOnClickListener(this);
        head_img.setOnClickListener(this);
        name_text.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String userid = null;
        Intent intent;
        switch (v.getId()){
            case R.id.head_img://头像
            case R.id.name://昵称
            case R.id.zhongxin://个人中心
                userid = getUserid();//获取当前用户userid
                if (userid.equals("0")){//无用户登录时，跳转登录
                    intent = new Intent(getActivity(),LoginActivity.class);
                    startActivity(intent);
                }else {//有用户登录时，跳转个人中心
                    intent = new Intent(getActivity(),MyCenterActivity.class);
                    startActivity(intent);
                }
                break;
            //登陆
            case R.id.button:
                intent = new Intent(getActivity(),LoginActivity.class);
                startActivity(intent);
                break;
                //注册
            case R.id.button1:
                intent = new Intent(getActivity(),RegisterActivity.class);
                startActivity(intent);
                break;
                //意见反馈
            case R.id.fankui:
                userid = getUserid();//获取当前用户userid
                if (userid.equals("0")){//无用户登录时，跳转登录
                    intent = new Intent(getActivity(),LoginActivity.class);
                    startActivity(intent);
                }else {//有用户登录时，跳转建议反馈
                    intent = new Intent(getActivity(),FeedbackActivity.class);
                    startActivity(intent);
                }
                break;
                //我的消息
            case R.id.xiaoxi:
                userid = getUserid();//获取当前用户userid
                if (userid.equals("0")){//无用户登录时，跳转登录
                    intent = new Intent(getActivity(),LoginActivity.class);
                    startActivity(intent);
                }else {//有用户登录时，跳转我的消息
                    intent = new Intent(getActivity(),MessageActivity.class);
                    startActivity(intent);
                }
                break;
                //系统通知
            case R.id.tongzhi:
                userid = getUserid();//获取当前用户userid
                if (userid.equals("0")){//无用户登录时，跳转登录
                    intent = new Intent(getActivity(),LoginActivity.class);
                    startActivity(intent);
                }else {//有用户登录时，跳转我的消息
                    intent = new Intent(getActivity(),NotifyActivity.class);
                    startActivity(intent);
                }
                break;
                //设置
            case R.id.shezhi:
                intent = new Intent(getActivity(),SettingActivity.class);
                startActivity(intent);
                break;
                //我的资产
            case R.id.zichan:
                userid = getUserid();//获取当前用户userid
                if (userid.equals("0")){//无用户登录时，跳转登录
                    intent = new Intent(getActivity(),LoginActivity.class);
                    startActivity(intent);
                }else {//有用户登录时，跳转我的资产
                    intent = new Intent(getActivity(), PropertyActivity.class);
                    startActivity(intent);
                }
                break;
                //退出登陆
            case R.id.tuichu:
                quit();//退出登录
                break;
                //上班
            case R.id.shanban:
                textView.setBackgroundResource(R.drawable.bg);
                textView1.setBackgroundResource(R.drawable.bg1);
                break;
                //下班
            case R.id.xiaban:
                textView.setBackgroundResource(R.drawable.bg1);
                textView1.setBackgroundResource(R.drawable.bg);
                break;
        }
    }

    private void quit() {//退出登录
        UserinfoDBUtil userinfoDBUtil = new UserinfoDBUtil();
        SQLiteDatabase database = userinfoDBUtil.getSqLiteDatabase(getActivity());
        //查询当前登录用户的userid
        Cursor cursor = database.query("userinfo",null,null,null,null,null,null);
        cursor.moveToFirst();
        if (!cursor.getString(1).equals("0")){
            String sql = "update userinfo set userid = 0 where _id = 1";
            database.execSQL(sql);
            name_text.setText("点击登录");
            leftmoney_text.setText("0.00");
            Toast.makeText(getActivity(), "退出成功", Toast.LENGTH_SHORT).show();
        }
        database.close();
        cursor.close();
    }

    private String getUserid(){
        UserinfoDBUtil userinfoDBUtil = new UserinfoDBUtil();//userinfo数据库工具类
        SQLiteDatabase database = userinfoDBUtil.getSqLiteDatabase(getActivity());//获取userinfo数据库
        //查询当前登录用户的userid
        Cursor cursor = database.query("userinfo",null,null,null,null,null,null);
        cursor.moveToFirst();
        String userid = cursor.getString(1);
        return userid;
    }
}
