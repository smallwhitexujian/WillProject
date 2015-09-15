package net.dev.mylib.cache.imageCache;

import android.content.Context;
import android.graphics.Bitmap;

public class ImageCache {

	ImageMemoryCache memoryCache ;
	ImageFileCache fileCache ;
	Context content;
	
	public ImageCache(Context _content)
	{
		content = _content;
		memoryCache = new ImageMemoryCache(content);
		fileCache = new ImageFileCache();
	}
	
	public Bitmap getBitmap(String url) {
	    Bitmap result = memoryCache.getBitmapFromCache(url);
	    if (result == null) {
	        result = fileCache.getImage(url);
	        if (result == null) {
	           
	        } else {
	            memoryCache.addBitmapToCache(url, result);
	        }
	    }
	    return result;
	}
	
	public void put(String url, Bitmap img)
	{
		  fileCache.saveBitmap(img, url);
          memoryCache.addBitmapToCache(url, img);
	}

}
