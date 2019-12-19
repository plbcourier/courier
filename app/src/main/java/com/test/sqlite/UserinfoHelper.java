package com.test.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2019/12/12.
 */

//-------------用户信息表--------------
public class UserinfoHelper extends SQLiteOpenHelper {
    public UserinfoHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //_id 主键，自增
        //userid 用户id
        //phone 手机号
        //password 密码
        //name 骑手名字
        //status 状态，0上班，1下班
        //leftmoney 余额
        //imagepath 头像路径
        String sql = "create table userinfo(" +
                "_id integer primary key autoincrement," +
                "userid integer,"+
                "phone integer," +
                "password varchar," +
                "name varchar,"+
                "status integer," +
                "leftmoney varchar," +
                "imagepath varchar)";
        db.execSQL(sql);

        sql = "insert into userinfo values(null,0,null,null,null,null,null,null)";
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
