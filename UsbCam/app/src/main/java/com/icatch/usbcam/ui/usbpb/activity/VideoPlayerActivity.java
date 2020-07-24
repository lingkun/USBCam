package com.icatch.usbcam.ui.usbpb.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.github.mjdev.libaums.fs.UsbFile;
import com.github.mjdev.libaums.fs.UsbFileInputStream;
import com.icatch.usbcam.R;
import com.icatch.usbcam.app.mycamera.CameraManager;
import com.icatch.usbcam.app.mycamera.MyCamera;
import com.icatch.usbcam.common.type.FileType;
import com.icatch.usbcam.common.type.UsbSdCardStatus;
import com.icatch.usbcam.data.appinfo.AppInfo;
import com.icatch.usbcam.engine.event.EventPollManager;
import com.icatch.usbcam.ui.preview.PreviewActivity;
import com.icatch.usbcam.ui.usbpb.UsbFileHelper;
import com.icatch.usbcam.ui.usbpb.UsbPbItemInfo;
import com.icatch.usbcam.utils.FileOpertion.FileTools;
import com.icatch.usbcam.widget.DownloadProgressDialog;
import com.icatch.usbcam.ui.base.BaseActivity;
import com.icatchtek.basecomponent.activitymanager.MActivityManager;
import com.icatchtek.basecomponent.prompt.AppDialog;
import com.icatchtek.basecomponent.prompt.MyProgressDialog;
import com.icatchtek.basecomponent.prompt.MyToast;
import com.icatchtek.baseutil.FileOper;
import com.icatchtek.baseutil.log.AppLog;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.cache.CacheFactory;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;
import com.shuyu.gsyvideoplayer.video.NormalGSYVideoPlayer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import tv.danmaku.ijk.media.exo2.Exo2PlayerManager;
import tv.danmaku.ijk.media.exo2.ExoPlayerCacheManager;

public class VideoPlayerActivity extends BaseActivity {
    private static String TAG = VideoPlayerActivity.class.getSimpleName();
    NormalGSYVideoPlayer videoPlayer;
    private UsbFile usbFile;
    private boolean isRemote = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        init();
    }

    private void init() {
//        GSYVideoManager.instance().set(this, GSYVideoType.SYSTEMPLAYER);
//        PlayerFactory.setPlayManager(new SystemPlayerManager());
        PlayerFactory.setPlayManager(Exo2PlayerManager.class);
        CacheFactory.setCacheManager(ExoPlayerCacheManager.class);
        videoPlayer =  findViewById(R.id.video_player);
        Intent intent = getIntent();
        Uri uri = intent.getData();
        isRemote = intent.getBooleanExtra("isRemote",true);
        Log.d("VideoPlayerActivity ", "video url:" + uri.toString());
        Log.d("VideoPlayerActivity ", "video path:" + uri.getPath());
        String fileName = intent.getStringExtra("filename");
        List<UsbPbItemInfo> usbPbItemInfos = null;
        int position = intent.getIntExtra("position", -1);
        int fileType = intent.getIntExtra("fileType", -1);
        if(fileType == FileType.FILE_VIDEO.ordinal()){
            usbPbItemInfos = UsbFileHelper.getInstance().getUsbVideoList();
        }else if(fileType == FileType.FILE_LOCK.ordinal()){
            usbPbItemInfos = UsbFileHelper.getInstance().getUsbLockFileList();
        }
        if (position >= 0 && usbPbItemInfos != null && usbPbItemInfos.size() > position) {
            usbFile = usbPbItemInfos.get(position).getFile();
        }
        videoPlayer.setUp(uri.toString(), false, fileName);
//        videoPlayer.setDrawingCacheEnabled(false);

        //增加封面
//        ImageView imageView = new ImageView(this);
//        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        videoPlayer.setThumbImageView(imageView);
        //增加title
        videoPlayer.getTitleTextView().setVisibility(View.VISIBLE);
        //设置返回键
        videoPlayer.getBackButton().setVisibility(View.VISIBLE);
        //设置旋转
//        orientationUtils = new OrientationUtils(this, videoPlayer);
//        //设置全屏按键功能,这是使用的是选择屏幕，而不是全屏
//        videoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                orientationUtils.resolveByClick();
//            }
//        });
        if(isRemote){
            videoPlayer.setFullHideStatusBar(true);
            videoPlayer.getFullscreenButton().setVisibility(View.VISIBLE);
            videoPlayer.getFullscreenButton().setImageResource(R.drawable.ic_file_download_white_24dp);
            videoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    download();
                }
            });
        }else {
            videoPlayer.getFullscreenButton().setVisibility(View.GONE);
        }

        //是否可以滑动调整
        videoPlayer.setIsTouchWiget(false);
        //设置返回按键功能
        videoPlayer.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        videoPlayer.startPlayLogic();
    }


    @Override
    protected void onPause() {
        super.onPause();
        videoPlayer.onVideoPause();
        clearEventPollListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoPlayer.onVideoResume();
        setEventPollListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CacheFactory.getCacheManager().release();
//        if (orientationUtils != null)
//            orientationUtils.releaseListener();
    }

    @Override
    public void onBackPressed() {
        //先返回正常状态
//        if (orientationUtils.getScreenType() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
//            videoPlayer.getFullscreenButton().performClick();
//            return;
//        }
        //释放所有
//        videoPlayer.release();

        videoPlayer.onVideoPause();
        videoPlayer.getGSYVideoManager().releaseMediaPlayer();
        videoPlayer.setVideoAllCallBack(null);
        GSYVideoManager.releaseAllVideos();
        super.onBackPressed();
    }


    private void download() {
        if (usbFile == null) {
            return;
        }
        videoPlayer.onVideoPause();
        CopyTaskParam param = new CopyTaskParam();
        param.from = usbFile;
        String downloadFolder = Environment.getExternalStorageDirectory().toString() + AppInfo.DOWNLOAD_PATH_VIDEO;
        FileOper.createDirectory(downloadFolder);
        String path = downloadFolder + usbFile.getName();
        String curFilePath = FileTools.chooseUniqueFilename(path);
        param.to = new File(curFilePath);
        new CopyTask().execute(param);
    }

    private static class CopyTaskParam {
        /* package */ UsbFile from;
        /* package */ File to;
    }

    /**
     * Asynchronous task to copy a file from the mass storage device connected
     * via USB to the internal storage.
     *
     * @author mjahnen
     */
    private class CopyTask extends AsyncTask<CopyTaskParam, Integer, Void> {
        private DownloadProgressDialog dialog;
        private CopyTaskParam param;
        private long startTime ;

        public CopyTask() {
            dialog = new DownloadProgressDialog(VideoPlayerActivity.this);
            dialog.setTitle(getString(R.string.download_file));
            dialog.setMessage(getString(R.string.current_download_progress));
            dialog.setIndeterminate(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected Void doInBackground(CopyTaskParam... params) {
            startTime = System.currentTimeMillis();
            param = params[0];
            try {
                OutputStream out = new BufferedOutputStream(new FileOutputStream(param.to));
                InputStream inputStream = new UsbFileInputStream(param.from);
                byte[] bytes = new byte[getChunkSize()];
                int count;
                long total = 0;
                AppLog.d(TAG, "Copy file with length: " + param.from.getLength());
                while ((count = inputStream.read(bytes)) != -1) {
                    out.write(bytes, 0, count);
                    total += count;
                    int progress = (int) total;
                    if (param.from.getLength() > Integer.MAX_VALUE) {
                        progress = (int) (total / 1024);
                    }
                    publishProgress(progress);
                }

                out.close();
                inputStream.close();
            } catch (IOException e) {
                Log.e(TAG, "error copying!", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            dialog.dismiss();
//            AppLog.d(TAG, "copy time: " + (System.currentTimeMillis() - startTime));
//            MyToast.show(VideoPlayerActivity.this,"copy time: " + (System.currentTimeMillis() - startTime));
            String message = getResources().getString(R.string.message_download_to).replace("$1$", AppInfo.DOWNLOAD_PATH_VIDEO);
            MyToast.show(VideoPlayerActivity.this, message);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int max = (int) param.from.getLength();
            if (param.from.getLength() > Integer.MAX_VALUE) {
                max = (int) (param.from.getLength() / 1024);
            }
            dialog.setMax(max);
            dialog.setProgress(values[0]);
        }
    }

    private int getChunkSize() {
        int chunkSize = UsbFileHelper.getInstance().getChunkSize();
        if (chunkSize <= 0) {
            chunkSize = 1024 * 1024 * 500;
        }
        Log.d(TAG, "Chunk size: " + chunkSize);
        return chunkSize;
    }

    private void gotoPreview() {
        MyProgressDialog.showProgressDialog(this, R.string.action_processing);
        stopResetTimer();
        stopSDStatusPolling();
        UsbFileHelper.getInstance().closeUsbDevice();
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean ret = CameraManager.getInstance().getCurCamera().reconnect(VideoPlayerActivity.this, false);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MyProgressDialog.closeProgressDialog();
                        MActivityManager.getInstance().finishAllActivityExceptOne(PreviewActivity.class);
//                        MActivityManager.getInstance().finishAllActivity();
//                        Intent intent = new Intent();
//                        intent.setClass(UsbVideoPbActivity.this, PreviewActivity.class);
//                        startActivity(intent);

                    }
                });
            }
        }).start();
    }

    void stopSDStatusPolling() {
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
                        String errorInfo = UsbSdCardStatus.getCardStatueInfo(VideoPlayerActivity.this, errorCode);
                        AppDialog.showDialogWarn(VideoPlayerActivity.this, errorInfo, new AppDialog.OnDialogSureClickListener() {
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
                        AppDialog.showDialogWarn(VideoPlayerActivity.this, R.string.dialog_card_removed, new AppDialog.OnDialogSureClickListener() {
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