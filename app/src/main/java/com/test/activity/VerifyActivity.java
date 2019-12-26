package com.test.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.test.courier.R;

import java.io.File;
import java.io.IOException;

public class VerifyActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView view;//身份证正面
    private ImageView view1;//身份证反面
    private Button button;//上传按钮
    private Uri imageUri;//图片存放地址
    public static final int TAKE_PHOTO=11;//拍照
    public static final int CROP_PHOTO=12;//裁剪图片
    public static final int CROP_PHOTOT=10;//裁剪图片
    public static final int LOCAL_CROP=13;//本地图库
    private File takePhotoImage;
    private int select;

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


        view.setOnClickListener(this);
        view1.setOnClickListener(this);
        button.setOnClickListener(this);
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
        }
    }
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO://拍照
//                if (requestCode==RESULT_OK){
//                    /*创建intent用于裁剪图片
//                    com.android.camera.action.CROP系统裁剪*/
//                    Intent intent=new Intent("com.android.camera.action.CROP");
//                    intent.setDataAndType(imageUri,"image/*");
//                    //  设置裁剪图片的宽高
//                    intent.putExtra("outputX", 300);
//                    intent.putExtra("outputY", 300);
//                    intent.putExtra("return-data", true);
//                    //指定输出到文件uri中
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
//                    //启动intent开始裁剪
//                    startActivityForResult(intent,CROP_PHOTO);
//
//                }
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
