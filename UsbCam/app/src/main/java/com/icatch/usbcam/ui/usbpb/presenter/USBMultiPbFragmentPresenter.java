package com.icatch.usbcam.ui.usbpb.presenter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.View;

import com.icatch.usbcam.R;
import com.icatch.usbcam.common.mode.OperationMode;
import com.icatch.usbcam.common.type.FileType;
import com.icatch.usbcam.common.type.PhotoWallPreviewType;
import com.icatch.usbcam.ui.base.BasePresenter;
import com.icatch.usbcam.ui.usbpb.SortRule;
import com.icatch.usbcam.ui.usbpb.UsbFileHelper;
import com.icatch.usbcam.ui.usbpb.UsbPbItemInfo;
import com.icatch.usbcam.ui.usbpb.activity.UsbPhotoPbActivity;
import com.icatch.usbcam.ui.usbpb.activity.VideoPlayerActivity;
import com.icatch.usbcam.ui.usbpb.adapter.USBMultiPbWallGridAdapter;
import com.icatch.usbcam.ui.usbpb.adapter.USBMultiPbWallListAdapter;
import com.icatch.usbcam.ui.usbpb.contract.USBMultiPbFragmentView;
import com.icatchtek.basecomponent.prompt.MyProgressDialog;
import com.icatchtek.baseutil.log.AppLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by b.jiang on 2017/5/19.
 */

public class USBMultiPbFragmentPresenter extends BasePresenter {

    private String TAG = USBMultiPbFragmentPresenter.class.getSimpleName();
    private USBMultiPbFragmentView multiPbPhotoView;
    private USBMultiPbWallListAdapter photoWallListAdapter;
    private USBMultiPbWallGridAdapter photoWallGridAdapter;
    private Activity activity;
    private static int section = 1;
    private Map<String, Integer> sectionMap = new HashMap<String, Integer>();
    private OperationMode curOperationMode = OperationMode.MODE_BROWSE;
    private List<UsbPbItemInfo> pbItemInfoList;
    private Handler handler;
    private FileType fileType = FileType.FILE_PHOTO;
    private PhotoWallPreviewType photoWallPreviewType = PhotoWallPreviewType.PREVIEW_TYPE_LIST;
    private int visiblePosition = 0;

    private String baseUrl = "http://localhost:8000/";
    private  SortRule sortRule = SortRule.CHANGE_TIME_DESCENDING;

    public USBMultiPbFragmentPresenter(Activity activity, FileType fileType) {
        super(activity);
        this.activity = activity;
        handler = new Handler();
        this.fileType = fileType;
    }

    public void setView(USBMultiPbFragmentView localPhotoWallView) {
        this.multiPbPhotoView = localPhotoWallView;
        initCfg();
    }

    private List<UsbPbItemInfo> getPhotoInfoList(FileType fileType, SortRule sortRule) {
        List<UsbPbItemInfo> usbPbItemInfos = null;
        if (fileType == FileType.FILE_PHOTO) {
            usbPbItemInfos = UsbFileHelper.getInstance().getPhotoFile(sortRule);
        } else if(fileType == FileType.FILE_VIDEO){
            usbPbItemInfos = UsbFileHelper.getInstance().getVideoFile(sortRule);
        }else if(fileType == FileType.FILE_LOCK){
            usbPbItemInfos = UsbFileHelper.getInstance().getLockFile(sortRule);
        }
        AppLog.i(TAG, "fileList size=" + (usbPbItemInfos == null ? 0 : usbPbItemInfos.size()));
        return usbPbItemInfos;
    }

    public void loadPhotoWall() {
        MyProgressDialog.showProgressDialog(activity, R.string.loading);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<UsbPbItemInfo> tempList = getPhotoInfoList(fileType,sortRule);
                if (tempList == null || tempList.size() <= 0) {
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
                            setAdaper(tempList);
                            MyProgressDialog.closeProgressDialog();
                        }
                    });
                }
//                test.showTestLog("end load file list");
            }
        }).start();
    }

    public void setAdaper(List<UsbPbItemInfo> list) {
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
                photoWallListAdapter = new USBMultiPbWallListAdapter(activity, pbItemInfoList, fileType);
                multiPbPhotoView.setListViewAdapter(photoWallListAdapter);
            }
        } else {
            multiPbPhotoView.setGridViewVisibility(View.VISIBLE);
            multiPbPhotoView.setListViewVisibility(View.GONE);
            if (photoWallGridAdapter != null) {
                photoWallGridAdapter.notifyDataSetChanged();
            } else {
                photoWallGridAdapter = (new USBMultiPbWallGridAdapter(activity, pbItemInfoList, fileType));
                multiPbPhotoView.setGridViewAdapter(photoWallGridAdapter);
            }
        }
    }

    public void refreshPhotoWall() {
        AppLog.d(TAG, "refreshPhotoWall layoutType=" + photoWallPreviewType);
        List<UsbPbItemInfo> pbItemInfoList = getPhotoInfoList(fileType,sortRule);
        if (pbItemInfoList == null || pbItemInfoList.size() <= 0) {
            multiPbPhotoView.setGridViewVisibility(View.GONE);
            multiPbPhotoView.setListViewVisibility(View.GONE);
            multiPbPhotoView.setNoContentTxvVisibility(View.VISIBLE);
        } else {
            multiPbPhotoView.setNoContentTxvVisibility(View.GONE);
            setAdaper(pbItemInfoList);
        }
    }

    public void setVisiblePosition(int visiblePosition) {
        this.visiblePosition = visiblePosition;
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
            AppLog.i(TAG, "listViewSelectOrCancelOnce curOperationMode=" + curOperationMode);
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
//            AppLog.i(TAG, "listViewSelectOrCancelOnce curOperationMode=" + curOperationMode);
            if (fileType == FileType.FILE_PHOTO) {
                Intent intent = new Intent();
                intent.putExtra("curfilePosition", position);
                intent.setClass(activity, UsbPhotoPbActivity.class);
                activity.startActivity(intent);
            } else {
//                Intent intent = new Intent();
//                intent.putExtra("curfilePath", videoPath);
//                intent.putExtra("curfilePosition", position);
//                intent.setClass(activity, LocalVideoPbActivity.class);
//                activity.startActivity(intent);
                playVideo(pbItemInfoList.get(position), position);
            }

        } else {
            photoWallListAdapter.changeSelectionState(position);
            multiPbPhotoView.setPhotoSelectNumText(photoWallListAdapter.getSelectedCount());
        }

    }

    public void gridViewSelectOrCancelOnce(int position) {
        AppLog.i(TAG, "gridViewSelectOrCancelOnce positon=" + position + " photoWallPreviewType=" + photoWallPreviewType);
//        String videoPath = pbItemInfoList.get(position).getFilePath();
        if (curOperationMode == OperationMode.MODE_BROWSE) {
            if (fileType == FileType.FILE_PHOTO) {
                Intent intent = new Intent();
                intent.putExtra("curfilePosition", position);
                intent.setClass(activity, UsbPhotoPbActivity.class);
                activity.startActivity(intent);
            } else {
//                Intent intent = new Intent();
//                intent.putExtra("curfilePath", videoPath);
//                intent.putExtra("curfilePosition", position);
//                intent.setClass(activity, LocalVideoPbActivity.class);
//                activity.startActivity(intent);
//                startHttpServer(pbItemInfoList.get(position).getFile());
                playVideo(pbItemInfoList.get(position), position);
            }
        } else {
            photoWallGridAdapter.changeCheckBoxState(position, curOperationMode);
            multiPbPhotoView.setPhotoSelectNumText(photoWallGridAdapter.getSelectedCount());
        }

    }


    public void selectOrCancelAll(boolean isSelectAll) {
        AppLog.d(TAG, "fileType=" + fileType + " curOperationMode= " + curOperationMode);
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

    public List<UsbPbItemInfo> getSelectedList() {
        if (photoWallPreviewType == PhotoWallPreviewType.PREVIEW_TYPE_LIST) {
            return photoWallListAdapter.getSelectedList();
        } else {
            return photoWallGridAdapter.getCheckedItemsList();
        }
    }


    private void playVideo(UsbPbItemInfo usbPbItemInfo, int position) {
//        Intent myIntent = new Intent(android.content.Intent.ACTION_VIEW);
//        myIntent.setData(Uri.parse(baseUrl + usbFile.getParent().getName()+"/"+ usbFile.getName()));
//        try {
//            activity.startActivity(myIntent);
//        } catch (ActivityNotFoundException e) {
//            Toast.makeText(activity, "Could no find an app for that file!",
//                    Toast.LENGTH_LONG).show();
//        }

//        Intent myIntent = new Intent(activity, UsbVideoPbActivity.class);
//        myIntent.setData(Uri.parse(baseUrl + usbPbItemInfo.getFile().getParent().getName() + "/" + usbPbItemInfo.getFile().getName()));
//        myIntent.putExtra("position", position);
//        activity.startActivity(myIntent);

        try {
            Intent myIntent = null;
//            myIntent = new Intent(activity, Class.forName("com.icatch.usbcam.ui.usbpb.activity.VideoPlayerActivity"));
            myIntent = new Intent(activity, VideoPlayerActivity.class);
            myIntent.setData(Uri.parse(baseUrl + usbPbItemInfo.getFile().getParent().getName() + "/" + usbPbItemInfo.getFile().getName()));
            myIntent.putExtra("position", position);
            myIntent.putExtra("filename", usbPbItemInfo.getFile().getName());
            myIntent.putExtra("fileType", fileType.ordinal());
            activity.startActivity(myIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setScrollState(boolean scrollState) {
        if (photoWallListAdapter != null) {
            photoWallListAdapter.setScroll(scrollState);
        }
    }

    public void changeSortRule(SortRule sortType) {
        if(sortType == sortRule){
            return;
        }
        sortRule = sortType;
        loadPhotoWall();
    }
}
