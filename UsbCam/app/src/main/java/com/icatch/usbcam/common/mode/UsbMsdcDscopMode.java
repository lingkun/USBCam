package com.icatch.usbcam.common.mode;

/**
 * @author b.jiang
 * @date 2019/6/3
 * @description
 */
public class UsbMsdcDscopMode {
    public static final int USB_MSDC_DSCOP_MODE_PV = 0x00;
    public static final int USB_MSDC_DSCOP_MODE_PV_TO_PB = 0x01;
    public static final int USB_MSDC_DSCOP_MODE_PB= 0x02;
    public static final int USB_MSDC_DSCOP_MODE_PB_TO_PV = 0x03;

    public static final int  SCSI_CMD_CSW_STUS_SUCCESS           = 0x00;// 执行OK

    //Start/Stop PV
    public static final int  SCSI_CMD_CSW_STUS_START_PV_T_OUT    = 0x06;  // FW 在两秒内，PV没有启动完成，APP 需要再下cmd:0x10 polling bit[3]
    public static final int  SCSI_CMD_CSW_STUS_START_PV_FAIL     = 0x07;  // PV Start fail
    public static final int  SCSI_CMD_CSW_STUS_STOP_PV_T_OUT     = 0x08;  // FW 在两秒内，PV没有停止完成，APP 需要再下cmd:0x10 polling bit[3]
    public static final int SCSI_CMD_CSW_STUS_STOP_PV_FAIL      = 0x09;  // PV Stop fail

    //Start/Stop Rec
    public static final int  SCSI_CMD_CSW_STUS_START_REC_T_OUT   = 0x0A; // FW 在两秒内，Rec没有启动完成，APP 需要再下cmd:0x10 polling bit[3]
    public static final int  SCSI_CMD_CSW_STUS_START_REC_FAIL    = 0x0B; // Rec Start fail
    public static final int  SCSI_CMD_CSW_STUS_STOP_REC_T_OUT    = 0x0C;  // FW 在两秒内，Rec没有停止完成，APP 需要再下cmd:0x10 polling bit[3]
    public static final int  SCSI_CMD_CSW_STUS_STOP_REC_FAIL     = 0x0D;  // Rec Stop fail

    //PV PB Switch
    public static final int  SCSI_CMD_CSW_STUS_PV_TO_PB_T_OUT    = 0x0E;  // FW 在两秒内，PV to PB 没有切换完成，APP 需要再下cmd:0x10 polling bit[7]
    public static final int  SCSI_CMD_CSW_STUS_PV_TO_PB_FAIL    = 0x0F;  // PV to PB Switch Fail
    public static final int  SCSI_CMD_CSW_STUS_PB_TO_PV_T_OUT    = 0x10;  // FW 在两秒内，PB to PV 没有切换完成，APP 需要再下cmd:0x10 polling bit[7]
    public static final int  SCSI_CMD_CSW_STUS_PB_TO_PV_FAIL    = 0x11;  // PB to PV Switch Fail

    public static final int  SCSI_CMD_CSW_STUS_FORMAT_SD_T_OUT   = 0x12;  // FW 在五秒内，SD Format没有切换完成，APP 需要再下cmd:0x10 polling bit[4]
    public static final int   SCSI_CMD_CSW_STUS_FORMAT_SD_FAIL    = 0x13;  // SD Formatfail



}
