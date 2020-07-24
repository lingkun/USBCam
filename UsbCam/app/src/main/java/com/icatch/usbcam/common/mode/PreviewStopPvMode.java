package com.icatch.usbcam.common.mode;

/**
 * @author b.jiang
 * @date 2019/6/27
 * @description
 */
public class PreviewStopPvMode {
    public static final int STOP_PV_ONLY= 0x00;
    public static final int STOP_PV_TO_PB = 0x01;
    public static final int STOP_PV_TO_HOME = 0x02;
    public static final int STOP_PV_TO_LOCAL = 0x03;
    public static final int STOP_PV_TO_SWITCH_PV_SIZE = 0x04;
}
