package com.test.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoActivity;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.TImage;
import com.jph.takephoto.model.TResult;
import com.test.courier.R;
import com.test.fragment.Fragment_my;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

//-------------个人中心-----------------
public class MyCenterActivity extends TakePhotoActivity implements View.OnClickListener {
    ImageView back,touxiang;
    private LinearLayout line1,line2,line3;
    TextView UserName,Birthday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_center);
        init();
    }

    private void init() {
        back = findViewById(R.id.back_img);
        touxiang = findViewById(R.id.head_portrait);
        line1 = findViewById(R.id.line1);
        line2 = findViewById(R.id.line2);
        line3 = findViewById(R.id.line3);
        UserName = findViewById(R.id.username);
        Birthday = findViewById(R.id.birthday);

        line1.setOnClickListener(this);
        line2.setOnClickListener(this);
        line3.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()){
            case R.id.back_img:
                finish();
                break;
            case R.id.line1:    //修改头像
                final String[] gender = new String[]{"从相册中选择","拍照"};   //弹框弹出选项
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MyCenterActivity.this);
                builder1.setTitle("请选择您要修改头像的方式");   //弹框的标题
                builder1.setItems(gender, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {   //弹框里各选项添加点击事件
//                        Toast.makeText(MyCenterActivity.this,""+i , Toast.LENGTH_SHORT).show();

                        TakePhoto takePhoto = getTakePhoto();   //第三方开源库takephoto的使用
                        CropOptions.Builder builder = new CropOptions.Builder();
                        builder.setAspectX(800).setAspectY(800);
                        builder.setWithOwnCrop(true);
                        File file = new File(Environment.getExternalStorageDirectory(),
                                "/temp/" + System.currentTimeMillis() + ".jpg");
                        if (!file.getParentFile().exists()) {
                            boolean mkdirs = file.getParentFile().mkdirs();
                            if (!mkdirs) {
                                Toast.makeText(MyCenterActivity.this, "文件目录创建失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                        Uri imageUri = Uri.fromFile(file);
                        CompressConfig config = new CompressConfig.Builder()
                                .setMaxSize(102400)    //压缩图片格式的最大尺寸
                                .setMaxPixel(400)       //压缩图片格式的最小尺寸
                                .enableReserveRaw(true)
                                .create();
                        takePhoto.onEnableCompress(config, true);

                        if (i == 0 ){
                            takePhoto.onPickFromDocumentsWithCrop(imageUri, builder.create());  //从相册获取照片
                        }else {
                            takePhoto.onPickFromCaptureWithCrop(imageUri, builder.create());   //拍照获取照片
                        }
                    }
                });
                builder1.show();
                break;

            case R.id.line2:      //修改用户名
                AlertDialog.Builder builder = new AlertDialog.Builder(MyCenterActivity.this);   //弹框修改用户名
                builder.setTitle("请输入新的用户名");
                final EditText et = new EditText(MyCenterActivity.this);
                et.setHint("请输入用户名");
                et.setSingleLine(true);
                builder.setView(et);
                builder.setNegativeButton("取消",null);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {   //当用户点击确定时更改用户名
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Toast.makeText(MyCenterActivity.this, ""+i, Toast.LENGTH_SHORT).show();
                            String newName = et.getText().toString();    //获取输入框的值
                        if ("".equals(newName)){
                            Toast.makeText(MyCenterActivity.this, "输入的用户名不能为空", Toast.LENGTH_SHORT).show();
                            return;
                        }else {
                            UserName.setText(newName);
                        }
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                break;

            case R.id.line3:
                final Calendar mCalendar = Calendar.getInstance();
                DatePickerDialog pickerDialog = new DatePickerDialog(MyCenterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker arg0, int year, int month, int day) {
                        mCalendar.set(year, month, day);        //将点击获得的年月日获取到calendar中。
                        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");//转型
//                        Toast.makeText(getApplicationContext(), format.format(mCalendar.getTime()), Toast.LENGTH_LONG).show();
                        Birthday.setText(format.format(mCalendar.getTime()));
                    }
                }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
                pickerDialog.show();
        }
    }

    @Override
    public void takeSuccess(TResult result) {      //获取头像的图片并显示出来
        super.takeSuccess(result);
        TImage image = result.getImage();   //成功获取图片
        Bitmap bitmap = BitmapFactory.decodeFile(image.getOriginalPath());   //修改图片
        touxiang.setImageBitmap(bitmap);
    }
}
