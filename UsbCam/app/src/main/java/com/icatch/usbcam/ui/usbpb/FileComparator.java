package com.icatch.usbcam.ui.usbpb;

import java.util.Comparator;

/**
 * @author b.jiang
 * @date 2019/4/18
 * @description
 */
public class FileComparator implements Comparator<UsbPbItemInfo> {
    private SortRule sortRule;

    public FileComparator(SortRule sortRule){
        this.sortRule = sortRule;
    }

    @Override
    public int compare(UsbPbItemInfo o1, UsbPbItemInfo o2) {
        if(SortRule.CHANGE_TIME_ASCENDING == sortRule){
            if(o1.file.lastModified() < o2.file.lastModified()){
                return -1;
            }else if(o1.file.lastModified() == o2.file.lastModified()){
                return 0;
            }else {
                return 1;
            }
        }else if(SortRule.CHANGE_TIME_DESCENDING == sortRule){
            if(o1.file.lastModified() < o2.file.lastModified()){
                return 1;
            }else if(o1.file.lastModified() == o2.file.lastModified()){
                return 0;
            }else {
                return -1;
            }
        }
        return 0;
    }
}