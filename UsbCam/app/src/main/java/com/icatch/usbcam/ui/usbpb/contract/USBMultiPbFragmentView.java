package com.icatch.usbcam.ui.usbpb.contract;

import android.graphics.Bitmap;
import android.view.View;

import com.icatch.usbcam.common.mode.OperationMode;
import com.icatch.usbcam.ui.usbpb.adapter.USBMultiPbWallGridAdapter;
import com.icatch.usbcam.ui.usbpb.adapter.USBMultiPbWallListAdapter;


/**
 * Created by b.jiang on 2017/5/19.
 */

public interface USBMultiPbFragmentView {
    void setListViewVisibility(int visibility);

    void setGridViewVisibility(int visibility);

    void setListViewAdapter(USBMultiPbWallListAdapter photoWallListAdapter);

    void setGridViewAdapter(USBMultiPbWallGridAdapter PhotoWallGridAdapter);

    void setListViewSelection(int position);

    void setGridViewSelection(int position);

    void setListViewHeaderText(String headerText);

    View listViewFindViewWithTag(int tag);

    View gridViewFindViewWithTag(int tag);

    void updateGridViewBitmaps(String tag, Bitmap bitmap);

    void notifyChangeMultiPbMode(OperationMode operationMode);

    void setPhotoSelectNumText(int selectNum);

    void setNoContentTxvVisibility(int visibility);
}
