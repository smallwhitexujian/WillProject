package com.anykey.balala.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.anykey.balala.AppBalala;
import com.anykey.balala.GlobalDef;
import com.anykey.balala.R;
import com.anykey.balala.adapter.CommonAdapter;
import com.anykey.balala.adapter.ViewHolder;
import com.anykey.balala.model.DynamicModel;

import net.dev.mylib.DebugLogs;
import net.dev.mylib.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xujian on 15/9/11.
 * 聊天抽离
 */
public class ChatLine extends BaseFragment{
    private View rootview;
    private ListView chatline;
    private CommonAdapter mAdapter;
    private List<DynamicModel> mDatas;
    public Handler getMsgHandler(){
        return uiHandler;
    }
    private Handler uiHandler = new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case GlobalDef.WM_ROOM_LOGIN_SUCCESS://登陆房间
                    break;
                case GlobalDef.WM_ROOM_SEND_MESSAGE://发送聊天消息

                    break;
                case GlobalDef.WM_ROOM_RECEIVE_MESSAGE://接受服务器消息

                    break;
            }
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_chatline,null);
        chatline = (ListView)rootview.findViewById(R.id.chatline);
        return rootview;
    }
}
