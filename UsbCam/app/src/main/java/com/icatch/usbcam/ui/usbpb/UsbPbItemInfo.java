package com.icatch.usbcam.ui.usbpb;

import com.github.mjdev.libaums.fs.UsbFile;
import com.icatch.usbcam.utils.ConvertTools;
import com.icatchtek.baseutil.log.AppLog;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @author b.jiang
 * @date 20190715
 */
public class UsbPbItemInfo implements Serializable{
    public UsbFile file;
    public int section;
    public boolean isItemChecked = false;
    private boolean isPanorama = false;

    public UsbPbItemInfo(UsbFile file, int section) {
        this.file = file;
        this.section =section;
        this.isItemChecked = false;
    }

    public UsbPbItemInfo(UsbFile file, int section, boolean isPanorama) {
        this.file = file;
        this.section = section;
        this.isPanorama = isPanorama;
    }

    public UsbPbItemInfo(UsbFile file) {
        super();
        this.file = file;
        this.isItemChecked = false;
    }

    public UsbFile getFile() {
        return file;
    }

    public void setSection(int section){
        this.section = section;
    }


    public String getFileDate(){
        long time = file.lastModified();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(new Date(time));
    }

    public String getFileSize(){
        int size = (int)file.getLength();
        return  ConvertTools.ByteConversionGBMBKB(size);
    }

    public String getFileName(){
        return file.getName();
    }
    public String getFileDateMMSS(){
        long time = file.lastModified();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date(time));
    }

    public long getFileCreateDate(){
        String fileName = file.getName();
        //文件名固定格式为 20190101_110230
        String timeString = fileName.substring(0,15);
//        AppLog.d("getFileCreateDateMMSS"," timeString :" + timeString);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Date date = null;
        try {
            date = format.parse(timeString);
        } catch (ParseException e) {
            AppLog.d("getFileCreateDateMMSS"," ParseException timeString:" + timeString);
            e.printStackTrace();
        }
        long time = 0;
        if(date != null){
            time =  date.getTime();
        }
        return time;
    }

    public boolean isPanorama() {
        return isPanorama;
    }

    public void setPanorama(boolean panorama) {
        isPanorama = panorama;
    }

}
