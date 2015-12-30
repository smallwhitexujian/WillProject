package socket.lib.socket;


import net.dev.mylib.DebugLogs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import socket.lib.protocol.Protocol;
import socket.lib.util.ByteArrayBuffer;
import socket.lib.util.ByteUtil;

/**
 * 收发包。Socket管理
 */
public class SocketManager {
    public static final int CONNECTNULL = -1;//原始状态
    public static final int CONNECTLOST = -2;//丢失连接
    public static final int CONNECTFAILD = 0;//连接失败
    public static final int CONNECTINIT = 1;//初始化中....
    public static final int CONNECTING = 2;//连接中
    public static final int CONNECTED = 3;//连接成功

    protected InputStream mInputStream;
    protected OutputStream mOutputStream;
    protected SocketManagerCallback mSocketManagerCallback;
    protected boolean isLostConnect = true;
    protected Socket mSocket;
    protected SocketInfo mSocketInfo;
    protected volatile boolean mSwitch;

    protected ExecutorService mPool = Executors.newFixedThreadPool(2);
    protected volatile boolean isValid = false;//是否连接成功
    protected Protocol mProtocol;
    protected int mRunStatus = CONNECTNULL;

    protected int MAX_BORDER = 10 * 1024 * 1024;;
    protected int SLEEP_TIME = 250;
    protected HeartbeatComponent mHeartbeatComponent;

    private Object lock = new Object();

    public SocketManager(Protocol protocol){
        mRunStatus = CONNECTINIT;
        mProtocol = protocol;
    }

    public void disconnect() {
        synchronized (lock) {
            DebugLogs.e("jjfly disconnection =====");
            doneHeartbeat();//关闭心跳
            try {
                if (mInputStream != null) {
                    mInputStream.close();
//                mInputStream = null;
                }
                if (mOutputStream != null) {
                    mOutputStream.close();
//                mOutputStream = null;
                }
                if (mSocket != null) {
                    DebugLogs.e("jjfly  close socket");
                    mSocket.close();
//                mSocket = null;
                }
                mSwitch = false;
                isValid = false;
                mRunStatus = CONNECTLOST;
            } catch (IOException e) {
                e.printStackTrace();
                DebugLogs.e("jjfly " + e.getMessage());
                mRunStatus = CONNECTNULL;
            }
        }
    }

    public boolean connect(SocketInfo socketInfo) {
        synchronized (lock) {
            DebugLogs.e("jjfly connect:" + socketInfo);
            mRunStatus = CONNECTING;
            if (socketInfo == null) {
                mRunStatus = CONNECTFAILD;
                return false;
            }
            try {
                mSocket = new Socket();
                mSocket.connect(new InetSocketAddress(socketInfo.host, socketInfo.port), socketInfo.timeout);
                mSocket.setSoTimeout(socketInfo.timeout);
                mInputStream = mSocket.getInputStream();
                mOutputStream = mSocket.getOutputStream();
                mSocketInfo = socketInfo;
                DebugLogs.e("Socket Connent Sussecc");
                isValid = true;
                mRunStatus = CONNECTED;
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                mRunStatus = CONNECTFAILD;
            }
            return false;
        }
    }

    protected void onLostConnect() {
        disconnect();
        if(mSocketManagerCallback != null){
            mSocketManagerCallback.onLostConnect();
        }
    }

    public void registerCallback(SocketManagerCallback callback){
        mSocketManagerCallback = callback;

    }

    public void read(){
        if(mInputStream == null){
            return;
        }
        mPool.execute(new Runnable() {
            @Override
            public void run() {
                ByteArrayBuffer mByteBuffer = null;
                mSwitch = true;
                isLostConnect = false;
                while (mSwitch) {
                    try {
                        if (mByteBuffer == null) {
                            mByteBuffer = new ByteArrayBuffer(mProtocol.getHeadLen());
                        }
                        int count = mByteBuffer.mlen;
                        int readCount = mByteBuffer.moffset;// 已经成功读取的字节的个数
                        while (readCount < mByteBuffer.mlen) {
                            int readNum = mInputStream.read(mByteBuffer.mbuffer, readCount, count - readCount);
                            if (readNum < 0) {
                                DebugLogs.e("jjfly ---lost connection ");
                                mSwitch = false;
                                isLostConnect = true;
                                break;
                            }
                            if (readNum > 0) {
                                readCount += readNum;
                                mByteBuffer.flush(readCount);
                            }
//                            Thread.sleep(SLEEP_TIME);
                        }
                        int totalLen = mProtocol.getDataLen(mByteBuffer.mbuffer);
                        //数据包小于指定大小,丢失连接（inputsream关闭)
                        if (mByteBuffer.mlen < mByteBuffer.moffset) {
                            mSwitch = false;
                            mByteBuffer = null;
                            isLostConnect = true;
                        } else {
                            if (mByteBuffer.mlen == totalLen) {
                                if (mSocketManagerCallback != null) {
                                    mSocketManagerCallback.onReceiveParcel(mByteBuffer.mbuffer);
                                    DebugLogs.e("===收到的==>"+ByteUtil.bytes2Hex(mByteBuffer.mbuffer));
                                }
                                mByteBuffer = null;
                            } else {
                                if (mByteBuffer.mlen < MAX_BORDER && totalLen < MAX_BORDER) {
                                    mByteBuffer.reSize(totalLen, mByteBuffer);
                                }
                                //oom异常处理
                                else {
                                    mSwitch = false;
                                    mByteBuffer = null;
                                    isLostConnect = true;
                                }
                            }
                        }
                    }catch (IOException e){
                        DebugLogs.i("ioException");
                        if(e instanceof java.net.SocketTimeoutException){
                            continue;
                        }
                        isLostConnect = true;
                        mSwitch = false;
                        e.printStackTrace();
                    }
                }
                disconnect();
                DebugLogs.i("jjfly "+isLostConnect);
                //丢失连接业务流程回调

                if (isLostConnect) {
                    if(mSocketManagerCallback != null){
                        mSocketManagerCallback.onLostConnect();
                    }
                }
                else{
                    if(mSocketManagerCallback != null){
                        mSocketManagerCallback.onReadTaskFinish();
                    }
                }
            }
        });
    }


    public boolean write(final byte[] src,final int srcStart,final int len){
        if (mOutputStream != null) {
            try {
                DebugLogs.e(ByteUtil.bytes2Hex(src));
                mOutputStream.write(src, srcStart, len);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                onLostConnect();
            }
        }
        return false;
    }

    public boolean isValid(){
        return isValid;
    }

    public int getRunStatus(){
        synchronized (lock) {
            return mRunStatus;
        }
    }

    public void writeHeartbeat(byte[] heartParcel) throws Exception {
        if (mOutputStream != null) {
            try {
//                DebugLogs.i(ByteUtil.bytes2Hex(heartParcel));
                mOutputStream.write(heartParcel,0,heartParcel.length);
            } catch (IOException e) {
                e.printStackTrace();
                onLostConnect();
                throw new Exception("");
            }
        }
    }


    //启动心跳
    public void doHeartbeat(HeartbeatComponent heartbeatComponent){
        mHeartbeatComponent = heartbeatComponent;
        byte[] heartbeatParcel = mProtocol.heartbeatParcel();
        mHeartbeatComponent.doHeartbeat(this,heartbeatParcel);
    }

    //停止心跳
    public void doneHeartbeat(){
        if(mHeartbeatComponent != null){
            mHeartbeatComponent.doneHeartbeat();
        }
    }



    //socket 回调
    public interface SocketManagerCallback {
        //业务接口
        public void onReceiveParcel(byte[] receive);//收到包时候回调
        public void onLostConnect();//丢失链接时候回调
        public void onReadTaskFinish();//读线程正常在没有丢失连接退出后的回调

    }

}