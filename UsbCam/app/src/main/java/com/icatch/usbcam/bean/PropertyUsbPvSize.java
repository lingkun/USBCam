package com.icatch.usbcam.bean;

import android.util.Log;

import com.icatch.usbcam.sdkapi.CameraProperties;
import com.icatchtek.baseutil.SharedPreferencesUtil;
import com.icatchtek.baseutil.log.AppLog;
import com.icatchtek.reliant.customer.type.ICatchCodec;
import com.icatchtek.reliant.customer.type.ICatchVideoFormat;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @author b.jiang
 * @date 2018/8/30
 * @description
 */
public class PropertyUsbPvSize {
    private String TAG = PropertyUsbPvSize.class.getSimpleName();
    private String[] valueArrayString;
    private CameraProperties cameraProperties;
    private List<ICatchVideoFormat> videoFormats;
    private HashMap<String, ICatchVideoFormat> hashMap;
    private int curIndex;

    public String getCurSizeStringInPreview() {
        return curSizeStringInPreview;
    }

    private String curSizeStringInPreview;

    public ICatchVideoFormat getCurVideoFormat() {
        return curVideoFormat;
    }

    private ICatchVideoFormat curVideoFormat = null;

    public PropertyUsbPvSize(CameraProperties cameraProperties) {
        this.cameraProperties = cameraProperties;
        initItem();
    }

    public void initItem() {
        // TODO Auto-generated method stub
        videoFormats = new LinkedList<>();
        ICatchVideoFormat videoFormat = new ICatchVideoFormat( ICatchCodec.ICH_CODEC_H264,960,540,30);
        videoFormat.setBitrate(2000000);
        videoFormats.add(videoFormat);

        List<ICatchVideoFormat> tempVideoFormats = cameraProperties.getSupportedStreamingInfos();
        if(tempVideoFormats != null && tempVideoFormats.size() > 0){
            videoFormats.addAll(tempVideoFormats);
        }
        int length = videoFormats.size();
        valueArrayString = new String[length];
        hashMap = new HashMap<>();
        curVideoFormat = videoFormats.get(0);
        curIndex = 0;
        curSizeStringInPreview = videoFormatToStringForPreview(0,curVideoFormat);
        for (int ii = 0; ii < length; ii++) {
            String temp = videoFormatToString(ii,videoFormats.get(ii));
            valueArrayString[ii] = temp;
            hashMap.put(temp,videoFormats.get(ii));
            Log.d(TAG,"video pv videoFormat:" + videoFormats.get(ii));
        }
    }

    public int getCurIndex() {
        // TODO Auto-generated method stub
        return curIndex;
    }

    public void setValueByPosition(int position) {
        AppLog.d(TAG,"setValueByPosition position:" + position+ " videoFormats.size():" +videoFormats.size());
        if(position > videoFormats.size()-1){
            return;
        }
        curVideoFormat = videoFormats.get(position);
        curIndex = position;
        curSizeStringInPreview = videoFormatToStringForPreview(position,curVideoFormat);

    }

    public String[] getValueArrayString() {
        return valueArrayString;
    }

    public String videoFormatToString(int index,ICatchVideoFormat videoFormat){
        if(videoFormat == null){
            return null;
        }
        if(index == 0){
            return "自动";
        }
        String temp = videoFormat.getVideoW() + "*" + videoFormat.getVideoH();
        return temp;
    }

    public String videoFormatToStringForPreview(int index,ICatchVideoFormat videoFormat){
        if(videoFormat == null){
            return null;
        }
        if(index == 0){
            return "自动";
        }
        String temp = videoFormat.getVideoH() + "P";
        return temp;
    }



}
