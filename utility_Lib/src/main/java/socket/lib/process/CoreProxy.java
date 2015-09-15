/***
 * socket 服务代理类，通过代理类可以访问socket功能，
 */
package socket.lib.process;


import socket.lib.socket.ConnectComponent;
import socket.lib.socket.HeartbeatComponent;
import socket.lib.socket.SocketManager;

public class CoreProxy {
    private SocketManager mSocketManager = null;

    public void startProcess(CoreProcess process){
        ProcessList processList = process.getProcessList(this);
        if(mSocketManager == null){
            mSocketManager = new SocketManager(processList.getProtocol());
        }
        mSocketManager.registerCallback(processList.getBussinessCallback());
        ConnectComponent connectComponent = new ConnectComponent();
        connectComponent.connect(mSocketManager, processList.getConnectCallback());
    }

    //心跳
    public void startProcessWithHeartbeat(CoreProcess process){
        //心跳
        ProcessList processList = process.getProcessList(this);
        if(mSocketManager == null){
            mSocketManager = new SocketManager(processList.getProtocol());
        }
        mSocketManager.registerCallback(processList.getBussinessCallback());
        ConnectComponent connectComponent = new ConnectComponent();
        connectComponent.connectWithHeartbeat(mSocketManager, processList.getConnectCallback());


    }

    //发送
    public void send(byte[] data){
        if(data == null){
            return;
        }
        if(mSocketManager == null){
           throw new RuntimeException("socketmanager not exists");
        }
        if(mSocketManager.isValid()){
            mSocketManager.write(data,0,data.length);
        }
    }



    public void stopProcess() {
        if (mSocketManager != null) {
            mSocketManager.disconnect();
        }
    }


    //发心跳包
    public void doHeartbeat(CoreProcess process){
        ProcessList processList = process.getProcessList(this);
        byte[] heartbeatParcel = processList.getProtocol().heartbeatParcel();
        send(heartbeatParcel);
    }
}
