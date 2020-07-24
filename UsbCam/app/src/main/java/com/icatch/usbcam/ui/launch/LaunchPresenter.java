package com.icatch.usbcam.ui.launch;

import android.app.Activity;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import android.widget.Toast;

import com.icatch.usbcam.R;
import com.icatch.usbcam.app.mycamera.CameraManager;
import com.icatch.usbcam.app.mycamera.CameraType;
import com.icatch.usbcam.app.mycamera.MyCamera;
import com.icatch.usbcam.common.usb.USBMonitor;
import com.icatch.usbcam.data.appinfo.ConfigureInfo;
import com.icatch.usbcam.log.SdkLog;
import com.icatch.usbcam.ui.base.BasePresenter;
import com.icatch.usbcam.ui.preview.PreviewActivity;
import com.icatchtek.basecomponent.prompt.AppDialog;
import com.icatchtek.basecomponent.prompt.MyProgressDialog;
import com.icatchtek.basecomponent.prompt.MyToast;
import com.icatchtek.baseutil.log.AppLog;
import com.icatchtek.reliant.core.jni.JUsbTransportLogger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yh.zhang C001012 on 2015/11/12 14:51.
 */
public class LaunchPresenter extends BasePresenter {
    private static final String TAG = LaunchPresenter.class.getSimpleName();
    private ILaunchView launchView;
    private final LaunchHandler launchHandler = new LaunchHandler();

    private Activity activity;
//    private USBMonitor mUSBMonitor;
    private UsbDevice usbDevice;

    public LaunchPresenter(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    public void setView(ILaunchView launchView) {
        this.launchView = launchView;
        initCfg();
    }

    @Override
    public void initCfg() {
//        AppLog.enableAppLog();
        // never sleep when run this activity
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//         do not display menu bar
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        AppLog.enableAppLog();
//        SdkLog.getInstance().enableSDKLog();
//        ConfigureInfo.getInstance().initCfgInfo(activity.getApplicationContext());
//        GlobalInfo.getInstance().startScreenListener();
    }

    private List<UsbDevice> getUsbDevicesListPrivate()
    {
        UsbManager usbManager  = (UsbManager)activity.getSystemService(Context.USB_SERVICE);
        if (usbManager == null) {
            return null;
        }

        final HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();

        List<UsbDevice> usbDevices = new LinkedList<>();
        if (deviceList != null)
        {
            JUsbTransportLogger.writeLog("usb_scsi", "B1-------------------");
            final Iterator<UsbDevice> iterator = deviceList.values().iterator();
            UsbDevice device;
            while (iterator.hasNext()) {
                device = iterator.next();
                JUsbTransportLogger.writeLog("usb_scsi", "get exists devices: " + device);
                usbDevices.add(device);
            }
            JUsbTransportLogger.writeLog("usb_scsi", "B2-------------------");
            return usbDevices;
        }
        return null;
    }

    private class LaunchHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

            }
        }
    }


    public synchronized void beginConnectUSBCamera() {
        AppLog.i(TAG, "beginConnectUSBCamera  usbDevice="+ usbDevice);
        USBMonitor usbMonitor = USBMonitor.getInstance();
        final UsbDevice usbDevice = usbMonitor.getDevice();
        if(usbDevice == null){
            AppDialog.showDialogQuit(activity, R.string.text_device_not_connect);
            return;
        }
        if(usbMonitor.checkUsbPermission(usbDevice)){
            MyProgressDialog.showProgressDialog(activity,R.string.text_connecting_camera);
            Observable<Boolean> observable = Observable.create(new ObservableOnSubscribe<Boolean>() {
                @Override
                public void subscribe(ObservableEmitter<Boolean> e) {
                    CameraManager.getInstance().createUSBCamera(CameraType.USB_CAMERA, usbDevice, 0);
                    MyCamera currentCamera = CameraManager.getInstance().getCurCamera();
                    if(!currentCamera.isConnected()){
                        currentCamera.connect(activity,false);
                    }
                    e.onNext(currentCamera.isConnected());
                }
            });
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableObserver<Boolean>() {
                        @Override
                        public void onNext(Boolean value) {
                            MyProgressDialog.closeProgressDialog();
                            if(value){
                                redirectToAnotherActivity(activity, PreviewActivity.class);
                                activity.finish();
                            }else {
                                MyToast.show(activity,R.string.text_operation_failed);
                            }
                        }

                        @Override
                        public void onError(Throwable throwable) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }else {
            usbMonitor.requestPermission(usbDevice);
        }
    }


    public void usbAttach(){
        AppLog.d(TAG, "USBMonitor onAttach:");
        Toast.makeText(activity, "USB_DEVICE_ATTACHED", Toast.LENGTH_SHORT).show();
    }

    public void usbConnect(UsbDevice device){
        AppLog.d(TAG, "USBMonitor onConnect device=" + device);
//        beginConnectUSBCamera();
    }

    public void usbDettach(){
        AppLog.d(TAG, "USB_DEVICE_DETACHED:");
        Toast.makeText(activity, "USB_DEVICE_DETACHED", Toast.LENGTH_SHORT).show();
    }

    public void usbCancel(){
//        AppDialog.showDialogQuit(activity, R.string.text_usb_permissions_denied);
//        Toast.makeText(activity, "USB_DEVICE_DETACHED", Toast.LENGTH_SHORT).show();
    }
}
