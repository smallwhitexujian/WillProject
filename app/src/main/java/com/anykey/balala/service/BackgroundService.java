package com.anykey.balala.service;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.view.WindowManager;

import com.anykey.balala.AppBalala;
import com.anykey.balala.CommonUrlConfig;
import com.anykey.balala.Constant;
import com.anykey.balala.GlobalDef;
import com.anykey.balala.R;
import com.anykey.balala.Socket.Process.RoomLoginProcess;
import com.anykey.balala.Utils.DownloadUtil;
import com.anykey.balala.Utils.NotificationUtil;
import com.anykey.balala.activity.ChatRoomActivity;
import com.anykey.balala.activity.LoginActivity;
import com.anykey.balala.activity.MainActivity;
import com.anykey.balala.fragment.MeFragment;
import com.anykey.balala.model.CommonModel;
import com.anykey.balala.model.GiftXmlModel;
import com.anykey.balala.model.NotificationConfig;
import com.anykey.balala.receiver.AppBroadcastReceiver;
import com.anykey.balala.work.ParseXmlWork;
import com.facebook.AccessToken;

import net.dev.mylib.DebugLogs;
import net.dev.mylib.JsonUtil;
import net.dev.mylib.Utility;
import net.dev.mylib.cache.fileCheanCache.FileUtil;
import net.dev.mylib.cache.sharedPreferences.SharedPreferencesUtil;
import net.dev.mylib.netWorkUtil.HttpPostFile;
import net.dev.mylib.netWorkUtil.NetWorkUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import socket.lib.process.CoreProxy;
import socket.lib.util.ByteUtil;

/**
 * Created by xujian on 15/8/27.
 * 后台service服务
 */
public class BackgroundService extends Service {
    public static final String KEY_LOGIN_PARAM = "login_param";
    public static final String KEY_SEND_PARCEL = "send_parcel";
    public static final String KEY_PROBE = "probe";
    public static final String CMD_LOGIN_IM_ACTION = "CMD_LOGIN_IM_ACTION";
    public static final String CMD_SEND_PARCEL_ACTION = "CMD_SEND_PARCEL_ACTION";
    public static final String CMD_HEARTBEAT_ACTION = "CMD_HEARTBEAT_ACTION";
    public static final String CMD_TIME_PROBE = "CMD_TIME_PROBE";
    public static final String CMD_TEST_ACTION = "CMD_TEST_ACTION";
    public static final String CMD_DOWNLOAD_XML = "CMD_DOWNLOAD_XML_ACTION";

    private MyBinder myBinder = new MyBinder();
    private RoomLoginProcess loginProcess;
    private CoreProxy proxy;
    private Handler mRoomHandler = null;
    private SharedPreferencesUtil sp;
    private AppBroadcastReceiver appBroadcastReceiver;
    private IMSocketFunction mIMSocketFunction;

    private Handler mBackgroundHander = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mRoomHandler.obtainMessage(msg.what, msg.arg1, msg.arg2, msg.obj).sendToTarget();
            switch (msg.what) {
                case GlobalDef.SO_DOHEART:
                    break;
            }
        }
    };

    public class MyBinder extends Binder {
        public BackgroundService getBackgroundService() {
            return BackgroundService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sp = SharedPreferencesUtil.getInstance(this);
        mIMSocketFunction = new IMSocketFunction(this);
        //接收广播
        appBroadcastReceiver = new AppBroadcastReceiver(new AppBroadcastReceiver.BroadcastCallback() {
            @Override
            public void onHandle(String action, final Context context, Intent intent) {
                switch (action) {
                    //主动退出
                    case AppBroadcastReceiver.BROADCAST_USER_LOGOUT:
                        logout();
                        break;
                    case AppBroadcastReceiver.NETWORK_CHANGE:
                        String token = sp.getToken();
                        String userid = sp.getUserId();
                        if (token == null || userid == null) {
                            return;
                        }
                        if (!NetWorkUtil.isConnected(BackgroundService.this)) {
                            DebugLogs.e("jjfly net work change lost......");
                            mIMSocketFunction.stopConnect();
                        } else {
                            DebugLogs.e("jjfly net work change connect......");
                            mIMSocketFunction.startConnect();
                        }
                        break;
                    case CMD_LOGIN_IM_ACTION:
                        mIMSocketFunction.startConnect();
                        break;
                    case CMD_SEND_PARCEL_ACTION:
                        byte[] parcel = intent.getByteArrayExtra(KEY_SEND_PARCEL);
                        DebugLogs.e("jjfly send message " + ByteUtil.bytes2Hex(parcel));
                        if (parcel != null) {
                            mIMSocketFunction.sendParcel(parcel);
                        }
                        break;
                    //被踢下线
                    case AppBroadcastReceiver.BROADCAST_USER_LOGOUT_S:
                        AlertDialog.Builder b = new AlertDialog.Builder(context);
                        b.setMessage(R.string.login_err);
                        b.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        logout();
                                    }
                                }
                        );
                        b.setCancelable(false);
                        AlertDialog d = b.create();
                        d.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                        d.show();
                        break;
                    case CMD_HEARTBEAT_ACTION:
                        mIMSocketFunction.doHearbeat();
                        break;
                    case CMD_TIME_PROBE:
                        DebugLogs.e("jjfly----------------" + intent.getBooleanExtra(KEY_PROBE, false));
                        if (intent.getBooleanExtra(KEY_PROBE, false)) {
                            mIMSocketFunction.startProbe();
                        } else {
                            mIMSocketFunction.stopProbe();
                        }
                    case CMD_DOWNLOAD_XML:
                        DebugLogs.e("----CMD_DOWNLOAD_XML------->");
                        String url = intent.getStringExtra(Constant.XML_URL_KEY);
                        final String filePath = intent.getStringExtra(Constant.XML_FILE_PATH_KEY);
                        final int fileType = intent.getIntExtra(Constant.XML_FILE_TYPE_KEY, 1);
                        if (url != null && url.length() > 0 && filePath != null && filePath.length() > 0) {
                            DownloadUtil.getInstance().goDownload(true, url, filePath, new DownloadUtil.DownloadCallback() {
                                @Override
                                public void beforeDownload() {

                                }

                                @Override
                                public void onProgress(int progress) {

                                }

                                @Override
                                public void afterDownload(boolean status) {
                                    if (status) {
                                        switch (fileType) {
                                            case 1:
                                                ParseXmlWork.getInstance().parseDiffXml(context, filePath, 1);
                                                break;
                                            case 2:
                                                DebugLogs.e("load gift new xml filePath-->" + filePath);
                                                boolean isParseOk = ParseXmlWork.getInstance().parseDiffXml(context, filePath, 2);
                                                if (isParseOk && !Utility.getEveryDayOpenFirst(context)) {
                                                    for (Map.Entry<String, GiftXmlModel> entry : AppBalala.giftMap.entrySet()) {
                                                        GiftXmlModel value = entry.getValue();
                                                        String urlStr = value.getImageURL();
                                                        if (urlStr != null) {
                                                            DownloadUtil.getInstance().goDownload(false, urlStr, AppBalala.giftFilePath + File.separator + FileUtil.convertUrlToFileName(urlStr), null);
                                                        }
                                                    }
                                                }
                                                break;
                                            case 3:
                                                ParseXmlWork.getInstance().parseDiffXml(context, filePath, 3);
                                                break;
                                        }
                                    }
                                }
                            });
                        }
                        break;
                    case CMD_TEST_ACTION:
                        break;
                }
            }
        });
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppBroadcastReceiver.BROADCAST_USER_LOGOUT);
        filter.addAction(AppBroadcastReceiver.NETWORK_CHANGE);
        filter.addAction(CMD_SEND_PARCEL_ACTION);
        filter.addAction(CMD_LOGIN_IM_ACTION);
        filter.addAction(CMD_HEARTBEAT_ACTION);
        filter.addAction(CMD_DOWNLOAD_XML);
        filter.addAction(CMD_TEST_ACTION);
        filter.addAction(AppBroadcastReceiver.BROADCAST_USER_LOGOUT_S);

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(BackgroundService.this);
        lbm.registerReceiver(appBroadcastReceiver, filter);
        registerReceiver(appBroadcastReceiver, filter);
    }

    /**
     * 返回到登陆界面
     */
    public void logout() {
        SharedPreferencesUtil.getInstance(getApplicationContext()).clearUserInfo();
        AccessToken.setCurrentAccessToken(null);
        MeFragment.isLoad = false;
        Intent loginActivity = new Intent();
        loginActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        loginActivity.setClass(getApplicationContext(), LoginActivity.class);
        startActivity(loginActivity);
        if (AppBalala.isOnHook) {
            AppBalala.isOnHook = false;
            ChatRoomActivity.getInstance().roomfinish();
            AppBalala.mChatlines.clear();
        }
        AppBalala.userinfomodel = null;
        MainActivity.instance.close();
        mIMSocketFunction.stopConnect();
    }


    /**
     * 上传照片
     *
     * @paramtype 上传图片类型
     * @paramid Id
     * @paramimageUrl 上传图片地址
     * @paramLocalImage 本地资源图片
     */
    public void UpPicture(final String type, final String id, final String LocalImage, final String pathString, final String Uid, final String Utoken) {
        new Thread() {
            public void run() {
                HashMap<String, String> params = new HashMap<>();
                params.put("userid", Uid);
                params.put("token", Utoken);
                params.put("type", type);
                params.put("id", id);
                HashMap<String, String> fileparams = new HashMap<>();
                fileparams.put("imageurl", LocalImage);//本地路径
                try {
                    String str = HttpPostFile.uploadFile(pathString, params, fileparams);
                    CommonModel results = JsonUtil.fromJson(str, CommonModel.class);
                    if (results != null && !results.code.equals(CommonUrlConfig.RequestState.OK)) {
                        Intent mydynamincActivity = new Intent(getApplicationContext(), MainActivity.class);
                        int requestCode = NotificationUtil.MSG_REQUEST_CODE;
                        int flag = PendingIntent.FLAG_CANCEL_CURRENT;
                        PendingIntent sPendingIntent = PendingIntent.getActivity(getApplicationContext(), requestCode, mydynamincActivity, flag);
                        NotificationUtil.launchNotify(NotificationConfig.getDefault(getApplicationContext(), sPendingIntent, requestCode, getString(R.string.notification_new_message), getString(R.string.upload_fail)));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        DebugLogs.e("后台服务被调用");
    }


    @Override
    public IBinder onBind(Intent intent) {
        DebugLogs.v("BackgroundService---->onBind");
        return myBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        DebugLogs.v("BackgroundService---->onRebind");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        DebugLogs.v("BackgroundService---->onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(appBroadcastReceiver);
    }

    public void setRoomHander(Handler hander) {
        this.mRoomHandler = hander;
    }

    /**
     * 登录房间
     *
     * @param host   服务器地址
     * @param Port   服务器端口
     * @param BarId  房间ID
     * @param UserId 用户ID
     */
    public void startRoomConnection(String host, int Port, int BarId, int UserId, String token) {
        DebugLogs.e("-----startRoomConnection");
        loginProcess = new RoomLoginProcess(mBackgroundHander, host, Port, BarId, UserId, token);
        proxy = new CoreProxy();
        proxy.startProcess(loginProcess);
    }

    /**
     * 停掉bar代理
     */
    public void quitRoom() {
        if (proxy != null) {
            proxy.stopProcess();
            proxy.setReconnection(false);
        }
    }

    /**
     * 公用拼包
     *
     * @param typeValue 包的操作码
     * @param jsonStr   发送的内容 json字符窜
     */
    public void sendMessage(int typeValue, String jsonStr) {
        loginProcess.sendMessage(proxy, typeValue, jsonStr);
    }

    /**
     * *********************
     * 外部socket统一通过这个接口发包
     * ****************************************
     */
    public boolean sendParcel(byte[] msg) {
        return mIMSocketFunction.sendParcel(msg);
    }


}
