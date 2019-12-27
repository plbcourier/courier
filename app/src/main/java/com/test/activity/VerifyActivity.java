package com.test.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class VerifyActivity extends AppCompatActivity implements View.OnClickListener{
    private Constant constant;
    private EditText editText;//骑手ID
    private ImageView view;//身份证正面
    private ImageView view1;//身份证反面
    private Button button;//上传按钮
    private Uri imageUri;//图片存放地址
    public static final int TAKE_PHOTO=11;//拍照
    public static final int CROP_PHOTO=12;//裁剪图片
    public static final int LOCAL_CROP=13;//本地图库
    private File takePhotoImage;//图片存放地址
    private int select;

    private Bitmap zhengmian;//身份证正面
    private Bitmap fanmian;//身份证反面
    private String text;//骑手id的值

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        //取消严格模式  FileProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy( builder.build() );
        }
        view=findViewById(R.id.view);//正面
        view1=findViewById(R.id.view1);//反面
        button=findViewById(R.id.button);//上传
        editText=findViewById(R.id.text);//骑手id



        view.setOnClickListener(this);
        view1.setOnClickListener(this);
        button.setOnClickListener(this);//上传按钮点击事件
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.view:
                /*Environment.getExternalStorageDirectory()特殊的文件，应用删除，该文件也不会删除*/
                takePhotoImage=new File(Environment.getExternalStorageDirectory(),"take_photo_image.jpg");
                select=0;
                takePhotoOrSelecctPicture();
                break;
            case R.id.view1:
                /*Environment.getExternalStorageDirectory()特殊的文件，应用删除，该文件也不会删除*/
                takePhotoImage=new File(Environment.getExternalStorageDirectory(),"take_photo_image1.jpg");
                select=1;
                takePhotoOrSelecctPicture();
                break;
            case R.id.button:
                //获取具体的值
                text=editText.getText().toString();//获取骑手id的值
                zhengmian=((BitmapDrawable)view.getDrawable()).getBitmap();//获取身份证正面图片
                fanmian=((BitmapDrawable)view1.getDrawable()).getBitmap();//获取身份证反面图片
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
                    Toast.makeText(VerifyActivity.this, "" + requestCode, Toast.LENGTH_SHORT).show();
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






}
