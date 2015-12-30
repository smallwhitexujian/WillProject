package socket.lib.socket;

import net.dev.mylib.DebugLogs;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 连接服务器 处理
 */
public class ConnectComponent {
    protected int connectTime = 0;//连接次数
    protected int MAX_RETRAY_TIME = 5;//重连次数
    protected int DELAY_TIME = 1000;//定时器启动时候延时
    protected int PERIOD = 3000;//定时器定时周期
    protected volatile boolean isRun = false;//防止多个线程调用时候出现多次调用连接方法，多线程连接时候,先到先得
    private Timer mTimer;
    private TimerTask mTimerTask;

    public void connect(final SocketManager socketManager,final ConnectComponentCallback callback){
        if(callback == null){
            DebugLogs.e("ConnectComponetCallback is null");
            return;
        }
        if (this.isRun) {
            DebugLogs.e("connect is run");
            return;
        }
        isRun = true;
        closeTimerTask();
        this.mTimer = new Timer();
        mTimerTask = new TimerTask() {
            public void run() {
                DebugLogs.e("reconnect time is " + connectTime);
                if (connectTime++ < MAX_RETRAY_TIME) {
                    if (socketManager.connect(callback.getSocketInfo())) {
                        socketManager.read();
                        closeTimerTask();
                        restore();
                        callback.connectSuc(connectTime);
                    } else {
                        callback.connectFaild(connectTime);
                    }

                } else {
                    closeTimerTask();
                    restore();
                    callback.retryOverlimit(connectTime);
                }
            }
        };
        this.mTimer.schedule(this.mTimerTask, DELAY_TIME, PERIOD);
    }



    public void connectWithHeartbeat(final SocketManager socketManager,final ConnectComponentCallback callback,final HeartbeatComponent heartbeatComponent){
        if(callback == null){
            DebugLogs.e("ConnectComponetCallback is null");
            return;
        }
        if (this.isRun) {
            DebugLogs.e("connect is run");
            return;
        }
        isRun = true;
        closeTimerTask();
        this.mTimer = new Timer();
        mTimerTask = new TimerTask() {
            public void run() {
                DebugLogs.e("reconnect time is " + connectTime);
                if (connectTime++ < MAX_RETRAY_TIME) {
                    if (socketManager.connect(callback.getSocketInfo())) {
                        socketManager.read();
                        socketManager.doHeartbeat(heartbeatComponent);
                        closeTimerTask();
                        restore();
                        callback.connectSuc(connectTime);
                    } else {
                        callback.connectFaild(connectTime);
                    }

                } else {
                    closeTimerTask();
                    restore();
                    callback.retryOverlimit(connectTime);
                }
            }
        };
        this.mTimer.schedule(this.mTimerTask, DELAY_TIME, PERIOD);
    }

    public void disconnect(){
        closeTimerTask();
        restore();
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

    private void restore(){
        mTimer = null;
        connectTime = 0;
        isRun = false;
    }


    public interface ConnectComponentCallback{
        public SocketInfo getSocketInfo();
        public void retryOverlimit(int connectTime);//连接超过次数用于提示统计
        public void connectFaild(int connectTime);//连接失败
        public void connectSuc(int connectTime);//连接成功

    }


}
