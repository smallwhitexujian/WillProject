package com.anykey.balala.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.anykey.balala.AppBalala;
import com.anykey.balala.CommonResultCode;
import com.anykey.balala.CommonUrlConfig;
import com.anykey.balala.R;
import com.anykey.balala.Socket.Process.RouterProcess;
import com.anykey.balala.Socket.Process.WillOutProtocol;
import com.anykey.balala.Socket.Process.WillProtocol;
import com.anykey.balala.Utils.FaceUtils;
import com.anykey.balala.Utils.PictureObtain;
import com.anykey.balala.fragment.BarFragment;
import com.anykey.balala.fragment.DiscoverFragment;
import com.anykey.balala.fragment.MeFragment;
import com.anykey.balala.fragment.MessageFragment;
import com.anykey.balala.model.SystemNotificationModel;
import com.anykey.balala.receiver.LocalRouterReceiver;
import com.networkbench.agent.impl.NBSAppAgent;

import net.dev.mylib.JsonUtil;
import net.dev.mylib.Utility;
import net.dev.mylib.cache.fileCheanCache.FileUtil;
import net.dev.mylib.cache.sharedPreferences.SharedPreferencesUtil;
import net.dev.mylib.netWorkUtil.GetJson;
import net.dev.mylib.upLoadApp.UploadApp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by xujian on 15/8/31.
 * 主界面
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {
    private SharedPreferencesUtil sp;
    private ImageView icon_tabMessage, icon_tabBar, icon_tabDiscover, icon_tabMe;
    private Fragment barFragment, discoverFragment, meFragment, messageFragment;
    private TextView tabMessage_str, tabBar_str, tabDiscover_str, tabMe_str;
    private LinearLayout tabMessage, tabBar, tabDiscover, tabMe;
    private FragmentManager fragmentManager = null;
    public static MainActivity instance = null;
    private LocalRouterReceiver receiver;
    private PictureObtain mObtain;
    private ImageView mNewDot;
    private String versionCode = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        sp = SharedPreferencesUtil.getInstance(mContext);
        versionCode = Utility.getVersionCode(MainActivity.this);
        initView();
        initContent();
        instance = this;
        if (sp.getIsCancel()){
            return;
        }else {
            upApk();
        }
        if (AppBalala.isDebug){//测试
            NBSAppAgent.setLicenseKey("95a69f84178141d7852938a9c67e1509").withLocationServiceEnabled(true).start(this);
        }else{
            NBSAppAgent.setLicenseKey("8f420af1e407467b98b698cb4fee1a63").withLocationServiceEnabled(true).start(this);
        }
    }



    @Override
    protected void onSaveInstanceState(Bundle bundle) {
//        super.onSaveInstanceState(bundle);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 初始化tab按钮界面
     */
    private void initView() {
        fragmentManager = getSupportFragmentManager();
        tabDiscover = (LinearLayout) findViewById(R.id.tabDiscover);
        tabMessage = (LinearLayout) findViewById(R.id.tabMessage);
        tabBar = (LinearLayout) findViewById(R.id.tabBar);
        tabMe = (LinearLayout) findViewById(R.id.tabMe);

        icon_tabMessage = (ImageView) findViewById(R.id.tabMessage_icon);
        icon_tabBar = (ImageView) findViewById(R.id.tabBar_icon);
        icon_tabDiscover = (ImageView) findViewById(R.id.tabDiscover_icon);
        icon_tabMe = (ImageView) findViewById(R.id.tabMe_icon);

        tabMessage_str = (TextView) findViewById(R.id.tabMessage_str);
        tabBar_str = (TextView) findViewById(R.id.tabBar_str);
        tabDiscover_str = (TextView) findViewById(R.id.tabDiscover_str);
        tabMe_str = (TextView) findViewById(R.id.tabMe_str);

        tabMessage.setOnClickListener(this);
        tabBar.setOnClickListener(this);
        tabDiscover.setOnClickListener(this);
        tabMe.setOnClickListener(this);
        if (AppBalala.faceModelList.isEmpty()) {
            FaceUtils.loadFaceXml(getApplication());
        }
        AppBalala.isSound = sp.getBoolean("isSound", false);
        mNewDot = (ImageView) findViewById(R.id.new_dot);
    }

    /**
     * 初始化Fragment;(可以选择当前那个页面显示)
     */
    private void initContent() {
        mObtain = new PictureObtain();
        barFragment = new BarFragment();
        discoverFragment = new DiscoverFragment();
        meFragment = new MeFragment();
        messageFragment = new MessageFragment();

        FragmentTransaction discoverTransaction = fragmentManager.beginTransaction();
        discoverTransaction.add(R.id.contentFrame, discoverFragment).hide(discoverFragment);
        discoverTransaction.commit();

        FragmentTransaction barTransaction = fragmentManager.beginTransaction();
        barTransaction.add(R.id.contentFrame, barFragment).show(barFragment);
        barTransaction.commit();
        tabBar_str.setTextColor(getResources().getColor(R.color.main_red));
        icon_tabBar.setImageResource(R.drawable.bar_icon_on);

        FragmentTransaction meTransaction = fragmentManager.beginTransaction();
        meTransaction.add(R.id.contentFrame, meFragment).hide(meFragment);
        meTransaction.commit();

        FragmentTransaction messageTransaction = fragmentManager.beginTransaction();
        messageTransaction.add(R.id.contentFrame, messageFragment).hide(messageFragment);
        messageTransaction.commit();
    }

    /**
     * 隐藏
     *
     * @param transaction
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (barFragment != null) {
            transaction.hide(barFragment);
        }
        if (discoverFragment != null) {
            transaction.hide(discoverFragment);
        }
        if (meFragment != null) {
            transaction.hide(meFragment);
        }
        if (messageFragment != null) {
            transaction.hide(messageFragment);
        }
    }


    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (resultCode == RESULT_OK) {
            String path;
            switch (requestCode) {
                case CommonResultCode.SET_ADD_PHOTO_CAMERA:
                    path = SharedPreferencesUtil.getInstance(this).getString("picUri", "");
                    getNewPhoto(path);
                    break;
                case CommonResultCode.SET_ADD_PHOTO_ALBUM:
                    Uri uri = data.getData();
                    path = mObtain.getRealPathFromURI(this, uri);
                    getNewPhoto(path);
                    break;
                case CommonResultCode.SELECT_BAR_CODE:
                    selectBar();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 拍照或者选择照片后
     */
    private void getNewPhoto(String filepath) {
        Intent sendintent = new Intent(this, SendDynamicActivity.class);
        sendintent.putExtra("type", "dynamic");
        sendintent.putExtra("filePath", filepath);
        startActivityForResult(sendintent, CommonResultCode.SEND_DYNAMIC_CODE);
    }

    public void selectBar() {
        setClearColor();
        setSelectedMenu(R.id.tabBar);
        tabBar_str.setTextColor(getResources().getColor(R.color.main_red));
        icon_tabBar.setImageResource(R.drawable.bar_icon_on);
    }

    @Override
    public void onClick(View v) {
        setClearColor();
        switch (v.getId()) {
            case R.id.tabMessage:
                setSelectedMenu(R.id.tabMessage);
                tabMessage_str.setTextColor(getResources().getColor(R.color.main_red));
                icon_tabMessage.setImageResource(R.drawable.message_icon_on);
                break;
            case R.id.tabBar:
                selectBar();
                break;
            case R.id.tabDiscover:
                setSelectedMenu(R.id.tabDiscover);
                tabDiscover_str.setTextColor(getResources().getColor(R.color.main_red));
                icon_tabDiscover.setImageResource(R.drawable.discover_icon_on);
                discoverFragment.onResume();
                break;
            case R.id.tabMe:
                setSelectedMenu(R.id.tabMe);
                tabMe_str.setTextColor(getResources().getColor(R.color.main_red));
                icon_tabMe.setImageResource(R.drawable.me_icon_on);
                break;

        }
    }

    private void setClearColor() {
        tabMessage_str.setTextColor(getResources().getColor(R.color.font_grey));
        tabBar_str.setTextColor(getResources().getColor(R.color.font_grey));
        tabDiscover_str.setTextColor(getResources().getColor(R.color.font_grey));
        tabMe_str.setTextColor(getResources().getColor(R.color.font_grey));
        icon_tabBar.setImageResource(R.drawable.bar_icon);
        icon_tabMe.setImageResource(R.drawable.me_icon);
        icon_tabMessage.setImageResource(R.drawable.message_icon);
        icon_tabDiscover.setImageResource(R.drawable.discover_icon);
    }

    public void setSelectedMenu(int viewid) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideFragments(transaction);
        switch (viewid) {
            case R.id.tabMessage:
                if (messageFragment == null) {
                    messageFragment = new MessageFragment();
                    transaction.add(R.id.contentFrame, messageFragment);
                } else {
                    messageFragment.onResume();
                    transaction.show(messageFragment);
                }
                break;
            case R.id.tabBar:
                if (barFragment == null) {
                    barFragment = new BarFragment();
                    transaction.add(R.id.contentFrame, barFragment);
                } else {
                    barFragment.onResume();
                    transaction.show(barFragment);
                }
                break;
            case R.id.tabDiscover:
                if (discoverFragment == null) {
                    discoverFragment = new DiscoverFragment();
                    transaction.add(R.id.contentFrame, discoverFragment);
                } else {
                    discoverFragment.onResume();
                    transaction.show(discoverFragment);
                }
                break;
            case R.id.tabMe:
                if (meFragment == null) {
                    meFragment = new MeFragment();
                    transaction.add(R.id.contentFrame, meFragment);
                } else {
                    meFragment.onResume();
                    transaction.show(meFragment);

                }
                break;
            default:
                break;
        }
        transaction.commitAllowingStateLoss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver = new LocalRouterReceiver(new LocalRouterReceiver.RouterCallback() {
            @Override
            public void onHandle(byte[] parcel,Object obj) {
                int type = new WillProtocol().getType(parcel);
                byte[] msgByte = new WillProtocol().getData(parcel);
                String msgStr = new String(msgByte).trim();
                if (type == WillOutProtocol.BROADCAST_TYPE_VALUE) {//接受喇叭
                    SystemNotificationModel.SystemBroadcast commonData = JsonUtil.fromJson(msgStr, SystemNotificationModel.SystemBroadcast.class);
                    if (commonData.code == 4) {//系统升级消息
                        String str = commonData.msg;
                        int VersionCode = Integer.valueOf(str.split("%-%")[0]);
                        String message = String.valueOf(str.split("%-%")[1]);
                        int force = commonData.data.force;
                        String AppURL = commonData.data.url;
                        if (Integer.valueOf(versionCode) < VersionCode){
                            UploadApp uploadApp = new UploadApp(FileUtil.getSDCardDir(MainActivity.this, AppBalala.FILEPATH_UPAPK));
                            uploadApp.showUpApk(MainActivity.this, message, AppURL, force);
                            if (force == 1){
                                sp.saveUpLoadApk(true,String.valueOf(VersionCode),message,AppURL);
                            }
                        }
                    }
                }
            }
        });
        IntentFilter filter = new IntentFilter();
        filter.addAction(RouterProcess.ACTION_ROUTER_PARCEL);
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(receiver, filter);
        if (!sp.getApkInfo_version().equals("")){
            int ApkVersion = Integer.valueOf(sp.getApkInfo_version());
            if (ApkVersion > Integer.valueOf(versionCode)){//检测有强制升级
                UploadApp uploadApp = new UploadApp(FileUtil.getSDCardDir(MainActivity.this, AppBalala.FILEPATH_UPAPK));
                uploadApp.showUpApk(MainActivity.this, sp.getApkInfo_message(), sp.getApkInfo_Url(), 1);
            }
        }
    }

    private void upApk() {
        try {
            HashMap<String,String> params = new HashMap<>();
            params.put("version",versionCode);
            params.put("os","2");//2.表示Android 。1表示IOS
            GetJson.Callback callback =new GetJson.Callback() {
                @Override
                public void onFinish(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String data = jsonObject.getString("data");
                        JSONObject json = new JSONObject(data);
                        String AppURL = json.getString("AppURL");
                        String Content = json.getString("Content");
                        String apkVersion = json.getString("AppVersion");
                        int isUp = Integer.valueOf(json.getString("isUp"));
                        if (isUp == 1){
                            sp.saveUpLoadApk(true,apkVersion,Content,AppURL);
                        }
                        if(Integer.valueOf(apkVersion) > Integer.valueOf(versionCode)){
                            UploadApp uploadApp = new UploadApp(FileUtil.getSDCardDir(MainActivity.this, AppBalala.FILEPATH_UPAPK));
                            uploadApp.showUpApk(MainActivity.this, Content, AppURL, isUp);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(VolleyError error) {

                }
            };
            GetJson getJson = new GetJson(MainActivity.this,callback);
            getJson.setConnection(Request.Method.GET, CommonUrlConfig.apkUp,params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(receiver);
        super.onPause();
    }

    //显示消息小红点
    public void setDot(int count) {
        if (count > 0) {
            mNewDot.setVisibility(View.VISIBLE);
        } else {
            mNewDot.setVisibility(View.GONE);
        }
    }
}