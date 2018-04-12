package com.example.liangfeng.work;


import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

import static android.graphics.Bitmap.createBitmap;

public class SubmitActivity extends AppCompatActivity {
    private EditText edit_title;
    private EditText edit_describe;
    private EditText edit_phone;
    Button take_photo;
    Button select_photo;
    Button submit;
    private ImageView picture;
    String imagePath = null;
    private Uri imageUri;
    String icon_path;
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);

        edit_phone = (EditText) findViewById(R.id.edit_phone);
        edit_describe = (EditText) findViewById(R.id.edit_describe);
        edit_title = (EditText) findViewById(R.id.edit_title);
        take_photo = (Button) findViewById(R.id.take_photo);
        select_photo = (Button) findViewById(R.id.select_picture);
        submit = (Button) findViewById(R.id.submit);
        picture = (ImageView) findViewById(R.id.edit_picture);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressDialog progressDialog = new ProgressDialog(SubmitActivity.this);
                progressDialog.setTitle("正在上传");
                progressDialog.setMessage("Loading.....");
                progressDialog.setCancelable(false);
                progressDialog.show();
                final String title, describe, phone, icon_path;
                title = edit_title.getText().toString();
                describe = edit_describe.getText().toString();
                phone = edit_phone.getText().toString();
                final BmobFile bmobfile = new BmobFile(new File(getExternalCacheDir(), "output_image.png"));
                bmobfile.uploadblock(new UploadFileListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            Lost lost = new Lost();
                            lost.setTitle(title);
                            lost.setDescribe(describe);
                            lost.setPhone(phone);
                            lost.setPicture(bmobfile);
                            lost.save(new SaveListener<String>() {
                                @Override
                                public void done(String s, BmobException e) {
                                    if (e == null) {
                                        Toast.makeText(SubmitActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Toast.makeText(SubmitActivity.this, "发布失败", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(SubmitActivity.this, "发布失败", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    }
                });
            }
        });

        take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File outputImage = new File(getExternalCacheDir(), "output_image.png");
                icon_path = Environment.getExternalStorageDirectory() + "output_image.png";
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT >= 24) {
                    imageUri = FileProvider.getUriForFile(SubmitActivity.this,
                            "com.example.cameralbumtest.fileprovice", outputImage);
                } else {
                    imageUri = Uri.fromFile(outputImage);
                }
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_PHOTO);
            }
        });
        //从相册中选择图片
        select_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("android.intent.action.GET_CONTENT");
                intent.setType("image/*");
                startActivityForResult(intent, CHOOSE_PHOTO);

            }
        });
    }

    @Override
    protected void onActivityResult(int requsetCode, int resultCode, Intent data) {
        switch (requsetCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        picture.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        //4.4以上使用这个方法处理图片
                        handleIMageOnKitKat(data);
                    } else {
                        handleIMageBeforKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    @TargetApi(19)
    private void handleIMageOnKitKat(Intent data) {
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            //如果是document类型的URI，则使用document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];//解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果不是document类型的URI，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            imagePath = uri.getPath();
        }
        displayImage(imagePath);
    }

    private void handleIMageBeforKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            picture.setImageBitmap(bitmap);
        } else {
            Toast.makeText(SubmitActivity.this, "未得到图片", Toast.LENGTH_SHORT).show();
        }
    }

            /*public Bitmap getBitmapFromUrl(String path){
                    Bitmap bm=null;
                    try{
                        URL url=new URL(path);
                        URLConnection connection=url.openConnection();
                        connection.connect();
                        InputStream inputStream=connection.getInputStream();
                        bm= BitmapFactory.decodeStream(inputStream);
                        int width = bm.getWidth();
                        int height = bm.getHeight();
                        //设置想要的大小
                        int newWidth=300;
                        int newHeight=300;

                        //计算压缩的比率
                        float scaleWidth=((float)newWidth)/width;
                        float scaleHeight=((float)newHeight)/height;

                        //获取想要缩放的matrix
                        Matrix matrix = new Matrix();
                        matrix.postScale(scaleWidth,scaleHeight);

                        //获取新的bitmap
                        bm=Bitmap.createBitmap(bm,0,0,width,height,matrix,true);
                        bm.getWidth();
                        bm.getHeight();

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return  bm;
                }*/

}

