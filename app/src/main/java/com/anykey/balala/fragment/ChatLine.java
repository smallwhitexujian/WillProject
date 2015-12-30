/**暂时不要删除**/
//package com.anykey.balala.fragment;
//
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.drawable.Drawable;
//import android.os.Bundle;
//import android.support.v4.content.LocalBroadcastManager;
//import android.text.Editable;
//import android.text.Html;
//import android.text.TextWatcher;
//import android.util.DisplayMetrics;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Adapter;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.GridLayout;
//import android.widget.GridView;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.RelativeLayout;
//
//import com.android.volley.Request;
//import com.android.volley.VolleyError;
//import com.anykey.balala.AppBalala;
//
//import android.text.Html.ImageGetter;
//import android.widget.Spinner;
//import android.widget.TextView;
//
//import com.anykey.balala.CommonUrlConfig;
//import com.anykey.balala.GlobalDef;
//import com.anykey.balala.R;
//import com.anykey.balala.Utils.FaceUtils;
//import com.anykey.balala.Utils.GiftSortComparator;
//import com.anykey.balala.Utils.MapUtil;
//import com.anykey.balala.Utils.sharedUtil;
//import com.anykey.balala.activity.ChatRoomActivity;
//import com.anykey.balala.activity.OpenRedRnvelopesActivity;
//import com.anykey.balala.activity.RechargeActivity;
//import com.anykey.balala.activity.SendRedRnvelopesActivity;
//import com.anykey.balala.activity.UserInfoActivity;
//import com.anykey.balala.adapter.ChatLineAdapter;
//import com.anykey.balala.adapter.CommonAdapter;
//import com.anykey.balala.adapter.GridAdapter;
//import com.anykey.balala.adapter.ViewHolder;
//import com.anykey.balala.model.ChatLineModel;
//import com.anykey.balala.model.CommonModel;
//import com.anykey.balala.model.FaceModel;
//import com.anykey.balala.model.GiftModel;
//import com.anykey.balala.model.RedPaperStateModel;
//import com.anykey.balala.receiver.AppBroadcastReceiver;
//import com.anykey.balala.work.ParseXmlWork;
//
//import net.dev.mylib.DebugLogs;
//import net.dev.mylib.Encryption;
//import net.dev.mylib.JsonUtil;
//import net.dev.mylib.ToastUtils;
//import net.dev.mylib.Utility;
//import net.dev.mylib.cache.fileCheanCache.FileUtil;
//import net.dev.mylib.cache.sharedPreferences.SharedPreferencesUtil;
//import net.dev.mylib.netWorkUtil.GetJson;
//import net.dev.mylib.netWorkUtil.getCode;
//import net.dev.mylib.view.ImageView.CircularImage;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by xujian on 15/9/11.
// * 聊天碎片
// */
//public class ChatLine extends BaseFragment implements View.OnClickListener {
//    private static ChatLineAdapter<ChatLineModel> mAdapter;
//    private ImageView faceView, aredenvelope, giftbtn;          // 表情按钮
//    private static ChatLine instance = null;
//    public RelativeLayout faceLayout, view_chatView;             // 表情布局界面
//    private static ListView chatline;
//    private GridAdapter faceAdapter;                            // 表情Adapter，
//    private String priMessage = "";                             // 检测两句话是否相同
//    private EditText messageEdit;
//    private GridView faceTable;                                 // 表情框
//    private Button sendBtn;                                     // 发送按钮
//    private View rootview;
//    private Dialog dialog;
//    private SharedPreferencesUtil sp = null;
//    public static ArrayAdapter<String> PopAdapter;
//    private LinearLayout giftView;                              // 礼物界面
//    public List<GiftModel> mGifts = new ArrayList<>();          // 礼物数据
//    private CommonAdapter<GiftModel> mGiftAdapter;              // 礼物适配器
//    private GridView item_gift;                                 // 礼物界面
//    private Spinner roomPopSpinner;                             // 在线人数列表
//    private Spinner roomGiftNumSpinner;                         // 礼物个数列表
//    private Button gift_send;                                   // 礼物发送按钮
//    private int giftId;                                         // 礼物的ID
//    private int giftType;                                       // 礼物的类型
//    private TextView gift_my_coin, gift_Diamonds, gift_Recharge; // 金币,钻石
//
//    /* 1:懒汉式，静态工程方法，创建实例 */
//    public static ChatLine getInstance() {
//        if (instance == null) {
//            instance = new ChatLine();
//        }
//        return instance;
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        rootview = inflater.inflate(R.layout.fragment_chatline, null);
//        sp = SharedPreferencesUtil.getInstance(getActivity());
//        initContent();
//        return rootview;
//    }
//
//    private void initContent() {
//        roomGiftNumSpinner = (Spinner) rootview.findViewById(R.id.roomGiftNumSpinner);
//        messageEdit = (EditText) rootview.findViewById(R.id.sendOfflineMsg_edit);
//        faceLayout = (RelativeLayout) rootview.findViewById(R.id.chatFaceLayout);
//        view_chatView = (RelativeLayout) rootview.findViewById(R.id.view_chatView);
//        view_chatView.setBackgroundResource(R.drawable.room_centre_background2);
//        roomPopSpinner = (Spinner) rootview.findViewById(R.id.roomPopSpinner);
//        gift_Diamonds = (TextView) rootview.findViewById(R.id.gift_Diamonds);
//        faceTable = (GridView) rootview.findViewById(R.id.chatFaceGridView);
//        aredenvelope = (ImageView) rootview.findViewById(R.id.aredenvelope);
//        gift_Recharge = (TextView) rootview.findViewById(R.id.gift_Recharge);
//        gift_my_coin = (TextView) rootview.findViewById(R.id.gift_my_coin);
//        giftView = (LinearLayout) rootview.findViewById(R.id.giftView);
//        item_gift = (GridView) rootview.findViewById(R.id.item_gift);
//        faceView = (ImageView) rootview.findViewById(R.id.faceView);
//        gift_send = (Button) rootview.findViewById(R.id.gift_send);
//        chatline = (ListView) rootview.findViewById(R.id.chatline);
//        giftbtn = (ImageView) rootview.findViewById(R.id.giftbtn);
//        sendBtn = (Button) rootview.findViewById(R.id.sendBtn);
//        Utility.closeKeybord(messageEdit, getActivity());
//
//        gift_send.setOnClickListener(this);
//        aredenvelope.setOnClickListener(this);
//        giftbtn.setOnClickListener(this);
//        sendBtn.setOnClickListener(this);
//        faceView.setOnClickListener(this);
//        gift_Recharge.setOnClickListener(this);
//        messageEdit.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                faceLayout.setVisibility(View.GONE);
//                giftView.setVisibility(View.GONE);
//                ChatRoomActivity.getInstance().mHander.obtainMessage(GlobalDef.WM_ROOM_HEART).sendToTarget();
//                return false;
//            }
//        });
//        messageEdit.addTextChangedListener(textWatcher);
//        chatline.setOnItemClickListener(onItemClickListener);
//        dialog = new AlertDialog.Builder(getActivity()).create();
//        loadGift();
//    }
//
//    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            Adapter adapter = parent.getAdapter();
//            ChatLineModel model = (ChatLineModel) adapter.getItem(position);
//            if (model.type == 8) {
//                redPaperGetCheck(model);
//            }
//        }
//    };
//
//    /**
//     * 检查红包
//     */
//    private void redPaperGetCheck(final ChatLineModel model) {
//        HashMap<String, String> params = new HashMap<>();
//        params.put("userid", sp.getUserId());
//        params.put("token", sp.getToken());
//        params.put("redid", model.id);
//
//        GetJson.Callback callback = new GetJson.Callback() {
//            @Override
//            public void onFinish(String response) {
//                RedPaperStateModel results = JsonUtil.fromJson(response, RedPaperStateModel.class);
//                ResultsRad(response, results, model);
//            }
//
//            @Override
//            public void onError(VolleyError error) {
//                getCode.hasCode errorCode = ((getCode.hasCode) error);
//                String strCode = errorCode.errorCode;
//                if (strCode.equals(CommonUrlConfig.RequestState.Logout)) {
//                    Intent voiceIntent = new Intent();
//                    voiceIntent.setAction(AppBroadcastReceiver.BROADCAST_USER_LOGOUT);
//                    LocalBroadcastManager.getInstance(mcontext).sendBroadcast(voiceIntent);
//                } else {
//                    ToastUtils.showToast(getActivity(), MapUtil.getString(strCode));
//                }
//            }
//        };
//        GetJson getJson = new GetJson(getActivity(), callback, true, getActivity().getString(R.string.loading));
//        getJson.setConnection(Request.Method.GET, CommonUrlConfig.RedPaperGetCheck, params);
//    }
//
//    /**
//     * 红包待拆
//     */
//    private void ResultsRad(String response, RedPaperStateModel results, final ChatLineModel model) {
//        LayoutInflater inflaterDl = LayoutInflater.from(getActivity());
//        RelativeLayout layout = (RelativeLayout) inflaterDl.inflate(R.layout.open_red_rnvelopes_dialog, null);
//        CircularImage userhead = (CircularImage) layout.findViewById(R.id.userhead);
//        TextView txt_name = (TextView) layout.findViewById(R.id.txt_name);
//        TextView txt_remark = (TextView) layout.findViewById(R.id.txt_remark);
//        RelativeLayout btnOK = (RelativeLayout) layout.findViewById(R.id.dialog_ok);//open按钮
//        ImageView btnClose = (ImageView) layout.findViewById(R.id.dialog_close);//关闭按钮
//        TextView txt_details = (TextView) layout.findViewById(R.id.txt_details);
//        dialog.show();
//        dialog.getWindow().setContentView(layout);
//        //可以抢的红包
//        if (results.state.equals("0")) {
//            txt_details.setVisibility(View.GONE);
//            txt_name.setText(model.from.name);
//            if (!model.message.equals("")) {
//                txt_remark.setText(model.message);
//            } else {
//                txt_remark.setText(getString(R.string.red_best_wishes));
//            }
//            try {
//                userhead.setImageUrl(model.from.headphoto);
//            } catch (Exception ex) {
//                userhead.setImageResource(R.drawable.icon_micro);
//            }
//            btnOK.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    GetRedPaper(model);
//                }
//            });
//            btnClose.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dialog.dismiss();
//                }
//            });
//        } else if (results.state.equals("2")) {//红包拆开界面
//            Intent intent = new Intent(getActivity(), OpenRedRnvelopesActivity.class);
//            intent.putExtra("response", response);
//            startActivity(intent);
//            dialog.dismiss();
//        } else if (results.state.equals("1")) {//抢完了。
//            try {
//                txt_details.setVisibility(View.VISIBLE);
//                btnOK.setVisibility(View.INVISIBLE);
//                txt_name.setText(model.from.name);
//                txt_remark.setText(getString(R.string.red_best_luck));
//                userhead.setImageUrl(model.from.headphoto);
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//            btnClose.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dialog.dismiss();
//                }
//            });
//            txt_details.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    RedPaperGetlist(model);//红包记录
//                }
//            });
//        }
//    }
//
//    /**
//     * 拆红包
//     */
//    private void GetRedPaper(final ChatLineModel model) {
//        HashMap<String, String> params = new HashMap<>();
//        params.put("userid", sp.getUserId());
//        params.put("token", sp.getToken());
//        params.put("redid", model.id);
//        GetJson.Callback callback = new GetJson.Callback() {
//            @Override
//            public void onFinish(String response) {
//                CommonModel results = JsonUtil.fromJson(response, CommonModel.class);
//                if (results != null && results.code.equals(CommonUrlConfig.RequestState.OK)) {
//                    Intent intent = new Intent(getActivity(), OpenRedRnvelopesActivity.class);
//                    intent.putExtra("response", response);
//                    MeFragment.isLoad = false;
//                    startActivity(intent);
//                    dialog.dismiss();
//                } else {
//                    ToastUtils.showToast(getActivity(), results.message);
//                }
//            }
//
//            @Override
//            public void onError(VolleyError error) {
//                getCode.hasCode errorCode = ((getCode.hasCode) error);
//                String strCode = errorCode.errorCode;
//                if (strCode.equals(CommonUrlConfig.RequestState.Logout)) {
//                    Intent voiceIntent = new Intent();
//                    voiceIntent.setAction(AppBroadcastReceiver.BROADCAST_USER_LOGOUT);
//                    LocalBroadcastManager.getInstance(mcontext).sendBroadcast(voiceIntent);
//                } else {
//                    ToastUtils.showToast(getActivity(), MapUtil.getString(strCode));
//                }
//            }
//        };
//        GetJson getJson = new GetJson(getActivity(), callback, true, getActivity().getString(R.string.loading));
//        getJson.setConnection(Request.Method.GET, CommonUrlConfig.GetRedPaper, params);
//    }
//
//    /**
//     * 单个红包明细
//     */
//    private void RedPaperGetlist(final ChatLineModel model) {
//        HashMap<String, String> params = new HashMap<>();
//        params.put("userid", sp.getUserId());
//        params.put("token", sp.getToken());
//        params.put("redid", model.id);
//
//        GetJson.Callback callback = new GetJson.Callback() {
//            @Override
//            public void onFinish(String response) {
//                CommonModel results = JsonUtil.fromJson(response, CommonModel.class);
//                if (results != null && results.code.equals(CommonUrlConfig.RequestState.OK)) {
//                    Intent intent = new Intent(getActivity(), OpenRedRnvelopesActivity.class);
//                    intent.putExtra("response", response);
//                    startActivity(intent);
//                    dialog.dismiss();
//                } else {
//                    ToastUtils.showToast(getActivity(), results.message);
//                }
//            }
//
//            @Override
//            public void onError(VolleyError error) {
//                getCode.hasCode errorCode = ((getCode.hasCode) error);
//                String strCode = errorCode.errorCode;
//                if (strCode.equals(CommonUrlConfig.RequestState.Logout)) {
//                    Intent voiceIntent = new Intent();
//                    voiceIntent.setAction(AppBroadcastReceiver.BROADCAST_USER_LOGOUT);
//                    LocalBroadcastManager.getInstance(mcontext).sendBroadcast(voiceIntent);
//                } else {
//                    ToastUtils.showToast(getActivity(), MapUtil.getString(strCode));
//                }
//            }
//        };
//        GetJson getJson = new GetJson(getActivity(), callback, true, getActivity().getString(R.string.loading));
//        getJson.setConnection(Request.Method.GET, CommonUrlConfig.RedPaperGetlist, params);
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.sendBtn://发送按钮
//                if (ChatRoomActivity.isSilence != null && ChatRoomActivity.isSilence.equals("1")) {
//                    ToastUtils.showToast(getActivity(), R.string.chat_room_no_speek);
//                    return;
//                } else {
//                    if (messageEdit.getText().length() > 0) {
//                        String str = getChatMsgString();
//                        if (str == null) {
//                            return;
//                        }
//                        setChatLineModel(getActivity(), sp.getUserId(), sp.getUserName(), sp.getUserHead(), str, sharedUtil.getInstance(mcontext).getUserLevel());
//                        ChatRoomActivity.getInstance().bindService.sendChatMessage("{\"Message\":\"" + str + "\"}");
//                    } else {
//                        ToastUtils.showToast(getActivity(), R.string.chat_room_on_Txt);
//                    }
//                    ChatRoomActivity.getInstance().mHander.obtainMessage(GlobalDef.WM_ROOM_HEART).sendToTarget();
//                    faceLayout.setVisibility(View.GONE);
//                    faceTable.setVisibility(View.GONE);
//                    messageEdit.setText("");
//                    Utility.closeKeybord(messageEdit, getActivity());
//                }
//                break;
//            case R.id.faceView://表情按钮
//                Utility.closeKeybord(messageEdit, getActivity());
//                HintFace();
//                onButtonAddFace();
//                break;
//            case R.id.aredenvelope://红包界面
//                Intent intent = new Intent(getActivity(), SendRedRnvelopesActivity.class);
//                startActivity(intent);
//                break;
//            case R.id.giftbtn://点击礼物展示礼物界面
//                initGiftView();
//                break;
//            case R.id.gift_send://礼物发送界面
//                int nNum = Integer.parseInt(roomGiftNumSpinner.getSelectedItem().toString());
//                String topople = "";
//                if (roomPopSpinner.getSelectedItem() != null && roomPopSpinner != null && !roomPopSpinner.getSelectedItem().toString().equals("")) {
//                    topople = roomPopSpinner.getSelectedItem().toString();
//                } else {
//                    ToastUtils.showToast(getActivity(), R.string.gift_toast_pp);
//                }
//                int toId = 0;
//                for (int i = 0; i < AppBalala.onlineListDatas.size(); i++) {
//                    if (AppBalala.onlineListDatas.get(i).name.equals(topople)) {
//                        toId = Integer.valueOf(AppBalala.onlineListDatas.get(i).uid);
//                    }
//                }
//                if (sp.getUserId().equals(String.valueOf(toId))) {
//                    ToastUtils.showToast(getActivity(), getString(R.string.gift_toast));
//                    break;
//                }
//                int typeValue = GlobalDef.WM_ROOM_SENDGIFT;
//                ChatRoomActivity.getInstance().bindService.sendMessage(typeValue, "{\"to\":" + toId + ",\"giftid\":" + giftId + ",\"number\":" + nNum + "}");
//                giftView.setVisibility(View.GONE);
//                ChatRoomActivity.getInstance().mHander.obtainMessage(GlobalDef.WM_ROOM_HEART).sendToTarget();
//                break;
//            case R.id.gift_Recharge:
//                Intent intent1 = new Intent(getActivity(), RechargeActivity.class);
//                intent1.putExtra(UserInfoActivity.ISROOM, true);
//                startActivity(intent1);
//                break;
//        }
//    }
//
//    /*
//     * 隐藏表情
//     */
//    private void HintFace() {
//        if (faceLayout.getVisibility() == View.VISIBLE && faceTable.getVisibility() == View.VISIBLE) {
//            faceLayout.setVisibility(View.GONE);
//            faceTable.setVisibility(View.GONE);
//            ChatRoomActivity.getInstance().mHander.obtainMessage(GlobalDef.WM_ROOM_HEART).sendToTarget();
//            return;
//        } else {
//            if (faceLayout.getVisibility() == View.VISIBLE) {
//                faceLayout.setVisibility(View.GONE);
//                faceTable.setVisibility(View.GONE);
//                ChatRoomActivity.getInstance().mHander.obtainMessage(GlobalDef.WM_ROOM_HEART).sendToTarget();
//            } else {
//                if (giftView.getVisibility() == View.VISIBLE) {
//                    giftView.setVisibility(View.GONE);
//                }
//                faceLayout.setVisibility(View.VISIBLE);
//                faceTable.setVisibility(View.VISIBLE);
//                ChatRoomActivity.getInstance().mHander.obtainMessage(GlobalDef.WM_ROOM_HEARTHINT).sendToTarget();
//            }
//        }
//    }
//
//    /**
//     * 判断聊天内容是否相同，防止刷广告
//     */
//    private String getChatMsgString() {
//        String str = FaceUtils.FilterHtml(Html.toHtml(messageEdit.getText()));
//        str = FaceUtils.UnicodeToGBK2(str);
//        //表情数
//        if (FaceUtils.CountSubString(str, "<img src=") > 11) {
//            ToastUtils.showToast(getActivity(), getString(R.string.char_room_edit1));
//            return null;
//        }
//        str = FaceUtils.FilterFace(str);
//        if (str.length() > 400) {
//            ToastUtils.showToast(getActivity(), getString(R.string.char_room_text_lenght));
//            return null;
//        }
//        if (priMessage.equalsIgnoreCase(str)) {
//            ToastUtils.showToast(getActivity(), getString(R.string.char_room_content));
//            return null;
//        }
//        priMessage = str;
//        return str;
//    }
//
//    /**
//     * 聊天消息初始化
//     *
//     * @param uid   发送的用户id
//     * @param name  发送的用户昵称
//     * @param photo 发送的用户头像
//     * @param msg   发送的内容
//     */
//    public void setChatLineModel(Context context, String uid, String name, String photo, String msg, String lv) {
//        ChatLineModel chat = new ChatLineModel();
//        ChatLineModel.from from = new ChatLineModel.from();
//        from.uid = uid;
//        from.name = name;
//        from.headphoto = photo;
//        from.level = lv;
//        chat.message = msg;
//        chat.from = from;
//        initChatMessage(context, chat);
//    }
//
//    /**
//     * 聊天记录初始化，
//     */
//    public void initChatMessage(Context context, ChatLineModel chatLineModel) {
//        AppBalala.mChatlines.add(chatLineModel);
//        int maxSize = 80;
//        if (AppBalala.mChatlines.size() >= maxSize) {
//            for (int i = 0; i < AppBalala.mChatlines.size(); i++) {
//                if (i < (maxSize / 2)) {
//                    AppBalala.mChatlines.remove(i);
//                }
//            }
//        }
//        if (mAdapter == null) {
//            mAdapter = new ChatLineAdapter<>(context, AppBalala.mChatlines);
//        }
//        if (chatline == null) {
//            chatline = (ListView) rootview.findViewById(R.id.chatline);
//        }
//        mAdapter.notifyDataSetChanged();
//        chatline.setAdapter(mAdapter);
//        chatline.setSelection(mAdapter.getCount());
//    }
//
//    /**
//     * 释放掉房间
//     */
//    public void CloseChatLine() {
//        if (mAdapter != null) {
//            mAdapter.clearMessageList();
//            mAdapter = null;
//        }
//        if (PopAdapter != null) {
//            PopAdapter.clear();
//            PopAdapter = null;
//        }
//        AppBalala.mChatlines.clear();
//    }
//
//
//    /**
//     * 初始化礼物界面
//     */
//    public void initGiftView() {
//        gift_my_coin.setText(AppBalala.Coin + "");
//        gift_Diamonds.setText(AppBalala.Diamonds + "");
//        if (AppBalala.onlineListDatas.size() >= 1) {
//            PopAdapter = new ArrayAdapter<>(getActivity(), R.layout.simple_spinner_gift_num_item);
//            for (int i = 0; i < AppBalala.onlineListDatas.size(); i++) {
//                if (!AppBalala.onlineListDatas.get(i).uid.equals(sp.getUserId())) {
//                    PopAdapter.add(AppBalala.onlineListDatas.get(i).name);
//                }
//            }
//            // 设置下拉列表的风格
//            PopAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            roomPopSpinner.setAdapter(PopAdapter);
//            roomPopSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    parent.setVisibility(View.VISIBLE);
//                }
//
//                @Override
//                public void onNothingSelected(AdapterView<?> parent) {
//
//                }
//            });
//            if (giftView.getVisibility() == View.VISIBLE) {
//                giftView.setVisibility(View.GONE);
//                ChatRoomActivity.getInstance().mHander.obtainMessage(GlobalDef.WM_ROOM_HEART).sendToTarget();
//            } else {
//                Utility.closeKeybord(messageEdit, getActivity());
//                giftView.setVisibility(View.VISIBLE);
//                if (faceLayout.getVisibility() == View.VISIBLE) {
//                    faceLayout.setVisibility(View.GONE);
//                }
//                ChatRoomActivity.getInstance().mHander.obtainMessage(GlobalDef.WM_ROOM_HEARTHINT).sendToTarget();
//            }
//        } else {
//            ChatRoomActivity.getInstance().bindService.sendQueryRoomPeople();
//        }
//    }
//
//    /**
//     * 礼物下载
//     */
//    protected void loadGift() {
//        if (AppBalala.giftMap == null || AppBalala.giftMap.entrySet().isEmpty()
//                || AppBalala.giftMap.size() == 0) {
//            new ParseXmlWork().loadGiftXML(mcontext, AppBalala.giftConfigPath, false);
//        }
//        for (Map.Entry<String, Map> entry : AppBalala.giftMap.entrySet()) {
//            Map value = entry.getValue();
//            GiftModel giftModel = new GiftModel();
//            giftModel.giftName = value.get("Name") + "";
//            giftModel.picPath = value.get("ImageName") + "";
//            giftModel.giftPrice = value.get("Price") + "";
//            giftModel.giftid = Integer.valueOf(value.get("ID") + "");
//            giftModel.giftType = Integer.valueOf(value.get("Type") + "");
//            giftModel.Sort = Integer.valueOf(String.valueOf(value.get("Sort")));
//            mGifts.add(giftModel);
//        }
//        Collections.sort(mGifts, new GiftSortComparator());
//        if (mGiftAdapter == null) {
//            mGiftAdapter = new CommonAdapter<GiftModel>(getActivity(), mGifts, R.layout.item_private_chat_gift) {
//                @Override
//                public void convert(ViewHolder helper, GiftModel item, int position) {
//                    helper.setImageResource(R.id.gift_icon, item.rsId);
//                    helper.setText(R.id.gift_name, item.giftName);
//                    helper.setText(R.id.gift_price, item.giftPrice);
//                    String path = getActivity().getFilesDir() + "/item/" + FileUtil.convertUrlToFileName(item.picPath);
//                    DebugLogs.d("gift:" + path);
//                    helper.setImageViewByImageLoader(R.id.gift_icon, path);
//                }
//            };
//            item_gift.setAdapter(mGiftAdapter);
//            item_gift.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    if ((parent).getTag() != null) {
//                        ((View) (parent).getTag()).setBackgroundDrawable(null);
//                    }
//                    (parent).setTag(view);
//                    view.setBackgroundResource(R.color.font_grey_b8);
//                    giftId = mGiftAdapter.getItem(position).giftid;
//                    giftType = mGiftAdapter.getItem(position).giftType;
//                }
//            });
//        }
//        initGiftNumSpinner();
//    }
//
//    /**
//     * 初始化礼物数量选择框
//     */
//    private void initGiftNumSpinner() {
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.simple_spinner_gift_num_item);
//        int numArray[] = {1, 18, 99, 188, 817};
//        for (int i = 0; i < numArray.length; i++) {
//            adapter.add(String.valueOf(numArray[i]));
//        }
//        // 设置下拉列表的风格
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        // 将adapter添加到m_Spinner中
//        roomGiftNumSpinner.setAdapter(adapter);
//        // 添加Spinner事件监听
//        roomGiftNumSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
//
//            @Override
//            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//                arg0.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> arg0) {
//            }
//        });
//
//    }
//
//
//    /**
//     * 打开 表情 列表
//     */
//    protected void onButtonAddFace() {
//        if (faceAdapter == null) {
//            initFaceGridView();
//        }
//    }
//
//    /**
//     * 初始化 表情 列表框
//     */
//    private void initFaceGridView() {
//        List<Integer> resIDs = new ArrayList<>();
//        for (FaceModel face : AppBalala.faceModelList) {
//            int id = this.getResources().getIdentifier(face.sFilePath.substring(0, face.sFilePath.length() - 4),
//                    "raw", getActivity().getPackageName());
//            if (id > 0) {
//                resIDs.add(id);
//            }
//        }
//        faceAdapter = new GridAdapter(getActivity(), resIDs);
//        faceAdapter.setBackColor(0x00000000);
//        faceAdapter.setImageSize(GridLayout.LayoutParams.WRAP_CONTENT, GridLayout.LayoutParams.WRAP_CONTENT);
//        faceTable.setAdapter(faceAdapter);
//        faceTable.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                onClick_InsertFace(Integer.parseInt(parent.getItemAtPosition(position).toString()));
//            }
//        });
//    }
//
//    /**
//     * 点击加入表情到输入框
//     *
//     * @param drawResID
//     */
//    protected void onClick_InsertFace(int drawResID) {
//        CharSequence cs1 = Html.fromHtml("<img src='" + drawResID + "'/>", msgImageGetter, null);
//        int index = messageEdit.getSelectionStart();//获取光标的位置
//        Editable etb = messageEdit.getText();
//        int length = etb.length();
//        if (index <= length) {
//            CharSequence cs = etb.subSequence(0, length);
//            CharSequence cs2 = cs.subSequence(0, index);
//            CharSequence cs3 = cs.subSequence(index, length);
//            messageEdit.setText(null);
//            messageEdit.append(cs2);
//            messageEdit.append(cs1);
//            messageEdit.append(cs3);
//        } else {
//            messageEdit.append(cs1);
//        }
//        messageEdit.setSelection(index + 1);
//    }
//
//    /**
//     * 聊天框表情的图片显示 ImageGetter 原始大小
//     */
//    public ImageGetter msgImageGetter = new ImageGetter() {
//
//        @Override
//        public Drawable getDrawable(String source) {
//            int id = Integer.parseInt(source);
//            Drawable d = null;
//            try {
//                d = getResources().getDrawable(id);
//                d.setBounds(0, 0, 66, 54);
//            } catch (OutOfMemoryError e) {
//                System.gc();
//                e.printStackTrace();
//            }
//            return d;
//        }
//
//    };
//    /**
//     * 监听文本输入框输入的文字
//     */
//    private TextWatcher textWatcher = new TextWatcher() {
//        @Override
//        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//        }
//
//        @Override
//        public void onTextChanged(CharSequence s, int start, int before, int count) {
//            if (s.length() > 0) {
//                sendBtn.setVisibility(View.VISIBLE);
//            } else {
//                sendBtn.setVisibility(View.GONE);
//            }
//        }
//
//        @Override
//        public void afterTextChanged(Editable s) {
//
//        }
//    };
//}
