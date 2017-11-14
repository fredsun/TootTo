package org.tootto.ui.fragment.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by fred on 2017/11/13.
 */

public class NonSwipeableViewPager extends ViewPager {

    private boolean noScroll = true;
    public NonSwipeableViewPager(@NonNull Context context) {
        super(context);
    }

    public NonSwipeableViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setNoScroll(boolean noScroll) {
        this.noScroll = noScroll;
    }

    //默认返回true消费事件
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return noScroll || super.onInterceptTouchEvent(ev);
    }

    //默认返回true消费事件
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return noScroll || super.onTouchEvent(ev);
    }

    //去除切换效果
    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item,false);
    }


}
