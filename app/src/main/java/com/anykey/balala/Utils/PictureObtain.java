package com.anykey.balala.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import net.dev.mylib.DebugLogs;
import net.dev.mylib.cache.sharedPreferences.SharedPreferencesUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PictureObtain {

    //拍照
    public String dispatchTakePictureIntent(Activity context, int reqCode) {
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
                saveUri(context, contentUri);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(contentUri)));
                takePictureIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                context.startActivityForResult(takePictureIntent, reqCode);
            }
        }
        DebugLogs.i("ssss " + contentUri);
        return contentUri;
    }

    //保存到DCIM的Camera目录下
    private String createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp;
        String DCIMDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
        File cameraDir = new File(DCIMDir + "/Camera");
        DebugLogs.i(cameraDir.getAbsolutePath() + cameraDir.exists());
        File image = new File(
                cameraDir,  /* directory */
                imageFileName  /* prefix */
                        + ".jpg"       /* suffix */
        );
        // Save a file: path for use with ACTION_VIEW intents
//            "file:" + image.getAbsolutePath()
        return image.getAbsolutePath();
    }


    //保存文件后发送通知广播
    public void notifyChange(Activity context, Uri contentUri) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    //取本地相册
    public void getLocalPicture(Activity context, int reqCode) {
        Intent photointent = null;
        if (android.os.Build.VERSION.SDK_INT >= 19) {
            photointent = new Intent(android.content.Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            photointent.setType("image/*");
        } else {
            photointent = new Intent(Intent.ACTION_GET_CONTENT);
            photointent.addCategory(Intent.CATEGORY_OPENABLE);
            photointent.setType("image/*");
        }
        context.startActivityForResult(photointent, reqCode);
    }

    //通过uri获取真实路径
    public String getRealPathFromURI(Activity context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            // Do not call Cursor.close() on a cursor obtained using this method,
            // because the activity will do that for you at the appropriate time
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            DebugLogs.d("column_index" + column_index);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            e.printStackTrace();
            return contentUri.getPath();
        } finally {
            cursor.close();
        }
    }

    //图像裁剪
    public void crop(Activity context, Uri uri, int reqCode) {
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
        context.startActivityForResult(cropIntent, reqCode);
    }


    //保存url
    private void saveUri(Context context, String uri) {
        SharedPreferencesUtil.getInstance(context).putString("picUri", uri);
    }

    //获取url
    private Uri getUri(Context context) {
        String fileName = SharedPreferencesUtil.getInstance(context).getString("picUri", "");
        Uri uri = Uri.fromFile(new File(fileName));
        return uri;

    }
}
