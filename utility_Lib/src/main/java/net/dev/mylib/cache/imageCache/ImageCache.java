package net.dev.mylib.cache.imageCache;

import android.content.Context;
import android.graphics.Bitmap;

import net.dev.mylib.DebugLogs;
import net.dev.mylib.Encryption;
import net.dev.mylib.Utility;
import net.dev.mylib.cache.fileCheanCache.FileCache;
import net.dev.mylib.cache.fileCheanCache.FileUtil;

import java.io.File;


public class ImageCache {
    private ImageMemoryCache memoryCache;
    private ImageFileCache fileCache;

    private static ImageCache imageCache;

    public static ImageCache getInstance(){
        if(imageCache==null){
            imageCache=new ImageCache();
        }
        return imageCache;
    }

    private ImageCache() {
        memoryCache = new ImageMemoryCache();
    }

    public void setCacheDir(String cacheDir){
        if(cacheDir!=null&&cacheDir.length()>0&&fileCache==null){
            fileCache = new ImageFileCache();
            fileCache.setCacheDir(cacheDir);
            try {
                File dirFile = new File(cacheDir);
                if (!dirFile.exists())
                    dirFile.mkdirs();
            }catch (Exception ex){}
        }
    }

    public String getCacheDownloadPath(String filename){
        if(fileCache==null){
            return null;
        }
        return fileCache.getCacheDownloadPath(filename);
    }

    public String getCacheUploadPath(String filename){
        if(fileCache==null){
            return null;
        }
        return fileCache.getCacheUploadPath(filename);
    }

    public Bitmap getCache(String url) {
        String filename=FileUtil.convertUrlToFileName(url);
        return memoryCache.getBitmapFromCache(filename);
    }

    public Bitmap getBitmap(String url) {
        String filename=FileUtil.convertUrlToFileName(url);
        boolean isDownload=true;
        if(url.startsWith("http")||url.toLowerCase().startsWith("http")){
            url=filename;
        }else{
            isDownload=false;
        }
        Bitmap result = memoryCache.getBitmapFromCache(filename);
        if (result == null&&fileCache!=null) {
            result = fileCache.getImage(url,isDownload);
            if (result != null) {
                memoryCache.addBitmapToCache(filename, result);
            }
        }
        return result;
    }

    public Bitmap getBitmapCut(String path,int w,int h,boolean isUpload,String extensions){
        String filename=FileUtil.convertUrlToFileName(path);
        Bitmap result = memoryCache.getBitmapFromCache(filename);
        if (result == null&&fileCache!=null) {
            result = fileCache.getImageFile(path, w, h,isUpload,extensions);
            if (result != null) {
                memoryCache.addBitmapToCache(filename, result);
            }
        }
        return result;
    }

    public void put(Bitmap img,String url) {
        String filename= FileUtil.convertUrlToFileName(url);
        memoryCache.addBitmapToCache(filename, img);
        //fileCache.saveBitmap(img, url);
    }

    public void save(Bitmap img,String path,String extensions) {
        fileCache.saveBitmap(img, path,extensions);
    }
}
