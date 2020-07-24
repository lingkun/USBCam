/**
 * Added by zhangyanhu C01012,2014-6-27
 */
package com.icatch.usbcam.sdkapi;

import android.util.Log;

import com.icatch.usbcam.app.mycamera.CameraManager;
import com.icatch.usbcam.app.mycamera.CommandSession;
import com.icatchtek.baseutil.log.AppLog;
import com.icatchtek.control.customer.ICatchCameraAssist;
import com.icatchtek.control.customer.ICatchCameraControl;
import com.icatchtek.control.customer.ICatchCameraListener;
import com.icatchtek.control.customer.exception.IchCameraModeException;
import com.icatchtek.control.customer.exception.IchCaptureImageException;
import com.icatchtek.control.customer.exception.IchStorageFormatException;
import com.icatchtek.reliant.customer.exception.IchDeviceException;
import com.icatchtek.reliant.customer.exception.IchInvalidSessionException;
import com.icatchtek.reliant.customer.exception.IchListenerExistsException;
import com.icatchtek.reliant.customer.exception.IchListenerNotExistsException;
import com.icatchtek.reliant.customer.exception.IchSocketException;
import com.icatchtek.reliant.customer.exception.IchTryAgainException;

public class CameraAction {
    private static final String TAG = "CameraAction";
    private ICatchCameraControl cameraControl;
    public ICatchCameraAssist cameraAssist;

    public CameraAction(ICatchCameraControl control, ICatchCameraAssist assist) {
        this.cameraControl = control;
        this.cameraAssist = assist;
    }

    public boolean capturePhoto() {
        AppLog.i(TAG, "begin doStillCapture");
        boolean ret = false;
        try {
            ret = cameraControl.capturePhoto();
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCaptureImageException e) {
            AppLog.e(TAG, "IchCaptureImageException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchTryAgainException e) {
            AppLog.e(TAG, "IchTryAgainException");
            e.printStackTrace();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            try {
                ret = cameraControl.capturePhoto();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        AppLog.i(TAG, "end doStillCapture ret = " + ret);
        return ret;
    }

    public boolean triggerCapturePhoto() {
        AppLog.i(TAG, "begin triggerCapturePhoto");
        boolean ret = false;
        try {
            ret = cameraControl.triggerCapturePhoto();
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCaptureImageException e) {
            AppLog.e(TAG, "IchCaptureImageException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end triggerCapturePhoto ret = " + ret);
        return ret;
    }

    public int startMovieRecord() {
        AppLog.i(TAG, "begin startVideoCapture");
        int cswStatus = -1;
        int retValue = -1;
        try {
            retValue = cameraControl.startMovieRecord();
            cswStatus =getCswStatus(retValue);
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end startVideoCapture retValue=" + retValue+ " cswStatus =" + cswStatus);
        return cswStatus;
    }

    public boolean startTimeLapse() {
        AppLog.i(TAG, "begin startTimeLapse");
        boolean ret = false;

        try {
            ret = cameraControl.startTimeLapse();
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end startTimeLapse ret =" + ret);
        return ret;
    }

    public boolean stopTimeLapse() {
        AppLog.i(TAG, "begin stopMovieRecordTimeLapse");
        boolean ret = false;

        try {
            ret = cameraControl.stopTimeLapse();
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end stopMovieRecordTimeLapse ret =" + ret);
        return ret;
    }

    public int stopMovieRecord() {
        AppLog.i(TAG, "begin stopMovieRecord");
        int cswStatus = -1;
        int retValue =-1;
        try {
            retValue = cameraControl.stopMovieRecord();
            cswStatus =getCswStatus(retValue);
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //暂时规避,调用两次
//        if (ret == false) {
//            try {
//                ret = cameraControl.stopMovieRecord();
//            } catch (IchSocketException e) {
//                AppLog.e(TAG, "IchSocketException");
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            } catch (IchCameraModeException e) {
//                AppLog.e(TAG, "IchCameraModeException");
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            } catch (IchInvalidSessionException e) {
//                AppLog.e(TAG, "IchInvalidSessionException");
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
        AppLog.i(TAG, "end stopMovieRecord retValue="+ retValue + " cswStatus =" + cswStatus);
        return cswStatus;
    }

    public int formatStorage() {
        AppLog.i(TAG, "begin formatSD");
        int cswStatus = -1;
        int retValue =-1;
        try {
             retValue = cameraControl.formatStorage();
             cswStatus =getCswStatus(retValue);
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchStorageFormatException e) {
            AppLog.e(TAG, "IchStorageFormatException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end formatSD retValue =" + retValue + " cswStatus =" + cswStatus);
        return cswStatus;
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

    public boolean sleepCamera() {
        AppLog.i(TAG, "begin sleepCamera");
        boolean retValue = false;
        try {
            try {
                retValue = cameraControl.toStandbyMode();
            } catch (IchDeviceException e) {
                // TODO Auto-generated catch block
                AppLog.e(TAG, "IchDeviceException");
                e.printStackTrace();
            } catch (IchInvalidSessionException e) {
                AppLog.e(TAG, "IchInvalidSessionException");
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (IchSocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end sleepCamera retValue =" + retValue);
        return retValue;
    }

    public boolean addCustomEventListener(int eventID, ICatchCameraListener listener) {
        AppLog.i(TAG, "begin addEventListener eventID=" + eventID);
        boolean retValue = false;
        try {
            retValue = cameraControl.addCustomEventListener(eventID, listener);
        } catch (IchInvalidSessionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchListenerExistsException e) {
            e.printStackTrace();
        }

        AppLog.i(TAG, "end addEventListener retValue = " + retValue);
        return retValue;
    }

    public boolean delCustomEventListener(int eventID, ICatchCameraListener listener) {
        AppLog.i(TAG, "begin delEventListener eventID=" + eventID);
        boolean retValue = false;
        try {
            retValue = cameraControl.delCustomEventListener(eventID, listener);
        } catch (IchListenerNotExistsException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end delEventListener retValue = " + retValue);
        return retValue;
    }

    public boolean addEventListener(int eventID, ICatchCameraListener listener) {
        AppLog.i(TAG, "begin addEventListener eventID=" + eventID);

        boolean retValue = false;
        try {

            retValue = cameraControl.addEventListener(eventID, listener);

        } catch (IchListenerExistsException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchListenerExistsException");
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end addEventListener retValue = " + retValue);
        return retValue;
    }

    public boolean delEventListener(int eventID, ICatchCameraListener listener) {
        AppLog.i(TAG, "begin delEventListener eventID=" + eventID);
        boolean retValue = false;
        try {
            retValue = cameraControl.delEventListener(eventID, listener);
        } catch (IchListenerNotExistsException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchListenerExistsException");
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end delEventListener retValue = " + retValue);
        return retValue;
    }

    /**
     * Added by zhangyanhu C01012,2014-7-2
     */
    public String getCameraMacAddress() {
        // TODO Auto-generated method stub
        String macAddress = "";
        macAddress = cameraControl.getMacAddress();
        return macAddress;
    }

    public boolean zoomIn() {
        AppLog.i(TAG, "begin zoomIn");
        boolean retValue = false;
        try {
            retValue = cameraControl.zoomIn();
        } catch (IchSocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchStorageFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end zoomIn retValue = " + retValue);
        return retValue;
    }

    public boolean zoomOut() {
        AppLog.i(TAG, "begin zoomOut");
        boolean retValue = false;
        try {
            retValue = cameraControl.zoomOut();
        } catch (IchSocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchStorageFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end zoomOut retValue = " + retValue);
        return retValue;
    }

    public boolean updateFW(String fileName) {
        boolean ret = false;
        AppLog.i(TAG, "begin update FW");
        CommandSession mSDKSession = CameraManager.getInstance().getCurCamera().getSDKsession();
        try {
            ret = cameraAssist.updateFw(mSDKSession.getSDKSession(), fileName);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "Exception e=" + e.getClass().getSimpleName());
            e.printStackTrace();
        }
        AppLog.i(TAG, "end updateFW ret=" + ret);
        return ret;
    }

    public static boolean addGlobalEventListener(int iCatchEventID, ICatchCameraListener listener, Boolean forAllSession) {
        boolean retValue = false;
        try {
            retValue = ICatchCameraAssist.addEventListener(iCatchEventID, listener, forAllSession);
        } catch (IchListenerExistsException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(TAG,"addGlobalEventListener id=" + iCatchEventID + " retValue=" + retValue);
        return retValue;
    }

    public static boolean delGlobalEventListener(int iCatchEventID, ICatchCameraListener listener, Boolean forAllSession) {
        boolean retValue = false;
        try {
            retValue = ICatchCameraAssist.delEventListener(iCatchEventID, listener, forAllSession);
        } catch (IchListenerNotExistsException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return retValue;
    }

    public boolean previewMove(int xshift, int yshfit) {
        AppLog.i(TAG, "begin previewMove");
        boolean ret = false;
        ret = cameraControl.pan(xshift, yshfit);
        AppLog.i(TAG, "end previewMove ret = " + ret);
        return ret;
        //return true;
    }

    public boolean resetPreviewMove() {
        AppLog.i(TAG, "begin resetPreviewMove");
        boolean ret = false;
        ret = cameraControl.panReset();
        AppLog.i(TAG, "end resetPreviewMove ret = " + ret);
        return ret;
        //return true;
    }

    public boolean changePreviewMode(int mode){
        AppLog.i(TAG, "begin changePreviewMode");
        boolean ret = false;
        try {
            ret = cameraControl.changePreviewMode(mode);
        } catch (IchCameraModeException e) {
            e.printStackTrace();
        }
        AppLog.i(TAG, "end changePreviewMode ret = " + ret);
        return ret;
    }

    public boolean setAudioMute(){
        AppLog.i(TAG, "begin setAudioMute");
        boolean ret = false;
        try {
            ret = cameraControl.setAudioMute();
        } catch (Exception e) {
            AppLog.e(TAG, "Exception e= " + e.getClass().getSimpleName());
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setAudioMute ret = " + ret);
        return ret;
    }

    public boolean setAudioUnMute(){
        AppLog.i(TAG, "begin setAudioUnMute");
        boolean ret = false;
        try {
            ret = cameraControl.setAudioUnMute();
        } catch (Exception e) {
            AppLog.e(TAG, "Exception e= " + e.getClass().getSimpleName());
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setAudioUnMute ret = " + ret);
        return ret;
    }

    public boolean setEventTrigger(){
        AppLog.i(TAG, "begin setEventTrigger");
        boolean ret = false;
        try {
            ret = cameraControl.setEventTrigger();
        } catch (Exception e) {
            AppLog.e(TAG, "Exception e= " + e.getClass().getSimpleName());
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setEventTrigger ret = " + ret);
        return ret;
    }


}
