/***
 * socket 服务代理类，通过代理类可以访问socket功能，
 */
package socket.lib.process;


import net.dev.mylib.DebugLogs;

import socket.lib.socket.ConnectComponent;
import socket.lib.socket.FixedPeriodHeartbeatComponent;
import socket.lib.socket.FixedRoomHeartbeat;
import socket.lib.socket.HeartbeatComponent;
import socket.lib.socket.SocketManager;

public class CoreProxy {
    private SocketManager mSocketManager = null;
    private CoreProcess mCoreProcess;
    private boolean mReconnection;
    private ConnectComponent mConnectComponent;

    /***
     *   标志是否合法：
     *     例如:标志 im 的状态,默认为false,登录成功后为true,socket断开之后为false
     */
    private volatile boolean mIsLegal = false;

    public CoreProxy(){
        mReconnection = true;
    }

    public void startProcess(CoreProcess process){
        this.mCoreProcess = process;
        ProcessList processList = process.getProcessList(this);
        if(mSocketManager == null){
            mSocketManager = new SocketManager(processList.getProtocol());
        }
        mSocketManager.registerCallback(processList.getBussinessCallback());
        mConnectComponent = new ConnectComponent();
        mConnectComponent.connect(mSocketManager, processList.getConnectCallback());
    }

    //发送
    public boolean send(byte[] data){
        try{
            if(data == null || mSocketManager == null){
                return false;
            }
            if(mSocketManager.getRunStatus() == SocketManager.CONNECTED){
                return mSocketManager.write(data,0,data.length);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public void stopProcess() {
        DebugLogs.e("jjfly ssss "+mSocketManager);
        if (mSocketManager != null) {
            if(mConnectComponent != null){
                mConnectComponent.disconnect();
            }
            mSocketManager.disconnect();
        }
        mReconnection = false;
    }

    //发心跳包

    /**
     * 心跳包发送器（可以改变心跳周期）
     * @param heartbeatComponent 周期算法接口
     */
    public void doHeartbeat(HeartbeatComponent heartbeatComponent){
      mSocketManager.doHeartbeat(heartbeatComponent);
    }

    /**
     * 心跳恒定周期10秒
     */
    public void doFixedPeriodHeartbeat(){
        FixedPeriodHeartbeatComponent heartbeatComponent = new FixedPeriodHeartbeatComponent();
        mSocketManager.doHeartbeat(heartbeatComponent);
    }

    public void doRoomHeartbeat(){
        FixedRoomHeartbeat heartbeatComponent = new FixedRoomHeartbeat();
        mSocketManager.doHeartbeat(heartbeatComponent);
    }

    public boolean isReconnection() {
        return mReconnection;
    }

    public void setReconnection(boolean reconnection) {
        this.mReconnection = reconnection;
    }


    public boolean isLegal() {
        return mIsLegal;
    }

    public void setIsLegal(boolean isLegal) {
        this.mIsLegal = isLegal;
    }


    public int getRunStatus(){
        if(mSocketManager != null){
            return mSocketManager.getRunStatus();
        }
        return SocketManager.CONNECTNULL;
    }

    public boolean isConnectValid(){
        if(mSocketManager == null){
            return false;
        }
        return mSocketManager.isValid();
    }
}
