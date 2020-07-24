package com.icatch.usbcam.ui.localpb.presenter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.View;

import com.icatch.usbcam.bean.LocalPbItemInfo;
import com.icatch.usbcam.common.mode.OperationMode;
import com.icatch.usbcam.common.type.FileType;
import com.icatch.usbcam.common.type.PhotoWallPreviewType;
import com.icatch.usbcam.ui.base.BasePresenter;
import com.icatch.usbcam.ui.localpb.LocalFileHelper;
import com.icatch.usbcam.ui.localpb.activity.LocalPhotoPbActivity;
import com.icatch.usbcam.ui.localpb.adapter.LocalMultiPbWallGridAdapter;
import com.icatch.usbcam.ui.localpb.adapter.LocalMultiPbWallListAdapter;
import com.icatch.usbcam.ui.localpb.contract.LocalMultiPbFragmentView;
import com.icatch.usbcam.ui.usbpb.UsbPbItemInfo;
import com.icatch.usbcam.ui.usbpb.activity.VideoPlayerActivity;
import com.icatch.usbcam.ui.usbpb.adapter.USBMultiPbWallGridAdapter;
import com.icatch.usbcam.ui.usbpb.adapter.USBMultiPbWallListAdapter;
import com.icatchtek.basecomponent.prompt.MyProgressDialog;
import com.icatchtek.baseutil.log.AppLog;
import com.icatchtek.module.mediaplayer.LocalVideoPlayerActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by b.jiang on 2017/5/19.
 */

public class LocalMultiPbFragmentPresenter extends BasePresenter {
    private String TAG = LocalMultiPbFragmentPresenter.class.getSimpleName();
    private LocalMultiPbFragmentView multiPbPhotoView;
    private LocalMultiPbWallListAdapter photoWallListAdapter;
    private LocalMultiPbWallGridAdapter photoWallGridAdapter;
    private Activity activity;
    private OperationMode curOperationMode = OperationMode.MODE_BROWSE;
    private List<LocalPbItemInfo> pbItemInfoList;
    private Handler handler;
    private FileType fileType = FileType.FILE_PHOTO;
    private PhotoWallPreviewType photoWallPreviewType = PhotoWallPreviewType.PREVIEW_TYPE_LIST;

    public LocalMultiPbFragmentPresenter(Activity activity, FileType fileType) {
        super(activity);
        this.activity = activity;
        handler = new Handler();
        this.fileType = fileType;
    }

    public void setView(LocalMultiPbFragmentView localPhotoWallView) {
        this.multiPbPhotoView = localPhotoWallView;
        initCfg();
    }


    public List<LocalPbItemInfo> getPhotoInfoList(FileType fileType) {
        List<LocalPbItemInfo> itemInfos;
        if (fileType == FileType.FILE_PHOTO) {
            itemInfos = LocalFileHelper.getInstance().getPhotoFile();
        } else {
            itemInfos = LocalFileHelper.getInstance().getVideoFile();
        }
        return itemInfos;
    }

    public void setItemEnable(boolean enable){
        if(photoWallListAdapter != null){
            if(enable){
                photoWallListAdapter.enableAllItem();
            }else {
                photoWallListAdapter.disableAllItem();
            }
        }
    }

    public void loadPhotoWall() {
        MyProgressDialog.showProgressDialog(activity, "Loading...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<LocalPbItemInfo> pbItemInfoList = getPhotoInfoList(fileType);
                if (pbItemInfoList == null || pbItemInfoList.size() <= 0) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            MyProgressDialog.closeProgressDialog();
                            multiPbPhotoView.setGridViewVisibility(View.GONE);
                            multiPbPhotoView.setListViewVisibility(View.GONE);
                            multiPbPhotoView.setNoContentTxvVisibility(View.VISIBLE);
//                            MyToast.show(activity, "no file");
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            multiPbPhotoView.setNoContentTxvVisibility(View.GONE);
                            setAdaper(pbItemInfoList);
                            MyProgressDialog.closeProgressDialog();
                        }
                    });
                }
            }
        }).start();
    }

    public void setAdaper(List<LocalPbItemInfo> list) {
        curOperationMode = OperationMode.MODE_BROWSE;
        if (pbItemInfoList == null) {
            pbItemInfoList = new ArrayList<>();
            pbItemInfoList.addAll(list);
        }else {
            pbItemInfoList.clear();
            pbItemInfoList.addAll(list);
        }
        if (photoWallPreviewType == PhotoWallPreviewType.PREVIEW_TYPE_LIST) {
            multiPbPhotoView.setGridViewVisibility(View.GONE);
            multiPbPhotoView.setListViewVisibility(View.VISIBLE);
            if (photoWallListAdapter != null) {
                photoWallListAdapter.notifyDataSetChanged();
            } else {
                photoWallListAdapter = new LocalMultiPbWallListAdapter(activity, pbItemInfoList, fileType);
                multiPbPhotoView.setListViewAdapter(photoWallListAdapter);
            }
        } else {
            multiPbPhotoView.setGridViewVisibility(View.VISIBLE);
            multiPbPhotoView.setListViewVisibility(View.GONE);
            if (photoWallGridAdapter != null) {
                photoWallGridAdapter.notifyDataSetChanged();
            } else {
                photoWallGridAdapter = (new LocalMultiPbWallGridAdapter(activity, pbItemInfoList, fileType));
                multiPbPhotoView.setGridViewAdapter(photoWallGridAdapter);
            }
        }
    }

    public void refreshPhotoWall() {
        AppLog.d(TAG, "refreshPhotoWall layoutType=" + photoWallPreviewType);
        List<LocalPbItemInfo> pbItemInfoList = getPhotoInfoList(fileType);
        if (pbItemInfoList == null || pbItemInfoList.size() <= 0) {
            multiPbPhotoView.setGridViewVisibility(View.GONE);
            multiPbPhotoView.setListViewVisibility(View.GONE);
            multiPbPhotoView.setNoContentTxvVisibility(View.VISIBLE);
        } else {
            multiPbPhotoView.setNoContentTxvVisibility(View.GONE);
            setAdaper(pbItemInfoList);
        }
    }

    public void changePreviewType() {
        if (photoWallPreviewType == PhotoWallPreviewType.PREVIEW_TYPE_LIST) {
            photoWallPreviewType = PhotoWallPreviewType.PREVIEW_TYPE_GRID;
        } else {
            photoWallPreviewType = PhotoWallPreviewType.PREVIEW_TYPE_LIST;
        }
        loadPhotoWall();
    }

    public void listViewEnterEditMode(int position) {
        if (curOperationMode == OperationMode.MODE_BROWSE) {
            curOperationMode = OperationMode.MODE_EDIT;
            multiPbPhotoView.notifyChangeMultiPbMode(curOperationMode);
            photoWallListAdapter.setOperationMode(curOperationMode);
            photoWallListAdapter.changeSelectionState(position);
            multiPbPhotoView.setPhotoSelectNumText(photoWallListAdapter.getSelectedCount());
            AppLog.i(TAG, "gridViewSelectOrCancelOnce curOperationMode=" + curOperationMode);
        }
    }

    public void gridViewEnterEditMode(int position) {
        if (curOperationMode == OperationMode.MODE_BROWSE) {
            curOperationMode = OperationMode.MODE_EDIT;
            multiPbPhotoView.notifyChangeMultiPbMode(curOperationMode);
            photoWallGridAdapter.changeCheckBoxState(position, curOperationMode);
            multiPbPhotoView.setPhotoSelectNumText(photoWallGridAdapter.getSelectedCount());
            AppLog.i(TAG, "gridViewSelectOrCancelOnce curOperationMode=" + curOperationMode);
        }
    }

    public void quitEditMode() {
        if (curOperationMode == OperationMode.MODE_EDIT) {
            curOperationMode = OperationMode.MODE_BROWSE;
            multiPbPhotoView.notifyChangeMultiPbMode(curOperationMode);
            if (photoWallPreviewType == PhotoWallPreviewType.PREVIEW_TYPE_LIST) {
                photoWallListAdapter.quitEditMode();
            } else {
                photoWallGridAdapter.quitEditMode();
            }
        }
    }

    public void listViewSelectOrCancelOnce(int position) {
        AppLog.i(TAG, "listViewSelectOrCancelOnce positon=" + position + " photoWallPreviewType=" + photoWallPreviewType);
        if (curOperationMode == OperationMode.MODE_BROWSE) {
            AppLog.i(TAG, "listViewSelectOrCancelOnce curOperationMode=" + curOperationMode);
            if (fileType == FileType.FILE_PHOTO) {
                Intent intent = new Intent();
                intent.putExtra("curfilePosition", position);
                intent.setClass(activity, LocalPhotoPbActivity.class);
                activity.startActivity(intent);
            } else {
//                Intent intent = new Intent();
////                intent.putExtra("curfilePath", videoPath);
////                intent.putExtra("curfilePosition", position);
//                intent.setClass(activity, LocalVideoPlayerActivity.class);
//                intent.setData(Uri.parse(videoPath));
//                activity.startActivity(intent);
                String videoPath = pbItemInfoList.get(position).getFilePath();
                String fileName = pbItemInfoList.get(position).getFileName();
                Intent intent = new Intent(activity, VideoPlayerActivity.class);
                intent.setData(Uri.parse(videoPath));
                intent.putExtra("isRemote", false);
                intent.putExtra("filename", fileName);
                activity.startActivity(intent);
            }

        } else {
            photoWallListAdapter.changeSelectionState(position);
            multiPbPhotoView.setPhotoSelectNumText(photoWallListAdapter.getSelectedCount());
        }

    }

    public void gridViewSelectOrCancelOnce(int position) {
        AppLog.i(TAG, "gridViewSelectOrCancelOnce positon=" + position + " photoWallPreviewType=" + photoWallPreviewType);
        String videoPath = pbItemInfoList.get(position).getFilePath();
        if (curOperationMode == OperationMode.MODE_BROWSE) {
            if (fileType == FileType.FILE_PHOTO) {
                Intent intent = new Intent();
                intent.putExtra("curfilePosition", position);
                intent.setClass(activity, LocalPhotoPbActivity.class);
                activity.startActivity(intent);
            } else {
                AppLog.i(TAG, "video path:" + videoPath);
//                Intent intent = new Intent();
//                intent.setClass(activity, LocalVideoPlayerActivity.class);
//                intent.setData(Uri.parse(videoPath));
//                activity.startActivity(intent);

                Intent intent = new Intent(activity, VideoPlayerActivity.class);
                intent.setData(Uri.parse(videoPath));
                intent.putExtra("isRemote", false);
                activity.startActivity(intent);
            }
        } else {
            photoWallGridAdapter.changeCheckBoxState(position, curOperationMode);
            multiPbPhotoView.setPhotoSelectNumText(photoWallGridAdapter.getSelectedCount());
        }

    }


    public void selectOrCancelAll(boolean isSelectAll) {
        if (curOperationMode == OperationMode.MODE_BROWSE) {
            return;
        }
        int selectNum;
        if (isSelectAll) {
            if (photoWallPreviewType == PhotoWallPreviewType.PREVIEW_TYPE_LIST) {
                photoWallListAdapter.selectAllItems();
                selectNum = photoWallListAdapter.getSelectedCount();
            } else {
                photoWallGridAdapter.selectAllItems();
                selectNum = photoWallGridAdapter.getSelectedCount();
            }
            multiPbPhotoView.setPhotoSelectNumText(selectNum);
        } else {
            if (photoWallPreviewType == PhotoWallPreviewType.PREVIEW_TYPE_LIST) {
                photoWallListAdapter.cancelAllSelections();
                selectNum = photoWallListAdapter.getSelectedCount();
            } else {
                photoWallGridAdapter.cancelAllSelections();
                selectNum = photoWallGridAdapter.getSelectedCount();
            }
            multiPbPhotoView.setPhotoSelectNumText(selectNum);
        }
    }

    public List<LocalPbItemInfo> getSelectedList() {
        if (photoWallPreviewType == PhotoWallPreviewType.PREVIEW_TYPE_LIST) {
            return photoWallListAdapter.getSelectedList();
        } else {
            return photoWallGridAdapter.getCheckedItemsList();
        }
    }

    public void setScrollState(boolean scrollState) {
        if (photoWallListAdapter != null) {
            photoWallListAdapter.setScroll(scrollState);
        }
    }

}
