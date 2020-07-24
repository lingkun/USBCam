package com.icatch.usbcam.engine.control;

import android.util.Log;

import com.icatch.usbcam.sdkapi.CameraAction;
import com.icatch.usbcam.sdkapi.CameraProperties;

import org.reactivestreams.Subscriber;

import java.util.Timer;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by zengping on 2017/5/27.
 */

public class CameraControl {
    private final String TAG = CameraControl.class.getSimpleName();
    private CameraAction cameraAction;
//    private CameraProperties properties;
    private boolean isRecording = false;
    private Timer recordTimer;
//    private RecordObserver recordObserver = null;
    private int recordSecs;
    private int usbSdCradStatus = 1;
    private boolean isAudioMute = true;

    public boolean isAudioMute() {
        return isAudioMute;
    }

    public void setAudioMute(boolean audioMute) {
        isAudioMute = audioMute;
    }

    public CameraControl(CameraAction cameraAction) {
        this.cameraAction = cameraAction;
//        this.properties = properties;
    }

    public boolean isRecording() {
        Log.d(TAG, "isRecording: " + isRecording);
        return isRecording;
    }

//    public boolean capturePhoto(Observer<Boolean> observer){
//        Log.i(TAG, "begin capturePhoto");
//        boolean ret = false;
//        Observable observable1 =Observable.create(new ObservableOnSubscribe<Boolean>() {
//            @Override
//            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
//                boolean ret = cameraAction.capturePhoto();
//                emitter.onNext(ret);
//                emitter.onComplete();
//            }
//        });
//        observable1.subscribe(observer);
//
//
//
//        return ret;
//    }

    public boolean formatStorage() {
        Log.i(TAG, "start formatStorage");
        boolean ret = false;
        Log.i(TAG, "end formatStorage ret = " + ret);
        return ret;
    }

    public boolean factoryReset() {
        Log.i(TAG, "start factoryReset");
        boolean ret = false;
        Log.i(TAG, "end factoryReset ret = " + ret);
        return ret;
    }

    public void setVolumeUp(){
        boolean temp;
        if(isAudioMute){
            temp = cameraAction.setAudioUnMute();
        }else {
            temp = cameraAction.setAudioMute();
        }

        if(temp){
            isAudioMute = !isAudioMute;
//            SharedPreferencesUtil.put(a,SharedPreferencesUtil.CONFIG_FILE,"audioMute" ,isAudioMute);
        }
//        if(isAudioMute){
//            previewView.setVolumeImvRes(R.drawable.ic_volume_off_white_24dp);
//        }else{
//            previewView.setVolumeImvRes(R.drawable.ic_volume_up_white_24dp);
//        }
    }
}
