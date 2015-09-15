package com.anykey.balala;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;

import com.activeandroid.ActiveAndroid;
import com.anykey.balala.service.BackgroundService;
import com.bugsnag.android.Bugsnag;
import com.facebook.FacebookSdk;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import net.dev.mylib.DebugLogs;
import net.dev.mylib.Utility;

import java.util.ArrayList;
import java.util.List;

import im.fir.sdk.FIR;

/**
 * Created by xujian on 15/8/26.
 * Application 全局变量
 */
public class AppBalala extends Application{
    private static AppBalala instance = null;
    private boolean m_Logined = false; // 登陆状态 true:已登录; false:未登录
    public static String HOST = "192.168.199.182";
    public static int PORT = 8899;

    //DuBug开关切换(DebugLog需要手动去Utility_lib关闭)
    public static boolean isDebug = true;

    public static List<Activity> activityList = new ArrayList<Activity>();
    public static DisplayImageOptions options;
    public static BackgroundService mBackGroundService; //服务器
    public static String getDeviceId = "";  // 设备ID
    public static String cityLanguage = ""; // 获取当前系统语言版本
    public static String Uid = "";          // 用户的Uid
    public static String Utoken = "";       // 用户的token
    public static int GENDER_MALE = 0;//男
    public static int GENDEWR_FEFALE = 1;//女


    @Override
    public void onCreate() {
        super.onCreate();
        if(!isDebug){
            //fir.im监控
            FIR.init(this);
            //Bugsnag 会将bug发送至邮箱哟
            Bugsnag.init(this);
        }
        initImageLoader(getApplicationContext());
        FacebookSdk.sdkInitialize(this);
        ActiveAndroid.initialize(this);
        getDeviceId = Utility.getUUID(AppBalala.this);
        cityLanguage = getResources().getConfiguration().locale.getCountry();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ActiveAndroid.dispose();
    }

    public static AppBalala getInstance(){
        if (instance == null){
            instance = new AppBalala();
        }
        return instance;
    }

    private void initImageLoader(Context context){
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .memoryCache(new LruMemoryCache(4 * 1024 * 1024))
                .memoryCacheSize(4 * 1024 * 1024)
                        //设置图片保存到本地的参数 [最大宽，最大高，压缩格式，压缩程序，处理位图]
                .discCacheExtraOptions(0, 0, null, 80, null)
                        //当同一个Uri获取不同大小的图片，缓存到内存时，只缓存一个。默认会缓存多个不同的大小的相同图片
                .denyCacheImageMultipleSizesInMemory()
                        //设置缓存文件的名字
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                        //设置图片下载和显示的工作队列排序
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                        //日志
                        //.writeDebugLogs();// Remove for release app
                .build();

        //图片加载参数
        options = new DisplayImageOptions.Builder()
//			.showImageOnLoading(R.drawable.user_no)// 设置图片在下载期间显示的图片
//			.showImageForEmptyUri(R.drawable.user_no)// 设置图片Uri为空或是错误的时候显示的图片
//			.showImageOnFail(R.drawable.user_no)// 设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(false) // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型,默认值——Bitmap.Config.ARGB_8888
                .build();
        ImageLoader.getInstance().init(config);
    }

    /**
     * 完全退出
     */
    public synchronized static void exitApp(Context con) {
        // 结束activity队列中的所有activity
        if (AppBalala.activityList != null) {
            for (Activity ac : AppBalala.activityList) {
                if (!ac.isFinishing()) {
                    ac.finish();
                }
            }
        }
        // 清除通知栏
        ((NotificationManager) con.getSystemService(android.content.Context.NOTIFICATION_SERVICE)).cancelAll();
        System.exit(0);
    }


    public synchronized static void register(Activity activity) {
        for (int i = activityList.size() - 1; i >= 0; i--) {
            Activity ac = activityList.get(i);

            if(activity.getClass().getName() == ac.getClass().getName()){ //存在
                activityList.remove(ac);
                if (!ac.isFinishing()) {
                    ac.finish();
                }
                break;
            }
        }
        activityList.add(activity);
    }

    /** Activity被销毁时，从Activities中移除 */
    public synchronized static void unregister(Activity activity) {
        try {
            if (activityList != null && activityList.size() != 0) {
                activityList.remove(activity);
                if (!activity.isFinishing()) {
                    activity.finish();
                }
            } else {
                DebugLogs.e("UserStatus --- No Activity in pool! unregister");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean isLogined() {
        synchronized(this){
            return m_Logined;
        }
    }

    public synchronized void setLogined(boolean m_Logined) {
        synchronized(this){
            this.m_Logined = m_Logined;
        }
    }

    public static Activity getActivityByName(String name) {

        for (int i = activityList.size() - 1; i >= 0; i--) {
            Activity ac = activityList.get(i);
            if (ac.isFinishing())
                continue;
            if (ac.getClass().getName().indexOf(name) >= 0) {
                return ac;
            }
        }
        return null;
    }
}
