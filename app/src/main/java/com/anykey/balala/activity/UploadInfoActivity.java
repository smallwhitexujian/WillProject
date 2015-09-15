package com.anykey.balala.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import com.anykey.balala.R;

import net.dev.mylib.DebugLogs;
import net.dev.mylib.cache.sharedPreferences.SharedPreferencesUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jjfly on 15-9-7.
 */
public class UploadInfoActivity extends BaseActivity implements View.OnClickListener{

    private ImageView mPhoneImageView;
    private ImageView mPictureImageView;
    private ImageView mFaceIconImageView;
    private String mContentUri;


    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_GET_PICTURE = 2;
    static final int REQUEST_CROP_PICTURE = 3;

    private PictureObtain mObtain;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_info);
        mObtain = new PictureObtain();
        initView();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        DebugLogs.i("data ==="+data);
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_TAKE_PHOTO:
                mObtain.notifyChange(UploadInfoActivity.this,getUri(UploadInfoActivity.this));
                mObtain.crop(UploadInfoActivity.this,getUri(UploadInfoActivity.this));
//                if(mContentUri != null){
//
//                }
//                else{
//                    DebugLogs.i("sss contentUri is "+mContentUri);
//                }
                break;
            case REQUEST_GET_PICTURE:
                if(data != null){
                    Uri uri = data.getData();
                    mObtain.crop(UploadInfoActivity.this,uri);
                }
                break;
            case REQUEST_CROP_PICTURE:
                if (data != null) {
                    if(data.getParcelableExtra("data") != null){
                        Bitmap bitmap = data.getParcelableExtra("data");
                        mFaceIconImageView.setImageBitmap(bitmap);
                    }
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DebugLogs.i("UserInfo destory is called ");
    }

    private void initView(){
        mPhoneImageView = (ImageView)findViewById(R.id.userinfo_photo);
        mPictureImageView = (ImageView)findViewById(R.id.userinfo_picture);
        mFaceIconImageView = (ImageView)findViewById(R.id.userinfo_faceIcon);
        mPhoneImageView.setOnClickListener(this);
        mPictureImageView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.userinfo_photo:
                mContentUri = mObtain.dispatchTakePictureIntent(UploadInfoActivity.this);
                break;
            case R.id.userinfo_picture:
                mObtain.getLocalPicture(UploadInfoActivity.this);
                break;
        }
    }

    private class PictureObtain{
        //拍照，保存到本地，广播通知相册更新
//        public static void dispatchTakePictureIntent(Activity context) {
//            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
//                context.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//            }
//        }
        public String dispatchTakePictureIntent(Activity context) {
            String contentUri = null;
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
                // Create the File where the photo should go
                try {
                    contentUri = createImageFile();

                } catch (IOException ex) {
                    // Error occurred while creating the File
                    ex.printStackTrace();
                }
                // Continue only if the File was successfully created
                if (contentUri != null) {
                    saveUri(context,contentUri);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(new File(contentUri)));
                    takePictureIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,1);
                    context.startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }
            DebugLogs.i("ssss "+contentUri);
            return contentUri;
        }

        //保存到DCIM的Camera目录下
        private String createImageFile() throws IOException {
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "IMG_" + timeStamp;
            String DCIMDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
            File cameraDir = new File(DCIMDir+"/Camera");
            DebugLogs.i(cameraDir.getAbsolutePath()+cameraDir.exists());
            File image = new File(
                    cameraDir,  /* directory */
                    imageFileName  /* prefix */
                    +".jpg"       /* suffix */
            );
            // Save a file: path for use with ACTION_VIEW intents
//            "file:" + image.getAbsolutePath()
           return image.getAbsolutePath();
        }


        public void notifyChange(Activity context,Uri contentUri){
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(contentUri);
            context.sendBroadcast(mediaScanIntent);
        }


        public void getLocalPicture(Activity context){
            Intent photointent=null;
            if(android.os.Build.VERSION.SDK_INT >=19){
                photointent = new Intent(android.content.Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                photointent.setType("image/*");
            }
            else{
                photointent = new Intent(Intent.ACTION_GET_CONTENT);
                photointent.addCategory(Intent.CATEGORY_OPENABLE);
                photointent.setType("image/*");
            }
            context.startActivityForResult(photointent, REQUEST_GET_PICTURE);
        }
        //通过uri
        public String getRealPathFromURI(Activity context,Uri contentUri){
            Cursor cursor = null;
            try{
                String[] proj = {MediaStore.Images.Media.DATA};
                // Do not call Cursor.close() on a cursor obtained using this method,
                // because the activity will do that for you at the appropriate time
                cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                DebugLogs.d("column_index"+column_index);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            }catch (Exception e){
                e.printStackTrace();
                return contentUri.getPath();
            }finally {
                cursor.close();
            }
        }

        public void crop(Activity context,Uri uri){
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(uri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 128);
            cropIntent.putExtra("outputY", 128);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            context.startActivityForResult(cropIntent,REQUEST_CROP_PICTURE);
        }

    }


    private void saveUri(Context context,String uri){
        SharedPreferencesUtil.getInstance(context).putString("picUri",uri);
    }

    private Uri getUri(Context context){
        String fileName = SharedPreferencesUtil.getInstance(context).getString("picUri","");
        Uri uri = Uri.fromFile(new File(fileName));
        return uri;

    }


}







