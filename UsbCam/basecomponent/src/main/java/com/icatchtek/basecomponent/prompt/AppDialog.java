package com.icatchtek.basecomponent.prompt;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.icatchtek.basecomponent.R;
import com.icatchtek.basecomponent.activitymanager.MActivityManager;

import java.lang.ref.WeakReference;


public class AppDialog {
    private final static String TAG = AppDialog.class.getSimpleName();
    private static AlertDialog dialog;

    public static void showDialogWarning(final Activity activity, int messageID) {
        if (!isLiving(activity)) {
            return;
        }
        dismissDialog();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.title_warning).setMessage(messageID);
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.title_ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();
    }


    public static void showDialogWarning(final Activity activity, final int messageID, final OnDialogButtonClickListener listener) {
        if (!isLiving(activity)) {
            return;
        }
        dismissDialog();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(messageID);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.title_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface d, int which) {
                d.dismiss();
                if (listener != null) {
                    listener.onSure();
                }

            }
        });
        builder.setNegativeButton(R.string.title_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface d, int which) {
                d.dismiss();
                if (listener != null) {
                    listener.onCancel();
                }

            }
        });
         dialog = builder.create();
        dialog.show();
    }

    public static void showDialogWarning(final Activity activity,final int positiveId, final int negativeId, final int messageID, final OnDialogButtonClickListener listener) {
        if (!isLiving(activity)) {
            return;
        }
        dismissDialog();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(messageID);
        builder.setCancelable(false);
        builder.setPositiveButton(positiveId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface d, int which) {
                d.dismiss();
                if (listener != null) {
                    listener.onSure();
                }

            }
        });
        builder.setNegativeButton(negativeId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface d, int which) {
                d.dismiss();
                if (listener != null) {
                    listener.onCancel();
                }

            }
        });
        dialog = builder.create();
        dialog.show();
    }

    public interface OnDialogButtonClickListener {
        void onCancel();

        void onSure();
    }

    public interface OnDialogSureClickListener {
        void onSure();
    }


    public static void showDialogWarn(final Activity activity, int messageID, final OnDialogSureClickListener listener) {
        if (!isLiving(activity)) {
            return;
        }
        dismissDialog();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(messageID);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.title_ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (listener != null) {
                    listener.onSure();
                }
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    public static void showDialogWarn(final Activity activity, String messageID, final OnDialogSureClickListener listener) {
        if (!isLiving(activity)) {
            return;
        }
        dismissDialog();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(messageID);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.title_ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (listener != null) {
                    listener.onSure();
                }
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    public static void showDialogQuit(final Activity activity, final int messageID) {
        if (!isLiving(activity)) {
            return;
        }
        dismissDialog();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(messageID);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.title_ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                MActivityManager.getInstance().exitApp();
                System.exit(0);
            }
        });
        if (!isLiving(activity)) {
            return;
        }
        builder.create().show();
    }


    public static void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            try {
                dialog.dismiss();
            } catch (Exception e) {

            }
            dialog = null;
        }
    }

    private static boolean isLiving(Activity activity){
        if (activity == null || activity.isFinishing()|| activity.isDestroyed() ) {
            return false;
        }else {
            return true;
        }
    }
}
