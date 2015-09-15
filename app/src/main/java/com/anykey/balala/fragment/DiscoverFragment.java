package com.anykey.balala.fragment;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import com.anykey.balala.CommonResultCode;
import com.anykey.balala.CommonUrlConfig;
import com.anykey.balala.R;
import com.anykey.balala.Utils.PictureObtain;
import com.anykey.balala.adapter.CommonAdapter;
import com.anykey.balala.adapter.ViewHolder;
import com.anykey.balala.model.CommonListResult;
import com.anykey.balala.model.DynamicModel;
import com.google.gson.reflect.TypeToken;
import net.dev.mylib.JsonUtil;
import net.dev.mylib.ToastUtils;
import net.dev.mylib.cache.sharedPreferences.SharedPreferencesUtil;
import net.dev.mylib.netWorkUtil.GetJson;
import net.dev.mylib.view.ActionSheetDialog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shanli on 15/9/11.
 * Discover Fragment.
 */
public class DiscoverFragment extends Hintfragment implements View.OnClickListener {

    //控件定义
    private View rootView;
    private ListView listView;
    private ImageView button;
    private CommonAdapter mAdapter;
    private PictureObtain mObtain;
    private SharedPreferencesUtil sp;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_discover, null);
        initView();
        initContent();
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                new ActionSheetDialog(getContext())
                        .builder()
                        .setCancelable(true)
                        .setCanceledOnTouchOutside(true)
                        .addSheetItem("拍照", ActionSheetDialog.SheetItemColor.Blue,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        mObtain.dispatchTakePictureIntent(getActivity(), CommonResultCode.SET_ADD_PHOTO_CAMERA);
                                    }
                                })
                        .addSheetItem("从手机相册选择", ActionSheetDialog.SheetItemColor.Blue,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        //choose_pic();
                                        mObtain.getLocalPicture(getActivity(), CommonResultCode.SET_ADD_PHOTO_ALBUM);
                                    }
                                }).show();
                break;
        }
    }

    private void initContent() {
        mObtain = new PictureObtain();
        sp = SharedPreferencesUtil.getInstance(getContext());
        Response();
    }

    private void initView() {
        listView = (ListView) rootView.findViewById(R.id.listView);
        button = (ImageView) rootView.findViewById(R.id.button);
        button.setOnClickListener(this);
    }

    /**
     * 获取返回结果
     */
    private void Response() {
        Map params = new HashMap();
        params.put("userid", sp.getUserId());
        params.put("pageindex", "1");
        params.put("pagesize", "10");
        params.put("sources", "2");
        params.put("token", sp.getToken());
        GetJson.Callback callback = new GetJson.Callback() {
            @Override
            public void onFinish(String response) {
                CommonListResult<DynamicModel> results = JsonUtil.fromJson(response, new TypeToken<CommonListResult<DynamicModel>>() {
                }.getType());
                if (results.code.equals(CommonUrlConfig.RequestState.OK) && results.hasData()) {
                    // 设置适配器
                    listView.setAdapter(mAdapter = new CommonAdapter<DynamicModel>(
                            getActivity().getApplicationContext(), results.data, R.layout.lst_dynamic) {
                        @Override
                        public void convert(ViewHolder helper, DynamicModel item) {
                            final String title = item.nickName;
                            helper.setText(R.id.tv_title, item.nickName);
                            helper.setText(R.id.tv_describe, item.content);
                            helper.setText(R.id.tv_address, item.address);
                            helper.setText(R.id.tv_time, item.createTime);
                            helper.setImageByUrl(R.id.img_body, item.imageUrl);
                            helper.setImageByUrl(R.id.img_head, item.headurl);
//                //整行监听
                            helper.getConvertView().setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ToastUtils.showToast(mContext, "点击的是整行");
                                }
                            });
                            //标题监听
                            helper.getView(R.id.tv_title).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ToastUtils.showToast(getActivity(), title);
                                }
                            });
                        }
                    });
                } else {
                    ToastUtils.showToast(getContext(), results.message);
                }
            }

            @Override
            public void onError(VolleyError error) {
                //处理错误
                ToastUtils.showToast(getContext(), "网络请求错误" + error.toString());
            }
        };
        GetJson getJson = new GetJson(getContext(), callback, true, getString(R.string.loading));
        getJson.setConnection(Request.Method.GET, CommonUrlConfig.DiscoverySquare, params);
    }

    /**
     * 懒加载，看到这个界面则加载
     */
    @Override
    protected void lazyLoad() {
    }
}