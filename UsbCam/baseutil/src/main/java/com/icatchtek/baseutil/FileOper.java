package com.icatchtek.baseutil;

import android.util.Log;


import com.icatchtek.baseutil.log.AppLog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by yh.zhang C001012 on 2015/10/15:16:25.
 * Fucntion:
 */
public class FileOper {
    private static final String TAG = FileOper.class.getSimpleName();
    public static void createDirectory(String directoryPath){
        Log.d("tigertiger","createDirectory ,directory = "+directoryPath);
        if (directoryPath != null) {
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                boolean ret = directory.mkdirs();
                Log.d("tigertiger","createDirectory: " + directoryPath + ", ret = "+ret);
            }
        }
    }

    public static void createFile(String directoryPath, String fileName) {
        AppLog.i("FileOper","start createFile");
        if (directoryPath != null) {
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }
        }

        File file = new File(directoryPath+fileName);
        AppLog.i("FileOper","directoryPath+fileName ="+directoryPath+fileName);
        if (!file.exists()) {
            AppLog.i("FileOper","file is not exists,need to create!");
            try {
                file.createNewFile();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                AppLog.i("FileOper", "FileNotFoundException");
            } catch (IOException e) {
                AppLog.i("FileOper","IOException");
                e.printStackTrace();
            }

        }
    }

    /**
     * 按文件数量删除过期文件
     *
     * @param validityFileNum 文件个数
     * @param folder 文件目录
     * @return
     */
    public static boolean deleteOverdueFile2(int validityFileNum,String folder) {
        File mfolder = new File(folder);
        if (mfolder.exists() && mfolder.isDirectory()) {
            File[] allFiles = mfolder.listFiles();
            ArrayList<File> mFilesList = new ArrayList<File>();
            if(allFiles == null || allFiles.length <= validityFileNum){
                return true;
            }
            for (int i = 0; i < allFiles.length; i++) {
                File mFile = allFiles[i];
                mFilesList.add(mFile);
            }
            //按升序排列
            Collections.sort(mFilesList, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    if(o1.lastModified() < o2.lastModified()){
                        return -1;
                    }else if(o1.lastModified() == o2.lastModified()){
                        return 0;
                    }else {
                        return 1;
                    }
                }
            });
            //判断日志文件如果大于validityFileNum，保留最新文件
            for (int i = 0; i < mFilesList.size() - validityFileNum; i++) {
                File mFile = mFilesList.get(i);
                Log.d(TAG,"deleteOverdueFile2 name:" + mFile.getName());
                mFile.delete();
            }

        }
        return true;
    }
}
