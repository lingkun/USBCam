package com.icatch.usbcam.sdkapi;


import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.UsbScsiExtension;
import com.icatch.usbcam.bean.CameraStatusInfo;
import com.icatchtek.baseutil.log.AppLog;
import com.icatchtek.reliant.customer.exception.IchInvalidArgumentException;
import com.icatchtek.reliant.customer.exception.IchTransportException;
import com.icatchtek.reliant.customer.exception.IchTryAgainException;
import com.icatchtek.reliant.customer.transport.ICatchITransport;
import com.icatchtek.reliant.customer.transport.ICatchUsbScsiTransport;
import com.icatchtek.reliant.customer.type.ICatchScsiCommandInfo;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author b.jiang
 * @date 2018/10/16
 * @description
 */
public class UsbScsiCommand {
    private final static String TAG = UsbScsiCommand.class.getSimpleName();
    private static final short USB_MSDC_SCSI_OPTCODE_CAM_PV_TO_PB = 0x0006;
    private static final short USB_MSDC_SCSI_OPTCODE_CAM_SD_CARD_STATUS = 0x0010;
    private static final short USB_MSDC_SCSI_OPTCODE_CAM_STREAM_STATUS = 0x0011;

    private ICatchITransport transport;
    private static final short SCSI_EXT_GET_DISK_INFO_CMD = 0x0103;
    private static final short USB_MSDC_SCSI_OPTCODE_CAM_TIME_UPDATE = 0x000B;
    private static final short USB_MSDC_SCSI_OPTCODE_CAM_VER_GET = 0x000C;
    private static final short USB_MSDC_SCSI_OPTCODE_CAM_PARA_RESET = 0x000D;
    private static final short USB_MSDC_SCSI_OPTCODE_CAM_SET_SCREEN_SREOLUTION = 0x000E;
    private static final short USB_MSDC_SCSI_OPTCODE_CAM_RETET_TIMER = 0x000F;
    public static final short USB_MSDC_SCSI_OPTCODE_CAM_FW_UPDATE = 0x0013;
    public static final short USB_MSDC_SCSI_OPTCODE_CAM_CHECK_FW_UPDATE = 0x0014;

    private static final short SCSI_EXT_GET_DISK_INFO_CMD_DATA_LEN = 16;
    private static final short USB_MSDC_SCSI_OPTCODE_CAM_TIME_UPDATE_LEN = 6;
    private static final short USB_MSDC_SCSI_OPTCODE_CAM_VER_GET_LEN = 18;
    private static final short USB_MSDC_SCSI_OPTCODE_CAM_SET_SCREEN_SREOLUTION_LEN = 4;
    private static final short USB_MSDC_SCSI_OPTCODE_CAM_SD_CARD_STATUS_LEN = 32;
    private static final byte DISK_INFO_FAT32 = 0x03;
    private static final byte DISK_INFO_EXFAT = 0x06;
    private static final short USB_MSDC_SCSI_OPTCODE_CAM_EXCEPTION_RECOVERY = 0x0201;

    public UsbScsiCommand(ICatchITransport transport) {
        this.transport = transport;
    }


    public int updateFwTime(Date date) {

        /* only support this feature in scsi transport mode */
        if (transport == null || !(this.transport instanceof ICatchUsbScsiTransport)) {
            return -1;
        }
        ICatchScsiCommandInfo commandInfo = new ICatchScsiCommandInfo(USB_MSDC_SCSI_OPTCODE_CAM_TIME_UPDATE);
        commandInfo.setDirection(ICatchScsiCommandInfo.USB_DIR_OUT);
        commandInfo.setDataLength(USB_MSDC_SCSI_OPTCODE_CAM_TIME_UPDATE_LEN);
        ICatchUsbScsiTransport scsiTransport = (ICatchUsbScsiTransport) this.transport;
        byte[] buffer = new byte[USB_MSDC_SCSI_OPTCODE_CAM_TIME_UPDATE_LEN];
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);//获取年份
        int month = cal.get(Calendar.MONTH);//获取月份
        int day = cal.get(Calendar.DATE);//获取日
        int hour = cal.get(Calendar.HOUR_OF_DAY);//小时
        int minute = cal.get(Calendar.MINUTE);//分
        int second = cal.get(Calendar.SECOND);//秒
        int WeekOfYear = cal.get(Calendar.DAY_OF_WEEK);//一周的第几天
        AppLog.d(TAG, "现在的时间是：公元" + year + "年" + month + "月" + day + "日      " + hour + "时" + minute + "分" + second + "秒       星期" + WeekOfYear);
        buffer[0] = (byte) (year - 2000);
        buffer[1] = (byte) (month + 1);
        buffer[2] = (byte) day;
        buffer[3] = (byte) hour;
        buffer[4] = (byte) minute;
        buffer[5] = (byte) second;
        try {
            AppLog.d(TAG, "updateFwTime data:" + date);
            scsiTransport.executeScsiCommand(commandInfo, buffer);
        } catch (Exception ex) {
            ex.printStackTrace();
            AppLog.d(TAG, "Exception e:" + ex.getClass().getSimpleName());
        }
        AppLog.d(TAG, "end updateFwTime");
        return 0;
    }


    public String getFwVersion() {
        AppLog.d(TAG, "getFwVersion");
        /* only support this feature in scsi transport mode */
        if (transport == null || !(this.transport instanceof ICatchUsbScsiTransport)) {
            return "unknown";
        }
        ICatchScsiCommandInfo commandInfo = new ICatchScsiCommandInfo(USB_MSDC_SCSI_OPTCODE_CAM_VER_GET);
        commandInfo.setDirection(ICatchScsiCommandInfo.USB_DIR_IN);
        commandInfo.setDataLength(USB_MSDC_SCSI_OPTCODE_CAM_VER_GET_LEN);
        ICatchUsbScsiTransport scsiTransport = (ICatchUsbScsiTransport) this.transport;
        byte[] buffer = new byte[USB_MSDC_SCSI_OPTCODE_CAM_VER_GET_LEN];
        try {
            int length = scsiTransport.executeScsiCommand(commandInfo, buffer);
//            if (length != SCSI_EXT_GET_DISK_INFO_CMD_DATA_LEN) {
//                return -1;
//            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
//        Toast.makeText(context, String.format(Locale.getDefault(), "card info, %02x %02x %02x %02x",
//                buffer[0], buffer[1], buffer[2], buffer[3]), Toast.LENGTH_SHORT).show();
        String ret = "unknown";
        if (buffer != null) {
            try {
                ret = new String(buffer, "ASCII");
//                if(ret != null && ret.length() > 18){
//                    ret = ret.substring(0,17);
//                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        AppLog.d(TAG, "end getFwVersion ret:" + ret);
        return ret;
    }

    public int exceptionRecoveryForPb(){
        if (transport == null || !(this.transport instanceof ICatchUsbScsiTransport)) {
            return -1;
        }
        ICatchScsiCommandInfo commandInfo = new ICatchScsiCommandInfo(USB_MSDC_SCSI_OPTCODE_CAM_EXCEPTION_RECOVERY);
        commandInfo.setDirection(ICatchScsiCommandInfo.USB_DIR_OUT);
        commandInfo.setParameter1(1);
        commandInfo.setDataLength(USB_MSDC_SCSI_OPTCODE_CAM_TIME_UPDATE_LEN);
        ICatchUsbScsiTransport scsiTransport = (ICatchUsbScsiTransport) this.transport;
        byte[] buffer = new byte[USB_MSDC_SCSI_OPTCODE_CAM_TIME_UPDATE_LEN];
        try {
            scsiTransport.executeScsiCommand(commandInfo, buffer);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return buffer[0];
    }

    public int exceptionRecoveryForPv(){
        if (transport == null || !(this.transport instanceof ICatchUsbScsiTransport)) {
            return -1;
        }
        ICatchScsiCommandInfo commandInfo = new ICatchScsiCommandInfo(USB_MSDC_SCSI_OPTCODE_CAM_EXCEPTION_RECOVERY);
        commandInfo.setDirection(ICatchScsiCommandInfo.USB_DIR_OUT);
        commandInfo.setParameter1(0);
        commandInfo.setDataLength(USB_MSDC_SCSI_OPTCODE_CAM_TIME_UPDATE_LEN);
        ICatchUsbScsiTransport scsiTransport = (ICatchUsbScsiTransport) this.transport;
        byte[] buffer = new byte[USB_MSDC_SCSI_OPTCODE_CAM_TIME_UPDATE_LEN];
        try {
            scsiTransport.executeScsiCommand(commandInfo, buffer);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return buffer[0];
    }

    public int resetCamera() {
        AppLog.d(TAG, "resetCamera");
        /* only support this feature in scsi transport mode */
        if (transport == null || !(this.transport instanceof ICatchUsbScsiTransport)) {
            return -1;
        }
        ICatchScsiCommandInfo commandInfo = new ICatchScsiCommandInfo(USB_MSDC_SCSI_OPTCODE_CAM_PARA_RESET);
        commandInfo.setResponseTimeout(2000);
        commandInfo.setDirection(ICatchScsiCommandInfo.USB_DIR_OUT);
        commandInfo.setDataLength(USB_MSDC_SCSI_OPTCODE_CAM_TIME_UPDATE_LEN);
        ICatchUsbScsiTransport scsiTransport = (ICatchUsbScsiTransport) this.transport;
        byte[] buffer = new byte[USB_MSDC_SCSI_OPTCODE_CAM_TIME_UPDATE_LEN];
        try {
            scsiTransport.executeScsiCommand(commandInfo, buffer);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public int setScreenResolution(int w, int h) {
        /* only support this feature in scsi transport mode */
        if (transport == null || !(this.transport instanceof ICatchUsbScsiTransport)) {
            return -1;
        }
        if (w <= 0 || h <= 0) {
            return -1;
        }
        ICatchScsiCommandInfo commandInfo = new ICatchScsiCommandInfo(USB_MSDC_SCSI_OPTCODE_CAM_SET_SCREEN_SREOLUTION);
        commandInfo.setDirection(ICatchScsiCommandInfo.USB_DIR_OUT);
        commandInfo.setDataLength(USB_MSDC_SCSI_OPTCODE_CAM_SET_SCREEN_SREOLUTION_LEN);
        ICatchUsbScsiTransport scsiTransport = (ICatchUsbScsiTransport) this.transport;
        byte[] buffer = new byte[USB_MSDC_SCSI_OPTCODE_CAM_SET_SCREEN_SREOLUTION_LEN];
        buffer[0] = (byte) (0x000000FF & (w >> 8));
        buffer[1] = (byte) (0x000000FF & w);
        buffer[2] = (byte) (0x000000FF & (h >> 8));
        buffer[3] = (byte) (0x000000FF & h);
        try {
            AppLog.d(TAG, "ScreenResolution w:" + w + " h:" + h);
            AppLog.d(TAG, "ScreenResolution:" + String.format(Locale.getDefault(), "card info, %02x %02x %02x %02x", buffer[0], buffer[1], buffer[2], buffer[3]));
            scsiTransport.executeScsiCommand(commandInfo, buffer);
        } catch (Exception ex) {
            ex.printStackTrace();
            AppLog.d(TAG, "Exception e:" + ex.getClass().getSimpleName());
        }
        return 0;
    }

    public CameraStatusInfo getCameraStatus() {
        /* only support this feature in scsi transport mode */
        if (transport == null || !(this.transport instanceof ICatchUsbScsiTransport)) {
            return null;
        }
        ICatchScsiCommandInfo commandInfo = new ICatchScsiCommandInfo(USB_MSDC_SCSI_OPTCODE_CAM_SD_CARD_STATUS);
        commandInfo.setDirection(ICatchScsiCommandInfo.USB_DIR_IN);
        commandInfo.setDataLength(USB_MSDC_SCSI_OPTCODE_CAM_SD_CARD_STATUS_LEN);
        ICatchUsbScsiTransport scsiTransport = (ICatchUsbScsiTransport) this.transport;
        byte[] buffer = new byte[USB_MSDC_SCSI_OPTCODE_CAM_SD_CARD_STATUS_LEN];
        int length = -1;
        try {
            length = scsiTransport.executeScsiCommand(commandInfo, buffer);
            if (length <= 0) {
                AppLog.d(TAG,"getCameraStatus length:" + length);
                return null;
            }
        } catch (IchTryAgainException e){
            AppLog.d(TAG,"getCameraStatus IchTryAgainException");
            //等待100ms重新下發
            try {
                Thread.sleep(100);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            try {
                length = scsiTransport.executeScsiCommand(commandInfo, buffer);
                AppLog.d(TAG,"getCameraStatus Again length:" + length);
                if (length <= 0) {
                    return null;
                }
            } catch (Exception e1) {
                e1.printStackTrace();
                AppLog.d(TAG,"getCameraStatus Exception :" + e1.getMessage());
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            AppLog.d(TAG,"getCameraStatus Exception :" + ex.getMessage());
            return null;
        }
//        Toast.makeText(context, String.format(Locale.getDefault(), "card info, %02x %02x %02x %02x",
//                buffer[0], buffer[1], buffer[2], buffer[3]), Toast.LENGTH_SHORT).show();
//        AppLog.d(TAG, "cameraStatusInfo buffer[3]:" + buffer[3] + " buffer[5]:" + buffer[5] + " buffer[6]:" + buffer[6]);
        CameraStatusInfo cameraStatusInfo = new CameraStatusInfo();
        cameraStatusInfo.setExist(buffer[0] == 0 ? false : true);
        cameraStatusInfo.setSdStateInfo(buffer[1]);
        cameraStatusInfo.setErrorInfo(buffer[2]);
//        0x00：表示 两路streaming都没有开
//        0x01：表示 只开启了 Rec 一路
//        0x02：表示 只开启了 PV 一路
//        0x03：表示 两路 streaming 
        cameraStatusInfo.setStreaming(buffer[3] == 2 || buffer[3] == 3 ? true : false);
        cameraStatusInfo.setVideoRec(buffer[3] == 1 || buffer[3] == 3 ? true : false);
        cameraStatusInfo.setEventTriggerStatus(buffer[4]);
        cameraStatusInfo.setFwUpdateStatus(buffer[6]);
        cameraStatusInfo.setSwitchModeStatus(buffer[7]);
        cameraStatusInfo.setSdInsertStatus(buffer[10]);
//        AppLog.d(TAG, "cameraStatusInfo:" + cameraStatusInfo);
        return cameraStatusInfo;
    }

    public boolean isAudioMute() {
        /* only support this feature in scsi transport mode */
        if (transport == null || !(this.transport instanceof ICatchUsbScsiTransport)) {
            return false;
        }
        ICatchScsiCommandInfo commandInfo = new ICatchScsiCommandInfo(USB_MSDC_SCSI_OPTCODE_CAM_SD_CARD_STATUS);
        commandInfo.setDirection(ICatchScsiCommandInfo.USB_DIR_IN);
        commandInfo.setDataLength(USB_MSDC_SCSI_OPTCODE_CAM_SD_CARD_STATUS_LEN);
        ICatchUsbScsiTransport scsiTransport = (ICatchUsbScsiTransport) this.transport;
        byte[] buffer = new byte[USB_MSDC_SCSI_OPTCODE_CAM_SD_CARD_STATUS_LEN];
        try {
            int length = scsiTransport.executeScsiCommand(commandInfo, buffer);
            if (length < USB_MSDC_SCSI_OPTCODE_CAM_SD_CARD_STATUS_LEN) {
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
//        Toast.makeText(context, String.format(Locale.getDefault(), "card info, %02x %02x %02x %02x",
//                buffer[0], buffer[1], buffer[2], buffer[3]), Toast.LENGTH_SHORT).show();
        if (buffer == null || buffer.length < 6) {
            return false;
        }
//        0：unmute
//        1：mute

        boolean isMute = buffer[5] == 1 ? true : false;
        AppLog.d(TAG, "isMute:" + isMute);
        return isMute;
    }

    public int resetTimer() {
        AppLog.d(TAG, "resetTimer");
        /* only support this feature in scsi transport mode */
        if (transport == null || !(this.transport instanceof ICatchUsbScsiTransport)) {
            return -1;
        }
        ICatchScsiCommandInfo commandInfo = new ICatchScsiCommandInfo(USB_MSDC_SCSI_OPTCODE_CAM_RETET_TIMER);
        commandInfo.setDirection(ICatchScsiCommandInfo.USB_DIR_OUT);
        commandInfo.setDataLength(USB_MSDC_SCSI_OPTCODE_CAM_TIME_UPDATE_LEN);
        ICatchUsbScsiTransport scsiTransport = (ICatchUsbScsiTransport) this.transport;
        byte[] buffer = new byte[USB_MSDC_SCSI_OPTCODE_CAM_TIME_UPDATE_LEN];
        try {
            scsiTransport.executeScsiCommand(commandInfo, buffer);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }


    public int resetTimerForPb(UsbMassStorageDevice msdcDevice) {
        if (msdcDevice == null || msdcDevice.getBlockDevice() == null) {
            return -1;
        }
        UsbScsiExtension extension = new UsbScsiExtension(USB_MSDC_SCSI_OPTCODE_CAM_RETET_TIMER);
        extension.setDirection(UsbScsiExtension.USB_DIR_OUT);
        extension.setDataLength(USB_MSDC_SCSI_OPTCODE_CAM_TIME_UPDATE_LEN);
        byte[] data = new byte[USB_MSDC_SCSI_OPTCODE_CAM_TIME_UPDATE_LEN];
        try {
            int dataSize = msdcDevice.getBlockDevice().execute(extension, data);
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    public CameraStatusInfo getCameraStatusForPb(UsbMassStorageDevice msdcDevice) {
        /* only support this feature in scsi transport mode */
        if (msdcDevice == null) {
            return null;
        }
//        AppLog.d(TAG, "getCameraStatusForPb");
        UsbScsiExtension extension = new UsbScsiExtension(USB_MSDC_SCSI_OPTCODE_CAM_SD_CARD_STATUS);
        extension.setDirection(UsbScsiExtension.USB_DIR_IN);
        extension.setDataLength(USB_MSDC_SCSI_OPTCODE_CAM_SD_CARD_STATUS_LEN);
        byte[] buffer = new byte[USB_MSDC_SCSI_OPTCODE_CAM_SD_CARD_STATUS_LEN];
        try {
            int length = msdcDevice.getBlockDevice().execute(extension, buffer);
            if (length < USB_MSDC_SCSI_OPTCODE_CAM_SD_CARD_STATUS_LEN) {
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
//        Toast.makeText(context, String.format(Locale.getDefault(), "card info, %02x %02x %02x %02x",
//                buffer[0], buffer[1], buffer[2], buffer[3]), Toast.LENGTH_SHORT).show();
        CameraStatusInfo cameraStatusInfo = new CameraStatusInfo();
        cameraStatusInfo.setExist(buffer[0] == 0 ? false : true);
        cameraStatusInfo.setErrorInfo(buffer[2]);
        cameraStatusInfo.setStreaming(buffer[3] == 2 || buffer[3] == 3 ? true : false);
        cameraStatusInfo.setVideoRec(buffer[3] == 1 || buffer[3] == 3 ? true : false);
        cameraStatusInfo.setFwUpdateStatus(buffer[6]);
        cameraStatusInfo.setSwitchModeStatus(buffer[7]);
        cameraStatusInfo.setSdInsertStatus(buffer[10]);
//        AppLog.d(TAG, "cameraStatusInfoForPb:" + cameraStatusInfo);
        return cameraStatusInfo;
    }

    public int getStreamStatus() {
        /* only support this feature in scsi transport mode */
        if (transport == null || !(this.transport instanceof ICatchUsbScsiTransport)) {
            return -1;
        }
        ICatchScsiCommandInfo commandInfo = new ICatchScsiCommandInfo(USB_MSDC_SCSI_OPTCODE_CAM_STREAM_STATUS);
        commandInfo.setDirection(ICatchScsiCommandInfo.USB_DIR_IN);
        commandInfo.setDataLength(4);
        ICatchUsbScsiTransport scsiTransport = (ICatchUsbScsiTransport) this.transport;
        byte[] buffer = new byte[4];
        try {
            int length = scsiTransport.executeScsiCommand(commandInfo, buffer);
            if (length < 4) {
                return -1;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return -1;
        }
        AppLog.d(TAG, "getStreamStatus buffer[0]:" + buffer[0]);
        return buffer[0];
    }

    public int updateFw(){
        if (transport == null || !(this.transport instanceof ICatchUsbScsiTransport)) {
            return -1;
        }
        ICatchScsiCommandInfo commandInfo = new ICatchScsiCommandInfo( USB_MSDC_SCSI_OPTCODE_CAM_FW_UPDATE);
//        commandInfo.setDirection(1);
        commandInfo.setDirection(ICatchScsiCommandInfo.USB_DIR_OUT);
        commandInfo.setDataLength(0);
        commandInfo.setParameter1(1);
        ICatchUsbScsiTransport scsiTransport = (ICatchUsbScsiTransport) this.transport;
        try {
            scsiTransport.executeScsiCommand(commandInfo, null);
        } catch (Exception ex) {
            ex.printStackTrace();
            AppLog.d(TAG, "Exception e:" + ex.getClass().getSimpleName());
        }
        return 0;
    }

    public int ignoreUpdateFw(){
        if (transport == null || !(this.transport instanceof ICatchUsbScsiTransport)) {
            return -1;
        }
        ICatchScsiCommandInfo commandInfo = new ICatchScsiCommandInfo( USB_MSDC_SCSI_OPTCODE_CAM_FW_UPDATE);
//        commandInfo.setDirection(1);
        commandInfo.setDirection(ICatchScsiCommandInfo.USB_DIR_OUT);
        commandInfo.setDataLength(0);
        commandInfo.setParameter1(0);
        ICatchUsbScsiTransport scsiTransport = (ICatchUsbScsiTransport) this.transport;
        try {
            scsiTransport.executeScsiCommand(commandInfo, null);
        } catch (Exception ex) {
            ex.printStackTrace();
            AppLog.d(TAG, "Exception e:" + ex.getClass().getSimpleName());
        }
        return 0;
    }

    public boolean checkFwUpdate(){
        int ret = getProperty(USB_MSDC_SCSI_OPTCODE_CAM_CHECK_FW_UPDATE);
        boolean needUpdate = false;
        /**
         * 0x00: 表示没有新的FW 版本
         * 0x01: 表示有新的FW版本
         */
        if(ret == 0x01){
            needUpdate = true;
        }
        return needUpdate;
    }


    public int setProperty(int propertyId,int value){
        if (transport == null || !(this.transport instanceof ICatchUsbScsiTransport)) {
            return -1;
        }
        if (propertyId <= 0 || value < 0) {
            return -1;
        }
        ICatchScsiCommandInfo commandInfo = new ICatchScsiCommandInfo((short) propertyId);
//        commandInfo.setDirection(1);
        commandInfo.setDirection(ICatchScsiCommandInfo.USB_DIR_OUT);
        commandInfo.setDataLength(0);
//        byte[] buffer = new byte[32];
//        buffer[0] = (byte) (0x0000000F & value);
        commandInfo.setParameter1(value);
        ICatchUsbScsiTransport scsiTransport = (ICatchUsbScsiTransport) this.transport;
        try {
            AppLog.d(TAG, "setProperty  propertyId:" +propertyId + " value:" + value);
            scsiTransport.executeScsiCommand(commandInfo, null);
        } catch (Exception ex) {
            ex.printStackTrace();
            AppLog.d(TAG, "Exception e:" + ex.getClass().getSimpleName());
        }
        return 0;
    }

    public int getProperty(int propertyId){
        if (transport == null || !(this.transport instanceof ICatchUsbScsiTransport)) {
            return -1;
        }
        if (propertyId <= 0) {
            return -1;
        }
        ICatchScsiCommandInfo commandInfo = new ICatchScsiCommandInfo((short) propertyId);
//        commandInfo.setDirection(0);
        commandInfo.setDirection(ICatchScsiCommandInfo.USB_DIR_IN);
        commandInfo.setDataLength(32);
        ICatchUsbScsiTransport scsiTransport = (ICatchUsbScsiTransport) this.transport;
        byte[] buffer = new byte[32];
        try {
            scsiTransport.executeScsiCommand(commandInfo, buffer);
        } catch (Exception ex) {
            ex.printStackTrace();
            AppLog.d(TAG, "Exception e:" + ex.getClass().getSimpleName());
        }
        AppLog.d(TAG, "getProperty  propertyId:" +propertyId + " value:" + buffer[0]);
        return buffer[0];
    }
}
