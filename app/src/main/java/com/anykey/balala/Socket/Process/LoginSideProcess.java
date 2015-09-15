package com.anykey.balala.Socket.Process;

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

import net.dev.mylib.DebugLogs;

/**
 * 登陆边服务器
 *
 */
public class LoginSideProcess extends CoreProcess {

    private Handler mHandler;
    private SocketInfo mSocketInfo;

    public LoginSideProcess(Handler handler, SocketInfo info){
        mHandler = handler;
        mSocketInfo = info;
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
                    case 2004:
                        Message msg2004 = mHandler.obtainMessage();
                        DebugLogs.i("loginsideprocess 2004");
                        msg2004.what = 2004;
                        mHandler.sendMessage(msg2004);
                        break;
                    case 2005:
                        Message msg2005 = mHandler.obtainMessage();
                        msg2005.what = 2005;
                        DebugLogs.i("loginsideprocess 2005");
                        mHandler.sendMessage(msg2005);
                        break;
                }
            }

            @Override
            public void onLostConnect() {
                CoreProcess loginProcess = new LoginProcess(mHandler);
                CoreProxy coreProxy = new CoreProxy();
                coreProxy.startProcess(loginProcess);
            }

            @Override
            public void onReadTaskFinish() {

            }
        };

        ConnectComponent.ConnectComponentCallback connectComponentCallback = new ConnectComponent.ConnectComponentCallback() {
            @Override
            public SocketInfo getSocketInfo() {
                SocketInfo socketInfo = new SocketInfo();
                socketInfo.host = "192.168.1.109";
//                socketInfo.host = "192.168.199.188";
                socketInfo.port = 60001;
                return socketInfo;
//               return mSocketInfo;
            }

            @Override
            public void retryOverlimit(int connectTime) {
//                mHandler.sendMessage()

            }

            @Override
            public void connectFaild(int connectTime) {
//                mHandler.sendMessage();
            }

            @Override
            public void connectSuc(int connectTime) {
//                mHandler.sendMessage();
            }
        };
        ProcessList processUnit = new ProcessList(bussinessCallback,connectComponentCallback,protocol);
        return processUnit;
    }


}
