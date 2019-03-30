package com.example.myapplication.scrolllayout;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class ProvideScrollStateScrollView extends ScrollView {

    private OnScrollChangedListener mListner;
    private int lastTop;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 当手指抬起后发动检测，这个时候手指抬起时的为止和当前位置一样，认为ScrollView没有滑动了
            if (lastTop == getScrollY()) {
                mListner.onHideScrollBar();
            } else {
                removeMessages(CHECK_STATE);
                sendEmptyMessageDelayed(CHECK_STATE, 300);
            }
        }
    };

    private int CHECK_STATE = 0;

    public ProvideScrollStateScrollView(Context context) {
        super(context);
    }

    public ProvideScrollStateScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProvideScrollStateScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setScrollChangedListener(OnScrollChangedListener listener) {
        mListner = listener;
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mListner == null) {
            return;
        }

        lastTop = t;
        mHandler.removeMessages(CHECK_STATE);
        mHandler.sendEmptyMessageDelayed(CHECK_STATE, 300);
        mListner.onScrollChanged(l, t, oldl, oldt);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                mListner.onShowScrollBar();
                break;
            case MotionEvent.ACTION_UP:
                lastTop = getScrollY();
                mHandler.removeMessages(CHECK_STATE);
                mHandler.sendEmptyMessageDelayed(CHECK_STATE, 300);
                break;
        }

        return super.onTouchEvent(ev);
    }


    public interface OnScrollChangedListener {

        void onScrollChanged(int l, int t, int oldl, int oldt);

        void onShowScrollBar();

        void onHideScrollBar();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeMessages(CHECK_STATE);
        mHandler = null;
        if (mListner != null) {
            mListner = null;
        }
    }
}
