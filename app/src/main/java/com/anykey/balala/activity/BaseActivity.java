package com.anykey.balala.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.anykey.balala.R;
import com.anykey.balala.view.HeaderLayout;

import net.dev.mylib.DebugLogs;
import net.dev.mylib.cache.fileCheanCache.FileCache;
import net.dev.mylib.netWorkUtil.NetWorkUtil;

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
        Intent intent;
        if (android.os.Build.VERSION.SDK_INT > 10) {
            intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
        } else {
            intent = new Intent();
            ComponentName component = new ComponentName("com.android.settings","com.android.settings.WirelessSettings");
            intent.setComponent(component);
            intent.setAction("android.intent.action.VIEW");
        }
        // context.startActivity(intent);
        // Intent intent = new Intent(Settings.ACTION_SETTINGS);
        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // startActivity(intent);
        startActivityForResult(intent, 0);
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
        NetWorkUtil.startNetWorkReceiver(mContext);
        DebugLogs.e(FileCache.getMemoryInfo(mContext));
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

//	protected void initActionBar() {
//		initActionBar(null);
//	}
//
//	protected void initActionBar(String title) {
//		ActionBar actionBar = getActionBar();
//		if (title != null) {
//			actionBar.setTitle(title);
//		}
//		actionBar.setDisplayUseLogoEnabled(false);
//		actionBar.setDisplayHomeAsUpEnabled(true);
//	}
//
//	protected void initActionBar(int id) {
//		initActionBar(HealthApp.ctx.getString(id));
//	}

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

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetWorkUtil.stopNetWorkReceiver(mContext);

    }
}