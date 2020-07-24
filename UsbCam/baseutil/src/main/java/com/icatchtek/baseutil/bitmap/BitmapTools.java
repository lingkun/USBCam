package com.icatchtek.baseutil.bitmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.text.TextUtils;
import android.util.Log;


import com.icatchtek.baseutil.info.AppInfo;
import com.icatchtek.baseutil.log.AppLog;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by zhang yanhu C001012 on 2015/11/18 19:54.
 */
public class BitmapTools {
    private static String TAG = BitmapTools.class.getSimpleName();
    public final static int THUMBNAIL_WIDTH = 100;
    public final static int THUMBNAIL_HEIGHT = 100;
    private final static long LIMITED_IMGAE_SIZE = 1024 * 1024 * 10;//byte


    public static Bitmap getBitmapByPath(String path) {
        AppLog.d( TAG, "Start getImageByPath imagePath=" + path );
        if (path == null) {
            return null;
        }
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高，注意此处的bitmap为null
        BitmapFactory.decodeFile( path, options );
        options.inJustDecodeBounds = false; // 设为 false
        options.inSampleSize = calculateInSampleSize( options, options.outWidth, options.outHeight );
        ;
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap = BitmapFactory.decodeFile( path, options );
//        AppLog.d( TAG, "End getImageByPath bitMap=" + bitmap );
//        AppLog.d( TAG, "End getImageByPath bitMap.getByteCount()=" + bitmap.getByteCount() );
        return bitmap;
    }

    /**
     * 根据指定的图像路径和大小来获取缩略图
     * 此方法有两点好处：
     * 1. 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，
     * 第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。
     * 2. 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使
     * 用这个工具生成的图像不会被拉伸。
     *
     * @param imagePath 图像的路径
     * @param width     指定输出图像的宽度
     * @param height    指定输出图像的高度
     * @return 生成的缩略图
     */
    public static Bitmap getImageThumbnailByPath(String imagePath, int width, int height) {
        AppLog.d( TAG, "Start getImageByPath imagePath=" + imagePath );
        if (imagePath == null) {
            return null;
        }
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高，注意此处的bitmap为null
        BitmapFactory.decodeFile( imagePath, options );
        options.inJustDecodeBounds = false; // 设为 false
        options.inSampleSize = calculateInSampleSize( options, width, height );
        ;
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap = BitmapFactory.decodeFile( imagePath, options );
//        AppLog.d( TAG, "End getImageByPath bitMap=" + bitmap );
//        AppLog.d( TAG, "End getImageByPath bitMap.getByteCount()=" + bitmap.getByteCount() );
        return zoomBitmap( bitmap, width, height );
    }

    public static Bitmap getImageThumbnailByPath(String imagePath) {
        AppLog.d( TAG, "Start getImageByPath imagePath=" + imagePath );
        int width = THUMBNAIL_WIDTH;
        int height = THUMBNAIL_HEIGHT;
        if (imagePath == null) {
            return null;
        }
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高，注意此处的bitmap为null
        BitmapFactory.decodeFile( imagePath, options );
        options.inJustDecodeBounds = false; // 设为 false
        options.inSampleSize = calculateInSampleSize( options, width, height );
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap = BitmapFactory.decodeFile( imagePath, options );
        return zoomBitmap( bitmap, width, height );
    }


    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        //图片实际的宽与高，根据默认最大大小值，得到图片实际的缩放比例
        while ((height / inSampleSize > reqHeight) || (width / inSampleSize > reqWidth)) {
            inSampleSize *= 2;
        }
        while (height * width / (inSampleSize * inSampleSize) * 4 > LIMITED_IMGAE_SIZE) {//out of memory.
            inSampleSize *= 2;
        }
        return inSampleSize;
    }

    /**
     * 获取视频的缩略图
     * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
     * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
     *
     * @param videoPath 视频的路径
     * @param width     指定输出视频缩略图的宽度
     * @param height    指定输出视频缩略图的高度度
     * @return 指定大小的视频缩略图
     */
    public static Bitmap getVideoThumbnailByPath(String videoPath, int width, int height) {
        AppLog.i( TAG, "start getVideoThumbnailByPath videoPath=" + videoPath );
        if (videoPath == null) {
            return null;
        }
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail( videoPath, ThumbnailUtils.OPTIONS_RECYCLE_INPUT );
        //bitmap = ThumbnailUtils.extractThumbnail( bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT );
        AppLog.i( TAG, "End getVideoThumbnailByPath bitmap=" + bitmap );
        return zoomBitmap( bitmap, width, height );
    }

    public static Bitmap getVideoThumbnailByPath(String videoPath) {
        AppLog.i( TAG, "start getVideoThumbnailByPath videoPath=" + videoPath );
        int width = THUMBNAIL_WIDTH;
        int height = THUMBNAIL_HEIGHT;
        if (videoPath == null) {
            return null;
        }
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail( videoPath, ThumbnailUtils.OPTIONS_RECYCLE_INPUT );
        //bitmap = ThumbnailUtils.extractThumbnail( bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT );
        AppLog.i( TAG, "End getVideoThumbnailByPath bitmap=" + bitmap );
        return zoomBitmap( bitmap, width, height );
    }


    public static Bitmap zoomBitmap(Bitmap bitmap, float width, float heigth) {
        if (bitmap == null) {
            return bitmap;
        }
        float zoomRate = 1.0f;

        if (bitmap.getWidth() >= width || bitmap.getHeight() >= heigth) {
            if (width * bitmap.getHeight() > heigth * bitmap.getWidth()) {
                zoomRate = heigth / bitmap.getHeight();
            } else if (width * bitmap.getHeight() <= heigth * bitmap.getWidth()) {
                zoomRate = width / bitmap.getWidth();
            }
        }
        Matrix matrix = new Matrix();
        matrix.postScale( zoomRate, zoomRate ); // 长和宽放大缩小的比例
        try {
            bitmap = Bitmap.createBitmap( bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true );
        } catch (OutOfMemoryError e) {
            AppLog.e( TAG, "zoomBitmap OutOfMemoryError" );
            bitmap.recycle();
            System.gc();
        }
        AppLog.d( TAG, "bitmap size is: " + bitmap.getByteCount() );
        return bitmap;
    }


    public static Bitmap getBitmapByWidth(String localImagePath, int width, int addedScaling) {
        if (TextUtils.isEmpty( localImagePath )) {
            return null;
        }

        Bitmap temBitmap = null;

        try {
            BitmapFactory.Options outOptions = new BitmapFactory.Options();

            // 设置该属性为true，不加载图片到内存，只返回图片的宽高到options中。
            outOptions.inJustDecodeBounds = true;

            // 加载获取图片的宽高
            BitmapFactory.decodeFile( localImagePath, outOptions );

            int height = outOptions.outHeight;

            if (outOptions.outWidth > width) {
                // 根据宽设置缩放比例
                outOptions.inSampleSize = outOptions.outWidth / width + 1 + addedScaling;
                outOptions.outWidth = width;

                // 计算缩放后的高度
                height = outOptions.outHeight / outOptions.inSampleSize;
                outOptions.outHeight = height;
            }

            // 重新设置该属性为false，加载图片返回
            outOptions.inJustDecodeBounds = false;
            outOptions.inPurgeable = true;
            outOptions.inInputShareable = true;
            temBitmap = BitmapFactory.decodeFile( localImagePath, outOptions );
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return temBitmap;
    }

    public static Bitmap decodeByteArray(byte[] data) {
        AppLog.d( TAG, "start decodeByteArray" );
        if (data == null) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray( data, 0, data.length, options );
        int sampleSize = calculateInSampleSize( options, options.outWidth, options.outHeight );

        options.inJustDecodeBounds = false;
        //并且制定缩放比例
        options.inSampleSize = sampleSize;
        Bitmap bitmap = BitmapFactory.decodeByteArray( data, 0, data.length, options );
        AppLog.d( TAG, "end decodeByteArray bitmap=" + bitmap );
        return bitmap;
    }

    public static Bitmap decodeByteArray(byte[] data, int reqWidth, int reqHeight) {
        AppLog.d( TAG, "start decodeByteArray" );
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray( data, 0, data.length, options );
        int sampleSize = calculateInSampleSize( options, reqWidth, reqHeight );

        options.inJustDecodeBounds = false;
        //并且制定缩放比例
        options.inSampleSize = sampleSize;
        Bitmap bitmap = BitmapFactory.decodeByteArray( data, 0, data.length, options );
        AppLog.d( TAG, "end decodeByteArray" );
        return zoomBitmap( bitmap, reqWidth, reqHeight );
    }

    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();//初始化一个流对象
        bitmap.compress( Bitmap.CompressFormat.PNG, 100, output );//把bitmap100%高质量压缩 到 output对象里
        byte[] result = output.toByteArray();//转换成功了
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static byte[] compressImage(String filePath, int reqSize) {
        if (filePath == null || TextUtils.isEmpty( filePath )) {
            return null;
        } else {
            File file = new File(filePath);
            if (file == null || !file.exists()) {
                return null;
            }

            if (file.length() / 1024f <= reqSize) {
                return fileToBetyArray(filePath);
            }

            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            //开始读入图片，此时把options.inJustDecodeBounds 设回true了,表示只返回宽高
            int inSampleSize = 1;
            Bitmap bitmap = null;
            newOpts.inJustDecodeBounds = true;
            bitmap = BitmapFactory.decodeFile(filePath, newOpts);
            AppLog.d("compressImage","real size: " + file.length() + ", reqSize: " + reqSize * 1024 + ", Options: inSampleSize: " + newOpts.inSampleSize + ", width: " + newOpts.outWidth + ", height: " + newOpts.outHeight);
            if (file.length() > (reqSize * 2 * 1024)) {
                inSampleSize = calculateInSampleSizeForPV(newOpts, (int)file.length(), reqSize * 1024);
            }

            newOpts.inJustDecodeBounds = false;
            newOpts.inSampleSize = inSampleSize;
            bitmap = BitmapFactory.decodeFile(filePath, newOpts);
            AppLog.d("compressImage","scale, " + "Options: inSampleSize: " + newOpts.inSampleSize + ", width: " + newOpts.outWidth + ", height: " + newOpts.outHeight);
            byte[] bytes = qualityCompress(bitmap, reqSize);
            if (bitmap != null) {
                bitmap.recycle();
                bitmap = null;
            }
            return bytes;
        }

    }

    private static int calculateInSampleSizeForPV(BitmapFactory.Options options, int realSize, int reqSize) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        inSampleSize = (int)Math.floor(Math.sqrt((double)realSize / (double)reqSize));


//        if (height > reqHeight || width > reqWidth) {
//            final int heightRatio = (int)Math.floor((float) height/ (float) reqHeight);
//            final int widthRatio = (int)Math.floor((float) width / (float) reqWidth);
//            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
//        }
        return inSampleSize;

    }

    public static byte[] fileToBetyArray(final String filePath)
    {
        FileInputStream fileInputStream = null;
        File file = new File(filePath);
        byte[] bFile = null;
        try {
            bFile = new byte[(int) file.length()];
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bFile);
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fileInputStream.close();
                bFile.clone();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bFile;
    }


    public static byte[] qualityCompress(Bitmap image, int reqSize) {
        if (image == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        AppLog.d("compressImage","------压缩质量--------" + options);
        AppLog.d("compressImage","------ByteArray--------" + baos.size() / 1024f + "KB" + ", bitmap: " + image.getByteCount());

        while ((baos.size() / 1024f) > reqSize) {  //循环判断如果压缩后图片是否大于等于size,大于等于继续压缩


            if (options > 5){//避免出现options<=0
                options -= 5;
            } else {
                AppLog.d("compressImage","------压缩质量break--------" + options);
                break;
            }
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            AppLog.d("compressImage","------压缩质量--------" + options);
            AppLog.d("compressImage","------ByteArray--------" + baos.size() / 1024f + "KB");
        }

        return baos.toByteArray();
    }

    public static void saveImage(byte[] image, String directoryPath, String fileName) {
        if (image == null || directoryPath == null || fileName == null) {
            return;
        }

        File directory = null;
        if (directoryPath != null) {
            directory = new File(directoryPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }
        }
        File file = new File(directory, fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String writeFile = directoryPath + fileName;
        Log.d("saveImage", "writeFile: " + writeFile);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(writeFile, false);
            out.write(image, 0, image.length);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getImageByPath(String imagePath, int width, int height) {
        AppLog.d(TAG, "Start getImageByPath imagePath=" + imagePath);
        if (imagePath == null) {
            return null;
        }
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高，注意此处的bitmap为null
        BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false; // 设为 false
        options.inSampleSize = calculateInSampleSize(options, width, height);
        ;
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap = BitmapFactory.decodeFile(imagePath, options);
//        AppLog.d( TAG, "End getImageByPath bitMap=" + bitmap );
//        AppLog.d( TAG, "End getImageByPath bitMap.getByteCount()=" + bitmap.getByteCount() );
        return zoomBitmap(bitmap, width, height);
    }

    public static int getImageWidth(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();

        /**
         * 最关键在此，把options.inJustDecodeBounds = true;
         * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
         */
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options); // 此时返回的bitmap为null
        /**
         *options.outHeight为原始图片的高
         */
        return options.outWidth;
    }

    public static int getImageHeight(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();

        /**
         * 最关键在此，把options.inJustDecodeBounds = true;
         * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
         */
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options); // 此时返回的bitmap为null
        /**
         *options.outHeight为原始图片的高
         */
        return options.outHeight;
    }

}
