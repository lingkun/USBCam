package com.icatch.usbcam.common.type;

import android.content.Context;

import com.icatch.usbcam.R;
import com.icatch.usbcam.common.mode.UsbMsdcDscopMode;
import com.icatchtek.baseutil.log.AppLog;


/**
 * @author b.jiang
 * @date 2018/10/12
 * @description
 */
public class UsbSdCardStatus {
    private static String TAG = UsbSdCardStatus.class.getSimpleName();
    public static final int SD_CARD_OUT = 0;
    public static final int SD_CARD_IN = 1;
    public static final int SD_CARD_ERR_NULL = 0;
    public static final int SD_CARD_ERR_NO_ERROR = 1;
    public static final int SD_CARD_ERR_CARD_ERROR = 2;
    public static final int SD_CARD_ERR_CARD_LOCKED = 3;
    public static final int SD_CARD_ERR_MEMORY_FULL = 4;
    public static final int SD_CARD_ERR_TO_POWER_OFF = 5;
    public static final int SD_CARD_ERR_INSUFFICIENT_DISK_SPACE = 6;
    public static final int SD_CARD_ERR_DISK_SPEED_TOO_LOW = 7;
    public static final int SD_CARD_ERR_MAX = 8;

    public static final int SD_CARD_STATUS_OUT = 9;
    public static final int SD_CARD_STATUS_IN = 10;

//    pdata[0] = 0：car out；1：card in
//    pdata[1] = card info
//    pdata[2] = card info
//    {
//        0：VIEW_ERR_NULL,
//            1：VIEW_ERR_NO_ERROR,
//            2：VIEW_ERR_CARD_ERROR,
//            3：VIEW_ERR_CARD_LOCKED,
//            4：VIEW_ERR_MEMORY_FULL,
//            5：VIEW_ERR_TO_POWER_OFF,
//            6：VIEW_ERR_INSUFFICIENT_DISK_SPACE,
//            7：VIEW_ERR_DISK_SPEED_TOO_SLOW,
//            8：VIEW_ERR_MAX
//    }
//    pdata[3] = 0,Media Streaming stop
//           1,Media Streaming start


    public static String getCardStatueInfo(Context context,int status){

        AppLog.d(TAG,"getCardStatueInfo status:" +status);
        String statusInfo = "";
//        if(status == SD_CARD_OUT){
//            statusInfo = context.getResources().getString(R.string.dialog_card_removed);
//        }else if (status == SD_CARD_IN){
//            statusInfo = context.getResources().getString(R.string.dialog_card_inserted);
//        }
//        else
        if (status == SD_CARD_ERR_CARD_ERROR){
            statusInfo = context.getString(R.string.dialog_card_error);
        } else if (status == SD_CARD_ERR_CARD_LOCKED){
            statusInfo = "SD card locked";
        }else if (status == SD_CARD_ERR_MEMORY_FULL){
            statusInfo = context.getString(R.string.dialog_card_full);
        }else if (status == SD_CARD_ERR_TO_POWER_OFF){
            statusInfo = context.getString(R.string.dialog_power_off);
        }else if (status == SD_CARD_ERR_INSUFFICIENT_DISK_SPACE){
            statusInfo = "Insufficient disk space";
        }else if (status == SD_CARD_ERR_DISK_SPEED_TOO_LOW){
            statusInfo = context.getString(R.string.card_speed_too_low);
        }else if (status == SD_CARD_ERR_MAX){
            statusInfo = "Sd card err max";
        }else {
            statusInfo = "Unknown error";
        }

        return statusInfo;
    }
}
