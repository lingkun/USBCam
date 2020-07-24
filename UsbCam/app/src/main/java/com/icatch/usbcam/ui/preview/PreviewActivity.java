package com.icatch.usbcam.ui.preview;

import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;

import com.icatch.usbcam.R;
import com.icatch.usbcam.common.mode.PreviewStopPvMode;
import com.icatch.usbcam.common.type.UsbSdCardStatus;
import com.icatch.usbcam.common.usb.USBMonitor;
import com.icatch.usbcam.common.usb.UsbEvent;
import com.icatch.usbcam.ui.base.BaseActivity;
import com.icatch.usbcam.ui.setting.SettingListAdapter;
import com.icatch.usbcam.utils.HomeKeyEvent;
import com.icatchtek.baseutil.AnimationUtil;
import com.icatchtek.baseutil.ClickUtils;
import com.icatchtek.baseutil.log.AppLog;

/**
 * @author b.jiang
 * @date 2018/10/22
 * @description
 */
public class PreviewActivity extends BaseActivity implements IPreviewView {

    private static final String TAG = PreviewActivity.class.getSimpleName();
    private PreviewPresenter presenter;
    private SurfaceView mSurfaceView;
    private ImageView backImv;
    private TextClock systemTimeTxv;
    private TextView pvSizeTxv;
    private LinearLayout recordLayout;
    private LinearLayout volumeLayout;
    private LinearLayout captureLayout;
    private LinearLayout localFileLayout;
    private LinearLayout tfFileLayout;
    private LinearLayout settingLayout;
    private ImageView lockBtn;
    private ImageView volumeImv;
    private TextView exitTxv;
    private RelativeLayout statusBar;
    private LinearLayout actionBar;
    private ImageView recStatusImv;
    private ImageView sdCardStatusImv;
    private RelativeLayout setupMenuLayout;
    private ImageView settingBackImv;
    private ListView settingListview;
    private ImageView photoImv;
    private ImageView tiggerRecImv;

    private TextView tiggerTxv;
    private TextView photoTxv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        presenter = new PreviewPresenter(this);
        presenter.setView(this);
        photoImv = findViewById(R.id.photo_imv);
        tiggerTxv = findViewById(R.id.tigger_txv);
        photoTxv = findViewById(R.id.photo_txv);
        tiggerRecImv = findViewById(R.id.tigger_rec_btn);
        mSurfaceView = findViewById(R.id.preview);
        mSurfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ClickUtils.isFastDoubleClick(v)) {
                    AppLog.i(TAG, "click the preview view id:" + v.getId());
                    presenter.showOrHideBar();
                }
            }
        });
        backImv = findViewById(R.id.back_btn);
        backImv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ClickUtils.isFastDoubleClick(v)) {
                    AppLog.i(TAG, "click the back_btn view id:" + v.getId());
                    presenter.back();
                }
            }
        });
        systemTimeTxv = findViewById(R.id.time_info_txv);
        exitTxv = findViewById(R.id.exit_txv);
        exitTxv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ClickUtils.isFastDoubleClick(v)) {
                    AppLog.i(TAG, "click the back_btn view id:" + v.getId());
                    presenter.back();
                }
            }
        });
        statusBar = findViewById(R.id.status_bar);
        actionBar = findViewById(R.id.action_bar_layout);

        pvSizeTxv = findViewById(R.id.video_size_txv);
        recordLayout = findViewById(R.id.record_layout);
        recordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ClickUtils.isFastDoubleClick(v)) {
                    AppLog.i(TAG, "click record_layout view id:" + v.getId());
                    presenter.triggerRecord();
                }
            }
        });
        volumeLayout = findViewById(R.id.audio_control);
        volumeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ClickUtils.isFastDoubleClick(v)) {
                    AppLog.i(TAG, "click the audio_control view id:" + v.getId());
                    presenter.volumeUpOrOff();
                }
            }
        });
        captureLayout = findViewById(R.id.capture_photo_layout);
        captureLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ClickUtils.isFastDoubleClick(v)) {
                    AppLog.i(TAG, "click the capture_photo_layout view id:" + v.getId());
                    presenter.capture();
                }
            }
        });
        localFileLayout = findViewById(R.id.local_file_layout);
        localFileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ClickUtils.isFastDoubleClick(v)) {
                    AppLog.i(TAG, "click the local_file_layout view id:" + v.getId());
                    presenter.redirectToLocalPb();
                }
            }
        });
        tfFileLayout = findViewById(R.id.tf_file_layout);
        tfFileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ClickUtils.isFastDoubleClick(v)) {
                    AppLog.i(TAG, "click the tf_file_layout view id:" + v.getId());
                    presenter.redirectToRemotePb();
                }
            }
        });
        settingLayout = findViewById(R.id.setting_layout);
        settingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ClickUtils.isFastDoubleClick(v)) {
                    AppLog.i(TAG, "click the setting_layout view id:" + v.getId());
                    presenter.redirectToSetting();
                }
            }
        });
        lockBtn = findViewById(R.id.lock_btn);
        lockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ClickUtils.isFastDoubleClick(v)) {
                    AppLog.i(TAG, "click the lock_btn view id:" + v.getId());
                    presenter.lockOrUnlock();
                }
            }
        });
        volumeImv = findViewById(R.id.volume_imv);

        recStatusImv = findViewById(R.id.record_status_imv);
        sdCardStatusImv = findViewById(R.id.sd_card_status_imv);

        setupMenuLayout = findViewById(R.id.setup_menu_layout);
        settingBackImv = findViewById(R.id.setting_back_btn);
        settingBackImv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ClickUtils.isFastDoubleClick(v)) {
                    AppLog.i(TAG, "click the setting_back_btn view id:" + v.getId());
                    presenter.exitSetting();
                }
            }
        });

        settingListview = findViewById(R.id.setup_menu_listview);
        settingListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!ClickUtils.isFastClick()) {
                    presenter.showSettingDialog(position);
                }
            }
        });

        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                AppLog.d(TAG, "surfaceCreated!!!");
                if (presenter.initSurface(mSurfaceView.getHolder())) {
                    presenter.startPreview(false);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                AppLog.d(TAG, "surfaceChanged!!!");
                presenter.setDrawingArea(width, height);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                AppLog.d(TAG, "surfaceDestroyed!!!");
                presenter.stopPreview(PreviewStopPvMode.STOP_PV_TO_LOCAL);
                presenter.resetSurfaceContext();
                presenter.destroyPreview();

            }
        });
        setActionBtnEnable(false);
        USBMonitor.getInstance().register();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                presenter.checkCamera();
            }
        }, 500);

//        if (Build.VERSION.SDK_INT < 23 || PermissionTools.CheckSelfPermission(this)) {
//            AppLog.enableAppLog();
//            SdkLog.getInstance().enableSDKLog();
//            ConfigureInfo.getInstance().initCfgInfo(getApplicationContext());
//        } else {
//            PermissionTools.RequestPermissions(PreviewActivity.this);
//        }

    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        switch (requestCode) {
//            case PermissionTools.WRITE_OR_READ_EXTERNAL_STORAGE_REQUEST_CODE:
//                AppLog.i(TAG, "permissions.length = " + permissions.length);
//                AppLog.i(TAG, "grantResults.length = " + grantResults.length);
//                boolean retValue = false;
//                for (int i = 0; i < permissions.length; i++) {
//                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
////                        Toast.makeText(this, "Request write storage ", Toast.LENGTH_SHORT).show();
//                        retValue = true;
//                    } else {
//                        retValue = false;
//                    }
//                }
//                if (retValue) {
//                    AppLog.enableAppLog();
//                    SdkLog.getInstance().enableSDKLog();
//                    ConfigureInfo.getInstance().initCfgInfo(this.getApplicationContext());
//                } else {
//                    AppDialog.showDialogQuit(this, R.string.permission_is_denied_info);
//                }
//
//                break;
//            default:
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();
        AppLog.d(TAG, "onResume");
        presenter.initData();
        presenter.initAudioStatus();
        presenter.addEventListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppLog.d(TAG, "onPause");
//        presenter.unregisterUSB();
        presenter.removeEventListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        USBMonitor.getInstance().unregister();
        presenter.destroyCamera();
        presenter.removeHandlerMessage();
    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        presenter.back();
    }

    @Override
    public void setSystemTimeInfo(String timeInfo) {
//        systemTimeTxv.setText(timeInfo);
    }

    @Override
    public void setWifiStatusVisibility(int visibility) {

    }

    @Override
    public void setRecordingTimeVisibility(int visibility) {

    }

    @Override
    public void setRecordingTime(String laspeTime) {

    }

    @Override
    public void setVideoSizeLayoutVisibility(int visibility) {

    }

    @Override
    public void setCaptureBtnEnability(boolean enablity) {

    }

    @Override
    public void setVideoSizeInfo(String sizeInfo) {
        pvSizeTxv.setText(sizeInfo);
    }

    @Override
    public void setActionBarVisibility(int visible) {
        if (visible == View.VISIBLE) {
            AnimationUtil.with().bottomMoveToViewLocation(actionBar, 300);
        } else {
            AnimationUtil.with().moveToViewBottom(actionBar, 300);
        }
//        actionBar.setVisibility(visible);
    }

    @Override
    public void setStatusBarVisibility(int visible) {
        if (visible == View.VISIBLE) {
            AnimationUtil.with().topMoveToViewLocation(statusBar, 300);
        } else {
            AnimationUtil.with().moveToViewTop(statusBar, 300);
        }
    }

    @Override
    public void setSupportPreviewTxvVisibility(int visibility) {

    }

    @Override
    public void setPvModeBtnBackgroundResource(int drawableId) {

    }

    @Override
    public int getSurfaceViewWidth() {
        View parentView = (View) mSurfaceView.getParent();
        int width = parentView.getWidth();
        return width;
    }

    @Override
    public int getSurfaceViewHeight() {
        View parentView = (View) mSurfaceView.getParent();
        int heigth = parentView.getHeight();
        return heigth;
    }

    @Override
    public void setVolumeImvRes(int resId) {
        volumeImv.setImageResource(resId);
    }

    @Override
    public void setLockImvRes(int resId) {
        lockBtn.setImageResource(resId);
    }

    @Override
    public void setLockImvVisibility(int visibility) {
        if (visibility == View.VISIBLE) {
            AnimationUtil.with().leftMoveToViewLocation(lockBtn, 300);
        } else {
            AnimationUtil.with().moveToViewLeft(lockBtn, 300);
        }
    }

    @Override
    public void setPivCaptureBtnEnability(boolean value) {
        captureLayout.setEnabled(value);
        photoImv.setEnabled(value);
        if (value) {
            photoTxv.setTextColor(getResources().getColor(R.color.primary_light));
        } else {
            photoTxv.setTextColor(getResources().getColor(R.color.dark_gray));
        }
    }

    @Override
    public void setRecStatusImvVisibility(int visibility) {
        AppLog.d(TAG, "setRecStatusImvVisibility visibility:" + visibility);
        if (recStatusImv == null) {
            return;
        }
        if (visibility == View.GONE) {
            recStatusImv.clearAnimation();
            recStatusImv.setVisibility(visibility);
        } else {
            recStatusImv.setVisibility(visibility);
            setFlickerAnimation(recStatusImv);
        }
    }

    @Override
    public void setSdStatusImvVisibility(int visibility) {
        AppLog.d(TAG, "setSdStatusImvVisibility visibility:" + visibility);
        sdCardStatusImv.clearAnimation();
        sdCardStatusImv.setVisibility(visibility);
    }

    @Override
    public void setSdStatusImvRes(int sdStatusId) {
        int resId = getSdStatusResId(sdStatusId);
        AppLog.d(TAG, "setSdStatusImvRes resId:" + resId);
        if (resId <= 0) {
            setSdStatusImvVisibility(View.GONE);
        } else {
            sdCardStatusImv.setVisibility(View.VISIBLE);
            sdCardStatusImv.setImageResource(resId);
            setFlickerAnimation(sdCardStatusImv);
        }
    }

    @Override
    public void setSettingMenuVisibility(int visibility) {
        if (visibility == View.VISIBLE) {
            actionBar.setVisibility(View.GONE);
            statusBar.setVisibility(View.GONE);
            setupMenuLayout.setVisibility(View.VISIBLE);
        } else {
            actionBar.setVisibility(View.VISIBLE);
            statusBar.setVisibility(View.VISIBLE);
            setupMenuLayout.setVisibility(View.GONE);
        }

    }

    @Override
    public void setSettingMenuListAdapter(SettingListAdapter settingListAdapter) {
        settingListview.setAdapter(settingListAdapter);
    }

    @Override
    public void setRecordLayoutEnable(boolean enable) {
        recordLayout.setEnabled(enable);
        tiggerRecImv.setEnabled(enable);
        if (enable) {
            tiggerTxv.setTextColor(getResources().getColor(R.color.primary_light));
        } else {
            tiggerTxv.setTextColor(getResources().getColor(R.color.dark_gray));
        }
    }

    @Override
    public int getSettingMenuListVisibility() {
        return setupMenuLayout.getVisibility();
    }

    @Override
    public void setBackBtnEnable(boolean enable) {
        backImv.setEnabled(enable);
        backImv.setClickable(enable);

    }

    @Override
    public void setActionBtnEnable(boolean enable) {
        AppLog.d(TAG, "setActionBtnEnable enable:" + enable);
        actionBar.setEnabled(enable);
        actionBar.setClickable(enable);
        tfFileLayout.setClickable(enable);
        localFileLayout.setClickable(enable);
//        setRecordLayoutEnable(enable);
//        volumeLayout.setEnabled(enable);
//        setCaptureBtnEnability(enable);
//        localFileLayout.setEnabled(enable);
//        settingLayout.setEnabled(enable);
    }

    @Override
    public void setSettingMenuListEnable(boolean enable) {
        setupMenuLayout.setEnabled(enable);
        settingListview.setEnabled(enable);
        settingListview.setItemsCanFocus(enable);
    }

    @Override
    public void setSurfaceViewVisibility(int visibility) {
        AppLog.d(TAG, "setSurfaceViewVisibility visibility:" + visibility);
        mSurfaceView.setVisibility(visibility);
    }

    @Override
    public SurfaceHolder getSurfaceHolder() {
        return mSurfaceView.getHolder();
    }

    private void setFlickerAnimation(ImageView imageView) {
        final Animation animation = new AlphaAnimation(1, 0);
        //闪烁时间间隔
        animation.setDuration(800);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);
        imageView.setAnimation(animation);
    }


    private int getSdStatusResId(int sdStatus) {
        int ret = 0;
        switch (sdStatus) {
            case UsbSdCardStatus.SD_CARD_STATUS_OUT:
                ret = R.drawable.nosd_vertical;
                break;
            case UsbSdCardStatus.SD_CARD_ERR_CARD_ERROR:
                ret = R.drawable.carderror_vertical;
                break;
            case UsbSdCardStatus.SD_CARD_ERR_CARD_LOCKED:
                break;
            case UsbSdCardStatus.SD_CARD_ERR_MEMORY_FULL:
                ret = R.drawable.cardfull_vertical;
                break;
            case UsbSdCardStatus.SD_CARD_ERR_INSUFFICIENT_DISK_SPACE:
            case UsbSdCardStatus.SD_CARD_ERR_DISK_SPEED_TOO_LOW:
            case UsbSdCardStatus.SD_CARD_ERR_MAX:
                ret = R.drawable.needformat_vertical;
                break;
            default:
                break;
        }
        return ret;
    }

    @Override
    public void onEventMain(UsbEvent usbEvent) {
        UsbEvent.EventType eventType = usbEvent.getEventType();
        UsbDevice usbDevice = usbEvent.getDevice();
        AppLog.d(TAG, "onEventMain eventType:" + eventType);
        switch (eventType) {
            case USB_ATTACH:
                presenter.usbAttach();
                break;
            case USB_DETTACH:
                presenter.usbDettach();
                break;
            case USB_CONNECT:
                presenter.usbConnect(usbDevice);
                break;
            case USB_DISCONNECT:
                break;
            case USB_CANCEL:
                presenter.usbCancel();
                break;
            default:
                break;
        }
    }

    @Override
    public void onEventMain(HomeKeyEvent homeKeyEvent) {
        HomeKeyEvent.EventType eventType = homeKeyEvent.getEventType();
        AppLog.d(TAG, "onEventMain eventType:" + eventType);
        switch (eventType) {
            case REASON_RECENT_APPS:
            case REASON_HOME_KEY:
                presenter.homeClick();
                break;
            default:
                break;
        }
    }


}
