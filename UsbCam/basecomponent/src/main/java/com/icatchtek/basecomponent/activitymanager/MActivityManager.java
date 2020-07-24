package com.icatchtek.basecomponent.activitymanager;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.LinkedList;


public class MActivityManager {
    /**
     * 接收activity的Stack
     */
    private final String TAG = MActivityManager.class.getSimpleName();
    private LinkedList<Activity> activityList = null;
    // TODO: Bug! Fix it.
    private static MActivityManager instance;
    private WeakReference<Activity> curActivity;
    public void setFirstActivity(Activity firstActivity) {
        this.firstActivity = new WeakReference<>(firstActivity);
    }
    private WeakReference<Activity>  firstActivity;

    private MActivityManager() {
    }

    public void setCurActivity(Activity curActivity) {
        this.curActivity = new WeakReference<Activity>(curActivity);
    }

    public Activity getCurActivity() {
        if (activityList == null || activityList.size() <= 0 || curActivity == null) {
            return null;
        }
        return curActivity.get();
    }

    /**
     * 单实例
     *
     * @return
     */
    public static MActivityManager getInstance() {
        if (instance == null) {
            instance = new MActivityManager();
        }
        return instance;
    }


    /**
     * 将activity移出栈
     *
     * @param activity
     */
    public void popActivity(Activity activity) {
        if (activity != null) {
            activityList.remove(activity);
            activity = null;
        }
        Log.d(TAG, "popActivity activityList size=" + activityList.size());
    }

    /**
     * 将activity推入栈内
     *
     * @param activity
     */
    public void pushActivity(Activity activity) {
        if (activityList == null) {
            activityList = new LinkedList<>();
        }
        if (activityList.contains(activity) == false) {
            activityList.addLast(activity);
            Log.d(TAG, "pushActivity activityList size=" + activityList.size());
        }
    }

    /**
     * 结束指定activity
     *
     * @param activity
     */
    private void finishActivity(Activity activity) {
        if (activity != null) {
            activity.finish();
        }
    }


    /**
     * 弹出除cls外的所有activity
     *
     * @param cls
     */
    public void popAllActivityExceptOne(Class<? extends Activity> cls) {
        while (true) {
            Activity activity = getCurActivity();
            if (activity == null) {
                break;
            }
            if (activity.getClass().equals(cls)) {
                break;
            }
            popActivity(activity);
        }
    }


    /**
     * 结束除cls之外的所有activity,执行结果都会清空Stack
     *
     * @param cls
     */
    public void finishAllActivityExceptOne(Class<? extends Activity> cls) {
        if (activityList == null || activityList.size() <= 0) {
            return;
        }
        int size = activityList.size();
        for (int ii = size - 1; ii >= 0; ii--) {
            Activity activity = activityList.get(ii);
            if (!activity.getClass().equals(cls)) {
                finishActivity(activity);
                activityList.remove(activity);
                Log.e(TAG, "finishAllActivityExceptOne: " + activity.getClass().getSimpleName());
            }
        }
    }

    /**
     * 结束所有activity
     */
    public void finishAllActivity() {
        if (activityList == null) {
            return;
        }
        for (Activity activity : activityList) {
            finishActivity(activity);
        }
        activityList.clear();
//        curActivity = null;
    }

    public void backFirstPage() {
        Log.i(TAG, "backFirstPage");
        if(firstActivity != null){
            Activity activity = firstActivity.get();
            if(activity != null){
                finishAllActivityExceptOne(activity.getClass());
            }

        }else {
            exitApp();
        }

    }

    public void exitApp() {
        Log.i(TAG, "exitApp");
        finishAllActivity();
    }
}
