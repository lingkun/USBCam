package com.icatch.usbcam.common.type;

import com.icatch.usbcam.R;

/**
 * @author b.jiang
 * @date 2019/6/14
 * @description
 */
public class AppErrorCode {
    public static final int SUCCESS = 0;
    public static final int FAILED = -1;
    public static final int NO_SD_CARD =-2;
    public static final int TRIGGER_RECCORDING =-3;
    public static final int MOVIE_RECCORDING =-4;
    public static final int SWITCH_PB_MODE_FAILED =-5;
    public static final int OPERATION_BUSY =-6;
    public static final int OPERATION_TIMEOUT =-7;


    public static int getResId(int errorCode){
        int resId;
            switch (errorCode){
                case SUCCESS:
                    resId = R.string.text_operation_success;
                    break;
                case FAILED:
                    resId = R.string.text_operation_failed;
                    break;
                case NO_SD_CARD:
                    resId = R.string.text_sd_card_not_exist;
                    break;
                case TRIGGER_RECCORDING:
                    resId = R.string.text_recording_video;
                    break;
                case SWITCH_PB_MODE_FAILED:
                    resId = R.string.switch_playback_mode_failed;
                    break;
                case OPERATION_BUSY:
                    resId = R.string.operation_is_busy;
                    break;
                case OPERATION_TIMEOUT:
                    resId = R.string.operation_timeout;
                    break;
                default:
                    resId = R.string.text_operation_failed;
                    break;
            }

            return resId;
    }

}
