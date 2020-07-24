package com.icatch.usbcam.ui.preview;

import android.app.Activity;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;

import com.icatch.usbcam.R;
import com.icatch.usbcam.app.mycamera.CameraManager;
import com.icatch.usbcam.app.mycamera.CameraType;
import com.icatch.usbcam.app.mycamera.MyCamera;
import com.icatch.usbcam.app.mycamera.PanoramaSession;
import com.icatch.usbcam.bean.CameraStatusInfo;
import com.icatch.usbcam.common.mode.PreviewStopPvMode;
import com.icatch.usbcam.common.type.AppErrorCode;
import com.icatch.usbcam.common.type.FwUpdateStatus;
import com.icatch.usbcam.common.type.UsbSdCardStatus;
import com.icatch.usbcam.common.usb.USBMonitor;
import com.icatch.usbcam.data.appinfo.AppInfo;
import com.icatch.usbcam.data.propertyid.PropertyId;
import com.icatch.usbcam.engine.control.CameraControl;
import com.icatch.usbcam.engine.event.EventPollManager;
import com.icatch.usbcam.engine.setting.SettingItem;
import com.icatch.usbcam.engine.streaming.CameraStreaming;
import com.icatch.usbcam.engine.streaming.RenderType;
import com.icatch.usbcam.sdkapi.CameraAction;
import com.icatch.usbcam.sdkapi.CameraProperties;
import com.icatch.usbcam.sdkapi.PanoramaPreviewPlayback;
import com.icatch.usbcam.sdkapi.SettingManager;
import com.icatch.usbcam.sdkapi.UsbScsiCommand;
import com.icatch.usbcam.ui.base.BasePresenter;
import com.icatch.usbcam.ui.localpb.activity.LocalMultiPbActivity;
import com.icatch.usbcam.ui.setting.SettingListAdapter;
import com.icatch.usbcam.ui.usbpb.activity.USBMultiPbActivity;
import com.icatchtek.basecomponent.prompt.AppDialog;
import com.icatchtek.basecomponent.prompt.MyProgressDialog;
import com.icatchtek.basecomponent.prompt.MyToast;
import com.icatchtek.baseutil.log.AppLog;
import com.icatchtek.control.customer.type.ICatchCamEventID;
import com.icatchtek.control.customer.type.ICatchCamVideoRecordStatus;
import com.icatchtek.pancam.customer.ICatchIPancamListener;
import com.icatchtek.pancam.customer.ICatchPancamConfig;
import com.icatchtek.pancam.customer.surface.ICatchSurfaceContext;
import com.icatchtek.pancam.customer.type.ICatchGLEvent;
import com.icatchtek.pancam.customer.type.ICatchGLEventID;
import com.icatchtek.reliant.customer.type.ICatchCodec;
import com.icatchtek.reliant.customer.type.ICatchH264StreamParam;
import com.icatchtek.reliant.customer.type.ICatchJPEGStreamParam;
import com.icatchtek.reliant.customer.type.ICatchStreamParam;
import com.icatchtek.reliant.customer.type.ICatchVideoFormat;

import java.lang.ref.WeakReference;
import java.util.LinkedList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * @author b.jiang
 * @date 2018/10/23
 * @description
 */
public class PreviewPresenter extends BasePresenter {
    private static final String TAG = "PreviewPresenter";
    IPreviewView previewView;
    private Activity activity;
    private USBMonitor mUSBMonitor;
    private MyCamera curCamera;
    private CameraStreaming cameraStreaming;
    private PanoramaPreviewPlayback panoramaPreviewPlayback;
    private SettingManager settingManager;
    private CameraAction cameraAction;
    private MediaPlayer videoCaptureStartBeep;
    private MediaPlayer modeSwitchBeep;
    private MediaPlayer stillCaptureStartBeep;
    private PreviewHandler previewHandler;
    private int usbSdCradStatus = 1;
    private boolean audioMute = false;
    private CameraControl cameraControl;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    boolean isShowActionBar = true;
    boolean isLock = false;
    //    private SDKEvent sdkEvent;
    private CameraProperties cameraProperties;
    private LinkedList<SettingItem> settingMenuList;
    private SettingListAdapter settingListAdapter;
    private EventPollManager eventPollManager;
    public static final int REQUEST_MEDIA = 112;
    private UsbFrameTransferFailedListener usbFrameTransferFailedListener;
    private boolean isAttach = true;
    private SurfaceHolder surfaceHolder;
    private ICatchSurfaceContext surfaceContext;
    private boolean eventTrigger = false;


    public PreviewPresenter(PreviewActivity activity) {
        super(activity);
        this.activity = activity;
        previewHandler = new PreviewHandler(this);
    }

    public void setView(IPreviewView previewView) {
        this.previewView = previewView;
        initCfg();
        initData();
    }

    public void initData() {
        isAttach = true;
        curCamera = CameraManager.getInstance().getCurCamera();
        if (curCamera == null || !curCamera.isConnected()) {
            return;
        }
        panoramaPreviewPlayback = curCamera.getPanoramaPreviewPlayback();
        eventPollManager = curCamera.getEventPollManager();
        settingManager = curCamera.getSettingManager();
        cameraProperties = curCamera.getCameraProperties();
        ICatchPancamConfig.getInstance().setPreviewCacheParam(0, 200);
        cameraAction = curCamera.getCameraAction();
        cameraStreaming = new CameraStreaming(panoramaPreviewPlayback);
        cameraControl = new CameraControl(cameraAction);
        videoCaptureStartBeep = MediaPlayer.create(activity, R.raw.camera_timer);
        stillCaptureStartBeep = MediaPlayer.create(activity, R.raw.captureshutter);
        modeSwitchBeep = MediaPlayer.create(activity, R.raw.focusbeep);
    }

    public synchronized boolean initSurface(SurfaceHolder surfaceHolder) {
        AppLog.i(TAG, "begin initSurface");
        if(curCamera== null || !curCamera.isConnected()){
            return false;
        }
        if (cameraStreaming == null) {
            return false;
        }

        if(cameraStreaming.isStreaming()){
            AppLog.e(TAG, "begin initSurface cameraStreaming isStreaming is true");
            return false;
        }
        int width = previewView.getSurfaceViewWidth();
        int heigth = previewView.getSurfaceViewHeight();

        AppLog.i(TAG, "SurfaceViewWidth=" + width + " SurfaceViewHeight=" + heigth);
        if (width <= 0 || heigth <= 0) {
            width = 1920;
            heigth = 1080;
        }
        ICatchPancamConfig.getInstance().setSoftwareDecoder(AppInfo.enableSoftwareDecoder);
        RenderType renderType;
        if (AppInfo.enableSdkRender) {
            renderType = RenderType.COMMON_RENDER;
        } else {
            renderType = RenderType.APP_RENDER;
        }
        if(this.surfaceContext == null){
            this.surfaceContext = new ICatchSurfaceContext(surfaceHolder.getSurface());
        }
        boolean ret = cameraStreaming.initSurface(surfaceContext,surfaceHolder, width, heigth, renderType);
        this.surfaceHolder = surfaceHolder;
        AppLog.i(TAG, "end initSurface ret:" + ret);
        return ret;
    }

    public synchronized void startPreview(final boolean needStartRec) {
        AppLog.d(TAG, "start startPreview needStartRec=" + needStartRec);
        if (curCamera == null || !curCamera.isConnected()) {
            AppLog.d(TAG, "camera is disconnect");
            return;
        }

        previewView.setActionBtnEnable(false);
        MyProgressDialog.showProgressDialog(activity, R.string.start_streaming);
        final Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) {
                ICatchStreamParam iCatchStreamParam = getStreamParam();
                int retValue = 0;
                if (!cameraStreaming.isStreaming()) {
                    retValue = cameraStreaming.start(iCatchStreamParam, !AppInfo.disableAudio);
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                if (needStartRec) {
                    startMovieRecord();
                }
                e.onNext(retValue);
                e.onComplete();
            }
        });
        DisposableObserver<Integer> disposableObserver = new DisposableObserver<Integer>() {

            @Override
            public void onNext(Integer value) {
                MyProgressDialog.closeProgressDialog();
                if (value != 0) {
                    MyToast.show(activity, R.string.dialog_start_pv_failed);
                }
                previewView.setActionBtnEnable(true);
                initState();
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError=" + e);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        };
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver);
    }

    public synchronized boolean stopPreview(int stopPvMode) {
        if (cameraStreaming != null && cameraStreaming.isStreaming()) {
            return cameraStreaming.stop(stopPvMode);
        }
        return true;
    }

    public void setDrawingArea(int width, int height) {
        if (cameraStreaming != null) {
            cameraStreaming.setDrawingArea(width, height);
        }
    }

    public void destroyCamera() {
        AppLog.d(TAG, "destroyCamera");
        if (curCamera != null && curCamera.isConnected()) {
            curCamera.disconnect();
        }
    }

    public void removeHandlerMessage() {
//        if (previewHandler != null) {
//            previewHandler.removeCallbacksAndMessages(null);
//        }
    }

    public void resetSurfaceContext(){
        surfaceContext = null;
    }

    public synchronized void destroyPreview() {
        AppLog.d(TAG, "destroyCamera");
        if (cameraStreaming != null) {
            cameraStreaming.destroyPreview();
        }
    }


    public void exitSetting() {
        AppLog.d(TAG, "exitSetting ");
        if (previewView.getSettingMenuListVisibility() == View.VISIBLE) {
            setPropertys();
        }
    }

    public void back() {
        AppLog.d(TAG, "back ");
        if (previewView.getSettingMenuListVisibility() == View.VISIBLE) {
            setPropertys();
        } else {
            stopPreview(PreviewStopPvMode.STOP_PV_TO_HOME);
            destroyCamera();
            activity.finish();
            System.exit(0);
        }
    }

    private void setPropertys() {
        previewView.setSettingMenuVisibility(View.GONE);
        final SettingManager settingManager = curCamera.getSettingManager();
        if (settingListAdapter != null && settingManager != null) {
            final LinkedList<SettingItem> changedItems = settingListAdapter.getChangedList();
            if (changedItems != null && changedItems.size() > 0) {
                MyProgressDialog.showProgressDialog(activity, R.string.action_processing);
                final boolean needReStartPv = needRestartPv(changedItems);
                Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> e) {
                        if (needReStartPv) {
                            stopPreview(PreviewStopPvMode.STOP_PV_TO_SWITCH_PV_SIZE);
                        }
                        settingManager.setPropertys(changedItems);
                        e.onNext(AppErrorCode.SUCCESS);
                    }
                });
                observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new DisposableObserver<Integer>() {
                            @Override
                            public void onNext(Integer value) {
                                MyProgressDialog.closeProgressDialog();
                                if (needReStartPv) {
                                    startPreview(false);
                                }
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                MyProgressDialog.closeProgressDialog();
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            }
        }
    }

    private boolean needRestartRec(LinkedList<SettingItem> changedItems) {
        boolean ret = true;
        if (changedItems == null || changedItems.size() < 1) {
            ret = false;
        }
        //如果只设置 碰撞灵敏度   就不用 Stop/Start rec
        if (changedItems.size() == 1
                && changedItems.get(0).getPropertyId() == PropertyId.CARSH_SENSITIVITY) {
            ret = false;
        }
        //如果只设置 显示分辨率   就不用 Stop/Start rec
        if (changedItems.size() == 1 && changedItems.get(0).getPropertyId() == PropertyId.VIDEO_SIZE) {
            ret = false;
        }
        AppLog.d(TAG, "needRestartRec : " + ret);
        return ret;
    }

    private boolean needRestartPv(LinkedList<SettingItem> changedItems) {
        if (changedItems == null || changedItems.size() <= 0) {
            return false;
        }
        boolean ret = false;
        //如果只设置 录影分辨率、录像画质、锁定录影时长、无缝录影、格式化 ，只做 Rec stop/start
        //如果设置中包含 显示分辨率 ，需要重新开流
        for (SettingItem item : changedItems) {
            if (item.getPropertyId() == PropertyId.VIDEO_SIZE) {
                ret = true;
                break;
            }
        }
        AppLog.d(TAG, "needRestartPv : " + ret);
        return ret;
    }

    private ICatchStreamParam getStreamParam() {
//        ICatchStreamParam defaultStreamParam = new ICatchH264StreamParam(1920, 1080, 30);
        ICatchStreamParam defaultStreamParam = new ICatchH264StreamParam(960, 540, 30);
        settingManager = curCamera.getSettingManager();
        if (settingManager == null || settingManager.getUsbPvSize() == null) {
            return defaultStreamParam;
        }
        ICatchVideoFormat videoFormat = settingManager.getUsbPvSize().getCurVideoFormat();
        AppLog.d(TAG, "getStreamParam videoFormat= " + videoFormat);
        if (videoFormat == null) {
            return defaultStreamParam;
        }
        if (videoFormat.getCodec() == ICatchCodec.ICH_CODEC_H264) {
            return new ICatchH264StreamParam(videoFormat.getVideoW(), videoFormat.getVideoH(), videoFormat.getFrameRate(), videoFormat.getBitrate());
        } else if (videoFormat.getCodec() == ICatchCodec.ICH_CODEC_JPEG) {
            return new ICatchJPEGStreamParam(videoFormat.getVideoW(), videoFormat.getVideoH(), videoFormat.getFrameRate(), videoFormat.getBitrate());
        } else {
            return new ICatchH264StreamParam(videoFormat.getVideoW(), videoFormat.getVideoH(), videoFormat.getFrameRate(), videoFormat.getBitrate());
        }
    }

    public void triggerRecord() {
        if (curCamera== null || !curCamera.isConnected()) {
            MyToast.show(activity, R.string.text_device_not_connect);
            return;
        }
        final UsbScsiCommand usbScsiCommand = curCamera.getUsbScsiCommand();
        if (usbScsiCommand == null) {
            return;
        }
        previewView.setRecordLayoutEnable(false);
        stillCaptureStartBeep.start();
        final Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) {
//                CameraStatusInfo cameraStatusInfo = usbScsiCommand.getCameraStatus();
                CameraStatusInfo cameraStatusInfo = eventPollManager.getCurStatusInfo();
                int retValue = -1;
                boolean ret = false;
                if (cameraStatusInfo == null) {
                    retValue = AppErrorCode.OPERATION_BUSY;
                } else if (cameraStatusInfo.getEventTriggerStatus() != 0) {
                    retValue = AppErrorCode.TRIGGER_RECCORDING;
                } else if (!cameraStatusInfo.isSDCardExist()) {
                    retValue = AppErrorCode.NO_SD_CARD;
                } else {
                    ret = cameraAction.setEventTrigger();
                    if (ret) {
                        eventTrigger = true;
                        retValue = AppErrorCode.SUCCESS;
                    } else {
                        retValue = AppErrorCode.FAILED;
                    }
                }
                e.onNext(retValue);
                e.onComplete();
            }
        });
        DisposableObserver<Integer> disposableObserver = new DisposableObserver<Integer>() {

            @Override
            public void onNext(Integer value) {
                AppLog.d(TAG, "triggerRecord retValue:" + value);
                MyProgressDialog.closeProgressDialog();
                if (AppErrorCode.SUCCESS == value) {
                    MyToast.show(activity, R.string.text_trigger_video);
                } else {
                    MyToast.show(activity, AppErrorCode.getResId(value));
                    previewView.setRecordLayoutEnable(true);
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError=" + e);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        };
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver);
        MyProgressDialog.showProgressDialog(activity, R.string.action_processing);
        mCompositeDisposable.add(disposableObserver);
    }

    public void volumeUpOrOff() {
        if (curCamera== null || !curCamera.isConnected()) {
            MyToast.show(activity, R.string.text_device_not_connect);
            return;
        }
        cameraControl.setVolumeUp();
        if (cameraControl.isAudioMute()) {
            previewView.setVolumeImvRes(R.drawable.record_close_volume_vertical_);
            MyToast.show(activity, R.string.text_turn_off_the_recording_sound);
        } else {
            previewView.setVolumeImvRes(R.drawable.record_open_volume_vertical_);
            MyToast.show(activity, R.string.text_turn_on_recording_sound);
        }
    }

    public void capture() {
        AppLog.d(TAG, "startPhotoCapture");
        if (curCamera== null  || !curCamera.isConnected()) {
            MyToast.show(activity, R.string.text_device_not_connect);
            return;
        }
        if (curCamera.getUsbScsiCommand() == null) {
            return;
        }
        previewView.setPivCaptureBtnEnability(false);
        stillCaptureStartBeep.start();
        final Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) {
                boolean retValue = false;
                int errorValue = -1;
                CameraStatusInfo cameraStatusInfo = eventPollManager.getCurStatusInfo();
                if (cameraStatusInfo == null) {
                    errorValue = AppErrorCode.OPERATION_BUSY;
                } else if (!cameraStatusInfo.isSDCardExist()) {
                    errorValue = AppErrorCode.NO_SD_CARD;
                } else {
                    retValue = cameraAction.capturePhoto();
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    if (retValue) {
                        errorValue = AppErrorCode.SUCCESS;
                    } else {
                        errorValue = AppErrorCode.FAILED;
                    }
                }
                e.onNext(errorValue);
                e.onComplete();
            }
        });
        DisposableObserver<Integer> disposableObserver = new DisposableObserver<Integer>() {

            @Override
            public void onNext(Integer value) {
                MyProgressDialog.closeProgressDialog();
                previewView.setPivCaptureBtnEnability(true);
                if (value == AppErrorCode.SUCCESS) {
                    MyToast.show(activity, R.string.text_capture_completed);
                } else if (value == AppErrorCode.FAILED) {
                    MyToast.show(activity, R.string.text_capture_busy);
                } else {
                    MyToast.show(activity, AppErrorCode.getResId(value));
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError=" + e);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        };
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver);
        MyProgressDialog.showProgressDialog(activity, R.string.action_processing);
        mCompositeDisposable.add(disposableObserver);
    }

    public void redirectToLocalPb() {
        AppLog.i(TAG, "localBtn is clicked ");
        previewView.setActionBtnEnable(false);
        final Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) {
                stopPreview(PreviewStopPvMode.STOP_PV_TO_LOCAL);
                removeEventListener();
                e.onNext(AppErrorCode.SUCCESS);
                e.onComplete();
            }
        });
        DisposableObserver<Integer> disposableObserver = new DisposableObserver<Integer>() {

            @Override
            public void onNext(Integer value) {
                AppLog.d(TAG, "onNext");
                previewView.setActionBtnEnable(true);
                MyProgressDialog.closeProgressDialog();
                if (value == AppErrorCode.SUCCESS) {
                    Intent intent = new Intent();
                    intent.putExtra("CUR_POSITION", 0);
                    intent.setClass(activity, LocalMultiPbActivity.class);
                    activity.startActivity(intent);
                } else {
                    MyToast.show(activity, AppErrorCode.getResId(value));
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError=" + e);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        };
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver);
        mCompositeDisposable.add(disposableObserver);
        MyProgressDialog.showProgressDialog(activity, R.string.switching);
    }

    public void redirectToRemotePb() {
        AppLog.i(TAG, "remotePb is clicked ");
        if (curCamera == null || !curCamera.isConnected()) {
            MyToast.show(activity, R.string.text_device_not_connect);
            return;
        }
        if (curCamera.getUsbScsiCommand() == null) {
            return;
        }
        previewView.setActionBtnEnable(false);
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) {
                int retValue = -1;
                final CameraStatusInfo cameraStatusInfo = eventPollManager.getCurStatusInfo();
                if (cameraStatusInfo == null) {
                    retValue = AppErrorCode.OPERATION_BUSY;
                } else if (!cameraStatusInfo.isSDCardExist()) {
                    retValue = AppErrorCode.NO_SD_CARD;

                } else {
//                    stopMovieRecord2(cameraStatusInfo);
                    stopPreview(PreviewStopPvMode.STOP_PV_TO_PB);
                    int ret = curCamera.switchToPlayback(activity);
                    if (ret >= 0) {
                        curCamera.disconnect();
                        retValue = AppErrorCode.SUCCESS;
                    } else {
                        retValue = AppErrorCode.SWITCH_PB_MODE_FAILED;
                    }
                }
                e.onNext(retValue);
                e.onComplete();
            }
        });
        DisposableObserver<Integer> disposableObserver = new DisposableObserver<Integer>() {

            @Override
            public void onNext(Integer value) {
                AppLog.d(TAG, "onNext");
                MyProgressDialog.closeProgressDialog();
                if (value == AppErrorCode.SUCCESS) {
//                    previewView.setRecStatusImvVisibility(View.GONE);
                    removeEventListener();
                    removeHandlerMessage();
                    Intent intent = new Intent();
                    intent.putExtra("CUR_POSITION", 0);
                    intent.setClass(activity, USBMultiPbActivity.class);
                    activity.startActivity(intent);
                } else {
                    previewView.setActionBtnEnable(true);
                    MyToast.show(activity, AppErrorCode.getResId(value));
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError=" + e);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        };
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver);
        mCompositeDisposable.add(disposableObserver);
        MyProgressDialog.showProgressDialog(activity, R.string.switching);
    }

    public void redirectToSetting() {
        AppLog.i(TAG, "Setting is clicked ");
        if (curCamera== null  || !curCamera.isConnected()) {
            MyToast.show(activity, R.string.text_device_not_connect);
            return;
        }
//        final Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
//            @Override
//            public void subscribe(ObservableEmitter<Integer> e) {
//                SettingManager settingManager = curCamera.getSettingManager();
//                if (settingManager != null) {
//                    LinkedList<SettingItem> tempList = settingManager.getUsbSettingList();
//                    if(settingMenuList == null){
//                        settingMenuList = new LinkedList<>();
//                        settingMenuList.addAll(tempList);
//                    }else {
//                        settingMenuList.clear();
//                        settingMenuList.addAll(tempList);
//
//                    }
//                }
//                e.onNext(0);
//                e.onComplete();
//            }
//        });
//        DisposableObserver<Integer> disposableObserver = new DisposableObserver<Integer>() {
//
//            @Override
//            public void onNext(Integer value) {
//                AppLog.d(TAG, "onNext");
//                MyProgressDialog.closeProgressDialog();
//                if(settingMenuList != null){
//                    previewView.setSettingMenuListEnable(true);
//                    previewView.setSettingMenuVisibility(View.VISIBLE);
//                    settingListAdapter= new SettingListAdapter(activity,settingMenuList);
//                    previewView.setSettingMenuListAdapter(settingListAdapter);
//                }
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Log.d(TAG, "onError=" + e);
//            }
//
//            @Override
//            public void onComplete() {
//                Log.d(TAG, "onComplete");
//            }
//        };
//        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver);
//        mCompositeDisposable.add(disposableObserver);
//        MyProgressDialog.showProgressDialog(activity, R.string.action_processing);
        SettingManager settingManager = curCamera.getSettingManager();
        if (settingManager != null) {
            LinkedList<SettingItem> tempList = settingManager.getSettingMenuList();
            if (tempList == null) {
                return;
            }
            if (settingMenuList == null) {
                settingMenuList = new LinkedList<>();
                settingMenuList.addAll(tempList);
            } else {
                settingMenuList.clear();
                settingMenuList.addAll(tempList);

            }
        }
        if (settingMenuList != null) {
            previewView.setSettingMenuListEnable(true);
            previewView.setSettingMenuVisibility(View.VISIBLE);
            settingListAdapter = new SettingListAdapter(activity, settingMenuList);
            previewView.setSettingMenuListAdapter(settingListAdapter);
        }

    }

    public void lockOrUnlock() {
        isLock = !isLock;
        if (isLock) {
            previewView.setLockImvRes(R.drawable.ic_lock);
            previewView.setActionBarVisibility(View.VISIBLE);
            previewView.setStatusBarVisibility(View.VISIBLE);
            previewView.setLockImvVisibility(View.VISIBLE);
        } else {
            previewView.setLockImvRes(R.drawable.ic_unlock);
        }
    }

    public void showOrHideBar() {
        if (isLock) {
            return;
        }
        isShowActionBar = !isShowActionBar;
        if (isShowActionBar) {
            previewView.setActionBarVisibility(View.VISIBLE);
            previewView.setStatusBarVisibility(View.VISIBLE);
            previewView.setLockImvVisibility(View.VISIBLE);
        } else {
            previewView.setActionBarVisibility(View.GONE);
            previewView.setStatusBarVisibility(View.GONE);
            previewView.setLockImvVisibility(View.GONE);
        }
    }

    public void initState() {
        if (!curCamera.isConnected()) {
            return;
        }
        settingManager = CameraManager.getInstance().getCurCamera().getSettingManager();
        String pvSize = settingManager.getUsbPvSize().getCurSizeStringInPreview();
        if (pvSize == null || pvSize.isEmpty()) {
            pvSize = "1080P";
        }
        previewView.setVideoSizeInfo(pvSize);
    }

    public void initAudioStatus() {
        if (curCamera == null|| !curCamera.isConnected()) {
            return;
        }
        boolean isAudioMute = false;
        if (curCamera.getUsbScsiCommand() != null) {
            isAudioMute = curCamera.getUsbScsiCommand().isAudioMute();
        }
        if (cameraControl != null) {
            cameraControl.setAudioMute(isAudioMute);
        }
        if (isAudioMute) {
            previewView.setVolumeImvRes(R.drawable.record_close_volume_vertical_);
        } else {
            previewView.setVolumeImvRes(R.drawable.record_open_volume_vertical_);
        }
    }

    private boolean isVideoRecord() {
        ICatchCamVideoRecordStatus status = cameraProperties.getVideoRecordStatus();
//        0x01: 表示 Rec
//        0x10: 表示 PV
//        0x11: 表示 PV & Rec 都有启动
        boolean ret = false;
        if (status != null) {
            if (status.getCardStatus() == 1 || status.getCardStatus() == 3) {
                ret = true;
            } else {
                ret = false;
            }
        } else {
            ret = true;
        }
        AppLog.d(TAG, "isVideoRecord :" + ret);
        return ret;
    }

    private int getStreamStatus() {
        ICatchCamVideoRecordStatus status = cameraProperties.getVideoRecordStatus();
//        0x01: 表示 Rec
//        0x10: 表示 PV
//        0x11: 表示 PV & Rec 都有启动
        if (status != null) {
            AppLog.d(TAG, "getStreamStatus :" + status.getCardStatus());
            return status.getCardStatus();
        } else {
            return -1;
        }
    }

    public void showSettingDialog(int position) {
        SettingManager.OnOperationComplete operationComplete = new SettingManager.OnOperationComplete() {
            @Override
            public void before(int resId) {
                //格式化 ，只做 Rec stop/start
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (previewView.getSettingMenuListVisibility() == View.VISIBLE) {
                            previewView.setSettingMenuVisibility(View.GONE);
                        }
                    }
                });
//                previewView.setBackBtnEnable(false);
                if (resId == R.string.setting_format) {
                    stopMovieRecord();
                } else {
                    stopMovieRecord();
//                    stopPreview();
                }
            }

            @Override
            public void complete(int resId) {
//                if (previewView.getSettingMenuListVisibility() == View.VISIBLE) {
//                    previewView.setSettingMenuVisibility(View.GONE);
//                }
//                previewView.setBackBtnEnable(true);
                initAudioStatus();
                //格式化 ，只做 Rec stop/start
                if (resId == R.string.setting_format) {
                    startMovieRecord();
                } else {
//                    startPreview(true);
                }
            }

            @Override
            public void cancel() {
                previewView.setSettingMenuListEnable(true);
            }
        };
        if (settingMenuList != null) {
            SettingItem settingItem = settingMenuList.get(position);
            if (settingItem.getTitleResId() == R.string.setting_format) {
                CameraStatusInfo cameraStatusInfo = eventPollManager.getCurStatusInfo();
                if (cameraStatusInfo != null && cameraStatusInfo.isSDCardExist()) {
                    previewView.setSettingMenuListEnable(false);
                    curCamera.getSettingManager().showFormatConfirmDialog(activity, operationComplete, settingItem);
                } else {
                    MyToast.show(activity, R.string.text_sd_card_not_exist);
                }
            } else if (settingItem.getTitleResId() == R.string.setting_reset) {
                previewView.setSettingMenuListEnable(false);
                curCamera.getSettingManager().showResetDialog(activity, operationComplete, settingItem);
            } else if (settingItem.getTitleResId() == R.string.setting_update_time) {
                previewView.setSettingMenuListEnable(false);
                curCamera.getSettingManager().setCameraTime(activity, operationComplete, settingItem);
            }else if (settingItem.getTitleResId() == R.string.setting_check_firmware_update) {
                if( curCamera.getUsbScsiCommand() != null && curCamera.getUsbScsiCommand().checkFwUpdate()){
                    curCamera.getSettingManager().showUpdateFwDialog(activity, operationComplete, settingItem);
                }else {
                    previewView.setSettingMenuListEnable(true);
                    MyToast.show(activity,"当前已是最新版本");
                }
            }
        }
    }

    public void homeClick() {
        stopPreview(PreviewStopPvMode.STOP_PV_TO_HOME);
        destroyPreview();
        destroyCamera();
        activity.finish();
        System.exit(0);
    }

    private static class PreviewHandler extends Handler {
        WeakReference<PreviewPresenter> weakReference;

        public PreviewHandler(PreviewPresenter presenter) {
            weakReference = new WeakReference<>(presenter);
        }

        @Override
        public void handleMessage(Message msg) {
            PreviewPresenter presenter = weakReference.get();
            if (presenter == null) {
                return;
            }
            switch (msg.what) {
                case ICatchCamEventID.ICH_CAM_EVENT_SD_CARD_OUT:
                    presenter.usbSdCradStatus = UsbSdCardStatus.SD_CARD_OUT;
                    presenter.previewView.setSdStatusImvRes(msg.what);
                    break;
                case ICatchCamEventID.ICH_CAM_EVENT_SD_CARD_IN:
                    presenter.usbSdCradStatus = UsbSdCardStatus.SD_CARD_IN;
                    MyToast.show(presenter.activity, R.string.dialog_card_inserted);
                    presenter.previewView.setSdStatusImvVisibility(View.GONE);
                    break;
                case ICatchCamEventID.ICH_CAM_EVENT_SD_CARD_LOCKED:
                    presenter.usbSdCradStatus = UsbSdCardStatus.SD_CARD_ERR_CARD_LOCKED;
                    MyToast.show(presenter.activity, "SD card locked");
                    break;
                case ICatchCamEventID.ICH_CAM_EVENT_SD_CARD_ERR:
                    presenter.usbSdCradStatus = UsbSdCardStatus.SD_CARD_ERR_CARD_ERROR;
                    MyToast.show(presenter.activity, R.string.dialog_card_error);
                    presenter.previewView.setSdStatusImvRes(msg.what);
                    break;
                case ICatchCamEventID.ICH_CAM_EVENT_SD_CARD_MEMORY_FULL:
                    presenter.usbSdCradStatus = UsbSdCardStatus.SD_CARD_ERR_MEMORY_FULL;
                    MyToast.show(presenter.activity, R.string.dialog_card_full);
                    presenter.previewView.setSdStatusImvRes(msg.what);
                    break;
                case ICatchCamEventID.ICH_CAM_EVENT_INSUFFICIENT_DISK_SPACE:
                    presenter.usbSdCradStatus = UsbSdCardStatus.SD_CARD_ERR_INSUFFICIENT_DISK_SPACE;
                    MyToast.show(presenter.activity, "Insufficient disk space");
                    presenter.previewView.setSdStatusImvRes(msg.what);
                    break;
                case ICatchCamEventID.ICH_CAM_EVENT_SD_CARD_SPEED_TOO_SLOW:
                    presenter.usbSdCradStatus = UsbSdCardStatus.SD_CARD_ERR_DISK_SPEED_TOO_LOW;
                    MyToast.show(presenter.activity, "Card speed tool slow");
                    presenter.previewView.setSdStatusImvRes(msg.what);
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    public void addEventListener() {
//        sdkEvent = new SDKEvent(previewHandler);
//        sdkEvent.addUsbSdCardEventListener();
        if (curCamera == null || !curCamera.isConnected()) {
            return;
        }
        usbFrameTransferFailedListener = new UsbFrameTransferFailedListener();
        PanoramaSession panoramaSession = curCamera.getPanoramaSession();
        if (panoramaSession != null && panoramaSession.getSession() != null) {
            try {
                panoramaSession.getSession().getControl().addEventListener(ICatchGLEventID.ICH_GL_EVENT_USB_FRAME_TRANSFER_FAILED, usbFrameTransferFailedListener);
            } catch (Exception e) {
                AppLog.d(TAG, "addEventListener ICH_GL_EVENT_USB_FRAME_TRANSFER_FAILED e:" + e.getMessage());
                e.printStackTrace();
            }
        }

        eventPollManager = curCamera.getEventPollManager();
        if (eventPollManager == null) {
            return;
        }
        eventPollManager.setSdCardStatusChange(new EventPollManager.OnSdCardStatusChangeListener() {
            @Override
            public void onSdCardError(int errorCode) {
                if (errorCode != UsbSdCardStatus.SD_CARD_ERR_NO_ERROR && errorCode != UsbSdCardStatus.SD_CARD_ERR_NULL) {
                    MyToast.show(activity, UsbSdCardStatus.getCardStatueInfo(activity, errorCode));
                    previewView.setSdStatusImvRes(errorCode);
                }
            }

            @Override
            public void onSdStatus(boolean isExist) {
                if (isExist) {
                    MyToast.show(activity, R.string.dialog_card_inserted);
                    previewView.setSdStatusImvRes(UsbSdCardStatus.SD_CARD_STATUS_IN);
                } else {
                    MyToast.show(activity, R.string.dialog_card_removed);
                    previewView.setSdStatusImvRes(UsbSdCardStatus.SD_CARD_STATUS_OUT);
                }

            }

            @Override
            public void onRecStatusChange(boolean isRec) {
                AppLog.i(TAG, "onRecStatusChange isRec:" + isRec);
                if (isRec) {
                    previewView.setRecStatusImvVisibility(View.VISIBLE);
                } else {
                    previewView.setRecStatusImvVisibility(View.GONE);
                }
            }

            @Override
            public void onSdInsertError() {
                MyToast.show(activity, R.string.sd_insert_error_info);
            }
        });
        eventPollManager.setTriggerStatusChangeListener(new EventPollManager.OnTriggerStatusChangeListener() {
            @Override
            public void onTriggerStatusChange(int status) {
                if (status == 0) {
                    if (eventTrigger) {
                        eventTrigger = false;
                        previewView.setRecordLayoutEnable(true);
                        MyToast.show(activity, R.string.text_complete_trigger_video);
                    }
                } else {
                    eventTrigger = true;
                    previewView.setRecordLayoutEnable(false);
                }
            }
        });
        eventPollManager.startPolling();
    }

    class UsbFrameTransferFailedListener implements ICatchIPancamListener {
        @Override
        public void eventNotify(ICatchGLEvent iCatchGLEvent) {
            AppLog.d(TAG, "app_usb_transfer failed isAttach:" + isAttach);
            if (!isAttach) {
                return;
            }
            previewHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (isAttach) {
                        stopPreview(PreviewStopPvMode.STOP_PV_ONLY);
                    }
//                    destroyCamera();
//                    curCamera.reconnect(activity, false);
                    if (isAttach) {
                        startPreview(false);
                    }
                }
            });
        }
    }

    public void removeEventListener() {
        if(curCamera == null || !curCamera.isConnected()){
            return;
        }
        PanoramaSession panoramaSession = curCamera.getPanoramaSession();
        if (usbFrameTransferFailedListener != null && panoramaSession != null && panoramaSession.getSession() != null) {
            try {
                panoramaSession.getSession().getControl().removeEventListener(ICatchGLEventID.ICH_GL_EVENT_USB_FRAME_TRANSFER_FAILED, usbFrameTransferFailedListener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (eventPollManager == null) {
            return;
        }
        eventPollManager.removeListener();
        eventPollManager.stopPolling();

    }

    public void registerUSB() {
//        if (mUSBMonitor != null) {
//            mUSBMonitor.register();
//        }
    }

    public void unregisterUSB() {
//        if (mUSBMonitor != null) {
//            mUSBMonitor.unregister();
//            mUSBMonitor = null;
//        }
    }

    public void initUsbMonitor() {
//        mUSBMonitor = new USBMonitor(activity, mOnDeviceConnectListener);
    }

    public boolean startMovieRecord() {
//        int ret = -1;
//        UsbScsiCommand usbScsiCommand = curCamera.getUsbScsiCommand();
//        cameraAction = curCamera.getCameraAction();
//        if (usbScsiCommand == null ) {
//            AppLog.e(TAG, "startMovieRecord sd card error!");
//            return false;
//        }
//        CameraStatusInfo cameraStatusInfo = usbScsiCommand.getCameraStatus();
//        AppLog.d(TAG,"startMovieRecord cameraStatusInfo:" +cameraStatusInfo);
//        if(cameraStatusInfo == null || !cameraStatusInfo.isSDCardExist()){
//            return false;
//        }
//        if(cameraStatusInfo.isVideoRec()){
//            AppLog.i(TAG, "initData is recording");
//            return true;
//        }
//        if (cameraAction != null) {
//            ret = cameraAction.startMovieRecord();
//        }
//        if(ret == 0){
//            return true;
//        }
//        boolean checkRecStatus = checkStartRecStatus(usbScsiCommand,ret,5000);
//        AppLog.d(TAG, "startMovieRecord ret = " + ret);
//        return checkRecStatus;
        return true;
    }


    private boolean stopMovieRecord() {
//        UsbScsiCommand usbScsiCommand = curCamera.getUsbScsiCommand();
//
//        if (usbScsiCommand == null) {
//            AppLog.e(TAG, "startMovieRecord sd card error!");
//            return true;
//        }
//        int ret = -1;
//        CameraStatusInfo cameraStatusInfo0 = usbScsiCommand.getCameraStatus();
//        AppLog.d(TAG,"stopMovieRecord cameraStatusInfo:" +cameraStatusInfo0);
//        if(cameraStatusInfo0 == null){
//            return false;
//        }
//
//        if(!cameraStatusInfo0.isSDCardExist()){
//            return false;
//        }
//        if(!cameraStatusInfo0.isVideoRec()){
//            return true;
//        }
//        if (cameraAction != null) {
//            ret = cameraAction.stopMovieRecord();
//        }
//        if(ret ==0){
//            return true;
//        }
//        boolean checkRecStatus = checkStopRecStatus(usbScsiCommand,ret,5000);
//        AppLog.d(TAG, "startMovieRecord ret = " + ret);
//        return checkRecStatus;
        return true;
    }

    private boolean stopMovieRecord2(CameraStatusInfo cameraStatusInfo) {
//        AppLog.d(TAG, "stopMovieRecord2 cameraStatusInfo:" + cameraStatusInfo);
//        UsbScsiCommand usbScsiCommand = curCamera.getUsbScsiCommand();
//        if (usbScsiCommand == null) {
//            AppLog.e(TAG, "startMovieRecord sd card error!");
//            return true;
//        }
//        int ret = -1;
//        if (cameraStatusInfo == null) {
//            return false;
//        }
//        if (!cameraStatusInfo.isSDCardExist()) {
//            return true;
//        }
//        if (!cameraStatusInfo.isVideoRec()) {
//            return true;
//        }
//        if (cameraAction != null) {
//            ret = cameraAction.stopMovieRecord();
//        }
//        if (ret == 0) {
//            return true;
//        }
//        boolean checkRecStatus = checkStopRecStatus(usbScsiCommand, ret, 5000);
//        AppLog.d(TAG, "startMovieRecord ret = " + ret);
//        return checkRecStatus;
        return true;
    }


    public void usbAttach() {
        AppLog.d(TAG, "USBMonitor onAttach:");
        isAttach = true;
        UsbDevice usbDevice = USBMonitor.getInstance().getDevice();
        USBMonitor.getInstance().requestPermission(usbDevice);
    }

    public void usbConnect(UsbDevice device) {
        AppLog.d(TAG, "USBMonitor onConnect device=" + device);
        reConnect(device);
    }

    public void usbDettach() {
        AppLog.d(TAG, "USB_DEVICE_DETACHED:");
        isAttach = false;
        removeEventListener();
        stopPreview(PreviewStopPvMode.STOP_PV_ONLY);
        destroyPreview();
        destroyCamera();
//        AppDialog.showDialogQuit(activity, R.string.text_device_disconnected);
        MyToast.show(activity, R.string.text_device_disconnected);
    }

    public void usbCancel() {
//        AppDialog.showDialogWarn(activity, R.string.text_usb_permissions_denied);
    }

    public void checkCamera(){
        USBMonitor usbMonitor = USBMonitor.getInstance();
        final UsbDevice usbDevice = usbMonitor.getDevice();
        if(usbDevice == null){
//            AppDialog.showDialogQuit(activity, R.string.text_device_not_connect);
            MyToast.show(activity, R.string.text_device_disconnected);
            previewView.setActionBtnEnable(false);
            return;
        }
        if(usbMonitor.checkUsbPermission(usbDevice)){
            reConnect(usbDevice);
        }else {
            usbMonitor.requestPermission(usbDevice);
        }
    }

    private synchronized void reConnect(final UsbDevice usbDevice) {
        if (curCamera != null && curCamera.isConnected()) {
            return;
        }
        if (!isAttach) {
            return;
        }
        MyProgressDialog.showProgressDialog(activity,R.string.text_connecting_camera);
        Observable<Boolean> observable = Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) {
                curCamera = CameraManager.getInstance().getCurCamera();
                if(curCamera == null){
                    CameraManager.getInstance().createUSBCamera(CameraType.USB_CAMERA, usbDevice, 0);
                    curCamera = CameraManager.getInstance().getCurCamera();
                }
                curCamera.setUsbDevice(usbDevice);
                if (!curCamera.isConnected()) {
                    curCamera.connect(activity, false);
                }
                e.onNext(curCamera.isConnected());
            }
        });
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Boolean>() {
                    @Override
                    public void onNext(Boolean value) {
                        MyProgressDialog.closeProgressDialog();
                        if (value && isAttach) {
                            initData();
                            addEventListener();
                            AppLog.d(TAG,"reConnect surfaceHolder:" + surfaceHolder);
                            if(surfaceHolder != null){
                                if(initSurface(surfaceHolder)){
                                    startPreview(false);
                                }
                            }else {
                                //surfaceChanged会重新被调用
                                previewView.setSurfaceViewVisibility(View.GONE);
                                previewView.setSurfaceViewVisibility(View.VISIBLE);
                            }
                        } else {
                            MyToast.show(activity, "重连失败");
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
