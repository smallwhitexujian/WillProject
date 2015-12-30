package com.anykey.balala.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anykey.balala.R;
import com.anykey.balala.Utils.MyOnPageChangeListener;
import com.anykey.balala.adapter.MyFragmentPagerAdapter;
import com.anykey.balala.view.HeaderLayout;
import java.util.ArrayList;

/**
 * Created by shanli on 15/9/11.
 * Discover Fragment.  发现
 */
public class DiscoverFragment extends Hintfragment implements View.OnClickListener {
    //控件定义
    private ArrayList<Fragment> fragmentList;
    private DynamicFragment dynamicFragment;
    private PeopleFragment peopleFragment;
    private ViewPager mAbSlidingTabView;
    private MyOnPageChangeListener mypageChanger;
    private View rootView;
    public static int currIndex = 0;
    public static TextView tab_onLine, tab_barInfo;
    private ImageView imageView;
    private boolean isVisible = false;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_discover, null);
        initview();
        isVisible = true;
        return rootView;
    }

    @Override
    public void onResume() {
        if (isVisible) {
            dynamicFragment.onResume();
        }
        super.onResume();
    }

    private void initview() {
        headerLayout = (HeaderLayout) rootView.findViewById(R.id.headerLayout);
        headerLayout.showLeftImageButton(R.drawable.bar_top_icon,new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        imageView = (ImageView) rootView.findViewById(R.id.iv_bottom_line);
        tab_onLine = (TextView) rootView.findViewById(R.id.Tab_Online);
        tab_barInfo = (TextView) rootView.findViewById(R.id.Tab_BarInfo);
        mAbSlidingTabView = (ViewPager) rootView.findViewById(R.id.mAbSlidingTabView);
        tab_barInfo.setOnClickListener(this);
        tab_onLine.setOnClickListener(this);

        mypageChanger = new MyOnPageChangeListener(getString(R.string.tab_discoverFragment));
        mypageChanger.InitWidth(imageView, getActivity());
        fragmentList = new ArrayList<>();

        dynamicFragment = new DynamicFragment();
        fragmentList.add(dynamicFragment);

        peopleFragment = new PeopleFragment();
        fragmentList.add(peopleFragment);

        mAbSlidingTabView.setAdapter(new MyFragmentPagerAdapter(getActivity().getSupportFragmentManager(), fragmentList));
        mAbSlidingTabView.setCurrentItem(0);
        mypageChanger.onPageSelected(0);
        tab_onLine.setSelected(true);
        tab_barInfo.setSelected(false);
        mAbSlidingTabView.setOnPageChangeListener(mypageChanger);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Tab_Online:
                mypageChanger.onPageSelected(0);
                mAbSlidingTabView.setCurrentItem(0);
                break;
            case R.id.Tab_BarInfo:
                mypageChanger.onPageSelected(1);
                mAbSlidingTabView.setCurrentItem(1);
                break;
            default:
                break;
        }
    }
    @Override
    protected void lazyLoad() {
    }
}