package com.icatch.usbcam.ui.usbpb.activity;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mjdev.libaums.fs.UsbFile;
import com.github.mjdev.libaums.fs.UsbFileInputStream;
import com.icatch.usbcam.R;
import com.icatch.usbcam.app.mycamera.CameraManager;
import com.icatch.usbcam.app.mycamera.MyCamera;
import com.icatch.usbcam.common.type.UsbSdCardStatus;
import com.icatch.usbcam.data.appinfo.AppInfo;
import com.icatch.usbcam.engine.event.EventPollManager;
import com.icatch.usbcam.ui.preview.PreviewActivity;
import com.icatch.usbcam.ui.usbpb.UsbFileHelper;
import com.icatch.usbcam.ui.usbpb.UsbPbItemInfo;
import com.icatch.usbcam.utils.FileOpertion.FileTools;
import com.icatch.usbcam.ui.base.BaseActivity;
import com.icatchtek.basecomponent.activitymanager.MActivityManager;
import com.icatchtek.basecomponent.prompt.AppDialog;
import com.icatchtek.basecomponent.prompt.MyProgressDialog;
import com.icatchtek.basecomponent.prompt.MyToast;
import com.icatchtek.baseutil.FileOper;
import com.icatchtek.baseutil.log.AppLog;
import com.icatchtek.module.mediaplayer.CustomVideoView;
import com.icatchtek.module.mediaplayer.PixelUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.List;

public class UsbVideoPbActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {
    private final String TAG = UsbVideoPbActivity.class.getSimpleName();
    //可以播放的本地视频文件地址组
    private RelativeLayout layoutVideoSeekBar;
    private RelativeLayout layoutTopBar;
    private ImageButton imbBack;
    private TextView txvCurTime;
    private SeekBar videoSeekBar;
    private TextView txvRemainTime;
    //视频视图
    private CustomVideoView videoView;
//    private IjkVideoView videoView;
//    private StandardGSYVideoPlayer videoView;

    //全屏播放控件
    private RelativeLayout layoutVideoFullScreen;
    private ImageButton imbPlayPauseFullScreen;
    private ImageButton imbPlayBackFullScreen;
    private ImageButton imbPlayForwardFullScreen;
    //    private ImageButton imbFullScreenBack;
//视频控制控件
    private RelativeLayout layoutVideoControl;
    private ImageButton imbPlayPause;
    private ImageButton imbFullScreen;
    //声音调节控件
    private RelativeLayout layoutVolumeSeekBar;
    private ImageButton imbVolumeModeSwitcher;
    private SeekBar volumeSeekBar;
    //划屏调节音量变量
    private float startY;
    private float startX;
    private int touchRange;
    private int mMaxStreamVoice;
    private float mDeltaVolume;
    private int adjustedVoice;
    private ProgressBar mVolumeProBar;
    //其他成员变量
    private AudioManager mAudioManager;
    private static final int UPDATE_VIDEO_SEEKBAR = 101;    //更新播放时间消息识别标志
    //    private static final int UPDATE_VOICE = 201;
    private boolean isFullScreen = true;
    private boolean isVolumeSilence = false;
    private boolean isHideAll = false;
    private int mCurStreamVolume;
    private int mLastStreamVolume;
    private UsbFile usbFile;
    private ImageButton downloadBtn;
//    private static final int mForwardTimeOnce = 3000;   //快进、快退按键，点击一次调节的步长（ms）
    // 接收Message,更新主线程UI
    private  Handler UIhandler;

    private static class  MyHandler extends Handler {
        WeakReference<UsbVideoPbActivity> weakReference;

        public MyHandler(UsbVideoPbActivity activity) {
            weakReference = new WeakReference<>(activity);
        }
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            //更新视频播放进度显示
            UsbVideoPbActivity activity = weakReference.get();
            if(activity == null) {
                return;
            }
            if (msg.what == UPDATE_VIDEO_SEEKBAR) {
                // 获取视频当前的播放时间
                int currentPosition = activity.videoView.getCurrentPosition();
                // 获取视频播放的总时间
                int totalDuration = activity.videoView.getDuration();

                // 格式化视频播放时间
                activity.updateTextViewWithTimeFormat(activity.txvRemainTime, totalDuration);
                activity.updateTextViewWithTimeFormat(activity.txvCurTime, currentPosition);
                activity.videoSeekBar.setMax(totalDuration);
                activity.videoSeekBar.setProgress(currentPosition);
                //自己通知自己更新
                activity.UIhandler.sendEmptyMessageDelayed(UPDATE_VIDEO_SEEKBAR, 100); //每100ms更新一次
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usb_video_pb);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // do not display menu bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        UIhandler = new MyHandler(this);
        initUI();
        //从上一个活动中获取视频路径并开始播放
        List<UsbPbItemInfo> usbPbItemInfos = UsbFileHelper.getInstance().getUsbVideoList();
        Intent intent = getIntent();
        Uri uri = intent.getData();
        int position = intent.getIntExtra("position", -1);
        if (position >= 0 && usbPbItemInfos != null && usbPbItemInfos.size() > position) {
            usbFile = usbPbItemInfos.get(position).getFile();
        }
        setVideoPath(uri);
        setVideoSeekBarEvent();     //设置视频监听事件
        setVoiceSeekBarEvent();     //设置音量监听事件
    }


    private void initUI() {
        //播放进度控件
        downloadBtn = findViewById(R.id.imb_usb_video_download);
        downloadBtn.setOnClickListener(this);
        layoutTopBar = (RelativeLayout) findViewById(R.id.layout_top_bar);
        layoutVideoSeekBar = (RelativeLayout) findViewById(R.id.layout_local_video_seekbar);
        imbBack = (ImageButton) findViewById(R.id.imb_local_video_player_back);
        txvCurTime = (TextView) findViewById(R.id.txv_local_video_total_time);
        videoSeekBar = (SeekBar) findViewById(R.id.seekbar_local_video_play_seek);
        txvRemainTime = (TextView) findViewById(R.id.txv_local_video_current_time);
        //视频视图、播放按键
        videoView = (CustomVideoView) findViewById(R.id.videoview_local_video_player_view);
        layoutVideoControl = (RelativeLayout) findViewById(R.id.layout_local_video_play);
        imbPlayPause = (ImageButton) findViewById(R.id.imb_local_video_play_pause);
        imbFullScreen = (ImageButton) findViewById(R.id.imb_local_video_full_screen);
        //音量调节控件
        layoutVolumeSeekBar = (RelativeLayout) findViewById(R.id.layout_local_video_volume);
        imbVolumeModeSwitcher = (ImageButton) findViewById(R.id.imb_local_video_volume_mode);
        volumeSeekBar = (SeekBar) findViewById(R.id.seekbar_local_video_voice_seek);
        //音量变量初始化
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mMaxStreamVoice = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mCurStreamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        volumeSeekBar.setMax(mMaxStreamVoice);
        volumeSeekBar.setProgress(mCurStreamVolume);
        updateVoice(mCurStreamVolume);
        //全屏播放控件（因布局位置的改变，同时图像源也需要变为背景透明，需要为全屏后的控制控件创建新的实例）
        layoutVideoFullScreen = (RelativeLayout) findViewById(R.id.layout_local_video_full_screen);
        imbPlayPauseFullScreen = (ImageButton) findViewById(R.id.imb_local_video_play_pause_full_screen);
        imbPlayBackFullScreen = (ImageButton) findViewById(R.id.imb_local_video_play_back_full_screen);
        imbPlayForwardFullScreen = (ImageButton) findViewById(R.id.imb_local_video_play_forward_full_screen);
        mVolumeProBar = (ProgressBar) findViewById(R.id.prb_local_video_voice_full_screen);
//        mVolumeProBar.setVisibility(View.GONE);
        //全屏前后对应的实例具有相同的点击响应功能
        imbPlayPause.setOnClickListener(this);
        imbPlayPauseFullScreen.setOnClickListener(this);
        imbPlayForwardFullScreen.setOnClickListener(this);
        imbPlayBackFullScreen.setOnClickListener(this);
        imbFullScreen.setOnClickListener(this);
        imbBack.setOnClickListener(this);
        imbVolumeModeSwitcher.setOnClickListener(this);
        videoView.setOnClickListener(this);
        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                AppLog.d(TAG,"onInfo what:" + what);

                switch (what) {
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        //开始卡顿-----需要做一些处理(比如：显示加载动画，或者当前下载速度)
                        mVolumeProBar.setVisibility(View.VISIBLE);
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        //卡顿结束   (隐藏加载动画，或者加载速度)
                        mVolumeProBar.setVisibility(View.GONE);
                        break;
                        default:
                            mVolumeProBar.setVisibility(View.GONE);
                }
                return true;
            }
        });

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                AppLog.d(TAG,"onPrepared");
                mVolumeProBar.setVisibility(View.GONE);
            }
        });
    }

    //设置视频路径并初始化播放控件
    private void setVideoPath(Uri uri) {
        videoView.setVideoURI(uri);
        videoView.start();
        UIhandler.sendEmptyMessage(UPDATE_VIDEO_SEEKBAR);  //更新播放时间、进度显示
    }


    // 视频播放完毕：结束当前活动并停止更新播放进度条
    private void setVideoSeekBarEvent() {
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override   //播放完视频，结束当前活动，返回上一级
            public void onCompletion(MediaPlayer mp) {
                UIhandler.removeMessages(UPDATE_VIDEO_SEEKBAR);    //停止更新播放时间显示
                UIhandler.removeCallbacksAndMessages(null);
                imbPlayPauseFullScreen.setImageResource(R.drawable.ic_play_arrow_white_24dp);
                imbPlayPause.setImageResource(R.drawable.ic_play_arrow_white_24dp);
                videoView.seekTo(0);
//                finish();
            }
        });

        // 视频播放进度条拖动监听
        videoSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                UIhandler.removeMessages(UPDATE_VIDEO_SEEKBAR);
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateTextViewWithTimeFormat(txvCurTime, progress);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                // 令视频播放进度遵循seekBar停止拖动这一刻的进度
                videoView.seekTo(progress);
                imbPlayPause.setImageResource(R.drawable.ic_pause_white_24dp);
                imbPlayPauseFullScreen.setImageResource(R.drawable.ic_pause_white_24dp);
                videoView.start();
                UIhandler.sendEmptyMessage(UPDATE_VIDEO_SEEKBAR);
            }
        });
    }

    //监听音量进度条的拖动
    private void setVoiceSeekBarEvent() {
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // 设置当前设备的的音量
                updateVoice(progress);
            }
        });

    }

    @Override   //全屏时，手势划屏调节音量
    public boolean onTouch(View view, MotionEvent event) {
        if (isFullScreen) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //记录初始值
                    startY = event.getY();
                    startX = event.getX();
//                    mCurStreamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    touchRange = Math.min(getWindowManager().getDefaultDisplay().getWidth(), getWindowManager().getDefaultDisplay().getHeight());   //这里获得的是VideoView的宽、高
//                    UIhandler.removeMessages(UPDATE_VOICE); //移除可能尚未更新的（上一次的）数据
                    break;
                case MotionEvent.ACTION_MOVE:
                    //追踪手势移动值
                    float endY = event.getY();
                    float endX = event.getX();
                    float distanceY = startY - endY;
                    if (endX > getWindowManager().getDefaultDisplay().getWidth() / 2 && startX > getWindowManager().getDefaultDisplay().getWidth() / 2) { //起止点都在右屏（屏蔽点击，唯滑动有效）：调节音量
                        mDeltaVolume = (distanceY / touchRange) * mMaxStreamVoice;       //按Y向划屏比例取调节值
                    }
                    int voiceTemp = (int) (mCurStreamVolume + mDeltaVolume);
                    if (voiceTemp >= 0 && voiceTemp <= mMaxStreamVoice) {
                        adjustedVoice = voiceTemp;
                    } else if (voiceTemp < 0) {
                        adjustedVoice = 0;
                    } else {
                        adjustedVoice = mMaxStreamVoice;
                    }
                    Log.d(TAG, "onTouch: mCurStreamVolume=" + mCurStreamVolume
                            + "   mDeltaVolume=" + mDeltaVolume
                            + "   voiceTemp=" + voiceTemp
                            + "   adjustedVoice=" + adjustedVoice);
                    mVolumeProBar.setVisibility(View.VISIBLE);
                    int progress = mVolumeProBar.getProgress();
                    progress = progress + adjustedVoice;
                    mVolumeProBar.setProgress(progress);
                    break;
                case MotionEvent.ACTION_UP:
                    mVolumeProBar.setVisibility(View.GONE);
                    updateVoice(adjustedVoice);
//                    UIhandler.sendEmptyMessageDelayed(UPDATE_VOICE, 300);
                    break;
                default:
            }
        }
        return super.onTouchEvent(event);
    }

    //设置播放音量
    private void updateVoice(int progress) {
        if (progress > 0 && progress <= mMaxStreamVoice) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            volumeSeekBar.setProgress(progress);
            imbVolumeModeSwitcher.setImageResource(R.drawable.volume);    //设置非静音状态按键图标
            isVolumeSilence = false;
            mCurStreamVolume = progress;
        } else if (progress <= 0) {
            mLastStreamVolume = mCurStreamVolume;   //记录当前音量值，以便点击按键恢复音量
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            volumeSeekBar.setProgress(0);
            imbVolumeModeSwitcher.setImageResource(R.drawable.full_screen_video_btn_mute);      //设置静音状态图标
            isVolumeSilence = true;
            mCurStreamVolume = 0;
        } else {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mMaxStreamVoice, 0);
            volumeSeekBar.setProgress(mMaxStreamVoice);
            imbVolumeModeSwitcher.setImageResource(R.drawable.volume);    //设置非静音状态按键图标
            isVolumeSilence = false;
            mCurStreamVolume = mMaxStreamVoice;
        }
    }

    @Override   //这里响应屏幕配置的改变
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // 全屏(横屏)时的配置
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //设置全屏播放
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //用来屏蔽系统菜单
            setVideoViewScale(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            //隐藏系统菜单栏
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE  //防止系统栏隐藏时内容区域大小发生变化
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN         //全屏显示，但状态栏不会被隐藏覆盖，状态栏依然可见，Activity顶端布局部分会被状态遮住。
//                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION           //隐藏底部的系统导航栏
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            full(true);

//            layoutVideoFullScreen.setVisibility(View.VISIBLE);
            layoutVideoSeekBar.setVisibility(View.GONE);
            layoutVideoControl.setVisibility(View.GONE);
            layoutVolumeSeekBar.setVisibility(View.GONE);
            //由于涉及到进度的更新，全屏前后使用同一个seekbar，仅更改其布局
            txvCurTime = (TextView) findViewById(R.id.txv_local_video_total_time_full_screen);
            videoSeekBar = (SeekBar) findViewById(R.id.seekbar_local_video_play_seek_full_screen);
            txvRemainTime = (TextView) findViewById(R.id.txv_local_video_current_time_full_screen);
            setVideoSeekBarEvent();
            videoView.setOnTouchListener(this); //手势划屏调节音量
            videoView.setOnClickListener(this); //显隐控件
            isFullScreen = true;
        }
        // 非全屏(竖屏)时的配置
        else {
            //切换显示控制播放控件
            layoutVideoFullScreen.setVisibility(View.GONE);
            layoutVideoSeekBar.setVisibility(View.VISIBLE);
            layoutVolumeSeekBar.setVisibility(View.VISIBLE);
            layoutVideoControl.setVisibility(View.VISIBLE);
            //恢复系统菜单栏与系统
//            getWindow().getDecorView().setSystemUiVisibility(View.VISIBLE); //这个设置可以恢复显示系统底部导航栏，也可以恢复显示系统顶部状态栏，但状态栏的文字颜色会虽当前主题而改变
            full(false);
            //恢复播放进度栏的布局
            txvCurTime = (TextView) findViewById(R.id.txv_local_video_total_time);
            videoSeekBar = (SeekBar) findViewById(R.id.seekbar_local_video_play_seek);
            txvRemainTime = (TextView) findViewById(R.id.txv_local_video_current_time);
            setVideoSeekBarEvent();
            mVolumeProBar.setVisibility(View.GONE);
            isFullScreen = false;
        }
        UIhandler.sendEmptyMessage(UPDATE_VIDEO_SEEKBAR);

    }


    private void full(boolean enable) {
        if (enable) {
            //设置全屏播放（但要点击一下屏幕才会隐藏导航栏）
//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //用来屏蔽系统菜单
//            setVideoViewScale(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            //全屏并隐藏系统状态栏与导航栏
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN            //设置全屏
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION       //隐藏底部的系统导航栏，但首次点击屏幕会出现一次，之后不再出现
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE         //防止系统栏隐藏时内容区域大小发生变化？
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY      //消除全屏后首次点击屏幕发生的导航栏闪现
            );
        } else {
            //恢复竖屏播放
            PixelUtil.initContext(this);
            setVideoViewScale(ViewGroup.LayoutParams.MATCH_PARENT, PixelUtil.dp2px(200));
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);         //取消全屏显示
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN); //取消全屏显示（与上一句必须同时使用才有效）
            //恢复显示系统状态栏与导航栏、但状态栏文本颜色变为了浅色
            getWindow().getDecorView().setSystemUiVisibility(View.VISIBLE);
            //设置系统状态栏主题为浅色，使得文本颜色恢复深色
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    // 设置视频视图的宽、高
    private void setVideoViewScale(int width, int height) {
        ViewGroup.LayoutParams layoutParams = videoView.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        videoView.setLayoutParams(layoutParams);

        ViewGroup.LayoutParams layoutParams2 = videoView.getLayoutParams();
        layoutParams2.width = width;
        layoutParams2.height = height;
        videoView.setLayoutParams(layoutParams2);
    }

    //播放时间的格式化与设置
    private void updateTextViewWithTimeFormat(TextView textView, int millisecond) {
        //设置播放时间的显示格式
        int second = millisecond / 1000;
        int hh = second / 3600;
        int mm = second % 3600 / 60;
        int ss = second % 60;
        String str = null;
        if (hh != 0) {
            str = String.format("%02d:%02d:%02d", hh, mm, ss);
        } else if (mm != 0) {
            str = String.format("%02d:%02d", mm, ss);
        } else {
            str = String.format("0:%02d", ss);
        }
        textView.setText(str);
    }

    @Override
    public void onClick(View v) {
        int clickedViewId = v.getId();
        //播放与暂停
        if (clickedViewId == R.id.imb_local_video_play_pause || clickedViewId == R.id.imb_local_video_play_pause_full_screen) {
            if (videoView.isPlaying()) {
                videoView.pause();
                UIhandler.removeMessages(UPDATE_VIDEO_SEEKBAR);    //停止更新播放时间显示
                imbPlayPauseFullScreen.setImageResource(R.drawable.ic_play_arrow_white_24dp);
                imbPlayPause.setImageResource(R.drawable.ic_play_arrow_white_24dp);
            } else {
                videoView.start();
                UIhandler.sendEmptyMessage(UPDATE_VIDEO_SEEKBAR);  //继续更新播放时间显示
                imbPlayPauseFullScreen.setImageResource(R.drawable.ic_pause_white_24dp);
                imbPlayPause.setImageResource(R.drawable.ic_pause_white_24dp);
            }
        } else if (clickedViewId == R.id.videoview_local_video_player_view) {
            if (layoutTopBar.getVisibility() == View.VISIBLE) {
                layoutTopBar.setVisibility(View.GONE);
                layoutVideoSeekBar.setVisibility(View.GONE);
            } else {
                layoutTopBar.setVisibility(View.VISIBLE);
                layoutVideoSeekBar.setVisibility(View.VISIBLE);
            }
        } else if (clickedViewId == R.id.imb_usb_video_download) {
            if (usbFile == null) {
                return;
            }
            if (videoView.isPlaying()) {
                videoView.pause();
                UIhandler.removeMessages(UPDATE_VIDEO_SEEKBAR);    //停止更新播放时间显示
                imbPlayPauseFullScreen.setImageResource(R.drawable.ic_play_arrow_white_24dp);
                imbPlayPause.setImageResource(R.drawable.ic_play_arrow_white_24dp);
            }
            CopyTaskParam param = new CopyTaskParam();
            param.from = usbFile;
            String downloadFolder = Environment.getExternalStorageDirectory().toString() + AppInfo.DOWNLOAD_PATH_VIDEO;
            FileOper.createDirectory(downloadFolder);
            String path = downloadFolder + usbFile.getName();
            String curFilePath = FileTools.chooseUniqueFilename(path);
            param.to = new File(curFilePath);
            new CopyTask().execute(param);
        }
        //进出全屏
        else  if (clickedViewId == R.id.imb_local_video_full_screen) {    //这里更改屏幕配置
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); //进入全屏配置
        } else if (clickedViewId == R.id.imb_local_video_player_back_full_screen) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  //退出全屏配置
        }

        //退出播放活动
        else if (clickedViewId == R.id.imb_local_video_player_back) {
//            videoView.stopPlayback();
            finish();   //结束播放活动
        }

        //静音与恢复
        else if (clickedViewId == R.id.imb_local_video_volume_mode) {
            if (isVolumeSilence) {   //当前：静音模式
                updateVoice(mLastStreamVolume);
            } else {    //当前：非静音模式
                updateVoice(0);
            }
        }
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

        private ProgressDialog dialog;
        private CopyTaskParam param;

        public CopyTask() {
            dialog = new ProgressDialog(UsbVideoPbActivity.this);
            dialog.setTitle("Download file");
            dialog.setMessage("Download a file to the internal storage, this can take some time!");
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
            long time = System.currentTimeMillis();
            param = params[0];
            try {
                OutputStream out = new BufferedOutputStream(new FileOutputStream(param.to));
                InputStream inputStream = new UsbFileInputStream(param.from);
                byte[] bytes = new byte[getChunkSize()];
                int count;
                long total = 0;

                Log.d(TAG, "Copy file with length: " + param.from.getLength());

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
            Log.d(TAG, "copy time: " + (System.currentTimeMillis() - time));
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            dialog.dismiss();
            String message = getResources().getString(R.string.message_download_to).replace("$1$", AppInfo.DOWNLOAD_PATH_VIDEO);
            MyToast.show(UsbVideoPbActivity.this, message);
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
                final boolean ret = CameraManager.getInstance().getCurCamera().reconnect(UsbVideoPbActivity.this, false);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MyProgressDialog.closeProgressDialog();
                        if(ret){
                            MActivityManager.getInstance().finishAllActivityExceptOne(PreviewActivity.class);
                        }else {
                            MyToast.show(UsbVideoPbActivity.this,R.string.text_operation_failed);
                        }

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
                        String errorInfo = UsbSdCardStatus.getCardStatueInfo(UsbVideoPbActivity.this, errorCode);
                        AppDialog.showDialogWarn(UsbVideoPbActivity.this, errorInfo, new AppDialog.OnDialogSureClickListener() {
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
                        AppDialog.showDialogWarn(UsbVideoPbActivity.this, R.string.dialog_card_removed, new AppDialog.OnDialogSureClickListener() {
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

    @Override
    protected void onResume() {
        super.onResume();
        setEventPollListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        clearEventPollListener();
    }

    public void stopResetTimer() {
        MyCamera camera = CameraManager.getInstance().getCurCamera();
        if (camera != null) {
            camera.stopResetTimer();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(UIhandler != null){
            UIhandler.removeCallbacksAndMessages(null);
        }
    }
}
