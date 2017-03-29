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

public class StepLineChartViewPre extends View {

    private Context mContext;
    private int stepMax;
    private int stepTarget;
    private int curStartX;
    private int deltaX;
    private Paint topLinePaint;
    private Paint limitPaint;
    private Paint targetPaint;
    private Paint linePaint;
    private Paint textPaint;
    private Paint textGreenPaint;
    private Paint weekPaint;
    private int screenWidth;
    private int spaceWidth;
    private int viewHeight;
    private String[] mWeekDates;
    private List<Integer> mStepData;


    public StepLineChartViewPre(Context context) {
        super(context);
        mContext = context;
        init();
    }


    public StepLineChartViewPre(Context context, AttributeSet attrs) {
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
        mStepData = new ArrayList<>();
        mStepData.add(5000);//第一个点为当天的点
        mStepData.add(11000);
        mStepData.add(6000);
        mStepData.add(12000);
        mStepData.add(2000);
        mStepData.add(8000);
        mStepData.add(4000);
        stepMax = 16000;
        stepTarget = 8000;

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

        textPaint = new Paint();
        textPaint.setColor(mContext.getResources().getColor(R.color.white));
        textPaint.setTextSize(ScreenUtils.sp2px(10F));
        textPaint.setTextAlign(Paint.Align.RIGHT);

        weekPaint = new Paint();
        weekPaint.setColor(mContext.getResources().getColor(R.color.grey_hint));
        weekPaint.setTextSize(ScreenUtils.sp2px(10F));
        weekPaint.setTextAlign(Paint.Align.CENTER);

        textGreenPaint = new Paint();
        textGreenPaint.setColor(mContext.getResources().getColor(R.color.green_normal));
        textGreenPaint.setTextSize(ScreenUtils.sp2px(10F));
        textGreenPaint.setTextAlign(Paint.Align.CENTER);

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

        float heightUnit = 63F;
        float dp4 = dp2Px(4F);
        float lineMarginRight = dp2Px(30F);

        //相关线条及提示
        canvas.drawLine(0, 0, screenWidth, 0, topLinePaint);
        canvas.drawLine(0, dp2Px(heightUnit), screenWidth - lineMarginRight, dp2Px(heightUnit), limitPaint);
        canvas.drawLine(0, dp2Px(heightUnit * 4), screenWidth - lineMarginRight, dp2Px(heightUnit * 4), limitPaint);
        canvas.drawText(stepMax + " 步", screenWidth - lineMarginRight, dp2Px(heightUnit) - dp4, textPaint);
        canvas.drawText(0 + " 步", screenWidth - lineMarginRight, dp2Px(heightUnit) * 4 - dp4, textPaint);

        //目标步数的虚线及提示
        Path path = new Path();
        path.moveTo(0, dp2Px(156));
        path.lineTo(screenWidth - lineMarginRight, dp2Px(156));
        PathEffect effects = new DashPathEffect(new float[]{dp4 * 2, dp4, dp4 * 2, dp4}, 1);
        targetPaint.setPathEffect(effects);
        canvas.drawPath(path, targetPaint);
        canvas.drawText("目标" + stepTarget + " 步", screenWidth - lineMarginRight, dp2Px(152), textPaint);

        //指示当天的底部小箭头，固定不动
        Path bottomArrow = new Path();
        bottomArrow.moveTo(centerX - dp4, BottomY);// 此点为多边形的起点
        bottomArrow.lineTo(centerX, dp2Px(308));
        bottomArrow.lineTo(centerX + dp4, BottomY);
        bottomArrow.close(); // 使这些点构成封闭的多边形
        canvas.drawPath(bottomArrow, textPaint);

        //日期背景
        canvas.drawRect(0, BottomY, screenWidth, viewHeight, textPaint);

        float tempCenterX;
        for (int i = 0; i < mWeekDates.length; i++) {
            tempCenterX = centerX - (spaceWidth * i) + curStartX;
            if (tempCenterX + dp2Px(15) >= centerX && tempCenterX - dp2Px(15) <= centerX) {
                //当前选择的点及日期
                canvas.drawText(mWeekDates[i], centerX - (spaceWidth * i) + curStartX, dp2Px(336), textGreenPaint);
            } else {
                //默认的点和日期
                canvas.drawText(mWeekDates[i], centerX - (spaceWidth * i) + curStartX, dp2Px(336), weekPaint);
            }
        }

        if (mStepData == null || mStepData.size() == 0) return;

        Bitmap curDayBmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_table_day_choice);
        Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_table_day);

        drawLine(canvas, mStepData, centerX, curDayBmp, bmp);

    }

    private void drawLine(Canvas canvas, List<Integer> dataList, int centerX, Bitmap curDayBmp, Bitmap bmp) {
        float dp4 = dp2Px(4);
        float tempCenterX;
        float tempCenterY;

        int dataSize = dataList.size();
        //画相关曲线
        Path pathLine;
        float prePointX, prePointY, tempPointX, curPointX, curPointY;
        for (int i = 1; i < dataSize; i++) {
            //前一个点的位置
            prePointX = centerX - (spaceWidth * (i - 1)) + curStartX;
            prePointY = getDataPointY(dataList.get(i - 1));

            curPointX = centerX - (spaceWidth * i) + curStartX;
            curPointY = getDataPointY(dataList.get(i));

            //临时的中间点的位置 X 坐标
            tempPointX = (prePointX + curPointX) / 2;

            //画出贝塞尔曲线
            pathLine = new Path();
            //设置Path的起点
            pathLine.moveTo(prePointX, prePointY);
            //设置贝塞尔曲线的控制点坐标和终点坐标
            pathLine.cubicTo(tempPointX - (spaceWidth / 4), prePointY, tempPointX + (spaceWidth / 4), curPointY, curPointX, curPointY);
            canvas.drawPath(pathLine, linePaint);

        }

        Path dataArrow;
        for (int i = 0; i < dataSize; i++) {
            tempCenterX = centerX - (spaceWidth * i) + curStartX;
            tempCenterY = getDataPointY(dataList.get(i));

            if (tempCenterX + dp2Px(15) >= centerX && tempCenterX - dp2Px(15) <= centerX) {
                //当前选择的点及日期
                canvas.drawBitmap(curDayBmp, tempCenterX - dp2Px(10), tempCenterY - dp2Px(10), textPaint);

                //                Path curDataTips = new Path();
                //
                //                curDataTips.moveTo(tempCenterX, tempCenterY-dp2Px(10));// 此点为多边形的起点
                //                curDataTips.lineTo(tempCenterX-dp4, tempCenterY-dp2Px(18));
                //                curDataTips.lineTo(tempCenterX - dp2Px(24), tempCenterY-dp2Px(18));
                //                curDataTips.lineTo(tempCenterX - dp2Px(24), tempCenterY-dp2Px(38));
                //                curDataTips.lineTo(tempCenterX + dp2Px(24), tempCenterY-dp2Px(38));
                //                curDataTips.lineTo(tempCenterX + dp2Px(24), tempCenterY-dp2Px(18));
                //                curDataTips.lineTo(tempCenterX+dp4, tempCenterY-dp2Px(18));
                //                curDataTips.lineTo(tempCenterX, tempCenterY-dp2Px(10));
                //                curDataTips.close(); // 使这些点构成封闭的多边形
                //                canvas.drawPath(curDataTips, textPaint);

                //步数提示边框
                dataArrow = new Path();
                dataArrow.moveTo(tempCenterX, tempCenterY - dp2Px(10));// 此点为多边形的起点
                dataArrow.lineTo(tempCenterX - dp4, tempCenterY - dp2Px(18));
                dataArrow.lineTo(tempCenterX + dp4, tempCenterY - dp2Px(18));
                dataArrow.close(); // 使这些点构成封闭的多边形
                canvas.drawPath(dataArrow, textPaint);
                canvas.drawRect(tempCenterX - dp2Px(22), tempCenterY - dp2Px(36), tempCenterX + dp2Px(22), tempCenterY - dp2Px(18), textPaint);

                canvas.drawText(dataList.get(i) + " 步", tempCenterX, tempCenterY - dp2Px(24), textGreenPaint);
            } else {
                //默认的点和日期
                canvas.drawBitmap(bmp, tempCenterX - dp2Px(10), tempCenterY - dp2Px(10), textPaint);
            }
        }
    }

    private float getDataPointY(int stepData) {
        int weight = (int) ((stepData / (float) stepMax) * dp2Px(188));
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

}
