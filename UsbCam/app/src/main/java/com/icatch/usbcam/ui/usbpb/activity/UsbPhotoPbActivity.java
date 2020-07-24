package com.icatch.usbcam.ui.usbpb.activity;


import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.icatch.usbcam.R;
import com.icatch.usbcam.ui.usbpb.contract.UsbPhotoPbView;
import com.icatch.usbcam.ui.usbpb.presenter.UsbPhotoPbPresenter;
import com.icatch.usbcam.widget.HackyViewPager;
import com.icatch.usbcam.ui.base.BaseActivity;
import com.icatchtek.baseutil.log.AppLog;

public class UsbPhotoPbActivity extends BaseActivity implements UsbPhotoPbView {
    private static final String TAG = UsbPhotoPbActivity.class.getSimpleName();
    private HackyViewPager viewPager;
    private ImageButton downloadBtn;
    private ImageButton deleteBtn;
    private TextView indexInfoTxv;
    private RelativeLayout topBar;
    private LinearLayout bottomBar;
    private UsbPhotoPbPresenter presenter;
    private ImageButton back;

    private SurfaceView mSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_pb);

        viewPager = (HackyViewPager) findViewById(R.id.viewpager);
        indexInfoTxv = (TextView) findViewById(R.id.pb_index_info);
        downloadBtn = (ImageButton) findViewById(R.id.photo_pb_download);
        deleteBtn = (ImageButton) findViewById(R.id.photo_pb_delete);
        topBar = (RelativeLayout) findViewById(R.id.pb_top_layout);
        bottomBar = (LinearLayout) findViewById(R.id.pb_bottom_layout);
        back = (ImageButton) findViewById(R.id.pb_back);
        mSurfaceView = (SurfaceView) findViewById(R.id.m_surfaceView);

        presenter = new UsbPhotoPbPresenter(this);
        presenter.setView(this);
        viewPager.setPageMargin(30);
        viewPager.setOffscreenPageLimit(1);



        viewPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppLog.d(TAG, "viewPager.setOnClickListener");
//                presenter.showBar();
            }
        });


        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.download();
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.delete();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.initView();
        presenter.submitAppInfo();
        presenter.setEventPollListener();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_HOME:
                Log.d("AppStart", "home");
                break;
            case KeyEvent.KEYCODE_BACK:
                Log.d("AppStart", "back");
                finish();
                break;
            default:
                return super.onKeyDown(keyCode, event);
        }
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.removeActivity();
    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged( newConfig );
//        presenter.reloadBitmap();
//    }

    @Override
    public void setViewPagerAdapter(PagerAdapter adapter) {
        if (adapter != null) {
            viewPager.setAdapter(adapter);
        }
    }

    @Override
    public void setTopBarVisibility(int visibility) {
        topBar.setVisibility(visibility);

    }

    @Override
    public void setBottomBarVisibility(int visibility) {
        bottomBar.setVisibility(visibility);
    }

    @Override
    public void setIndexInfoTxv(String photoName) {
        indexInfoTxv.setText(photoName);
    }

    @Override
    public void setViewPagerCurrentItem(int position) {
        viewPager.setCurrentItem(position);
    }

    @Override
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        viewPager.addOnPageChangeListener(listener);
    }

    @Override
    public int getViewPagerCurrentItem() {
        return viewPager.getCurrentItem();
    }

    @Override
    public int getTopBarVisibility() {
        return topBar.getVisibility();
    }

    @Override
    public void setSurfaceviewTransparent(boolean value) {
        if (value) {
            mSurfaceView.setVisibility(View.GONE);
//            mSurfaceView.setZOrderOnTop( true );//设置画布  背景透明
//            mSurfaceView.getHolder().setFormat( PixelFormat.TRANSLUCENT );
        } else {
//            mSurfaceView.getHolder().
            mSurfaceView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setPanoramaTypeTxv(int resId) {
    }

    @Override
    public void setViewPagerVisibility(int visibility) {
        viewPager.setVisibility(visibility);
    }


    @Override
    protected void onPause() {
        super.onPause();
        presenter.clearEventPollListener();
    }
}


