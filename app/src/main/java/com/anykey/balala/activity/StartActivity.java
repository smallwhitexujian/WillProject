package com.anykey.balala.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.anykey.balala.R;

import net.dev.mylib.cache.sharedPreferences.SharedPreferencesUtil;

/**
 * Created by xujian on 15/8/28.
 */
public class StartActivity extends BaseActivity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (SharedPreferencesUtil.getInstance(mContext).isAutoLogin()){
                    Intent intent = new Intent(mContext, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        },1000);
    }
}
