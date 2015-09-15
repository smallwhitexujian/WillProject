package com.anykey.balala.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.anykey.balala.AppBalala;
import com.anykey.balala.CommonUrlConfig;
import com.anykey.balala.R;
import com.anykey.balala.model.CommonData;
import com.google.gson.reflect.TypeToken;

import net.dev.mylib.DebugLogs;
import net.dev.mylib.JsonUtil;
import net.dev.mylib.ToastUtils;
import net.dev.mylib.netWorkUtil.GetJson;
import net.dev.mylib.time.DateTimePicker.DateTimePickerDialog;
import net.dev.mylib.time.calendar.DateFormat;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by jjfly on 15-9-8.
 */
public class ProfileActivity extends BaseActivity {


    private EditText mNickEdiText;
    private EditText mBirthdayEditText;

    private Button mNext;
    private RadioGroup mRadioGrop;
    private int mGender;


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

    private void initView(){
        mNickEdiText = (EditText)findViewById(R.id.profile_usernick);
        mBirthdayEditText = (EditText)findViewById(R.id.profile_birthday);
        mRadioGrop = (RadioGroup)findViewById(R.id.profile_radio_group);
        mRadioGrop.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.profile_gender_female){
                    mGender = AppBalala.GENDEWR_FEFALE;
                    ToastUtils.showToast(ProfileActivity.this, "女");

                }
                else{
                    mGender = AppBalala.GENDER_MALE;
                    ToastUtils.showToast(ProfileActivity.this, "男");
                }

            }
        });

        mNext = (Button)findViewById(R.id.profile_next);
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                AppBalala.Uid = SharedPreferencesUtil.getInstance(mContext).getString("UserId", "");
//                AppBalala.Utoken = SharedPreferencesUtil.getInstance(mContext).getString("Token", "");
                String nickNameStr = mNickEdiText.getText().toString().trim();
                String birthdayStr = mBirthdayEditText.getText().toString().trim();
                if ("".equals(nickNameStr)) {
                    return;
                }
                if ("".equals(birthdayStr)) {
                    return;
                }
                Map<String, String> param = new HashMap<String, String>();
                String nickName = nickNameStr;
                String birthday = birthdayStr;
                String userId = AppBalala.Uid;
                String token = AppBalala.Utoken;
                param.put("userid", userId);
                param.put("nickName", nickName);
                param.put("sex", mGender + "");
                param.put("token", token);
                param.put("birthday", birthday);
                Response(ProfileActivity.this, param, CommonUrlConfig.userRegInfoFill, null);
            }
        });



        mBirthdayEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    long dateValue = 0;
                    try {
                        DebugLogs.i("date   " + mBirthdayEditText.getText().toString());
                        dateValue = DateFormat.getDateValue(mBirthdayEditText.getText().toString(), "yyyy-MM-dd");
                    } catch (ParseException e) {
                        e.printStackTrace();
                        dateValue = System.currentTimeMillis();
                        DebugLogs.i("date erro .............");
                    }
                    DateTimePickerDialog dialog = new DateTimePickerDialog(ProfileActivity.this, dateValue, true);
                    dialog.setOnDateTimeSetListener(new DateTimePickerDialog.OnDateTimeSetListener() {
                        @Override
                        public void OnDateTimeSet(AlertDialog dialog, long date) {
                            DebugLogs.i("date " + date);
                            String dateStr = DateFormat.formatDate(date, "yyyy-MM-dd");
                            mBirthdayEditText.setText(dateStr);
                        }
                    });
                    dialog.show();
                    mBirthdayEditText.clearFocus();
                }
            }
        });
    }

    private void Response(Context context,Map<String,String> params,String url,String prompt) {
        if(prompt == null){
            prompt = "咻咻咻...";
        }
        GetJson.Callback callback = new GetJson.Callback() {
            @Override
            public void onFinish(String response) {
                //处理结果
                DebugLogs.i("response is "+response);
                CommonData result = JsonUtil.fromJson(response, new TypeToken<CommonData>() {}.getType());
                if(result == null){
                    DebugLogs.i("response is "+response);
                    return;
                }
                String code = result.code;
                String message = result.message;
                if(CommonUrlConfig.RequestState.OK.equals(code)){
                    Intent i = new Intent(ProfileActivity.this,UploadInfoActivity.class);
                    startActivity(i);
                }
                else{
                    ToastUtils.showToast(ProfileActivity.this,message);
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



}



