package com.icatch.usbcam.engine.event;

import android.os.Handler;
import android.os.Looper;

import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.icatch.usbcam.bean.CameraStatusInfo;
import com.icatch.usbcam.sdkapi.CameraProperties;
import com.icatch.usbcam.sdkapi.UsbScsiCommand;
import com.icatchtek.baseutil.ThreadPoolUtils;
import com.icatchtek.baseutil.log.AppLog;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author b.jiang
 * @date 2018/11/5
 * @description
 */
public class EventPollManager {
    private static String TAG = EventPollManager.class.getSimpleName();
    private Timer sdCardStatusTimer;
    private UsbScsiCommand usbScsiCommand;

    private OnSdCardStatusChangeListener listener;
    private OnTriggerStatusChangeListener triggerStatusChangeListener;
    private CameraStatusInfo curStatusInfo;
    private CameraProperties cameraProperties;
    private Handler handler;
    private ScheduledFuture future;

    public EventPollManager(UsbScsiCommand usbScsiCommand, CameraProperties cameraProperties){
        this.usbScsiCommand = usbScsiCommand;
        this.cameraProperties = cameraProperties;
        handler = new Handler(Looper.getMainLooper());
    }

    public void resetClient(UsbScsiCommand usbScsiCommand, CameraProperties cameraProperties){
        this.usbScsiCommand = usbScsiCommand;
        this.cameraProperties = cameraProperties;
    }


    public void startPolling(){
        stopPolling();
        sdCardStatusTimer = new Timer();
        sdCardStatusTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                final CameraStatusInfo cameraStatusInfo = usbScsiCommand.getCameraStatus();
                if(cameraStatusInfo != null){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(curStatusInfo == null){
                                if(listener != null){

                                    if(!cameraStatusInfo.isSDCardExist()){
                                        listener.onSdStatus(cameraStatusInfo.isSDCardExist());
                                    }else {
                                        if(cameraStatusInfo.isSdError()) {
                                            listener.onSdCardError(cameraStatusInfo.getErrorInfo());
                                        }else if(cameraStatusInfo.isSdInsertError()){
                                            listener.onSdInsertError();
                                        }
                                    }
                                    listener.onRecStatusChange(cameraStatusInfo.isVideoRec());

                                }
                                if(triggerStatusChangeListener != null){
                                    triggerStatusChangeListener.onTriggerStatusChange(cameraStatusInfo.getEventTriggerStatus());
                                }
                            }else {
                                if(listener != null){
                                    if(curStatusInfo.isSDCardExist() != cameraStatusInfo.isSDCardExist()){
                                        if(cameraStatusInfo.isSdInsertError()) {
                                            listener.onSdInsertError();
                                        }else {
                                            listener.onSdStatus(cameraStatusInfo.isSDCardExist());
                                        }
                                    }
                                    if(curStatusInfo.getErrorInfo() != cameraStatusInfo.getErrorInfo()){
                                        if(cameraStatusInfo.isSdError()){
                                            listener.onSdCardError(cameraStatusInfo.getErrorInfo());
                                        }
                                    }
                                    if(curStatusInfo.isVideoRec() != cameraStatusInfo.isVideoRec() ){
                                        listener.onRecStatusChange(cameraStatusInfo.isVideoRec());
                                    }
                                }

                                if(curStatusInfo != null && curStatusInfo.getEventTriggerStatus() != cameraStatusInfo.getEventTriggerStatus()){
                                    if(triggerStatusChangeListener != null) {
                                        triggerStatusChangeListener.onTriggerStatusChange(cameraStatusInfo.getEventTriggerStatus());
                                    }
                                }
                            }
                            curStatusInfo = cameraStatusInfo;
                        }
                    });
                }

            }
        }, 100, 200);
    }

    public CameraStatusInfo getCurStatusInfo() {
        if(curStatusInfo == null){
            AppLog.d(TAG,"getCurStatusInfo curStatusInfo is null");
            curStatusInfo =  usbScsiCommand.getCameraStatus();
        }
        AppLog.d(TAG,"getCurStatusInfo curStatusInfo:" + curStatusInfo);
        return curStatusInfo;
    }

    public void startPollingForPb(final UsbMassStorageDevice msdcDevice){
        if(msdcDevice == null){
            return;
        }
        stopPolling();
        sdCardStatusTimer = new Timer();
        sdCardStatusTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                final CameraStatusInfo cameraStatusInfo = usbScsiCommand.getCameraStatusForPb(msdcDevice);
                if(cameraStatusInfo != null){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(curStatusInfo == null){
                                if(listener != null){
                                    listener.onSdStatus(cameraStatusInfo.isSDCardExist());
                                }
                                if(listener != null){
                                    listener.onSdCardError(cameraStatusInfo.getErrorInfo());
                                }
                            }else {
                                if(listener != null){
                                    if(curStatusInfo.isSDCardExist() != cameraStatusInfo.isSDCardExist()){
                                        listener.onSdStatus(cameraStatusInfo.isSDCardExist());
                                    }
                                }
                                if(listener != null){
                                    if(curStatusInfo.getErrorInfo() != cameraStatusInfo.getErrorInfo()){
                                        listener.onSdCardError(cameraStatusInfo.getErrorInfo());
                                    }
                                }
                            }
                            curStatusInfo = cameraStatusInfo;
                        }
                    });
                }
            }
        }, 500, 200);
    }

    public synchronized void stopPolling() {
        curStatusInfo = null;
        if (sdCardStatusTimer != null) {
            sdCardStatusTimer.cancel();
            sdCardStatusTimer = null;
        }
    }

    private void startPolling2(Runnable command){
        stopPolling2();
        future = ThreadPoolUtils.getInstance().scheduleWithFixedRate(command, 100, 200, TimeUnit.MILLISECONDS);
    }

    private void stopPolling2(){
        if(future != null && !future.isCancelled()){
            future.cancel(true);
            future = null;
        }
    }

    public interface OnSdCardStatusChangeListener {
         void onSdCardError(int errorCode);
         void onSdStatus(boolean isExist);
         void onRecStatusChange(boolean isRec);
         void onSdInsertError();
    }

    public interface OnTriggerStatusChangeListener {
        void onTriggerStatusChange(int status);
    }

    public synchronized void setSdCardStatusChange(OnSdCardStatusChangeListener listener) {
        this.listener = listener;
    }

    public synchronized void setTriggerStatusChangeListener(OnTriggerStatusChangeListener listener){
        triggerStatusChangeListener = listener;
    }

    public synchronized void removeListener(){
        listener = null;
        triggerStatusChangeListener = null;
    }

}
