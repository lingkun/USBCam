package com.icatch.usbcam.ui.usbpb.presenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.github.mjdev.libaums.fs.UsbFile;
import com.github.mjdev.libaums.fs.UsbFileInputStream;
import com.icatch.usbcam.R;
import com.icatch.usbcam.app.mycamera.CameraManager;
import com.icatch.usbcam.app.mycamera.MyCamera;
import com.icatch.usbcam.common.type.UsbSdCardStatus;
import com.icatch.usbcam.data.SystemInfo;
import com.icatch.usbcam.data.appinfo.AppInfo;
import com.icatch.usbcam.engine.event.EventPollManager;
import com.icatch.usbcam.ui.base.BasePresenter;
import com.icatch.usbcam.ui.preview.PreviewActivity;
import com.icatch.usbcam.ui.usbpb.UsbFileHelper;
import com.icatch.usbcam.ui.usbpb.UsbPbItemInfo;
import com.icatch.usbcam.ui.usbpb.adapter.UsbPhotoPbViewPagerAdapter;
import com.icatch.usbcam.ui.usbpb.contract.UsbPhotoPbView;
import com.icatch.usbcam.utils.FileOpertion.FileTools;
import com.icatch.usbcam.utils.MediaRefresh;
import com.icatchtek.basecomponent.activitymanager.MActivityManager;
import com.icatchtek.basecomponent.prompt.AppDialog;
import com.icatchtek.basecomponent.prompt.MyProgressDialog;
import com.icatchtek.basecomponent.prompt.MyToast;
import com.icatchtek.baseutil.FileOper;
import com.icatchtek.baseutil.log.AppLog;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class UsbPhotoPbPresenter extends BasePresenter {
    private String TAG = UsbPhotoPbPresenter.class.getSimpleName();
    private UsbPhotoPbView photoPbView;
    private Activity activity;
    private List<UsbPbItemInfo> fileList;
    private UsbPhotoPbViewPagerAdapter viewPagerAdapter;
    private Handler handler;
    private int curPhotoIdx;
    private int lastItem = -1;
    private int tempLastItem = -1;
    private boolean isScrolling = false;
    private int slideDirection = DIRECTION_RIGHT;

    private final static int DIRECTION_RIGHT = 0x1;
    private final static int DIRECTION_LEFT = 0x2;
    private final static int DIRECTION_UNKNOWN = 0x4;

    public String downloadingFilename;
    public long downloadProcess;
    public long downloadingFilesize;
    private ExecutorService executor;
    private Future<Object> future;
    private String curFilePath = "";


    public UsbPhotoPbPresenter(Activity activity) {
        super(activity);
        this.activity = activity;
        handler = new Handler();
        slideDirection = DIRECTION_UNKNOWN;
    }

    public void setView(UsbPhotoPbView photoPbView) {
        this.photoPbView = photoPbView;
        initCfg();
        fileList = UsbFileHelper.getInstance().getUsbPhotoList();
        Bundle data = activity.getIntent().getExtras();
        curPhotoIdx = data.getInt("curfilePosition");
    }

    public void initView() {
        viewPagerAdapter = new UsbPhotoPbViewPagerAdapter(activity, fileList);
        viewPagerAdapter.setOnPhotoTapListener(new UsbPhotoPbViewPagerAdapter.OnPhotoTapListener() {
            @Override
            public void onPhotoTap() {
                showBar();
            }
        });
        photoPbView.setViewPagerAdapter(viewPagerAdapter);
        photoPbView.setViewPagerCurrentItem(curPhotoIdx);
        ShowCurPageNum();
        photoPbView.setOnPageChangeListener(new MyViewPagerOnPagerChangeListener());
    }

    public void showBar() {
        boolean isShowBar = photoPbView.getTopBarVisibility() == View.VISIBLE ? true : false;
        AppLog.d(TAG, "showBar isShowBar=" + isShowBar);
        if (isShowBar) {
            photoPbView.setTopBarVisibility(View.GONE);
            photoPbView.setBottomBarVisibility(View.GONE);
        } else {
            photoPbView.setTopBarVisibility(View.VISIBLE);
            photoPbView.setBottomBarVisibility(View.VISIBLE);
        }
    }

    public void delete() {
        showDeleteEnsureDialog();
    }

    public void download() {
//        showDownloadEnsureDialog();
        if (SystemInfo.getSDFreeSize() < fileList.get(curPhotoIdx).getFile().getLength()) {
            MyToast.show(activity, R.string.text_sd_card_memory_shortage);
        } else {
            MyProgressDialog.showProgressDialog(activity, R.string.dialog_downloading_single);
            executor = Executors.newSingleThreadExecutor();
            future = executor.submit(new DownloadThread(), null);
        }
    }


    public void loadPreviousImage() {
        AppLog.d(TAG, "loadPreviousImage=");
        if (curPhotoIdx > 0) {
            curPhotoIdx--;
        }
        slideDirection = DIRECTION_LEFT;
        photoPbView.setViewPagerCurrentItem(curPhotoIdx);
    }

    public void loadNextImage() {
        AppLog.d(TAG, "loadNextImage=");
        if (curPhotoIdx < fileList.size() - 1) {
            curPhotoIdx++;
        }
        slideDirection = DIRECTION_RIGHT;
        photoPbView.setViewPagerCurrentItem(curPhotoIdx);
    }


    private class MyViewPagerOnPagerChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {

            switch (arg0) {
                case ViewPager.SCROLL_STATE_DRAGGING:
                    isScrolling = true;
                    tempLastItem = photoPbView.getViewPagerCurrentItem();
                    break;
                case ViewPager.SCROLL_STATE_SETTLING:
                    if (isScrolling == true && tempLastItem != -1 && tempLastItem != photoPbView.getViewPagerCurrentItem()) {
                        lastItem = tempLastItem;
                    }

                    curPhotoIdx = photoPbView.getViewPagerCurrentItem();
                    isScrolling = false;
                    ShowCurPageNum();
                    break;
                case ViewPager.SCROLL_STATE_IDLE:
                    break;
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            if (isScrolling) {
                if (lastItem > arg2) {
                    // 递减，向右侧滑动
                    slideDirection = DIRECTION_RIGHT;
                } else if (lastItem < arg2) {
                    // 递减，向右侧滑动
                    slideDirection = DIRECTION_LEFT;
                } else if (lastItem == arg2) {
                    slideDirection = DIRECTION_RIGHT;
                }
            }
            lastItem = arg2;
        }

        @Override
        public void onPageSelected(int arg0) {
            ShowCurPageNum();
        }
    }

    private class DownloadThread implements Runnable {
        private String TAG = "DownloadThread";
        private int curIdx = photoPbView.getViewPagerCurrentItem();

        @Override
        public void run() {
            AppLog.d(TAG, "begin DownloadThread");
            AppInfo.isDownloading = true;
            String path;

            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                path = Environment.getExternalStorageDirectory().toString() + AppInfo.DOWNLOAD_PATH_PHOTO;
            } else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyProgressDialog.closeProgressDialog();
                        MyToast.show(activity, R.string.message_download_failed);
                    }
                });
                return;
            }
            String fileName = fileList.get(curIdx).getFileName();
            UsbFile usbFile = fileList.get(curIdx).getFile();
            AppLog.d(TAG, "------------fileName =" + fileName);
            FileOper.createDirectory(path);
            downloadingFilename = path + fileName;
            downloadingFilesize = fileList.get(curIdx).getFile().getLength();
            File tempFile = new File(downloadingFilename);
            curFilePath = FileTools.chooseUniqueFilename(downloadingFilename);
            File toFile = new File(curFilePath);
            try {
                OutputStream out = new BufferedOutputStream(new FileOutputStream(toFile));
                InputStream inputStream = new UsbFileInputStream(usbFile);
                byte[] bytes = new byte[1024 * 1024];
                int count;
                long total = 0;
                while ((count = inputStream.read(bytes)) != -1) {
                    out.write(bytes, 0, count);
                    total += count;
//                    int progress = (int) total;
//                    if(param.from.getLength() > Integer.MAX_VALUE) {
//                        progress = (int) (total / 1024);
//                    }
//                    publishProgress(progress);
                }

                out.close();
                inputStream.close();
                MediaRefresh.scanFileAsync(activity, downloadingFilename);
                AppInfo.isDownloading = false;
                final String message = activity.getResources().getString(R.string.message_download_to).replace("$1$", AppInfo.DOWNLOAD_PATH_PHOTO);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyProgressDialog.closeProgressDialog();
                        MyToast.show(activity, message);
                    }
                });
            } catch (IOException e) {
                Log.e(TAG, "error copying!", e);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyProgressDialog.closeProgressDialog();
                        MyToast.show(activity, R.string.message_download_failed);
                    }
                });
            }


            AppLog.d(TAG, "end DownloadThread");
        }
    }

    private class DeleteThread implements Runnable {
        @Override
        public void run() {
            curPhotoIdx = photoPbView.getViewPagerCurrentItem();
            UsbFile curFile = fileList.get(curPhotoIdx).getFile();
            try {
                curFile.delete();
//                fileList = UsbFileHelper.getInstance().getUsbPhotoList();
                fileList.remove(curPhotoIdx);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyProgressDialog.closeProgressDialog();
                        viewPagerAdapter.notifyDataSetChanged();
                        int photoNums = fileList.size();
                        if (photoNums == 0) {
                            activity.finish();
                            return;
                        } else {
                            if (curPhotoIdx == photoNums) {
                                curPhotoIdx--;
                            }
                            AppLog.d(TAG, "photoNums=" + photoNums + " curPhotoIdx=" + curPhotoIdx);
                            photoPbView.setViewPagerCurrentItem(curPhotoIdx);
                            ShowCurPageNum();
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyProgressDialog.closeProgressDialog();
                        MyToast.show(activity, R.string.dialog_delete_failed_single);
                    }
                });
            }
            AppLog.d(TAG, "end DeleteThread");
        }
    }

    private void ShowCurPageNum() {
        int curPhoto = photoPbView.getViewPagerCurrentItem() + 1;
        String indexInfo = curPhoto + "/" + fileList.size();
        photoPbView.setIndexInfoTxv(indexInfo);
    }

    public void showDownloadEnsureDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setTitle(R.string.dialog_downloading_single);
        long videoFileSize = 0;
        videoFileSize = fileList.get(curPhotoIdx).getFile().getLength() / 1024 / 1024;
        long minute = videoFileSize / 60;
        long seconds = videoFileSize % 60;
        CharSequence what = activity.getResources().getString(R.string.gallery_download_with_vid_msg).replace("$1$", "1").replace("$3$", String.valueOf
                (seconds)).replace("$2$", String.valueOf(minute));
        builder.setMessage(what);
        builder.setNegativeButton(R.string.gallery_download, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                AppLog.d(TAG, "showProgressDialog");
                downloadProcess = 0;
                if (SystemInfo.getSDFreeSize() < fileList.get(curPhotoIdx).getFile().getLength()) {
                    dialog.dismiss();
                    MyToast.show(activity, R.string.text_sd_card_memory_shortage);
                } else {
                    MyProgressDialog.showProgressDialog(activity, R.string.dialog_downloading_single);
                    executor = Executors.newSingleThreadExecutor();
                    future = executor.submit(new DownloadThread(), null);
                }

            }
        });
        builder.setPositiveButton(R.string.title_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public void showDeleteEnsureDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setTitle(R.string.image_delete_des);
        builder.setNegativeButton(R.string.title_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // 这里添加点击确定后的逻辑
                MyProgressDialog.showProgressDialog(activity, R.string.dialog_deleting);
                executor = Executors.newSingleThreadExecutor();
                future = executor.submit(new DeleteThread(), null);
            }
        });
        builder.setPositiveButton(R.string.title_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // 这里添加点击确定后的逻辑
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void gotoPreview() {
        MyProgressDialog.showProgressDialog(activity, R.string.action_processing);
        stopResetTimer();
        stopSDStatusPolling();
        UsbFileHelper.getInstance().closeUsbDevice();
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean ret = CameraManager.getInstance().getCurCamera().reconnect(activity, false);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MyProgressDialog.closeProgressDialog();
                        MActivityManager.getInstance().finishAllActivityExceptOne(PreviewActivity.class);
                    }
                });
            }
        }).start();
    }

    private void stopSDStatusPolling() {
        MyCamera camera = CameraManager.getInstance().getCurCamera();
        if (camera != null && camera.getEventPollManager() != null) {
            camera.getEventPollManager().stopPolling();
        }
    }

    public void setEventPollListener() {
        final MyCamera camera = CameraManager.getInstance().getCurCamera();
        if (camera != null && camera.getEventPollManager() != null) {
            camera.getEventPollManager().setSdCardStatusChange(new EventPollManager.OnSdCardStatusChangeListener() {
                @Override
                public void onSdCardError(int errorCode) {
                    AppLog.d(TAG, "onSdCardError errorCode:" + errorCode);
                    if (errorCode == UsbSdCardStatus.SD_CARD_ERR_CARD_ERROR
                            || errorCode == UsbSdCardStatus.SD_CARD_ERR_CARD_LOCKED
                            || errorCode == UsbSdCardStatus.SD_CARD_ERR_INSUFFICIENT_DISK_SPACE
                            || errorCode == UsbSdCardStatus.SD_CARD_ERR_TO_POWER_OFF) {
                        clearEventPollListener();
                        String errorInfo = UsbSdCardStatus.getCardStatueInfo(activity, errorCode);
                        AppDialog.showDialogWarn(activity, errorInfo, new AppDialog.OnDialogSureClickListener() {
                            @Override
                            public void onSure() {
                                gotoPreview();
                            }
                        });
                    }
                }

                @Override
                public void onSdStatus(boolean isExist) {
                    if (!isExist) {
                        clearEventPollListener();
                        AppDialog.showDialogWarn(activity, R.string.dialog_card_removed, new AppDialog.OnDialogSureClickListener() {
                            @Override
                            public void onSure() {
                                gotoPreview();
                            }
                        });
                    }
                    AppLog.d(TAG, "onSdStatus isSDCardExist:" + isExist);
                }

                @Override
                public void onRecStatusChange(boolean isRec) {

                }

                @Override
                public void onSdInsertError() {

                }
            });
        }
    }

    public void clearEventPollListener() {
        MyCamera camera = CameraManager.getInstance().getCurCamera();
        if (camera != null && camera.getEventPollManager() != null) {
            camera.getEventPollManager().setSdCardStatusChange(null);
        }
    }

    public void stopResetTimer() {
        MyCamera camera = CameraManager.getInstance().getCurCamera();
        if (camera != null) {
            camera.stopResetTimer();
        }
    }
}

