package com.icatch.usbcam.ui.usbpb.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.icatch.usbcam.R;
import com.icatch.usbcam.ui.base.BaseActivity;
import com.icatch.usbcam.ui.usbpb.SortRule;
import com.icatch.usbcam.ui.usbpb.contract.USBMultiPbView;
import com.icatch.usbcam.ui.usbpb.presenter.USBMultiPbPresenter;
import com.icatch.usbcam.utils.FixedSpeedScroller;
import com.icatch.usbcam.utils.GlideUtils;
import com.icatch.usbcam.utils.HomeKeyEvent;
import com.icatchtek.baseutil.ClickUtils;
import com.icatchtek.baseutil.log.AppLog;

import java.lang.reflect.Field;

public class USBMultiPbActivity extends BaseActivity implements USBMultiPbView, PopupMenu.OnMenuItemClickListener {
    private String TAG = "USBMultiPbActivity";
    private ViewPager viewPager;//页卡内容V
    private USBMultiPbPresenter presenter;
    MenuItem menuPhotoWallType;
    ImageButton selectBtn;
    ImageButton deleteBtn;
    ImageButton downloadBtn;
    TextView selectedNumTxv;
    LinearLayout multiPbEditLayout;
    TabLayout tabLayout;
    ImageButton backImgbtn;
    TextView titleTxv;
//    private InnerRecevier receiver;
    private ImageButton sortBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_pb);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sortBtn = findViewById(R.id.sort);
        sortBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });
        viewPager = (ViewPager) findViewById(R.id.vPager);
        selectBtn = (ImageButton) findViewById(R.id.action_select);
        deleteBtn = (ImageButton) findViewById(R.id.action_delete);
        downloadBtn = (ImageButton) findViewById(R.id.action_download);
        selectedNumTxv = (TextView) findViewById(R.id.info_selected_num);
        multiPbEditLayout = (LinearLayout) findViewById(R.id.edit_layout);
        backImgbtn = findViewById(R.id.arrow_back);
        backImgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ClickUtils.isFastDoubleClick()) {
                    presenter.reback();
                }
            }
        });
        titleTxv = findViewById(R.id.title_name);
        titleTxv.setText(R.string.title_file_list);
        //viewPager.setPageMargin((int) getResources().getDimensionPixelOffset(R.dimen.space_10));
        viewPager.setOffscreenPageLimit(3);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        presenter = new USBMultiPbPresenter(this);
        presenter.setView(this);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                presenter.updateViewpagerStatus(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
        });

        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.selectOrCancel();
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.delete();
            }
        });
        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                presenter.share();
            }
        });
        presenter.loadViewPager();
        tabLayout.setupWithViewPager(viewPager);

        //修改viewPager切换滑动速度 JIRA ICOM-3509 20160721
        try {
            Field field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(viewPager.getContext(),
                    new AccelerateInterpolator());
            field.set(viewPager, scroller);
            scroller.setmDuration(280);
        } catch (Exception e) {
            AppLog.e(TAG, "FixedSpeedScroller Exception");
        }
        presenter.bindService();
        presenter.startResetTimer();
        presenter.startSDStatusPolling();
//        //创建广播
//        receiver = new InnerRecevier();
//        //动态注册广播
//        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
//        //启动广播
//        registerReceiver(receiver, intentFilter);
    }

    private void showPopupMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);//第二个参数是绑定的那个view
        //获取菜单填充器
        MenuInflater inflater = popup.getMenuInflater();
        //填充菜单
        inflater.inflate(R.menu.sort_menu, popup.getMenu());
        //绑定菜单项的点击事件
        popup.setOnMenuItemClickListener(this);
        //显示(这一行代码不要忘记了)
        popup.show();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.submitAppInfo();
        presenter.setEventPollListener();
        AppLog.d(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.clearEventPollListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        presenter.isAppBackground();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if(receiver != null){
//            unregisterReceiver(receiver);
//            receiver = null;
//        }
        presenter.unbindService();
        presenter.reset();
        GlideUtils.clearMemory(this);
        System.gc();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_multi_pb, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_multi_pb_preview_type) {
            menuPhotoWallType = item;
            presenter.changePreviewType();
        } else if (id == android.R.id.home) {
            presenter.reback();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_HOME:
                AppLog.d("AppStart", "home");
                break;
            case KeyEvent.KEYCODE_BACK:
                AppLog.d("AppStart", "back");
                if (!ClickUtils.isFastClick()) {
                    presenter.reback();
                }

                break;
            default:
                return super.onKeyDown(keyCode, event);
        }
        return true;
    }

    @Override
    public void setViewPageAdapter(FragmentPagerAdapter adapter) {
        viewPager.setAdapter(adapter);
    }

    @Override
    public void setViewPageCurrentItem(int item) {
        AppLog.d(TAG, "setViewPageCurrentItem item=" + item);
        viewPager.setCurrentItem(item);
    }

    @Override
    public void setMenuPhotoWallTypeIcon(int iconRes) {
        menuPhotoWallType.setIcon(iconRes);
    }

    @Override
    public void setViewPagerScanScroll(boolean isCanScroll) {
//        viewPager.setScanScroll(isCanScroll);
    }

    @Override
    public void setSelectNumText(String text) {
        selectedNumTxv.setText(text);
    }

    @Override
    public void setSelectBtnVisibility(int visibility) {
        selectBtn.setVisibility(visibility);
    }

    @Override
    public void setSelectBtnIcon(int icon) {
        selectBtn.setImageResource(icon);
    }

    @Override
    public void setSelectNumTextVisibility(int visibility) {
        selectedNumTxv.setVisibility(visibility);
    }

    @Override
    public void setTabLayoutClickable(boolean value) {
        AppLog.d(TAG, "setTabLayoutClickable value=" + value);
        tabLayout.setClickable(value);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tabLayout.setContextClickable(value);
        }
        tabLayout.setFocusable(value);
        tabLayout.setLongClickable(value);
        tabLayout.setEnabled(value);
    }

    @Override
    public void setEditLayoutVisibility(int visibility) {
        multiPbEditLayout.setVisibility(visibility);
    }

    @Override
    public int getViewPageIndex() {
        return viewPager.getCurrentItem();
    }

    @Override
    public void setSortBtnVisibility(int visibility) {
        sortBtn.setVisibility(visibility);
    }

    @Override
    public void setViewPagerEnable(boolean enable) {
        viewPager.setEnabled(enable);
        viewPager.setClickable(enable);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_ascending:
                Toast.makeText(this, "按时间升序排列", Toast.LENGTH_SHORT).show();
                presenter.changeSortRule(SortRule.CHANGE_TIME_ASCENDING);
                break;
            case R.id.sort_descending:
                Toast.makeText(this, "按时间降序排列", Toast.LENGTH_SHORT).show();
                presenter.changeSortRule(SortRule.CHANGE_TIME_DESCENDING);
                break;
            default:
                break;
        }
        return false;
    }

//    private class InnerRecevier extends BroadcastReceiver {
//
//        final String SYSTEM_DIALOG_REASON_KEY = "reason";
//
//        final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
//
//        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)) {
//                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
//                if (reason != null) {
//                    if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
////                        Toast.makeText(USBMultiPbActivity.this, "Home键被监听", Toast.LENGTH_SHORT).show();
//                        presenter.homeClick();
//                    } else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
////                        Toast.makeText(USBMultiPbActivity.this, "多任务键被监听", Toast.LENGTH_SHORT).show();
//                        presenter.homeClick();
//                    }
//                }
//            }
//        }
//    }

    @Override
    public void onEventMain(HomeKeyEvent homeKeyEvent) {
        HomeKeyEvent.EventType eventType = homeKeyEvent.getEventType();
        AppLog.d(TAG,"onEventMain eventType:" +eventType);
        switch (eventType){
            case REASON_RECENT_APPS:
            case REASON_HOME_KEY:
                presenter.homeClick();
                break;
            default:
                break;
        }
    }


}
