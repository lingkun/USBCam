package com.icatch.usbcam.ui.localpb;

import android.os.Environment;

import com.icatch.usbcam.bean.LocalPbItemInfo;
import com.icatch.usbcam.data.appinfo.AppInfo;
import com.icatchtek.baseutil.log.AppLog;
import com.icatch.usbcam.utils.FileOpertion.MFileTools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author b.jiang
 * @date 2018/11/1
 * @description
 */
public class LocalFileHelper {

    private String TAG = LocalFileHelper.class.getSimpleName();
    private static LocalFileHelper instance;
    private List<LocalPbItemInfo> localPhotoList;
    public List<LocalPbItemInfo> localVideoList;

    public static synchronized LocalFileHelper getInstance() {
        if (instance == null) {
            instance = new LocalFileHelper();
        }
        return instance;
    }

    public List<LocalPbItemInfo> getLocalPhotoList() {
        return localPhotoList;
    }

    public void setLocalPhotoList(List<LocalPbItemInfo> localPhotoList) {
        this.localPhotoList = localPhotoList;
    }

    public List<LocalPbItemInfo> getLocalVideoList() {
        return localVideoList;
    }

    public void setLocalVideoList(List<LocalPbItemInfo> localVideoList) {
        this.localVideoList = localVideoList;
    }

    public List<LocalPbItemInfo> getVideoFile(){
        final List<LocalPbItemInfo> photoList = new ArrayList<LocalPbItemInfo>();
        List<File> fileList;
        String filePath = Environment.getExternalStorageDirectory().toString() + AppInfo.DOWNLOAD_PATH_VIDEO;
        fileList = MFileTools.getVideosOrderByDate(filePath);
        if (fileList == null || fileList.size() <= 0) {
            return null;
        }

        AppLog.i(TAG, "fileList size=" + fileList.size());
        for (int ii = 0; ii < fileList.size(); ii++) {
            LocalPbItemInfo mGridItem = new LocalPbItemInfo(fileList.get(ii));
            photoList.add(mGridItem);
        }
        localVideoList = photoList;

        return photoList;
    }

    public List<LocalPbItemInfo> getPhotoFile(){
        final List<LocalPbItemInfo> photoList = new ArrayList<LocalPbItemInfo>();
        List<File> fileList;
        String filePath = Environment.getExternalStorageDirectory().toString() + AppInfo.DOWNLOAD_PATH_PHOTO;
        fileList = MFileTools.getPhotosOrderByDate(filePath);
        if (fileList == null || fileList.size() <= 0) {
            return null;
        }

        AppLog.i(TAG, "fileList size=" + fileList.size());
        for (int ii = 0; ii < fileList.size(); ii++) {
            LocalPbItemInfo mGridItem = new LocalPbItemInfo(fileList.get(ii));
            photoList.add(mGridItem);
        }
        localPhotoList = photoList;
        return photoList;
    }

}
