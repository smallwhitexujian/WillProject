package com.anykey.balala.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anykey.balala.R;

/**
 * Created by xujian on 15/8/31.
 * Message fragment
 */
public class MessageFragment extends Hintfragment{
    private View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_message,null);
        return rootView;
    }

    /**
     * 懒加载，看到这个界面则加载
     */
    @Override
    protected void lazyLoad() {

    }
}
