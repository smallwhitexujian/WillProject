package com.anykey.balala.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.anykey.balala.AppBalala;
import com.anykey.balala.R;
import com.anykey.balala.view.HeaderLayout;
import com.appsflyer.AppsFlyerLib;
import com.networkbench.agent.impl.NBSAppAgent;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;
/**
 * Created by xujian on 15/8/26.
 * activity基本类。保函有自定义标题栏。其中也有跳转网络设置属性
 */
public class BaseActivity extends FragmentActivity {
    protected Context mContext;
    private View topView;
    private static final int SET_NETWORK = 0;
    protected HeaderLayout headerLayout;

    @Override
    public void setContentView(int layoutResID) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        topView = inflater.inflate(layoutResID, null);
        headerLayout = (HeaderLayout) getTopView().findViewById(R.id.headerLayout);
        super.setContentView(topView);
    }

    @Override
    public void setContentView(View view) {
        topView = view;
        super.setContentView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        topView = view;
        super.setContentView(view, params);
    }

    public View getTopView() {
        return topView;
    }

    public void setNetwork(View view) {
//        Intent intent;
        if (android.os.Build.VERSION.SDK_INT > 13) {     //3.2以上打开设置界面，也可以直接用ACTION_WIRELESS_SETTINGS打开到wifi界面
            startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
        } else {
            startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
        }

    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        finish();
        // overridePendingTransition(R.anim.move_left_in_activity,
        // R.anim.move_right_out_activity);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        // overridePendingTransition(R.anim.move_right_in_activity,
        // R.anim.move_left_out_activity);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        // overridePendingTransition(R.anim.move_right_in_activity,
        // R.anim.move_left_out_activity);
    }

    protected static void alwaysShowMenuItem(MenuItem add) {
        add.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS
                | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mContext = this;
        if (AppBalala.isDebug){
            NBSAppAgent.setLicenseKey("95a69f84178141d7852938a9c67e1509").start(this);
        }else{
            NBSAppAgent.setLicenseKey("8f420af1e407467b98b698cb4fee1a63").start(this);
        }
    }

    public void close() {
        if (!this.isFinishing()) {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void hideSoftInputView() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
                hideSoftInputView();
            }
        }
        return super.onTouchEvent(event);
    }

    protected void setSoftInputMode() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SET_NETWORK) {
//			if (!NetworkUtils.isConnection(this)) {
//				MessageUtils.showToast(R.string.network_no);
//				finish();
//			}
        }
    }


//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        Intent intent = new Intent();
//        intent.setClass(this, StartActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
//        finish();
//    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
    }

    @Override
    public void onStart() {
        super.onStart();
        AppsFlyerLib.onActivityResume(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        AppsFlyerLib.onActivityPause(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        TCAgent.onPause(this);
        MobclickAgent.onPause(this);
        AppsFlyerLib.onActivityPause(this);
    }

    @Override
    protected void onResume() {
        //解决系统字体大小修改布局错乱的问题
        Resources resource = getResources();
        Configuration c = resource.getConfiguration();
        c.fontScale = 1.0f;
        resource.updateConfiguration(c, resource.getDisplayMetrics());
        super.onResume();
        TCAgent.onResume(this);
        MobclickAgent.onResume(this);
        AppsFlyerLib.onActivityResume(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }
}