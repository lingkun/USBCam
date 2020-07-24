package com.icatch.usbcam.data.appinfo;

/**
 * Created by yh.zhang C001012 on 2015/10/15:13:27.
 * Fucntion:
 */
public class AppInfo {
    public static final String SDK_LOG_DIRECTORY_PATH = "/USBCam_SDK_Log/";
    public static final String APP_LOG_DIRECTORY_PATH = "/USBCam_APP_Log/";
    public static final String PROPERTY_CFG_FILE_NAME = "netconfig.properties";
    public static final String STREAM_OUTPUT_DIRECTORY_PATH = "/USBCamResoure/Raw/";

    public static final String PROPERTY_CFG_DIRECTORY_PATH = "/USBCamResoure/";
    public static final String APP_VERSION = "V1.0.2";
    public static final String DOWNLOAD_PATH_PHOTO = "/DCIM/USBCam/photo/";
    public static final String DOWNLOAD_PATH_VIDEO = "/DCIM/USBCam/video/";
    public static int currentViewpagerPosition = 0;
    public static int curVisibleItem = 0;
    public static boolean isDownloading = false;
    public static boolean enableSdkRender = false;
    public static boolean disableAudio = true;
    public static boolean saveSDKLog = true;
    public static boolean enableSoftwareDecoder = false;
}
