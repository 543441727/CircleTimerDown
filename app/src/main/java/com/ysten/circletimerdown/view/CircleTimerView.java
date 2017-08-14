package com.ysten.circletimerdown.view;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;

import com.ysten.circletimerdown.R;

import java.util.Timer;
import java.util.TimerTask;


/**
 * author : wangjitao
 * e-mail : 543441727@qq.com
 * time   : 2017/08/14
 * desc   :
 * version: 1.0
 */
public class CircleTimerView extends View {

    private Context context ;

    //里面实心圆颜色
    private int mSolidCircleColor ;
    //里面圆的半径
    private int mSolidCircleRadius;
    //外面圆弧的颜色
    private int mEmptyCircleColor ;
    //外面圆弧的半径(可以使用画笔的宽度来实现)
    private int mEmptyCircleRadius ;
    //文字大小
    private int mTextSize ;
    //文字颜色
    private int mTextColor ;
    //文字
    private String mText ;
    //绘制的方向
    private int mDrawOrientation;
    //圆弧绘制的速度
    private int mSpeed;
    //圆的画笔
    private Paint mPaintCircle ;
    //圆弧的画笔
    private Paint mPaintArc ;
    //绘制文字的画笔
    private Paint mPaintText;
    //时长
    private int mTimeLength ;

    //默认值
    private int defaultSolidCircleColor ;
    private int defaultEmptyCircleColor ;
    private int defaultSolidCircleRadius ;
    private int defaultEmptyCircleRadius ;
    private int defaultTextColor ;
    private int defaultTextSize ;
    private int defaultTimeLength ;
    private int defaultDrawOritation ;

    //当前扇形的角度
    private int startProgress ;
    private int endProgress ;
    private float currProgress ;

    //动画集合
    private AnimatorSet set ;

    //回调
    private OnCountDownFinish onCountDownFinish ;

    public CircleTimerView(Context context) {
        this(context,null);
    }

    public CircleTimerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CircleTimerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context ;

        //初始化默认值
        defaultSolidCircleColor = getResources().getColor(R.color.colorPrimary);
        defaultEmptyCircleColor = getResources().getColor(R.color.colorAccent);
        defaultTextColor = getResources().getColor(R.color.colorYellow);

        defaultSolidCircleRadius = (int) getResources().getDimension(R.dimen.dimen_20);
        defaultEmptyCircleRadius = (int) getResources().getDimension(R.dimen.dimen_25);
        defaultTextSize = (int) getResources().getDimension(R.dimen.dimen_16);

        defaultTimeLength = 3 ;
        defaultDrawOritation = 1 ;

        //获取自定义属性
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleTimerView);
        mSolidCircleColor = a.getColor(R.styleable.CircleTimerView_solid_circle_color,defaultSolidCircleColor);
        mSolidCircleRadius = a.getDimensionPixelOffset(R.styleable.CircleTimerView_solid_circle_radius ,defaultSolidCircleRadius);

        mEmptyCircleColor = a.getColor(R.styleable.CircleTimerView_empty_circle_color,defaultEmptyCircleColor);
        mEmptyCircleRadius = a.getDimensionPixelOffset(R.styleable.CircleTimerView_empty_circle_radius ,defaultEmptyCircleRadius);

        mTextColor = a.getColor(R.styleable.CircleTimerView_circle_text_color,defaultTextColor);
        mTextSize = a.getDimensionPixelOffset(R.styleable.CircleTimerView_circle_text_size ,defaultTextSize);

        mDrawOrientation = a.getInt(R.styleable.CircleTimerView_circle_draw_orientation,defaultDrawOritation);
        mTimeLength = a.getInt(R.styleable.CircleTimerView_time_length ,defaultTimeLength);

        a.recycle();

        init();
    }

    private void init() {
        //初始化画笔
        mPaintCircle = new Paint();
        mPaintCircle.setStyle(Paint.Style.FILL);
        mPaintCircle.setAntiAlias(true);
        mPaintCircle.setColor(mSolidCircleColor);

        mPaintArc = new Paint();
        mPaintArc.setStyle(Paint.Style.STROKE);
        mPaintArc.setAntiAlias(true);
        mPaintArc.setColor(mEmptyCircleColor);
        mPaintArc.setStrokeWidth(mEmptyCircleRadius - mSolidCircleRadius);

        mPaintText = new Paint();
        mPaintText.setStyle(Paint.Style.STROKE);
        mPaintText.setAntiAlias(true);
        mPaintText.setTextSize(mTextSize);
        mPaintText.setColor(mTextColor);

        mText= mTimeLength +"" ;
        if(defaultDrawOritation == 1){
            startProgress = 360 ;
            endProgress = 0 ;
        }else {
            startProgress = 0 ;
            endProgress = 360 ;
        }
        currProgress = startProgress ;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //设置宽高
        setMeasuredDimension(mEmptyCircleRadius*2,mEmptyCircleRadius*2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制背景圆
        canvas.drawCircle(mEmptyCircleRadius,mEmptyCircleRadius,mSolidCircleRadius,mPaintCircle);

        //绘制圆弧
        RectF oval = new RectF((mEmptyCircleRadius - mSolidCircleRadius)/2, (mEmptyCircleRadius - mSolidCircleRadius)/2
                , mEmptyCircleRadius + (mEmptyCircleRadius - mSolidCircleRadius)/2+mSolidCircleRadius, mEmptyCircleRadius + (mEmptyCircleRadius - mSolidCircleRadius)/2+mSolidCircleRadius); // 用于定义的圆弧的形状和大小的界限

        canvas.drawArc(oval, -90, currProgress, false, mPaintArc); // 根据进度画圆弧

        //绘制文字
        Rect mBound = new Rect();
        mPaintText.getTextBounds(mText, 0, mText.length(), mBound);
        canvas.drawText(mText, getWidth() / 2 - mBound.width() / 2, getHeight() / 2 + mBound.height() / 2, mPaintText);
    }

    public OnCountDownFinish getOnCountDownFinish() {
        return onCountDownFinish;
    }

    public void setOnCountDownFinish(OnCountDownFinish onCountDownFinish) {
        this.onCountDownFinish = onCountDownFinish;
    }


    /**
     * 通过外部开关控制
     */
    public void start(){

        ValueAnimator animator1 = ValueAnimator.ofFloat(startProgress,endProgress);
        animator1.setInterpolator(new LinearInterpolator());
        animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                currProgress = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });

        ValueAnimator animator2 = ValueAnimator.ofInt(mTimeLength,0);
        animator2.setInterpolator(new LinearInterpolator());
        animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mTimeLength = (int) valueAnimator.getAnimatedValue();
                if (mTimeLength == 0)
                    return;
                mText =mTimeLength+ "";
            }
        });

        set = new AnimatorSet();
        set.playTogether(animator1,animator2);
        set.setDuration(mTimeLength * 1000);

        set.start();

        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (onCountDownFinish != null){
                    onCountDownFinish.onFinish();
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });


    }

    public void cancelAnim(){
        if(set != null)
        set.pause();
    }

    public interface OnCountDownFinish{
        void onFinish();
    }
}
