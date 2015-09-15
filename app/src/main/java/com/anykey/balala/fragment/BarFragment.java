package com.anykey.balala.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.anykey.balala.R;
import com.anykey.balala.Utils.ShareFacebook;
import com.anykey.balala.activity.ChatRoom;
import com.anykey.balala.activity.MainActivity;

import net.dev.mylib.DebugLogs;
import net.dev.mylib.ToastUtils;

/**
 * Created by xujian on 15/8/31.
 * Bar fragment. chatRoom
 */
public class BarFragment extends Hintfragment implements View.OnClickListener{
    private View rootView;
    private Button shareButton,sendMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_bar,null);
        shareButton = (Button)rootView.findViewById(R.id.shareButton);
        sendMessage = (Button)rootView.findViewById(R.id.sendMessage);
        sendMessage.setOnClickListener(this);
        shareButton.setOnClickListener(this);
        return rootView;
    }


    /**
     * 懒加载，看到这个界面则加载
     */
    @Override
    protected void lazyLoad() {
        DebugLogs.e("--BarFragment--lazyLoad----->");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.shareButton:
                ToastUtils.showToast(getActivity(),"shareButton");
                MainActivity parentActivity = (MainActivity) getActivity();
                ShareFacebook shareFacebook = new ShareFacebook(mcontext,parentActivity);
                shareFacebook.postStatusUpdate();
                break;
            case R.id.sendMessage:
                Intent intent = new Intent(getActivity(), ChatRoom.class);
                getActivity().startActivity(intent);
                break;
        }
    }
}
