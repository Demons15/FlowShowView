package com.cxsz.flowshowview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.graphics.Shader;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

/**
 * 流量显示视图
 */
public class FlowShowView extends BaseFlowShowView {

    //外环画笔
    private Paint mPaintOuterArc;
    //内环画笔
    private Paint mPaintInnerArc;
    //进度点画笔
    private Paint mPaintProgressPoint;
    //指示器画笔
    private Paint mPaintIndicator;
    //外环区域
    private RectF mRectOuterArc;
    //内环区域
    private RectF mRectInnerArc;
    //圆环画笔颜色
    private int mOuterArcColor;
    private int mProgressOuterArcColor;
    //内外环之间的间距
    private float mArcSpacing;
    //进度条的圆点属性
    private float[] mProgressPointPosition;
    private float mProgressPointRadius;
    //指标器的Path
    private Path mIndicatorPath;
    //指示器的起始位置
    private float mIndicatorStart;

    //默认圆环之间间距
    private static final float DEFAULT_ARC_SPACING = 10;
    //外环的默认属性
    private static final float DEFAULT_OUTER_ARC_WIDTH = 6f;
    private static final int DEFAULT_OUTER_ARC_COLOR = Color.parseColor("#FF3E4D6D");
    //外环进度的默认属性
    private static final int DEFAULT_PROGRESS_OUTER_ARC_COLOR = Color.parseColor("#FFE2AA24");
    //进度点的默认属性
    private static final float DEFAULT_PROGRESS_POINT_RADIUS = 6;
    private static final int DEFAULT_PROGRESS_POINT_COLOR = Color.parseColor("#FFE2AA24");
    //内环默认属性
    private static final int DEFAULT_INNER_ARC_COLOR = Color.parseColor("#FFE2AA24");
    //指示器默认属性
    private static final int DEFAULT_INDICATOR_COLOR = Color.parseColor("#FFE2AA24");

    // 大刻度画笔默认值
    private final static float DEFAULT_LARGE_CALIBRATION_WIDTH = 2f;
    private final static int DEFAULT_LARGE_CALIBRATION_COLOR = Color.argb(200, 255, 255, 255);
    // 小刻度画笔默认值
    private final static float DEFAULT_SMALL_CALIBRATION_WIDTH = 0.5f;
    private final static int DEFAULT_SMALL_CALIBRATION_COLOR = Color.argb(100, 255, 255, 255);
    // 默认刻度文字画笔参数
    private final static float DEFAULT_CALIBRATION_TEXT_TEXT_SIZE = 14f;
    private final static int DEFAULT_CALIBRATION_TEXT_TEXT_COLOR = Color.parseColor("#FFE2AA24");
    //大刻度画笔
    protected Paint mPaintLargeCalibration;
    //小刻度画笔
    protected Paint mPaintSmallCalibration;
    //刻度文字画笔
    protected Paint mPaintCalibrationText;
    //刻度起始位置和结束位置
    private float mCalibrationStart;
    private float mCalibrationEnd;
    //刻度的文本位置
    private float mCalibrationTextStart;
    private Paint mSweepPaintOuterArc;
    private Paint progressPaint;

    public FlowShowView(Context context) {
        this(context, null);
    }

    public FlowShowView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowShowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 初始化界面
     */
    @Override
    protected void initView() {
        //默认数据
        mArcSpacing = dp2px(DEFAULT_ARC_SPACING);
        mOuterArcColor = DEFAULT_OUTER_ARC_COLOR;
        mProgressOuterArcColor = DEFAULT_PROGRESS_OUTER_ARC_COLOR;
        mProgressPointRadius = dp2px(DEFAULT_PROGRESS_POINT_RADIUS);

        //初始化画笔
        //外环画笔
        mPaintOuterArc = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintOuterArc.setStrokeWidth(dp2px(DEFAULT_OUTER_ARC_WIDTH));
        mPaintOuterArc.setStyle(Paint.Style.STROKE);
        mPaintOuterArc.setStrokeCap(Paint.Cap.ROUND);
        //进度的画笔
        progressPaint = new Paint();
        LinearGradient progresslinearGradient = new LinearGradient(600, 0, 0, 0, new int[]{ Color.parseColor("#FFE2AA24"), Color.parseColor("#E6E2AA24"), Color.parseColor("#CCE2AA24"),Color.parseColor("#B3E2AA24"),
                Color.parseColor("#99E2AA24"), Color.parseColor("#80E2AA24"),Color.parseColor("#66E2AA24"), Color.parseColor("#4DE2AA24"), Color.parseColor("#33E2AA24"),Color.parseColor("#1AE2AA24"), Color.parseColor("#00E2AA24")}, null, LinearGradient.TileMode.MIRROR);
        progressPaint.setShader(progresslinearGradient);
        progressPaint.setStrokeWidth(dp2px(DEFAULT_OUTER_ARC_WIDTH));
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        //扫过的画笔
        mSweepPaintOuterArc = new Paint(Paint.ANTI_ALIAS_FLAG);
        LinearGradient linearGradient = new LinearGradient(600, 0, 0, 0, new int[]{ Color.parseColor("#1AE2AA24"), Color.parseColor("#0DE2AA24"), Color.parseColor("#00000000")}, null, LinearGradient.TileMode.MIRROR);
        mSweepPaintOuterArc.setShader(linearGradient);

        //内环画笔
        mPaintInnerArc = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintInnerArc.setColor(DEFAULT_INNER_ARC_COLOR);

        //进度点画笔
        mPaintProgressPoint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintProgressPoint.setStyle(Paint.Style.FILL);
        mPaintProgressPoint.setColor(DEFAULT_PROGRESS_POINT_COLOR);


        //指示器画笔
        mPaintIndicator = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintIndicator.setStrokeCap(Paint.Cap.SQUARE);
        mPaintIndicator.setColor(DEFAULT_INDICATOR_COLOR);
        mPaintIndicator.setStrokeWidth(dp2px(1));


        //大刻度画笔
        mPaintLargeCalibration = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintLargeCalibration.setStrokeWidth(dp2px(DEFAULT_LARGE_CALIBRATION_WIDTH));
        mPaintLargeCalibration.setColor(DEFAULT_LARGE_CALIBRATION_COLOR);

        //小刻度画笔
        mPaintSmallCalibration = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintSmallCalibration.setStrokeWidth(dp2px(DEFAULT_SMALL_CALIBRATION_WIDTH));
        mPaintSmallCalibration.setColor(DEFAULT_SMALL_CALIBRATION_COLOR);

        //刻度文字画笔
        mPaintCalibrationText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintCalibrationText.setTextAlign(Paint.Align.CENTER);
        mPaintCalibrationText.setTextSize(sp2px(DEFAULT_CALIBRATION_TEXT_TEXT_SIZE));
        mPaintCalibrationText.setColor(DEFAULT_CALIBRATION_TEXT_TEXT_COLOR);

        //进度点的图片
        mProgressPointPosition = new float[2];
    }

    /**
     * 初始化圆环区域
     */
    @Override
    protected void initArcRect(float left, float top, float right, float bottom) {
        //外环区域
        mRectOuterArc = new RectF(left, top, right, bottom);
        initInnerRect();
    }

    /**
     * 初始化内部的区域
     */
    private void initInnerRect() {
        //内环位置
        mRectInnerArc = new RectF(mRectOuterArc.left + mArcSpacing, mRectOuterArc.top + mArcSpacing,
                mRectOuterArc.right - mArcSpacing, mRectOuterArc.bottom - mArcSpacing);

        //指标器的路径
        mIndicatorStart = mRectInnerArc.top + mArcSpacing / 2;
        mIndicatorPath = new Path();
        mIndicatorPath.moveTo(mRadius, mIndicatorStart + 40);
        mIndicatorPath.rLineTo(-dp2px(6), dp2px(40));
        mIndicatorPath.rLineTo(dp2px(12), 0);
        mIndicatorPath.close();

        //计算刻度位置
        mCalibrationStart = mRectOuterArc.top + mArcSpacing - mPaintInnerArc.getStrokeWidth() / 2;
        mCalibrationEnd = mCalibrationStart + mPaintInnerArc.getStrokeWidth();

        //刻度文字位置
        mCalibrationTextStart = mCalibrationEnd - dp2px(20);

    }

    /**
     * 绘制圆环
     */
    @Override
    protected void drawArc(Canvas canvas, float arcStartAngle, float arcSweepAngle) {
        //绘制圆环
        mPaintOuterArc.setColor(mOuterArcColor);
        canvas.drawArc(mRectOuterArc, arcStartAngle, arcSweepAngle, false, mPaintOuterArc);

        //绘制刻度
        drawCalibration(canvas, arcStartAngle);
    }

    /**
     * 绘制刻度
     */
    private void drawCalibration(Canvas canvas, float arcStartAngle) {
        if (mLargeCalibrationNumber == 0) {
            return;
        }
        //旋转画布
        canvas.save();
        canvas.rotate(arcStartAngle - 270, mRadius, mRadius);
        int mod = mLargeBetweenCalibrationNumber + 1;
        //遍历数量
        for (int i = 0; i < mCalibrationTotalNumber; i++) {
            //绘制刻度线
            if (i % mod == 0) {
                //绘制刻度文字
                int index = i / mod;
                if (mCalibrationNumberText != null && mCalibrationNumberText.length > index) {
                    canvas.drawText("• " + String.valueOf(mCalibrationNumberText[index]) + getUnitInfo(), mRadius, mCalibrationTextStart, mPaintCalibrationText);
                }
            }
            //旋转
            canvas.rotate(mSmallCalibrationBetweenAngle, mRadius, mRadius);
        }
        canvas.restore();
    }

    /**
     * 绘制进度圆环
     */
    @Override
    protected void drawProgressArc(Canvas canvas, float arcStartAngle, float progressSweepAngle) {
        //绘制进度点
        if (progressSweepAngle == 0) {
            return;
        }
        Path path = new Path();
        //添加进度圆环的区域
        path.addArc(mRectOuterArc, arcStartAngle, progressSweepAngle);
        //计算切线值和为重
        PathMeasure pathMeasure = new PathMeasure(path, false);
        pathMeasure.getPosTan(pathMeasure.getLength(), mProgressPointPosition, null);
        //绘制圆环
//        mPaintOuterArc.setColor(mProgressOuterArcColor);
        canvas.drawPath(path, progressPaint);
        //绘制进度点
        if (mProgressPointPosition[0] != 0 && mProgressPointPosition[1] != 0) {
            canvas.drawCircle(mProgressPointPosition[0], mProgressPointPosition[1], mProgressPointRadius, mPaintProgressPoint);
        }

        canvas.drawCircle(mRadius, mRadius, dp2px(42), mPaintInnerArc);


        //绘制指针
        canvas.save();
        canvas.rotate(arcStartAngle + progressSweepAngle - 270, mRadius, mRadius);
        mPaintIndicator.setStyle(Paint.Style.FILL);
        canvas.drawPath(mIndicatorPath, mPaintIndicator);
//        mPaintIndicator.setStyle(Paint.Style.STROKE);
//        canvas.drawCircle(mRadius, mIndicatorStart + dp2px(6) + 1, dp2px(2), mPaintIndicator);
        canvas.restore();

        canvas.drawArc(mRectOuterArc, arcStartAngle, progressSweepAngle, true, mSweepPaintOuterArc);
    }

    @Override
    protected void drawText(Canvas canvas, int value, String valueLevel, String currentTime) {
        //绘制数值
        float marginTop = mRadius + mTextSpacing;
        canvas.drawText(String.valueOf(value) + getUnitInfo(), mRadius, marginTop - 10, mPaintValue);

        //绘制日期
        if (!TextUtils.isEmpty(currentTime)) {
            marginTop = marginTop + getPaintHeight(mPaintDate, currentTime) + mTextSpacing;
            canvas.drawText(currentTime, mRadius, marginTop - 10, mPaintDate);
        }
    }

    /**
     * 设置圆环的距离
     */
    public void setArcSpacing(float dpSize) {
        mArcSpacing = dp2px(dpSize);

        initInnerRect();

        postInvalidate();
    }

    /**
     * 设置外环颜色
     */
    public void setOuterArcPaint(float dpSize, @ColorInt int color) {
        mPaintOuterArc.setStrokeWidth(dp2px(dpSize));
        mOuterArcColor = color;

        postInvalidate();
    }

    /**
     * 设置进度条的颜色
     */
    public void setProgressOuterArcColor(@ColorInt int color) {
        mProgressOuterArcColor = color;

        postInvalidate();
    }

    /**
     * 设置进度圆点的属性
     */
    public void setProgressPointPaint(float dpRadiusSize, @ColorInt int color) {
        mProgressPointRadius = dp2px(dpRadiusSize);
        mPaintProgressPoint.setColor(color);

        postInvalidate();
    }

    /**
     * 设置指示器属性
     */
    public void setIndicatorPaint(@ColorInt int color) {
        mPaintIndicator.setColor(color);

        postInvalidate();
    }
}
