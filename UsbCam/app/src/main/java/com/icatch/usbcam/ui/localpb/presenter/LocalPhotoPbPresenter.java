package com.icatch.usbcam.ui.localpb.presenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.icatch.usbcam.R;
import com.icatch.usbcam.bean.LocalPbItemInfo;
import com.icatch.usbcam.ui.base.BasePresenter;
import com.icatch.usbcam.ui.localpb.LocalFileHelper;
import com.icatch.usbcam.ui.localpb.adapter.LocalPhotoPbViewPagerAdapter;
import com.icatch.usbcam.ui.localpb.contract.LocalPhotoPbView;
import com.icatch.usbcam.utils.MediaRefresh;
import com.icatchtek.basecomponent.prompt.MyProgressDialog;
import com.icatchtek.baseutil.log.AppLog;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @author b.jiang
 */
public class LocalPhotoPbPresenter extends BasePresenter {
    private String TAG = LocalPhotoPbPresenter.class.getSimpleName();
    private LocalPhotoPbView photoPbView;
    private Activity activity;
    private List<LocalPbItemInfo> fileList;
    private LocalPhotoPbViewPagerAdapter viewPagerAdapter;
    private int curPhotoIdx;
    private int lastItem = -1;
    private int tempLastItem = -1;
    private boolean isScrolling = false;
    private int photoNums = 0;
    private int slideDirection = DIRECTION_RIGHT;
    private final static int DIRECTION_RIGHT = 0x1;
    private final static int DIRECTION_LEFT = 0x2;
    private final static int DIRECTION_UNKNOWN = 0x4;
    private ExecutorService executor;
    private Handler handler;

    public LocalPhotoPbPresenter(Activity activity) {
        super(activity);
        this.activity = activity;
        handler = new Handler();
        slideDirection = DIRECTION_UNKNOWN;
    }

    public void setView(LocalPhotoPbView localPhotoPbView) {
        this.photoPbView = localPhotoPbView;
        initCfg();
        fileList = LocalFileHelper.getInstance().getLocalPhotoList();
        Bundle data = activity.getIntent().getExtras();
        curPhotoIdx = data.getInt("curfilePosition");
        AppLog.d(TAG, "photo position =" + curPhotoIdx);
    }

    public void initView() {
        viewPagerAdapter = new LocalPhotoPbViewPagerAdapter(activity, fileList);
        viewPagerAdapter.setOnPhotoTapListener(new LocalPhotoPbViewPagerAdapter.OnPhotoTapListener() {
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

    private void ShowCurPageNum() {
        int curPhoto = photoPbView.getViewPagerCurrentItem() + 1;
        String indexInfo = curPhoto + "/" + fileList.size();
        photoPbView.setIndexInfoTxv(indexInfo);
    }

    public void delete() {
        showDeleteEnsureDialog();
    }

    public void share() {
        int curPosition = photoPbView.getViewPagerCurrentItem();
        String photoPath = fileList.get(curPosition).file.getPath();
        AppLog.d(TAG, "share curPosition=" + curPosition + " photoPath=" + photoPath);
        Uri imageUri = Uri.fromFile(new File(photoPath));
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("image/*");
        activity.startActivity(Intent.createChooser(shareIntent, activity.getResources().getString(R.string.gallery_share_to)));
    }

    private void showDeleteEnsureDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setTitle(R.string.image_delete_des);
        builder.setNegativeButton(R.string.title_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                // 这里添加点击确定后的逻辑
                MyProgressDialog.showProgressDialog(activity, R.string.dialog_deleting);
                executor = Executors.newSingleThreadExecutor();
                executor.submit(new DeleteThread(), null);
            }
        });
        builder.setPositiveButton(R.string.title_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private class DeleteThread implements Runnable {
        @Override
        public void run() {
            curPhotoIdx = photoPbView.getViewPagerCurrentItem();
            File tempFile = fileList.get(curPhotoIdx).file;
            if (tempFile.exists()) {
                tempFile.delete();
                MediaRefresh.notifySystemToScan(tempFile);
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    fileList.remove(curPhotoIdx);
                    viewPagerAdapter.notifyDataSetChanged();
                    photoPbView.setViewPagerAdapter(viewPagerAdapter);
                    photoNums = fileList.size();
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
                    MyProgressDialog.closeProgressDialog();
                }
            });
            AppLog.d(TAG, "end DeleteThread");
        }
    }
}
