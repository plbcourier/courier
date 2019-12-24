package com.test.courier;

import android.content.Context;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.test.entity.Coords;

/**
 * Created by Administrator on 2019/12/24.
 */

//------------获取当前坐标工具类--------------
public class CoordsUtil {
    private LocationClient locationClient;//百度地图定位
    private MyLocationListener myLocationListener;//地图数据监听
    private Coords coords;//坐标实体类

    private void getLongitude(Context context){
        myLocationListener = new MyLocationListener();
        locationClient = new LocationClient(context);
        locationClient.registerLocationListener(myLocationListener);//注册监听
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//高精度模式
        option.setCoorType("BD09ll");//百度经纬度坐标
        option.setScanSpan(0);//单次定位
        option.setOpenGps(true);//使用GPS
        option.setIsNeedAddress(true);//获取地址
        locationClient.setLocOption(option);
        locationClient.start();
    }


    private class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明
            Double longitude = bdLocation.getLongitude();//获取经度信息
            Double latitude = bdLocation.getLatitude();//获取纬度信息
            String address = bdLocation.getAddrStr();//获取定位的地址字符串

            coords = new Coords();
            coords.setLongitude(longitude);
            coords.setLatitude(latitude);
            coords.setAddress(address);
        }

    }
}
