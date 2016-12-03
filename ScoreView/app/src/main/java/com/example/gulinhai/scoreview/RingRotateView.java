package com.example.gulinhai.scoreview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;


/**
 * Created by glh on 2016-12-02.
 */
public class RingRotateView extends View {

    private Paint mOutCirclePaint;//外圆的画笔
    private int mOutCircleColor = -1;//外圆的颜色

    private Paint mInCirclePaint;//内圆的画笔
    private int mInCircleColor = -1;//内圆的颜色
    private int mInCircleWidth = 50;//内圆的宽度

    private Paint mInArcPaint;//内弧线的画笔
    private int mInArcWidth = 50;//内弧线的宽度
    private int width, height;//圆弧宽高

    private Paint mTextPaint;//文字画笔
    private int mTextSize = -1;

    private int mPadding = 100;//内边距

    private Context mContext;

    private Score currentPoint;
    private ValueAnimator animator;
    private Rect mTextRect;
    private RectF rectF=new RectF();
    private Shader mShader;//渐变值
    private Matrix mMatrix = new Matrix();

    private float mScore = 90;//得分
    private float mCountScore = 100;//总分

    private OnScoreListener mOnScoreListener;

    public interface OnScoreListener {
        void finish();
    }

    public RingRotateView(Context context) {
        this(context, null);
    }

    public RingRotateView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RingRotateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init(attrs);
    }

    private int parseColor(String color) {
        return Color.parseColor("#" + color);
    }

    /**
     * 初始化
     *
     * @param _attrs 自定义属性
     */
    private void init(AttributeSet _attrs) {
        initSweepGradient();//初始化渐变器
        getAttributeSet(_attrs);//获取自定义属性
        initOutCirclePaint();//初始化外圆属性
        initInCirclePaint();//初始化内圆属性
        initInArcPaint();//初始化内弧线属性
        initTextPaint();//初始化文字属性
        currentPoint = new Score(0, 0);
    }

    /**
     * 初始化渐变器
     */
    private void initSweepGradient() {
        mShader = new SweepGradient(0, 0, new int[]{0xFF09F68C,
                0xFFB0F44B,
                0xFFE8DD30,
                0xFFF1CA2E,
                0xFFFF6433}, null);

    }

    /**
     * 获取自定义属性
     *
     * @param _attributeSet 自定义属性
     */
    private void getAttributeSet(AttributeSet _attributeSet) {
        if (_attributeSet != null) {
            TypedArray typedArray = mContext.obtainStyledAttributes(_attributeSet, R.styleable.ScoreView);
            for (int i = 0, length = typedArray.getIndexCount(); i < length; i++) {
                int attr = typedArray.getIndex(i);
                switch (attr) {
                    case R.styleable.ScoreView_mOutCircleColor:
                        mOutCircleColor = typedArray.getColor(attr, -1);
                        break;
                    case R.styleable.ScoreView_mInArcWidth:
                        mInArcWidth = (int) typedArray.getDimension(attr, 50);
                        break;
                    case R.styleable.ScoreView_mInCircleColor:
                        mInCircleColor = typedArray.getColor(attr, -1);
                        break;
                    case R.styleable.ScoreView_mInCircleWidth:
                        mInCircleWidth = typedArray.getDimensionPixelSize(attr, 50) / 2;
                    case R.styleable.ScoreView_mTextSize:
                        mTextSize = typedArray.getDimensionPixelSize(attr, 20);
                        break;
                    default:
                        break;
                }
            }
            typedArray.recycle();
        }
        mPadding = mInArcWidth;

    }

    /**
     * 初始化外圆属性
     */
    private void initOutCirclePaint() {
        mOutCirclePaint = new Paint();
        mOutCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mOutCirclePaint.setStrokeWidth((float) (mInArcWidth * (1.0 / 5.0)));
        if (mOutCircleColor == -1) {
            mOutCirclePaint.setColor(parseColor("0d000000"));
        } else {
            mOutCirclePaint.setColor(mOutCircleColor);
        }
        mOutCirclePaint.setStrokeCap(Paint.Cap.ROUND);
        mOutCirclePaint.setAntiAlias(true);
    }

    /**
     * 初始化内圆属性
     */
    private void initInCirclePaint() {
        mInCirclePaint = new Paint();
        mInCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mInCirclePaint.setStrokeWidth(mInArcWidth * 2);
        if (mInCircleColor == -1) {
            mInCirclePaint.setColor(parseColor("03000000"));
        } else {
            mInCirclePaint.setColor(mInCircleColor);
        }
        mInCirclePaint.setStrokeCap(Paint.Cap.ROUND);
        mInCirclePaint.setAntiAlias(true);
    }

    /**
     * 初始化内弧线属性
     */
    private void initInArcPaint() {
        mInArcPaint = new Paint();
        mInArcPaint.setStyle(Paint.Style.STROKE);
        mInArcPaint.setStrokeWidth(mInArcWidth);
        mInArcPaint.setColor(Color.RED);
        mInArcPaint.setStrokeCap(Paint.Cap.ROUND);
        mInArcPaint.setAntiAlias(true);
        mInArcPaint.setShader(mShader);
    }

    /**
     * 初始化文字属性
     */
    private void initTextPaint() {
        mTextPaint = new Paint();
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setStrokeWidth(10);
        if (mTextSize == -1) {
            mTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, mContext.getResources().getDisplayMetrics()));
        } else {
            mTextPaint.setTextSize(mTextSize);
        }
        mTextPaint.setStrokeCap(Paint.Cap.ROUND);
        mTextPaint.setAntiAlias(true);
        mTextRect = new Rect();
        mTextPaint.setShader(mShader);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);


    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mTextPaint.getTextBounds("" + currentPoint.score * 2, 0, ("" + currentPoint.score * 2).length(), mTextRect);
        mMatrix.setRotate(currentPoint.angle * 2, 0, height);
        mShader.setLocalMatrix(mMatrix);
        rectF.left=mPadding;
        rectF.top=mPadding;
        rectF.right=width-mPadding;
        rectF.bottom=height-mPadding;
        canvas.drawArc(rectF, 0, 360, false, mOutCirclePaint);//外圆
        canvas.drawCircle(width / 2, height / 2, mInCircleWidth, mInCirclePaint);//内圆
        canvas.drawArc(rectF, -(90 + currentPoint.angle), currentPoint.angle * 2, false, mInArcPaint);//圆弧线
        canvas.drawText("" + currentPoint.score * 2, width / 2 - mTextRect.width() / 2, height / 2 + mTextRect.height() / 2, mTextPaint);//文字

    }


    private void startCircleAnimation(TimeInterpolator _interpolator, long _duration) {
        final float core = ((mScore / mCountScore) * 360);
        if (animator == null) {
            animator = ValueAnimator.ofObject(new TypeEvaluator() {
                @Override
                public Object evaluate(float fraction, Object startValue, Object endValue) {
                    Score start = (Score) startValue;
                    Score end = (Score) endValue;
                    float startAngle = start.angle;
                    float endAngle = end.angle;

                    float startScore = start.score;
                    float endScore = end.score;

                    int currentAngle = (int) ((startAngle + (endAngle - startAngle) * fraction) / 2);
                    int score = (int) ((startScore + (endScore - startAngle) * fraction) / 2);

                    return new Score(score, currentAngle);
                }
            }, new Score(0, 0), new Score((int) mScore, (int) core));


            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    currentPoint = (Score) valueAnimator.getAnimatedValue();
                    invalidate();
                }


            });

            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (mOnScoreListener != null) {
                        mOnScoreListener.finish();
                    }
                }
            });
            if (_interpolator != null) {
                animator.setInterpolator(_interpolator);
            }
            if (_duration == 0) {
                animator.setDuration(2000);
            } else {
                animator.setDuration(_duration);
            }
        }

        if (!animator.isRunning()) {
            animator.start();
        }

    }


    /**
     * 设置评分
     *
     * @param _score      得分
     * @param _countScore 总分
     */
    public void setScore(float _score, float _countScore) {
        this.mCountScore = _countScore;
        this.mScore = _score;
    }


    public void startAnimation() {
        startCircleAnimation(null, 0);
    }

    public void startAnimation(TimeInterpolator _interpolator) {
        startCircleAnimation(_interpolator, 0);
    }

    public void startAnimation(TimeInterpolator _interpolator, long _duration) {
        startCircleAnimation(_interpolator, _duration);
    }

    public void setOnScoreListener(OnScoreListener _listener) {
        this.mOnScoreListener = _listener;
    }

}
