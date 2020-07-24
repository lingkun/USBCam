package com.icatch.usbcam.ui.localpb.activity;


import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.icatch.usbcam.R;
import com.icatch.usbcam.ui.localpb.contract.LocalPhotoPbView;
import com.icatch.usbcam.ui.localpb.presenter.LocalPhotoPbPresenter;
import com.icatch.usbcam.utils.GlideUtils;
import com.icatch.usbcam.widget.HackyViewPager;
import com.icatchtek.baseutil.log.AppLog;


public class LocalPhotoPbActivity extends AppCompatActivity implements LocalPhotoPbView {
    private static final String TAG = LocalPhotoPbActivity.class.getSimpleName();
    private HackyViewPager viewPager;
    private TextView indexInfoTxv;
    //    private SurfaceView mSurfaceView;
    private ImageButton shareBtn;
    private ImageButton deleteBtn;
    private RelativeLayout topBar;
    private LinearLayout bottomBar;
    private ImageButton back;
    private LocalPhotoPbPresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppLog.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_photo_pb);

        viewPager = (HackyViewPager) findViewById(R.id.viewpager);
        viewPager.setPageMargin(30);
        indexInfoTxv = (TextView) findViewById(R.id.pb_index_info);
        shareBtn = (ImageButton) findViewById(R.id.local_photo_pb_share);
        deleteBtn = (ImageButton) findViewById(R.id.local_photo_pb_delete);
        topBar = (RelativeLayout) findViewById(R.id.local_pb_top_layout);
        bottomBar = (LinearLayout) findViewById(R.id.local_pb_bottom_layout);
        back = (ImageButton) findViewById(R.id.local_pb_back);

        presenter = new LocalPhotoPbPresenter(this);
        presenter.setView(this);


        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.share();
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
//                presenter.finish();
            }
        });


        viewPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppLog.d(TAG, "viewPager.setOnClickListener");
                // presenter.showBar();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_HOME:
                Log.d("AppStart", "home");
                break;
            case KeyEvent.KEYCODE_BACK:
                Log.d("AppStart", "back");
//                presenter.destroyImage(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE);
                finish();
//                presenter.finish();
                break;
            default:
                return super.onKeyDown(keyCode, event);
        }
        return true;
    }

    @Override
    protected void onResume() {
        AppLog.d(TAG, "onResume");
        super.onResume();
        presenter.initView();
        presenter.submitAppInfo();
    }


    @Override
    protected void onStart() {
        AppLog.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onStop() {
        AppLog.d(TAG, "onStop");
        super.onStop();
//        presenter.isAppBackground();
    }

    @Override
    protected void onDestroy() {
        AppLog.d(TAG, "onDestroy");
        super.onDestroy();
        presenter.removeActivity();
        GlideUtils.clearMemory(this);
        System.gc();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void setViewPagerAdapter(PagerAdapter adapter) {
        if (adapter != null) {
            viewPager.setAdapter(adapter);
        }
    }

    @Override
    public void setIndexInfoTxv(String indexInfo) {
        indexInfoTxv.setText(indexInfo);
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
    public void setTopBarVisibility(int visibility) {
        topBar.setVisibility(visibility);
    }

    @Override
    public void setBottomBarVisibility(int visibility) {
        bottomBar.setVisibility(visibility);
    }


    @Override
    public void setViewPagerVisibility(int visibility) {
        viewPager.setVisibility(visibility);
    }

}

