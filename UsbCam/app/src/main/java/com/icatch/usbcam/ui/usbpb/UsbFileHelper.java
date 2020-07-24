package com.icatch.usbcam.ui.usbpb;

import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.UsbFile;
import com.icatchtek.baseutil.log.AppLog;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class UsbFileHelper {
    private String TAG = UsbFileHelper.class.getSimpleName();
    private UsbFile rootUsbFile;
    private static UsbFileHelper instance;
    private List<UsbPbItemInfo> usbPhotoList;
    public List<UsbPbItemInfo> usbVideoList;
    public List<UsbPbItemInfo> usbLockFileList;
    private WeakReference<UsbMassStorageDevice> storageDevice;


    public void setStorageDevice(UsbMassStorageDevice storageDevice) {
        this.storageDevice = new WeakReference<>(storageDevice);
    }

    public UsbMassStorageDevice getStorageDevice() {
        return storageDevice == null ? null:storageDevice.get();
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    private int chunkSize;

    public static  String BASE_URL= "http://localhost:8000/";
    public static synchronized UsbFileHelper getInstance() {
        if (instance == null) {
            instance = new UsbFileHelper();
        }
        return instance;
    }

    public UsbFile getRootUsbFile() {
        return rootUsbFile;
    }

    public List<UsbPbItemInfo> getUsbPhotoList() {
        return usbPhotoList;
    }

    public void setUsbPhotoList(List<UsbPbItemInfo> usbPhotoList) {
        this.usbPhotoList = usbPhotoList;
    }

    public List<UsbPbItemInfo> getUsbVideoList() {
        return usbVideoList;
    }

    public List<UsbPbItemInfo> getUsbLockFileList() {
        return usbLockFileList;
    }

    public void setUsbVideoList(List<UsbPbItemInfo> usbVideoList) {
        this.usbVideoList = usbVideoList;
    }
    public void setRootUsbFile(UsbFile rootUsbFile) {
        AppLog.d(TAG,"setRootUsbFile root=" + rootUsbFile);
        this.rootUsbFile = rootUsbFile;
    }

    public List<UsbPbItemInfo> getVideoFile(SortRule sortRule){
        List<UsbPbItemInfo> usbFiles = null;
        UsbFile[] usbFiles1 = null;
        if(rootUsbFile == null) {
            return null;
        }
        try {
            usbFiles1 = rootUsbFile.listFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(usbFiles1 == null){
            return null;
        }
        UsbFile videoUsbFile = null;
        for (UsbFile temp :usbFiles1 ){
            if(temp.getName().equals("video") || temp.getName().equals("VIDEO")){
                videoUsbFile = temp;
                break;
            }
        }
        if(videoUsbFile == null){
            return null;
        }
        usbFiles = new LinkedList<>();
        try {
            UsbFile[] usbFiles2 = videoUsbFile.listFiles();
            for (UsbFile temp:usbFiles2){
                if(temp.getName().contains(".mp4") || temp.getName().contains(".mov") ||temp.getName().contains(".MP4") ||temp.getName().contains(".MOV") ||temp.getName().contains(".AVI") ||temp.getName().contains(".avi")){
                    usbFiles.add(new UsbPbItemInfo(temp));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Collections.sort(usbFiles,new FileComparator2(sortRule));
        usbVideoList = usbFiles;
        return usbFiles;
    }

    public List<UsbPbItemInfo> getLockFile(SortRule sortRule){
        List<UsbPbItemInfo> usbFiles = null;
        UsbFile[] usbFiles1 = null;
        if(rootUsbFile == null) {
            return null;
        }
        try {
            usbFiles1 = rootUsbFile.listFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(usbFiles1 == null){
            return null;
        }
        UsbFile videoUsbFile = null;
        for (UsbFile temp :usbFiles1 ){
            if(temp.getName().equals("event") || temp.getName().equals("EVENT")){
                videoUsbFile = temp;
                break;
            }
        }
        if(videoUsbFile == null){
            return null;
        }
        usbFiles = new LinkedList<>();
        try {
            UsbFile[] usbFiles2 = videoUsbFile.listFiles();
            for (UsbFile temp:usbFiles2){
                if(temp.getName().contains(".mp4") || temp.getName().contains(".mov") ||temp.getName().contains(".MP4") ||temp.getName().contains(".MOV") ||temp.getName().contains(".AVI") ||temp.getName().contains(".avi")){
                    usbFiles.add(new UsbPbItemInfo(temp));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Collections.sort(usbFiles,new FileComparator2(sortRule));
        usbLockFileList = usbFiles;
        return usbFiles;
    }
    public List<UsbPbItemInfo> getFile(){
        return null;
    }

    public List<UsbPbItemInfo> getPhotoFile(SortRule sortRule){
        List<UsbPbItemInfo> usbFiles = null;
        UsbFile[] usbFiles1 = null;
        if(rootUsbFile == null) {
            return null;
        }
        try {
            usbFiles1 = rootUsbFile.listFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(usbFiles1 == null){
            return null;
        }
        UsbFile videoUsbFile = null;
        for (UsbFile temp :usbFiles1 ){
            if(temp.getName().equals("JPG")){
                videoUsbFile = temp;
                break;
            }
        }
        if(videoUsbFile == null){
            return null;
        }
        usbFiles = new LinkedList<>();
        try {
            UsbFile[] usbFiles2 = videoUsbFile.listFiles();
            for (UsbFile temp:usbFiles2){
                if(temp.getName().contains(".jpg") || temp.getName().contains(".png") ||temp.getName().contains(".PNG") ||temp.getName().contains(".JPG") ){
                    usbFiles.add(new UsbPbItemInfo(temp));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Collections.sort(usbFiles,new FileComparator2(sortRule));
        usbPhotoList = usbFiles;
        return usbFiles;
    }


    public UsbFile getVideoRoot(){
        UsbFile[] usbFiles1 = null;
        if(rootUsbFile == null) {
            return null;
        }
        try {
            usbFiles1 = rootUsbFile.listFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(usbFiles1 == null){
            return null;
        }
        UsbFile videoUsbFile = null;
        for (UsbFile temp :usbFiles1 ){
            if(temp.getName().equals("video") || temp.getName().equals("VIDEO")){
                videoUsbFile = temp;
                break;
            }
        }
        return videoUsbFile;
    }

    public void closeUsbDevice(){
        if(storageDevice != null ){
            UsbMassStorageDevice device = storageDevice.get();
            if(device!= null){
                device.close();
                device = null;
            }
            storageDevice= null;
        }
    }

}
