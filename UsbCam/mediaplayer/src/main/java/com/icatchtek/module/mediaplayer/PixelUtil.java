package com.icatchtek.module.mediaplayer;


import android.content.Context;
import android.content.res.Resources;

public class PixelUtil {

    private static Context mContext;

    public static void initContext(Context context) {
        mContext = context;
    }

    public static int dp2px(float value) {
        final float scale = mContext.getResources().getDisplayMetrics().densityDpi;

        return (int) (value * (scale / 160) + 0.5f);
    }

    public static int dp2px(float value, Context context) {
        final float scale = mContext.getResources().getDisplayMetrics().densityDpi;

        return (int) (value * (scale / 160) + 0.5f);
    }

    public static int px2dp(float value) {
        final float scale = mContext.getResources().getDisplayMetrics().densityDpi;
        return (int) ((value * 160) / scale + 0.5f);
    }

    public static int px2dp(float value, Context context) {
        final float scale = context.getResources().getDisplayMetrics().densityDpi;
        return (int) ((value * 160) / scale + 0.5f);
    }

    public static int sp2px(float value) {
        Resources resources;
        if (mContext == null) {
            resources = Resources.getSystem();
        } else {
            resources = mContext.getResources();
        }
        float spvalue = value * resources.getDisplayMetrics().scaledDensity;
        return (int) (spvalue + 0.5f);
    }

    public static int sp2px(float value, Context context) {
        Resources resources;
        if (mContext == null) {
            resources = Resources.getSystem();
        } else {
            resources = context.getResources();
        }
        float spvalue = value * resources.getDisplayMetrics().scaledDensity;
        return (int) (spvalue + 0.5f);
    }

    public static int px2sp(float value) {
        final float scale = mContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (value / scale + 0.5f);
    }

    public static int px2sp(float value, Context context) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (value / scale + 0.5f);
    }
}