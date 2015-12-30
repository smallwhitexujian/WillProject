package com.anykey.balala.fragment;

/**
 * Created by xujian on 15/8/26.
 * hint判断当前fragment是否处于显示状态，显示才会加载(实现数据缓加载)
 */
public abstract class Hintfragment extends BaseFragment{
    /**
     * Fragment当前状态是否可见
     */
    protected boolean isVisible = false;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    /**
     * 可见
     */
    protected void onVisible() {
        lazyLoad();
    }

    /**
     * 不可见
     */
    protected void onInvisible() {

    }

    /**
     * 延迟加载
     * 子类必须重写此方法
     */
    protected abstract void lazyLoad();
}
