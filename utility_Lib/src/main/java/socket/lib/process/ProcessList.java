package socket.lib.process;

import socket.lib.protocol.Protocol;
import socket.lib.socket.ConnectComponent;
import socket.lib.socket.SocketManager;

/**
 * 处理类 业务回调
 */
public class ProcessList {

    private SocketManager.SocketManagerCallback mBussinessCallback;
    private ConnectComponent.ConnectComponentCallback mConnectCallback;
    private Protocol mProtocol;

    public ProcessList(SocketManager.SocketManagerCallback bussinessCallback, ConnectComponent.ConnectComponentCallback connectCalback, Protocol protocol){
        this.mBussinessCallback = bussinessCallback;
        this.mConnectCallback = connectCalback;
        this.mProtocol = protocol;
    }


    public SocketManager.SocketManagerCallback getBussinessCallback() {
        return mBussinessCallback;
    }


    public ConnectComponent.ConnectComponentCallback getConnectCallback() {
        return mConnectCallback;
    }

    public Protocol getProtocol() {
        return mProtocol;
    }
}
