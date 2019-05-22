package com.example.myapplication.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Interpolator;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ScrollView;

import com.example.myapplication.R;


public class CustomScrollBarScrollView extends ScrollView implements Runnable {

    private Drawable mScrollBar;
    private int mContentViewWitdh;
    private int mContentViewHeight;
    private int mScrollBarMaginContent;
    private int mThumbWidth;
    private int mThumbHeight;
    private float mContentMaxScrollLength;
    private float mThumbMaxScrollLength;
    private int mScrollBarState;
    private Handler mHandler = new Handler();
    private int mTop;
    private float[] interpolatorValues;
    public final Interpolator scrollBarInterpolator = new Interpolator(1, 2);
    private static final float[] OPAQUE = {255};
    private static final float[] TRANSPARENT = {0.0f};
    private static final int SCROLL_BAR_FADE_DURATION = 250;
    private static final int SCROLL_BAR_DEFAULT_DELAY_BEFORE_FADE = 1600; // 400 * 4 , 源码
    private static final int SCROLL_BAR_DEFAULT_DELAY = 300;


    /**
     * Scrollbars are not visible
     */
    public static final int OFF = 0;

    /**
     * Scrollbars are visible
     */
    public static final int ON = 1;

    /**
     * Scrollbars are fading away
     */
    public static final int FADING = 2;
    private long fadeStartTime;


    public CustomScrollBarScrollView(Context context) {
        super(context);
    }

    public CustomScrollBarScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomScrollBarScrollView);
        mScrollBar = a.getDrawable(R.styleable.CustomScrollBarScrollView_scrollBar);
        mScrollBarMaginContent = a.getDimensionPixelSize(R.styleable.CustomScrollBarScrollView_scrollbarMarginContent, 0);

        a.recycle();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int scrollViewHeight = getMeasuredHeight();

        mThumbWidth = mScrollBar.getMinimumWidth();
        mThumbHeight = mScrollBar.getMinimumHeight();

        View contentView = getChildAt(0);
        mContentViewWitdh = contentView.getMeasuredWidth();
        mContentViewHeight = contentView.getMeasuredHeight();

        mContentMaxScrollLength = mContentViewHeight - scrollViewHeight;
        mThumbMaxScrollLength = scrollViewHeight - mThumbHeight;


        LayoutParams layoutParams = (LayoutParams) contentView.getLayoutParams();
        layoutParams.rightMargin = mScrollBarMaginContent + mThumbWidth;
        contentView.setLayoutParams(layoutParams);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            postInvalidateOnAnimation();
        } else {
            invalidate();
        }
        mTop = t;
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        awakenCustomScrollBars();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int state = mScrollBarState;
        if (state == OFF) {
            return;
        }
        boolean invalidate = false;

        if (state == FADING) {
            // We're fading -- get our fade interpolation
            if (interpolatorValues == null) {
                interpolatorValues = new float[1];
            }

            float[] values = interpolatorValues;   // float[] values = new float[1];

            Interpolator.Result result = scrollBarInterpolator.timeToValues(values);
            if (result ==
                    Interpolator.Result.FREEZE_END) {
                mScrollBarState = OFF;
            } else {
                int alpha = Math.round(values[0]);
                mScrollBar.mutate().setAlpha(alpha);
            }
            invalidate = true;
        } else {
            // We're just on -- but we may have been fading before so
            // reset alpha
            mScrollBar.mutate().setAlpha(255);
        }

        mScrollBar.setBounds(mContentViewWitdh + mScrollBarMaginContent, (int) (mThumbMaxScrollLength / mContentMaxScrollLength * mTop) + mTop, mContentViewWitdh + mScrollBarMaginContent + mThumbWidth, (int) (mThumbMaxScrollLength / mContentMaxScrollLength * mTop) + mThumbHeight + mTop);
        mScrollBar.draw(canvas);

        if (invalidate) {
            invalidate();
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
            initialAwakenScrollBars();
        }
    }

    private void initialAwakenScrollBars() {
        awakenCustomScrollBars(SCROLL_BAR_DEFAULT_DELAY_BEFORE_FADE, true);
    }

    private void awakenCustomScrollBars() {
        awakenCustomScrollBars(SCROLL_BAR_DEFAULT_DELAY, true);
    }

    private void awakenCustomScrollBars(int startDelay, boolean invalidate) {
        if (invalidate) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                postInvalidateOnAnimation();
            } else {
                invalidate();
            }
        }

        if (mScrollBarState == OFF) {
            final int KEY_REPEAT_FIRST_DELAY = 750;
            startDelay = Math.max(KEY_REPEAT_FIRST_DELAY, startDelay);
        }

        fadeStartTime = AnimationUtils.currentAnimationTimeMillis() + startDelay;

        mScrollBarState = ON;

        mHandler.removeCallbacks(this);
        mHandler.postAtTime(this, fadeStartTime);
    }


    @Override
    public void run() {
        long now = AnimationUtils.currentAnimationTimeMillis();
        if (now >= fadeStartTime) {

            int nextFrame = (int) now;
            int framesCount = 0;

            Interpolator interpolator = scrollBarInterpolator;

            // Start opaque
            interpolator.setKeyFrame(framesCount++, nextFrame, OPAQUE);

            // End transparent
            nextFrame += SCROLL_BAR_FADE_DURATION;

            interpolator.setKeyFrame(framesCount, nextFrame, TRANSPARENT);

            mScrollBarState = FADING;

            // Kick off the fade animation
            this.invalidate();
        }
    }
}
