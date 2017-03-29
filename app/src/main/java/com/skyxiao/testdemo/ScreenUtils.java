package com.skyxiao.testdemo;

import android.content.Context;
import android.view.WindowManager;

/**
 * Created by SkyXiao on 2017/3/15.
 */

public class ScreenUtils {
    private static int     screenHeight;    // 高度
    private static int     screenWidth;    // 宽度
    private static float   density;    // 宽度
    private static Context mContext;

    private ScreenUtils() {

    }

    public static int getScreenHeight() {
        if (mContext == null) {
            throw new RuntimeException("You need to be init.In Application.onCreate()");
        }
        if (screenHeight == 0) {
            screenHeight = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight();
        }
        return screenHeight;
    }

    public static int getScreenWidth() {
        if (mContext == null) {
            throw new RuntimeException("You need to be init.In Application.onCreate()");
        }
        if (screenWidth == 0) {
            screenWidth = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
        }
        return screenWidth;
    }

    /**
     * dp转换为px
     */
    public static int dp2px( float dp) {
        if (density == 0) {
            density = mContext.getResources().getDisplayMetrics().density;
        }
        return (int)  (dp * density + 0.5f);
    }

    public static void init(Context context) {
        mContext = context;
    }

    public static float sp2px(float v) {
        return v * mContext.getResources().getDisplayMetrics().scaledDensity;
    }
}

