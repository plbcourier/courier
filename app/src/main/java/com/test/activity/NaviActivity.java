package com.test.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRoutePlanOption;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.test.courier.DrivingRouteOverlay;
import com.test.courier.GPS_Presenter;
import com.test.courier.MyOrientationListener;
import com.test.courier.R;

import java.util.ArrayList;
//开始导航页面
public class NaviActivity extends AppCompatActivity implements View.OnClickListener,GPS_Presenter.GPS_Interface {
    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;
    private Context context;
    private ImageView back_img;
    //定位相关
    private double mLatitude;
    private double mLongtitude;
    //方向传感器
    private MyOrientationListener mMyOrientationListener;
    private float mCurrentX;
    //自定义图标
    private BitmapDescriptor mIconLocation;
    private LocationClient mLocationClient;
    public BDAbstractLocationListener myListener;
    private LatLng mLastLocationData;
    private MyLocationConfiguration.LocationMode mCurrentMode;//定义当前定位模式
    private boolean isFirstin = true;
    private String type_nav;//类型
    private String marketName_nai;//取货点
    private String address_nav;//收货点
    // 路线规划相关
    private RoutePlanSearch mSearch = null;

    private Button btnDrive;
    private EditText start_edt_city,start_edt_address,end_edt_city,end_edt_address;

    private GPS_Presenter gps_presenter ;
    private ImageView dingwei_img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_navi);
        gps_presenter = new GPS_Presenter( this,this) ;
        this.context = this;
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.baiduMapView);
        mBaiduMap = mMapView.getMap();
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        type_nav=bundle.getString("type");
        marketName_nai=bundle.getString("marketName");
        address_nav=bundle.getString("address");
        initView();
        initMyLocation();
        initPoutePlan();
        ActionBar actionBar=getSupportActionBar();
        actionBar.hide();
        mHandler.postDelayed(mRunnable,1000);
        /*end_edt_city.setText("衡阳市");*/
/*        start_edt_address.setText("湖南省衡阳市珠晖区衡阳火车站");*/
/*        end_edt_address.setText(*//*bundle.getString("address")*//*"三泰市场");*/
    }
    //延迟1秒定位和规划路线
    private Runnable mRunnable=new Runnable() {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(1);
        }
    };

    private void initMyLocation() {
        //缩放地图
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap.setMapStatus(msu);
        //开启定位
        mBaiduMap.setMyLocationEnabled(true);
        //声明LocationClient类
        mLocationClient = new LocationClient(getApplication());
        //通过LocationClientOption设置LocationClient相关参数
        LocationClientOption option = new LocationClientOption();
        //设置定位模式：高精度，低功耗，仅设备
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //设置坐标系
        option.setCoorType("bd09ll");
        option.setOpenGps(true); // 打开gps
        //设置需要地址信息
        option.setIsNeedAddress(true);
        //5秒更新一次我的定位
        option.setScanSpan(3000);
        //设置需要位置描述信息
        option.setIsNeedLocationDescribe(true);
        option.setIsNeedLocationPoiList(true);
        mLocationClient.setLocOption(option);
        mLocationClient.registerLocationListener(mBdLocationListener);
        myListener = new NaviActivity.MyLocationListener();
        //注册监听函数
        mLocationClient.registerLocationListener(myListener);
        //初始化图标
       /*mIconLocation = BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps);*/
        mCurrentMode =MyLocationConfiguration.LocationMode.NORMAL;//设置定位模式
        //设置构造方式,将定位模式,定义图标添加其中
        MyLocationConfiguration config=new MyLocationConfiguration(mCurrentMode,true,mIconLocation);
        mBaiduMap.setMyLocationConfiguration(config);
        initOrientation();
        //开始定位
        mLocationClient.start();
    }
    //回到定位中心
    private void centerToMyLocation(double latitude, double longtitude) {
        mBaiduMap.clear();
        mLastLocationData = new LatLng(latitude, longtitude);
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(mLastLocationData);
        mBaiduMap.animateMapStatus(msu);
    }
    //传感器
    private void initOrientation() {
        //传感器
        mMyOrientationListener = new MyOrientationListener(context);
        mMyOrientationListener.setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
            @Override
            public void onOrientationChanged(float x) {
                mCurrentX = x;
            }
        });
    }
    //路线规划初始化
    private void initPoutePlan() {
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(listener);
    }
    // 路线规划模块
    public OnGetRoutePlanResultListener listener = new OnGetRoutePlanResultListener() {
        @Override
        public void onGetWalkingRouteResult(WalkingRouteResult result) {
        }
        @Override
        public void onGetTransitRouteResult(TransitRouteResult result) {
        }
        @Override
        public void onGetMassTransitRouteResult(MassTransitRouteResult result) {
        }
        @Override
        public void onGetDrivingRouteResult(DrivingRouteResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(NaviActivity.this, "路线规划:未找到结果\nGPS是否打开!\n地址是否正确!", Toast.LENGTH_SHORT).show();
                //禁止定位
                isFirstin = false;
            }
            assert result != null;
            if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                result.getSuggestAddrInfo();
                return;
            }
            if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                mBaiduMap.clear();
                Toast.makeText(NaviActivity.this, "路线规划:搜索完成", Toast.LENGTH_LONG).show();
                DrivingRouteOverlay overlay = new DrivingRouteOverlay(mBaiduMap);

                overlay.setData(result.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();
            }
            //禁止定位
            isFirstin = false;
        }
        @Override
        public void onGetIndoorRouteResult(IndoorRouteResult var1) {
        }
        @Override
        public void onGetBikingRouteResult(BikingRouteResult result) {
        }
    };
    private void initView() {
        btnDrive = (Button)findViewById(R.id.btn_drive);
        dingwei_img=findViewById(R.id.dingwei_img);
        start_edt_city = (EditText)findViewById(R.id.Start_Edt_City);
        start_edt_address = (EditText)findViewById(R.id.Start_Edt_Address);
        end_edt_city = (EditText)findViewById(R.id.End_Edt_City);
        end_edt_address = (EditText)findViewById(R.id.End_Edt_Address);
        back_img=findViewById(R.id.back_img_nav);
        back_img.setOnClickListener(this);
        btnDrive.setOnClickListener(this);
        dingwei_img.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String startCity = start_edt_city.getText().toString();
        String startAddress = start_edt_address.getText().toString();
        String endCity = end_edt_city.getText().toString();
        String endAddress = end_edt_address.getText().toString();
        switch (view.getId()){
            case R.id.btn_drive:
                // 设置起、终点信息 动态输入规划路线
                LatLng startPoint = new LatLng(mLatitude, mLongtitude);//当前坐标(经纬度)
                /*PlanNode stNode = PlanNode.withCityNameAndPlaceName(startCity,startAddress);*/
                PlanNode enNode = PlanNode.withCityNameAndPlaceName(String.valueOf(endCity),String.valueOf(endAddress));//字符串
              /*  //经纬度规划路线
                LatLng startPoint = new LatLng(mLatitude, mLongtitude);//当前坐标
                LatLng endPoint = new LatLng(28.213478, 112.979353);//长沙
                PlanNode stNode = PlanNode.withLocation(startPoint);
                PlanNode enNode = PlanNode.withLocation(endPoint);*/
                mSearch.drivingSearch((new DrivingRoutePlanOption()).from(PlanNode.withLocation(startPoint)).to(enNode));
                break;
            case R.id.back_img_nav:
                finish();
                break;
                default:
                    break;
            case R.id.dingwei_img:
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(18));
                //自己当前的位置
                MapStatusUpdate mu=MapStatusUpdateFactory.newLatLng(mLastLocationData);
                mBaiduMap.animateMapStatus(mu);
                break;
        }
    }
    //定位
    private class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //mapView 销毁后不在处理新接收的位置
            if (location == null || mMapView == null){
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())//设置精度
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentX)
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            //设置自定义图标
            MyLocationConfiguration config = new
                    MyLocationConfiguration(
                    MyLocationConfiguration.LocationMode.NORMAL, true, mIconLocation);
            mBaiduMap.setMyLocationConfiguration(config);
            //更新经纬度
            mLatitude = location.getLatitude();
            mLongtitude = location.getLongitude();
            start_edt_city.setText(location.getCity());
            start_edt_address.setText(location.getAddrStr());
            if (type_nav.equals("qu")){
                end_edt_address.setText(marketName_nai+"");
            }else{
                end_edt_address.setText(address_nav+"");
            }
            end_edt_city.setText(location.getCity());
            //设置起点
            mLastLocationData = new LatLng(mLatitude, mLongtitude);
            if (isFirstin) {
                centerToMyLocation(location.getLatitude(), location.getLongitude());

                if (location.getLocType() == BDLocation.TypeGpsLocation) {
                    // GPS定位结果
                    Toast.makeText(context, "定位:"+location.getAddrStr(), Toast.LENGTH_LONG).show();
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                    // 网络定位结果
                    Toast.makeText(context, "定位:"+location.getAddrStr(), Toast.LENGTH_SHORT).show();
                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {
                    // 离线定位结果
                    Toast.makeText(context, "定位:"+location.getAddrStr(), Toast.LENGTH_SHORT).show();
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    Toast.makeText(context, "定位:服务器错误", Toast.LENGTH_SHORT).show();
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    Toast.makeText(context, "定位:网络错误", Toast.LENGTH_SHORT).show();
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    Toast.makeText(context, "定位:手机模式错误，请检查是否飞行", Toast.LENGTH_SHORT).show();
                }
                isFirstin = false;
            }
        }
    }
    private BDAbstractLocationListener mBdLocationListener = new BDAbstractLocationListener() {
        //当位置对象为空或者因为退出而到时地图对象销毁为空时，不监听
        public void onReceiveLocation(BDLocation location) {
            if (location == null || mMapView == null)
                return;
            Log.v("aaa","方向：" + location.getDirection() + ",纬度：" + location.getLatitude()
                    + "，经度：" + location.getLongitude()
                    + ",时间：" + location.getTime() + ",描述："
                    + location.getLocationDescribe() + ",地址：" + location.getAddrStr() + ",精度：" + location.getRadius()+",城市:"+location.getCity()+",城市码:"+location.getCityCode()+",城市区县信息:"+location.getDistrict()+",周边信息:"+location.getPoiList()+",楼宇ID:"+location.getBuildingID()+",楼宇名称:"+location.getBuildingName()+",楼层信息:"+location.getFloor());
/*            Toast.makeText(MainActivity.this,"方向："+location.getDirection()+",纬度："+location.getLatitude()+",经度："+location.getLongitude()+",时间:"+location.getTime()+",描述:"+location.getAddrStr()+",精度:"+location.getRadius()+",城市:"+location.getCity()+",城市码:"+location.getCityCode()+",城市区县信息:"+location.getDistrict()+",周边信息:"+location.getPoiList()+",楼宇ID:"+location.getBuildingID()+",楼宇名称:"+location.getBuildingName()+",楼层信息:"+location.getFloor(),Toast.LENGTH_LONG).show();*/
        }
    };
    protected void onStart() {
        super.onStart();
        //开启定位
        mBaiduMap.setMyLocationEnabled(true);
        if (!mLocationClient.isStarted())
            mLocationClient.start();
        //开启方向传感器
        mMyOrientationListener.start();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
        mSearch.destroy();
    }
    @Override
    protected void onStop() {
        super.onStop();
        //停止定位
        mBaiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();
        //停止方向传感器
        mMyOrientationListener.stop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        mSearch.destroy();
        if (gps_presenter!=null){
            gps_presenter.onDestroy();
        }
    }
    @Override
    public void gpsSwitchState(boolean gpsOpen) {
            if (gpsOpen){
                View view= LayoutInflater.from(NaviActivity.this).inflate(R.layout.tost_layout,null,false);
                TextView tv=(TextView)view.findViewById(R.id.text_1);
                tv.setText("手机GPS已打开,可以进行定位功能!");
                Toast toast=new Toast(NaviActivity.this);
                toast.setView(view);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.show();
                /*Toast.makeText(this, "手机GPS已打开,可以进行定位功能!", Toast.LENGTH_LONG).show();*/
            }else{
                View view= LayoutInflater.from(NaviActivity.this).inflate(R.layout.tost_layout2,null,false);
                TextView tv=(TextView)view.findViewById(R.id.text_2);
                tv.setText("手机GPS已关闭,不能进行定位功能,请手动打开!");
                Toast toast=new Toast(NaviActivity.this);
                toast.setView(view);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.show();
         /*       Toast.makeText(this, " 手机GPS已关闭,不能进行定位功能,请手动打开!", Toast.LENGTH_LONG).show();*/
            }
    }
    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mLocationClient.start();
            btnDrive.performClick();
        }
    };
}
