package com.test.jpush;

import android.app.Application;
import android.util.Log;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2019/12/31.
 */

public class JPushApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        JPushInterface.setDebugMode(true);
        JPushInterface.init(getApplicationContext());
        String registrationId = JPushInterface.getRegistrationID(getApplicationContext());
        Log.e("tag",registrationId);
    }
}
