package com.anykey.balala.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.anykey.balala.AppBalala;
import com.anykey.balala.CommonUrlConfig;
import com.anykey.balala.R;
import com.anykey.balala.model.CommonData;
import com.anykey.balala.model.CommonListResult;
import com.anykey.balala.model.TokenInfo;
import com.google.gson.reflect.TypeToken;

import net.dev.mylib.JsonUtil;
import net.dev.mylib.ToastUtils;
import net.dev.mylib.cache.sharedPreferences.SharedPreferencesUtil;
import net.dev.mylib.netWorkUtil.GetJson;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by shanli on 15/8/26.
 * 手机号注册界面
 */
public class NewUserActivity extends BaseActivity implements View.OnClickListener {

    private Button btn_next, btn_getcode;
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
        initContent();
    }

    private void initContent() {
    }

    private void initView() {
        btn_next = (Button) findViewById(R.id.btn_next);
        btn_getcode = (Button) findViewById(R.id.btn_getcode);
        btn_getcode.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        et_code = (EditText) findViewById(R.id.et_code);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_password = (EditText) findViewById(R.id.et_password);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                //提交验证信息到接口
                Response();
                break;
            case R.id.btn_getcode:
                //获得短信验证码
                getCode();
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
                    } else {// 不能重新发送

                        btn_getcode.setEnabled(false);
                        btn_getcode.setText(getString(R.string.button_resend) + "(" + secondNum + ")");
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
        et_phone.setEnabled(false);
        btn_getcode.setEnabled(false);
        secondNum = 60;// 重发等待时间
        Map params = new HashMap();
        params.put("phone", et_phone.getText().toString());
        params.put("Source", "2");
        GetJson.Callback callback = new GetJson.Callback() {
            @Override
            public void onFinish(String response) {
                CommonData results = JsonUtil.fromJson(response, CommonData.class);
                if (results.code.equals(CommonUrlConfig.RequestState.OK)) {

                    if(AppBalala.isDebug) {
                        et_code.setText(results.data);
                    }

                    ToastUtils.showToast(mContext, "验证码已经发送");

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
                    ToastUtils.showToast(mContext, "手机号已经存在");
                    et_phone.setEnabled(true);
                    btn_getcode.setEnabled(true);
                } else {
                    et_phone.setEnabled(true);
                    btn_getcode.setEnabled(true);
                    ToastUtils.showToast(mContext, results.message);
                }
            }

            @Override
            public void onError(VolleyError error) {
                //处理错误
                ToastUtils.showToast(mContext, error.getMessage());
            }
        };
        GetJson getJson = new GetJson(NewUserActivity.this, callback, true, getString(R.string.loading));
        getJson.setConnection(Request.Method.GET, CommonUrlConfig.getPhoneCode, params);
    }

    /**
     * 获取返回结果
     */
    private void Response() {
        Map params = new HashMap();

        params.put("phone", et_phone.getText().toString());
        params.put("password", et_password.getText().toString());
        params.put("code", et_code.getText().toString());
        params.put("deviceid", AppBalala.getDeviceId);
        params.put("Source", "2");

        GetJson.Callback callback = new GetJson.Callback() {
            @Override
            public void onFinish(String response) {
                CommonListResult<TokenInfo> results = JsonUtil.fromJson(response, new TypeToken<CommonListResult<TokenInfo>>() {
                }.getType());
                if (results.code.equals(CommonUrlConfig.RequestState.OK)) {

                    String token = results.data.get(0).Token;
                    String userid = results.data.get(0).Userid;
                    sp.saveLoginInfo(true, token, userid);
                    AppBalala.Uid = userid;
                    AppBalala.Utoken = token;
                    AppBalala.getInstance().setLogined(true);
                    Intent intent = new Intent(NewUserActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    ToastUtils.showToast(mContext, results.message);
                }
            }

            @Override
            public void onError(VolleyError error) {
                //处理错误
                ToastUtils.showToast(mContext, "网络请求错误" + error.toString());
            }
        };
        GetJson getJson = new GetJson(NewUserActivity.this, callback, true, getString(R.string.loading));
        getJson.setConnection(Request.Method.GET, CommonUrlConfig.phoneRegister, params);
    }
}
