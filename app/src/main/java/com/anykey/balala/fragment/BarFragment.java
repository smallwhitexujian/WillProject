package com.anykey.balala.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.anykey.balala.AppBalala;
import com.anykey.balala.CommonUrlConfig;
import com.anykey.balala.Constant;
import com.anykey.balala.GlobalDef;
import com.anykey.balala.R;
import com.anykey.balala.Utils.CategoryTabStrip;
import com.anykey.balala.Utils.ListenRoom;
import com.anykey.balala.Utils.MapUtil;
import com.anykey.balala.Utils.sharedUtil;
import com.anykey.balala.activity.ChatRoomActivity;
import com.anykey.balala.activity.CreateMoneyActivity;
import com.anykey.balala.adapter.CommonAdapter;
import com.anykey.balala.adapter.ViewHolder;
import com.anykey.balala.model.CommonListResult;
import com.anykey.balala.model.RoomModel;
import com.anykey.balala.model.SearchBarModel;
import com.anykey.balala.receiver.AppBroadcastReceiver;
import com.anykey.balala.view.HeaderLayout;
import com.google.gson.reflect.TypeToken;

import net.dev.mylib.DebugLogs;
import net.dev.mylib.Encryption;
import net.dev.mylib.JsonUtil;
import net.dev.mylib.ToastUtils;
import net.dev.mylib.netWorkUtil.GetJson;
import net.dev.mylib.netWorkUtil.NetWorkUtil;
import net.dev.mylib.netWorkUtil.getCode;
import net.dev.mylib.view.ImageView.CircularImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xujian on 15/8/31.
 * Bar fragment. chatRoom
 */
public class BarFragment extends Hintfragment implements View.OnClickListener {
    private View rootView;
    private List<Fragment> mFragments = new ArrayList<>();
    private List<String> tabTexts = new ArrayList<>();
    private CategoryTabStrip tabs;
    private ViewPager pager;
    private MyPagerAdaper adaper;
    private LinearLayout layout_isOnHook;
    private CommonListResult<SearchBarModel> results;
    private RelativeLayout category;
    private ImageView Attention_isOnHook, Share_isOnHook, exit_isOnHook;
    private TextView barName_isOnHook, synopsis_isOnHook, strType;
    private CircularImage head_isOnHook;
    private boolean isAtt = true;
    private List<SearchBarModel> commentData = new ArrayList<>();
    private ListView dailog_listView;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rootView = inflater.inflate(R.layout.fragment_bar, null);
                initview();
                initData();
            }
        });
        return rootView;
    }

    private void initview() {
        category = (RelativeLayout) rootView.findViewById(R.id.category_layout);
        tabs = (CategoryTabStrip) rootView.findViewById(R.id.category_strip);
        pager = (ViewPager) rootView.findViewById(R.id.view_pager);
        layout_isOnHook = (LinearLayout) rootView.findViewById(R.id.layout_isOnHook);
        head_isOnHook = (CircularImage) rootView.findViewById(R.id.head_isOnHook);
        Attention_isOnHook = (ImageView) rootView.findViewById(R.id.Attention_isOnHook);
        Share_isOnHook = (ImageView) rootView.findViewById(R.id.Share_isOnHook);
        AnimationDrawable animationDrawable = (AnimationDrawable) Share_isOnHook.getDrawable();
        animationDrawable.start();
        exit_isOnHook = (ImageView) rootView.findViewById(R.id.exit_isOnHook);
        barName_isOnHook = (TextView) rootView.findViewById(R.id.barName_isOnHook);
        synopsis_isOnHook = (TextView) rootView.findViewById(R.id.synopsis_isOnHook);
        headerLayout = (HeaderLayout) rootView.findViewById(R.id.headerLayout);

        layout_isOnHook.setVisibility(View.GONE);
        layout_isOnHook.setOnClickListener(this);
        Attention_isOnHook.setOnClickListener(this);
        exit_isOnHook.setOnClickListener(this);
        headerLayout.showTitle("");
        headerLayout.showLeftImageButton(R.drawable.bar_top_icon, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        headerLayout.showRightImageButton(R.drawable.bar_top_search, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSreachDialog();
            }
        });
        headerLayout.showRightImageButton(R.drawable.bar_top_add, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateMoneyActivity.class);
                startActivity(intent);
            }
        });
        if (tabTexts.size() <= 0) {
            category.setVisibility(View.GONE);
        } else {
            category.setVisibility(View.VISIBLE);
        }
        adaper = new MyPagerAdaper(getActivity().getSupportFragmentManager(), mFragments);
        pager.setAdapter(adaper);
        pager.setCurrentItem(0);
        tabs.setViewPager(pager);
        adaper.notifyDataSetChanged();
    }

    private void initData() {
        if (ChatRoomActivity.barResult != null && ChatRoomActivity.barResult.isfollow != null && ChatRoomActivity.barResult.isfollow.equals(Constant.isFollow)) {
            Attention_isOnHook.setImageResource(R.drawable.bar_bottom_praise_on);
            isAtt = true;
        } else {
            Attention_isOnHook.setImageResource(R.drawable.bar_is);
            isAtt = false;
        }
    }

    private void showSreachDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
        Window window = dialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setGravity(Gravity.CENTER);
        window.setContentView(R.layout.dialog_search_bar);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.FILL_PARENT;
        lp.height = WindowManager.LayoutParams.FILL_PARENT;
        window.setAttributes(lp);
        TextView btnCancel = (TextView) window.findViewById(R.id.btnCancel);
        dailog_listView = (ListView) window.findViewById(R.id.search_list);
        strType = (TextView) window.findViewById(R.id.strType);
        EditText searchEdit = (EditText) window.findViewById(R.id.searchEdit);
        //只用下面这一行弹出对话框时需要点击输入框才能弹出软键盘
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        //加上下面这一行弹出对话框时软键盘随之弹出
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ResponseBarSearch("");
        searchEdit.addTextChangedListener(textWatcher);
        dailog_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                dialog.dismiss();
                ListenRoom.quitRoom();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String RoomName = commentData.get(position).barname;
                        String heatday = commentData.get(position).heatday;
                        String barid = commentData.get(position).barid;
                        String roomserverip = commentData.get(position).roomserverip;
                        String RoomIpId = roomserverip.split(":")[0];
                        String RoomPort = roomserverip.split(":")[1];
                        RoomModel roomModel = new RoomModel();
                        roomModel.setId(Integer.valueOf(barid));
                        roomModel.setName(RoomName);
                        roomModel.setIp(RoomIpId);
                        roomModel.setPort(Integer.valueOf(RoomPort));
                        roomModel.setHeatDay(heatday);
                        roomModel.setLevel(commentData.get(position).barlevel);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("room", roomModel);
                        Intent intent = new Intent(mContext, ChatRoomActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
                Confirm(commentData.get(position).baridx);
            }
        });
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
            if (s.length() > 0) {
                commentData.clear();
                strType.setVisibility(View.GONE);
                ResponseBarSearch(s.toString());
            } else {
                commentData.clear();
                strType.setVisibility(View.VISIBLE);
                ResponseBarSearch("");
            }

        }
    };

    /**
     * 热门搜索
     *
     * @param idx 房间idx
     */
    private void Confirm(String idx) {
        Map<String, String> params = new HashMap<>();
        params.put("baridx", idx);
        GetJson.Callback callback = new GetJson.Callback() {
            @Override
            public void onFinish(String response) {

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
                    ToastUtils.showToast(getActivity(), MapUtil.getString(mContext, strCode));
                }

            }
        };
        GetJson getJson = new GetJson(getActivity(), callback);
        getJson.setConnection(Request.Method.GET, CommonUrlConfig.BarSearchTop, params);
    }

    //bar返回结果
    private void ResponseBarSearch(final String searchEdit) {
        Map<String, String> params = new HashMap<>();
        params.put("search", Encryption.utf8ToUnicode(searchEdit));
        GetJson.Callback callback = new GetJson.Callback() {
            @Override
            public void onFinish(String response) {
                results = JsonUtil.fromJson(response, new TypeToken<CommonListResult<SearchBarModel>>() {
                }.getType());
                if (results == null) {
                    return;
                }
                commentData = results.data;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (searchEdit.equals("")) {
                            CommonAdapter<SearchBarModel> nameAdapter = new CommonAdapter<SearchBarModel>(getActivity(), commentData, R.layout.item_topbar_search) {
                                @Override
                                public void convert(ViewHolder helper, SearchBarModel item, int position) {
                                    helper.setText(R.id.TopBarName, item.barname);
                                }
                            };
                            dailog_listView.setAdapter(nameAdapter);
                        } else {
                            if (results.hasData()) {
                                strType.setVisibility(View.GONE);
                                CommonAdapter<SearchBarModel> adapter = new CommonAdapter<SearchBarModel>(getActivity(), commentData, R.layout.item_saerch_bar) {
                                    @Override
                                    public void convert(ViewHolder helper, SearchBarModel item, int position) {
                                        helper.setText(R.id.barItme_name, item.barname);
                                        helper.setImageUrl(R.id.barItem_handerImage, item.barimage, R.drawable.info_bg);
                                        helper.setText(R.id.barItem_context, item.Introduce);
                                    }
                                };
                                dailog_listView.setAdapter(adapter);
                            } else {
                                commentData.clear();
                            }
                        }
                    }
                });
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
                    ToastUtils.showToast(getActivity(), MapUtil.getString(mContext, strCode));
                }
            }
        };
        GetJson getJson = new GetJson(getActivity(), callback);
        getJson.setConnection(Request.Method.GET, CommonUrlConfig.searchBar, params);
    }


    @Override
    public void onClick(View v) {
        if (!NetWorkUtil.isConnected(getActivity())) {
            ToastUtils.showToast(getActivity(), getString(R.string.toast_no_network));
            return;
        }
        switch (v.getId()) {
            case R.id.layout_isOnHook:
                if (AppBalala.isOnHook && AppBalala.chatroomApplication != null) {
                    Intent intent = new Intent(getActivity(), AppBalala.chatroomApplication.getClass());
                    startActivity(intent);
                }
                break;
            case R.id.exit_isOnHook:
                layout_isOnHook.setVisibility(View.GONE);
                AppBalala.isOnHook = false;
                if (AppBalala.chatroomApplication != null) {
                    ChatRoomActivity.bindService.sendMessage(GlobalDef.WM_ROOM_LOGIN_OUT, null);
                    AppBalala.chatroomApplication.finish();
                    ChatRoomActivity.getInstance().CloseChatLine();
                    RoomOnlineFragment.getInstance().OnlineClose();
                    AppBalala.chatroomApplication = null;
                }
                break;
            case R.id.Share_isOnHook:
//                ShareFacebook shareFacebook = new ShareFacebook(getActivity(), getActivity());
//                shareFacebook.postStatusUpdate(AppBalala.shareTitle, AppBalala.shareContent, AppBalala.shareURL);
//                TaskUtil taskUtil = new TaskUtil(mContext);
//                taskUtil.accomplishTasks("3");
                break;
            case R.id.Attention_isOnHook:
                if (isAtt) {
                    ResponseBarFollow("1");
                    isAtt = false;
                    if (ChatRoomActivity.barResult.isfollow != null) {
                        ChatRoomActivity.barResult.isfollow = "0";
                        Attention_isOnHook.setImageResource(R.drawable.bar_bottom_praise);
                    }
                } else {//关注
                    isAtt = true;
                    ResponseBarFollow("0");
                    if (ChatRoomActivity.barResult.isfollow != null) {
                        ChatRoomActivity.barResult.isfollow = Constant.isFollow;
                        Attention_isOnHook.setImageResource(R.drawable.bar_bottom_praise_on);
                    }
                }
                break;
        }
    }

    /**
     * @param type 0去关注 1去取消
     */
    private void ResponseBarFollow(String type) {
        Map<String, String> params = new HashMap<>();
        params.put("userid", sharedUtil.getInstance(getActivity()).getUid());
        params.put("token", sharedUtil.getInstance(getActivity()).getUserToken());
        params.put("barid", String.valueOf(AppBalala.BarId));
        params.put("type", type);
        GetJson.Callback callback = new GetJson.Callback() {
            @Override
            public void onFinish(String response) {
                DebugLogs.e("--response--->" + response);
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
                    ToastUtils.showToast(getActivity(), MapUtil.getString(mContext, strCode));
                }
            }
        };
        GetJson getJson = new GetJson(getActivity(), callback);
        getJson.setConnection(Request.Method.GET, CommonUrlConfig.BarFollow, params);
    }

    public class MyPagerAdaper extends FragmentPagerAdapter {

        public MyPagerAdaper(FragmentManager fm, List<Fragment> mFragments) {
            super(fm);
            List<Fragment> fragments = mFragments;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (tabTexts.size() > 0) {
                return tabTexts.get(position);
            } else {
                return "";
            }
        }

        /**
         * @return
         * @ The code is fine, but make sure that the listFragment contains three different Fragment's, and the fragment is not null.
         */
        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return BarHomeFragment.newINstantce(position);
        }

        @Override
        public int getCount() {
            if (tabTexts.size() > 0) {
                return tabTexts.size();
            } else {
                return 1;
            }

        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (AppBalala.isOnHook) {
            if (ChatRoomActivity.barResult == null) {
                return;
            }
            barName_isOnHook.setText(ChatRoomActivity.barResult.barname);
            synopsis_isOnHook.setText(ChatRoomActivity.barResult.introduce);
            try {
                Bitmap bitmap = AppBalala.imageCache.getCache(ChatRoomActivity.barResult.barimage);
                if (bitmap != null) {
                    head_isOnHook.setImageBitmap(bitmap);
                } else {
                    AppBalala.imageFileLoader.execute(ChatRoomActivity.barResult.barimage, head_isOnHook.getWidth(), head_isOnHook.getHeight(), new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.obj != null) {
                                Bitmap bitmap = (Bitmap) msg.obj;
                                head_isOnHook.setImageBitmap(bitmap);
                            }
                        }
                    });
                }
            } catch (Exception e) {
                System.gc();
            }
            layout_isOnHook.setVisibility(View.VISIBLE);
            if (ChatRoomActivity.barResult.isfollow.equals(Constant.isFollow)) {//表示关注
                isAtt = true;
                Attention_isOnHook.setImageResource(R.drawable.bar_bottom_praise_on);
            } else {//没有关注
                isAtt = false;
                Attention_isOnHook.setImageResource(R.drawable.bar_is);
            }
        } else {
            layout_isOnHook.setVisibility(View.GONE);
        }
    }

    /**
     * 懒加载，看到这个界面则加载
     */
    @Override
    protected void lazyLoad() {
        DebugLogs.d("-------------->--BarFragment--lazyLoad----->");
    }

}
