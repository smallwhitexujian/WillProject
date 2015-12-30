package com.anykey.balala.Socket.Process;

import android.os.Handler;

import com.anykey.balala.GlobalDef;
import com.anykey.balala.model.RoomInfo;

import net.dev.mylib.DebugLogs;
import net.dev.mylib.JsonUtil;

import socket.lib.process.CoreProcess;
import socket.lib.process.CoreProxy;
import socket.lib.process.ProcessList;
import socket.lib.protocol.Protocol;
import socket.lib.socket.ConnectComponent;
import socket.lib.socket.SocketInfo;
import socket.lib.socket.SocketManager;

/**
 * Created by xujian on 15/9/10.
 * 房间登陆
 */
public class RoomLoginProcess extends CoreProcess {
    private Handler mHandler;
    private String HOST = "";
    private int PORT = 0;
    private int BarId = 0;
    private int UserId = 0;
    private String userToken = "";

    public RoomLoginProcess(Handler handler,String host ,int port,int barId,int userId,String token){
        this.mHandler = handler;
        this.HOST = host;
        this.PORT = port;
        this.BarId = barId;
        this.UserId = userId;
        this.userToken = token;
    }

    @Override
    public ProcessList getProcessList(final CoreProxy proxy) {
        final Protocol protocol = new WillProtocol();
        SocketManager.SocketManagerCallback callback = new SocketManager.SocketManagerCallback() {
            @Override
            public void onReceiveParcel(byte[] receive) {
                AcceptChatMessage(protocol,receive);
            }

            @Override
            public void onLostConnect() {
                //房间连接不上进行重连
                if (proxy != null && proxy.isReconnection()){
                    mHandler.obtainMessage(GlobalDef.SERVICE_STATUS_CONNETN).sendToTarget();
                }else{
                    mHandler.obtainMessage(GlobalDef.SERVICE_STATUS_FAILD).sendToTarget();
                }
            }

            @Override
            public void onReadTaskFinish() {

            }
        };

        ConnectComponent.ConnectComponentCallback connectComponentCallback = new ConnectComponent.ConnectComponentCallback() {
            @Override
            public SocketInfo getSocketInfo() {
                SocketInfo roomLogin = new SocketInfo();
                roomLogin.host = HOST;
                roomLogin.port = PORT;
                return roomLogin;
            }

            @Override
            public void retryOverlimit(int connectTime) {
                mHandler.obtainMessage(GlobalDef.SERVICE_STATUS_FAILD,1,0,0).sendToTarget();
                DebugLogs.e("retryOverlimit"+connectTime);
            }

            @Override
            public void connectFaild(int connectTime) {
                DebugLogs.e("connectFaild" + connectTime);
            }

            @Override
            public void connectSuc(int connectTime) {
                DebugLogs.e(proxy.getRunStatus()+"connectSuc--------->登陆服务器"+connectTime);
                mHandler.obtainMessage(GlobalDef.SERVICE_STATUS_SUCCESS).sendToTarget();
                RoomInfo roomInfo = new RoomInfo(BarId,UserId,userToken);
                String jsonString = JsonUtil.toJson(roomInfo);
                sendMessage(proxy,GlobalDef.WM_ROOM_LOGIN,jsonString);
                proxy.doRoomHeartbeat();//发送心跳
            }
        };
        ProcessList processList = new ProcessList(callback,connectComponentCallback,protocol);
        return processList;
    }


    /**
     * 接受服务器消息
     */
    public String AcceptChatMessage(Protocol protocol ,byte[] pack){
        byte[] datas = protocol.getData(pack);//获得服务器返回的数据
        int type = protocol.getType(pack);//获得服务器状态码
        mHandler.obtainMessage(type,new String(datas).trim()).sendToTarget();
        return "";
    }

    /**
     * 房间发出的消息
     */
    public void sendMessage(CoreProxy proxy,int typeValue, String jsonStr){
        byte[] bytes = WillProtocol.sendMessage(typeValue,jsonStr);
        if (SocketRunStatus(proxy)){
            proxy.send(bytes);
        }else{//失败从新连接
            mHandler.obtainMessage(GlobalDef.SERVICE_STATUS_CONNETN).sendToTarget();
        }
    }

    //获取Socket是否连接
    private boolean SocketRunStatus(CoreProxy proxy){
        int status = proxy.getRunStatus();
        switch (status){
            case SocketManager.CONNECTED:
                return true;
            default:
                return false;
        }
    }
}
