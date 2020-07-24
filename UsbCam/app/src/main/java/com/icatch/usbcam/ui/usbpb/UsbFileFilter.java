package com.icatch.usbcam.ui.usbpb;

import com.icatch.usbcam.common.type.FileType;

import java.io.File;
import java.io.FileFilter;

/**
 * @author b.jiang
 * @date 2019/4/18
 * @description
 */
public class UsbFileFilter implements FileFilter {
    FileType fileType;
    UsbFileFilter(FileType fileType){
        this.fileType = fileType;
    }
    @Override
    public boolean accept(File pathname) {
        String tmp = pathname.getName().toLowerCase();
        if (tmp.endsWith(".mov") || tmp.endsWith(".jpg")) {
            return true;
        }
        return false;
    }
}
