package com.anykey.balala.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.os.Environment;

import com.anykey.balala.CommonResultCode;
import com.anykey.balala.CommonUrlConfig;
import com.anykey.balala.R;
import com.anykey.balala.model.CommonData;

import net.dev.mylib.JsonUtil;
import net.dev.mylib.ToastUtils;
import net.dev.mylib.cache.sharedPreferences.SharedPreferencesUtil;
import net.dev.mylib.netWorkUtil.HttpPostFile;
import net.dev.mylib.view.ImageView.RemoteImageView;
import net.dev.mylib.view.LoadingDialog;

import java.util.HashMap;
import java.util.Map;

/**
 * shanli  2015/09/11
 * 发布动态页面
 */
public  class SendDynamicActivity extends BaseActivity  implements View.OnClickListener {
    private Button btn_send;
    private EditText et_content,et_address;
    private RemoteImageView img;
    private SharedPreferencesUtil sp = SharedPreferencesUtil.getInstance(mContext);
    private String filePath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_dynamic);
        initView();
        initContent();
    }

    private void initContent() {
        filePath = getIntent().getStringExtra("filePath");
        img.setImageUrl(filePath);
    }

    private void initView() {
        btn_send = (Button) findViewById(R.id.btn_send);
        et_address = (EditText) findViewById(R.id.et_address);
        btn_send.setOnClickListener(this);
        et_content = (EditText) findViewById(R.id.et_content);
        img = (RemoteImageView) findViewById(R.id.img);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id)
        {
            case R.id.btn_send:
                send();
                break;
        }
    }

    private void send() {
        LoadingDialog.showSysLoadingDialog(mContext, getString(R.string.loading));
        btn_send.setEnabled(false);
        new Thread() {
            public void run() {
                String pathString = CommonUrlConfig.DynamicLssue;
                Map params = new HashMap();
                params.put("userid", String.valueOf(sp.getUserId()));
                params.put("content", et_content.getText().toString());
                params.put("address", et_address.getText().toString());
                params.put("barid", "0");
                params.put("token",sp.getToken());
                Map fileparams = new HashMap();
                fileparams.put("imageurl",filePath);

                String resultJsonData = "";
                try {
                    resultJsonData = HttpPostFile.uploadFile(pathString,params,fileparams);
                } catch (Exception e) {
                    resultJsonData = e.getMessage();
                }
                Message msg = mainHandler.obtainMessage(
                        CommonResultCode.SEND_DYNAMIC_CODE, resultJsonData);
                mainHandler.sendMessage(msg);
            }
        }.start();
    }

    private Handler mainHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int nCommand = msg.what;
            switch (nCommand)
            {
                case CommonResultCode.SEND_DYNAMIC_CODE:
                    LoadingDialog.cancelLoadingDialog();
                    CommonData results = JsonUtil.fromJson(msg.obj.toString(), CommonData.class);
                    if (results.code.equals(CommonUrlConfig.RequestState.OK)) {
                        ToastUtils.showToast(SendDynamicActivity.this,results.message);
                        finish();
                    }
                    else
                    {
                        ToastUtils.showToast(SendDynamicActivity.this,results.message);
                        btn_send.setEnabled(true);
                    }
                    et_content.setText( msg.obj.toString());
                    break;
            }
        }
    };
}