package com.anykey.balala.Socket.Process;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import socket.lib.process.CoreProcess;
import socket.lib.process.CoreProxy;
import socket.lib.process.ProcessList;
import socket.lib.protocol.Protocol;
import socket.lib.protocol.WillProtocol;
import socket.lib.socket.ConnectComponent;
import socket.lib.socket.SocketInfo;
import socket.lib.socket.SocketManager;
import socket.lib.util.ByteUtil;

import net.dev.mylib.DebugLogs;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 登陆服务器
 */
public class LoginProcess extends CoreProcess {
    private Handler mHandler;
    private SocketInfo mSocketInfo;

    public LoginProcess(Handler handler){
        mHandler = handler;
    }

    @Override
    public ProcessList getProcessList(final CoreProxy proxy){
        final Protocol protocol = new WillProtocol();
        SocketManager.SocketManagerCallback bussinessCallback = new SocketManager.SocketManagerCallback() {
            @Override
            public void onReceiveParcel(byte[] receive) {
                if(receive == null){
                    return;
                }
                int type = protocol.getType(receive);
                switch (type){
                    //心跳
                    case 100:
                        byte[] bytes = WillProtocol.beatheart();
                        proxy.send(bytes);
                        break;
                    //登陆到登陆服务器成功
                    case 2002:
                        Message msg2002 = mHandler.obtainMessage();
                        msg2002.what = 2002;
                        Bundle bundle = new Bundle();
                        bundle.putString("data",new String(protocol.getData(receive)));
                        msg2002.setData(bundle);
                        DebugLogs.i(ByteUtil.bytes2Hex(receive));
                        mSocketInfo = parseSocketInfo(protocol, receive);
                        mHandler.sendMessage(msg2002);
                        proxy.stopProcess();
                        //登陆到登陆服务器失败
                    case 2003:
                        Message msg2003 = mHandler.obtainMessage();
                        msg2003.what = 2003;
                        mHandler.sendMessage(msg2003);
                        proxy.stopProcess();
                        break;
                }
            }

            @Override
            public void onLostConnect() {
                DebugLogs.i("qiang lost connection ");

            }

            @Override
            public void onReadTaskFinish() {
                LoginSideProcess loginSideProcess = new LoginSideProcess(mHandler,mSocketInfo);
                CoreProxy coreProxy = new CoreProxy();
                coreProxy.startProcess(loginSideProcess);
            }
        };

        ConnectComponent.ConnectComponentCallback connectComponentCallback = new ConnectComponent.ConnectComponentCallback() {
            @Override
            public SocketInfo getSocketInfo() {
                SocketInfo loginSocketInfo = new SocketInfo();
                loginSocketInfo.host = "192.168.1.109";
//                loginSocketInfo.port = 9988;
//                loginSocketInfo.host = "192.168.199.188";
                loginSocketInfo.port = 60000;
                return loginSocketInfo;

//                return mSocketInfo;
            }

            @Override
            public void retryOverlimit(int connectTime) {

            }

            @Override
            public void connectFaild(int connectTime) {

            }

            @Override
            public void connectSuc(int connectTime) {

            }
        };
        ProcessList processUnit = new ProcessList(bussinessCallback,connectComponentCallback,protocol);
        return processUnit;
    }


    //解析包
    public SocketInfo parseSocketInfo(Protocol protocol,byte[] pack) {
        SocketInfo socketInfo = null;
        byte[] datas = protocol.getData(pack);
        int type = protocol.getType(pack);
        //如果不是登陆包类型则丢弃
        //验证失败
        if (type != 2002) {
            return socketInfo;
        }
        //解析json
        try {
            JSONObject jsonObj = new JSONObject(new String(datas));
            String ipStr = jsonObj.getString("ip");
            String signStr = jsonObj.getString("sign");
            socketInfo = new SocketInfo();
            String[] ipSplitAry = ipStr.split(":");
            socketInfo.host = ipSplitAry[0];
            socketInfo.port = Integer.parseInt(ipSplitAry[1]);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return socketInfo;
    }
}
