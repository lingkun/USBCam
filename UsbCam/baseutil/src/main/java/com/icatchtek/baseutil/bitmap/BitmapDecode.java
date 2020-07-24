package com.icatchtek.baseutil.bitmap;
/**
 * Created by b.jiang on 2016/3/25.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;


import com.icatchtek.baseutil.log.AppLog;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;

/**
 * Added by zhangyanhu C01012,2014-7-30
 */
public class BitmapDecode {
    private String TAG = "BitmapDecode";
    SoftReference<Bitmap> softBitmap;
    private Bitmap bitmap;
    Bitmap resizeBmp;

    public Bitmap decodeSampledBitmapFromByteArray(byte[] data, int offset, int length, int reqWidth, int reqHeight) {

        InputStream input = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        input = new ByteArrayInputStream(data);
        bitmap = BitmapFactory.decodeStream(input, null, options);
        if (input != null) {
            try {
                input.close();
            } catch (IOException e) {
                AppLog.e(TAG,"decodeSampledBitmapFromByteArray IOException");
                e.printStackTrace();
            }
        }
//        return zoomBitmap(bitmap, reqWidth, reqHeight);
        return bitmap;
    }

    // 计算放大缩小值
    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    public Bitmap zoomBitmap(Bitmap bitmap, float width, float heigth) {
        float zoomRate = 1.0f;

        if (bitmap.getWidth() >= width || bitmap.getHeight() >= heigth) {
        } else if (width * bitmap.getHeight() > heigth * bitmap.getWidth()) {
            zoomRate = heigth / bitmap.getHeight();
        } else if (width * bitmap.getHeight() <= heigth * bitmap.getWidth()) {
            zoomRate = width / bitmap.getWidth();
        }
        Matrix matrix = new Matrix();
        matrix.postScale(zoomRate, zoomRate); // 长和宽放大缩小的比例
        try {
            resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
            AppLog.e(TAG, "zoomBitmap OutOfMemoryError");
            recycleBitmap();
        }
        this.bitmap.recycle();
        this.bitmap = null;
        return resizeBmp;
    }

    public void recycleBitmap() {
        if (bitmap != null) {
            // 如果没有回收
            bitmap.recycle();
            bitmap = null;
        }
        if (softBitmap != null) {
            // 如果没有回收
            softBitmap.clear();
            softBitmap = null;
        }
        if (resizeBmp != null) {
            // 如果没有回收
            resizeBmp.recycle();
            resizeBmp = null;
        }
        System.gc();
    }

}
