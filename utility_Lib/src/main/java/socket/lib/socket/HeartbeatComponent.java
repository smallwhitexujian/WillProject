package socket.lib.socket;



/**
 * Created by jjfly on 15-9-15.
 */

public abstract class HeartbeatComponent {

    //发送心跳包
    public void doHeartbeat(final SocketManager socketManager, final byte[] heartbeatParcel){

    }

    //关闭心跳包
    public void doneHeartbeat(){

    }

    //获取心跳周期
    public int obtainPeriod(){
        return 1000;
    }




}
