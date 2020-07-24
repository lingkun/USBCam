package com.icatchtek.baseutil;

import android.content.Context;
import android.os.Build;
import android.os.StatFs;
import android.provider.Settings;
import android.util.Log;

import com.icatchtek.baseutil.log.AppLog;

import java.io.File;

public class SystemInfo {
    private static final String TAG = SystemInfo.class.getName();

    public static String getDeviceUniqueLabel(Context context){
        String uniqueLabel = "";
        //將Android id作爲唯一標示
        uniqueLabel = getAndroidId(context);
        return uniqueLabel;
    }

    private static String getAndroidId(Context context){
        String androidId;
        androidId = (String) SharedPreferencesUtil.get(context,SharedPreferencesUtil.CONFIG_FILE,"AndroidId","");
        if(androidId != null && !androidId.equals("")){
            AppLog.d(TAG,"11 getAndroidId androidId=" + androidId);
            return androidId;
        }
        androidId = Settings.Secure.getString(context.getContentResolver(),Settings.Secure.ANDROID_ID);
        SharedPreferencesUtil.put(context,SharedPreferencesUtil.CONFIG_FILE,"AndroidId",androidId);

        AppLog.d(TAG,"22 getAndroidId androidId=" + androidId);
        return androidId;
    }

}
