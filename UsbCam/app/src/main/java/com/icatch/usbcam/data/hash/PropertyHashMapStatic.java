package com.icatch.usbcam.data.hash;

import android.annotation.SuppressLint;

import com.icatch.usbcam.R;
import com.icatch.usbcam.bean.ItemInfo;
import com.icatch.usbcam.common.type.CarshSensitivity;
import com.icatch.usbcam.common.type.EventFileTime;
import com.icatch.usbcam.common.type.VideoQuality;
import com.icatchtek.baseutil.log.AppLog;
import com.icatchtek.control.customer.type.ICatchCamBurstNumber;
import com.icatchtek.control.customer.type.ICatchCamDateStamp;
import com.icatchtek.control.customer.type.ICatchCamLightFrequency;
import com.icatchtek.control.customer.type.ICatchCamWhiteBalance;

import java.util.HashMap;

public class PropertyHashMapStatic {
    private final String tag = "PropertyHashMapStatic";
    @SuppressLint("UseSparseArrays")
    public static HashMap<Integer, ItemInfo> burstMap = new HashMap<Integer, ItemInfo>();
    @SuppressLint("UseSparseArrays")
    public static HashMap<Integer, ItemInfo> whiteBalanceMap = new HashMap<Integer, ItemInfo>();
    @SuppressLint("UseSparseArrays")
    public static HashMap<Integer, ItemInfo> electricityFrequencyMap = new HashMap<Integer, ItemInfo>();
    @SuppressLint("UseSparseArrays")
    public static HashMap<Integer, ItemInfo> dateStampMap = new HashMap<Integer, ItemInfo>();
    //录像画质
    @SuppressLint("UseSparseArrays")
    public static HashMap<Integer, ItemInfo> videoQualityMap = new HashMap<Integer, ItemInfo>();

    //锁定录像时长设定
    @SuppressLint("UseSparseArrays")
    public static HashMap<Integer, ItemInfo> eventFileTimeMap = new HashMap<Integer, ItemInfo>();
    //碰撞灵敏度设定
    @SuppressLint("UseSparseArrays")
    public static HashMap<Integer, ItemInfo> carshSensitivityMap = new HashMap<Integer, ItemInfo>();

    //usb录影size
    @SuppressLint("UseSparseArrays")
    public static HashMap<Integer, ItemInfo> usbVideoRecSizeMap = new HashMap<Integer, ItemInfo>();

    //usb pv size
    @SuppressLint("UseSparseArrays")
    public static HashMap<Integer, ItemInfo> usbPvSizeMap = new HashMap<Integer, ItemInfo>();

    //延时录影
    @SuppressLint("UseSparseArrays")
    public static HashMap<Integer, ItemInfo> seamlessesVideoMap =new HashMap<Integer, ItemInfo>();

    public static PropertyHashMapStatic propertyHashMap;

    public static PropertyHashMapStatic getInstance() {
        if (propertyHashMap == null) {
            propertyHashMap = new PropertyHashMapStatic();
        }
        return propertyHashMap;
    }

    public void initPropertyHashMap() {
        AppLog.i(tag, "Start initPropertyHashMap");
        initWhiteBalanceMap();
        initBurstMap();
        initElectricityFrequencyMap();
        initDateStampMap();
        initVideoQualityMap();
        initCarshSensitivityMap();
        initEventFileTimeMap();
        initUsbVideoRecSizeMap();
        initUsbPvSizeMap();
        initSeamlessesVideoMap();
        AppLog.i(tag, "End initPropertyHashMap");
    }

    private void initSeamlessesVideoMap(){
//        seamlessesVideoMap.put(1, new ItemInfo(R.string.setting_seamlesses_time_off, null, 0));
//        seamlessesVideoMap.put(2, new ItemInfo(R.string.setting_seamlesses_time_1min, null, 0));
//        seamlessesVideoMap.put(3, new ItemInfo(R.string.setting_seamlesses_time_3min, null, 0));
//        seamlessesVideoMap.put(4, new ItemInfo(R.string.setting_seamlesses_time_5min, null, 0));
//        seamlessesVideoMap.put(1, new ItemInfo(R.string.setting_seamlesses_time_off, null, 0));
        seamlessesVideoMap.put(1, new ItemInfo(R.string.setting_seamlesses_time_1min, null, 0));
        seamlessesVideoMap.put(2, new ItemInfo(R.string.setting_seamlesses_time_3min, null, 0));
        seamlessesVideoMap.put(3, new ItemInfo(R.string.setting_seamlesses_time_5min, null, 0));
    }


    private void initUsbPvSizeMap(){
        usbPvSizeMap.put(1080, new ItemInfo("1920*1080", "1920*1080", 0));
        usbPvSizeMap.put(720, new ItemInfo("1280*720", "1280*720", 0));
        usbPvSizeMap.put(540, new ItemInfo("960*540", "960*540", 0));
    }

    private void initUsbVideoRecSizeMap(){
        usbVideoRecSizeMap.put(1, new ItemInfo("1920*1080", "1920*1080", 0));
        usbVideoRecSizeMap.put(2, new ItemInfo("1280*720", "1280*720", 0));
    }

    private void initEventFileTimeMap(){
        eventFileTimeMap.put(EventFileTime.FILE_TIME_10, new ItemInfo(R.string.setting_event_file_time_10s, null, 0));
        eventFileTimeMap.put(EventFileTime.FILE_TIME_20, new ItemInfo(R.string.setting_event_file_time_20s, null, 0));
        eventFileTimeMap.put(EventFileTime.FILE_TIME_30,new ItemInfo(R.string.setting_event_file_time_30s, null, 0));
    }

    private void initVideoQualityMap(){
        videoQualityMap.put(VideoQuality.QUALITY_HIGH, new ItemInfo(R.string.text_quality_hyperfine, null, 0));
        videoQualityMap.put(VideoQuality.QUALITY_MEDIUM, new ItemInfo(R.string.text_quality_fine, null, 0));
        videoQualityMap.put(VideoQuality.QUALITY_LOW, new ItemInfo(R.string.text_quality_standard, null, 0));
    }

    private void initCarshSensitivityMap(){
        carshSensitivityMap.put(CarshSensitivity.SENSITIVITY_HIGH, new ItemInfo(R.string.text_high, null, 0));
        carshSensitivityMap.put(CarshSensitivity.SENSITIVITY_MEDIUM, new ItemInfo(R.string.text_medium, null, 0));
        carshSensitivityMap.put(CarshSensitivity.SENSITIVITY_LOW, new ItemInfo(R.string.text_low, null, 0));
    }

    public void initWhiteBalanceMap() {
        whiteBalanceMap.put(ICatchCamWhiteBalance.ICH_CAM_WB_AUTO, new ItemInfo(R.string.wb_auto, null, R.drawable.awb_auto));
        whiteBalanceMap.put(ICatchCamWhiteBalance.ICH_CAM_WB_CLOUDY, new ItemInfo(R.string.wb_cloudy, null, R.drawable.awb_cloudy));
        whiteBalanceMap.put(ICatchCamWhiteBalance.ICH_CAM_WB_DAYLIGHT, new ItemInfo(R.string.wb_daylight, null, R.drawable.awb_daylight));
        whiteBalanceMap.put(ICatchCamWhiteBalance.ICH_CAM_WB_FLUORESCENT, new ItemInfo(R.string.wb_fluorescent, null, R.drawable.awb_fluoresecent));
        whiteBalanceMap.put(ICatchCamWhiteBalance.ICH_CAM_WB_TUNGSTEN, new ItemInfo(R.string.wb_incandescent, null, R.drawable.awb_incadescent)); //
        // whiteBalanceMap.put(ICatchCamWhiteBalance.ICH_CAM_WB_UNDEFINED,
    }

    public void initBurstMap() {
        burstMap.put(ICatchCamBurstNumber.ICH_CAM_BURST_NUMBER_OFF, new ItemInfo(R.string.burst_off, null, 0));
        burstMap.put(ICatchCamBurstNumber.ICH_CAM_BURST_NUMBER_3, new ItemInfo(R.string.burst_3, null, R.drawable.continuous_shot_1));
        burstMap.put(ICatchCamBurstNumber.ICH_CAM_BURST_NUMBER_5, new ItemInfo(R.string.burst_5, null, R.drawable.continuous_shot_2));
        burstMap.put(ICatchCamBurstNumber.ICH_CAM_BURST_NUMBER_10, new ItemInfo(R.string.burst_10, null, R.drawable.continuous_shot_3));
        burstMap.put(ICatchCamBurstNumber.ICH_CAM_BURST_NUMBER_HS, new ItemInfo(R.string.burst_hs, null, 0));
    }

    public void initElectricityFrequencyMap() {
        electricityFrequencyMap.put(ICatchCamLightFrequency.ICH_CAM_LIGHT_FREQUENCY_50HZ, new ItemInfo(R.string.frequency_50HZ, null, 0));
        electricityFrequencyMap.put(ICatchCamLightFrequency.ICH_CAM_LIGHT_FREQUENCY_60HZ, new ItemInfo(R.string.frequency_60HZ, null, 0));
    }

    public void initDateStampMap() {
        dateStampMap.put(ICatchCamDateStamp.ICH_CAM_DATE_STAMP_OFF, new ItemInfo(R.string.dateStamp_off, null, 0));
        dateStampMap.put(ICatchCamDateStamp.ICH_CAM_DATE_STAMP_DATE, new ItemInfo(R.string.dateStamp_date, null, 0));
        dateStampMap.put(ICatchCamDateStamp.ICH_CAM_DATE_STAMP_DATE_TIME, new ItemInfo(R.string.dateStamp_date_and_time, null, 0));
    }
}
