package com.skyxiao.testdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by SkyXiao on 2017/3/15.
 */

public class StepLineChartView extends View {

    private Context mContext;
    private int dataMax;//最大值
    private int dataTarget;//目标值
    private int curStartX;//用于监控触屏，在处理重绘，以展示移动效果
    private int deltaX;

    private Paint topLinePaint;//画笔-顶部线
    private Paint limitPaint;//画笔-范围值
    private Paint targetPaint;//画笔-目标值虚线
    private Paint linePaint;//画笔-曲线
    private Paint textRightPaint;//画笔-提示字样:右对齐
    private Paint textTipsPaint;//画笔-图表中提示框
    private Paint lineTipsBgPaint;//画笔-图标框提示背景
    private Paint weekPaint;//画笔-底部日期字体

    private int screenWidth;//屏幕宽度||图表宽度
    private int spaceWidth;//数据间的间距宽度
    private int viewHeight;//图表的高度，不包括底部时间样式
    private String[] mWeekDates;//所显示日期的时间，暂时显示近7日
    private List<Integer> mFirstLineData;//第一条曲线的数据
    private List<Integer> mSecondLineData;//第二条曲线的数据
    private String mUnit = "";//显示的单位 步||次/分


    public StepLineChartView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public StepLineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        screenWidth = ScreenUtils.getScreenWidth();
        viewHeight = (int) dp2Px(347);

        widthMeasureSpec = MeasureSpec.makeMeasureSpec(ScreenUtils.getScreenWidth() * 2, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY);//315+32:图高度+底部字高度
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void init() {
        mUnit = " 步";
        dataMax = 16000;
        dataTarget = 8000;
        mFirstLineData = new ArrayList<>();
        mFirstLineData.add(2000);//第一个点为当天的点
        mFirstLineData.add(3000);
        mFirstLineData.add(8000);
        mFirstLineData.add(12000);
        mFirstLineData.add(5000);
        mFirstLineData.add(8000);
        mFirstLineData.add(400);
        calculateWeekDate();

        topLinePaint = new Paint();
        topLinePaint.setColor(mContext.getResources().getColor(R.color.white_a20));
        topLinePaint.setStrokeWidth(1F);

        limitPaint = new Paint();
        limitPaint.setColor(mContext.getResources().getColor(R.color.white_a60));
        limitPaint.setStrokeWidth(1F);

        linePaint = new Paint();
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(mContext.getResources().getColor(R.color.white));
        linePaint.setStrokeWidth(10F);

        targetPaint = new Paint();
        targetPaint.setStyle(Paint.Style.STROKE);
        targetPaint.setColor(mContext.getResources().getColor(R.color.white));
        targetPaint.setStrokeWidth(1F);

        textRightPaint = new Paint();
        textRightPaint.setColor(mContext.getResources().getColor(R.color.white));
        textRightPaint.setTextSize(ScreenUtils.sp2px(10F));
        textRightPaint.setTextAlign(Paint.Align.RIGHT);

        weekPaint = new Paint();
        weekPaint.setColor(mContext.getResources().getColor(R.color.grey_hint));
        weekPaint.setTextSize(ScreenUtils.sp2px(10F));
        weekPaint.setTextAlign(Paint.Align.CENTER);


        textTipsPaint = new Paint();
        textTipsPaint.setTextSize(ScreenUtils.sp2px(10F));
        textTipsPaint.setTextAlign(Paint.Align.CENTER);

        lineTipsBgPaint = new Paint();

    }

    public float dp2Px(float dp) {
        return ScreenUtils.dp2px(dp);
    }

    private void calculateWeekDate() {
        mWeekDates = new String[7];
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MM/dd");
        for (int i = 0; i < 7; i++) {
            cal.add(Calendar.DAY_OF_YEAR, -i);
            mWeekDates[i] = df.format(cal.getTime());
            cal.add(Calendar.DAY_OF_YEAR, +i);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        spaceWidth = screenWidth / 6;
        int centerX = screenWidth / 2;//图表中心位置 X 坐标
        float BottomY = dp2Px(315);//图表底部位置 Y 坐标

        float heightUnit = dp2Px(63F);
        float dp4 = dp2Px(4F);
        float lineMarginRight = dp2Px(30F);

        //相关线条及提示
        canvas.drawLine(0, 0, screenWidth, 0, topLinePaint);//顶部暗线
        canvas.drawLine(0, heightUnit, screenWidth - lineMarginRight, heightUnit, limitPaint);
        canvas.drawLine(0, heightUnit * 4, screenWidth - lineMarginRight, heightUnit * 4, limitPaint);
        canvas.drawText(dataMax + mUnit, screenWidth - lineMarginRight, heightUnit - dp4, textRightPaint);
        canvas.drawText(0 + mUnit, screenWidth - lineMarginRight, heightUnit * 4 - dp4, textRightPaint);

        //目标步数的虚线及提示
        Path path = new Path();
        float dashLineY = heightUnit * 4 - ((dataTarget / (float) dataMax) * (heightUnit * 3));
        path.moveTo(0, dashLineY);
        path.lineTo(screenWidth - lineMarginRight, dashLineY);
        PathEffect effects = new DashPathEffect(new float[]{dp4 * 2, dp4, dp4 * 2, dp4}, 1);
        targetPaint.setPathEffect(effects);
        canvas.drawPath(path, targetPaint);

        boolean isStep = null == mSecondLineData;
        canvas.drawText((isStep ? "目标" : "") + dataTarget + mUnit, screenWidth - lineMarginRight, dashLineY - dp4, textRightPaint);

        //指示当天的底部小箭头，固定不动
        Path bottomArrow = new Path();
        bottomArrow.moveTo(centerX - dp4, BottomY);// 此点为多边形的起点
        bottomArrow.lineTo(centerX, dp2Px(308));
        bottomArrow.lineTo(centerX + dp4, BottomY);
        bottomArrow.close(); // 使这些点构成封闭的多边形
        canvas.drawPath(bottomArrow, textRightPaint);

        //日期背景
        canvas.drawRect(0, BottomY, screenWidth, viewHeight, textRightPaint);

        float tempCenterX;
        for (int i = 0; i < mWeekDates.length; i++) {
            tempCenterX = centerX - (spaceWidth * i) + curStartX;
            if (tempCenterX + dp2Px(15) >= centerX && tempCenterX - dp2Px(15) <= centerX) {
                //当前选择的点及日期
                weekPaint.setColor(mContext.getResources().getColor(R.color.green_normal));
            } else {
                //默认的点和日期
                weekPaint.setColor(mContext.getResources().getColor(R.color.grey_hint));
            }
            canvas.drawText(mWeekDates[i], centerX - (spaceWidth * i) + curStartX, dp2Px(336), weekPaint);
        }

        //画第一条曲线
        if (null == mFirstLineData) return;
        Bitmap curDayBmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_table_day_choice);
        Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_table_day);
        drawLine(canvas, mFirstLineData, centerX, curDayBmp, bmp, false);

        //画第二条曲线
        if (null != mSecondLineData) {
            Bitmap secondDayBmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_table_night_choice);
            Bitmap secondBmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_table_night);
            drawLine(canvas, mSecondLineData, centerX, secondDayBmp, secondBmp, true);
        }

    }

    private void drawLine(Canvas canvas, List<Integer> dataList, int centerX, Bitmap curDayBmp, Bitmap bmp, boolean isHeart) {
        float dp4 = dp2Px(4);
        int dataSize = dataList.size();

        int itemStepPre = 0;
        int itemStep;
        int indexPre = -1;
        //画相关曲线
        Path pathLine;
        float prePointX, prePointY, tempPointX, curPointX, curPointY;
        for (int i = 0; i < dataSize; i++) {
            itemStep = dataList.get(i);
            if (itemStep <= 0) continue;
            if (0 == itemStepPre) {
                itemStepPre = itemStep;
                indexPre = i;
                continue;
            }
            //前一个点的位置
            prePointX = centerX - (spaceWidth * (indexPre)) + curStartX;
            prePointY = getDataPointY(itemStepPre);
            //当前一个点的位置
            curPointX = centerX - (spaceWidth * i) + curStartX;
            curPointY = getDataPointY(dataList.get(i));
            //临时的中间点的位置 X 坐标
            tempPointX = (prePointX + curPointX) / 2;
            if (itemStep > 0 && itemStepPre > 0) {
                //画出贝塞尔曲线
                pathLine = new Path();
                //设置Path的起点
                pathLine.moveTo(prePointX, prePointY);
                //设置贝塞尔曲线的控制点坐标和终点坐标
                pathLine.cubicTo(tempPointX - (spaceWidth / 4), prePointY, tempPointX + (spaceWidth / 4), curPointY, curPointX, curPointY);
                linePaint.setColor(mContext.getResources().getColor(isHeart ? R.color.green_normal : R.color.white));
                canvas.drawPath(pathLine, linePaint);
                itemStepPre = itemStep;
                indexPre = i;
            }

        }

        //画点
        float tempCenterX;
        float tempCenterY;
        Path dataArrow;
        textTipsPaint.setColor(mContext.getResources().getColor(isHeart ? R.color.white : R.color.green_normal));
        lineTipsBgPaint.setColor(mContext.getResources().getColor(isHeart ? R.color.green_normal : R.color.white));

        for (int i = 0; i < dataSize; i++) {
            itemStep = dataList.get(i);
            if (itemStep <= 0) continue;

            tempCenterX = centerX - (spaceWidth * i) + curStartX;
            tempCenterY = getDataPointY(itemStep);

            if (tempCenterX + dp2Px(15) >= centerX && tempCenterX - dp2Px(15) <= centerX) {
                //当前选择的点及日期

                canvas.drawBitmap(curDayBmp, tempCenterX - dp2Px(10), tempCenterY - dp2Px(10), textTipsPaint);
                //步数提示边框
                dataArrow = new Path();
                dataArrow.moveTo(tempCenterX, tempCenterY - dp2Px(10));// 此点为多边形的起点
                dataArrow.lineTo(tempCenterX - dp4, tempCenterY - dp2Px(18));
                dataArrow.lineTo(tempCenterX + dp4, tempCenterY - dp2Px(18));
                dataArrow.close(); // 使这些点构成封闭的多边形
                canvas.drawPath(dataArrow, lineTipsBgPaint);
                canvas.drawRect(tempCenterX - dp2Px(22), tempCenterY - dp2Px(36), tempCenterX + dp2Px(22), tempCenterY - dp2Px(18), lineTipsBgPaint);

                canvas.drawText(dataList.get(i) + mUnit, tempCenterX, tempCenterY - dp2Px(24), textTipsPaint);
            } else {
                //默认的点和日期
                canvas.drawBitmap(bmp, tempCenterX - dp2Px(10), tempCenterY - dp2Px(10), textTipsPaint);
            }
        }
    }

    private float getDataPointY(int stepData) {
        int weight = (int) ((stepData / (float) dataMax) * dp2Px(188));
        return dp2Px(250) - weight;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                deltaX = x - curStartX;
                break;
            case MotionEvent.ACTION_MOVE:
                curStartX = x - deltaX;
                //控制边界，只再两屏宽度内显示
                if (curStartX >= screenWidth) {
                    curStartX = screenWidth;
                } else if (curStartX <= 0) {
                    curStartX = 0;
                }
                postInvalidate();
                break;

            case MotionEvent.ACTION_UP:
                //记录（0，0）坐标位置，重新画图
                curStartX = ((int) Math.rint(curStartX / (float) spaceWidth)) * spaceWidth;
                postInvalidate();
                break;
        }
        return true;//处理了触摸消息，消息不再传递
    }


    public void initChart(int dataMax, int dataTarget, String unit, List<Integer> firstLineData, List<Integer> secondLineData) {
        this.mUnit = unit;
        this.dataMax = dataMax;
        this.dataTarget = dataTarget;
        if (null != firstLineData && 7 == firstLineData.size()) {
            this.mFirstLineData = firstLineData;
        }
        if (null != secondLineData && 7 == secondLineData.size()) {
            this.mSecondLineData = secondLineData;
        }
        postInvalidate();
    }

}