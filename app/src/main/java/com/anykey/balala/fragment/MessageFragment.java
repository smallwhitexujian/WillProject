package com.anykey.balala.fragment;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anykey.balala.AppBalala;
import com.anykey.balala.CommonResultCode;
import com.anykey.balala.Constant;
import com.anykey.balala.R;
import com.anykey.balala.Socket.Process.RouterProcess;
import com.anykey.balala.Socket.Process.WillOutProtocol;
import com.anykey.balala.Utils.FaceUtils;
import com.anykey.balala.Utils.sharedUtil;
import com.anykey.balala.activity.MainActivity;
import com.anykey.balala.activity.RelationsActivity;
import com.anykey.balala.activity.SubscriptionsActivity;
import com.anykey.balala.activity.TaskActivity;
import com.anykey.balala.adapter.CommonAdapter;
import com.anykey.balala.adapter.ViewHolder;
import com.anykey.balala.model.MessageRecordDBModel;
import com.anykey.balala.model.MessageRecordModel;
import com.anykey.balala.model.PrivateChatDBModel;
import com.anykey.balala.model.PrivateChatModel;
import com.anykey.balala.receiver.LocalRouterReceiver;
import com.anykey.balala.view.HeaderLayout;
import com.anykey.balala.view.SearchDialog;
import com.anykey.balala.view.SelfDialog;

import net.dev.mylib.cache.sharedPreferences.SharedPreferencesUtil;
import net.dev.mylib.time.DateUtil;

import java.util.ArrayList;
import java.util.List;

;

/**
 * Created by xujian on 15/8/31.
 * Message fragment
 */
public class MessageFragment extends Hintfragment {
    private final int LOAD_DATA = 1;

    private View rootView;
    private ListView mListView;
    private CommonAdapter<MessageRecordModel> mMessageAdapter;//消息适配器
    private List<MessageRecordModel> mMessages = new ArrayList<>();
    private View mSearchBar;
    private HandlerThread mHandlerThread;
    private LocalRouterReceiver receiver;
    private boolean isPrepared;
    private SharedPreferencesUtil sp;

    private Handler mUIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case LOAD_DATA:
                    long uid;
                    if (!sharedUtil.getInstance(mContext).getUid().equals("")) {
                        uid = Long.valueOf(sp.getUserId());
                        mMessages = loadMessage(uid);
                        mMessageAdapter.setData(mMessages);
                        mMessageAdapter.notifyDataSetChanged();
                        ((MainActivity) getActivity()).setDot(getUnreadItem(uid));
                    }
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_message, null);
        isPrepared = true;
        initView();
        setView();
        return rootView;
    }

    /**
     * 懒加载，看到这个界面则加载
     */
    @Override
    protected void lazyLoad() {

    }

    @Override
    public void onStart() {
        super.onStart();
        AppBalala.currentUI = MessageFragment.class.getName();
        receiver = new LocalRouterReceiver(new LocalRouterReceiver.RouterCallback() {
            @Override
            public void onHandle(byte[] parcel,Object obj) {
                mUIHandler.obtainMessage(LOAD_DATA).sendToTarget();
            }
        });
        IntentFilter filter = new IntentFilter();
        filter.addAction(RouterProcess.ACTION_ROUTER_LOAD_DATA);
        filter.addAction(RouterProcess.ACTION_ROUTER_PARCEL);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, filter);
        mUIHandler.obtainMessage(LOAD_DATA).sendToTarget();


    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
    }


    private void initView() {
        headerLayout = (HeaderLayout) rootView.findViewById(R.id.headerLayout);
        headerLayout.showRightImageButton(R.drawable.bar_top_search, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SearchDialog(getActivity(), SearchDialog.FROM_RELATION_SEARCH).showSreachDialog();
            }
        });
        headerLayout.showRightImageButton(R.drawable.message_right_plus, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), RelationsActivity.class);
                getActivity().startActivity(i);
            }
        });
        headerLayout.showLeftImageButton(R.drawable.bar_top_icon, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mListView = (ListView) rootView.findViewById(R.id.message_listview);

        mMessageAdapter = new CommonAdapter<MessageRecordModel>(getActivity(), mMessages, R.layout.item_message) {
            @Override
            public void convert(ViewHolder helper, MessageRecordModel item, int position) {

                if (item.isTag == true) {
                    int width = ViewGroup.LayoutParams.MATCH_PARENT;
                    final float scale = getActivity().getResources().getDisplayMetrics().density;
                    int height = (int) (16 * scale + 0.5f);
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, height);
                    helper.setLayoutParams(R.id.message_item_layout, lp);
                    //helper.setBackgroundColor(R.id.message_item_layout, 0xfff0f0f0);
                    //helper.setBackgroundColor(R.id.message_item_layout, R.color.white);
                    helper.hideView(R.id.message_item_icon);
                    helper.hideView(R.id.message_item_title);
                    helper.hideView(R.id.message_item_content);
                    helper.hideView(R.id.message_item_datetime);
                    helper.hideView(R.id.message_new_textview);
                } else {
                    int width = ViewGroup.LayoutParams.MATCH_PARENT;
                    int height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, height);
                    helper.setLayoutParams(R.id.message_item_layout, lp);
                    //helper.setBackgroundColor(R.id.message_item_layout, 0xffffffff);
                    //helper.setBackgroundColor(R.id.message_item_layout, R.color.white);

                    helper.showView(R.id.message_item_title);
                    if (null == item.title || "".equals(item.title)) {
                        helper.setText(R.id.message_item_title, getString(R.string.anonymous));
                    } else {
                        helper.setText(R.id.message_item_title, item.title);
                    }
                    if (getString(R.string.task_title).equals(item.title) || getString(R.string.title_activity_visitors).equals(item.title) || getString(R.string.title_activity_subscriptions).equals(item.title)) {
                        helper.hideView(R.id.message_item_content);
                        helper.hideView(R.id.message_item_datetime);
                        helper.showView(R.id.message_item_icon);
                        helper.hideView(R.id.message_new_textview);
                        helper.setImageResource(R.id.message_item_icon, item.iconId);
                    } else {
                        helper.showView(R.id.message_item_content);
                        helper.showView(R.id.message_item_icon);
                        helper.showView(R.id.message_item_datetime);
//                        if (item.headphoto == null) {
//                            helper.setImageResource(R.id.message_item_icon, R.drawable.icon_micro);
//                        } else {
//                            helper.setImageUrl(R.id.message_item_icon, item.headphoto);
//                        }
                        if (item.count > 0) {
                            helper.showView(R.id.message_new_textview);
                            helper.setText(R.id.message_new_textview, item.count + "");
                        } else {
                            helper.hideView(R.id.message_new_textview);
                        }
                        try {
                            if (item.message != null) {
                                if(item.type == WillOutProtocol.VOICE_CHAT_TYPE_VALUE){
                                    helper.setText(R.id.message_item_content, "[" + getActivity().getResources().getString(R.string.voice_notice) + "]");
                                }
//                                if(item.type == WillOutProtocol.PRIVATE_CHAT_TYPE_VALUE){
//                                    //换表情
//                                    DebugLogs.e("jjfly "+item.message);
//                                    AppBalala.faceHotKeyMap.get("");
//                                    helper.setText(R.id.message_item_content, item.message);
//                                }
                                else{

                                    helper.setText(R.id.message_item_content, FaceUtils.filterFace(item.message));
                                }
                            }
                            helper.setImageUrl(R.id.message_item_icon, item.headphoto);
                            if (item.count > 0) {
                                helper.showView(R.id.message_new_textview);
                                helper.setText(R.id.message_new_textview, item.count + "");
                            } else {
                                helper.hideView(R.id.message_new_textview);
                            }
//                            helper.setImageResource(R.id.message_item_icon, item.iconId);
//                            helper.setText(R.id.message_item_datetime, DateUtil.DateFormat2(getActivity(), Long.valueOf(item.time)));
                            helper.setText(R.id.message_item_datetime, DateUtil.DateFormat2(getActivity(), Long.valueOf(item.localtime)));
                        } catch (NumberFormatException e) {

                        }
                    }
                }
            }
        };
        mListView.setAdapter(mMessageAdapter);
        mHandlerThread = new HandlerThread(MessageFragment.class.getName());
        mHandlerThread.start();

        sp = SharedPreferencesUtil.getInstance(mContext);
    }

    private void setView() {
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final MessageRecordModel item = mMessages.get(position);
                if (item.isTag == true || getString(R.string.task_title).equals(item.title) || getString(R.string.title_activity_visitors).equals(item.title) || getString(R.string.title_activity_subscriptions).equals(item.title)) {
                    return true;
                }
                final View dview = LayoutInflater.from(getActivity()).inflate(R.layout.self_dialog_menu, null);
                TextView mDelTextView = (TextView) dview.findViewById(R.id.delete_item);
                TextView mTopTextView = (TextView) dview.findViewById(R.id.top_item);
                final SelfDialog dialog = new SelfDialog(getActivity(), dview);
                final MessageRecordDBModel messageRecordDBModel = new MessageRecordDBModel();
                final PrivateChatDBModel privateChatDBModel = new PrivateChatDBModel();
//                final ResouceMessageDBModel resMsgDBModle = new ResouceMessageDBModel();
                mDelTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //删除消息记录
                        //删除对应的消息记录
                        messageRecordDBModel.deleteMessageRecord(Long.valueOf(item.from), Long.valueOf(sp.getUserId()));
                        privateChatDBModel.deleteChatMessage(Long.valueOf(sp.getUserId()), Long.valueOf(item.from));
//                        resMsgDBModle.deleteChatMessage(Long.valueOf(AppBalala.Uid), Long.valueOf(item.from));
                        mUIHandler.obtainMessage(LOAD_DATA).sendToTarget();
                        dialog.cancel();
                    }
                });

                mTopTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        messageRecordDBModel.setTop(item.from);
                        mUIHandler.obtainMessage(LOAD_DATA).sendToTarget();
                        dialog.cancel();
                    }
                });
                dialog.show();
                return true;
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                MessageRecordModel item = mMessages.get(position);
                if (item.isTag == true) {
                    return;
                }
                if (getString(R.string.task_title).equals(item.title)) {
                    Intent taskIntent = new Intent(getActivity(), TaskActivity.class);
                    getActivity().startActivityForResult(taskIntent, CommonResultCode.SELECT_BAR_CODE);
                    return;
                }
                if (getString(R.string.title_activity_visitors).equals(item.title)) {
                    Intent vistorIntent = new Intent(getActivity(), com.anykey.balala.activity.VistorActivity.class);
                    getActivity().startActivity(vistorIntent);
                    return;
                }
                if (getString(R.string.title_activity_subscriptions).equals(item.title)) {
                    Intent subscriptionIntent = new Intent(getActivity(), SubscriptionsActivity.class);
                    getActivity().startActivity(subscriptionIntent);
                    return;
                } else {
//                    MessageRecordModel messageModel = mMessageAdapter.getItem(position - 1);
                    Intent i = new Intent(getActivity(), com.anykey.balala.activity.PrivateChatActivity.class);
                    PrivateChatModel.UserInfo userInfo = new PrivateChatModel.UserInfo();
                    userInfo.uid = item.from;
                    userInfo.nickname = item.title;
                    userInfo.headphoto = item.headphoto;
                    i.putExtra(Constant.FROM_USERINFO_KEY, userInfo);
                    getActivity().startActivity(i);
                    return;
                }
            }
        });
    }

    private List<MessageRecordModel> loadMessage(long userid) {
        //从数据库中读取出消息记录
        List<MessageRecordModel> header = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            MessageRecordModel messageModel = new MessageRecordModel();
            switch (i) {
                case 1:
                    messageModel.title = getString(R.string.task_title);
                    messageModel.iconId = R.drawable.message_left_task;
                    break;
                case 2:
                    messageModel.title = getString(R.string.title_activity_visitors);
                    messageModel.iconId = R.drawable.message_left_task_visitors;
                    break;
                case 3:
                    messageModel.title = getString(R.string.title_activity_subscriptions);
                    messageModel.iconId = R.drawable.message_left_task_subscriptions;
                    break;
                case 4:
                    messageModel.isTag = true;
                    break;
            }
            header.add(messageModel);
        }
        List<MessageRecordModel> datas = new MessageRecordDBModel().loadMessageRecord(Long.valueOf(sp.getUserId()));
        if (datas != null) {
            header.addAll(datas);
        }
        return header;
    }

    private int getUnreadItem(long toid) {
        MessageRecordDBModel messageRecordDBModel = new MessageRecordDBModel();
        return messageRecordDBModel.countUnreadItem(toid);
    }
}
