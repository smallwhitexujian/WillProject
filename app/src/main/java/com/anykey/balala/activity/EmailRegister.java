package com.anykey.balala.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.anykey.balala.AppBalala;
import com.anykey.balala.CommonUrlConfig;
import com.anykey.balala.R;
import com.anykey.balala.model.CommonListResult;
import com.anykey.balala.model.TokenInfo;
import com.google.gson.reflect.TypeToken;

import net.dev.mylib.DebugLogs;
import net.dev.mylib.JsonUtil;
import net.dev.mylib.ToastUtils;
import net.dev.mylib.Utility;
import net.dev.mylib.cache.sharedPreferences.SharedPreferencesUtil;
import net.dev.mylib.netWorkUtil.GetJson;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jjfly on 15-9-6.
 */
public class EmailRegister extends BaseActivity implements View.OnClickListener {


    private EditText mEmailAddress,mPasswordInput;
    private Button mFinishBtn;
    private SharedPreferencesUtil sp = SharedPreferencesUtil.getInstance(mContext);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_register);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.email_register_finish_btn:
                Map<String,String> param = new HashMap<String,String>();
                param.put("email",mEmailAddress.getText().toString());
                param.put("password",mPasswordInput.getText().toString());
                param.put("deviceid", Utility.getImeiCode(EmailRegister.this));//设备id
                param.put("lat","0");//经度
                param.put("log","0");//纬度
                param.put("source","2");//1、ios;2、android
                Response(EmailRegister.this, param, CommonUrlConfig.emailRegister, null);
                break;
        }
    }

    private void initView(){
        mEmailAddress = (EditText)findViewById(R.id.email_register_address);
        mFinishBtn = (Button)findViewById(R.id.email_register_finish_btn);
        mPasswordInput = (EditText)findViewById(R.id.email_register_password);

        mFinishBtn.setOnClickListener(this);
    }



    private void Response(Context context,Map<String,String> params,String url,String prompt) {
        if(prompt == null){
            prompt = "正在努力加载中...";
        }
        GetJson.Callback callback = new GetJson.Callback() {
            @Override
            public void onFinish(String response) {
                //处理结果
                DebugLogs.i("response is "+response);
//                CommonModel result = JsonUtil.fromJson(response, new TypeToken<CommonListResult<TokenInfo>>(){}.getType());
                CommonListResult<TokenInfo> result = JsonUtil.fromJson(response, new TypeToken<CommonListResult<TokenInfo>>() {}.getType());
                DebugLogs.i("response "+result.data.get(0).Userid+"-----");
                    String code = result.code;
                    String message = result.message;
                    if(CommonUrlConfig.RequestState.OK.equals(code)){
                        DebugLogs.i("response is " + response);
                        String userId =  result.data.get(0).Userid;
                        String Token =  result.data.get(0).Token;
                        sp.saveLoginInfo(true,Token,userId);
                        AppBalala.Uid = userId;
                        AppBalala.Utoken = Token;
                        DebugLogs.i("response userId is "+userId+"--"+"Token is "+Token);
                        ToastUtils.showToast(EmailRegister.this, message, ToastUtils.TOAST_DURATION);
                        Intent i = new Intent(EmailRegister.this,ProfileActivity.class);
                        startActivity(i);
                    }
                    else{
                        ToastUtils.showToast(EmailRegister.this,message,ToastUtils.TOAST_DURATION);
                    }
            }

            @Override
            public void onError(VolleyError error) {
                //处理错误

            }
        };
        GetJson getJson = new GetJson(context,callback,true,prompt);
        getJson.setConnection(Request.Method.GET, url, params);
    }


    //
    private void restore(){

    }
}
