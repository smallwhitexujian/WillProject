package com.anykey.balala.Socket.Process;

import android.os.Handler;

import com.anykey.balala.model.SendMessageInfo;

import net.dev.mylib.DebugLogs;

import socket.lib.process.CoreProcess;
import socket.lib.process.CoreProxy;
import socket.lib.process.ProcessList;
import socket.lib.protocol.Protocol;
import socket.lib.protocol.WillProtocol;
import socket.lib.socket.ConnectComponent;
import socket.lib.socket.SocketInfo;
import socket.lib.socket.SocketManager;

/**
 * Created by xujian on 15/9/10.
 * 房间登陆
 */
public class RoomLoginProcess extends CoreProcess {
    private Handler mHandler;
    private String HOST = "192.168.199.182";
    private int PORT = 8899;

    public RoomLoginProcess(Handler handler,String host ,int port){
        this.mHandler = handler;
        this.HOST = host;
        this.PORT = port;
    }

    @Override
    public ProcessList getProcessList(final CoreProxy proxy) {
        final Protocol protocol = new WillProtocol();
        SocketManager.SocketManagerCallback callback = new SocketManager.SocketManagerCallback() {
            @Override
            public void onReceiveParcel(byte[] receive) {
                if (receive == null){
                    return;
                }
                int type = protocol.getType(receive);
                switch (type){
                    //心跳
                    case 100:
                        byte[] bytes = WillProtocol.beatheart();
                        proxy.send(bytes);
                        break;
                    case 10003://登陆成功
                        AcceptChatMessage(protocol,receive);
                        break;
                }
            }

            @Override
            public void onLostConnect() {
                DebugLogs.e("Lost Socket connect");
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
                DebugLogs.e("retryOverlimit"+connectTime);
            }

            @Override
            public void connectFaild(int connectTime) {
                DebugLogs.e("connectFaild" + connectTime);
            }

            @Override
            public void connectSuc(int connectTime) {
                DebugLogs.e("connectSuc"+connectTime);
            }
        };
        ProcessList processList = new ProcessList(callback,connectComponentCallback,protocol);
        return processList;
    }

    public void sendChatLine(CoreProxy proxy,String jsonStr) {
        byte[] byte234 = WillProtocol.sendText(jsonStr);
        proxy.send(byte234);
    }

    /**
     * //接受服务器返回的聊天消息
     * @param protocol
     * @param pack
     * @return
     */
    public String AcceptChatMessage(Protocol protocol ,byte[] pack){
        byte[] datas = protocol.getData(pack);//获得服务器返回的数据
        int type = protocol.getType(pack);//获得服务器状态码
//        SendMessageInfo results = JsonUtil.fromJson(new String(datas).trim().toString(), SendMessageInfo.class);
        mHandler.obtainMessage(type,new String(datas).trim().toString()).sendToTarget();
        return "";
    }
}
