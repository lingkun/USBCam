package com.icatch.usbcam.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.icatchtek.baseutil.log.AppLog;

/**
 * @author b.jiang
 * @date 2019/2/1
 * @description
 */
public class NetWorkUtils {
    private static String TAG = NetWorkUtils.class.getSimpleName();
    /**
     * 判断是否有网络连接
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        boolean isConnected = false;
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null && mNetworkInfo.isAvailable() && mNetworkInfo.isConnected()) {
                isConnected = true;
            }
        }

        AppLog.i(TAG, "isNetworkConnected=" + isConnected);
        return isConnected;
    }
}
