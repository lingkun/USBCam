package com.icatch.usbcam.app;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.icatch.usbcam.common.usb.USBMonitor;
import com.icatch.usbcam.data.appinfo.ConfigureInfo;
import com.icatch.usbcam.log.SdkLog;
import com.icatch.usbcam.utils.CrashHandler;
import com.icatch.usbcam.utils.HomeReceiverUtil;
import com.icatch.usbcam.utils.NetWorkUtils;
import com.icatchtek.baseutil.log.AppLog;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * @author b.jiang
 * @date 2018/10/22
 * @description
 */
public class UsbApp extends MultiDexApplication {
    private static final String TAG = UsbApp.class.getSimpleName();
    private static Context instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = getApplicationContext();
        AppLog.d(TAG, "cpu type is " + android.os.Build.CPU_ABI);
        if (android.os.Build.CPU_ABI.contains("armeabi") == false) {
            return;
        }
//        SdkLog.getInstance().enableSDKLog();
//        FileOper.createDirectory(Environment.getExternalStorageDirectory().toString() + AppInfo.DOWNLOAD_PATH_PHOTO);
//        FileOper.createDirectory(Environment.getExternalStorageDirectory().toString() + AppInfo.DOWNLOAD_PATH_VIDEO);
        if (NetWorkUtils.isNetworkConnected(this)) {
            CrashReport.initCrashReport(getApplicationContext(), "e6f9ce498a", true);
        } else {
            CrashHandler.getInstance().init(this);
        }
//        CrashReport.initCrashReport(getApplicationContext(), "e6f9ce498a", true);
//        initLeakCanary();
//        USBMonitor.getInstance().register();
        HomeReceiverUtil.getInstance().registerHomeKeyReceiver(getApplicationContext());
        AppLog.enableAppLog();
        SdkLog.getInstance().enableSDKLog();
        ConfigureInfo.getInstance().initCfgInfo(getApplicationContext());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        USBMonitor.getInstance().unregister();
        HomeReceiverUtil.getInstance().unregisterHomeKeyReceiver(getApplicationContext());
    }

    public static Context getContext() {
        return instance;
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        instance = getApplicationContext();
    }

//
//    private void initLeakCanary(){
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        LeakCanary.install(this);
//    }
}
