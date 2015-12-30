package com.anykey.balala.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.anykey.balala.AppBalala;
import com.anykey.balala.CommonResultCode;
import com.anykey.balala.R;

import net.dev.mylib.DebugLogs;
import net.dev.mylib.cache.fileCheanCache.FileUtil;
import net.dev.mylib.cache.sharedPreferences.SharedPreferencesUtil;
import net.dev.mylib.view.ActionSheetDialog;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PictureObtain {

    public void showDialog(final Activity activity){
        new ActionSheetDialog(activity)
                .builder()
                .setCancelable(true)
                .setCanceledOnTouchOutside(true)
                .addSheetItem(activity.getString(R.string.camera), ActionSheetDialog.SheetItemColor.Blue,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                dispatchTakePictureIntent(activity, CommonResultCode.SET_ADD_PHOTO_CAMERA);
                            }
                        })
                .addSheetItem(activity.getString(R.string.album), ActionSheetDialog.SheetItemColor.Blue,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                getLocalPicture(activity, CommonResultCode.SET_ADD_PHOTO_ALBUM);
                            }
                        }).show();
    }

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
        String state = android.os.Environment.getExternalStorageState();
        if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
            throw new IOException("SD Card is not mounted,It is  " + state + ".");
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp+".jpg";

        String imagePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+ AppBalala.FILEPATH_CAMERA+"/"+imageFileName;
        File fileInfo = new File(imagePath).getParentFile();
        mkDir(fileInfo);

//        String DCIMDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
//        Environment.getExternalStorageDirectory();
//        File cameraDir = new File(DCIMDir + "/Camera");
//        File image = new File(
//                cameraDir,  /* directory */
//                imageFileName  /* prefix */
//                        + ".jpg"       /* suffix */
//        );
        return imagePath;
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
            if (cursor != null){
                cursor.close();
            }
        }
    }

    /**
     * @param context
     * @param uri
     * @param reqCode
     * @param x
     * @param y
     *  裁剪小图片, 不推荐使用
     */
    public void cropSmall(Activity context, Uri uri, int reqCode,int x,int y) {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        // indicate image type and Uri
        cropIntent.setDataAndType(uri, "image/*");
        // set crop properties
        cropIntent.putExtra("crop", "true");
        // indicate aspect of desired crop
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        cropIntent.putExtra("scale", true);
        // indicate output X and Y
        cropIntent.putExtra("outputX", x);
        cropIntent.putExtra("outputY", y);
        // retrieve data on return
        cropIntent.putExtra("return-data", true);
        context.startActivityForResult(cropIntent, reqCode);
    }

    /**
     * @param context
     * @param uri
     * @param reqCode
     * @param reqCode:onActivityResult 的requist值
     * @param outputX:图片分辨率
     * @param outputY:图片分辨率
     *
     *  把裁切的结果覆盖原来的图片
     */
    public void cropBig(Activity context, Uri uri, int reqCode,int outputX,int outputY){
        cropBig(context, uri, uri, reqCode, outputX, outputY);
    }

    //把裁切的结果放不同路径下

    /***
     * @param context
     * @param srcUri :原始uri
     * @param distUri:目的url
     * @param reqCode:onActivityResult 的requist值
     * @param outputX:图片分辨率
     * @param outputY:图片分辨率
     */
    public void cropBig(Activity context, Uri srcUri,Uri distUri, int reqCode,int outputX,int outputY){
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setDataAndType(srcUri, "image/*");
        cropIntent.putExtra("crop", "true");
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        cropIntent.putExtra("outputX", outputX);
        cropIntent.putExtra("outputY", outputY);
        cropIntent.putExtra("scale", true);
        cropIntent.putExtra(MediaStore.EXTRA_OUTPUT,distUri);
        cropIntent.putExtra("return-data", false);
        cropIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        cropIntent.putExtra("noFaceDetection", true); // no face detection
        context.startActivityForResult(cropIntent, reqCode);
    }

    /**
     * 读取照片exif信息中的旋转角度<br/>
     * http://www.eoeandroid.com/thread-196978-1-1.html
     *
     * @param path 照片路径
     * @return角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }


    //保存url
    public void saveUri(Context context, String uri) {
        SharedPreferencesUtil.getInstance(context).putString("picUri", uri);
    }

    //获取url
    public Uri getUri(Context context) {
        String fileName = SharedPreferencesUtil.getInstance(context).getString("picUri", "");
        Uri uri = Uri.fromFile(new File(fileName));
        return uri;

    }

    //创建目录，如果已经存在目录则不创建
    private static void mkDir(File dirInfo){
        File parent = dirInfo.getParentFile();
        if (!parent.exists()) {
            mkDir(parent);
        }
        if(!dirInfo.exists()){
            dirInfo.mkdir();
        }
    }

    public Uri obtainUrl(){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp+".jpg";
        //String imagePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+AppBalala.FILEPATH_CAMERA+"/"+imageFileName;
        String imagePath=AppBalala.imageCache.getCacheUploadPath(FileUtil.convertUrlToFileNameEx(imageFileName));
        mkDir(new File(imagePath).getParentFile());
        return Uri.fromFile(new File(imagePath));
    }

}
