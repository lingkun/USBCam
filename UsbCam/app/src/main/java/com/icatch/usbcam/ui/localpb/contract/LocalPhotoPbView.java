package com.icatch.usbcam.ui.localpb.contract;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;


public interface LocalPhotoPbView {
    void setViewPagerAdapter(PagerAdapter adapter);
    void setIndexInfoTxv(String indexInfo);
    void setViewPagerCurrentItem(int position);
    void setOnPageChangeListener(ViewPager.OnPageChangeListener listener);
    int getViewPagerCurrentItem();
    int getTopBarVisibility();

    void setTopBarVisibility(int visibility);

    void setBottomBarVisibility(int visibility);


    void setViewPagerVisibility(int visibility);

}

