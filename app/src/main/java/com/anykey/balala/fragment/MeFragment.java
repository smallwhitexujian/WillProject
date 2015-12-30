package com.anykey.balala.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.anykey.balala.AppBalala;
import com.anykey.balala.CommonUrlConfig;
import com.anykey.balala.R;
import com.anykey.balala.Utils.Constellation;
import com.anykey.balala.Utils.MapUtil;
import com.anykey.balala.Utils.sharedUtil;
import com.anykey.balala.activity.BarIndexDetailActivity;
import com.anykey.balala.activity.MyDiscoverActivity;
import com.anykey.balala.activity.MyGiftActivity;
import com.anykey.balala.activity.MyInfoActivity;
import com.anykey.balala.activity.RechargeActivity;
import com.anykey.balala.activity.RelationsActivity;
import com.anykey.balala.activity.SettingActivity;
import com.anykey.balala.activity.ShowImageActivity;
import com.anykey.balala.activity.TaskActivity;
import com.anykey.balala.model.CommonListResult;
import com.anykey.balala.model.UserinfoModel;
import com.anykey.balala.receiver.AppBroadcastReceiver;
import com.google.gson.reflect.TypeToken;

import net.dev.mylib.JsonUtil;
import net.dev.mylib.ToastUtils;
import net.dev.mylib.cache.sharedPreferences.SharedPreferencesUtil;
import net.dev.mylib.loaderimage.GaussAmbiguity;
import net.dev.mylib.netWorkUtil.GetJson;
import net.dev.mylib.netWorkUtil.getCode;
import net.dev.mylib.time.DateUtil;
import net.dev.mylib.view.ImageView.CircularImage;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by shanli on 15/8/31.
 * Me Fragment. 个人资料
 */
public class MeFragment extends Hintfragment implements View.OnClickListener {
    private View rootView;
    private LinearLayout layout_myinfo, ly_age, ly_gift, ly_post, ly_bar, ly_relations, ly_setting, ly_earn, ly_topup;
    private CircularImage img_user_headimg;

    private TextView txt_nickname, txt_diamonds, txt_coin, txt_vip, txt_age, txt_suffer, txt_id;
    private ImageView img_sex, img_constellation, txt_Editor;
    private ProgressBar progress;
    public static boolean isLoad = false;
    private Drawable gaussImageDrawable;
    private boolean isPrepared;
    private SharedPreferencesUtil sp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_me, null);
        isPrepared = true;
        initView();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        gaussImageDrawable = null;
        if (isLoad && AppBalala.userinfomodel != null) {
            findData();
        } else {
            initContent();
        }
    }

    private void initContent() {
        UserInformation();
    }

    private void initView() {
        //定义
        img_user_headimg = (CircularImage) rootView.findViewById(R.id.img_user_headimg);
        txt_nickname = (TextView) rootView.findViewById(R.id.txt_nickname);
        txt_Editor = (ImageView) rootView.findViewById(R.id.txt_Editor);
        progress = (ProgressBar) rootView.findViewById(R.id.progress);
        layout_myinfo = (LinearLayout) rootView.findViewById(R.id.layout_myinfo);
        layout_myinfo.setBackgroundResource(R.drawable.me_bg);
        txt_diamonds = (TextView) rootView.findViewById(R.id.txt_diamonds);
        txt_coin = (TextView) rootView.findViewById(R.id.txt_coin);
        txt_vip = (TextView) rootView.findViewById(R.id.txt_vip);
        txt_age = (TextView) rootView.findViewById(R.id.txt_age);
        txt_suffer = (TextView) rootView.findViewById(R.id.txt_suffer);
        img_sex = (ImageView) rootView.findViewById(R.id.img_sex);
        ly_age = (LinearLayout) rootView.findViewById(R.id.ly_age);
        img_constellation = (ImageView) rootView.findViewById(R.id.img_constellation);
        ly_gift = (LinearLayout) rootView.findViewById(R.id.ly_gift);
        ly_post = (LinearLayout) rootView.findViewById(R.id.ly_post);
        ly_bar = (LinearLayout) rootView.findViewById(R.id.ly_bar);
        ly_relations = (LinearLayout) rootView.findViewById(R.id.ly_relations);
        ly_setting = (LinearLayout) rootView.findViewById(R.id.ly_setting);
        txt_id = (TextView) rootView.findViewById(R.id.txt_id);

        ly_topup = (LinearLayout) rootView.findViewById(R.id.ly_topup);
        ly_earn = (LinearLayout) rootView.findViewById(R.id.ly_earn);
        //事件
        txt_Editor.setOnClickListener(this);
        img_user_headimg.setOnClickListener(this);
        layout_myinfo.setOnClickListener(this);
        ly_gift.setOnClickListener(this);
        ly_post.setOnClickListener(this);
        ly_bar.setOnClickListener(this);
        ly_relations.setOnClickListener(this);
        ly_setting.setOnClickListener(this);

        ly_topup.setOnClickListener(this);
        ly_earn.setOnClickListener(this);

        sp = SharedPreferencesUtil.getInstance(mContext);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_Editor:
                Intent myInfoActivity = new Intent(mContext, MyInfoActivity.class);
                startActivity(myInfoActivity);
                break;
            case R.id.img_user_headimg:
                ShowImageActivity.Create(mContext, AppBalala.userinfomodel.headurl.replace("_ex.", "."), AppBalala.userinfomodel.headurl);
                break;
            case R.id.ly_gift:
                Intent giftActivity = new Intent(mContext, MyGiftActivity.class);
                startActivity(giftActivity);
                break;
            case R.id.ly_post:
                Intent mydynamincActivity = new Intent(mContext, MyDiscoverActivity.class);
                mydynamincActivity.putExtra("url", CommonUrlConfig.PersonalDynamics);
                mydynamincActivity.putExtra("title", getString(R.string.title_activity_my_post));
                mydynamincActivity.putExtra("fuserid", sp.getUserId());
                startActivity(mydynamincActivity);
                break;
            case R.id.ly_bar:
                Intent intent = new Intent(mContext, BarIndexDetailActivity.class);
                intent.putExtra("type", "MyBar");
                intent.putExtra("barName", getString(R.string.title_activity_my_bar));
                intent.putExtra("userId", sharedUtil.getInstance(getActivity()).getUid());
                startActivity(intent);
                break;
            case R.id.ly_relations:
                Intent i = new Intent(getActivity(), RelationsActivity.class);
                getActivity().startActivity(i);
                break;
            case R.id.ly_setting:
                Intent setting = new Intent(getActivity(), SettingActivity.class);
                startActivity(setting);
                break;
            case R.id.ly_earn:
                Intent intentTask = new Intent(getActivity(), TaskActivity.class);
                startActivity(intentTask);
                break;
            case R.id.ly_topup:
                startActivity(new Intent(getActivity(), RechargeActivity.class));
                break;
        }
    }

    private void findData() {
        img_user_headimg.setImageUrl(AppBalala.userinfomodel.headurl);
        Bitmap bitmap = AppBalala.imageCache.getCache(sp.getUserHead());
        if (bitmap != null) {
            gaussImageDrawable = GaussAmbiguity.BlurImages(bitmap, getActivity());
            layout_myinfo.setBackgroundDrawable(gaussImageDrawable);
        } else {
            AppBalala.imageFileLoader.execute(sp.getUserHead(), layout_myinfo.getWidth(), layout_myinfo.getHeight(), new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.obj != null) {
                        Bitmap bitmap = (Bitmap) msg.obj;
                        gaussImageDrawable = GaussAmbiguity.BlurImages(bitmap, getActivity());
                        layout_myinfo.setBackgroundDrawable(gaussImageDrawable);
                    }
                }
            });
        }
        txt_nickname.setText(AppBalala.userinfomodel.nickname);
        txt_id.setText("ID:" + AppBalala.userinfomodel.prettyid);
        txt_coin.setText(String.valueOf(AppBalala.Coin));
        txt_diamonds.setText(String.valueOf(AppBalala.Diamonds));
        txt_vip.setText("LV." + AppBalala.userinfomodel.userlevel);
        txt_age.setText(AppBalala.userinfomodel.age);
        txt_suffer.setText(AppBalala.userinfomodel.suffer);
        progress.setProgress(Integer.parseInt(AppBalala.userinfomodel.sufferpercent));
        //img_constellation.setImageResource();
        Date birthday;

        try {
            //将用户生日转化为日期类型
            birthday = DateUtil.ConverToDate(AppBalala.userinfomodel.birthday, DateUtil.FormatString);
        } catch (Exception ex) {
            //如果转化失败，获取当前时间
            birthday = Calendar.getInstance().getTime();
        }
        String constellation = Constellation.getConstellation(birthday.getMonth() + 1, birthday.getDate());
        AppBalala.userinfomodel.constellation = constellation;
        img_constellation.setImageResource(Constellation.getConstellationImage(constellation));
        if (AppBalala.userinfomodel.sex.equals("1")) {
            ly_age.setBackgroundResource(R.drawable.me_top_icon_gender);
            img_sex.setImageResource(R.drawable.me_top_icon_male);
        } else {
            ly_age.setBackgroundResource(R.drawable.me_top_icon_female_bg);
            img_sex.setImageResource(R.drawable.me_top_icon_female);
        }
    }

    /**
     * 获取用户信息
     */
    private void UserInformation() {
        HashMap<String, String> params = new HashMap<>();
        params.put("userid", sharedUtil.getInstance(mContext).getUid());
        params.put("token", sharedUtil.getInstance(mContext).getUserToken());
        GetJson.Callback callback = new GetJson.Callback() {
            @Override
            public void onFinish(String response) {
                CommonListResult<UserinfoModel> results = JsonUtil.fromJson(response, new TypeToken<CommonListResult<UserinfoModel>>() {
                }.getType());

                if (results.hasData()) {
                    isLoad = true;
                    AppBalala.userinfomodel = results.data.get(0);
                    sharedUtil.getInstance(mContext).setUserPhoto(results.data.get(0).headurl);
                    sharedUtil.getInstance(mContext).setUserName(results.data.get(0).nickname);
                    AppBalala.Coin = Long.parseLong(AppBalala.userinfomodel.coin);
                    AppBalala.Diamonds = Long.parseLong(AppBalala.userinfomodel.diamonds);
                    findData();
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
                    ToastUtils.showToast(getActivity(), MapUtil.getString(mContext, strCode));
                }
            }
        };

        GetJson getJson = new GetJson(getActivity(), callback, true, getString(R.string.loading));
        getJson.setConnection(Request.Method.GET, CommonUrlConfig.UserInformation, params);
    }

    /**
     * 懒加载，看到这个界面则加载
     */
    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible) {
            return;
        }
    }
}