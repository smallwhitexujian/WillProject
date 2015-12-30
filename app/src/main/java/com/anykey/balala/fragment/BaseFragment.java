package com.anykey.balala.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.anykey.balala.view.HeaderLayout;
import com.umeng.analytics.MobclickAgent;

import java.lang.reflect.Field;

/**
 * Created by xujian on 15/8/26.
 * fragment基本类。
 */
public class BaseFragment extends Fragment {
    protected HeaderLayout headerLayout;
    public Context mContext;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public void onPause() {
        MobclickAgent.onPause(getActivity());
        super.onPause();
    }

    @Override
    public void onResume() {
        MobclickAgent.onResume(getActivity());       //统计时长

        super.onResume();
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    //repair bug:java.lang.IllegalStateException: Activity has been destroyed
    public void onDestroyView() {
        super.onDestroyView();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);


        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
