package com.test.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.test.courier.R;
import com.test.entity.Constant;
import com.test.sqlite.UserinfoDBUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

//-----------实名认证-----------
public class VerifyActivity extends AppCompatActivity implements View.OnClickListener{
    private Constant constant;
    private ImageView view;//身份证正面
    private ImageView view1;//身份证反面
    private Button button;//上传按钮
    private Uri imageUri;//图片存放地址
    public static final int TAKE_PHOTO=11;//拍照
    public static final int CROP_PHOTO=12;//裁剪图片
    public static final int LOCAL_CROP=13;//本地图库
    private File takePhotoImage;//图片存放地址
    private int select;
    private ImageView back_img;

    private Bitmap zhengmian;//身份证正面
    private Bitmap fanmian;//身份证反面

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        //取消严格模式  FileProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy( builder.build() );
        }
        view=findViewById(R.id.view);//正面
        view1=findViewById(R.id.view1);//反面
        button=findViewById(R.id.button);//上传
        constant = new Constant();
        back_img = findViewById(R.id.back_img);
        back_img.setOnClickListener(this);


        view.setOnClickListener(this);
        view1.setOnClickListener(this);
        button.setOnClickListener(this);//上传按钮点击事件

        checkStatus();//检查认证状态
    }

    private void checkStatus() {//检查认证状态
        CheckStatusTask checkStatusTask = new CheckStatusTask();
        checkStatusTask.execute();
    }

    private class CheckStatusTask extends AsyncTask<Void,Void,String>{//检查认证状态线程

        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new FormBody.Builder()
                    .add("deliveryId",getUserid())
                    .build();
            Request request = new Request.Builder()
                    .url(constant.PREFIX+constant.CARDSTATUS)
                    .method("POST",requestBody)
                    .build();
            Response response = null;
            String jsonstr = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response.isSuccessful()){
                try {
                    jsonstr = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return jsonstr;
        }

        @Override
        protected void onPostExecute(String jsonstr) {
            super.onPostExecute(jsonstr);
            //0未审核1审核中2审核通过3审核不通过
            if ("0".equals(jsonstr)){
                Toast.makeText(VerifyActivity.this, "您还未实名认证", Toast.LENGTH_SHORT).show();
            }else if ("1".equals(jsonstr)){
                Toast.makeText(VerifyActivity.this, "您的认证信息审核中", Toast.LENGTH_SHORT).show();
            }else if ("2".equals(jsonstr)){
                Toast.makeText(VerifyActivity.this, "您的认证信息已通过", Toast.LENGTH_SHORT).show();
            }else if ("3".equals(jsonstr)){
                Toast.makeText(VerifyActivity.this, "您的认证信息未通过", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(VerifyActivity.this, "认证信息查询失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_img:
                finish();
                break;
            case R.id.view:
                /*Environment.getExternalStorageDirectory()特殊的文件，应用删除，该文件也不会删除*/
                takePhotoImage=new File(Environment.getExternalStorageDirectory(),"take_photo_image.jpg");
                select=0;
                takePhotoOrSelecctPicture();

                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                    final String cameraPermission = Manifest.permission.CAMERA;//相机权限
                    final String readStoragePermission = Manifest.permission.READ_EXTERNAL_STORAGE;//读取外部存储权限
                    final String writeStoragePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;//写入外部存储权限

                    //检查权限，没有则申请授权
                    if (ContextCompat.checkSelfPermission(VerifyActivity.this,cameraPermission)!=PackageManager.PERMISSION_GRANTED
                            ||ContextCompat.checkSelfPermission(VerifyActivity.this,writeStoragePermission)!=PackageManager.PERMISSION_GRANTED
                            ||ContextCompat.checkSelfPermission(VerifyActivity.this,readStoragePermission)!=PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(VerifyActivity.this,
                                new String[]{cameraPermission,writeStoragePermission,readStoragePermission},300);
                    }
                }

                break;
            case R.id.view1:
                /*Environment.getExternalStorageDirectory()特殊的文件，应用删除，该文件也不会删除*/
                takePhotoImage=new File(Environment.getExternalStorageDirectory(),"take_photo_image1.jpg");
                select=1;
                takePhotoOrSelecctPicture();

                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                    final String cameraPermission = Manifest.permission.CAMERA;//相机权限
                    final String readStoragePermission = Manifest.permission.READ_EXTERNAL_STORAGE;//读取外部存储权限
                    final String writeStoragePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;//写入外部存储权限

                    //检查权限，没有则申请授权
                    if (ContextCompat.checkSelfPermission(VerifyActivity.this,cameraPermission)!=PackageManager.PERMISSION_GRANTED
                            ||ContextCompat.checkSelfPermission(VerifyActivity.this,writeStoragePermission)!=PackageManager.PERMISSION_GRANTED
                            ||ContextCompat.checkSelfPermission(VerifyActivity.this,readStoragePermission)!=PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(VerifyActivity.this,
                                new String[]{cameraPermission,writeStoragePermission,readStoragePermission},300);
                    }
                }

                break;
            case R.id.button:
                view.setDrawingCacheEnabled(true);
                zhengmian=((BitmapDrawable)view.getDrawable()).getBitmap();//获取身份证正面图片
                view.setDrawingCacheEnabled(false);

                view1.setDrawingCacheEnabled(true);
                fanmian=((BitmapDrawable)view1.getDrawable()).getBitmap();//获取身份证反面图片
                view1.setDrawingCacheEnabled(false);

                File image=getFile(zhengmian);
                File image1=getFile(fanmian);
                UploadIDcard uploadIDcard=new UploadIDcard();
                uploadIDcard.execute(image,image1);
                break;

        }
    }
    //选择是照相机还是图库
    private void takePhotoOrSelecctPicture(){
        CharSequence[] items={"拍照","图库"};//裁剪items选项

        //弹出对话框提示用户拍照或者通过本地图库选择图片
        new AlertDialog.Builder(VerifyActivity.this)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                                    final String cameraPermission = Manifest.permission.CAMERA;//相机权限
                                    final String readStoragePermission = Manifest.permission.READ_EXTERNAL_STORAGE;//读取外部存储权限
                                    final String writeStoragePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;//写入外部存储权限

                                    //检查权限，没有则申请授权
                                    if (ContextCompat.checkSelfPermission(VerifyActivity.this,cameraPermission)!=PackageManager.PERMISSION_GRANTED
                                            ||ContextCompat.checkSelfPermission(VerifyActivity.this,writeStoragePermission)!=PackageManager.PERMISSION_GRANTED
                                            ||ContextCompat.checkSelfPermission(VerifyActivity.this,readStoragePermission)!=PackageManager.PERMISSION_GRANTED){
                                        ActivityCompat.requestPermissions(VerifyActivity.this,
                                                new String[]{cameraPermission,writeStoragePermission,readStoragePermission},300);
                                    }

                                    if (ContextCompat.checkSelfPermission(VerifyActivity.this,cameraPermission)==PackageManager.PERMISSION_GRANTED
                                            &&ContextCompat.checkSelfPermission(VerifyActivity.this,readStoragePermission)==PackageManager.PERMISSION_GRANTED
                                            &&ContextCompat.checkSelfPermission(VerifyActivity.this,writeStoragePermission)==PackageManager.PERMISSION_GRANTED){
                                        try {
                                            //文件存在，删除文件
                                            if (takePhotoImage.exists()){
                                                takePhotoImage.delete();
                                            }
                                            //根据路径名自动创建一个新的空文件
                                            takePhotoImage.createNewFile();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        //获取图片文件的uri对象
                                        imageUri =Uri.fromFile(takePhotoImage);

                                        //创建一个Intent用于启动手机的照相拍照功能
                                        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        //指定输出到文件uri中
                                        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                                        //启动intent开始拍照
                                        startActivityForResult(intent,TAKE_PHOTO);
                                    }
                                }
                                break;
                            case 1:
                                /*创建intent用于打开手机本地图库选择图片*/
                                Intent intent1=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(intent1,LOCAL_CROP);
                                break;
                        }
                    }
                }).show();
    }
    /*
    * 调用startaActivityForResult方法启动一个intent后，可以用在该方法中拿到返回的数据
    * */
    //用于裁剪
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            //相机没有用到裁剪
            case TAKE_PHOTO://拍照
                if (resultCode == RESULT_OK) {
                    try {
                        // 展示拍照后裁剪的图片
                        if (imageUri != null) {
                            // 创建BitmapFactory.Options对象
                            BitmapFactory.Options option = new BitmapFactory.Options();
                            // 属性设置，用于压缩bitmap对象
                            option.inSampleSize = 2;
                            option.inPreferredConfig = Bitmap.Config.RGB_565;
                            // 根据文件流解析生成Bitmap对象
                            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri), null, option);
                            // 展示图片
                            if (select==0){
                                view.setImageBitmap(bitmap);
                            }else {
                                view1.setImageBitmap(bitmap);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case LOCAL_CROP:// 系统图库

                if (resultCode == RESULT_OK) {
                    // 创建intent用于裁剪图片
                    Intent intent1 = new Intent("com.android.camera.action.CROP");
                    // 获取图库所选图片的uri
                    Uri uri = data.getData();
                    intent1.setDataAndType(uri, "image/*");
                    //  设置裁剪图片的宽高
                    intent1.putExtra("outputX", 300);
                    intent1.putExtra("outputY", 300);
                    // 裁剪后返回数据
                    intent1.putExtra("return-data", true);
                    // 启动intent，开始裁剪
                    startActivityForResult(intent1, CROP_PHOTO);
                    //Toast.makeText(VerifyActivity.this, "" + requestCode, Toast.LENGTH_SHORT).show();
                }
                break;
            case CROP_PHOTO:// 裁剪后展示图片
                if (resultCode == RESULT_OK) {
                    try {
                        // 展示拍照后裁剪的图片
                        if (imageUri != null) {
                            // 创建BitmapFactory.Options对象
                            BitmapFactory.Options option = new BitmapFactory.Options();
                            // 属性设置，用于压缩bitmap对象
                            option.inSampleSize = 2;
                            option.inPreferredConfig = Bitmap.Config.RGB_565;
                            // 根据文件流解析生成Bitmap对象
                            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri), null, option);
                            // 展示图片
                            if (select==0){
                                view.setImageBitmap(bitmap);
                            }else {
                                view1.setImageBitmap(bitmap);
                            }
                        }

                        // 展示图库中选择裁剪后的图片
                        if (data != null) {
                            // 根据返回的data，获取Bitmap对象
                            Bitmap bitmap = data.getExtras().getParcelable("data");
                            // 展示图片
                            if (select==0){
                                view.setImageBitmap(bitmap);
                            }else {
                                view1.setImageBitmap(bitmap);
                            }

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }
    //用于获取
    private String getUserid(){//获取当前登录用户id
        UserinfoDBUtil userinfoDBUtil = new UserinfoDBUtil();//userinfo数据库工具类
        SQLiteDatabase database = userinfoDBUtil.getSqLiteDatabase(VerifyActivity.this);//获取userinfo数据库
        //查询当前登录用户的userid
        Cursor cursor = database.query("userinfo",null,null,null,null,null,null);
        cursor.moveToFirst();
        String userid = cursor.getString(1);
        return userid;
    }
    //将获取的图片转化成file
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

    //AsyncTask三个参数，第一个File是执行前调用时返回值，Void则是中途返回值，String则是结束后返回值
    public class UploadIDcard extends AsyncTask<File,Void,String>{

        @Override
        protected String doInBackground(File... files) {
            //获取具体的值
            String userid = getUserid();//骑手id的值
            OkHttpClient client=new OkHttpClient();
            MultipartBody.Builder multipartBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
            if (files[0]!=null && files[1]!=null){
                // MediaType.parse() 里面是上传的文件类型。
                RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"),files[0]);
                RequestBody requestBody1 = RequestBody.create(MediaType.parse("image/*"),files[1]);
                //获取文件名称
                String filename = files[0].getName();
                String filename1 = files[1].getName();
                // 参数分别为， 请求key ，文件名称 ， RequestBody
                multipartBody
                        .addFormDataPart("id",userid)
                        .addFormDataPart("files",filename,requestBody)
                        .addFormDataPart("files",filename1,requestBody1);

            }
            Request request = new Request.Builder()
                    .url(constant.PREFIX+constant.UPLOADCARD)
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
                                    Toast.makeText(VerifyActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(VerifyActivity.this, "上传中，请稍后查看", Toast.LENGTH_SHORT).show();
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
    }

}
