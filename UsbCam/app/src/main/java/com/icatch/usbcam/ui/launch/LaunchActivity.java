package com.icatch.usbcam.ui.launch;

import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.Handler;

import com.icatch.usbcam.R;
import com.icatch.usbcam.common.usb.UsbEvent;
import com.icatch.usbcam.ui.base.BaseActivity;
import com.icatch.usbcam.ui.preview.PreviewActivity;
import com.icatchtek.baseutil.log.AppLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author b.jiang
 */
public class LaunchActivity extends BaseActivity implements ILaunchView{

    private String TAG = LaunchActivity.class.getSimpleName();
    private LaunchPresenter presenter;
    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        presenter = new LaunchPresenter(LaunchActivity.this);
        presenter.setView(this);
//        presenter.initUsbMonitor();
    }


    @Override
    protected void onStart() {
        super.onStart();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                presenter.redirectToAnotherActivity(LaunchActivity.this, PreviewActivity.class);
                finish();
//                presenter.beginConnectUSBCamera();
            }
        },500);
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//
//            }
//        },500);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        presenter.unregisterUSB();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppLog.d(TAG,"onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void setLaunchLayoutVisibility(int visibility) {

    }

    @Override
    public void onEventMain(UsbEvent usbEvent) {
        UsbEvent.EventType eventType = usbEvent.getEventType();
        UsbDevice usbDevice  =usbEvent.getDevice();
        AppLog.d(TAG,"onEventMain eventType:" +eventType);
        switch (eventType){
            case USB_ATTACH:
                presenter.usbAttach();
                break;
            case USB_DETTACH:
                presenter.usbDettach();
                break;
            case USB_CONNECT:
                presenter.usbConnect(usbDevice);
                break;
            case USB_DISCONNECT:
                break;
            case USB_CANCEL:
                presenter.usbCancel();
                break;
            default:
                break;

        }
    }
}
