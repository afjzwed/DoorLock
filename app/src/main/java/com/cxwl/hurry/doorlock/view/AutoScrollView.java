package com.cxwl.hurry.doorlock.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by William on 2018/6/6.
 */

public class AutoScrollView extends ScrollView {
    private Handler autoScrollhandler = new Handler();
    private long duration = 200;
    private boolean isScrolled = false;
    private int currentIndex = 0;
    private long period = 1000;
    private int currentY = -1;
    private double x;
    private double y;

    /**
     * @param context
     */
    public AutoScrollView(Context context) {
        this(context, null);
    }

    /**
     * @param context
     * @param attrs
     */
    public AutoScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public AutoScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent p_event) {
        Log.d("test", "onInterceptTouchEvent");
        return true;
    }

    /**
     * 判断当前是否为滚动状态
     *
     * @return the isScrolled
     */
    public boolean isScrolled() {
        return isScrolled;
    }

    /**
     * 开启或者关闭自动滚动功能
     *
     * @param isScrolled true为开启，false为关闭
     */
    public void setScrolled(boolean isScrolled) {
        this.isScrolled = isScrolled;
    }

    /**
     * 获取当前滚动到结尾时的停顿时间，单位：毫秒
     *
     * @return the period
     */
    public long getPeriod() {
        return period;
    }

    /**
     * 设置当前滚动到结尾时的停顿时间，单位：毫秒
     *
     * @param period
     */
    public void setPeriod(long period) {
        this.period = period;
    }

    /**
     * 获取当前的滚动速度，单位：毫秒，值越小，速度越快。
     */
    public long getSpeed() {
        return duration;
    }

    /**
     * 设置当前的滚动速度，单位：毫秒，值越小，速度越快。
     *
     * @param speed
     */
    public void setSpeed(long speed) {
        this.duration = speed;
    }

    public void autoScroll(boolean isFrist) {
        if (isFrist) {
            autoScrollhandler.postDelayed(runnable, period);
        } else {
            removeRunnable();
            autoScrollhandler.postDelayed(runnable, period);
        }
    }

    public void removeRunnable(){
        autoScrollhandler.removeCallbacks(runnable);
    }

    public void removeAll() {
        autoScrollhandler.removeCallbacksAndMessages(null);//在onDestroy里调用
        //        autoScrollhandler.removeMessage(0)会把所有可执行任务都移除掉
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isScrolled) {
//                Log.d("wh", "currentY = " + currentY + " getScrollY() = " + getScrollY());
                if (currentY == getScrollY()) {//滚动停止了
                    currentIndex = 0;
                    currentY = -1;
                    scrollTo(0, 0);
                    autoScrollhandler.postDelayed(this, period);
                } else {
                    currentY = getScrollY();
                    currentIndex++;
                    scrollTo(0, currentIndex * 1);//往上滑1像素
                    autoScrollhandler.postDelayed(this, duration);
                }
            } else {
                currentIndex = 0;
                currentY = -1;
                scrollTo(0, 0);
            }
        }
    };

    public void change() {
//        currentIndex = 0;
        currentY = -1;
        scrollTo(0, 0);
//        Log.d("wh", "currentY = " + currentY + " getScrollY() = " + getScrollY());
    }

}
