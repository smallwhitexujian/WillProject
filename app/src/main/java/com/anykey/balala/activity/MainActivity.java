package com.anykey.balala.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.anykey.balala.CommonResultCode;
import com.anykey.balala.R;
import com.anykey.balala.Utils.PictureObtain;
import com.anykey.balala.fragment.BarFragment;
import com.anykey.balala.fragment.DiscoverFragment;
import com.anykey.balala.fragment.MeFragment;
import com.anykey.balala.fragment.MessageFragment;

import net.dev.mylib.ToastUtils;
import net.dev.mylib.cache.sharedPreferences.SharedPreferencesUtil;

/**
 * Created by xujian on 15/8/31.
 * 主界面
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {
    private FragmentManager fragmentManager = null;
    private LinearLayout tabMessage, tabBar, tabDiscover, tabMe;
    private ImageView icon_tabMessage, icon_tabBar, icon_tabDiscover, icon_tabMe;
    private Fragment barFragment, discoverFragment, meFragment, messageFragment;
    private PictureObtain mObtain;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        initView();
        initContent();
        ToastUtils.showToast(mContext, "这是一个测试Toast");
    }

    public void close() {
        if (!this.isFinishing()) {
            finish();
        }
    }

    /**
     * 初始化tab按钮界面
     */
    private void initView() {
        fragmentManager = getSupportFragmentManager();
        tabMessage = (LinearLayout) findViewById(R.id.tabMessage);
        tabBar = (LinearLayout) findViewById(R.id.tabBar);
        tabDiscover = (LinearLayout) findViewById(R.id.tabDiscover);
        tabMe = (LinearLayout) findViewById(R.id.tabMe);
        tabMessage.setOnClickListener(this);
        tabBar.setOnClickListener(this);
        tabDiscover.setOnClickListener(this);
        tabMe.setOnClickListener(this);
        icon_tabMessage = (ImageView) findViewById(R.id.tabMessage_icon);
        icon_tabBar = (ImageView) findViewById(R.id.tabBar_icon);
        icon_tabDiscover = (ImageView) findViewById(R.id.tabDiscover_icon);
        icon_tabMe = (ImageView) findViewById(R.id.tabMe_icon);
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
        FragmentTransaction barTransaction = fragmentManager.beginTransaction();
        barTransaction.add(R.id.contentFrame, barFragment);
        barTransaction.commit();
        FragmentTransaction discoverTransaction = fragmentManager.beginTransaction();
        discoverTransaction.add(R.id.contentFrame, discoverFragment).hide(discoverFragment);
        discoverTransaction.commit();
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

    public void onActivityResult(int requestCode, int resultCode,
                                 final Intent data) {
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
        sendintent.putExtra("filePath", filepath);
        startActivityForResult(sendintent, 1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tabMessage:
                setSelectedMenu(R.id.tabMessage);
                break;
            case R.id.tabBar:
                setSelectedMenu(R.id.tabBar);
                break;
            case R.id.tabDiscover:
                setSelectedMenu(R.id.tabDiscover);
                break;
            case R.id.tabMe:
                setSelectedMenu(R.id.tabMe);
                break;
        }
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
}