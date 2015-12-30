package net.dev.mylib.loaderimage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.media.ExifInterface;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;

import net.dev.mylib.DebugLogs;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * 类说明 图片保存与读取的工具类
 *
 * @author shanli
 */
public class ImageUtil {
    private final static String TAG = "ImageUtil";
    public final static String SD_ROOT_PATH = "/Balala";
    public final static String ICON_PATH = "/singer/";
    // public final static String GIFT_PATH = "/gift/";
    public final static String FACE_PATH = "/face/";
    public final static String PICTURE_PATH = "/picture/";
    public final static String CASH_PATH = "/cash/";
    public final static String EFFECT_PATH = "/effect/";

    private final static int MB = 1024 * 1024;
    private final static int CACHE_SIZE = 8 * MB;
    public final static int FREE_SD_SPACE_NEEDED_TO_CACHE = 10;
    private final static long OVER_TIME = 36000;

    private static ImageUtil INSTANCE;

    public static ImageUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ImageUtil();
        }
        return INSTANCE;
    }


    public static void init(Context con) {

        String path = getPath(con, "/icon/");
        File dir = new File(path);
        if (dir.exists()) {
            File newdir = new File(getPath(con, ICON_PATH));
            dir.renameTo(newdir);
        }
    }

    public static String getPath(Context con, String path) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File f = Environment.getExternalStorageDirectory();
            StringBuffer str = new StringBuffer();
            str.append(f.getPath()).append(SD_ROOT_PATH + path);
            return str.toString();
        } else {
            return con.getFilesDir().getAbsolutePath() + path;
        }
    }

    //图片去色
    public static Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        try {
            height = bmpOriginal.getHeight();
            width = bmpOriginal.getWidth();
            Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Config.ARGB_4444);
            Canvas c = new Canvas(bmpGrayscale);
            Paint paint = new Paint();
            ColorMatrix cm = new ColorMatrix();
            cm.setSaturation(0);
            ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
            paint.setColorFilter(f);
            c.drawBitmap(bmpOriginal, 0, 0, paint);
            return bmpGrayscale;
        } catch (Exception ex) {
            return bmpOriginal;
        }
    }

    /**
     * 初始化图片缓存的磁盘路径(文件夹路径)
     *
     * @param SD_Path  SD卡的路径
     * @param Rom_Path 非SD卡路径
     */
    public static String initImagePath(String SD_Path, String Rom_Path) {
        String return_path;
        if (TextUtils.isEmpty(SD_Path) || TextUtils.isEmpty(Rom_Path)) {
            return null;
        }
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File f = Environment.getExternalStorageDirectory();
            StringBuffer path = new StringBuffer();
            path.append(f.getPath()).append(SD_Path);
            File dir = new File(path.toString());
            if (!dir.exists()) {
                makeDirOnSD(f.getPath(),
                        SD_Path.substring(1, SD_Path.length() - 1));
                dir.mkdir();
            }
            return_path = path.toString();
            getInstance().removeCache(return_path);
        } else {
            File dir = new File(Rom_Path);
            if (!dir.exists()) {
                dir.mkdir();
            }
            return_path = Rom_Path;
        }
        return return_path;
    }

    /**
     * 初始化图片缓存的磁盘路径(文件夹路径)
     *
     * @param con
     * @param path 文件名（例：ImageUtil.CASH_PATH）
     * @return
     */
    public static String initImagePath(Context con, String path) {
        if (con == null || con.getFilesDir() == null)
            return null;
        return initImagePath(SD_ROOT_PATH + path, con.getFilesDir()
                .getAbsolutePath() + path);
    }

    /**
     * 创建绝对路径(包含多级)
     *
     * @param header 绝对路径的前半部分(已存在)
     * @param tail   绝对路径的后半部分(第一个和最后一个字符不能是/，格式：123/258/456)
     */
    public static void makeDirOnSD(String header, String tail) {
        String[] sub = tail.split("/");
        File dir = new File(header);
        for (int i = 0; i < sub.length; i++) {
            if (!dir.exists()) {
                dir.mkdir();
            }
            File dir2 = new File(dir + File.separator + sub[i]);
            if (!dir2.exists()) {
                dir2.mkdir();
            }
            dir = dir2;
        }
    }

    /**
     * 设置图片裁剪的一些参数
     *
     * @param intent
     * @return
     */
    public static Intent setCropExtra(Intent intent) {
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 640);
        intent.putExtra("outputY", 640);
        intent.putExtra("return-data", false);

        return intent;
    }

    private static Bitmap canvasBitmap(Bitmap bitmap, Rect srcRect,
                                       Rect desRect, Config config) {
        if (bitmap != null) {
            Bitmap canvasBitmap = null;
            try {
                canvasBitmap = Bitmap.createBitmap(desRect.width(),
                        desRect.height(), config);
            } catch (OutOfMemoryError er) {
                er.printStackTrace();
            }
            if (canvasBitmap != null) {
                Canvas canvas = new Canvas(canvasBitmap);
                canvas.drawBitmap(bitmap, srcRect, desRect, new Paint(
                        Paint.FILTER_BITMAP_FLAG));
                bitmap = canvasBitmap;
            }
        }
        return bitmap;
    }

    private static Bitmap formatFaceIcon(Bitmap bitmap) {
        int top, left, width, height;
        top = left = width = height = 0;
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        if (width < height) {
            top = (height - width) / 2;
            height = width;
        } else if (width > height) {
            left = (width - height) / 2;
            width = height;
        }
        bitmap = canvasBitmap(bitmap, new Rect(left, top, width + left, height
                + top), new Rect(0, 0, 58, 58), Config.ARGB_8888);
        Bitmap tempBmp = Bitmap.createBitmap(58, 58, Config.ARGB_8888);
        Canvas canvas = new Canvas(tempBmp);
        Paint paint = new Paint();
        int color = 0xff424242;
        Rect f_rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF f_rectF = new RectF(0, 0, 58, 58);
        float roundPx = 5;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(f_rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        Rect toRect = new Rect();
        toRect.left = ((int) f_rectF.width() - bitmap.getWidth()) / 2;
        toRect.right = toRect.left + bitmap.getWidth();
        toRect.top = ((int) f_rectF.height() - bitmap.getHeight()) / 2;
        toRect.bottom = toRect.top + bitmap.getHeight();
        canvas.drawBitmap(bitmap, f_rect, toRect, paint);
        // bitmap.recycle();
        bitmap = null;
        return tempBmp;
    }

    /**
     * 保存图片，以100%的质量保存
     *
     * @param bitmap  图片资源
     * @param picPath 保存路径
     */
    public static void saveBitmap(Bitmap bitmap, String picPath) {
        // MyLog.i("++"+(bitmap==null));
        if (bitmap == null || picPath == null) {
            return;
        }
        if (picPath.contains(FACE_PATH)) {
            bitmap = formatFaceIcon(bitmap);
        }
        // MyLog.i("====saveBitmap----"+picPath);
        try {
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                // 清理目录
                ImageUtil.getInstance().removeCache(picPath);
                // 判断SDCARD上的空间
                // MyLog.i("space==="+freeSpaceOnSD());
                if (FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSD()) {
                    // MyLog.i("空间小于10");
                    return;
                }
            }
            File file = new File(picPath);
            FileOutputStream out;
            try {
                out = new FileOutputStream(file);
                if (bitmap.compress(CompressFormat.PNG, 100, out)) {
                    out.flush();
                    out.close();
                }
                // if ((bitmap != null) && (!bitmap.isRecycled()))
                // bitmap.recycle();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            // MyLog.i("保存图片出错");
        }

    }

    public static boolean saveBitmap(Bitmap bitmap, String picPath, CompressFormat format) {
        if (bitmap == null || picPath == null) {
            return false;
        }
        if (picPath.contains("face")) {
            bitmap = formatFaceIcon(bitmap);
        }
        try {
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                // 清理目录
                ImageUtil.getInstance().removeCache(picPath);
                if (FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSD()) {
                    return false;
                }
            }
            File file = new File(picPath);
            FileOutputStream out;
            try {
                out = new FileOutputStream(file);
                if (bitmap.compress(format, 100, out)) {
                    out.flush();
                    out.close();
                    return true;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 保存图片,以70%的质量保存
     *
     * @param bitmap
     * @param picPath
     */
    public static void saveBitmapOther(Bitmap bitmap, String picPath) {
        // MyLog.i("++"+(bitmap==null));
        if (bitmap == null || picPath == null) {
            return;
        }
        // MyLog.i("====saveBitmap----"+picPath);
        try {
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                // 清理目录
                ImageUtil.getInstance().removeCache(picPath);
                // 判断SDCARD上的空间
                // MyLog.i("space==="+freeSpaceOnSD());
                if (FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSD()) {
                    // MyLog.i("空间小于10");
                    return;
                }
            }
            File file = new File(picPath);
            FileOutputStream out;
            try {
                out = new FileOutputStream(file);
                if (bitmap.compress(CompressFormat.JPEG, 70, out)) {
                    out.flush();
                    out.close();
                }
                if ((bitmap != null) && (!bitmap.isRecycled())) {
                    bitmap.recycle();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            // MyLog.i("保存图片出错");
        }

    }

    /**
     * 获取图片
     *
     * @param path 图片的保存路径
     * @return
     */
    public static Bitmap getBitmap(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        File f = new File(path);
        if (!f.exists()) {
            return null;
        }
        // Bitmap b = BitmapFactory.decodeFile(path);
        Bitmap b = null;
        try {
            /*
			 * BitmapFactory.Options opts = new BitmapFactory.Options();
			 * opts.inSampleSize = ImageUtil.computeSampleSize(opts, -1,480 *
			 * 640); MyLog.i("图片取样值："+opts.inSampleSize); b =
			 * BitmapFactory.decodeFile(path, opts);
			 */

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            opts.inPreferredConfig = Config.ARGB_4444;
            BitmapFactory.decodeStream(new FileInputStream(path), null, opts);
            opts.inSampleSize = ImageUtil
                    .computeSampleSize(opts, -1, 480 * 640);
            // MyLog.i("图片取样值："+opts.inSampleSize);
            opts.inJustDecodeBounds = false;
            b = BitmapFactory.decodeStream(new FileInputStream(path), null,
                    opts);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        } catch (OutOfMemoryError error) {
            if (b != null) {
                b.recycle();
            }
            error.printStackTrace();

        }

        return b;
    }

    /**
     * 从本地获取图片
     *
     * @param path      路径
     * @param maxWidth  取样最大宽度(单位px)
     * @param maxHeight 取样最大高度(单位px)
     * @return
     */
    public static Bitmap getBitmap(String path, int maxWidth, int maxHeight) {
        Bitmap b = null;
        try {
            if (!(new File(path).exists()))
                return null;
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(path), null, opts);
            opts.inSampleSize = ImageUtil.computeSampleSize(opts, -1, maxWidth
                    * maxHeight);
            // MyLog.i("图片取样值："+opts.inSampleSize);
            opts.inJustDecodeBounds = false;
            b = BitmapFactory.decodeStream(new FileInputStream(path), null,
                    opts);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        } catch (OutOfMemoryError error) {
            if (b != null) {
                b.recycle();
            }
            error.printStackTrace();
        }
        return b;
    }

    public static Bitmap getBitmap(String path, int maxWidth, int maxHeight,
                                   Config inPreferredConfig) {
        Bitmap b = null;
        try {
            if (!(new File(path).exists()))
                return null;
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(path), null, opts);
            opts.inSampleSize = ImageUtil.computeSampleSize(opts, -1, maxWidth
                    * maxHeight);
            // MyLog.i("图片取样值："+opts.inSampleSize);
            opts.inJustDecodeBounds = false;
            opts.inPreferredConfig = inPreferredConfig;
            b = BitmapFactory.decodeStream(new FileInputStream(path), null,
                    opts);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        } catch (OutOfMemoryError error) {
            if (b != null) {
                b.recycle();
            }
            error.printStackTrace();
        }
        return b;
    }

	/*
	 * public static Bitmap getBitmapFromFileCache(Context context, String url)
	 * { Bitmap bm = null; // MyLog.i(TAG, "filePath:"+filePath); try { String
	 * filePath = initImagePath(ImageUtil.SD_ROOT_PATH + ImageUtil.ICON_PATH,
	 * context.getFilesDir() .getAbsolutePath() + ImageUtil.ICON_PATH); String
	 * imageName = ToolUtil.md5(url); if (new File(filePath +
	 * imageName).exists()) { // MyLog.i(TAG, "图片存在SD卡上"); bm =
	 * getBitmap(filePath + imageName); } } catch (Exception e) { // TODO:
	 * handle exception e.printStackTrace(); } return bm; }
	 */

    /**
     * 将File转成byte[]
     *
     * @param file
     * @return
     */
    public static byte[] getBytesFromFile(File file) {
        if (file == null) {
            return null;
        }
        try {
            FileInputStream stream = new FileInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = stream.read(b)) != -1) {
                out.write(b, 0, n);
            }
            stream.close();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将图片IO流数据转成byte[] 随后可以调用Bitmap b =
     * BitmapFactory.decodeByteArray(byte,0,byte.length)获得Bitmap
     *
     * @param is
     * @return
     * @throws IOException
     */
    public static byte[] getBytesFromIO(InputStream is) throws IOException {
        ByteArrayOutputStream outstream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024]; // 用数据装
        int len = -1;
        while ((len = is.read(buffer)) != -1) {
            outstream.write(buffer, 0, len);
        }
        outstream.close();
        // 关闭流一定要记得。
        return outstream.toByteArray();
    }

    /**
     * 计算sdcard上的剩余空间
     *
     * @return
     */
    public static int freeSpaceOnSD() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
                .getPath());
        double sdFreeMB = ((double) stat.getAvailableBlocks() * (double) stat
                .getBlockSize()) / MB;
        return (int) sdFreeMB;
    }

    /**
     * 删除过期文件
     *
     * @param dirPath
     * @param filename
     */
    public static void removeExpiredCache(String dirPath, String filename) {
        if (null == dirPath || null == filename) {
            return;
        }
        File file = new File(dirPath, filename);
        if (System.currentTimeMillis() - file.lastModified() > OVER_TIME) {
            file.delete();
        }
    }

    /**
     * 计算存储目录下的文件大小，
     * 当文件总大小大于规定的cache_size或者sdcard剩余空间小于FREE_SD_SPACE_NEEDED_TO_CACHE的规定
     * 那么删除40%最近没有被使用的文件
     *
     * @param dirPath
     */
    public void removeCache(String dirPath) {
        // MyLog.i("removeCache");

        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        if (null == files) {
            return;
        }
        int dirSize = 0;
        for (int i = 0; i < files.length; i++) {// 未判断多级目录缓存文件
            dirSize += files[i].length();
        }
        if (dirSize > CACHE_SIZE
                || FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSD()) {
            int removeFactor = (int) ((0.4 * files.length) + 1);
            Arrays.sort(files, new FileLastModifySort());

            // clear some file
            if (removeFactor <= files.length) {
                for (int i = 0; i < removeFactor; i++) {
                    files[i].delete();
                    // MyLog.i("removeCache delete file " +
                    // files[i].getName());
                }
            }

        }
    }

    class FileLastModifySort implements Comparator<File> {

        @Override
        public int compare(File arg0, File arg1) {
            if (arg0.lastModified() > arg1.lastModified()) {
                return 1;
            } else if (arg0.lastModified() == arg1.lastModified()) {
                return 0;
            } else {
                return -1;
            }
        }

    }

    /**
     * 截取图片，去除图片左右的暗色 暂时只处理左右两边
     *
     * @param srcImage
     * @return
     */
    public static Bitmap cutRealImage(Bitmap srcImage) {

        // MyLog.i("begin time: " + System.currentTimeMillis());

        if (null == srcImage) {
            return null;
        }

        int srcWidth = srcImage.getWidth();
        int srcHeight = srcImage.getHeight();
        int middleIndexWidth = srcWidth / 2;
        int middleIndexHeight = srcHeight / 2;

        int widthLeft = 1;
        int widthRight = srcWidth - 1;
        int heightTop = 1;
        int heightBottom = srcHeight - 1;
        int indexColor = 0;

        // left,从中间往左计算
        int beginIndexWidth = 0;
        int endIndexWidth = middleIndexWidth;
        while (endIndexWidth > beginIndexWidth) {

            indexColor = srcImage.getPixel(endIndexWidth, middleIndexHeight);
            if (checkWithRemoveColor(indexColor)) {
                widthLeft = endIndexWidth + 10;
                break;
            }
            endIndexWidth = endIndexWidth - 5;
        }

        // right,从中间往右计算
        beginIndexWidth = middleIndexWidth;
        endIndexWidth = srcWidth;
        while (endIndexWidth > beginIndexWidth) {

            indexColor = srcImage.getPixel(beginIndexWidth, middleIndexHeight);
            if (checkWithRemoveColor(indexColor)) {
                widthRight = beginIndexWidth - 10;
                break;
            }
            beginIndexWidth = beginIndexWidth + 5;

        }

        // top
        int beginIndexHeight = 0;
        int endIndexHeight = middleIndexHeight;
        while (endIndexHeight > beginIndexHeight) {

            indexColor = srcImage.getPixel(middleIndexWidth, endIndexHeight);
            if (checkWithRemoveColor(indexColor)) {
                heightTop = endIndexHeight + 10;
                break;
            }
            endIndexHeight = endIndexHeight - 5;

        }

        // bottom
        beginIndexHeight = middleIndexHeight;
        endIndexHeight = srcHeight;
        while (endIndexHeight > beginIndexHeight) {

            indexColor = srcImage.getPixel(middleIndexWidth, beginIndexHeight);
            if (checkWithRemoveColor(indexColor)) {
                heightBottom = beginIndexHeight - 10;
                break;
            }
            beginIndexHeight = beginIndexHeight + 5;

        }

        // 截取中间的图片
        int dstWidth = widthRight - widthLeft;
        int dstHeight = heightBottom - heightTop;
        Bitmap dstImage = Bitmap.createBitmap(srcImage, widthLeft, heightTop,
                dstWidth, dstHeight);

        // MyLog.i("end time: " + System.currentTimeMillis());

        return dstImage;
    }

    private static boolean checkWithRemoveColor(int indexColor) {

        final int removeRed = 75;
        final int removeGreen = 75;
        final int removeBlue = 75;

        int red = Color.red(indexColor);
        int green = Color.green(indexColor);
        int blue = Color.blue(indexColor);

		/*
		 * MyLog.i("red: " + red); MyLog.i("green: " + green); MyLog.i("blue: "
		 * + blue);
		 */

        if (red == removeRed && green == removeGreen && blue == removeBlue) {
            return true;
        }

        return false;
    }

    public static int computeSampleSize(BitmapFactory.Options options,
                                        int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
                Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    /**
     * 截屏
     *
     * @param v        视图
     * @param filePath 保存路径
     */
    public static String getScreenHot(View v, String filePath) {
        try {
            Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                    Config.ARGB_8888);
            Canvas canvas = new Canvas();
            canvas.setBitmap(bitmap);
            v.draw(canvas);
            try {
                FileOutputStream fos = new FileOutputStream(filePath);
                bitmap.compress(CompressFormat.PNG, 100, fos);
                return filePath;
            } catch (FileNotFoundException e) {
                e.printStackTrace();

            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            // MyLog.i("截屏", "内存不足！");
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 计算图片的缩放比例
     *
     * @return
     */
    public static int getScare(String imageUrl, Display display) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(imageUrl);
            HttpResponse response = client.execute(httpGet);
            int code = response.getStatusLine().getStatusCode();

            if (200 == code) {
                InputStream is = response.getEntity().getContent();
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(is, null, opts);

                int imageWidth = opts.outWidth;
                int imageHeight = opts.outHeight;


                int screenWidth = display.getWidth();
                int screenHeight = display.getHeight();

                int widthscale = imageWidth / screenWidth;
                int heightscale = imageHeight / screenHeight;
                int scale = widthscale > heightscale ? widthscale : heightscale;

                return scale;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;//网络连接失败时默认返回1
    }

    public static byte[] bitmap2Bytes(Bitmap bmp, boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static Bitmap bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    /**
     * Drawable 转 bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof NinePatchDrawable) {
            Bitmap bitmap = Bitmap
                    .createBitmap(
                            drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(),
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888
                                    : Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            return null;
        }
    }

    public static boolean bindImageView(Context con, ImageView view,
                                        String path, String fileName) {
        String fullpath = ImageUtil.initImagePath(con, path) + fileName;
        Bitmap bitmap = ImageUtil.getBitmap(fullpath);
        if (bitmap != null) {
            view.setImageBitmap(bitmap);
            return true;
        }
        return false;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

    /**
     * @author xpp create at 2013-12-06 10:23:00 检查是否有SD卡
     */
    public static boolean hasSdCard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    // 图片缓存
    private LruCache<String, Bitmap> mLruCache;

    /**
     * 从LruCache中获取一张图片，如果不存在就返回null。
     */
    private Bitmap getBitmapFromLruCache(String key) {
        return mLruCache.get(key);
    }

    public static Bitmap getimage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        // newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath);// 此时返回bm为空
        int digree = 0;
        // 读取方向信息
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(srcPath);
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
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), m, true);
        }
        return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
    }

    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;
        while (baos.size() / 1024 > 200) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            image.compress(CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 9;// 每次都减少10
        }
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    public static Bitmap comp(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(CompressFormat.JPEG, 100, baos);
        int options = 100;
        while (baos.size() / 1024 > 1000) {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();// 重置baos即清空baos
            image.compress(CompressFormat.JPEG, options, baos);// 把压缩后的数据存放到baos中
            options -= 5;
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 现在主流手机比较多是1024*900分辨率，所以高和宽我们设置为
        float hh = 1024f;// 这里设置高度为800f
        float ww = 900f;// 这里设置宽度为480f
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return bitmap; // compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
    }


    //压缩成多大的图片
    public static Bitmap scopeCompress(Bitmap image, float distWidth, float distheight, int size) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(CompressFormat.JPEG, 100, baos);
        int options = 100;
        while (baos.size() / 1024 > size) {
            baos.reset(); // 重置baos即清空baos
            image.compress(CompressFormat.JPEG, options, baos);
            options -= 5;
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = null;
        BitmapFactory.decodeStream(isBm, null, newOpts);
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;

        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > distWidth) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / distWidth);
        } else if (w < h && h > distheight) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / distheight);
        }
        if (be <= 0) {
            be = 1;
        }
        newOpts.inJustDecodeBounds = false;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return bitmap; // compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
    }


    //压缩成多大的图片
    public static void saveScopeCompress(Bitmap image, int size, String filePath) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(CompressFormat.JPEG, 100, baos);
        int options = 100;
        while (baos.size() / 1024 > size) {
            baos.reset(); // 重置baos即清空baos
            image.compress(CompressFormat.JPEG, options, baos);
            options -= 5;
        }
        DebugLogs.i("jjfly options :" + options + "------" + baos.size() / 1024);
        try {
            FileOutputStream fout = new FileOutputStream(filePath);
            baos.writeTo(fout);
            fout.flush();
            fout.close();
            baos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //压缩成多大的图片
    public static void saveScopeCompress(Bitmap image, float distWidth, float distheight, int size, String filePath) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(CompressFormat.JPEG, 100, baos);
        int options = 100;
        while (baos.size() / 1024 > size) {
            baos.reset(); // 重置baos即清空baos
            image.compress(CompressFormat.JPEG, options, baos);
            options -= 5;
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = null;
        BitmapFactory.decodeStream(isBm, null, newOpts);
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;

        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > distWidth) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / distWidth);
        } else if (w < h && h > distheight) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / distheight);
        }
        if (be <= 0) {
            be = 1;
        }
        newOpts.inJustDecodeBounds = false;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        try {
            FileOutputStream fout = new FileOutputStream(filePath);
            if (bitmap.compress(CompressFormat.JPEG, options, fout)) {
                fout.flush();
                fout.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}