package com.anykey.balala.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.anykey.balala.view.HeaderLayout;

/**
 * Created by xujian on 15/8/26.
 * fragment基本类。
 */
public class BaseFragment extends Fragment{
    private HeaderLayout headerLayout;
    public Context mcontext;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mcontext = getActivity();
    }
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
