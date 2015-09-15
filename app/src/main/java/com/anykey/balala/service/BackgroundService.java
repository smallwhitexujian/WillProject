package com.anykey.balala.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.anykey.balala.AppBalala;
import com.anykey.balala.GlobalDef;
import com.anykey.balala.Socket.Process.RoomLoginProcess;

import net.dev.mylib.DebugLogs;
import net.dev.mylib.JsonUtil;

import socket.lib.process.CoreProxy;
import socket.lib.protocol.RoomInfo;
import socket.lib.protocol.WillProtocol;

/**
 * Created by xujian on 15/8/27.
 * 后台service服务
 */
public class BackgroundService extends Service {
    private MyBinder myBinder = new MyBinder();
    private RoomLoginProcess loginProcess;
    private CoreProxy proxy;
    private Handler mRoomHandler = null;

    private Handler mBackgroundHander = new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case GlobalDef.SO_DOHEART://接受到的心跳包
                    break;
                case GlobalDef.WM_ROOM_RECEIVE_MESSAGE:
                    mRoomHandler.obtainMessage().sendToTarget();
                    break;
            }
        }
    };
    public class MyBinder extends Binder{
        public BackgroundService getBackgroundService(){
            return BackgroundService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppBalala.mBackGroundService = this;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        DebugLogs.e("后台服务被调用");
    }


    @Override
    public IBinder onBind(Intent intent) {
        DebugLogs.v("BackgroundService---->onBind");
        return myBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        DebugLogs.v("BackgroundService---->onRebind");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        DebugLogs.v("BackgroundService---->onUnbind");
        return super.onUnbind(intent);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void setRoomHander(Handler hander){
        this.mRoomHandler = hander;
    }

    /**
     * 登录房间
     * @param host   服务器地址
     * @param Port   服务器端口
     * @param BarId  房间ID
     * @param UserId 用户ID
     */
    public void startRoomConnection(String host,int Port,int BarId,int UserId) {
        DebugLogs.e("-----startRoomConnection------>");
        loginProcess = new RoomLoginProcess(mBackgroundHander, host,Port);
        proxy = new CoreProxy();
        proxy.startProcess(loginProcess);
        RoomInfo roomInfo = new RoomInfo(BarId,UserId);
        String jsonString = JsonUtil.toJson(roomInfo);
        byte[] loginByte = WillProtocol.RoomloginParcel(jsonString);
        proxy.send(loginByte);
    }

    /**
     * 发送聊天
     */
    public void sendChatMessage(String jsonStr){
        loginProcess.sendChatLine(proxy,jsonStr);
    }
}
