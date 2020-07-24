package com.icatch.usbcam.common.type;

/**
 * @author b.jiang
 * @date 2019/7/18
 * @description
 */
public class FwUpdateStatus {
    public static int FW_UPDATE_DONE = 0x00;
    public static int FW_UPDATE_NEED_UPDATE = 0x01;
    public static int FW_UPDATE_DOING = 0x02;
    public static int FW_UPDATE_FILE_ERROR = 0x03;
    public static int FW_UPDATE_FAIL = 0x04;
}
