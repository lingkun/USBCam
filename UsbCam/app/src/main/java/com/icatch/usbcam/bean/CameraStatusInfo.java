package com.icatch.usbcam.bean;

import com.icatch.usbcam.common.type.UsbSdCardStatus;
import com.icatchtek.baseutil.log.AppLog;

/**
 * @author b.jiang
 * @date 2018/11/5
 * @description
 */
public class CameraStatusInfo {
    private boolean isExist;
    private int errorInfo;
    private int sdStateInfo;
    private boolean isStreaming;
    private boolean isVideoRec;
    private int eventTriggerStatus;
    private int fwUpdateStatus = 0;
    private int switchModeStatus = -1;
    private int sdInsertStatus = 0;

    public int getSdInsertStatus() {
        return sdInsertStatus;
    }

    public void setSdInsertStatus(int sdInsertStatus) {
        this.sdInsertStatus = sdInsertStatus;
    }

    public int getEventTriggerStatus() {
        return eventTriggerStatus;
    }

    public void setEventTriggerStatus(int eventTriggerStatus) {
        this.eventTriggerStatus = eventTriggerStatus;
    }

    public int getFwUpdateStatus() {
        AppLog.d("test","FwUpdateStatus:" + fwUpdateStatus);
        return fwUpdateStatus;
    }

    public void setFwUpdateStatus(int fwUpdateStatus) {
        this.fwUpdateStatus = fwUpdateStatus;
    }

    public int getSwitchModeStatus() {
        return switchModeStatus;
    }

    public void setSwitchModeStatus(int switchModeStatus) {
        this.switchModeStatus = switchModeStatus;
    }

    public CameraStatusInfo(boolean isExist, int errorInfo, boolean isStreaming) {
        this.isExist = isExist;
        this.errorInfo = errorInfo;
        this.isStreaming = isStreaming;
    }

    public boolean isSDCardExist() {
        return isExist;
    }

    public void setExist(boolean exist) {
        isExist = exist;
    }

    public int getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(int errorInfo) {
        this.errorInfo = errorInfo;
    }

    public boolean isStreaming() {
        return isStreaming;
    }

    public void setStreaming(boolean streaming) {
        isStreaming = streaming;
    }

    public boolean isVideoRec() {
        return isVideoRec;
    }

    public void setVideoRec(boolean videoRec) {
        isVideoRec = videoRec;
    }

    public CameraStatusInfo(){
        this.isExist = false;
        this.errorInfo = 0;
        this.isStreaming = false;
        this.isVideoRec = false;
    }

    @Override
    public String toString() {
        String msg = "isSDCardExist:" + isExist + " ErrorInfo:" + errorInfo + " isStreaming:" + isStreaming  + " isVideoRec:" + isVideoRec
                + " fwUpdateStatus：" + fwUpdateStatus + " switchModeStatus：" + switchModeStatus + " eventTriggerStatus:" +eventTriggerStatus
                + " sdInsertStatus:" + sdInsertStatus;
        return msg;
    }

    public int getSdStateInfo() {
        return sdStateInfo;
    }

    public void setSdStateInfo(int sdStateInfo) {
        this.sdStateInfo = sdStateInfo;
    }

    public boolean isSdError(){
        if( isExist && getErrorInfo() != UsbSdCardStatus.SD_CARD_ERR_NULL && getErrorInfo() != UsbSdCardStatus.SD_CARD_ERR_NO_ERROR){
            return true;
        }else {
            return false;
        }
    }

    public boolean isSdInsertError(){
        if( isExist && getSdInsertStatus() != 0 ){
            return true;
        }else {
            return false;
        }
    }
}
