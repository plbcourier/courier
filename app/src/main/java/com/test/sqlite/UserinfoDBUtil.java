package com.test.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Administrator on 2019/12/12.
 */

//-----------UserInfo数据库管理类，通过get方法获取userinfo数据库
public class UserinfoDBUtil {
    private UserinfoHelper userinfoHelper;
    private SQLiteDatabase sqLiteDatabase;
    public SQLiteDatabase getSqLiteDatabase(Context context){
        userinfoHelper = new UserinfoHelper(context,"userinfo",null,1);
        sqLiteDatabase = userinfoHelper.getWritableDatabase();
        return sqLiteDatabase;
    }
}
