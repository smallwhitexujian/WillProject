package com.anykey.balala.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.anykey.balala.AppBalala;
import com.anykey.balala.CommonUrlConfig;
import com.anykey.balala.GlobalDef;
import com.anykey.balala.LocationMap.GpsTracker;
import com.anykey.balala.R;
import com.anykey.balala.Utils.MapUtil;
import com.anykey.balala.Utils.sharedUtil;
import com.anykey.balala.fragment.DynamicFragment;
import com.anykey.balala.model.DynamicModel;
import com.anykey.balala.receiver.AppBroadcastReceiver;
import com.anykey.balala.view.HeaderLayout;

import net.dev.mylib.DebugLogs;
import net.dev.mylib.Encryption;
import net.dev.mylib.JsonUtil;
import net.dev.mylib.ToastUtils;
import net.dev.mylib.cache.fileCheanCache.FileUtil;
import net.dev.mylib.cache.sharedPreferences.SharedPreferencesUtil;
import net.dev.mylib.netWorkUtil.GetJson;
import net.dev.mylib.netWorkUtil.getCode;

import java.util.HashMap;

/**
 * shanli  2015/09/11
 * 发布动态页面
 */
public class SendDynamicActivity extends BinderActivity implements View.OnClickListener {
    private EditText et_content, et_address;
    private TextView introduce_mun;
    private ImageView img;
    private GpsTracker gpsTracker;
    private String type, filePath;
    private SharedPreferencesUtil sp;
    private int code;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_dynamic);
        initView();
        initContent();
    }

    private void initContent() {
        type = getIntent().getStringExtra("type");
        filePath = getIntent().getStringExtra("filePath");
        if (type.equals("dynamic")) {
            Bitmap bitmap = AppBalala.imageCache.getBitmapCut(filePath, 720, 1280, true, FileUtil.getExtensions(filePath));
            if (bitmap == null) {
                ToastUtils.showToast(SendDynamicActivity.this, R.string.load_pic_err);
            } else {
                img.setImageBitmap(bitmap);
                filePath = AppBalala.imageCache.getCacheUploadPath(FileUtil.convertUrlToFileNameEx(filePath));
            }
        } else {
            try {
                Bitmap bitmap = AppBalala.imageCache.getCache(filePath);
                if (bitmap != null) {
                    img.setImageBitmap(bitmap);
                } else {
                    AppBalala.imageFileLoader.execute(filePath, img.getWidth(), img.getHeight(), new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.obj != null) {
                                Bitmap bitmap = (Bitmap) msg.obj;
                                img.setImageBitmap(bitmap);
                            }
                        }
                    });
                }
            } catch (Exception e) {
                System.gc();
            }
        }
    }

    private void initView() {
        headerLayout = (HeaderLayout) findViewById(R.id.headerLayout);
        headerLayout.showTitle(R.string.post);
        headerLayout.showLeftBackButton(R.string.button_cancle, null);
        headerLayout.showRightTextButton(R.color.white, R.string.button_release, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_content.getText().toString().trim().equals("")) {
                    ToastUtils.showToast(mContext, R.string.please_input_dynamic);
                    return;
                }

                //普通动态
                if (type.equals("dynamic")) {
                    SendDynamic(CommonUrlConfig.DynamicLssue, "0");

                } else if (type.equals("bar")) {
                    //bar动态
                    SendDynamic(CommonUrlConfig.BarShareDynamic, String.valueOf(AppBalala.BarId));
                }
            }
        });

        sp = SharedPreferencesUtil.getInstance(mContext);

        introduce_mun = (TextView) findViewById(R.id.introduce_mun);
        et_address = (EditText) findViewById(R.id.et_address);
        gpsTracker = new GpsTracker(SendDynamicActivity.this);
        et_address.setText(gpsTracker.getAddress());
        et_content = (EditText) findViewById(R.id.et_content);
        img = (ImageView) findViewById(R.id.img);
        img.setOnClickListener(this);
        et_content.addTextChangedListener(textWatcher);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.img:
                ShowImageActivity.Create(this, filePath);
                break;
        }
    }

    /**
     * 发送动态
     */
    private void SendDynamic(String url, String barId) {
        final HashMap<String, String> params = new HashMap<>();
        params.put("userid", sharedUtil.getInstance(mContext).getUid());
        params.put("token", sharedUtil.getInstance(mContext).getUserToken());
        params.put("content", Encryption.utf8ToUnicode(et_content.getText().toString()));
        params.put("address", Encryption.utf8ToUnicode(et_address.getText().toString()));
        params.put("barid", barId);
        GetJson.Callback callback = new GetJson.Callback() {
            @Override
            public void onFinish(String response) {
                DynamicModel results = JsonUtil.fromJson(response, DynamicModel.class);
                DynamicModel model = new DynamicModel();
                if (type.equals("bar")) {
                    Intent intent = new Intent(mContext, ChatRoomActivity.class);
                    startActivity(intent);

                    model.barid = getIntent().getStringExtra("barid");
                    model.baridx = getIntent().getStringExtra("baridx");
                    model.barname = getIntent().getStringExtra("barname");
                    model.barlevel = getIntent().getStringExtra("barlevel");
                    model.heatday = getIntent().getStringExtra("heatday");
                    model.barimage = filePath;
                    model.livestate = "";
                    model.roomserverip = getIntent().getStringExtra("roomserverip");

                } else if (type.equals("dynamic")) {
                    model.barid = "0";
                    UpPicture(results, filePath);
                }

                model.discoveryId = results.id;
                DebugLogs.e("=================response1==============" + response);
                model.nickName = sharedUtil.getInstance(mContext).getUserName();
                model.headurl = sharedUtil.getInstance(mContext).getUserPhoto();
                model.userid = sharedUtil.getInstance(mContext).getUid();
                model.imageUrl = filePath;

                model.createTime = getString(R.string.just);
                model.content = et_content.getText().toString();
                model.address = et_address.getText().toString();
                model.praiseNum = "0";
                model.commentNum = "0";
                model.isPraise = "0";
                if (DynamicFragment.instance != null) {
                    DynamicFragment.instance.AddDynamic(model);
                }

                ToastUtils.showToast(mContext, R.string.ok);

                handler.obtainMessage(GlobalDef.WM_ACTIVITY_FINISH).sendToTarget();
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
                    ToastUtils.showToast(mContext, MapUtil.getString(mContext, strCode));
                }
            }
        };
        GetJson getJson = new GetJson(mContext, callback, true, mContext.getString(R.string.loading));
        getJson.setConnection(Request.Method.POST, url, params);
    }

    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int a = 140;
            int b = a - s.length();
            handler.obtainMessage(GlobalDef.WM_CREATE_BAR_ED_MESSAGE, 0, 0, b).sendToTarget();

        }
    };

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GlobalDef.WM_CREATE_BAR_ED_MESSAGE:
                    introduce_mun.setText(msg.obj + "");
                    break;
                case GlobalDef.WM_ACTIVITY_FINISH:
                    finish();
                    break;
            }
        }
    };

    /**
     * 上传照片
     *
     * @paramtype 上传图片类型
     * @paramid Id
     * @paramimageUrl 上传图片地址
     * @paramLocalImage 本地资源图片
     */
    private void UpPicture(final DynamicModel results, final String LocalImage) {

        DebugLogs.e("发送POST" + LocalImage);
        bindService.UpPicture(results.type, results.id, LocalImage, results.priurl, sharedUtil.getInstance(mContext).getUid(), sharedUtil.getInstance(mContext).getUserToken());
    }
}