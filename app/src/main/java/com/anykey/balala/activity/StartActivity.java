package com.anykey.balala.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.widget.TextView;

import com.anykey.balala.AppBalala;
import com.anykey.balala.GlobalDef;
import com.anykey.balala.R;
import com.anykey.balala.work.InitWork;

import net.dev.mylib.DebugLogs;
import net.dev.mylib.Utility;
import net.dev.mylib.cache.sharedPreferences.SharedPreferencesUtil;
import net.dev.mylib.netWorkUtil.NetWorkUtil;

/**
 * Created by xujian on 15/8/28.
 */
public class StartActivity extends BaseActivity {
    private boolean isWaitingNetLinked = false;
    private TextView runTips;
    private SharedPreferencesUtil sp;

    private MediaPlayer mMediaPlayer;

    private Handler UIhandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GlobalDef.WM_NET_LINK:
                    if (isWaitingNetLinked) {
                        DebugLogs.i("有网后重新初始化");
                        isWaitingNetLinked = false;
                    }
                    break;
                /* 更新提示 */
                case GlobalDef.WM_START_UPDATE_TIPS:
                    DebugLogs.i(msg.obj.toString());
                    runTips.setText(msg.obj.toString());
                    break;
                case GlobalDef.WM_GO_LOGINREGISTER:
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case GlobalDef.WM_ENTER_MAIN:
                    Intent mainIntent = new Intent(mContext, MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                    break;
                case GlobalDef.WM_PARSE_XML_ERROR:
                    runTips.setText("parse xml error");
                    break;
                default:
                    break;
            }
        }
    };

    private HandlerThread mHandlerThread;
    private Handler mWorkerHandler;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        NetWorkUtil.startNetWorkReceiver(this);
        runTips = (TextView) findViewById(R.id.textView1);
        sp = SharedPreferencesUtil.getInstance(mContext);
        AppBalala.isSound = sp.getBoolean("isSound", true);

        if (!NetWorkUtil.CheckConnectionOther(this)) {
            DebugLogs.e("没网");
            isWaitingNetLinked = true;
            return;
        }
        if (AppBalala.IMLoginStatus) {//外部服务器登陆成功，保证外部服务器成功直接进入首页
            DebugLogs.e("log---->IMLoginStatus=true");
            UIhandler.obtainMessage(GlobalDef.WM_ENTER_MAIN).sendToTarget();
        } else {
            DebugLogs.e("log---->IMLoginStatus=false");//外部服务器断开的情况
            mHandlerThread = new HandlerThread("start");
            mHandlerThread.start();
            mWorkerHandler = new Handler(mHandlerThread.getLooper());
            mWorkerHandler.postDelayed(new InitWork(StartActivity.this, UIhandler), 200);
        }

        if (AppBalala.isSound && !AppBalala.isDebug) {
            mMediaPlayer = new MediaPlayer();
            try {
                AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.balala_start);
                mMediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();

                final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                    mMediaPlayer.setLooping(false);
                    try {
                        mMediaPlayer.prepare();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //延时播放
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mMediaPlayer.start();
                        }
                    }, 500);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            AppBalala.versionCode = getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode;
            AppBalala.versionName = getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName.trim();
            AppBalala.MAC = Utility.getUUID(this);
            AppBalala.phoneBRAND = android.os.Build.BRAND.trim();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (NetWorkUtil.isConnected(StartActivity.this)) {//来网的时候重新去加载xml
            mHandlerThread = new HandlerThread("start");
            mHandlerThread.start();
            mWorkerHandler = new Handler(mHandlerThread.getLooper());
            mWorkerHandler.postDelayed(new InitWork(StartActivity.this, UIhandler), 200);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            NetWorkUtil.stopNetWorkReceiver(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}