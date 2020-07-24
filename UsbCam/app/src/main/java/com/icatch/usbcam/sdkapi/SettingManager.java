package com.icatch.usbcam.sdkapi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;

import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.icatch.usbcam.R;
import com.icatch.usbcam.app.UsbApp;
import com.icatch.usbcam.bean.CameraStatusInfo;
import com.icatch.usbcam.bean.PropertyTypeInteger;
import com.icatch.usbcam.bean.PropertyUsbPvSize;
import com.icatch.usbcam.common.mode.UsbMsdcDscopMode;
import com.icatch.usbcam.common.type.AppErrorCode;
import com.icatch.usbcam.common.type.FormatStatus;
import com.icatch.usbcam.common.type.FwUpdateStatus;
import com.icatch.usbcam.data.appinfo.AppInfo;
import com.icatch.usbcam.data.hash.PropertyHashMapStatic;
import com.icatch.usbcam.data.propertyid.PropertyId;
import com.icatch.usbcam.engine.setting.SettingItem;
import com.icatchtek.basecomponent.prompt.AppDialog;
import com.icatchtek.basecomponent.prompt.MyProgressDialog;
import com.icatchtek.basecomponent.prompt.MyToast;
import com.icatchtek.baseutil.log.AppLog;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by b.jiang on 2017/9/15.
 */

public class SettingManager {
    private String TAG = SettingManager.class.getSimpleName();
    private CameraProperties cameraProperty;
    private UsbScsiCommand usbScsiCommand;
    private CameraAction cameraAction;
//    private PropertyTypeInteger whiteBalance;
    //录像画质
    private PropertyTypeInteger videoQuality;
    //录像时长
    private PropertyTypeInteger eventFileTime;
    //碰撞灵敏度
    private PropertyTypeInteger crashSensitivity;
    //录像分辨率
    private PropertyTypeInteger usbVideoRecodeSize;
    //显示分辨率
    private PropertyUsbPvSize usbPvSize;
    //无缝录影时长
    private PropertyTypeInteger seamlessVideo;

    private LinkedList<SettingItem> settingMenuList;
    private String fwVersion = "unknown";

    public SettingManager(CameraProperties cameraProperty, UsbScsiCommand usbScsiCommand, CameraAction cameraAction) {
        this.cameraProperty = cameraProperty;
        this.usbScsiCommand = usbScsiCommand;
        this.cameraAction = cameraAction;
        PropertyHashMapStatic.getInstance().initPropertyHashMap();
        initProperty();
    }

    public void resetClient(CameraProperties cameraProperty, UsbScsiCommand usbScsiCommand, CameraAction cameraAction){
        this.cameraProperty = cameraProperty;
        this.usbScsiCommand = usbScsiCommand;
        this.cameraAction = cameraAction;
        resetProperty();
    }


    private void resetProperty(){
        if(seamlessVideo != null){
            seamlessVideo.setCameraProperties(cameraProperty);
        }
        if(usbVideoRecodeSize != null){
            usbVideoRecodeSize.setCameraProperties(cameraProperty);
        }
        if(videoQuality != null){
            videoQuality.setCameraProperties(cameraProperty);
        }
        if(eventFileTime != null){
            eventFileTime.setCameraProperties(cameraProperty);
        }
        if(crashSensitivity != null){
            crashSensitivity.setCameraProperties(cameraProperty);
        }
//        if(whiteBalance != null){
//            whiteBalance.setCameraProperties(cameraProperty);
//        }
    }
    private void initProperty() {
        // TODO Auto-generated method stub
        AppLog.i(TAG, "Start initProperty");
        if (cameraProperty.hasFuction(PropertyId.USB_SEAMLESS_VIDEO)) {
            seamlessVideo = new PropertyTypeInteger(cameraProperty, PropertyHashMapStatic.seamlessesVideoMap, PropertyId.USB_SEAMLESS_VIDEO, UsbApp.getContext());
        }
        usbPvSize = new PropertyUsbPvSize(cameraProperty);
        if (cameraProperty.hasFuction(PropertyId.USB_VIDEO_REC_SIZE)) {
            usbVideoRecodeSize = new PropertyTypeInteger(cameraProperty, PropertyHashMapStatic.usbVideoRecSizeMap, PropertyId.USB_VIDEO_REC_SIZE, UsbApp.getContext());
        }

        if (cameraProperty.hasFuction(PropertyId.VIDEO_QUALITY)) {
            videoQuality = new PropertyTypeInteger(cameraProperty, PropertyHashMapStatic.videoQualityMap, PropertyId.VIDEO_QUALITY, UsbApp.getContext());
        }
        if (cameraProperty.hasFuction(PropertyId.EVENT_FILE_TIME)) {
            eventFileTime = new PropertyTypeInteger(cameraProperty, PropertyHashMapStatic.eventFileTimeMap, PropertyId.EVENT_FILE_TIME, UsbApp.getContext());
        }
        if (cameraProperty.hasFuction(PropertyId.CARSH_SENSITIVITY)) {
            crashSensitivity = new PropertyTypeInteger(cameraProperty, PropertyHashMapStatic.carshSensitivityMap, PropertyId.CARSH_SENSITIVITY, UsbApp.getContext());
        }
        if (usbScsiCommand != null) {
            fwVersion = usbScsiCommand.getFwVersion();
        }
        initSettingList();
        AppLog.i(TAG, "End initProperty");
    }

    private void retsetProperty() {
        // TODO Auto-generated method stub
        AppLog.i(TAG, "Start retsetProperty");
        if(seamlessVideo != null){
            seamlessVideo.initIndex();
        }
        if(usbVideoRecodeSize != null){
            usbVideoRecodeSize.initIndex();
        }
        if(videoQuality != null){
            videoQuality.initIndex();
        }
        if(eventFileTime != null){
            eventFileTime.initIndex();
        }
        if(crashSensitivity != null){
            crashSensitivity.initIndex();
        }
        //---------
//        if (cameraProperty.hasFuction(PropertyId.USB_SEAMLESS_VIDEO)) {
//            seamlessVideo = new PropertyTypeInteger(cameraProperty, PropertyHashMapStatic.seamlessesVideoMap, PropertyId.USB_SEAMLESS_VIDEO, UsbApp.getContext());
//        }
////        usbPvSize = new PropertyUsbPvSize(cameraProperty);
//        if (cameraProperty.hasFuction(PropertyId.USB_VIDEO_REC_SIZE)) {
//            usbVideoRecodeSize = new PropertyTypeInteger(cameraProperty, PropertyHashMapStatic.usbVideoRecSizeMap, PropertyId.USB_VIDEO_REC_SIZE, UsbApp.getContext());
//        }
//        if (cameraProperty.hasFuction(PropertyId.VIDEO_QUALITY)) {
//            videoQuality = new PropertyTypeInteger(cameraProperty, PropertyHashMapStatic.videoQualityMap, PropertyId.VIDEO_QUALITY, UsbApp.getContext());
//        }
//        if (cameraProperty.hasFuction(PropertyId.EVENT_FILE_TIME)) {
//            eventFileTime = new PropertyTypeInteger(cameraProperty, PropertyHashMapStatic.eventFileTimeMap, PropertyId.EVENT_FILE_TIME, UsbApp.getContext());
//        }
//        if (cameraProperty.hasFuction(PropertyId.CARSH_SENSITIVITY)) {
//            crashSensitivity = new PropertyTypeInteger(cameraProperty, PropertyHashMapStatic.carshSensitivityMap, PropertyId.CARSH_SENSITIVITY, UsbApp.getContext());
//        }
        initSettingList();
        AppLog.i(TAG, "End retsetProperty");
    }

    public PropertyUsbPvSize getUsbPvSize() {
        return usbPvSize;
    }

    private LinkedList<SettingItem> initSettingList() {
        if (settingMenuList == null) {
            settingMenuList = new LinkedList<>();
        } else {
            settingMenuList.clear();
        }
//        if (cameraProperty.hasFuction(ICatchCamProperty.ICH_CAM_CAP_WHITE_BALANCE)) {
//            settingMenuList.add(new SettingItem(R.string.title_awb,ICatchCamProperty.ICH_CAM_CAP_WHITE_BALANCE,whiteBalance.getStringList(),whiteBalance.getCurIndex());
//        }
        if(usbPvSize.getValueArrayString() != null && usbPvSize.getValueArrayString().length >0){
            settingMenuList.add(new SettingItem(R.string.setting_preview_resolution, PropertyId.VIDEO_SIZE, usbPvSize.getValueArrayString(), usbPvSize.getCurIndex()));
        }
        if (cameraProperty.hasFuction(PropertyId.USB_VIDEO_REC_SIZE) && usbVideoRecodeSize.getStringList() != null && usbVideoRecodeSize.getStringList().length >0) {
            settingMenuList.add(new SettingItem(R.string.setting_video_rec_size, PropertyId.USB_VIDEO_REC_SIZE, usbVideoRecodeSize.getStringList(), usbVideoRecodeSize.getCurIndex()));
        }
        if (cameraProperty.hasFuction(PropertyId.VIDEO_QUALITY)&& videoQuality.getStringList() != null && videoQuality.getStringList().length >0) {
            settingMenuList.add(new SettingItem(R.string.setting_video_quality, PropertyId.VIDEO_QUALITY, videoQuality.getStringList(), videoQuality.getCurIndex()));
        }
        if (cameraProperty.hasFuction(PropertyId.EVENT_FILE_TIME)&& eventFileTime.getStringList() != null && eventFileTime.getStringList().length >0) {
            settingMenuList.add(new SettingItem(R.string.setting_event_file_time, PropertyId.EVENT_FILE_TIME, eventFileTime.getStringList(), eventFileTime.getCurIndex()));
        }
        if (cameraProperty.hasFuction(PropertyId.CARSH_SENSITIVITY)&& crashSensitivity.getStringList() != null && crashSensitivity.getStringList().length >0) {
            settingMenuList.add(new SettingItem(R.string.setting_crash_sensitivity, PropertyId.CARSH_SENSITIVITY, crashSensitivity.getStringList(), crashSensitivity.getCurIndex()));
        }
//        settingMenuList.add(new SettingMenu(R.string.setting_event_trigger, ""));
        if (cameraProperty.hasFuction(PropertyId.USB_SEAMLESS_VIDEO) && seamlessVideo.getStringList() != null && seamlessVideo.getStringList().length >0) {
            settingMenuList.add(new SettingItem(R.string.setting_seamless, PropertyId.USB_SEAMLESS_VIDEO, seamlessVideo.getStringList(), seamlessVideo.getCurIndex()));
        }
//        settingMenuList.add(new SettingItem(R.string.setting_update_time, 0));
        settingMenuList.add(new SettingItem(R.string.setting_format, 0));
        settingMenuList.add(new SettingItem(R.string.setting_reset, 0));
        settingMenuList.add(new SettingItem(R.string.setting_check_firmware_update, 0));
        settingMenuList.add(new SettingItem(R.string.setting_firmware_version, 0, fwVersion));
        settingMenuList.add(new SettingItem(R.string.setting_app_version, 0, AppInfo.APP_VERSION));
        return settingMenuList;
    }


    public LinkedList<SettingItem> getSettingMenuList() {
        return settingMenuList;
    }

    public void setPropertys(LinkedList<SettingItem> settingItems) {
        if (settingItems == null || settingItems.size() <= 0) {
            return;
        }
        for (SettingItem temp : settingItems) {
            setProperty(temp.getPropertyId(), temp.getIndex());
        }
        initSettingList();
    }

    private void setProperty(int propertyId, int index) {
        AppLog.d(TAG,"setProperty propertyId:" + propertyId + " index:" + index);
        if (propertyId <= 0) {
            return;
        }
        switch (propertyId) {
//            case PropertyId.WHITE_BALANCE:
//                if(whiteBalance.getCurIndex() != index){
//                    whiteBalance.setValue(index);
//                }
//                break;
            case PropertyId.VIDEO_SIZE:
                if (usbPvSize.getCurIndex() != index) {
                    usbPvSize.setValueByPosition(index);
                }
                break;
            case PropertyId.USB_VIDEO_REC_SIZE:
                if (usbVideoRecodeSize.getCurIndex() != index) {
                    usbVideoRecodeSize.setValue(index);
//                    usbPvSize.initItem();
                }
                break;
            //录像画质
            case PropertyId.VIDEO_QUALITY:
                if (videoQuality.getCurIndex() != index) {
                    videoQuality.setValue(index);
                }
                break;
            //录像时长
            case PropertyId.USB_SEAMLESS_VIDEO:
                if (seamlessVideo.getCurIndex() != index) {
                    seamlessVideo.setValue(index);
                }
                break;
            //锁定录像时长
            case PropertyId.EVENT_FILE_TIME:
                if (eventFileTime.getCurIndex() != index) {
                    eventFileTime.setValue(index);
                }
                break;
            //碰撞灵敏度
            case PropertyId.CARSH_SENSITIVITY:
                if (crashSensitivity.getCurIndex() != index) {
                    crashSensitivity.setValue(index);
                }
                break;
            //语言设置
            //时间同步

            default:

        }
    }

    public void showFormatConfirmDialog(final Activity activity, final OnOperationComplete operationComplete, final SettingItem settingItem) {
        AppDialog.showDialogWarning(activity, R.string.setting_format_desc, new AppDialog.OnDialogButtonClickListener() {
            @Override
            public void onCancel() {
                if (operationComplete != null) {
                    operationComplete.cancel();
                }
            }

            @Override
            public void onSure() {
                Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> e) {
                        int retValue = -1;
                        if (operationComplete != null) {
                            operationComplete.before(settingItem.getTitleResId());
                        }
                        retValue = cameraAction.formatStorage();
                        e.onNext(retValue);
                    }
                });
                observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new DisposableObserver<Integer>() {
                            @Override
                            public void onNext(Integer value) {
                                MyProgressDialog.closeProgressDialog();
                                MyToast.show(activity, AppErrorCode.getResId(value));
                                if (operationComplete != null) {
                                    operationComplete.complete(settingItem.getTitleResId());
                                }
                            }

                            @Override
                            public void onError(Throwable throwable) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        });
                MyProgressDialog.showProgressDialog(activity, R.string.setting_format);
            }
        });
    }

    public void showResetDialog(final Activity activity, final OnOperationComplete operationComplete,final SettingItem settingItem) {
        AppDialog.showDialogWarning(activity, R.string.setting_reset, new AppDialog.OnDialogButtonClickListener() {
            @Override
            public void onCancel() {
                if (operationComplete != null) {
                    operationComplete.cancel();
                }
            }

            @Override
            public void onSure() {
                Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> e) {
                        if (operationComplete != null) {
                            operationComplete.before(settingItem.getTitleResId());
                        }
                        if (usbScsiCommand != null) {
                            usbScsiCommand.resetCamera();
                        }
                        retsetProperty();
                        e.onNext(AppErrorCode.SUCCESS);
                    }
                });
                observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new DisposableObserver<Integer>() {
                            @Override
                            public void onNext(Integer value) {
                                MyProgressDialog.closeProgressDialog();
                                MyToast.show(activity, AppErrorCode.getResId(value));
                                if (operationComplete != null) {
                                    operationComplete.complete(settingItem.getTitleResId());
                                }
                            }

                            @Override
                            public void onError(Throwable throwable) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        });
                MyProgressDialog.showProgressDialog(activity, R.string.setting_reset);
            }
        });
    }

    public void setCameraTime(final Activity activity, final OnOperationComplete operationComplete,final SettingItem settingItem) {
        // TODO Auto-generated method stub
        final Handler handler = new Handler();
        MyProgressDialog.showProgressDialog(activity, R.string.setting_update_time);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (operationComplete != null) {
                    operationComplete.before(settingItem.getTitleResId());
                }
                if (usbScsiCommand != null) {
                    usbScsiCommand.updateFwTime(new Date());
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyProgressDialog.closeProgressDialog();
                        MyToast.show(activity, R.string.text_operation_success);
                        if (operationComplete != null) {
                            operationComplete.complete(settingItem.getTitleResId());
                        }
                    }
                });
            }
        }).start();

    }

    public void showUpdateFwDialog(final Activity activity, final OnOperationComplete operationComplete, final SettingItem settingItem) {
        AppDialog.showDialogWarning(activity, R.string.title_ok,R.string.title_ignore,R.string.new_fw_to_update, new AppDialog.OnDialogButtonClickListener() {
            @Override
            public void onCancel() {
                usbScsiCommand.ignoreUpdateFw();
                if (operationComplete != null) {
                    operationComplete.cancel();
                }
            }

            @Override
            public void onSure() {
                Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> e) {
                        if (operationComplete != null) {
                            operationComplete.before(settingItem.getTitleResId());
                        }
                        if (usbScsiCommand != null) {
                            usbScsiCommand.updateFw();
                        }
                        //设置超时时间为1分钟
                        int ret;
                        try {
                            ret = checkFwUpdateState(60*1000);
                        } catch (TimeoutException e1) {
                            e1.printStackTrace();
                            ret = AppErrorCode.OPERATION_TIMEOUT;
                        }
                        e.onNext(ret);
                    }
                });
                observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new DisposableObserver<Integer>() {
                            @Override
                            public void onNext(Integer value) {
                                dismissRoundProgressDialog();
                                MyToast.show(activity, AppErrorCode.getResId(value));
                                if(value == AppErrorCode.SUCCESS){
                                    if (usbScsiCommand != null) {
                                        fwVersion = usbScsiCommand.getFwVersion();
                                        initSettingList();
                                        AppLog.d(TAG,"complete UpdateFw fwVersion:" + fwVersion);
                                    }
                                    AppDialog.showDialogWarning(activity,R.string.update_completion_prompt);
                                }
                                if (operationComplete != null) {
                                    operationComplete.complete(settingItem.getTitleResId());
                                }
                            }

                            @Override
                            public void onError(Throwable throwable) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        });
                showRoundProgressDialog(activity,"正在更新，请勿断电");
            }
        });
    }

    private int checkFwUpdateState(long timeout) throws TimeoutException {
        int sleep = 500;
        int num = (int) (timeout / sleep);
        CameraStatusInfo cameraStatusInfo = usbScsiCommand.getCameraStatus();
        int curNum = 1;
        while (cameraStatusInfo != null && cameraStatusInfo.getFwUpdateStatus() == FwUpdateStatus.FW_UPDATE_DOING && curNum < num) {
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            cameraStatusInfo = usbScsiCommand.getCameraStatus();
            curNum++;
        }
        int retValue;
        if (cameraStatusInfo != null && cameraStatusInfo.getFwUpdateStatus() == FwUpdateStatus.FW_UPDATE_DOING && curNum == num)  {
            throw new TimeoutException("Fw update timeout");
        }
        if(cameraStatusInfo != null && cameraStatusInfo.getFwUpdateStatus() == FwUpdateStatus.FW_UPDATE_DONE){
            retValue = AppErrorCode.SUCCESS;
        }else {
            retValue = AppErrorCode.FAILED;
        }
        AppLog.d(TAG, "checkFwUpdateState retValue:" + retValue + " cuNum:" + curNum);
        return retValue;
    }


    public interface OnOperationComplete {
        void before(int resId);

        void complete(int resId);

        void cancel();
    }

    ProgressDialog pd;
    private void showRoundProgressDialog(Context context,String message) {
        dismissRoundProgressDialog();
        pd = new ProgressDialog(context);
        pd.setMessage(message);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
    }

    private void dismissRoundProgressDialog() {
        if(pd !=null && pd.isShowing()){
            pd.dismiss();
            pd = null;
        }
    }
}
