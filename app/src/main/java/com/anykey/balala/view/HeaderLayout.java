package com.anykey.balala.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anykey.balala.R;

/**
 * Created by xujian on 15/8/27.
 * 自定义title
 */
public class HeaderLayout extends LinearLayout {
    LayoutInflater mInflater;
    RelativeLayout header;
    TextView titleView;
    LinearLayout leftContainer, rightContainer;
    Button backBtn,submit;
    View imageViewLayout;

    public HeaderLayout(Context context) {
        super(context);
        init();
    }

    public HeaderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mInflater = LayoutInflater.from(getContext());
        header = (RelativeLayout) mInflater.inflate(R.layout.base_common_header, null, false);
        titleView = (TextView) header.findViewById(R.id.titleView);
        leftContainer = (LinearLayout) header.findViewById(R.id.leftContainer);
        rightContainer = (LinearLayout) header.findViewById(R.id.rightContainer);
        backBtn = (Button) header.findViewById(R.id.backBtn);
        submit = (Button)header.findViewById(R.id.submit);
        addView(header);
    }

    public void showTitle(int titleId) {
        titleView.setText(titleId);
    }

    public void showTitle(String s) {
        titleView.setText(s);
    }

    public void showLeftBackButton(OnClickListener listener) {
        showLeftBackButton(R.string.emptyStr, listener);
    }

    public void showLeftBackButton() {
        showLeftBackButton(null);
    }

    public void showLeftBackButton(int backTextId, OnClickListener listener) {
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.back,0,0,0);
        backBtn.setText(backTextId);
        if (listener == null) {
            listener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((Activity) getContext()).finish();
                }
            };
        }
        backBtn.setOnClickListener(listener);
    }

    public void showLeftBackButton(int backTextId, OnClickListener listener,boolean isback) {
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setText(backTextId);
        if (listener == null) {
            listener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((Activity) getContext()).finish();
                }
            };
        }
        backBtn.setOnClickListener(listener);
    }

    public void showRightSubmitButton(int backTextId, OnClickListener listener) {
        submit.setVisibility(View.VISIBLE);
        submit.setText(backTextId);
        if (listener == null) {
            listener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((Activity) getContext()).finish();
                }
            };
        }
        submit.setOnClickListener(listener);
    }



    public void showLeftImageButton(int rightResId, OnClickListener listener) {
        View imageViewLayout = mInflater.inflate(R.layout.base_common_header_right_image_btn, null, false);
        ImageButton leftButton = (ImageButton) imageViewLayout.findViewById(R.id.imageBtn);
        leftButton.setImageResource(rightResId);
        leftButton.setOnClickListener(listener);
        leftContainer.addView(imageViewLayout);
    }

    public void showLeftImageButton(int rightResId, OnClickListener listener,boolean isShow) {
        View imageViewLayout = mInflater.inflate(R.layout.base_common_header_right_image_btn, null, false);
        ImageButton leftButton = (ImageButton) imageViewLayout.findViewById(R.id.imageBtn);
//        TextView notreadnum = (TextView)imageViewLayout.findViewById(R.id.notreadnum);
//        if (isShow){
//            notreadnum.setVisibility(VISIBLE);
//        }else{
//            notreadnum.setVisibility(GONE);
//        }
        leftButton.setImageResource(rightResId);
        leftButton.setOnClickListener(listener);
        leftContainer.addView(imageViewLayout);
    }

    public void showRightImageButton(int rightResId, OnClickListener listener) {
        View imageViewLayout = mInflater.inflate(R.layout.base_common_header_right_image_btn, null, false);
        ImageButton rightButton = (ImageButton) imageViewLayout.findViewById(R.id.imageBtn);
        rightButton.setImageResource(rightResId);
        rightButton.setOnClickListener(listener);
        rightContainer.addView(imageViewLayout);
    }

    public void showRightImageButton(int rightResId, OnClickListener listener,boolean isShow) {
        imageViewLayout = mInflater.inflate(R.layout.base_common_header_right_image_btn, null, false);
        ImageButton rightButton = (ImageButton) imageViewLayout.findViewById(R.id.imageBtn);
//        TextView notreadnum = (TextView)imageViewLayout.findViewById(R.id.notreadnum);
//        if (isShow){
//            notreadnum.setVisibility(VISIBLE);
//        }else{
//            notreadnum.setVisibility(GONE);
//        }
        rightButton.setImageResource(rightResId);
        rightButton.setOnClickListener(listener);
        rightContainer.addView(imageViewLayout);
    }

    public void removeView(){
        rightContainer.removeView(imageViewLayout);
    }

    public void showRightTextButton(int color, int rightResId, OnClickListener listener) {
        View imageViewLayout = mInflater.inflate(R.layout.base_common_header_right_btn, null, false);
        TextView rightButton = (TextView) imageViewLayout.findViewById(R.id.textBtn);
        rightButton.setText(rightResId);
        rightButton.setOnClickListener(listener);
        rightContainer.addView(imageViewLayout);
    }
}
