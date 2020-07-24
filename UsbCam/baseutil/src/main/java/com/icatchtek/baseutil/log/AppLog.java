package com.icatchtek.baseutil.log;


import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.icatchtek.baseutil.FileOper;
import com.icatchtek.baseutil.info.AppInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * @author b.jiang
 */
public class AppLog {
    private static String TAG = AppLog.class.getSimpleName();
    private static String writeFile;
    private static FileOutputStream out = null;
    private static boolean hasConfiguration = false;
    private static File writeLogFile = null;
    private final static long maxFileSize = 1024 * 1024 * 50;
    private static boolean enableLog = false;
    private static int validityFileNum = 5;
    private static int minSize = 100 *1024 *1024;


    public static void enableAppLog() {
//        if(SystemInfo.getSDFreeSize(MyApplication.getContext()) < minSize){
//            return;
//        }
        enableLog = true;
        initConfiguration();
    }

    private static void initConfiguration() {
        File directory = null;
        String fileName = null;
        String path = null;
        long time = System.currentTimeMillis();
        Date date = new Date(time);
        // System.out.println(date);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH@mm@ss", Locale.CHINA);
        path = Environment.getExternalStorageDirectory().toString() + AppInfo.APP_LOG_DIRECTORY_PATH;
        if (path != null) {
            directory = new File(path);
            if (!directory.exists()) {
                directory.mkdirs();
            }
        }

        FileOper.deleteOverdueFile2(validityFileNum,path);

        fileName = sdf.format(date) + ".log";
        File file = new File(directory, fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        writeFile = path + fileName;
        writeLogFile = new File(writeFile);
        if (out != null) {
            closeWriteStream();
        }
        try {
            out = new FileOutputStream(writeFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        hasConfiguration = true;
        i("initConfiguration", sdf.format(date));
        i("initConfiguration", AppInfo.APP_VERSION);
        i(TAG,"Build.VERSION.SDK_INT:" + Build.VERSION.SDK_INT);
        i(TAG,"Build.VERSION.CODENAME:" + Build.VERSION.CODENAME);
        i(TAG,"Build.VERSION.INCREMENTAL:" + Build.VERSION.INCREMENTAL);
        i(TAG,"Build.VERSION.BOARD:" + Build.BOARD);
        i(TAG,"Build.VERSION.ID:" + Build.ID);
        i(TAG,"Build.VERSION.MODEL:" + Build.MODEL);
        i(TAG,"Build.VERSION.MANUFACTURER:" + Build.MANUFACTURER);
        i(TAG,"Build.VERSION.PRODUCT:" + Build.PRODUCT);
        i(TAG,"Build.VERSION.RELEASE:" + Build.VERSION.RELEASE);
        i(TAG,"Build.VERSION.CODENAME:" + Build.VERSION.CODENAME);
        i(TAG,"Build.VERSION.INCREMENTAL:" + Build.VERSION.INCREMENTAL);
        i(TAG, "cpu type is " + android.os.Build.CPU_ABI);
    }


    private static String getSystemDate() {
        long time = System.currentTimeMillis();
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:sss	");
        return sdf.format(date);
    }


    public static void e(String tag, String conent) {
        if (enableLog == false) {
            return;
        }
        String temp = getSystemDate() + " " + "AppError:" + "[" + tag + "]" + conent + "\n";
        Log.i(TAG, temp);
        write(temp);
    }

    public static void i(String tag, String conent) {
        if (enableLog == false) {
            return;
        }
        String temp = getSystemDate() + " " + "AppInfo:" + "[" + tag + "]" + conent + "\n";
        Log.i(TAG, temp);
        write(temp);
    }

    public static void d(String tag, String conent) {
        if (enableLog == false) {
            return;
        }
        String temp = getSystemDate() + " " + "AppDebug:" + "[" + tag + "]" + conent + "\n";
        Log.i(TAG, temp);
        write(temp);
    }

    private static void write(String content) {
        if (enableLog == false) {
            return;
        }
        if (!hasConfiguration) {
            initConfiguration();
        }
        if (writeLogFile.length() >= maxFileSize) {
            initConfiguration();
        }
        try {
            if (out != null) {
                out.write(content.getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
//        MediaRefresh.notifySystemToScan(writeFile);
    }

    public static void closeWriteStream() {
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}