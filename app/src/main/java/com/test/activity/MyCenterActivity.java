package com.test.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.makeramen.roundedimageview.RoundedImageView;
import com.test.courier.R;
import com.test.entity.Constant;
import com.test.fragment.Fragment_my;
import com.test.sqlite.UserinfoDBUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

//-------------个人中心-----------------
public class MyCenterActivity extends TakePhotoActivity implements View.OnClickListener {
    private ImageView back;
    private RoundedImageView touxiang;
    private LinearLayout line1,line2,line3;
    private TextView username_text,Birthday;
    private Constant constant;//常量类

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_center);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    private void refreshData() {//刷新头像和昵称
        UserinfoDBUtil userinfoDBUtil = new UserinfoDBUtil();//userinfo数据库工具类
        SQLiteDatabase database = userinfoDBUtil.getSqLiteDatabase(MyCenterActivity.this);//获取userinfo数据库
        //查询当前登录用户的userid
        Cursor cursor = database.query("userinfo",null,null,null,null,null,null);
        cursor.moveToFirst();
        String userid = cursor.getString(1);
        cursor = database.query("userinfo",null,"userid = ?",new String[]{userid},null,null,null);
        cursor.moveToFirst();
        cursor.moveToNext();
        String name = cursor.getString(4);
        username_text.setText(name);
        String imagepath = cursor.getString(7);
        HeadImageTask headImageTask = new HeadImageTask(touxiang);
        headImageTask.execute(imagepath);

    }

    private void init() {
        back = findViewById(R.id.back_img);
        touxiang = findViewById(R.id.head_portrait);
        line1 = findViewById(R.id.line1);
        line2 = findViewById(R.id.line2);
        line3 = findViewById(R.id.line3);
        username_text = findViewById(R.id.username_text);
        Birthday = findViewById(R.id.birthday);
        constant = new Constant();

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
                builder1.setTitle("修改头像");   //弹框的标题
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
                        String newName = et.getText().toString();    //获取输入框的值
                        if ("".equals(newName)){
                            Toast.makeText(MyCenterActivity.this, "输入的用户名不能为空", Toast.LENGTH_SHORT).show();
                            return;
                        }else {
                            username_text.setText(newName);
                            ChangeNameTask changeNameTask = new ChangeNameTask();//开启修改昵称线程，传入新昵称
                            changeNameTask.execute(newName);
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

    private class HeadImageTask extends AsyncTask<String,Void,Bitmap> {//网络获取头像线程,传入值图片url，返回值Bitmap图片
        RoundedImageView roundedImageView;

        public HeadImageTask(RoundedImageView roundedImageView) {
            this.roundedImageView = roundedImageView;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            String picurl = strings[0];
            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder().url(picurl).build();
            Bitmap bitmap = null;
            try {
                ResponseBody responseBody = okHttpClient.newCall(request).execute().body();
                InputStream inputStream = responseBody.byteStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            roundedImageView.setImageBitmap(bitmap);
        }
    }

    private class ChangeNameTask extends AsyncTask<String,Void,String>{//更改昵称线程
        String newname = null;
        @Override
        protected String doInBackground(String... strings) {
            newname = strings[0];
            UserinfoDBUtil userinfoDBUtil = new UserinfoDBUtil();//userinfo数据库工具类
            SQLiteDatabase database = userinfoDBUtil.getSqLiteDatabase(MyCenterActivity.this);//获取userinfo数据库
            //查询当前登录用户的userid
            Cursor cursor = database.query("userinfo",null,null,null,null,null,null);
            cursor.moveToFirst();
            String userid = cursor.getString(1);
            cursor = database.query("userinfo",null,"userid = ?",new String[]{userid},null,null,null);
            cursor.moveToFirst();
            cursor.moveToNext();
            String phone = cursor.getString(2);

            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new FormBody.Builder()//传入请求参数，手机号、密码
                    .add("name",strings[0])
                    .add("phone",phone)
                    .build();
            Request request = new Request.Builder()//请求对象
                    .url(constant.PREFIX+constant.CHANGENAME)
                    .method("POST",requestBody)
                    .build();
            Response response = null;//响应对象
            String jsonstr = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response.isSuccessful()){//响应成功
                try {
                    jsonstr = response.body().string();//返回的json数据
                    return jsonstr;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return jsonstr;
        }

        @Override
        protected void onPostExecute(String jsonstr) {
            super.onPostExecute(jsonstr);
            if (jsonstr.indexOf("success")!=-1){//修改成功
                UserinfoDBUtil userinfoDBUtil = new UserinfoDBUtil();//userinfo数据库工具类
                SQLiteDatabase database = userinfoDBUtil.getSqLiteDatabase(MyCenterActivity.this);//获取userinfo数据库
                //查询当前登录用户的userid
                Cursor cursor = database.query("userinfo",null,null,null,null,null,null);
                cursor.moveToFirst();
                String userid = cursor.getString(1);
                String sql = "update userinfo set name = ? where userid = ?";
                database.execSQL(sql,new String[]{newname,userid});
                refreshData();
            }else {
                Toast.makeText(MyCenterActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void takeSuccess(TResult result) {//获取头像的图片并显示出来
        super.takeSuccess(result);
        TImage image = result.getImage();   //成功获取图片
        Bitmap bitmap = BitmapFactory.decodeFile(image.getOriginalPath());   //修改图片
        touxiang.setImageBitmap(bitmap);
        File imageFile = getFile(bitmap);
        UploadHeadImgTask uploadHeadImgTask = new UploadHeadImgTask();
        uploadHeadImgTask.execute(imageFile);
    }
    //AsyncTask中参数第一个File是开始调用返回值，Void则是中途返回值，String则是完成后返回值
    private class UploadHeadImgTask extends AsyncTask<File,Void,String>{//上传头像线程
       // doInBackground（）该方法运行在后台线程中。这里将主要负责执行那些很耗时的后台计算工作。可以调用 publishProgress方法来更新实时的任务进度。该方法是抽象方法，子类必须实现。
        @Override
        protected String doInBackground(File... files) {
            String userid = getUserid();
            OkHttpClient client = new OkHttpClient();
            MultipartBody.Builder multipartBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
            if (files[0]!=null){
                // MediaType.parse() 里面是上传的文件类型。
                RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"),files[0]);
                //获取文件名称
                String filename = files[0].getName();
                // 参数分别为， 请求key ，文件名称 ， RequestBody
                multipartBody.addFormDataPart("icon",filename,requestBody)
                        .addFormDataPart("id",userid);
            }
            final String[] jsonstr = {null};
            Request request = new Request.Builder()
                    .url(constant.PREFIX+constant.UPLOADICON)
                    .post(multipartBody.build())
                    .build();
            client.newBuilder().readTimeout(3000, TimeUnit.MILLISECONDS)
                    .build()
                    .newCall(request)
                    .enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MyCenterActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MyCenterActivity.this, "上传中，请稍后查看", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String jsonstr) {
            super.onPostExecute(jsonstr);
            refreshData();
        }
    }

    public File getFile(Bitmap bitmap) {//将图片转成file
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        File file = new File(Environment.getExternalStorageDirectory() + "/temp.jpg");
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            InputStream is = new ByteArrayInputStream(baos.toByteArray());
            int x = 0;
            byte[] b = new byte[1024 * 100];
            while ((x = is.read(b)) != -1) {
                fos.write(b, 0, x);
            }
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    private String getUserid(){//获取当前登录用户id
        UserinfoDBUtil userinfoDBUtil = new UserinfoDBUtil();//userinfo数据库工具类
        SQLiteDatabase database = userinfoDBUtil.getSqLiteDatabase(MyCenterActivity.this);//获取userinfo数据库
        //查询当前登录用户的userid
        Cursor cursor = database.query("userinfo",null,null,null,null,null,null);
        cursor.moveToFirst();
        String userid = cursor.getString(1);
        return userid;
    }

}
