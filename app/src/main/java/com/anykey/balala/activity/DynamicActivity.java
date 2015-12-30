package com.anykey.balala.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.anykey.balala.AppBalala;
import com.anykey.balala.CommonUrlConfig;
import com.anykey.balala.R;
import com.anykey.balala.Utils.MapUtil;
import com.anykey.balala.Utils.ShareFacebook;
import com.anykey.balala.Utils.TaskUtil;
import com.anykey.balala.Utils.sharedUtil;
import com.anykey.balala.adapter.CommonAdapter;
import com.anykey.balala.adapter.ViewHolder;
import com.anykey.balala.model.CommonData;
import com.anykey.balala.model.CommonListResult;
import com.anykey.balala.model.DiscoveryCommentModel;
import com.anykey.balala.model.DynamicModel;
import com.anykey.balala.model.RoomModel;
import com.anykey.balala.receiver.AppBroadcastReceiver;
import com.anykey.balala.view.HeaderLayout;
import com.google.gson.reflect.TypeToken;

import net.dev.mylib.Encryption;
import net.dev.mylib.JsonUtil;
import net.dev.mylib.ToastUtils;
import net.dev.mylib.Utility;
import net.dev.mylib.netWorkUtil.GetJson;
import net.dev.mylib.netWorkUtil.getCode;
import net.dev.mylib.view.ImageView.CircularImage;
import net.dev.mylib.view.library.SwipyRefreshLayout;
import net.dev.mylib.view.library.SwipyRefreshLayoutDirection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 动态详情页面  sandy
 */
public class DynamicActivity extends BaseActivity implements View.OnClickListener, SwipyRefreshLayout.OnRefreshListener {
    private CircularImage img_head;
    private ImageView img_body;
    private TextView tv_title, tv_time, tv_address, tv_describe, tv_praiseNum, tv_commentNum, txt_bar;
    private ImageView iv_like;
    private EditText et_comment;
    private ListView listView;
    private Button btn_send;
    private DynamicModel model;
    private CommonAdapter mAdapter;
    private List<DiscoveryCommentModel> commentData = new ArrayList<>();
    private SwipyRefreshLayout mSwipyRefreshLayout;
    private String datesort;
    private boolean IS_REFRESH = true;  //是否需要刷新
    private boolean IS_FIRST = true;  //首次加载
    private int pageindex = 1;
    private Bitmap bodybitmap;

    //评论用户ID
    private String puserid = "0";
    private String pnikename = "";

    public static void CreateActivity(Context context, DynamicModel model) {
        Intent intent = new Intent(context, DynamicActivity.class);
        intent.putExtra("model", JsonUtil.toJson(model));
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic);
        initView();
        initContent();
    }

    private void initContent() {
        Bundle bundle;
        bundle = getIntent().getExtras();
        if (bundle != null) {
            model = JsonUtil.fromJson(bundle.getString("model"), DynamicModel.class);
            if (model != null) {
                img_head.setImageUrl(model.headurl);
                tv_time.setText(model.createTime);
                tv_title.setText(model.nickName);
                if (model.address.equals("")) {
                    tv_address.setVisibility(View.GONE);
                } else {
                    tv_address.setVisibility(View.VISIBLE);
                    tv_address.setText(model.address);
                }
                tv_describe.setText(model.content);
                try {

                    Bitmap bitmap = AppBalala.imageCache.getCache(model.imageUrl);
                    if (bitmap != null) {
                        bodybitmap = bitmap;
                        img_body.setImageBitmap(bitmap);
                    } else {
                        AppBalala.imageFileLoader.execute(model.imageUrl, img_body.getWidth(), img_body.getHeight(),
                                new Handler() {
                                    @Override
                                    public void handleMessage(Message msg) {
                                        if (msg.obj != null) {
                                            Bitmap bitmap = (Bitmap) msg.obj;
                                            bodybitmap = bitmap;
                                            img_body.setImageBitmap(bitmap);
                                        }
                                    }
                                });
                    }
                } catch (Exception e) {
                    System.gc();
                }
                tv_praiseNum.setText(model.praiseNum);
                tv_commentNum.setText(model.commentNum);
                if (model.barid.equals("0")) {
                    txt_bar.setVisibility(View.GONE);
                } else {
                    txt_bar.setVisibility(View.VISIBLE);
                    txt_bar.setText(model.baridx);
                }
                if (model.isPraise.equals("1")) {
                    iv_like.setImageResource(R.drawable.like_icon_on);
                } else {
                    iv_like.setImageResource(R.drawable.like_icon);
                }
                tv_commentNum.setText(getString(R.string.view_all) + " " + model.commentNum + " " + getString(R.string.comment));
            }

            img_body.setOnClickListener(this);
            img_head.setOnClickListener(this);
        }

        mAdapter = new CommonAdapter<DiscoveryCommentModel>(getApplicationContext(), commentData, R.layout.lst_comment) {
            @Override
            public void convert(ViewHolder helper, final DiscoveryCommentModel item, final int position) {
                if (!item.ToUserid.equals("0")) {
                    helper.setText(R.id.tv_describe, Html.fromHtml("<font color='#48a0ed'><b>@" + item.ToNickName + "</b></font> " + item.Content));
                } else {
                    helper.setText(R.id.tv_describe, item.Content);
                }
                helper.setText(R.id.tv_title, item.FromNickName);

                if (sharedUtil.getInstance(mContext).getUid().equals(item.FromUserid)) {
                    helper.getView(R.id.btn_del).setVisibility(View.VISIBLE);
                } else {
                    helper.getView(R.id.btn_del).setVisibility(View.GONE);
                }
                helper.setImageUrl(R.id.img_comhead, item.fromHeadUrl);
                helper.setText(R.id.tv_time, item.createtime);
                helper.setOnClick(R.id.img_comhead, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent userInfoActivity = new Intent(DynamicActivity.this, UserInfoActivity.class);
                        userInfoActivity.putExtra("userid", item.FromUserid);
                        startActivity(userInfoActivity);
                    }
                });

                //删除评论
                helper.getView(R.id.btn_del).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DiscoveryCommentDel(position, item.Id);
                    }
                });
            }
        };

        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                et_comment.setHint("@" + commentData.get(position - 1).FromNickName);
                puserid = commentData.get(position - 1).FromUserid;
                pnikename = commentData.get(position - 1).FromNickName;
                et_comment.requestFocus();
                Utility.openKeybord(et_comment, mContext);
            }
        });
        DiscoveryCommentList();
    }

    private void initView() {
        headerLayout = (HeaderLayout) findViewById(R.id.headerLayout);
        headerLayout.showTitle(R.string.moment_details);
        headerLayout.showLeftBackButton();
        et_comment = (EditText) findViewById(R.id.et_comment);
        btn_send = (Button) findViewById(R.id.btn_send);
        listView = (ListView) findViewById(R.id.listView);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = inflater.inflate(R.layout.lst_dynamic, null);
        img_head = (CircularImage) convertView.findViewById(R.id.img_head);
        tv_title = (TextView) convertView.findViewById(R.id.tv_title);
        tv_time = (TextView) convertView.findViewById(R.id.tv_time);
        tv_address = (TextView) convertView.findViewById(R.id.tv_address);
        tv_describe = (TextView) convertView.findViewById(R.id.tv_describe);
        img_body = (ImageView) convertView.findViewById(R.id.img_body);
        iv_like = (ImageView) convertView.findViewById(R.id.iv_like);
        ImageView iv_review = (ImageView) convertView.findViewById(R.id.iv_review);
        ImageView iv_share = (ImageView) convertView.findViewById(R.id.iv_share);
        tv_praiseNum = (TextView) convertView.findViewById(R.id.tv_praiseNum);
        tv_commentNum = (TextView) convertView.findViewById(R.id.tv_commentNum);
        txt_bar = (TextView) convertView.findViewById(R.id.txt_bar);

        mSwipyRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.pullToRefreshView);
        mSwipyRefreshLayout.setOnRefreshListener(this);
        mSwipyRefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTH);

        btn_send.setOnClickListener(this);
        convertView.setOnClickListener(this);
        iv_like.setOnClickListener(this);
        iv_share.setOnClickListener(this);
        iv_review.setOnClickListener(this);
        iv_share.setOnClickListener(this);
        txt_bar.setOnClickListener(this);
        listView.addHeaderView(convertView);
        et_comment.addTextChangedListener(textWatcher);
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
                btn_send.setEnabled(true);
                btn_send.setBackgroundResource(R.drawable.red_btn_bg);
                btn_send.setTextColor(getResources().getColor(R.color.white));
            } else {
                btn_send.setEnabled(false);
                btn_send.setBackgroundResource(R.drawable.assistant_white_send);
                btn_send.setTextColor(getResources().getColor(R.color.font_grey));
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                DiscoveryCommentAdd(model.discoveryId);
                break;
            case R.id.iv_like:
                DiscoveryPraise(model.discoveryId, model.isPraise);
                break;
            case R.id.iv_review:
                puserid = "0";
                pnikename = "";
                et_comment.setHint(R.string.comment);
                et_comment.requestFocus();
                Utility.openKeybord(et_comment, mContext);
                break;
            case R.id.img_body:
                ShowImageActivity.Create(this, model.imageUrl.replace("_ex.", "."), model.imageUrl);
                break;
            case R.id.iv_share:
                ShareFacebook shareFacebook = new ShareFacebook(mContext, (Activity) mContext);
                shareFacebook.postStatusUpdate(model.nickName, model.content, AppBalala.shareURL, bodybitmap);
                TaskUtil taskUtil = new TaskUtil(mContext);
                taskUtil.accomplishTasks("3");
                break;
            case R.id.txt_bar:
                String roomServerIp = model.roomserverip;
                RoomModel roomModel = new RoomModel();
                roomModel.setId(Integer.valueOf(model.barid));
                roomModel.setName(model.barname);
                roomModel.setIp(roomServerIp.split(":")[0]);
                roomModel.setPort(Integer.valueOf(roomServerIp.split(":")[1]));
                roomModel.setHeatDay(model.heatday);
                roomModel.setLevel(model.barlevel);

                Bundle bundle = new Bundle();
                bundle.putSerializable("room", roomModel);
                Intent intent = new Intent(mContext, ChatRoomActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.img_head:
                Intent userInfoActivity = new Intent(this, UserInfoActivity.class);
                userInfoActivity.putExtra("userid", model.userid);
                startActivity(userInfoActivity);
                break;
        }
    }

    /**
     * 点赞
     */
    private void DiscoveryPraise(String discoveryId, final String type) {
        HashMap<String, String> params = new HashMap<>();
        params.put("dyid", discoveryId);
        params.put("type", type);
        params.put("token", sharedUtil.getInstance(mContext).getUserToken());
        params.put("userid", sharedUtil.getInstance(mContext).getUid());
        GetJson.Callback callback = new GetJson.Callback() {
            @Override
            public void onFinish(String response) {
                CommonData results = JsonUtil.fromJson(response, CommonData.class);
                if (results != null) {
                    if (type.equals("0")) {
                        model.isPraise = "1";
                        iv_like.setImageResource(R.drawable.like_icon_on);
                    } else {
                        model.isPraise = "0";
                        iv_like.setImageResource(R.drawable.like_icon);
                    }
                    model.praiseNum = results.data;
                    tv_praiseNum.setText(results.data);
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
                    ToastUtils.showToast(DynamicActivity.this, MapUtil.getString(mContext, strCode));
                }
            }
        };
        GetJson getJson = new GetJson(mContext, callback, true, mContext.getString(R.string.loading));
        getJson.setConnection(Request.Method.GET, CommonUrlConfig.DiscoveryPraise, params);
    }

    /**
     * 评论
     */
    private void DiscoveryCommentAdd(String discoveryId) {
        if (et_comment.getText().toString().length() == 0) {
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("puserid", puserid);
        params.put("dyid", discoveryId);
        params.put("content", Encryption.utf8ToUnicode(et_comment.getText().toString()));
        params.put("token", sharedUtil.getInstance(mContext).getUserToken());
        params.put("userid", sharedUtil.getInstance(mContext).getUid());
        GetJson.Callback callback = new GetJson.Callback() {
            @Override
            public void onFinish(String response) {
                CommonListResult<DiscoveryCommentModel> results = JsonUtil.fromJson(response, new TypeToken<CommonListResult<DiscoveryCommentModel>>() {
                }.getType());
                if (results != null && results.hasData()) {
                    DiscoveryCommentModel commentmodel = new DiscoveryCommentModel();
                    commentmodel.Id = results.data.get(0).Id;
                    commentmodel.ToNickName = pnikename;
                    commentmodel.ToUserid = puserid;
                    commentmodel.Content = et_comment.getText().toString();
                    commentmodel.FromNickName = sharedUtil.getInstance(mContext).getUserName();
                    commentmodel.FromUserid = sharedUtil.getInstance(mContext).getUid();
                    commentmodel.fromHeadUrl = sharedUtil.getInstance(mContext).getUserPhoto();
                    commentmodel.createtime = getString(R.string.just);
                    ToastUtils.showToast(mContext, R.string.ok);
                    commentData.add(0, commentmodel);
                    mAdapter.notifyDataSetChanged();
                    et_comment.setText("");
                    tv_commentNum.setText(getString(R.string.view_all) + " " + commentData.size() + " " + getString(R.string.comment));

                    Utility.closeKeybord(et_comment, mContext);
                    puserid = "0";
                    pnikename = "";
                    et_comment.setHint(R.string.comment);
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
                    ToastUtils.showToast(DynamicActivity.this, MapUtil.getString(mContext, strCode));
                }
            }
        };
        GetJson getJson = new GetJson(mContext, callback, true, mContext.getString(R.string.loading));
        getJson.setConnection(Request.Method.POST, CommonUrlConfig.DiscoveryCommentAdd, params);
    }

    /**
     * 删除我的评论
     */
    private void DiscoveryCommentDel(final int position, String cid) {
        HashMap<String, String> params = new HashMap<>();
        params.put("cid", cid);
        params.put("dyid", model.discoveryId);
        params.put("token", sharedUtil.getInstance(mContext).getUserToken());
        params.put("userid", sharedUtil.getInstance(mContext).getUid());
        GetJson.Callback callback = new GetJson.Callback() {
            @Override
            public void onFinish(String response) {
                ToastUtils.showToast(mContext, R.string.ok);
                commentData.remove(position);
                mAdapter.notifyDataSetChanged();
                tv_commentNum.setText(getString(R.string.view_all) + " " + commentData.size() + " " + getString(R.string.comment));
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
                    ToastUtils.showToast(DynamicActivity.this, MapUtil.getString(mContext, strCode));
                }
            }
        };
        GetJson getJson = new GetJson(mContext, callback, true, mContext.getString(R.string.loading));
        getJson.setConnection(Request.Method.GET, CommonUrlConfig.DiscoveryCommentDel, params);
    }

    /**
     * 获取评论列表
     */
    private void DiscoveryCommentList() {
        HashMap<String, String> params = new HashMap<>();
        params.put("dyid", model.discoveryId);
        if (!IS_FIRST) {
            params.put("datesort", datesort);
        }
        params.put("pageindex", String.valueOf(pageindex));
        params.put("pagesize", "12");
        params.put("token", sharedUtil.getInstance(mContext).getUserToken());
        params.put("userid", sharedUtil.getInstance(mContext).getUid());

        GetJson.Callback callback = new GetJson.Callback() {
            @Override
            public void onFinish(String response) {
                CommonListResult<DiscoveryCommentModel> results = JsonUtil.fromJson(response, new TypeToken<CommonListResult<DiscoveryCommentModel>>() {
                }.getType());
                if (results != null && results.hasData()) {
                    // 设置适配器
                    if (IS_REFRESH) {
                        commentData.clear();
                        commentData.addAll(results.data);
                    } else {
                        commentData.addAll(results.data);
                    }
                    pageindex = results.index + 1;
                    mAdapter.notifyDataSetChanged();
                    datesort = String.valueOf(results.time);
                    IS_FIRST = false;
                    IS_REFRESH = false;
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
                    ToastUtils.showToast(DynamicActivity.this, MapUtil.getString(mContext, strCode));
                }
            }
        };
        GetJson getJson = new GetJson(mContext, callback, true, mContext.getString(R.string.loading));
        getJson.setConnection(Request.Method.GET, CommonUrlConfig.DiscoveryCommentList, params);
    }

    @Override
    public void onRefresh(final SwipyRefreshLayoutDirection direction) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (direction == SwipyRefreshLayoutDirection.TOP) {
                            IS_FIRST = true;
                            IS_REFRESH = true;
                            pageindex = 1;
                        }
                        DiscoveryCommentList();
                        mSwipyRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }, 100);
    }
}