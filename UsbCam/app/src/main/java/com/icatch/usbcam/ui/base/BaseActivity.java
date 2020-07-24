package com.icatch.usbcam.ui.base;

import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.icatch.usbcam.R;
import com.icatch.usbcam.app.mycamera.CameraManager;
import com.icatch.usbcam.app.mycamera.MyCamera;
import com.icatch.usbcam.common.usb.UsbEvent;
import com.icatch.usbcam.engine.streaming.CameraStreaming;
import com.icatch.usbcam.utils.HomeKeyEvent;
import com.icatchtek.basecomponent.activitymanager.MActivityManager;
import com.icatchtek.basecomponent.prompt.AppDialog;
import com.icatchtek.baseutil.log.AppLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * @author b.jiang
 */
public class BaseActivity extends AppCompatActivity {
    private String TAG = BaseActivity.class.getSimpleName();
    private boolean isAttach =true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MActivityManager.getInstance().pushActivity(this);
//        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.background), true);
    }


    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MActivityManager.getInstance().setCurActivity(this);


    }

    @Override
    protected void onPause() {
        super.onPause();
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMain(UsbEvent usbEvent){
        UsbEvent.EventType eventType = usbEvent.getEventType();
        UsbDevice usbDevice  =usbEvent.getDevice();
        AppLog.d(TAG,"onEventMain eventType:" +eventType);
        switch (eventType){
            case USB_ATTACH:
                isAttach = true;
                break;
            case USB_DETTACH:
                isAttach = false;
                AppDialog.showDialogQuit(this, R.string.text_device_disconnected);
                break;
            case USB_CONNECT:

                break;
            case USB_DISCONNECT:
                break;
            case USB_CANCEL:

                break;
                default:
                    break;

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMain(HomeKeyEvent homeKeyEvent){
        HomeKeyEvent.EventType eventType = homeKeyEvent.getEventType();
        AppLog.d(TAG,"onEventMain eventType:" +eventType);
        switch (eventType){
            default:
                break;

        }
    }

    public boolean isAttach() {
        return isAttach;
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Activity退至后台时，关闭显示的Dialog
        EventBus.getDefault().unregister(this);
        AppDialog.dismissDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MActivityManager.getInstance().popActivity(this);
    }
}
