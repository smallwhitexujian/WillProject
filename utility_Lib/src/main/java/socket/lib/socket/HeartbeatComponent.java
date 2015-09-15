package socket.lib.socket;

import net.dev.mylib.DebugLogs;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by jjfly on 15-9-15.
 */
public class HeartbeatComponent {
    private Timer mTimer;
    private TimerTask mTimerTask;
    private volatile boolean isRun = false;

    public void doHeartbeat(final SocketManager socketManager, final byte[] heartbeatParcel){
        if(socketManager == null || heartbeatParcel == null){
            DebugLogs.e("doHeartbeat faild ");
            return;
        }
        if(isRun){
            return;
        }
        isRun = true;
        closeTimerTask();
        this.mTimer = new Timer();
        mTimerTask = new TimerTask() {
            public void run() {
                try {
                    socketManager.writeHeartbeat(heartbeatParcel);
                } catch (Exception e) {
                    e.printStackTrace();
                    doneHeartbeat();
                }
            }
        };
    }

    private void closeTimerTask(){
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if(mTimerTask != null){
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    public void doneHeartbeat(){
        closeTimerTask();
        isRun = false;
    }



}
