package com.icatch.usbcam.ui.preview;

import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.icatch.usbcam.ui.setting.SettingListAdapter;

/**
 * @author b.jiang
 * @date 2018/10/23
 * @description
 */
public interface IPreviewView {

    void setSystemTimeInfo(String timeInfo);
    void setWifiStatusVisibility(int visibility);
    void setRecordingTimeVisibility(int visibility);
    void setRecordingTime(String laspeTime);

    void setVideoSizeLayoutVisibility(int visibility);

    void setCaptureBtnEnability(boolean enablity);

    void setVideoSizeInfo(String sizeInfo);

    void setActionBarVisibility(int isVisible);
    void setStatusBarVisibility(int isVisible);

    void setSupportPreviewTxvVisibility(int visibility);

    void setPvModeBtnBackgroundResource(int drawableId);

    int getSurfaceViewWidth();

    int getSurfaceViewHeight();

    void setVolumeImvRes(int resId);
    void setLockImvRes(int resId);

    void setLockImvVisibility(int visibility);

    void setPivCaptureBtnEnability(boolean value);

    void setRecStatusImvVisibility(int visibility);

    void setSdStatusImvVisibility(int visibility);

    void setSdStatusImvRes(int resId);

    void setSettingMenuVisibility(int visibility);

    void setSettingMenuListAdapter(SettingListAdapter settingListAdapter);

    void setRecordLayoutEnable(boolean enable);
    int getSettingMenuListVisibility();

    void setBackBtnEnable(boolean enable);

    void setActionBtnEnable(boolean enable);

    void setSettingMenuListEnable(boolean enable);

    void setSurfaceViewVisibility(int visibility);
    SurfaceHolder getSurfaceHolder();
}
