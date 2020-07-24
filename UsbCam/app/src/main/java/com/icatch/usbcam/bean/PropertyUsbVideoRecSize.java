package com.icatch.usbcam.bean;

import android.util.Log;

import com.icatch.usbcam.data.propertyid.PropertyId;
import com.icatch.usbcam.sdkapi.CameraProperties;
import com.icatchtek.reliant.customer.type.ICatchVideoFormat;

import java.util.HashMap;
import java.util.List;

/**
 * @author b.jiang
 * @date 2018/8/30
 * @description
 */
public class PropertyUsbVideoRecSize {
    private  int propertyId = PropertyId.USB_VIDEO_REC_SIZE;
    private String TAG = PropertyUsbPvSize.class.getSimpleName();
    private String[] valueArrayString;
    private CameraProperties cameraProperties;
    private HashMap<Integer, String> hashMap;
    private int curIndex;

    public PropertyUsbVideoRecSize(CameraProperties cameraProperties) {
        this.cameraProperties = cameraProperties;
        initItem();
    }

    public void initItem() {
        // TODO Auto-generated method stub
        List<Integer> videoList = cameraProperties.getSupportedPropertyValues(propertyId);
        if(videoList == null || videoList.size()==0){
            return;
        }
        int length = videoList.size();
        valueArrayString = new String[length/2];
        hashMap = new HashMap<>();
        for (int ii = 0; ii < length; ii = ii+2) {
            String temp = videoList.get(ii) + "*" + videoList.get(ii+1);
            valueArrayString[ii/2] = temp;
            hashMap.put(ii/2,temp);
            Log.d(TAG,"video pv size:" + temp);
        }

    }

    public String getCurrentValue() {
        // TODO Auto-generated method stub
        int index = cameraProperties.getCurrentPropertyValue(propertyId);
        if(index < 0 || index > valueArrayString.length-1){
            return "unknown";
        }
        return valueArrayString[index];
    }

    public int getCurrentIndex() {
        // TODO Auto-generated method stub
        int index = cameraProperties.getCurrentPropertyValue(propertyId);
        return index;
    }

    public boolean setValue(int index) {
        // TODO Auto-generated method stub
        if(index <0 || index > valueArrayString.length-1){
            return false;
        }
        if(index == curIndex){
            return true;
        }
        boolean ret = cameraProperties.setPropertyValue(propertyId,index);
        return ret;
    }

    public boolean setValueByPosition(int index) {
        if(index <0 || index > valueArrayString.length-1){
            return false;
        }

        if(index == curIndex){
            return true;
        }
        boolean ret = cameraProperties.setPropertyValue(propertyId,index);
        if(ret){
            curIndex =index;
        }
        return ret;

    }

    public String[] getValueArrayString() {
        return valueArrayString;
    }

    public String videoFormatToString(ICatchVideoFormat videoFormat){
        if(videoFormat == null){
            return null;
        }
        String temp = videoFormat.getVideoW() + "*" + videoFormat.getVideoH();
        return temp;
    }
}
