package com.icatch.usbcam.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

/**
 * @author b.jiang
 * @date 2019/6/28
 * @description
 */
public class HomeReceiverUtil {
    private static String TAG = HomeReceiverUtil.class.getSimpleName();
    final static String SYSTEM_DIALOG_REASON_KEY = "reason";

    final static String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";

    final static String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
    private static BroadcastReceiver mHomeReceiver = null;

    private static HomeReceiverUtil instance;
    public static synchronized HomeReceiverUtil getInstance() {
        if (instance == null) {
            instance = new HomeReceiverUtil();
        }
        return instance;
    }

    private HomeReceiverUtil(){

    }
    /**
     * 添加home的广播
     * @param context
     */
    public  void registerHomeKeyReceiver(Context context) {
        Log.d(TAG,"注册home的广播");
        mHomeReceiver = new InnerRecevier();
        //动态注册广播
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        //启动广播
        context.registerReceiver(mHomeReceiver, intentFilter);
        final IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
    }

    /**
     * 注销home的广播
     * @param context
     */
    public  void unregisterHomeKeyReceiver(Context context) {
        Log.d(TAG, "销毁home的广播");
        if (null != mHomeReceiver) {
            context.unregisterReceiver(mHomeReceiver);
            mHomeReceiver = null ;
            Log.d(TAG,"已经注销了，不能再注销了");
        }
    }

    private static class InnerRecevier extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (reason != null) {
                    if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
//                        Toast.makeText(USBMultiPbActivity.this, "Home键被监听", Toast.LENGTH_SHORT).show();
                        EventBus.getDefault().post(new HomeKeyEvent(HomeKeyEvent.EventType.REASON_HOME_KEY));
                    } else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                        EventBus.getDefault().post(new HomeKeyEvent(HomeKeyEvent.EventType.REASON_RECENT_APPS));
//                        Toast.makeText(USBMultiPbActivity.this, "多任务键被监听", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
}
