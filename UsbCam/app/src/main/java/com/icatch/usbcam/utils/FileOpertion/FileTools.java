package com.icatch.usbcam.utils.FileOpertion;

import android.util.Log;

import com.icatch.usbcam.common.type.FileType;
import com.icatchtek.baseutil.log.AppLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zhang yanhu C001012 on 2015/11/19 10:02.
 */
public class FileTools {
    private final static String TAG = "FileTools";
    private static String[] Urls = null;
    private final static String FILENAME_SEQUENCE_SEPARATOR = "-";

    //按照文件大小排序
    public static List<File> getFilesOrderByLength(String fliePath) {
        List<File> files = Arrays.asList(new File(fliePath).listFiles());
        Collections.sort(files, new Comparator<File>() {
            public int compare(File f1, File f2) {
                long diff = f1.length() - f2.length();
                if (diff > 0)
                    return 1;
                else if (diff == 0)
                    return 0;
                else
                    return -1;
            }

            public boolean equals(Object obj) {
                return true;
            }
        });
        for (File f : files) {
            if (f.isDirectory()) {
                continue;
            }
        }
        return files;
    }

    //按照文件名称排序
    public static List<File> getFilesOrderByName(String fliePath) {
        List<File> files = Arrays.asList(new File(fliePath).listFiles());
        Collections.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (o1.isDirectory() && o2.isFile())
                    return -1;
                if (o1.isFile() && o2.isDirectory())
                    return 1;
                return o1.getName().compareTo(o2.getName());
            }
        });
        for (File f : files) {
            AppLog.i(TAG, f.getName());
        }
        return files;
    }

    //按日期排序,降序
    public static List<File> getFilesOrderByDate(String filePath) {
        AppLog.i(TAG, "Start getFilesOrderByDate filePath=" + filePath);
        File file = new File(filePath);
        AppLog.i(TAG, "Start getFilesOrderByDate file=" + file);
        File[] fileArray = file.listFiles();
        AppLog.i(TAG, "Start getFilesOrderByDate fileArray=" + fileArray);
        if (fileArray == null) {
            return null;
        }
        AppLog.i(TAG, "Start getFilesOrderByDate size=" + fileArray.length);
        List<File> files = Arrays.asList(fileArray);
        AppLog.i(TAG, "Start getFilesOrderByDate 2");
        Collections.sort(files, new Comparator<File>() {
            public int compare(File f1, File f2) {
                long diff = f1.lastModified() - f2.lastModified();
                if (diff < 0)
                    return 1;
                else if (diff == 0)
                    return 0;
                else
                    return -1;
            }

            public boolean equals(Object obj) {
                return true;
            }

        });
        AppLog.i(TAG, "files.size() = " + files.size());
        for (int ii = 0; ii < files.size(); ii++) {
            AppLog.i(TAG, "file name = " + files.get(ii).getName());
            AppLog.i(TAG, "modify time = " + new Date(files.get(ii).lastModified()));
        }
        AppLog.i(TAG, "End getFilesOrderByDate");
        return files;
    }

    public static String[] getFileUrls(String path, FileType fileType) {
        AppLog.i(TAG, "Start getUrls path=" + path);
        File folder = new File(path);
        String[] temp = folder.list();
        AppLog.i(TAG, "Start getUrls temp=" + temp);
        AppLog.i(TAG, "Start getUrls temp size=" + temp.length);
        LinkedList<String> tempList = new LinkedList<String>();
        tempList.clear();
        if (fileType == FileType.FILE_PHOTO) {
            for (int ii = 0; ii < temp.length; ii++) {
                if (temp[ii].endsWith(".jpg") || temp[ii].endsWith(".png") || temp[ii].endsWith(".PNG") || temp[ii].endsWith(".JPG")) {
                    tempList.addLast(temp[ii]);
                }
            }
        } else {
            for (int ii = 0; ii < temp.length; ii++) {
                if (temp[ii].endsWith(".MP4") || temp[ii].endsWith(".wmv") || temp[ii].endsWith(".mp4") || temp[ii].endsWith(".3gp")
                        || temp[ii].endsWith(".MOV") || temp[ii].endsWith(".mov") || temp[ii].endsWith(".AVI") || temp[ii].endsWith(".avi")) {
                    tempList.addLast(temp[ii]);
                }
            }
        }

        Urls = new String[tempList.size()];
        for (int ii = 0; ii < tempList.size(); ii++) {

            Urls[ii] = path + tempList.get(ii);
            AppLog.d(TAG, "Urls[" + ii + "]=" + Urls[ii]);
        }
        AppLog.d(TAG, "Urls.length ==" + Urls.length);
        return Urls;
    }

    public static String getFileDate(String fileName) {
        if (fileName == null) {
            return null;
        }

        File file = new File(fileName);
        if (!file.exists()) {
            return null;
        }
        long time = file.lastModified();
        AppLog.d(TAG, "file neme" + fileName);
        AppLog.d(TAG, "file.lastModified()" + time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        return format.format(new Date(time));

    }

    public static long getFileSize(File f) {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSize(flist[i]);
            } else {
                size = size + flist[i].length();
            }
        }
        return size;
    }


    public static String chooseUniqueFilename(String fileNamePath) {
        if (fileNamePath == null) {
            return "error";
        }
        String filename = fileNamePath.substring(0, fileNamePath.lastIndexOf("."));
        String extension = fileNamePath.substring(fileNamePath.lastIndexOf("."), fileNamePath.length());
        String fullFilename = filename + extension;
        if (!new File(fullFilename).exists()) {
            Log.d(TAG, "file not exists=" + fullFilename);
            return fullFilename;
        }
        filename = filename + FILENAME_SEQUENCE_SEPARATOR;
        int sequence = 1;
//        for (int magnitude = 1; magnitude < 1000000000; magnitude *= 10) {
//            for (int iteration = 0; iteration < 9; ++iteration) {
//                fullFilename = filename + sequence + extension;
//                if (!new File(fullFilename).exists()) {
//                    Log.d(TAG, "file fullFilename" + fullFilename);
//                    return fullFilename;
//                }
//                Log.d(TAG, "file with sequence number " + sequence + " exists");
//                sequence += sRandom.nextInt(magnitude) + 1;
//            }
//        }

        for (int iteration = 0; iteration < 10000; ++iteration) {
            fullFilename = filename + sequence + extension;
            if (!new File(fullFilename).exists()) {
                Log.d(TAG, "file fullFilename" + fullFilename);
                return fullFilename;
            }
            Log.d(TAG, "file with sequence number " + sequence + " exists");
            sequence += 1;
        }
        return fullFilename;
    }

    public static boolean saveSerializable(String fileName, Serializable data) {
        FileOutputStream fileOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        File file = new File(fileName);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            fileOutputStream = new FileOutputStream(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    public static Serializable readSerializable(String fileName) {
        Serializable data = null;
        ObjectInputStream ois = null;
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
//            ois = new ObjectInputStream(context.openFileInput(fileName));
            ois = new ObjectInputStream(fileInputStream);
            data = (Serializable) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return data;
    }

}
