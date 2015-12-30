package com.anykey.balala.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.Selection;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.anykey.balala.AppBalala;
import com.anykey.balala.CommonUrlConfig;
import com.anykey.balala.R;
import com.anykey.balala.Utils.MapUtil;
import com.anykey.balala.model.BaseUserInfo;
import com.anykey.balala.model.CommonData;
import com.anykey.balala.model.CommonListResult;
import com.anykey.balala.receiver.AppBroadcastReceiver;
import com.anykey.balala.service.BackgroundService;
import com.anykey.balala.view.HeaderLayout;
import com.google.gson.reflect.TypeToken;

import net.dev.mylib.Encryption;
import net.dev.mylib.JsonUtil;
import net.dev.mylib.ToastUtils;
import net.dev.mylib.Utility;
import net.dev.mylib.cache.sharedPreferences.SharedPreferencesUtil;
import net.dev.mylib.netWorkUtil.GetJson;
import net.dev.mylib.netWorkUtil.getCode;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by shanli on 15/8/26.
 * 手机号注册界面
 */
public class NewUserActivity extends BinderActivity implements View.OnClickListener {

    private Button btn_getcode;
    private EditText et_phone, et_password, et_code;
    private int secondNum;
    private SharedPreferencesUtil sp = SharedPreferencesUtil.getInstance(mContext);
    protected final static int Show_Send_Btn_Msg = 0x0112;

    // 定时器相关
    private TimerTask timerTask;
    private Timer timer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        initView();
    }

    private void initView() {
        headerLayout = (HeaderLayout) findViewById(R.id.headerLayout);
        headerLayout.showTitle(R.string.register_with_phone);
        headerLayout.showLeftBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewUserActivity.this, LoginActivity.class);
                startActivity(intent);
                Utility.closeKeybord(et_phone, NewUserActivity.this);
                Utility.closeKeybord(et_password, NewUserActivity.this);
                Utility.closeKeybord(et_code, NewUserActivity.this);
                finish();
            }
        });
        Button btn_next = (Button) findViewById(R.id.btn_next);
        btn_getcode = (Button) findViewById(R.id.btn_getcode);
        btn_getcode.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        et_code = (EditText) findViewById(R.id.et_code);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_phone.setText(R.string.country_code);
        Editable etext = et_phone.getText();
        Selection.setSelection(etext, etext.length());
        Utility.openKeybord(et_phone, this);
        et_password = (EditText) findViewById(R.id.et_password);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                //提交验证信息到接口
                if (et_phone.length() > 6 && et_code.length() == 6 && et_password.length() >= 6) {
                    Response();
                }
                break;
            case R.id.btn_getcode:
                //获得短信验证码
                if (et_phone.length() > 6) {
                    getCode();
                }
                break;
        }
    }

    private void CancelTimer() {
        // 取消定时任务
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        // 取消定时器
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @SuppressLint({"HandlerLeak"})
    private Handler handler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case Show_Send_Btn_Msg:
                    if (secondNum == 0) {// 可以重新发送
                        // 取消定时任务
                        CancelTimer();

                        btn_getcode.setText(R.string.button_resend);
                        btn_getcode.setEnabled(true);
                        btn_getcode.setBackgroundDrawable(getResources().getDrawable(R.drawable.task_right_button));
                    } else {// 不能重新发送
                        btn_getcode.setEnabled(false);
                        btn_getcode.setText(getString(R.string.button_resend) + "(" + secondNum + ")");
                        btn_getcode.setBackgroundDrawable(getResources().getDrawable(R.drawable.task_right_button_3));
                        --secondNum;
                    }
                    break;
            }
        }
    };

    /**
     * 获取短信验证码
     */
    private void getCode() {

        secondNum = 60;// 重发等待时间
        HashMap<String, String> params = new HashMap<>();
        params.put("phone", et_phone.getText().toString().replace("+", ""));
        params.put("type", "1");
        params.put("Source", "2");
        GetJson.Callback callback = new GetJson.Callback() {
            @Override
            public void onFinish(String response) {
                CommonData results = JsonUtil.fromJson(response, CommonData.class);
                if (results != null) {
                    timer = new Timer();
                    // 1秒钟执行一次
                    timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            handler.sendEmptyMessage(Show_Send_Btn_Msg);
                        }
                    };
                    // 一秒钟执行一次
                    timer.schedule(timerTask, 0, 1000);

                } else if (results.code.equals("1005")) {
                    ToastUtils.showToast(mContext, R.string.mobile_phone_number_already_exists);

                    btn_getcode.setBackgroundDrawable(getResources().getDrawable(R.drawable.task_right_button));
                    btn_getcode.setEnabled(true);
                } else {

                    btn_getcode.setEnabled(true);
                    btn_getcode.setBackgroundDrawable(getResources().getDrawable(R.drawable.task_right_button));
                    ToastUtils.showToast(mContext, results.message);
                }
            }

            @Override
            public void onError(VolleyError error) {
                //处理错误
                ToastUtils.showToast(mContext, error.getMessage());
                getCode.hasCode errorCode = ((getCode.hasCode) error);
                String strCode = errorCode.errorCode;
                if (strCode.equals(CommonUrlConfig.RequestState.Logout)) {
                    Intent voiceIntent = new Intent();
                    voiceIntent.setAction(AppBroadcastReceiver.BROADCAST_USER_LOGOUT);
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(voiceIntent);
                } else {
                    ToastUtils.showToast(NewUserActivity.this, MapUtil.getString(mContext, strCode));
                }
            }
        };
        GetJson getJson = new GetJson(NewUserActivity.this, callback, true, getString(R.string.loading));
        getJson.setConnection(Request.Method.GET, CommonUrlConfig.getPhoneCode, params);
    }

    /**
     * 点击物理返回按钮**
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //
            finish();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        return false;
    }

    /**
     * 获取返回结果
     */
    private void Response() {
        String phoneNumber = et_phone.getText().toString().replace("+", "");
        String password = et_password.getText().toString();
        String phoneCode = et_code.getText().toString();
        if (phoneNumber == null || phoneNumber.length() == 0) {
            return;
        }
        if (password == null || password.length() == 0) {
            return;
        }
        if (phoneCode == null || phoneCode.length() == 0) {
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("phone", phoneNumber);
        params.put("password", Encryption.MD5(password));
        params.put("code", phoneCode);
        params.put("deviceid", AppBalala.getDeviceId);
        params.put("Source", "2");
        GetJson.Callback callback = new GetJson.Callback() {
            @Override
            public void onFinish(String response) {
                CommonListResult<BaseUserInfo> results = JsonUtil.fromJson(response, new TypeToken<CommonListResult<BaseUserInfo>>() {
                }.getType());
                if (results != null) {
                    String token = results.data.get(0).Token;
                    String userid = results.data.get(0).Userid;
                    String uname = results.data.get(0).nickname;
                    String userlevel = results.data.get(0).userlevel;
                    String PictureUri = results.data.get(0).headurl;

                    sp.saveLoginInfo(true, token, userid, uname, PictureUri, userlevel);
                    Intent i = new Intent();
                    i.setAction(BackgroundService.CMD_LOGIN_IM_ACTION);
                    mContext.sendBroadcast(i);

                    Intent intent = new Intent(NewUserActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    LoginActivity.instance.finish();
                    finish();
                }
            }

            @Override
            public void onError(VolleyError error) {
                //处理错误
                getCode.hasCode errorCode = ((getCode.hasCode) error);
                String strCode = errorCode.errorCode;
                if (strCode.equals(CommonUrlConfig.RequestState.Logout)) {
                    Intent voiceIntent = new Intent();
                    voiceIntent.setAction(AppBroadcastReceiver.BROADCAST_USER_LOGOUT);
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(voiceIntent);
                } else {
                    ToastUtils.showToast(NewUserActivity.this, MapUtil.getString(mContext, strCode));
                }
            }
        };
        GetJson getJson = new GetJson(NewUserActivity.this, callback, true, getString(R.string.loading));
        getJson.setConnection(Request.Method.GET, CommonUrlConfig.phoneRegister, params);
    }
}
