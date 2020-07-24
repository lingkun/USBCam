package com.icatchtek.baseutil.perference;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.icatchtek.baseutil.FileOper;
import com.icatchtek.baseutil.info.AppInfo;
import com.icatchtek.baseutil.log.AppLog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;

public class BasePreferences {
    private static final String TAG = "CameraPreferences";

    public final static String PUSH_TEST = "pushTest";
    private final static String FILE_NAME = "storeInfo";
    public static final String FILE_PATH = android.os.Environment.getExternalStorageDirectory().toString() + AppInfo.PROPERTY_CFG_DIRECTORY_PATH + "/databases";
    public static final boolean isDebug = true;

    public static void writeDataByName(Context context, String name, String value) {
        SharedPreferences mySharedPreferences  =getSharedPreferences(context,FILE_NAME);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString(name, value);
        //提交当前数据
        editor.commit();
    }

    public static String readStringDataByName(Context context, String name) {
//        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences mySharedPreferences  =getSharedPreferences(context,FILE_NAME);
        // 使用getString方法获得value，注意第2个参数是value的默认值
        String value = mySharedPreferences.getString(name, "");
        return value;
    }

    public static void writeDataByName(Context context, String name, int value) {
//        SharedPreferences mySharedPreferences = context.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences mySharedPreferences  =getSharedPreferences(context,FILE_NAME);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putInt(name, value);
        //提交当前数据
        editor.commit();
    }

    public static void writeDataByName(Context context, String name, boolean value) {
//        SharedPreferences mySharedPreferences = context.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences mySharedPreferences  =getSharedPreferences(context,FILE_NAME);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putBoolean(name, value);
        //提交当前数据
        editor.commit();
    }

    public static int readIntDataByName(Context context, String name) {
//        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences mySharedPreferences  =getSharedPreferences(context,FILE_NAME);
        // 使用getString方法获得value，注意第2个参数是value的默认值
        int value = mySharedPreferences.getInt(name, 0);
        AppLog.d(TAG, "readIntDataByName name=" + name + " value=" + value);
        //使用toast信息提示框显示信息
        return value;
    }

    public static void writeIntDataByName(Context context, String name, int value) {
        AppLog.d(TAG, "writeIntDataByName name=" + name + " value=" + value);
//        SharedPreferences mySharedPreferences = context.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences mySharedPreferences  =getSharedPreferences(context,FILE_NAME);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putInt(name, value);
        //提交当前数据
        editor.commit();
    }

    public static boolean readBoolDataByName(Context context, String name) {
//        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences mySharedPreferences  =getSharedPreferences(context,FILE_NAME);
        // 使用getString方法获得value，注意第2个参数是value的默认值
        boolean value = mySharedPreferences.getBoolean(name, true);
        //使用toast信息提示框显示信息
        return value;
    }

    public static boolean readBoolDataByName(Context context, String name,boolean var) {
//        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences mySharedPreferences  =getSharedPreferences(context,FILE_NAME);
        // 使用getString方法获得value，注意第2个参数是value的默认值
        boolean value = mySharedPreferences.getBoolean(name, var);
        //使用toast信息提示框显示信息
        return value;
    }

    private static SharedPreferences getSharedPreferences(Context context, String fileName) {
        if (isDebug) {
            try {
                // 获取ContextWrapper对象中的mBase变量。该变量保存了ContextImpl对象
                Field field = ContextWrapper.class.getDeclaredField("mBase");
                field.setAccessible(true);
                // 获取mBase变量
                Object obj = field.get(context);
                // 获取ContextImpl。mPreferencesDir变量，该变量保存了数据文件的保存路径
                field = obj.getClass().getDeclaredField("mPreferencesDir");
                field.setAccessible(true);
                // 创建自定义路径
                File file = new File(FILE_PATH);
                FileOper.createDirectory(FILE_PATH);
                // 修改mPreferencesDir变量的值
                field.set(obj, file);
                // 返回修改路径以后的 SharedPreferences :%FILE_PATH%/%fileName%.xml
                return context.getSharedPreferences(fileName, Activity.MODE_PRIVATE);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        // 返回默认路径下的 SharedPreferences : /data/data/%package_name%/shared_prefs/%fileName%.xml
        return context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    public static void writeObject (Context context, String key, Object obj) {
        if (obj instanceof Serializable) {// obj必须实现Serializable接口，否则会出问题
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(obj);
                String string64 = new String(Base64.encode(baos.toByteArray(), 0));
                SharedPreferences.Editor editor = getSharedPreferences(context,FILE_NAME).edit();
                editor.putString(key, string64).commit();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            throw new IllegalArgumentException("the obj must implement Serializble");
        }

    }

    public static Object readObject(Context context, String key) {
        Object obj = null;
        try {
            String base64 = getSharedPreferences(context,FILE_NAME).getString(key, "");
            if (base64.equals("")) {
                return null;
            }
            byte[] base64Bytes = Base64.decode(base64.getBytes(), 1);
            ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            obj = ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static void deleteData(Context context, String key) {
        SharedPreferences mySharedPreferences  =getSharedPreferences(context,FILE_NAME);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.remove(key);
        editor.commit();
        AppLog.d(TAG, "delete: " + key);
    }
}
