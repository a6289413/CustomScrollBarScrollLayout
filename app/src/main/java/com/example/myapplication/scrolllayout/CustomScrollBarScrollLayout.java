package com.example.myapplication.scrolllayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.myapplication.R;


public class CustomScrollBarScrollLayout extends FrameLayout {
    private ImageView mIvScrollBar;
    private ProvideScrollStateScrollView mPsssvScrollView;
    private Handler mHandler = new Handler();
    private int mScrollBarX;
    private float mScrollViewCanScrollHeight;
    private float mScrollBarCanScrollHeight;
    private int mScrollBarHeight;
    private int mScrollBarWidth;

    private int mScrollBarMarginScrollView;


    public CustomScrollBarScrollLayout(@NonNull Context context) {
        super(context);
        initView(context, null, 0);
    }

    public CustomScrollBarScrollLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, 0);
    }

    public CustomScrollBarScrollLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_scrollbar_scroll_layout, this);
        mIvScrollBar = view.findViewById(R.id.iv_scroll_bar);
        mPsssvScrollView = view.findViewById(R.id.psssv_srcoll_view);
        setScollChangedListener();

        if (attrs != null) {
            final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomScrollBarScrollLayout);
            mIvScrollBar.setImageDrawable(a.getDrawable(R.styleable.CustomScrollBarScrollLayout_scrollBar));
            mScrollBarMarginScrollView = (int) a.getDimension(R.styleable.CustomScrollBarScrollLayout_scrollBarMarginScrollView, 0);
            a.recycle();
        }

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int viewHeight = getMeasuredHeight();
        mScrollBarWidth = mIvScrollBar.getMeasuredWidth();
        mScrollBarHeight = mIvScrollBar.getMeasuredHeight();

        int range = mPsssvScrollView.getChildAt(0).getMeasuredHeight();
        mScrollViewCanScrollHeight = range - viewHeight;
        mScrollBarCanScrollHeight = viewHeight - mScrollBarHeight;

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mIvScrollBar.getLayoutParams();
        params.leftMargin = mScrollBarMarginScrollView;
        mIvScrollBar.setLayoutParams(params);
    }

    private void setScollChangedListener() {
        mPsssvScrollView.setScrollChangedListener(new ProvideScrollStateScrollView.OnScrollChangedListener() {
            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {
                if (t >= 0 && t <= mScrollViewCanScrollHeight) {
                    mIvScrollBar.layout(mScrollBarX, (int) (mScrollBarCanScrollHeight / mScrollViewCanScrollHeight * t), mScrollBarX + mScrollBarWidth, (int) (mScrollBarCanScrollHeight / mScrollViewCanScrollHeight * t + mScrollBarHeight));
                }
            }

            @Override
            public void onShowScrollBar() {
                mHandler.removeCallbacksAndMessages(null);
                if (mIvScrollBar.getVisibility() == View.INVISIBLE) {
                    if (mIvScrollBar.getAnimation() != null) {
                        mIvScrollBar.clearAnimation();
                    }
                    mIvScrollBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onHideScrollBar() {
                mHandler.removeCallbacksAndMessages(null);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mIvScrollBar.getVisibility() == VISIBLE) {
                            hideOrShowScrollBar(1, 0, 200, INVISIBLE);
                        }
                    }
                }, 200);
            }
        });
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
            if (mIvScrollBar.getAnimation() != null) {
                mIvScrollBar.clearAnimation();
            }

            if (mIvScrollBar.getVisibility() == INVISIBLE) {
                mIvScrollBar.setVisibility(View.VISIBLE);
            }

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    hideOrShowScrollBar(1, 0, 200, INVISIBLE);
                }
            }, 2000);
        }
    }

    private void hideOrShowScrollBar(int from, int to, int duration, int viewState) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(from, to);
        alphaAnimation.setFillAfter(true);
        alphaAnimation.setDuration(duration);
        mIvScrollBar.setAnimation(alphaAnimation);
        mIvScrollBar.setVisibility(viewState);
    }

    // view 绘制完成后才会被调用
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        mScrollBarX = (int) mIvScrollBar.getX();
    }

    public void addScrollViewContent(View view) {
        mPsssvScrollView.addView(view);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }
}
