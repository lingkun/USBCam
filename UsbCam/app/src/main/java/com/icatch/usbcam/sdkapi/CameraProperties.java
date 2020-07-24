/**
 * Added by zhangyanhu C01012,2014-6-23
 */
package com.icatch.usbcam.sdkapi;

import android.util.Log;

import com.icatch.usbcam.common.convert.BurstConvert;
import com.icatch.usbcam.data.propertyid.PropertyId;
import com.icatchtek.control.customer.ICatchCameraControl;
import com.icatchtek.control.customer.ICatchCameraProperty;
import com.icatchtek.control.customer.exception.IchCameraModeException;
import com.icatchtek.control.customer.exception.IchDevicePropException;
import com.icatchtek.control.customer.exception.IchNoSDCardException;
import com.icatchtek.control.customer.exception.IchStorageFormatException;
import com.icatchtek.control.customer.type.ICatchCamLightFrequency;
import com.icatchtek.control.customer.type.ICatchCamVideoRecordStatus;
import com.icatchtek.reliant.customer.exception.IchDeviceException;
import com.icatchtek.reliant.customer.exception.IchInvalidSessionException;
import com.icatchtek.reliant.customer.exception.IchSocketException;
import com.icatchtek.reliant.customer.exception.IchTryAgainException;
import com.icatchtek.reliant.customer.type.ICatchCodec;
import com.icatchtek.reliant.customer.type.ICatchVideoFormat;
import com.icatchtek.baseutil.log.AppLog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CameraProperties {
    private final String TAG = "CameraProperties";
    private List<Integer> fuction;
    // private PreviewStream previewStream = new PreviewStream();
    private List<Integer> modeList;
    private ICatchCameraProperty cameraProperty;
    private ICatchCameraControl cameraControl;

    public CameraProperties(ICatchCameraProperty cameraProperty, ICatchCameraControl cameraControl) {
        this.cameraControl = cameraControl;
        this.cameraProperty = cameraProperty;
    }

    public void resetClient(ICatchCameraProperty cameraProperty, ICatchCameraControl cameraControl){
        this.cameraControl = cameraControl;
        this.cameraProperty = cameraProperty;
    }

    public List<String> getSupportedImageSizes() {
        List<String> list = null;
        try {
            list = cameraProperty.getSupportedImageSizes();
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getSupportedImageSizes list.size =" + list.size());
        if (list != null) {
            for (int ii = 0; ii < list.size(); ii++) {
                AppLog.i(TAG, "image size ii=" + ii + " size=" + list.get(ii));
            }
        }
        return list;
    }

    public List<String> getSupportedVideoSizes() {
        AppLog.i(TAG, "begin getSupportedVideoSizes");
        List<String> list = null;
        try {
            list = cameraProperty.getSupportedVideoSizes();
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "begin getSupportedVideoSizes size =" + list.size());
        return list;
    }

    public List<Integer> getSupportedWhiteBalances() {
        AppLog.i(TAG, "begin getSupportedWhiteBalances");
        List<Integer> list = null;
        try {
            list = cameraProperty.getSupportedWhiteBalances();
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getSupportedWhiteBalances list=" + list);
        return list;
    }

    public List<Integer> getSupportedCaptureDelays() {
        AppLog.i(TAG, "begin getSupportedCaptureDelays");
        List<Integer> list = null;
        try {
            list = cameraProperty.getSupportedCaptureDelays();
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getSupportedCaptureDelays list.size() =" + list.size());
        for (Integer temp : list
                ) {
            AppLog.i(TAG, "end getSupportedCaptureDelays list value=" + temp);
        }
        return list;
    }

    public List<Integer> getSupportedLightFrequencys() {
        AppLog.i(TAG, "begin getSupportedLightFrequencys");
        List<Integer> list = null;

        try {
            list = cameraProperty.getSupportedLightFrequencies();
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // delete LIGHT_FREQUENCY_AUTO because UI don't need this option
        for (int ii = 0; ii < list.size(); ii++) {
            if (list.get(ii) == ICatchCamLightFrequency.ICH_CAM_LIGHT_FREQUENCY_AUTO) {
                list.remove(ii);
            }
        }
        AppLog.i(TAG, "end getSupportedLightFrequencys list.size() =" + list.size());
        return list;
    }

    public boolean setImageSize(String value) {
        AppLog.i(TAG, "begin setImageSize set value =" + value);
        boolean retVal = false;

        try {
            retVal = cameraProperty.setImageSize(value);
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setImageSize retVal=" + retVal);
        return retVal;
    }

    public boolean setVideoSize(String value) {
        AppLog.i(TAG, "begin setVideoSize set value =" + value);
        boolean retVal = false;

        try {
            retVal = cameraProperty.setVideoSize(value);
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setVideoSize retVal=" + retVal);
        return retVal;
    }

    public boolean setWhiteBalance(int value) {
        AppLog.i(TAG, "begin setWhiteBalanceset value =" + value);
        boolean retVal = false;
        if (value < 0 || value == 0xff) {
            return false;
        }
        try {
            retVal = cameraProperty.setWhiteBalance(value);
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setWhiteBalance retVal=" + retVal);
        return retVal;
    }

    public boolean setLightFrequency(int value) {
        AppLog.i(TAG, "begin setLightFrequency set value =" + value);
        boolean retVal = false;
        if (value < 0 || value == 0xff) {
            return false;
        }
        try {
//			retVal = cameraProperty.setLightFrequency(value);
            retVal = cameraProperty.setLightFrequency(value);
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setLightFrequency retVal=" + retVal);
        return retVal;
    }

    public String getCurrentImageSize() {
        AppLog.i(TAG, "begin getCurrentImageSize");
        String value = "unknown";

        try {
            value = cameraProperty.getCurrentImageSize();
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getCurrentImageSize value =" + value);
        return value;
    }

    public String getCurrentVideoSize() {
        AppLog.i(TAG, "begin getCurrentVideoSize");
        String value = "unknown";

        try {
            value = cameraProperty.getCurrentVideoSize();
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getCurrentVideoSize value =" + value);
        return value;
    }

    public int getCurrentWhiteBalance() {
        AppLog.i(TAG, "begin getCurrentWhiteBalance");
        int value = 0xff;
        try {
            value = cameraProperty.getCurrentWhiteBalance();
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getCurrentWhiteBalance retvalue =" + value);
        return value;
    }

    public int getCurrentLightFrequency() {
        AppLog.i(TAG, "begin getCurrentLightFrequency");
        int value = 0xff;
        try {
            value = cameraProperty.getCurrentLightFrequency();
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getCurrentLightFrequency value =" + value);
        return value;
    }

    public boolean setCaptureDelay(int value) {
        AppLog.i(TAG, "begin setCaptureDelay set value =" + value);
        boolean retVal = false;

        try {
            AppLog.i(TAG, "start setCaptureDelay ");
            retVal = cameraProperty.setCaptureDelay(value);
            AppLog.i(TAG, "end setCaptureDelay ");
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setCaptureDelay retVal =" + retVal);
        return retVal;
    }

    public int getCurrentCaptureDelay() {
        AppLog.i(TAG, "begin getCurrentCaptureDelay");
        int retVal = 0;

        try {
            retVal = cameraProperty.getCurrentCaptureDelay();
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getCurrentCaptureDelay retVal =" + retVal);
        return retVal;
    }

    public int getCurrentDateStamp() {
        AppLog.i(TAG, "begin getCurrentDateStampType");
        int retValue = 0;
        try {
            retValue = cameraProperty.getCurrentDateStamp();
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "getCurrentDateStampType retValue =" + retValue);
        return retValue;
    }

    /**
     * Added by zhangyanhu C01012,2014-4-1
     */
    public boolean setDateStamp(int dateStamp) {
        AppLog.i(TAG, "begin setDateStampType set value = " + dateStamp);
        Boolean retValue = false;
        try {
            retValue = cameraProperty.setDateStamp(dateStamp);
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getCurrentVideoSize retValue =" + retValue);
        return retValue;
    }

    /**
     * Added by zhangyanhu C01012,2014-4-1
     */
    public List<Integer> getDateStampList() {
        AppLog.i(TAG, "begin getDateStampList");
        List<Integer> list = null;
        try {
            list = cameraProperty.getSupportedDateStamps();
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getDateStampList list.size ==" + list.size());
        return list;
    }

    public List<Integer> getSupportFuction() {
        AppLog.i(TAG, "begin getSupportFuction");
        List<Integer> fuction = null;
        // List<Integer> temp = null;
        try {
            fuction = cameraProperty.getSupportedProperties();
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getSupportFuction fuction.size() =" + fuction.size());
        if(fuction !=null && fuction.size()>0){
            for (int functionId:fuction
                 ) {
                AppLog.i(TAG, "end getSupportFuction id=" + functionId);
            }
        }
        return fuction;
    }

    /**
     * to prase the burst number Added by zhangyanhu C01012,2014-2-10
     */
    public int getCurrentBurstNum() {
        AppLog.i(TAG, "begin getCurrentBurstNum");
        int number = 0xff;
        try {
            number = cameraProperty.getCurrentBurstNumber();
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "getCurrentBurstNum num =" + number);
        return number;
    }

    public int getCurrentAppBurstNum() {
        AppLog.i(TAG, "begin getCurrentAppBurstNum");
        int number = 0xff;
        try {
            number = cameraProperty.getCurrentBurstNumber();
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        number = BurstConvert.getInstance().getBurstConverFromFw(number);
        AppLog.i(TAG, "getCurrentAppBurstNum num =" + number);
        return number;
    }

    public boolean setCurrentBurst(int burstNum) {
        AppLog.i(TAG, "begin setCurrentBurst set value = " + burstNum);
        if (burstNum < 0 || burstNum == 0xff) {
            return false;
        }
        boolean retValue = false;
        try {
            retValue = cameraProperty.setBurstNumber(burstNum);
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setCurrentBurst retValue =" + retValue);
        return retValue;
    }

    public int getRemainImageNum() {
        AppLog.i(TAG, "begin getRemainImageNum");
        int num = 0;
        try {
            num = cameraControl.getFreeSpaceInImages();
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchNoSDCardException e) {
            AppLog.e(TAG, "IchNoSDCardException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getRemainImageNum num =" + num);
        return num;
    }

    public int getRecordingRemainTime() {
        AppLog.i(TAG, "begin getRecordingRemainTimeInt");
        int recordingTime = 0;

        try {
            recordingTime = cameraControl.getRemainRecordingTime();
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchNoSDCardException e) {
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getRecordingRemainTimeInt recordingTime =" + recordingTime);
        return recordingTime;
    }

    public boolean isSDCardExist() {
        AppLog.i(TAG, "begin isSDCardExist");
        Boolean isReady = false;
        try {
            isReady = cameraControl.isSDCardExist();
        } catch (IchSocketException e) {
            AppLog.e(TAG,
                    "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG,
                    "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG,
                    "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDeviceException e) {
            AppLog.e(TAG, "IchDeviceException");
            e.printStackTrace();
        }
        AppLog.i(TAG, "end isSDCardExist isReady =" + isReady);
        return isReady;

        //return GlobalInfo.getInstance().getCurrentCamera().isSdCardReady();
        // JIRA ICOM-1577 End:Modify by b.jiang C01063 2015-07-17
    }

    public int getBatteryElectric() {
        AppLog.i(TAG, "start getBatteryElectric");
        int electric = 0;
        try {
            electric = cameraControl.getCurrentBatteryLevel();
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getBatteryElectric electric =" + electric);
        return electric;
    }

    public boolean supportVideoPlayback() {
        AppLog.i(TAG, "begin hasVideoPlaybackFuction");
        boolean retValue = false;
        try {
            retValue = cameraControl.supportVideoPlayback();
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchNoSDCardException e) {
            AppLog.e(TAG, "IchNoSDCardException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "hasVideoPlaybackFuction retValue =" + retValue);
        return retValue;
        // return false;
    }

    public boolean cameraModeSupport(int mode) {
        AppLog.i(TAG, "begin cameraModeSupport  mode=" + mode);
        Boolean retValue = false;
        if (modeList == null) {
            modeList = getSupportedModes();
        }
//        modeList = getSupportedModes();
        if (modeList.contains(mode)) {
            retValue = true;
        }
        AppLog.i(TAG, "end cameraModeSupport retValue =" + retValue);
        return retValue;
    }

    public String getCameraMacAddress() {
        AppLog.i(TAG, "begin getCameraMacAddress macAddress macAddress ");
        String macAddress = cameraControl.getMacAddress();
        AppLog.i(TAG, "end getCameraMacAddress macAddress =" + macAddress);
        return macAddress;
    }

    public boolean hasFuction(int fuc) {
        AppLog.i(TAG, "begin hasFuction query fuction = " + fuc);
        if (fuction == null) {
            fuction = getSupportFuction();
        }
        Boolean retValue = false;
        if (fuction.contains(fuc) == true) {
            retValue = true;
        }
        AppLog.i(TAG, "end hasFuction retValue =" + retValue);
        return retValue;
    }

    /**
     * Added by zhangyanhu C01012,2014-7-4
     */
    public List<Integer> getsupportedDateStamps() {
        AppLog.i(TAG, "begin getsupportedDateStamps");
        List<Integer> list = null;

        try {
            list = cameraProperty.getSupportedDateStamps();
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getsupportedDateStamps list.size() =" + list.size());
        return list;
    }

    public List<Integer> getsupportedBurstNums() {
        // TODO Auto-generated method stub
        AppLog.i(TAG, "begin getsupportedBurstNums");
        List<Integer> list = null;

        try {
            list = cameraProperty.getSupportedBurstNumbers();
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getsupportedBurstNums list.size() =" + list.size());
        return list;
    }

    /**
     * Added by zhangyanhu C01012,2014-7-4
     */
    public List<Integer> getSupportedFrequencies() {
        // TODO Auto-generated method stub
        AppLog.i(TAG, "begin getSupportedFrequencies");
        List<Integer> list = null;
        try {
            list = cameraProperty.getSupportedLightFrequencies();
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getSupportedFrequencies list.size() =" + list.size());
        return list;
    }

    /**
     * Added by zhangyanhu C01012,2014-8-21
     *
     * @return
     */
    public List<Integer> getSupportedModes() {
        AppLog.i(TAG, "begin getSupportedModes");

        List<Integer> list = null;
        try {
            list = cameraControl.getSupportedModes();
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
        AppLog.i(TAG, "end getSupportedModes list =" + list);
        return list;
    }

    public List<Integer> getSupportedTimeLapseDurations() {
        AppLog.i(TAG, "begin getSupportedTimeLapseDurations");
        List<Integer> list = null;
        // boolean retValue = false;
        try {
            list = cameraProperty.getSupportedTimeLapseDurations();
        } catch (IchSocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        for (int ii = 0; ii < list.size(); ii++) {
            AppLog.i(TAG, "list.get(ii) =" + list.get(ii));
        }
        AppLog.i(TAG, "end getSupportedTimeLapseDurations list =" + list.size());
        return list;
    }

    public List<Integer> getSupportedTimeLapseIntervals() {
        AppLog.i(TAG, "begin getSupportedTimeLapseIntervals");
        List<Integer> list = null;
        // boolean retValue = false;
        try {
            list = cameraProperty.getSupportedTimeLapseIntervals();
        } catch (IchSocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        for (int ii = 0; ii < list.size(); ii++) {
            AppLog.i(TAG, "list.get(ii) =" + list.get(ii));
        }
        AppLog.i(TAG, "end getSupportedTimeLapseIntervals list =" + list.size());
        return list;
    }

    public boolean setTimeLapseDuration(int timeDuration) {
        AppLog.i(TAG, "begin setTimeLapseDuration videoDuration =" + timeDuration);
        boolean retVal = false;
        if (timeDuration < 0 || timeDuration == 0xff) {
            return false;
        }
        try {
            retVal = cameraProperty.setTimeLapseDuration(timeDuration);
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setTimeLapseDuration retVal=" + retVal);
        return retVal;
    }

    public int getCurrentTimeLapseDuration() {
        AppLog.i(TAG, "begin getCurrentTimeLapseDuration");
        int retVal = 0xff;
        try {
            retVal = cameraProperty.getCurrentTimeLapseDuration();
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getCurrentTimeLapseDuration retVal=" + retVal);
        return retVal;
    }

    public boolean setTimeLapseInterval(int timeInterval) {
        AppLog.i(TAG, "begin setTimeLapseInterval videoDuration =" + timeInterval);
        boolean retVal = false;
//		if (timeInterval < 0 || timeInterval == 0xff) {
//			return false;
//		}
        try {
            retVal = cameraProperty.setTimeLapseInterval(timeInterval);
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setTimeLapseInterval retVal=" + retVal);
        return retVal;
    }

    public int getCurrentTimeLapseInterval() {
        AppLog.i(TAG, "begin getCurrentTimeLapseInterval");
        int retVal = 0xff;
        try {
            retVal = cameraProperty.getCurrentTimeLapseInterval();
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getCurrentTimeLapseInterval retVal=" + retVal);
        return retVal;
    }

    public float getMaxZoomRatio() {
        AppLog.i(TAG, "start getMaxZoomRatio");
        float retValue = 0;
        try {
            retValue = cameraProperty.getMaxZoomRatio() / 10.0f;
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getMaxZoomRatio retValue =" + retValue);
        return retValue;
    }

    public float getCurrentZoomRatio() {
        AppLog.i(TAG, "start getCurrentZoomRatio");
        float retValue = 0;
        try {
            retValue = cameraProperty.getCurrentZoomRatio() / 10.0f;
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getCurrentZoomRatio retValue =" + retValue);
        return retValue;
    }

    public int getCurrentUpsideDown() {
        AppLog.i(TAG, "start getCurrentUpsideDown");
        int retValue = 0;
        try {
            retValue = cameraProperty.getCurrentUpsideDown();
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getCurrentUpsideDown retValue =" + retValue);
        return retValue;
    }

    public boolean setUpsideDown(int upside) {
        AppLog.i(TAG, "start setUpsideDown upside = " + upside);
        boolean retValue = false;
        try {
            retValue = cameraProperty.setUpsideDown(upside);
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setUpsideDown retValue =" + retValue);
        return retValue;
    }

    public int getCurrentSlowMotion() {
        AppLog.i(TAG, "start getCurrentSlowMotion");
        int retValue = 0;
        try {
            retValue = cameraProperty.getCurrentSlowMotion();
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getCurrentSlowMotion retValue =" + retValue);
        return retValue;
    }

    public boolean setSlowMotion(int slowMotion) {
        AppLog.i(TAG, "start setSlowMotion slowMotion = " + slowMotion);
        boolean retValue = false;
        try {
            retValue = cameraProperty.setSlowMotion(slowMotion);
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setSlowMotion retValue =" + retValue);
        return retValue;
    }

    public boolean setCameraDate() {
        long time = System.currentTimeMillis();
        Date date = new Date(time);
        SimpleDateFormat myFmt = new SimpleDateFormat("yyyyMMdd HHmmss");
        String tempDate = myFmt.format(date);
        tempDate = tempDate.replaceAll(" ", "T");
        tempDate = tempDate + ".0";
        AppLog.i(TAG, "start setCameraDate date = " + tempDate);
        boolean retValue = false;
        try {
            retValue = cameraProperty.setStringPropertyValue(PropertyId.CAMERA_DATE, tempDate);
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setCameraDate retValue =" + retValue);
        return retValue;
    }

    public boolean setCameraEssidName(String ssidName) {
        AppLog.i(TAG, "start setCameraEssidName date = " + ssidName);
        boolean retValue = false;
        try {
            retValue = cameraProperty.setStringPropertyValue(PropertyId.ESSID_NAME, ssidName);
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setCameraEssidName retValue =" + retValue);
        return retValue;
    }

    public String getCameraEssidName() {
        AppLog.i(TAG, "start getCameraEssidName");
        String retValue = null;
        try {
            retValue = cameraProperty.getCurrentStringPropertyValue(PropertyId.ESSID_NAME);
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getCameraEssidName retValue =" + retValue);
        return retValue;
    }

    public String getCameraEssidPassword() {
        AppLog.i(TAG, "start getCameraEssidPassword");
        String retValue = null;
        try {
            retValue = cameraProperty.getCurrentStringPropertyValue(PropertyId.ESSID_PASSWORD);
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getCameraEssidPassword retValue =" + retValue);
        return retValue;
    }

    public boolean setCameraEssidPassword(String ssidPassword) {
        AppLog.i(TAG, "start setStringPropertyValue date = " + ssidPassword);
        boolean retValue = false;
        try {
            retValue = cameraProperty.setStringPropertyValue(PropertyId.ESSID_PASSWORD, ssidPassword);
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setCameraSsid retValue =" + retValue);
        return retValue;
    }

    public boolean setCameraSsid(String ssid) {
        AppLog.i(TAG, "start setCameraSsid date = " + ssid);
        boolean retValue = false;
        try {
            retValue = cameraProperty.setStringPropertyValue(PropertyId.CAMERA_ESSID, ssid);
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setCameraSsid retValue =" + retValue);
        return retValue;
    }

    public boolean setCameraName(String cameraName) {
        AppLog.i(TAG, "start setStringPropertyValue cameraName = " + cameraName);
        boolean retValue = false;
        try {
            retValue = cameraProperty.setStringPropertyValue(PropertyId.CAMERA_NAME, cameraName);
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setStringPropertyValue retValue =" + retValue);
        return retValue;
    }

    public String getCameraName() {
        AppLog.i(TAG, "start getCameraName");
        String retValue = null;
        try {
            retValue = cameraProperty.getCurrentStringPropertyValue(PropertyId.CAMERA_NAME);
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getCameraName retValue =" + retValue);
        return retValue;
    }

    public String getCameraName(ICatchCameraProperty cameraConfiguration1) {
        AppLog.i(TAG, "start getCameraName");
        String retValue = null;
        try {
            retValue = cameraConfiguration1.getCurrentStringPropertyValue(PropertyId.CAMERA_NAME);
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getCameraName retValue =" + retValue);
        return retValue;
    }

    public String getCameraPasswordNew() {
        AppLog.i(TAG, "start getCameraPassword");
        String retValue = null;
        try {
            retValue = cameraProperty.getCurrentStringPropertyValue(PropertyId.CAMERA_PASSWORD_NEW);
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getCameraPassword retValue =" + retValue);
        return retValue;
    }

    public boolean setCameraPasswordNew(String cameraNamePassword) {
        AppLog.i(TAG, "start setCameraPasswordNew cameraName = " + cameraNamePassword);
        boolean retValue = false;
        try {
            retValue = cameraProperty.setStringPropertyValue(PropertyId.CAMERA_PASSWORD_NEW, cameraNamePassword);
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setCameraPasswordNew retValue =" + retValue);
        return retValue;
    }

    public String getCameraSsid() {
        AppLog.i(TAG, "start getCameraSsid date = ");
        String retValue = null;
        try {
            retValue = cameraProperty.getCurrentStringPropertyValue(PropertyId.CAMERA_ESSID);
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getCameraSsid retValue =" + retValue);
        return retValue;
    }

    public boolean setCameraPassword(String password) {
        AppLog.i(TAG, "start setCameraSsid date = " + password);
        boolean retValue = false;
        try {
            retValue = cameraProperty.setStringPropertyValue(PropertyId.CAMERA_PASSWORD, password);
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setCameraSsid retValue =" + retValue);
        return retValue;
    }

    public String getCameraPassword() {
        AppLog.i(TAG, "start getCameraPassword date = ");
        String retValue = null;
        try {
            retValue = cameraProperty.getCurrentStringPropertyValue(PropertyId.CAMERA_PASSWORD);
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getCameraPassword retValue =" + retValue);
        return retValue;
    }

    public boolean setCaptureDelayMode(int value) {
        AppLog.i(TAG, "start setCaptureDelayMode value = " + value);
        boolean retValue = false;
        try {
            retValue = cameraProperty.setPropertyValue(PropertyId.CAPTURE_DELAY_MODE, value);
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setCaptureDelayMode retValue =" + retValue);
        return retValue;
    }



    public boolean setServicePassword(String value) {
        AppLog.i(TAG, "start setServicePassword value = " + value);
        boolean retValue = false;
        try {
            retValue = cameraProperty.setStringPropertyValue(PropertyId.SERVICE_PASSWORD, value);
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setServicePassword retValue =" + retValue);
        return retValue;
    }

    public boolean notifyFwToShareMode(int value) {
        AppLog.i(TAG, "start notifyFwToShareMode value = " + value);
        boolean retValue = false;
        try {
            retValue = cameraProperty.setPropertyValue(PropertyId.NOTIFY_FW_TO_SHARE_MODE, value);
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end notifyFwToShareMode retValue =" + retValue);
        return retValue;
    }

    public List<Integer> getSupportedPropertyValues(int propertyId) {
        AppLog.i(TAG, "begin getSupportedPropertyValues propertyId =" + propertyId);
        List<Integer> list = null;
        try {
            list = cameraProperty.getSupportedPropertyValues(propertyId);
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getSupportedPropertyValues list.size()=" + list.size());
        AppLog.i(TAG, "end getSupportedPropertyValues list=" + list);
        return list;
    }

    public int getCurrentPropertyValue(int propertyId) {
        AppLog.i(TAG, "start getCurrentPropertyValue propertyId = " + propertyId);
        int retValue = 0;
        try {
            retValue = cameraProperty.getCurrentPropertyValue(propertyId);
        } catch (IchTryAgainException e) {
            //重新获取一次
            AppLog.e(TAG, "IchTryAgainException");
            try {
                Thread.sleep(50);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            try {
                retValue = cameraProperty.getCurrentPropertyValue(propertyId);
            } catch (Exception e1) {
                AppLog.e(TAG, "Again get Exception e:" + e.getClass().getSimpleName());
                e1.printStackTrace();
            }
        } catch (Exception e) {
            AppLog.e(TAG, "Exception e:" + e.getClass().getSimpleName());
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getCurrentPropertyValue retValue =" + retValue);
        return retValue;
    }

    public String getCurrentStringPropertyValue(int propertyId) {
        AppLog.i(TAG, "start getCurrentStringPropertyValue propertyId = " + propertyId);
        String retValue = null;
        try {
            retValue = cameraProperty.getCurrentStringPropertyValue(propertyId);
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getCurrentStringPropertyValue retValue =" + retValue);
        return retValue;
    }

    public boolean setPropertyValue(int propertyId, int value) {
        AppLog.i(TAG, "start setPropertyValue propertyId=" + propertyId + " value=" + value);
        boolean retValue = false;
        try {
            retValue = cameraProperty.setPropertyValue(propertyId, value);
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setPropertyValue retValue =" + retValue);
        return retValue;
    }

    public boolean setStringPropertyValue(int propertyId, String value) {
        AppLog.i(TAG, "start setStringPropertyValue propertyId=" + propertyId + " value=[" + value + "]");
        boolean retValue = false;
        try {
            retValue = cameraProperty.setStringPropertyValue(propertyId, value);
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setStringPropertyValue retValue =" + retValue);
        return retValue;
    }

    public boolean notifyCameraConnectChnage(int value) {
        AppLog.i(TAG, "start notifyCameraConnectChnage value = " + value);
        boolean retValue = false;
        try {
            retValue = cameraProperty.setPropertyValue(PropertyId.CAMERA_CONNECT_CHANGE, value);
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end notifyCameraConnectChnage retValue =" + retValue);
        return retValue;
    }

    public List<ICatchVideoFormat> getSupportedStreamingInfos() {
        AppLog.i(TAG, "start getSupportedStreamingInfos");
        List<ICatchVideoFormat> retList = null;
        try {
            retList = cameraProperty.getSupportedStreamingInfos();
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getSupportedStreamingInfos retList.size() =" + retList.size());
        if(retList != null && retList.size() >0){
            for (ICatchVideoFormat temp: retList
                 ) {
                AppLog.i(TAG, "end getSupportedStreamingInfos videoInfo:" + temp.toString());
            }
        }
        return retList;
    }

    public String getBestResolution() {
        AppLog.i(TAG, "start getBestResolution");
        String bestResolution = null;

        List<ICatchVideoFormat> tempList = getResolutionList(cameraProperty);
        if (tempList == null || tempList.size() == 0) {
            return null;
        }
        Log.d("1111", "getResolutionList() tempList.size() = " + tempList.size());
        int tempWidth = 0;
        int tempHeigth = 0;

        ICatchVideoFormat temp;

        for (int ii = 0; ii < tempList.size(); ii++) {
            temp = tempList.get(ii);
            if (temp.getCodec() == ICatchCodec.ICH_CODEC_H264) {
                if (bestResolution == null) {
                    bestResolution = "H264?" + "W=" + temp.getVideoW() + "&H=" + temp.getVideoH() + "&BR=" + temp.getBitrate() + "&";
                }

                if (temp.getVideoW() == 640 && temp.getVideoH() == 360) {
                    bestResolution = "H264?" + "W=" + temp.getVideoW() + "&H=" + temp.getVideoH() + "&BR=" + temp.getBitrate() + "&";
                    return bestResolution;
                } else if (temp.getVideoW() == 640 && temp.getVideoH() == 480) {
                    if (tempWidth != 640) {
                        bestResolution = "H264?" + "W=" + temp.getVideoW() + "&H=" + temp.getVideoH() + "&BR=" + temp.getBitrate() + "&";
                        tempWidth = 640;
                        tempHeigth = 480;
                    }
                } else if (temp.getVideoW() == 720) {
                    if (tempWidth != 640) {
                        if (temp.getVideoW() * 9 == temp.getVideoH() * 16)// 16:9优先
                        {
                            bestResolution = "H264?" + "W=" + temp.getVideoW() + "&H=" + temp.getVideoH() + "&BR=" + temp.getBitrate() + "&";
                            tempWidth = 720;
                            tempHeigth = temp.getVideoH();
                        } else if (temp.getVideoW() * 3 == temp.getVideoH() * 4)// 4:3
                        {
                            if (tempWidth != 720)
                                bestResolution = "H264?" + "W=" + temp.getVideoW() + "&H=" + temp.getVideoH() + "&BR=" + temp.getBitrate() + "&";
                            tempWidth = 720;
                            tempHeigth = temp.getVideoH();
                        }
                    }
                } else if (temp.getVideoW() < tempWidth) {
                    if (temp.getVideoW() * 9 == temp.getVideoH() * 16)// 16:9优先
                    {
                        bestResolution = "H264?" + "W=" + temp.getVideoW() + "&H=" + temp.getVideoH() + "&BR=" + temp.getBitrate() + "&";
                        tempWidth = temp.getVideoW();
                        tempHeigth = temp.getVideoH();
                    } else if (temp.getVideoW() * 3 == temp.getVideoH() * 4)// 4:3
                    {
                        if (tempWidth != temp.getVideoW())
                            bestResolution = "H264?" + "W=" + temp.getVideoW() + "&H=" + temp.getVideoH() + "&BR=" + temp.getBitrate() + "&";
                        tempWidth = temp.getVideoW();
                        tempHeigth = temp.getVideoH();
                    }
                }
            }
        }
        if (bestResolution != null) {
            return bestResolution;
        }
        for (int ii = 0; ii < tempList.size(); ii++) {
            temp = tempList.get(ii);
            if (temp.getCodec() == ICatchCodec.ICH_CODEC_JPEG) {
                if (bestResolution == null) {
                    bestResolution = "MJPG?" + "W=" + temp.getVideoW() + "&H=" + temp.getVideoH() + "&BR=" + temp.getBitrate() + "&";
                }

                if (temp.getVideoW() == 640 && temp.getVideoH() == 360) {
                    bestResolution = "MJPG?" + "W=" + temp.getVideoW() + "&H=" + temp.getVideoH() + "&BR=" + temp.getBitrate() + "&";
                    return bestResolution;
                } else if (temp.getVideoW() == 640 && temp.getVideoH() == 480) {
                    if (tempWidth != 640) {
                        bestResolution = "MJPG?" + "W=" + temp.getVideoW() + "&H=" + temp.getVideoH() + "&BR=" + temp.getBitrate() + "&";
                        tempWidth = 640;
                        tempHeigth = 480;
                    }
                } else if (temp.getVideoW() == 720) {
                    if (tempWidth != 640) {
                        if (temp.getVideoW() * 9 == temp.getVideoH() * 16)// 16:9优先
                        {
                            bestResolution = "MJPG?" + "W=" + temp.getVideoW() + "&H=" + temp.getVideoH() + "&BR=" + temp.getBitrate() + "&";
                            tempWidth = 720;
                            tempHeigth = temp.getVideoH();
                        } else if (temp.getVideoW() * 3 == temp.getVideoH() * 4)// 4:3
                        {
                            if (tempWidth != 720)
                                bestResolution = "MJPG?" + "W=" + temp.getVideoW() + "&H=" + temp.getVideoH() + "&BR=" + temp.getBitrate() + "&";
                            tempWidth = 720;
                            tempHeigth = temp.getVideoH();
                        }
                    }
                } else if (temp.getVideoW() < tempWidth) {
                    if (temp.getVideoW() * 9 == temp.getVideoH() * 16)// 16:9优先
                    {
                        bestResolution = "MJPG?" + "W=" + temp.getVideoW() + "&H=" + temp.getVideoH() + "&BR=" + temp.getBitrate() + "&";
                        tempWidth = temp.getVideoW();
                        tempHeigth = temp.getVideoH();
                    } else if (temp.getVideoW() * 3 == temp.getVideoH() * 4)// 4:3
                    {
                        if (tempWidth != temp.getVideoW())
                            bestResolution = "MJPG?" + "W=" + temp.getVideoW() + "&H=" + temp.getVideoH() + "&BR=" + temp.getBitrate() + "&";
                        tempWidth = temp.getVideoW();
                        tempHeigth = temp.getVideoH();
                    }
                }
            }
        }

        AppLog.i(TAG, "end getBestResolution");
        return bestResolution;

    }

    public List<ICatchVideoFormat> getResolutionList(ICatchCameraProperty cameraConfiguration) {
        AppLog.i(TAG, "start getResolutionList");
        List<ICatchVideoFormat> retList = null;
        try {
            retList = cameraConfiguration.getSupportedStreamingInfos();
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getResolutionList retList =" + retList);
        for (int ii = 0; ii < retList.size(); ii++) {
            Log.d("1111", " retList.get(ii)==" + retList.get(ii));
        }

        return retList;
    }

    public String getAppDefaultResolution() {
        AppLog.i(TAG, "start getAppDefaultResolution");
        String appDefaultResolution = null;

        List<ICatchVideoFormat> tempList = getResolutionList(cameraProperty);
        if (tempList == null || tempList.size() == 0) {
            return null;
        }
        Log.d("1111", "getResolutionList() tempList.size() = " + tempList.size());

        ICatchVideoFormat temp;

        for (int ii = 0; ii < tempList.size(); ii++) {
            temp = tempList.get(ii);

            if (temp.getCodec() == ICatchCodec.ICH_CODEC_H264) {
                if (temp.getVideoW() == 1280 && temp.getVideoH() == 720 && temp.getBitrate() == 500000) {
                    appDefaultResolution = "H264?" + "W=" + temp.getVideoW() + "&H=" + temp.getVideoH() + "&BR=" + temp.getBitrate() + "&";
                    return appDefaultResolution;
                }
            }
        }

        AppLog.i(TAG, "end getAppDefaultResolution");
        return appDefaultResolution;

    }

    public String getFWDefaultResolution() {
        AppLog.i(TAG, "start getFWDefaultResolution");
        String resolution = null;
        ICatchVideoFormat retValue = null;
        try {
            retValue = cameraProperty.getCurrentStreamingInfo();
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (retValue != null) {
            if (retValue.getCodec() == ICatchCodec.ICH_CODEC_H264) {
                resolution = "H264?" + "W=" + retValue.getVideoW() + "&H=" + retValue.getVideoH() + "&BR=" + retValue.getBitrate() + "&";
            } else if (retValue.getCodec() == ICatchCodec.ICH_CODEC_JPEG) {
                resolution = "MJPG?" + "W=" + retValue.getVideoW() + "&H=" + retValue.getVideoH() + "&BR=" + retValue.getBitrate() + "&";
            }
        }
        AppLog.i(TAG, "end getFWDefaultResolution retValue=" + retValue);
        AppLog.i(TAG, "end getFWDefaultResolution resolution=" + resolution);
        return resolution;

    }

    public boolean setStreamingInfo(ICatchVideoFormat iCatchVideoFormat) {
        AppLog.i(TAG, "start setStreamingInfo");
        boolean retValue = false;
        try {
            retValue = cameraProperty.setStreamingInfo(iCatchVideoFormat);
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setStreamingInfo");
        return retValue;

    }

    public String getCurrentStreamInfo() {
        AppLog.i(TAG, "start getCurrentStreamInfo cameraProperty=" + cameraProperty);

        ICatchVideoFormat retValue = null;
        String bestResolution = null;
        try {
            retValue = cameraProperty.getCurrentStreamingInfo();
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (retValue == null) {
            AppLog.i(TAG, "end getCurrentStreamInfo retValue = " + retValue);
            return null;
        }
        if (hasFuction(0xd7ae)) {
            if (retValue.getCodec() == ICatchCodec.ICH_CODEC_H264) {
                bestResolution = "H264?" + "W=" + retValue.getVideoW() + "&H=" + retValue.getVideoH() + "&BR=" + retValue.getBitrate() + "&FPS="
                        + retValue.getFrameRate() + "&";
            } else if (retValue.getCodec() == ICatchCodec.ICH_CODEC_JPEG) {
                bestResolution = "MJPG?" + "W=" + retValue.getVideoW() + "&H=" + retValue.getVideoH() + "&BR=" + retValue.getBitrate() + "&FPS="
                        + retValue.getFrameRate() + "&";
            }
        } else {
            if (retValue.getCodec() == ICatchCodec.ICH_CODEC_H264) {
                bestResolution = "H264?" + "W=" + retValue.getVideoW() + "&H=" + retValue.getVideoH() + "&BR=" + retValue.getBitrate();
            } else if (retValue.getCodec() == ICatchCodec.ICH_CODEC_JPEG) {
                bestResolution = "MJPG?" + "W=" + retValue.getVideoW() + "&H=" + retValue.getVideoH() + "&BR=" + retValue.getBitrate();
            }
        }


        AppLog.i(TAG, "end getCurrentStreamInfo bestResolution =" + bestResolution);
        return bestResolution;
    }

    public int getPreviewCacheTime() {
        AppLog.i(TAG, "start getPreviewCacheTime");
        int retValue = 0;
        try {
            retValue = cameraProperty.getPreviewCacheTime();
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
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getPreviewCacheTime retValue =" + retValue);
        return retValue;
    }

    public boolean isSupportPreview() {
        AppLog.i(TAG, "start getRecordingTime");
        int retValue = 0;
        boolean isSupport = false;
        try {
            retValue = cameraProperty.getCurrentPropertyValue(PropertyId.SUPPORT_PREVIEW);
        } catch (Exception e) {
            AppLog.e(TAG, "Exception:" + e.getClass().getSimpleName());
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getRecordingTime retValue =" + retValue);
        if (retValue == 0) {
            isSupport = false;
        } else {
            isSupport = true;
        }
        return isSupport;
    }

    public boolean setSeamless(int value){
        AppLog.i(TAG, "begin setSeamless");
        boolean ret = false;
        try {
            ret = cameraProperty.setSeamless(value);
        } catch (Exception e) {
            AppLog.e(TAG, "Exception e= " + e.getClass().getSimpleName());
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setSeamless ret = " + ret);
        return ret;
    }

    public int getCurrentSeamless(){
        AppLog.i(TAG, "begin getCurrentSeamless");
        int ret = -1;
        try {
            ret = cameraProperty.getCurrentSeamless();
        } catch (Exception e) {
            AppLog.e(TAG, "Exception e= " + e.getClass().getSimpleName());
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getCurrentSeamless ret = " + ret);
        return ret;
    }


    public List<Integer> getSupportedSeamlesses(){
        AppLog.i(TAG, "begin getSupportedSeamlesses");
        List<Integer> ret = null;
        try {
            ret = cameraProperty.getSupportedSeamlesses();
        } catch (Exception e) {
            AppLog.e(TAG, "Exception e= " + e.getClass().getSimpleName());
            e.printStackTrace();
        }

        AppLog.i(TAG, "end getSupportedSeamlesses ret = " + ret);
        if(ret == null){
            return  null;
        }
        return ret;
    }


    public ICatchCamVideoRecordStatus getVideoRecordStatus(){
        if (cameraControl == null) {
            return null;
        }
        ICatchCamVideoRecordStatus videoRecordStatus = null;
        try {
            videoRecordStatus = cameraControl.getVideoRecordStatus();
        } catch (IchSocketException e1) {
            e1.printStackTrace();
        } catch (IchCameraModeException e1) {
            e1.printStackTrace();
        } catch (IchStorageFormatException e1) {
            e1.printStackTrace();
        } catch (IchInvalidSessionException e1) {
            e1.printStackTrace();
        }
        if(videoRecordStatus != null){
//            AppLog.i(TAG,"getVideoRecordStatus status: " + videoRecordStatus.toString());
        }else {
            AppLog.i(TAG,"getVideoRecordStatus is null!");
        }

        return videoRecordStatus;
    }

    public int getRecodeTime(ICatchCamVideoRecordStatus videoRecordStatus){
        if(videoRecordStatus == null)
        {
            return 0;
        }
        int time = videoRecordStatus.getHours() * 60* 60 + videoRecordStatus.getMinutes() * 60  + videoRecordStatus.getSeconds();
        return time;
    }
}
