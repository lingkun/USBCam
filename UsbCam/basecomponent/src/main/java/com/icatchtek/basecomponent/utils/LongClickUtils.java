package com.icatchtek.basecomponent.utils;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by b.jiang on 2018/6/6.
 */

public class LongClickUtils {
    private static final String TAG = "LongClickUtils";

    /**
     * @param handler           外界handler(为了减少handler的泛滥使用,全局最后传handler引用,如果没有就直接传 new Handler())
     * @param longClickView     被长按的视图(任意控件)
     * @param delayMillis       长按时间,毫秒
     * @param longClickListener 长按回调的返回事件
     */
    public static void setLongClick(final Handler handler, final View longClickView, final long delayMillis, final View.OnLongClickListener longClickListener) {
        longClickView.setOnTouchListener(new View.OnTouchListener() {
            private int TOUCH_MAX = 20;
            private int mLastMotionX;
            private int mLastMotionY;
            private ImageView imageViewv;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                this.imageViewv = (ImageView) v;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_CANCEL:
                        Log.d(TAG,"ACTION_CANCEL");
                        // 滑动时移除已有Runnable回调
                        handler.removeCallbacks(r);
                        handler.post(cleanColorFilter);
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d(TAG,"ACTION_UP");
                        // 抬起时,移除已有Runnable回调,抬起就算长按了(不需要考虑用户是否长按了超过预设的时间)
                        handler.removeCallbacks(r);
                        handler.post(cleanColorFilter);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.d(TAG,"ACTION_MOVE  x=" + Math.abs(mLastMotionX - x) + ", y=" +  Math.abs(mLastMotionY - y));
                        if (Math.abs(mLastMotionX - x) > TOUCH_MAX
                                || Math.abs(mLastMotionY - y) > TOUCH_MAX) {
                            // 移动误差阈值
                            // xy方向判断
                            // 移动超过阈值，则表示移动了,就不是长按(看需求),移除 已有的Runnable回调
                            Log.d(TAG,"ACTION_MOVE removeCallbacks");
                            handler.removeCallbacks(r);
                            handler.post(cleanColorFilter);
                        }
                        break;
                    case MotionEvent.ACTION_DOWN:
                        Log.d(TAG,"ACTION_DOWN");
                        // 每次按下重新计时
                        // 按下前,先移除 已有的Runnable回调,防止用户多次单击导致多次回调长按事件的bug
                        handler.removeCallbacks(r);
                        mLastMotionX = x;
                        mLastMotionY = y;
                        handler.post(setColorFilter);
                        // 按下时,开始计时
                        handler.postDelayed(r, delayMillis);
                        break;
                    case MotionEvent.ACTION_OUTSIDE:
                        Log.d(TAG,"ACTION_OUTSIDE");
                        handler.post(cleanColorFilter);

                        break;
                }
                return true;//onclick等其他事件不能用请改这里
            }

            private Runnable r = new Runnable() {
                @Override
                public void run() {
                    if (longClickListener != null) {// 回调给用户,用户可能传null,需要判断null
                        longClickListener.onLongClick(longClickView);
                    }
                }
            };

            private Runnable setColorFilter = new Runnable() {
                @Override
                public void run() {
                    imageViewv.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                    imageViewv.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                }
            };

            private Runnable cleanColorFilter = new Runnable() {
                @Override
                public void run() {
                    imageViewv.clearColorFilter();
                    imageViewv.getBackground().clearColorFilter();
                }
            };
        });
    }
}
