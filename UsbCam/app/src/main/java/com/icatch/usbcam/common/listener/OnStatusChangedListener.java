package com.icatch.usbcam.common.listener;


import com.icatch.usbcam.common.mode.OperationMode;

public interface OnStatusChangedListener {
    public void onChangeOperationMode(OperationMode curOperationMode);
    public void onSelectedItemsCountChanged(int SelectedNum);
}
