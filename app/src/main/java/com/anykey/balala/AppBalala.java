package com.anykey.balala;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.multidex.MultiDex;

import com.activeandroid.ActiveAndroid;
import com.anykey.balala.activity.ChatRoomActivity;
import com.anykey.balala.model.BaseXmlModel;
import com.anykey.balala.model.ChatLineModel;
import com.anykey.balala.model.ErrorXmlModel;
import com.anykey.balala.model.FaceModel;
import com.anykey.balala.model.GiftXmlModel;
import com.anykey.balala.model.OnlineListModel;
import com.anykey.balala.model.UserinfoModel;
import com.anykey.balala.service.BackgroundService;
import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.appsflyer.DebugLogQueue;
import com.facebook.FacebookSdk;
import com.tendcloud.tenddata.TCAgent;

import net.dev.mylib.DebugLogs;
import net.dev.mylib.Utility;
import net.dev.mylib.cache.fileCheanCache.FileUtil;
import net.dev.mylib.cache.imageCache.ImageCache;
import net.dev.mylib.loaderimage.ImageFileLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xujian on 15/8/26.
 * Application 全局变量
 */
public class AppBalala extends Application {
    public static String SDCARD_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String FILEPATH_ROOT = "Balala";
    public static final String FILEPATH_CACHE = FILEPATH_ROOT + File.separator + "cache";
    public static final String FILEPATH_VOICE = FILEPATH_ROOT + File.separator + "voice";
    public static final String FILEPATH_UPAPK = FILEPATH_ROOT + File.separator + "upapk";
    public static final String FILEPATH_CAMERA = FILEPATH_ROOT + File.separator + "camera";
    public static final String FILEPATH_VOICE_RECORD = FILEPATH_VOICE + File.separator + "record";
    public static String XMLDirectory = "balala.xml";

    //DeBug开关切换(DebugLog需要手动去Utility_lib关闭)
    public static boolean isDebug = true;
    public static String getDeviceId = "";                                          // 设备ID
    public static String cityLanguage = "";                                         // 获取当前系统语言版本
    public static long Coin = 0;                                                    // 刷新币

    public static long Diamonds = 0;                                                // 刷新钻石

    public static int GENDER_MALE = 1;                                              // 男
    public static int GENDEWR_FEFALE = 0;                                           // 女

    public static String MAC;                                                       // 唯一识别
    public static int versionCode;                                                  // 版本编号
    public static String versionName;                                               // 版本名称
    public static String phoneBRAND;                                                // 手机类型
    public static UserinfoModel userinfomodel = null;
    //声音设置
    public static boolean isSound = false;                                          // false 表示禁音，true 表示有声音
    //房间 -----
    public static int BarId = 0;                                                    // 房间ID
    public static boolean isOnHook = false;                                         // 判断是或否挂机
    public static ChatRoomActivity chatroomApplication = null;                      // 保持ChatRoom存在
    public static List<FaceModel> faceModelList = new ArrayList<>();                // 默认表情list
    public static Map<String, FaceModel> faceHotKeyMap = new HashMap<>();           // 表情map，通过热键找表情类
    public static Map<String, FaceModel> facePathMap = new HashMap<>();             // 表情map，通过文件名找表情类
    public static Map<Integer, String> facesMap = null;                             // 表情map，Integer为文件id，String为热键
    public static Map<String, FaceModel> allFaceHotKeyMap = new HashMap<>();        // 非默认表情map，通过热键找中文解说
    public static List<HashMap<String, String>> runTextList = new ArrayList<>();    // 跑马灯数据存储
    public static ArrayList<ChatLineModel> mChatlines = new ArrayList<>();          // 房间数据存储
    public static List<OnlineListModel> onlineListDatas = new ArrayList<>();        // 房间在线人数列表
    public static List<OnlineListModel.MemberApplication> Membermodels = new ArrayList<>();          // 申请列表

    public static String currentUI = "";                                            // 当前界面
    public static List<BaseXmlModel> baseXmlModelList = new ArrayList<>();// 总的配置map
    public static Map<String, GiftXmlModel> giftMap = new HashMap<>();                       // 礼物map
    //基础配置文件路径
    public static String baseConfigPath = null;
    //礼物配置文件路径
    public static String giftConfigPath = null;
    public static String giftFilePath = null;
    //http error alert 2015 11/11 cbl
    public static List<ErrorXmlModel> alertMap = new ArrayList<>();
    public static String alertConfigPath = null;
    public static boolean IMLoginStatus = false;

    public static ImageCache imageCache;
    public static ImageFileLoader imageFileLoader;
    public static final long SystemAssistantID = 10000;
    //分享
    public static String shareTitle = null;
    public static String shareContent = null;
    public static String shareURL = null;

    @Override
    public void onCreate() {
        super.onCreate();
        baseConfigPath = getFilesDir() + "/balala.xml";
        giftConfigPath = getFilesDir() + "/item.xml";
        alertConfigPath = getFilesDir() + "/alert.xml";
        giftFilePath = getFilesDir() + "/item";
        imageCache = ImageCache.getInstance();
        imageFileLoader = ImageFileLoader.getInstance();
        imageCache.setCacheDir(FileUtil.getSDCardDir(this, FILEPATH_CACHE));
        getDeviceId = Utility.getUUID(AppBalala.this);
        cityLanguage = getResources().getConfiguration().locale.getCountry();
        startBackgroudService();

        new Thread(new Runnable() {
            @Override
            public void run() {
                TCAgent.init(getApplicationContext());
                TCAgent.setReportUncaughtExceptions(true);

                AppsFlyerLib.setCurrencyCode("USD");
                AppsFlyerLib.setAppsFlyerKey("10a4cb0e-8ce5-4b65-8114-40077157611f");
                AppsFlyerLib.sendTracking(getApplicationContext());

                FacebookSdk.sdkInitialize(getApplicationContext());
                ActiveAndroid.initialize(getApplicationContext());

                AppsFlyerLib.registerConversionListener(getApplicationContext(), new AppsFlyerConversionListener() {
                    public void onInstallConversionDataLoaded(Map<String, String> conversionData) {
                        DebugLogQueue.getInstance().push("\nGot conversion data from server");
                        for (String attrName : conversionData.keySet()) {
                            DebugLogs.d("attribute: " + attrName + " = " + conversionData.get(attrName));
                        }
                    }

                    public void onInstallConversionFailure(String errorMessage) {
                        DebugLogs.d("error getting conversion data: " + errorMessage);
                    }

                    public void onAppOpenAttribution(Map<String, String> attributionData) {
                        printMap(attributionData);
                    }

                    public void onAttributionFailure(String errorMessage) {
                        DebugLogs.d("error onAttributionFailure : " + errorMessage);

                    }

                    private void printMap(Map<String, String> map) {
                        for (String key : map.keySet()) {
                            DebugLogs.d(key + "=" + map.get(key));
                        }
                    }
                });
            }
        }).start();

        // Add code to print out the key hash
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo("com.anykey.balala", PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                DebugLogs.d("KeyHash:" + Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ActiveAndroid.dispose();
    }

    private void startBackgroudService() {
        Intent i = new Intent(this, BackgroundService.class);
        startService(i);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
