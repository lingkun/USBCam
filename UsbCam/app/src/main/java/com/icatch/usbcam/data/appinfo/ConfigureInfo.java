package com.icatch.usbcam.data.appinfo;

import android.content.Context;
import android.os.Environment;

import com.icatchtek.baseutil.FileOper;
import com.icatchtek.baseutil.log.AppLog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by zhang yanhu C001012 on 2015/11/17 14:01.
 */
public class ConfigureInfo {
    private static ConfigureInfo configureInfo;
    private static final String TAG = "ConfigureInfo";
    private final String[] cfgTopic = {
            "AppVersion=" + AppInfo.APP_VERSION,
            "enableSoftwareDecoder=false",
            "SaveAppLog=true",
            "SaveSDKLog=true",
            "disableAudio=true",
            "enableSdkRender=true"
    };

    private ConfigureInfo() {

    }

    public static ConfigureInfo getInstance() {
        if (configureInfo == null) {
            configureInfo = new ConfigureInfo();
        }
        return configureInfo;
    }

    public void initCfgInfo(Context context) {
        AppLog.d(TAG, "readCfgInfo..........");
//        String directoryPath = context.getExternalCacheDir() + AppInfo.PROPERTY_CFG_DIRECTORY_PATH;
        String directoryPath = context.getExternalCacheDir().getAbsolutePath() + AppInfo.PROPERTY_CFG_DIRECTORY_PATH;
        AppLog.d(TAG, "readCfgInfo..........directoryPath=" + directoryPath);
        String fileName = AppInfo.PROPERTY_CFG_FILE_NAME;
        String info = "";
        for (int ii = 0; ii < cfgTopic.length; ii++) {
            info = info + cfgTopic[ii] + "\n";
        }
        File file = new File(directoryPath + fileName);
        if (file.exists() == false) {
            FileOper.createFile(directoryPath, fileName);
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(directoryPath + fileName);
                out.write(info.getBytes());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        //如果配置文件存在，判断是否需要替换旧版的配置文件;
        else {
            CfgProperty cfgInfo = new CfgProperty(directoryPath + fileName);
            String cfgVersion = null;
            try {
                cfgVersion = cfgInfo.getProperty("AppVersion");
            } catch (FileNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            if (cfgVersion != null) {
                AppLog.d(TAG, "cfgVersion..........=" + cfgVersion);
                if (!cfgVersion.equals(AppInfo.APP_VERSION)) {
                    writeCfgInfo(directoryPath + fileName, info);
                }
                AppLog.d(TAG, "cfgVersion=" + cfgVersion + " appVersion=" + AppInfo.APP_VERSION);
            } else {
                writeCfgInfo(directoryPath + fileName, info);
                AppLog.d(TAG, "cfgVersion=" + cfgVersion + " appVersion=" + AppInfo.APP_VERSION);
            }
        }

        CfgProperty cfgInfo = new CfgProperty(directoryPath + fileName);
        String saveSDKLog = null;
        String enableSoftwareDecoder = null;
        String disableAudio = null;
        String enableRender = null;
        try {
            saveSDKLog = cfgInfo.getProperty("SaveSDKLog");
            enableSoftwareDecoder = cfgInfo.getProperty("enableSoftwareDecoder");
            disableAudio = cfgInfo.getProperty("disableAudio");
            enableRender = cfgInfo.getProperty("enableSdkRender");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String streamOutputPath = Environment.getExternalStorageDirectory().toString() + AppInfo.STREAM_OUTPUT_DIRECTORY_PATH;
        FileOper.createDirectory(streamOutputPath);

//        if (saveSDKLog != null) {
//            if (saveSDKLog.equals("true")) {
//                AppInfo.saveSDKLog = true;
//                SdkLog.getInstance().enableSDKLog();
//            }
//            AppLog.i(TAG, "saveSDKLog:" + AppInfo.saveSDKLog);
//        }

//        if (enableInterExtHeadCheck != null) {
//            if (enableInterExtHeadCheck.equals("true")) {
//                ICatchPancamConfig.getInstance().setExtHeadCheck(true);
//            } else {
//                ICatchPancamConfig.getInstance().setExtHeadCheck(false);
//            }
//            AppLog.i(TAG, "enableInterExtHeadCheck=" + enableInterExtHeadCheck);
//        }
//        ICatchPancamConfig.getInstance().setExtHeadCheck(true);

        if (enableRender != null) {
            if (enableRender.equals("true")) {
                AppInfo.enableSdkRender = true;
            } else {
                AppInfo.enableSdkRender = false;
            }
            AppLog.i(TAG, "enableSdkRender:" + AppInfo.enableSdkRender);
        }

        if (enableSoftwareDecoder != null) {
            if (enableSoftwareDecoder.equals("true")) {
                AppInfo.enableSoftwareDecoder = true;
//                ICatchPancamConfig.getInstance().setSoftwareDecoder(true);
            } else {
                AppInfo.enableSoftwareDecoder = false;
//                ICatchPancamConfig.getInstance().setSoftwareDecoder(false);
            }
            AppLog.i(TAG, "softwareDecoder:" + enableSoftwareDecoder);
        }
        if (disableAudio != null) {
            if (disableAudio.equals("true")) {
                AppInfo.disableAudio = true;
            } else {
                AppInfo.disableAudio = false;
            }
            AppLog.i(TAG, "disableAudio:" + AppInfo.disableAudio);
        }
//        Toast.makeText(context,"softwareDecoder:" + AppInfo.enableSoftwareDecoder + "\nenableSdkRender:" + AppInfo.enableSdkRender,Toast.LENGTH_LONG).show();

    }

    private void writeCfgInfo(String path, String cfgInfo) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(path);
            out.write(cfgInfo.getBytes(), 0, cfgInfo.getBytes().length);
            out.close();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            AppLog.i(TAG, "end writeCfgInfo :IOException ");
            e1.printStackTrace();
        }
    }
}
