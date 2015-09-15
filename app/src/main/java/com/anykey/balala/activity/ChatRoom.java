package com.anykey.balala.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.Button;

import com.anykey.balala.AppBalala;
import com.anykey.balala.GlobalDef;
import com.anykey.balala.R;
import com.anykey.balala.Socket.Process.RoomLoginProcess;
import com.anykey.balala.service.BackgroundService;

import net.dev.mylib.DebugLogs;


/**
 * Created by xujian on 15/9/11.
 * chatRoom 房间聊天
 */
public class ChatRoom extends BaseActivity implements View.OnClickListener{
    public static BackgroundService bindService = null;
    private Button sendChat;
    private RoomLoginProcess roomProcess;

    private Handler mHander = new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case GlobalDef.WM_ROOM_LOGIN_SUCCESS://登陆房间
                    bindService.startRoomConnection(AppBalala.HOST,AppBalala.PORT,1,10001);
                    break;
                case GlobalDef.WM_ROOM_SEND_MESSAGE://发送聊天消息
                    bindService.sendChatMessage("{\"Code\":0,\"From\":{\"Uid\":1231,\"Name\":\"asdgfhjg\"}}");
                    break;
                case GlobalDef.WM_ROOM_RECEIVE_MESSAGE://接受服务器消息
                    DebugLogs.e("----------->接受服务器返回"+msg.obj);
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        sendChat = (Button)findViewById(R.id.sendChat);
        sendChat.setOnClickListener(this);
        bindService();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unBindService();
    }

    /**
     * 绑定服务
     */
    private void bindService(){
        Intent intent = new Intent(ChatRoom.this, BackgroundService.class);
        ChatRoom.this.bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    /**
     * 连接服务状态
     */
    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            DebugLogs.i("Service----->onServiceDisconnected()");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DebugLogs.i("Service----->onServiceConnected()");
            BackgroundService.MyBinder binder = (BackgroundService.MyBinder)service;
            bindService = binder.getBackgroundService();
            bindService.setRoomHander(mHander);
            mHander.obtainMessage(GlobalDef.WM_ROOM_LOGIN_SUCCESS).sendToTarget();
        }

    };
    /**
     * 解绑
     */
    private void unBindService(){
        ChatRoom.this.unbindService(conn);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sendChat:
                mHander.obtainMessage(GlobalDef.WM_ROOM_SEND_MESSAGE).sendToTarget();
                break;
        }
    }
}
