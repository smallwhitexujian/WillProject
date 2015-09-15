package com.anykey.balala.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.anykey.balala.R;
import com.anykey.balala.activity.LoginActivity;
import com.anykey.balala.activity.MainActivity;
import com.facebook.AccessToken;
import net.dev.mylib.cache.sharedPreferences.SharedPreferencesUtil;
import net.dev.mylib.view.ImageView.RemoteImageView;

/**
 * Created by xujian on 15/8/31.
 * Me Fragment. 个人资料
 */
public class MeFragment extends Hintfragment {
    private View rootView;
    private Button btn_logout;
    private RemoteImageView img;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_me, null);
        btn_logout = (Button) rootView.findViewById(R.id.btn_logout);
        img = (RemoteImageView) rootView.findViewById(R.id.img);
        img.setDefaultImage(R.drawable.pictures_no);
        img.setImageUrl("http://hi.csdn.net/attachment/201110/30/0_1319976939pkgr.gif");
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesUtil.getInstance(getContext()).clearUserInfo();
                AccessToken.setCurrentAccessToken(null);
                Intent loginActivity = new Intent(getContext(), LoginActivity.class);
                startActivity(loginActivity);
                MainActivity parentActivity = (MainActivity) getActivity();
                parentActivity.close();
            }
        });
        return rootView;
    }

    /**
     * 懒加载，看到这个界面则加载
     */
    @Override
    protected void lazyLoad() {
    }
}