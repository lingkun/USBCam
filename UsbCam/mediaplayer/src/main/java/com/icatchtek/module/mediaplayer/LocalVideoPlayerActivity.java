package com.icatchtek.module.mediaplayer;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
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

import java.lang.ref.WeakReference;


public class LocalVideoPlayerActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {
    private final String TAG = LocalVideoPlayerActivity.class.getSimpleName();
    //可以播放的本地视频文件地址组
//    private List<String> mVideoPathList = new ArrayList<>();
//    private int mCurVideoIdx;
    //视频进度控件
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
    private ImageButton imbPlayBack;
    private ImageButton imbPlayForward;
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
    private boolean isFullScreen = false;
    private boolean isVolumeSilence = false;
    private boolean isHideAll = false;
    private int mCurStreamVolume;
    private int mLastStreamVolume;
//    private static final int mForwardTimeOnce = 3000;   //快进、快退按键，点击一次调节的步长（ms）

    // 接收Message,更新主线程UI
    private Handler UIhandler = new MyHandler(this);

    private static class  MyHandler extends Handler {
        WeakReference<LocalVideoPlayerActivity> weakReference;

        public MyHandler(LocalVideoPlayerActivity activity) {
            weakReference = new WeakReference<>(activity);
        }
        @Override
        public void handleMessage(android.os.Message msg) {
            LocalVideoPlayerActivity activity = weakReference.get();
            if(activity == null) {
                return;
            }
            super.handleMessage(msg);
            //更新视频播放进度显示
            if (msg.what == UPDATE_VIDEO_SEEKBAR) {
                // 获取视频当前的播放时间
                int currentPosition = activity.videoView.getCurrentPosition();
                // 获取视频播放的总时间
                int totalDuration = activity.videoView.getDuration();

                // 格式化视频播放时间
                activity.updateTextViewWithTimeFormat(activity.txvRemainTime, (totalDuration - currentPosition));
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
        setContentView(R.layout.activity_local_video_player);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initUI();
        //从上一个活动中获取视频路径并开始播放
        Intent intent = getIntent();
        Uri uri  = intent.getData();
        setVideoPath(uri);
        setVideoSeekBarEvent();     //设置视频监听事件
        setVoiceSeekBarEvent();     //设置音量监听事件
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(UIhandler != null){
            UIhandler.removeCallbacksAndMessages(null);
        }
    }

    private void initUI() {
        //播放进度控件
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
        setSeekBarColor(volumeSeekBar);
        setSeekBarColor(videoSeekBar);
        //音量变量初始化
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mMaxStreamVoice = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mCurStreamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        volumeSeekBar.setMax(mMaxStreamVoice);
        volumeSeekBar.setProgress(mCurStreamVolume);
        updateVoice(mCurStreamVolume);
        //播放时快进、快退
        imbPlayForward = (ImageButton) findViewById(R.id.imb_local_video_play_forward);
        imbPlayBack = (ImageButton) findViewById(R.id.imb_local_video_play_back);
        //全屏播放控件（因布局位置的改变，同时图像源也需要变为背景透明，需要为全屏后的控制控件创建新的实例）
        layoutVideoFullScreen = (RelativeLayout) findViewById(R.id.layout_local_video_full_screen);
        imbPlayPauseFullScreen = (ImageButton) findViewById(R.id.imb_local_video_play_pause_full_screen);
        imbPlayBackFullScreen = (ImageButton) findViewById(R.id.imb_local_video_play_back_full_screen);
        imbPlayForwardFullScreen = (ImageButton) findViewById(R.id.imb_local_video_play_forward_full_screen);
        mVolumeProBar = (ProgressBar) findViewById(R.id.prb_local_video_voice_full_screen);
        mVolumeProBar.setVisibility(View.GONE);
        //全屏前后对应的实例具有相同的点击响应功能
        imbPlayPause.setOnClickListener(this);
        imbPlayPauseFullScreen.setOnClickListener(this);
        imbPlayForward.setOnClickListener(this);
        imbPlayForwardFullScreen.setOnClickListener(this);
        imbPlayBack.setOnClickListener(this);
        imbPlayBackFullScreen.setOnClickListener(this);
        imbFullScreen.setOnClickListener(this);
        imbBack.setOnClickListener(this);
        imbVolumeModeSwitcher.setOnClickListener(this);

        videoView.setOnClickListener(this);

        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener(){
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        //开始卡顿-----需要做一些处理(比如：显示加载动画，或者当前下载速度)
                        mVolumeProBar.setVisibility(View.VISIBLE);
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        //卡顿结束   (隐藏加载动画，或者加载速度)
                        mVolumeProBar.setVisibility(View.GONE);
                        break;
                }
                return false;
            }

        }) ;
    }

    //设置视频路径并初始化播放控件
    private void setVideoPath(Uri uri) {
        videoView.setVideoURI(uri);
        videoView.start();
        UIhandler.sendEmptyMessage(UPDATE_VIDEO_SEEKBAR);  //更新播放时间、进度显示
    }

    //静态设置颜色会导致seekbar宽度设置失效，故需要动态设置其颜色
    private void setSeekBarColor(SeekBar seekBar) {
        //android 4.4 crash
//        seekBar.getProgressDrawable().setColorFilter(Color.parseColor("#076ee4"), PorterDuff.Mode.SRC_ATOP);
//        seekBar.getThumb().setColorFilter(Color.parseColor("#076ee4"), PorterDuff.Mode.SRC_ATOP);
//        Drawable drawable = seekBar.getBackground();
//        if(drawable != null){
//            drawable.setColorFilter(Color.parseColor("#9b9b9b"), PorterDuff.Mode.SRC_ATOP); //设置背景失败
//        }

    }

    // 视频播放完毕：结束当前活动并停止更新播放进度条
    private void setVideoSeekBarEvent() {
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override   //播放完视频，结束当前活动，返回上一级
            public void onCompletion(MediaPlayer mp) {
                finish();
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
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress,0);
            volumeSeekBar.setProgress(progress);
            imbVolumeModeSwitcher.setImageResource(R.drawable.volume);    //设置非静音状态按键图标
            isVolumeSilence = false;
            mCurStreamVolume = progress;
        } else if (progress <= 0){
            mLastStreamVolume = mCurStreamVolume;   //记录当前音量值，以便点击按键恢复音量
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0,0);
            volumeSeekBar.setProgress(0);
            imbVolumeModeSwitcher.setImageResource(R.drawable.full_screen_video_btn_mute);      //设置静音状态图标
            isVolumeSilence = true;
            mCurStreamVolume = 0;
        } else {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mMaxStreamVoice,0);
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
            setSeekBarColor(videoSeekBar);
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
            setSeekBarColor(videoSeekBar);
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
        if (clickedViewId == R.id.imb_local_video_play_pause || clickedViewId == R.id.imb_local_video_play_pause_full_screen){
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
        }else if (clickedViewId == R.id.videoview_local_video_player_view){
            if(layoutTopBar.getVisibility() == View.VISIBLE){
                layoutTopBar.setVisibility(View.GONE);
                layoutVideoSeekBar.setVisibility(View.GONE);
            }else {
                layoutTopBar.setVisibility(View.VISIBLE);
                layoutVideoSeekBar.setVisibility(View.VISIBLE);
            }
        }

        //快进与快退
//        else if (videoView.isPlaying()) {
//            //快进播放
//            if (clickedViewId == R.id.imb_local_video_play_forward || clickedViewId == R.id.imb_local_video_play_forward_full_screen) {   //全屏前后对应控件功能相同
//                int curPosition = videoView.getCurrentPosition();
//                int setPosition = curPosition + mForwardTimeOnce;
//                int maxPosition = videoView.getDuration();
//                if (setPosition <= maxPosition) {
//                    //实际的播放效果是比设定的时间早几秒钟的，网查资料显示：只能跳转到指定时间最近的关键帧开始播放（这是由视频源决定的）
//                    videoView.seekTo(setPosition);
//                    UIhandler.removeMessages(UPDATE_VIDEO_SEEKBAR);
//                    UIhandler.sendEmptyMessage(UPDATE_VIDEO_SEEKBAR);
//                } else {
//                    finish();
//                }
//            }
//            //快退播放
//            else if (clickedViewId == R.id.imb_local_video_play_back || clickedViewId == R.id.imb_local_video_play_back_full_screen) {
//                int curPosition = videoView.getCurrentPosition();
//                int setPosition = curPosition - mForwardTimeOnce;
//                if (setPosition >= 0) {
//                    //实际的播放效果是比设定的时间早几秒钟的，网查资料显示：只能跳转到指定时间最近的关键帧开始播放（这是由视频源决定的）
//                    videoView.seekTo(setPosition);
//                } else {
//                    videoView.seekTo(0);
//                }
//                UIhandler.removeMessages(UPDATE_VIDEO_SEEKBAR);
//                UIhandler.sendEmptyMessage(UPDATE_VIDEO_SEEKBAR);
//            }
//        }

        //切换到上一个/下一个视频并播放
//        if (clickedViewId == R.id.imb_local_video_play_forward || clickedViewId == R.id.imb_local_video_play_forward_full_screen) {   //全屏前后对应控件功能相同
//            mCurVideoIdx = mCurVideoIdx + 1;
//            if (mCurVideoIdx < mVideoPathList.size()) {
//                setVideoPath(mVideoPathList.get(mCurVideoIdx));
//            } else {
//                Toast.makeText(this, "已是最后一个", Toast.LENGTH_SHORT).show();
//                mCurVideoIdx = mVideoPathList.size() - 1;
//            }
//        }
//        //上一个
//        else if (clickedViewId == R.id.imb_local_video_play_back || clickedViewId == R.id.imb_local_video_play_back_full_screen) {
//            mCurVideoIdx = mCurVideoIdx - 1;
//            if (mCurVideoIdx >= 0) {
//                setVideoPath(mVideoPathList.get(mCurVideoIdx));
//            } else {
//                Toast.makeText(this, "已是第一个", Toast.LENGTH_SHORT).show();
//                mCurVideoIdx = 0;
//            }
//        }

        //进出全屏
        if (clickedViewId == R.id.imb_local_video_full_screen) {    //这里更改屏幕配置
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); //进入全屏配置
        } else if (clickedViewId == R.id.imb_local_video_player_back_full_screen) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  //退出全屏配置
        }

        //退出播放活动
        if (clickedViewId == R.id.imb_local_video_player_back ) {
            finish();   //结束播放活动
        }

        //静音与恢复
        if (clickedViewId == R.id.imb_local_video_volume_mode) {
            if (isVolumeSilence) {   //当前：静音模式
                updateVoice(mLastStreamVolume);
            } else {    //当前：非静音模式
                updateVoice(0);
            }
        }

        //全屏模式下，显隐控件
        if (clickedViewId == R.id.videoview_local_video_player_view) {
            if (isFullScreen) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                if (isHideAll) {
                    layoutVideoFullScreen.setVisibility(View.GONE);
                    isHideAll = false;
                } else {
                    layoutVideoFullScreen.setVisibility(View.VISIBLE);
                    isHideAll = true;
                }
            }
        }
    }
}