package com.icatch.usbcam.bean;

import android.content.Context;
import android.content.res.Resources;

import com.icatch.usbcam.data.propertyid.PropertyId;
import com.icatch.usbcam.data.hash.PropertyHashMapDynamic;
import com.icatch.usbcam.sdkapi.CameraProperties;
import com.icatchtek.control.customer.type.ICatchCamProperty;

import java.util.HashMap;
import java.util.List;

public class PropertyTypeInteger {
    private static final String TAG = PropertyTypeInteger.class.getSimpleName();
    private HashMap<Integer, ItemInfo> hashMap;
    private int propertyId;
    private String[] valueListString;
    private List<Integer> valueListInt;
    private Context context;
    private Resources res;
    private CameraProperties cameraProperties;

    public int getCurIndex() {
        return curIndex;
    }

    private int curIndex;

    public PropertyTypeInteger(CameraProperties cameraProperties, HashMap<Integer, ItemInfo> hashMap, int propertyId, Context context) {
        this.hashMap = hashMap;
        this.propertyId = propertyId;
        this.context = context;
        this.cameraProperties = cameraProperties;
        initItem();
        initIndex();
    }

    public void setCameraProperties(CameraProperties cameraProperties) {
        this.cameraProperties = cameraProperties;
    }

    public PropertyTypeInteger(CameraProperties cameraProperties, int propertyId, Context context) {
        this.propertyId = propertyId;
        this.context = context;
        this.cameraProperties = cameraProperties;
        initItem();
        initIndex();
    }

    public void initItem() {
        // TODO Auto-generated method stub
        if (hashMap == null) {
            hashMap = PropertyHashMapDynamic.getInstance().getDynamicHashInt(cameraProperties, propertyId);
        }
        res = context.getResources();

        switch (propertyId) {
            case PropertyId.WHITE_BALANCE:
                valueListInt = cameraProperties.getSupportedWhiteBalances();
                break;
            case PropertyId.CAPTURE_DELAY:
                valueListInt = cameraProperties.getSupportedCaptureDelays();
                break;
            case PropertyId.BURST_NUMBER:
                valueListInt = cameraProperties.getsupportedBurstNums();
                break;
            case PropertyId.LIGHT_FREQUENCY:
                valueListInt = cameraProperties.getSupportedLightFrequencys();
                break;
            case PropertyId.DATE_STAMP:
                valueListInt = cameraProperties.getsupportedDateStamps();
                break;
//            case PropertyId.USB_SEAMLESS_VIDEO:
//                valueListInt = cameraProperties.getSupportedSeamlesses();
//                break;
            default:
                valueListInt = cameraProperties.getSupportedPropertyValues(propertyId);
                break;
        }
        valueListString = new String[valueListInt.size()];
        if (valueListInt != null) {
            for (int ii = 0; ii < valueListInt.size(); ii++) {

                if (hashMap.get(valueListInt.get(ii)) == null) {
                    valueListString[ii] = "unknown";
                    continue;
                }
                if (propertyId == ICatchCamProperty.ICH_CAM_CAP_CAPTURE_DELAY || propertyId == PropertyId.USB_VIDEO_REC_SIZE) {
                    valueListString[ii] = hashMap.get(valueListInt.get(ii)).uiStringInSettingString;
                } else {
                    valueListString[ii] = res.getString(hashMap.get(valueListInt.get(ii)).uiStringInSetting);
                }

            }
        }

    }


    public void initIndex() {
        // TODO Auto-generated method stub
        int retValue;
        switch (propertyId) {
            case PropertyId.WHITE_BALANCE:
                retValue = cameraProperties.getCurrentWhiteBalance();
                break;
            case PropertyId.CAPTURE_DELAY:
                retValue = cameraProperties.getCurrentCaptureDelay();
                break;
            case PropertyId.BURST_NUMBER:
                retValue = cameraProperties.getCurrentBurstNum();
                break;
            case PropertyId.LIGHT_FREQUENCY:
                retValue = cameraProperties.getCurrentLightFrequency();
                break;
            case PropertyId.DATE_STAMP:
                retValue = cameraProperties.getCurrentDateStamp();
                break;
            case PropertyId.UP_SIDE:
                retValue = cameraProperties.getCurrentUpsideDown();
                break;
            case PropertyId.SLOW_MOTION:
                retValue = cameraProperties.getCurrentSlowMotion();
                break;
//            case PropertyId.USB_SEAMLESS_VIDEO:
//                retValue = cameraProperties.getCurrentSeamless();
//                break;
            default:
                retValue = cameraProperties.getCurrentPropertyValue(propertyId);
                break;
        }
        curIndex = -1;
        if (valueListInt == null || valueListInt.size() == 0) {
            return;
        }
        for (int ii = 0; ii < valueListInt.size(); ii++) {
            if (valueListInt.get(ii) == retValue) {
                curIndex = ii;
                break;
            }
        }
    }

    public String[] getStringList() {
        // TODO Auto-generated method stub
        return valueListString;
    }

    public Boolean setValue(int index) {
        // TODO Auto-generated method stub
        boolean retValue;
        if (valueListInt == null || valueListInt.size() == 0 || valueListInt.size() < (index + 1)) {
            return false;
        }
        int value = valueListInt.get(index);
        switch (propertyId) {
            case PropertyId.WHITE_BALANCE:
                retValue = cameraProperties.setWhiteBalance(value);
                break;
            case PropertyId.CAPTURE_DELAY:
                retValue = cameraProperties.setCaptureDelay(value);
                break;
            case PropertyId.BURST_NUMBER:
                retValue = cameraProperties.setCurrentBurst(value);
                break;
            case PropertyId.LIGHT_FREQUENCY:
                retValue = cameraProperties.setLightFrequency(value);
                break;
            case PropertyId.DATE_STAMP:
                retValue = cameraProperties.setDateStamp(value);
                break;
//            case PropertyId.USB_SEAMLESS_VIDEO:
//                retValue = cameraProperties.setSeamless(value);
//                break;
            default:
                retValue = cameraProperties.setPropertyValue(propertyId, value);
                break;
        }
        if(retValue){
            curIndex = index;
        }
        return retValue;
    }
}
