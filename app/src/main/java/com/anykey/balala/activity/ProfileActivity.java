package com.anykey.balala.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.anykey.balala.AppBalala;
import com.anykey.balala.CommonResultCode;
import com.anykey.balala.CommonUrlConfig;
import com.anykey.balala.R;
import com.anykey.balala.Utils.MapUtil;
import com.anykey.balala.Utils.PictureObtain;
import com.anykey.balala.Utils.sharedUtil;
import com.anykey.balala.receiver.AppBroadcastReceiver;
import com.anykey.balala.view.HeaderLayout;

import net.dev.mylib.Encryption;
import net.dev.mylib.ToastUtils;
import net.dev.mylib.cache.sharedPreferences.SharedPreferencesUtil;
import net.dev.mylib.netWorkUtil.GetJson;
import net.dev.mylib.netWorkUtil.getCode;
import net.dev.mylib.time.DateUtil;
import net.dev.mylib.view.ActionSheetDialog;
import net.dev.mylib.view.ImageView.CircularImage;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sandy
 * 注册时完善个人资料 on 15-9-8.
 */
public class ProfileActivity extends BinderActivity {

    private EditText txt_name;
    private TextView mBirthdayEditText, txt_service;
    private Button mNext;
    private RadioGroup mRadioGrop;
    private PictureObtain mObtain;
    private int mGender = 1;
    private CircularImage img_head;
    private DatePickerDialog dpd;
    private Calendar cal = Calendar.getInstance();

    private SharedPreferencesUtil sp = SharedPreferencesUtil.getInstance(mContext);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        headerLayout = (HeaderLayout) findViewById(R.id.headerLayout);
        headerLayout.showTitle(R.string.create_your_profile);
        headerLayout.showLeftBackButton();
        mObtain = new PictureObtain();
        img_head = (CircularImage) findViewById(R.id.img_head);
        txt_name = (EditText) findViewById(R.id.txt_name);
        mBirthdayEditText = (TextView) findViewById(R.id.profile_birthday);
        mRadioGrop = (RadioGroup) findViewById(R.id.profile_radio_group);
        mRadioGrop.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.profile_gender_female) {
                    mGender = AppBalala.GENDEWR_FEFALE;
                } else {
                    mGender = AppBalala.GENDER_MALE;
                }
            }
        });

        img_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ActionSheetDialog(mContext)
                        .builder()
                        .setCancelable(true)
                        .setCanceledOnTouchOutside(true)
                        .addSheetItem(getString(R.string.camera), ActionSheetDialog.SheetItemColor.Blue,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        mObtain.dispatchTakePictureIntent(ProfileActivity.this, CommonResultCode.SET_ADD_PHOTO_CAMERA);
                                    }
                                })
                        .addSheetItem(getString(R.string.album), ActionSheetDialog.SheetItemColor.Blue,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        mObtain.getLocalPicture(ProfileActivity.this, CommonResultCode.SET_ADD_PHOTO_ALBUM);
                                    }
                                }).show();

            }
        });
        dpd = new DatePickerDialog(this,
                listener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        );

        mNext = (Button) findViewById(R.id.profile_next);
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickNameStr = txt_name.getText().toString().trim();
                String birthdayStr = mBirthdayEditText.getText().toString().trim();
                if ("".equals(nickNameStr)) {
                    ToastUtils.showToast(ProfileActivity.this, R.string.please_input_nickname);
                    return;
                }
                if ("".equals(birthdayStr)) {
                    ToastUtils.showToast(ProfileActivity.this, R.string.please_input_birthday);
                    return;
                }
                Map<String, String> param = new HashMap<>();
                param.put("userid", sharedUtil.getInstance(mContext).getUid());
                param.put("token", sharedUtil.getInstance(mContext).getUserToken());
                param.put("nickname", Encryption.utf8ToUnicode(nickNameStr));
                param.put("sex", String.valueOf(mGender));
                param.put("birthday", birthdayStr);
                Update(param);
            }
        });
        mBirthdayEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dpd.show();
            }
        });

        txt_service = (TextView) findViewById(R.id.txt_service);
        txt_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent webActivity = new Intent(ProfileActivity.this, WebActivity.class);
                webActivity.putExtra(WebActivity.URL_KEY, CommonUrlConfig.Agreement);
                startActivity(webActivity);
            }
        });
    }

    /**
     * 修改用户信息
     */
    private void Update(Map<String, String> params) {
        GetJson.Callback callback = new GetJson.Callback() {
            @Override
            public void onFinish(String response) {
                sp.saveLoginInfo(true, sharedUtil.getInstance(mContext).getUserToken(), sharedUtil.getInstance(mContext).getUid(),
                        txt_name.getText().toString(), sharedUtil.getInstance(mContext).getUserPhoto(), sharedUtil.getInstance(mContext).getUserLevel());
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onError(VolleyError error) {
                getCode.hasCode errorCode = ((getCode.hasCode) error);
                String strCode = errorCode.errorCode;
                if (strCode.equals(CommonUrlConfig.RequestState.Logout)) {
                    Intent voiceIntent = new Intent();
                    voiceIntent.setAction(AppBroadcastReceiver.BROADCAST_USER_LOGOUT);
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(voiceIntent);
                } else {
                    ToastUtils.showToast(ProfileActivity.this, MapUtil.getString(mContext, strCode));
                }
            }
        };
        GetJson getJson = new GetJson(mContext, callback, true, mContext.getString(R.string.loading));
        getJson.setConnection(Request.Method.POST, CommonUrlConfig.UserInformationEdit, params);
    }

    /**
     * 日历控件选择后
     */
    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {  //
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            cal.set(Calendar.YEAR, arg1);
            cal.set(Calendar.MONTH, arg2);
            cal.set(Calendar.DAY_OF_MONTH, arg3);
            mBirthdayEditText.setText(DateUtil.DateFormat(cal.getTime(), "yyyy-MM-dd"));
        }
    };

    private void UpPicture(final String LocalImage) {

        bindService.UpPicture("1", sharedUtil.getInstance(mContext).getUid(), LocalImage, CommonUrlConfig.PicUpload,
                sharedUtil.getInstance(mContext).getUid(), sharedUtil.getInstance(mContext).getUserToken());

    }

    Uri distUri;

    /**
     * 接收用户返回头像参数
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri;
            switch (requestCode) {
                case CommonResultCode.SET_ADD_PHOTO_CAMERA:
                    //拍照
                    distUri = mObtain.obtainUrl();
                    mObtain.notifyChange(ProfileActivity.this, mObtain.getUri(mContext));
                    mObtain.cropBig(ProfileActivity.this, mObtain.getUri(mContext), distUri, CommonResultCode.REQUEST_CROP_PICTURE, 800, 800);
                    break;
                case CommonResultCode.SET_ADD_PHOTO_ALBUM:
                    //从相册获取
                    if (data != null) {
                        distUri = mObtain.obtainUrl();
                        mObtain.cropBig(ProfileActivity.this, data.getData(), distUri, CommonResultCode.REQUEST_CROP_PICTURE, 800, 800);
                    }
                    break;
                case CommonResultCode.REQUEST_CROP_PICTURE:
                    //裁剪后的图片
                    String spath = mObtain.getRealPathFromURI(ProfileActivity.this, distUri);
                    if (!new File(spath).exists()) {
                        return;
                    }
                    sharedUtil.getInstance(ProfileActivity.this).setUserPhoto(spath);
                    try {
                        Bitmap bitmap = AppBalala.imageCache.getCache(spath);
                        if (bitmap != null) {
                            img_head.setImageBitmap(bitmap);
                        } else {
                            AppBalala.imageFileLoader.execute(spath, img_head.getWidth(), img_head.getHeight(), new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    if (msg.obj != null) {
                                        Bitmap bitmap = (Bitmap) msg.obj;
                                        img_head.setImageBitmap(bitmap);
                                    }
                                }
                            });
                        }
                    } catch (Exception e) {
                        System.gc();
                    }
                    UpPicture(spath);
                    break;
            }
        }
    }
}