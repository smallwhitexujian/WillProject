package net.dev.mylib.cache.imageCache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Comparator;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.webkit.MimeTypeMap;

import net.dev.mylib.DebugLogs;
import net.dev.mylib.Encryption;
import net.dev.mylib.cache.fileCheanCache.FileUtil;
import net.dev.mylib.loaderimage.ImageFileLoader;

public class ImageFileCache {
    public static final String FILEPATH_CACHE_DOWNLOAD =  "download";
    public static final String FILEPATH_CACHE_UPLOAD = "upload";
    //private static final String WHOLESALE_CONV = ".cach";

    private static final int MB = 1024 * 1024;
    private static final int CACHE_SIZE = 20;
    private static final int FREE_SD_SPACE_NEEDED_TO_CACHE = 20;

    private String cacheDir;

    public ImageFileCache() {
    }

    public void setCacheDir(String cacheDir){
        this.cacheDir=cacheDir;
        removeCache(getCacheDownloadPath(""));
        removeCache(getCacheUploadPath(""));
    }

    public Bitmap getImage(String filename,boolean isDownload) {
        String path;
        if(isDownload){
            path = getCacheDownloadPath(filename);
        }else{
            path=filename;
        }
        File file = new File(path);
        if (file.exists()) {
            try {
                Bitmap bmp;
                if(isDownload){
                    bmp = BitmapFactory.decodeFile(path);// ImageLoader.getInstance(3, ImageLoader.Type.LIFO).loadImage2(path, 1024, 1680);
                    if(bmp==null){
                        file.delete();
                    }else{
                        FileUtil.updateLastModified(file);
                    }
                }else {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPurgeable = true;
                    options.inInputShareable = true;
                    options.inJustDecodeBounds = true;
                    bmp = BitmapFactory.decodeFile(path, options);
                    options.inSampleSize = ImageFileLoader.computeSampleSize(options, -1, 800 * 600);
                    options.inJustDecodeBounds = false;
                    bmp = BitmapFactory.decodeFile(path, options);
                }
                return bmp;
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        return null;
    }

    public Bitmap getImageFile(String path,int w,int h,boolean isUpload,String extensions){
        File file = new File(path);
        if (file.exists()) {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                //options.inPreferredConfig = Bitmap.Config.ARGB_4444;
                options.inPurgeable = true;
                options.inInputShareable = true;
                options.inJustDecodeBounds = true;
                Bitmap bmp = BitmapFactory.decodeFile(path, options);
                options.inSampleSize = ImageFileLoader.computeSampleSize(options, -1, w * h);
                options.inJustDecodeBounds = false;
                bmp = BitmapFactory.decodeFile(path, options);//ImageLoader.getInstance().loadImage2(path, w, h);
                if (bmp == null) {
                    return null;
                }

                int digree = 0;
                // 读取方向信息
                ExifInterface exif = null;

                try {
                    exif = new ExifInterface(path);
                } catch (IOException e) {
                    e.printStackTrace();
                    exif = null;
                }

                if (exif != null) {
                    // 读取图片中相机方向信息
                    int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED);
                    // 计算旋转角度
                    switch (ori) {
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            digree = 90;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            digree = 180;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            digree = 270;
                            break;
                        default:
                            digree = 0;
                            break;
                    }
                }

                if (digree != 0) {
                    // 旋转图片
                    Matrix m = new Matrix();
                    m.postRotate(digree);
                    bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
                            bmp.getHeight(), m, true);
                }
                if(bmp!=null){
                    //extensions=FileUtil.getExtensions(path);
                    if(isUpload){
                        path=getCacheUploadPath(FileUtil.convertUrlToFileName(path)+extensions);
                    }else{
                        FileUtil.updateLastModified(file);
                    }
                    saveBitmap(bmp,path,extensions);
                }
                return bmp;
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        return null;
    }

    public void saveBitmap(Bitmap bm, String path,String extensions) {
        if (bm == null) {
            return;
        }
        if (FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSd()) {
            return;
        }
        File file = new File(path);
        if(!file.exists()){
            file.getParentFile().mkdirs();
        }
        try {
            OutputStream outStream = new FileOutputStream(file);
            if (extensions.equals(".jpg")) {
                bm.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            }else if(extensions.equals(".png")){
                bm.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            }else{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    bm.compress(Bitmap.CompressFormat.WEBP,100,outStream);
                }else{
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                }
            }
            outStream.flush();
            outStream.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }

    private boolean removeCache(String dirPath) {
        DebugLogs.d(dirPath);
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        if (files == null) {
            return true;
        }
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return false;
        }

        long yesterday= System.currentTimeMillis()-1000*60*60*24;

        int dirSize = files.length;
        boolean needfree=FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSd();
        for (int i = 0; i < dirSize; i++) {
//            if (files[i].getName().contains(WHOLESALE_CONV)) {
//                dirSize += files[i].length();
//            }
            if(files[i].lastModified()<yesterday||needfree){
                files[i].delete();
            }
        }

        if (freeSpaceOnSd() <= CACHE_SIZE) {
            return false;
        }

        return true;
    }

    private int freeSpaceOnSd() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        double sdFreeMB = ((double) stat.getAvailableBlocks() * (double) stat.getBlockSize()) / MB;
        return (int) sdFreeMB;
    }

    public String getCacheDownloadPath(String filename){
        return cacheDir+ File.separator+ ImageFileCache.FILEPATH_CACHE_DOWNLOAD+File.separator+filename;
    }

    public String getCacheUploadPath(String filename){
        return cacheDir+ File.separator+ ImageFileCache.FILEPATH_CACHE_UPLOAD+File.separator+filename;
    }


}
