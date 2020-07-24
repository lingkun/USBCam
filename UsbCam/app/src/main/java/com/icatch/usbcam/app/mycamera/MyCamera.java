package com.icatch.usbcam.app.mycamera;

import android.app.Activity;
import android.content.Context;
import android.hardware.usb.UsbDevice;

import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.icatch.usbcam.bean.CameraStatusInfo;
import com.icatch.usbcam.common.mode.UsbMsdcDscopMode;
import com.icatch.usbcam.common.usb.USBHost_Feature;
import com.icatch.usbcam.engine.event.EventPollManager;
import com.icatch.usbcam.sdkapi.CameraAction;
import com.icatch.usbcam.sdkapi.CameraProperties;
import com.icatch.usbcam.sdkapi.PanoramaPreviewPlayback;
import com.icatch.usbcam.sdkapi.SettingManager;
import com.icatch.usbcam.sdkapi.UsbScsiCommand;
import com.icatchtek.baseutil.log.AppLog;
import com.icatchtek.control.customer.ICatchCameraSession;
import com.icatchtek.pancam.customer.ICatchPancamSession;
import com.icatchtek.reliant.customer.exception.IchInvalidArgumentException;
import com.icatchtek.reliant.customer.exception.IchInvalidSessionException;
import com.icatchtek.reliant.customer.exception.IchTransportException;
import com.icatchtek.reliant.customer.transport.ICatchINETTransport;
import com.icatchtek.reliant.customer.transport.ICatchITransport;
import com.icatchtek.reliant.customer.transport.ICatchUsbScsiTransport;
import com.icatchtek.reliant.customer.type.ICatchScsiMode;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author b.jiang
 * @date 2018/10/22
 * @description
 */
public class MyCamera {
    private final String TAG = MyCamera.class.getSimpleName();
    private CommandSession commandSession;
    private PanoramaSession panoramaSession;
    private CameraAction cameraAction;
    private CameraProperties cameraProperties;
    private PanoramaPreviewPlayback panoramaPreviewPlayback;
    private UsbScsiCommand usbScsiCommand;
    private SettingManager settingManager;
    public String cameraName;
    private String ipAddress;
    public boolean isStreamReady = false;
    private int cameraType;
    private boolean isConnected = false;
    private int position;
    private UsbDevice usbDevice;
    private ICatchITransport transport;
    private Timer resetTimer;
    private EventPollManager eventPollManager;

    public MyCamera(int cameraType) {
        this.cameraType = cameraType;
    }

    public MyCamera(int cameraType, String cameraName) {
        this.cameraName = cameraName;
        this.cameraType = cameraType;
    }

    public MyCamera(int cameraType, UsbDevice usbDevice, int position) {
        this.cameraType = cameraType;
        this.position = position;
        this.usbDevice = usbDevice;
        this.cameraName = "UsbCamera_" + usbDevice.getVendorId();
    }

    public void setUsbDevice(UsbDevice usbDevice) {
        this.usbDevice = usbDevice;
    }

    public boolean connect(Context context, boolean enablePTPIP) {
        AppLog.d(TAG, "connect cameraType=" + cameraType + " enablePTPIP=" + enablePTPIP);
        boolean ret = false;
        commandSession = new CommandSession();
        panoramaSession = new PanoramaSession();
//        ICatchITransport transport = null;
        if (cameraType == CameraType.PANORAMA_CAMERA) {
            transport = new ICatchINETTransport(ipAddress);
        } else if (cameraType == CameraType.USB_CAMERA) {
            USBHost_Feature feature = new USBHost_Feature(context);
            feature.setUsbDevice(usbDevice.getVendorId(), usbDevice.getProductId());
            try {
                transport = new ICatchUsbScsiTransport(feature.getUsbDevice(), feature.getUsbDeviceConnection());
            } catch (Exception e) {
                AppLog.i(TAG, "new ICatchUsbScsiTransport e:" + e.getClass().getSimpleName() + " message:" + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        if (transport != null) {
            AppLog.i(TAG, "transport is" + transport.getClass().getSimpleName());
            try {
                transport.prepareTransport();
            } catch (IchTransportException e) {
                AppLog.i(TAG, "prepareTransport IchTransportException");
                e.printStackTrace();
                return false;
            }
            ret = commandSession.prepareSession(transport, enablePTPIP);
            if (!ret) {
                return false;
            }
            ret = panoramaSession.prepareSession(transport);
        }
        if (ret) {
            isConnected = true;
            try {
                cameraAction = new CameraAction(commandSession.getSDKSession().getControlClient(), ICatchCameraSession.getCameraAssist(transport));
            } catch (IchInvalidSessionException e) {
                e.printStackTrace();
            } catch (IchInvalidArgumentException e) {
                e.printStackTrace();
            }
            usbScsiCommand = new UsbScsiCommand(transport);
            usbScsiCommand.updateFwTime(new Date());
            if (getCurrentMode() == ICatchScsiMode.SCSI_MODE_PLAYBACK) {
                switchToPreview();
            }
//            int w = SystemInfo.getMetrics(UsbApp.getContext()).widthPixels;
//            int h = SystemInfo.getMetrics(UsbApp.getContext()).heightPixels;
//            if (w > h) {
//                usbScsiCommand.setScreenResolution(w, h);//
//            } else {
//                usbScsiCommand.setScreenResolution(h, w);//
//            }
            initCamera();
        }
        return ret;
    }

    public boolean reconnect(Context context, boolean enablePTPIP) {
        AppLog.d(TAG, "reconnect cameraType=" + cameraType + " enablePTPIP=" + enablePTPIP);
        if(isConnected){
            AppLog.d(TAG, "reconnect is connected");
            return true;
        }
        boolean ret = false;
        commandSession = new CommandSession();
        panoramaSession = new PanoramaSession();
//        ICatchITransport transport = null;
        if (cameraType == CameraType.PANORAMA_CAMERA) {
            transport = new ICatchINETTransport(ipAddress);
        } else if (cameraType == CameraType.USB_CAMERA) {
            USBHost_Feature feature = new USBHost_Feature(context);
            feature.setUsbDevice(usbDevice.getVendorId(), usbDevice.getProductId());
            try {
                transport = new ICatchUsbScsiTransport(feature.getUsbDevice(), feature.getUsbDeviceConnection());
            } catch (Exception e) {
                AppLog.i(TAG, "new ICatchUsbScsiTransport e:" + e.getClass().getSimpleName() + " message:" + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        if (transport != null) {
            AppLog.i(TAG, "transport is" + transport.getClass().getSimpleName());
            try {
                transport.prepareTransport();
            } catch (IchTransportException e) {
                AppLog.i(TAG, "prepareTransport IchTransportException");
                e.printStackTrace();
                return false;
            }
            ret = commandSession.prepareSession(transport, enablePTPIP);
            if (!ret) {
                return false;
            }
            ret = panoramaSession.prepareSession(transport);
        }
        if (ret) {
            isConnected = true;
            try {
                cameraAction = new CameraAction(commandSession.getSDKSession().getControlClient(), ICatchCameraSession.getCameraAssist(transport));
            } catch (IchInvalidSessionException e) {
                e.printStackTrace();
            } catch (IchInvalidArgumentException e) {
                e.printStackTrace();
            }

            usbScsiCommand = new UsbScsiCommand(transport);
            if (getCurrentMode() != ICatchScsiMode.SCSI_MODE_PREVIEW) {
                switchToPreview();
            }
//            ret = switchToPreview();
            resetCamera();
        }
        return ret;
    }

    public boolean disconnect() {
        if (isConnected) {
            isConnected = false;
            eventPollManager.stopPolling();
            if (transport != null) {
                try {
                    transport.destroyTransport();
                } catch (IchTransportException e) {
                    e.printStackTrace();
                }
            }
            commandSession.destroySession();
            panoramaSession.destroySession();
            commandSession = null;
            panoramaSession = null;
        }
        return true;
    }

    private boolean resetCamera() {
        boolean retValue = false;
        AppLog.i(TAG, "Start initClient");
        ICatchCameraSession iCatchCommandSession = commandSession.getSDKSession();
        ICatchPancamSession iCatchPancamSession = panoramaSession.getSession();
        try {
            if(cameraProperties != null){
                cameraProperties.resetClient(iCatchCommandSession.getPropertyClient(), iCatchCommandSession.getControlClient());
            }else {
                cameraProperties = new CameraProperties(iCatchCommandSession.getPropertyClient(), iCatchCommandSession.getControlClient());
            }
        } catch (IchInvalidSessionException e) {
            e.printStackTrace();
        }
        if(panoramaPreviewPlayback != null){
            panoramaPreviewPlayback.resetClient(iCatchPancamSession);
        }else {
            panoramaPreviewPlayback = new PanoramaPreviewPlayback(iCatchPancamSession);
        }

        if(eventPollManager != null){
            eventPollManager.resetClient(usbScsiCommand,cameraProperties);
        } else {
            eventPollManager = new EventPollManager(usbScsiCommand,cameraProperties);
        }
        if(settingManager != null){
            settingManager.resetClient(cameraProperties,usbScsiCommand,cameraAction);
        }else {
            settingManager = new SettingManager(cameraProperties,usbScsiCommand,cameraAction);
        }
        return retValue;
    }

    private boolean initCamera() {
        boolean retValue = false;
        AppLog.i(TAG, "Start initClient");
        ICatchCameraSession iCatchCommandSession = commandSession.getSDKSession();
        ICatchPancamSession iCatchPancamSession = panoramaSession.getSession();
        try {
            cameraProperties = new CameraProperties(iCatchCommandSession.getPropertyClient(), iCatchCommandSession.getControlClient());
            panoramaPreviewPlayback = new PanoramaPreviewPlayback(iCatchPancamSession);
            eventPollManager = new EventPollManager(usbScsiCommand,cameraProperties);
        } catch (IchInvalidSessionException e) {
            e.printStackTrace();
        }
        settingManager = new SettingManager(cameraProperties,usbScsiCommand,cameraAction);
        return retValue;
    }

    public CommandSession getSDKsession() {
        return commandSession;
    }

    public CameraAction getCameraAction() {
        return cameraAction;
    }

    public CameraProperties getCameraProperties() {
        return cameraProperties;
    }

    public PanoramaPreviewPlayback getPanoramaPreviewPlayback() {
        return panoramaPreviewPlayback;
    }

    public SettingManager getSettingManager() {
        return settingManager;
    }

    public UsbScsiCommand getUsbScsiCommand() {
        return usbScsiCommand;
    }

    public EventPollManager getEventPollManager() {
        return eventPollManager;
    }

    public boolean isConnected() {
        AppLog.d(TAG, "isConnected:" + isConnected);
        return isConnected;
    }

    public PanoramaSession getPanoramaSession() {
        return panoramaSession;
    }

    public String getCameraName() {
        return cameraName;
    }

    public int getPosition() {
        return position;
    }

    public UsbDevice getUsbDevice() {
        return usbDevice;
    }

    public int getCameraType() {
        return cameraType;
    }

    public int switchToPlayback(Activity activity) {
        int ret = -1;
        if (transport != null && transport instanceof ICatchUsbScsiTransport) {
            try {
                ret = ((ICatchUsbScsiTransport) transport).switchToPlayback();
            } catch (IchTransportException e) {
                AppLog.d(TAG, "switchToPlayback IchTransportException");
                e.printStackTrace();
            }
        }
        AppLog.d(TAG,"switchToPlayback ret:" + ret);
        return ret;
//        if(ret == UsbMsdcDscopMode.SCSI_CMD_CSW_STUS_SUCCESS){
//            return 0;
//        }
//        int cswStatus = getCswStatus(ret);
//        UsbMassStorageDevice[] storageDevices = UsbMassStorageDevice.getMassStorageDevices(activity);
//        if(storageDevices == null || storageDevices.length <= 0){
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            return -1;
//        }
//        try {
//            storageDevices[0].init();
//            int switchState = checkPbState(storageDevices[0],cswStatus,10000);
//            if(switchState != 0){
//                usbScsiCommand.exceptionRecoveryForPb();
//                switchState = checkPbState(storageDevices[0],cswStatus,10000);
//                storageDevices[0].close();
//                return switchState;
//            }else {
//                storageDevices[0].close();
//                return 0;
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return 0;
    }

    private int checkPbState(UsbMassStorageDevice msdcDevice,int cswStatus,long timeout){
        if(cswStatus ==  UsbMsdcDscopMode.SCSI_CMD_CSW_STUS_PV_TO_PB_T_OUT){
            int sleep = 200;
            int num = (int) (timeout/sleep);
            CameraStatusInfo cameraStatusInfo = usbScsiCommand.getCameraStatusForPb(msdcDevice);
            int curNum = 1;
            while (cameraStatusInfo != null && cameraStatusInfo.getSwitchModeStatus() != UsbMsdcDscopMode.USB_MSDC_DSCOP_MODE_PB && curNum < num)  {
                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                cameraStatusInfo = usbScsiCommand.getCameraStatusForPb(msdcDevice);
                curNum++;
            }
            int retValue;
            if(cameraStatusInfo != null && cameraStatusInfo.getSwitchModeStatus() == UsbMsdcDscopMode.USB_MSDC_DSCOP_MODE_PB){
                retValue = 0;
            }else {
                retValue =  -1;
            }
            AppLog.d(TAG, "checkPbState retValue:" + retValue + " cuNum:" + curNum);
            return retValue;
        }else {
            return -1;
        }
    }

    public boolean switchToPreview() {
        int ret = -1;
        boolean pvStatus = false;
        if (transport != null && transport instanceof ICatchUsbScsiTransport) {
            try {
                ret = ((ICatchUsbScsiTransport) transport).switchToPreview();
            } catch (IchTransportException e) {
                AppLog.d(TAG, "switchToPreview IchTransportException");
                e.printStackTrace();
            }
            AppLog.d(TAG,"switchToPreview ret:" + ret);
            if(ret == UsbMsdcDscopMode.SCSI_CMD_CSW_STUS_SUCCESS){
                return true;
            }
            int cswStatus = getCswStatus(ret);
//            pvStatus = checkPvStatus(usbScsiCommand,cswStatus,5000);
//            if(!pvStatus){
//                usbScsiCommand.exceptionRecoveryForPv();
//                pvStatus = checkPvStatus(usbScsiCommand,cswStatus,5000);
//            }
            if(cswStatus == 0){
                pvStatus = true;
            }else {
                pvStatus = false;
            }
            AppLog.d(TAG, "switchToPreview pvStatus=" + pvStatus);
        }
        return pvStatus;
    }


    public int getCurrentMode() {
        int ret = -1;
        if (transport != null && transport instanceof ICatchUsbScsiTransport) {
            try {
                ret = ((ICatchUsbScsiTransport) transport).getCurrentMode();
            } catch (IchTransportException e) {
                AppLog.d(TAG, "getCurrentMode IchTransportException");
                e.printStackTrace();
            }
            AppLog.d(TAG, "getCurrentMode ret=" + ret);
        }
        return ret;
    }


    public void startResetTimer(final UsbMassStorageDevice msdcDevice){
        //FW timer 时间为5s
       stopResetTimer();
       resetTimer = new Timer();
        resetTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                usbScsiCommand.resetTimerForPb(msdcDevice);
            }
        }, 0, 1000);
    }

    public void stopResetTimer() {
        if (resetTimer != null) {
            resetTimer.cancel();
            resetTimer = null;
        }
    }

    private int getCswStatus(int ret){
        AppLog.d(TAG,"getCswStatus ret:" + ret);
        if (ret < 0) {
            int uintErrorCode = -ret;
            int cswStatus;
            if ((uintErrorCode & 0xFF00) == 0xFF00) {
                cswStatus = uintErrorCode & 0x00FF;
                AppLog.i("csw", "status: " + cswStatus);
            } else {
                cswStatus = uintErrorCode;
                AppLog.i("csw", "not csw status, original code is: " + uintErrorCode);
            }
            return cswStatus;
        }else {
            return 0;
        }
    }

    private void startTimer(){
//        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1,
//                new BasicThreadFactory.Builder().namingPattern("example-schedule-pool-%d").daemon(true).build());
//        executorService.scheduleAtFixedRate(new Runnable() {
//            @Override
//            public void run() {
//                //do something
//            }
//        },initialDelay,period, TimeUnit.HOURS);
    }


}

