package com.icatch.usbcam.ui.usbpb.presenter;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.FileSystem;
import com.github.mjdev.libaums.fs.UsbFile;
import com.github.mjdev.libaums.partition.Partition;
import com.github.mjdev.libaums.server.http.UsbFileHttpServerService;
import com.github.mjdev.libaums.server.http.server.AsyncHttpServer;
import com.icatch.usbcam.R;
import com.icatch.usbcam.app.mycamera.CameraManager;
import com.icatch.usbcam.app.mycamera.MyCamera;
import com.icatch.usbcam.common.listener.OnStatusChangedListener;
import com.icatch.usbcam.common.mode.OperationMode;
import com.icatch.usbcam.common.type.FileType;
import com.icatch.usbcam.common.type.PhotoWallPreviewType;
import com.icatch.usbcam.common.type.UsbSdCardStatus;
import com.icatch.usbcam.data.appinfo.AppInfo;
import com.icatch.usbcam.engine.event.EventPollManager;
import com.icatch.usbcam.ui.base.BaseActivity;
import com.icatch.usbcam.ui.base.BasePresenter;
import com.icatch.usbcam.ui.usbpb.SortRule;
import com.icatch.usbcam.ui.usbpb.UsbFileHelper;
import com.icatch.usbcam.ui.usbpb.UsbPbItemInfo;
import com.icatch.usbcam.ui.usbpb.activity.USBMultiPbFragment;
import com.icatch.usbcam.ui.usbpb.adapter.ViewPagerAdapter;
import com.icatch.usbcam.ui.usbpb.contract.USBMultiPbView;
import com.icatchtek.basecomponent.activitymanager.MActivityManager;
import com.icatchtek.basecomponent.prompt.AppDialog;
import com.icatchtek.basecomponent.prompt.MyProgressDialog;
import com.icatchtek.basecomponent.prompt.MyToast;
import com.icatchtek.baseutil.log.AppLog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


/**
 * @author b.jiang
 */
public class USBMultiPbPresenter extends BasePresenter {
    private static final String TAG = USBMultiPbPresenter.class.getSimpleName();
    private USBMultiPbView multiPbView;
    private BaseActivity activity;
    OperationMode curOperationMode = OperationMode.MODE_BROWSE;
    ViewPagerAdapter adapter;
    private boolean curSelectAll = false;
    private UsbMassStorageDevice curStorageDevice;
    private PhotoWallPreviewType photoWallPreviewType = PhotoWallPreviewType.PREVIEW_TYPE_LIST;
    private Intent serviceIntent = null;
    private UsbFileHttpServerService serverService;
    ServiceConnection serviceConnection;
    private List<USBMultiPbFragment> fragments;
    private Handler handler;
    private boolean usbDeviceInited = false;

    public USBMultiPbPresenter(BaseActivity activity) {
        super(activity);
        this.activity = activity;
        Intent intent = activity.getIntent();
        handler = new Handler();
        AppInfo.currentViewpagerPosition = intent.getIntExtra("CUR_POSITION", 0);
    }

    public void setView(USBMultiPbView multiPbView) {
        this.multiPbView = multiPbView;
        initCfg();
        setUsbDevice();
    }

    public void loadViewPager() {
        initViewpager();
    }

    public void reset() {
        AppInfo.currentViewpagerPosition = 0;
        AppInfo.curVisibleItem = 0;
    }

    private void initViewpager() {
        if(fragments == null){
            fragments = new ArrayList<>();
        }else {
            fragments.clear();
        }
        USBMultiPbFragment multiPbVideoFragment = USBMultiPbFragment.newInstance(FileType.FILE_VIDEO.ordinal());
        multiPbVideoFragment.setOperationListener(onStatusChangedListener);

        USBMultiPbFragment multiPbLockFileFragment = USBMultiPbFragment.newInstance(FileType.FILE_LOCK.ordinal());
        multiPbLockFileFragment.setOperationListener(onStatusChangedListener);

        USBMultiPbFragment multiPbPhotoFragment = USBMultiPbFragment.newInstance(FileType.FILE_PHOTO.ordinal());
        multiPbPhotoFragment.setOperationListener(onStatusChangedListener);

        fragments.add(multiPbVideoFragment);
        fragments.add(multiPbLockFileFragment);
        fragments.add(multiPbPhotoFragment);

        FragmentManager manager = ((FragmentActivity) activity).getSupportFragmentManager();
        adapter = new ViewPagerAdapter(manager);
        ArrayList<String> titles = new ArrayList<>();
        titles.add(activity.getString(R.string.title_video));
        titles.add(activity.getString(R.string.title_lock_file));
        titles.add(activity.getString(R.string.title_photo));
        adapter.setFragments(fragments, titles);
        multiPbView.setViewPageAdapter(adapter);
        multiPbView.setViewPageCurrentItem(AppInfo.currentViewpagerPosition);
    }

    private OnStatusChangedListener onStatusChangedListener = new OnStatusChangedListener() {
        @Override
        public void onChangeOperationMode(OperationMode operationMode) {
            changeOperationMode(operationMode);
        }

        @Override
        public void onSelectedItemsCountChanged(int SelectedNum) {
            String temp = "Selected(" + SelectedNum + ")";
            multiPbView.setSelectNumText(temp);
        }

    };

    private void changeOperationMode(OperationMode operationMode){
        curOperationMode = operationMode;
        if (curOperationMode == OperationMode.MODE_BROWSE) {
            multiPbView.setViewPagerScanScroll(true);
            multiPbView.setTabLayoutClickable(true);
            multiPbView.setSelectBtnVisibility(View.GONE);
            multiPbView.setSelectNumTextVisibility(View.GONE);
            multiPbView.setEditLayoutVisibility(View.GONE);
            multiPbView.setSortBtnVisibility(View.VISIBLE);
            multiPbView.setSelectBtnIcon(R.drawable.ic_select_all_white_24dp);
            curSelectAll = false;
            AppLog.d(TAG, "multiPbVideoFragment quit EditMode");
        } else {
            multiPbView.setViewPagerScanScroll(false);
            multiPbView.setTabLayoutClickable(false);
            multiPbView.setSortBtnVisibility(View.GONE);
            multiPbView.setSelectBtnVisibility(View.VISIBLE);
            multiPbView.setSelectNumTextVisibility(View.VISIBLE);
            multiPbView.setEditLayoutVisibility(View.VISIBLE);
        }
    }

    public void updateViewpagerStatus(int arg0) {
        AppLog.d(TAG, "updateViewpagerStatus arg0=" + arg0);
        AppInfo.currentViewpagerPosition = arg0;
    }

    public void changePreviewType() {
        if (curOperationMode == OperationMode.MODE_BROWSE) {
            clealAsytaskList();
            if (photoWallPreviewType == PhotoWallPreviewType.PREVIEW_TYPE_LIST) {
                multiPbView.setMenuPhotoWallTypeIcon(R.drawable.ic_view_grid_white_24dp);
            } else {
                multiPbView.setMenuPhotoWallTypeIcon(R.drawable.ic_view_list_white_24dp);
            }
            if(fragments != null) {
                for (USBMultiPbFragment fragment : fragments
                ) {
                    fragment.changePreviewType();
                }
            }
        }
    }

    public void changeSortRule(SortRule sortRule){
        if (curOperationMode == OperationMode.MODE_BROWSE) {
            clealAsytaskList();
            if(fragments != null) {
                for (USBMultiPbFragment fragment : fragments
                ) {
                    fragment.changeSortRule(sortRule);
                }
            }
        }
    }

    public void reback() {
        AppLog.d(TAG,"reback curOperationMode :"+ curOperationMode);
        if (curOperationMode == OperationMode.MODE_BROWSE) {
            multiPbView.setViewPagerEnable(false);
            MyProgressDialog.showProgressDialog(activity,R.string.action_processing);
            Observable<Boolean> observable = Observable.create(new ObservableOnSubscribe<Boolean>() {
                @Override
                public void subscribe(ObservableEmitter<Boolean> e) {
                    stopSDStatusPolling();
                    stopHttpServer();
                    stopResetTimer();
                    closeUsbDevice();
                    boolean ret = CameraManager.getInstance().getCurCamera().reconnect(activity,false);
                    e.onNext(ret);
                }
            });
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableObserver<Boolean>() {
                        @Override
                        public void onNext(Boolean value) {
                            MyProgressDialog.closeProgressDialog();
                            if(value){
                                activity.finish();
                            }else {
                                MyToast.show(activity,"连接失败");
                            }
                        }

                        @Override
                        public void onError(Throwable throwable) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else if (curOperationMode == OperationMode.MODE_EDIT) {
            curOperationMode = OperationMode.MODE_BROWSE;
            if(fragments != null && fragments.size() >0) {
                USBMultiPbFragment fragment = fragments.get(multiPbView.getViewPageIndex());
                if(fragment != null){
                    fragment.quitEditMode();
                }
            }
        }
    }



    public void homeClick() {
        AppLog.d(TAG,"homeClick");
        MActivityManager.getInstance().backFirstPage();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    stopSDStatusPolling();
                    stopResetTimer();
                    closeUsbDevice();
                    stopHttpServer();
//                    boolean ret = CameraManager.getInstance().getCurCamera().reconnect(activity,false);
//                    activity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//
////                            activity.finish();
//                        }
//                    });
                }
            }).start();

    }

    public void selectOrCancel() {
        if (curSelectAll) {
            multiPbView.setSelectBtnIcon(R.drawable.ic_select_all_white_24dp);
            curSelectAll = false;
        } else {
            multiPbView.setSelectBtnIcon(R.drawable.ic_unselected_white_24dp);
            curSelectAll = true;
        }
        if(fragments != null && fragments.size() >0) {
            USBMultiPbFragment fragment = fragments.get(multiPbView.getViewPageIndex());
            if(fragment != null){
                fragment.selectOrCancelAll(curSelectAll);
            }
        }
    }

    public void delete() {
        List<UsbPbItemInfo> list = null;
        FileType fileType = FileType.FILE_PHOTO;
        AppLog.d(TAG, "delete multiPbView.getViewPageIndex()=" + multiPbView.getViewPageIndex());
        if(fragments != null && fragments.size() >0) {
            USBMultiPbFragment fragment = fragments.get(multiPbView.getViewPageIndex());
            if(fragment != null){
                list = fragment.getSelectedList();
                fileType = fragment.getFileType();
            }
        }
        if (list == null || list.size() <= 0) {
            AppLog.d(TAG, "asytaskList size=" + list.size());
            MyToast.show(activity, R.string.gallery_no_file_selected);
        } else {
            CharSequence what = activity.getResources().getString(R.string.gallery_delete_des).replace("$1$", String.valueOf(list.size()));
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setCancelable(false);
            builder.setMessage(what);
            builder.setPositiveButton(activity.getResources().getString(R.string.title_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            final List<UsbPbItemInfo> finalList = list;
            final FileType finalFileType = fileType;
            builder.setNegativeButton(activity.getResources().getString(R.string.title_delete), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MyProgressDialog.showProgressDialog(activity, R.string.dialog_deleting);
                    new Thread(new DeleteFileThread(finalList, finalFileType)).start();
                }
            });
            builder.create().show();
        }
    }


    class DeleteFileThread implements Runnable {
        private List<UsbPbItemInfo> fileList;
        private List<UsbPbItemInfo> deleteFailedList;
        private List<UsbPbItemInfo> deleteSucceedList;
        private Handler handler;
        private FileType fileType;

        public DeleteFileThread(List<UsbPbItemInfo> fileList, FileType fileType) {
            this.fileList = fileList;
            this.handler = new Handler();
            this.fileType = fileType;
        }

        @Override
        public void run() {
            AppLog.d(TAG, "DeleteThread");

            if (deleteFailedList == null) {
                deleteFailedList = new LinkedList<UsbPbItemInfo>();
            } else {
                deleteFailedList.clear();
            }
            if (deleteSucceedList == null) {
                deleteSucceedList = new LinkedList<UsbPbItemInfo>();
            } else {
                deleteSucceedList.clear();
            }
            for (UsbPbItemInfo tempFile : fileList) {
                UsbFile file = tempFile.file;
                try {
                    file.delete();
                    deleteSucceedList.add(tempFile);

                } catch (IOException e) {
                    deleteFailedList.add(tempFile);
                    e.printStackTrace();
                }
            }
            clealAsytaskList();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    MyProgressDialog.closeProgressDialog();
                    curOperationMode = OperationMode.MODE_BROWSE;
                    if(fragments != null && fragments.size() >0) {
                        USBMultiPbFragment fragment = fragments.get(multiPbView.getViewPageIndex());
                        if(fragment != null){
                            fragment.quitEditMode();
                            fragment.refreshPhotoWall();
                        }
                    }
                }
            });
        }
    }

    public void clealAsytaskList() {
        if(fragments != null) {
            for (USBMultiPbFragment fragment : fragments
            ) {
                fragment.clealAsytaskList();
            }
        }

    }

    private void setUsbDevice(){
        UsbMassStorageDevice[] storageDevices = UsbMassStorageDevice.getMassStorageDevices(activity);
        AppLog.d(TAG,"getMassStorageDevices storageDevices="+storageDevices);
        if(storageDevices == null || storageDevices.length <= 0){
            AppLog.d(TAG,"getMassStorageDevices is null");
            return;
        }
        try {
            AppLog.d(TAG,"storageDevices size:" +storageDevices.length);
            storageDevices[0].init();
            curStorageDevice = storageDevices[0];
            usbDeviceInited =true;
            List<Partition> partitions =storageDevices[0].getPartitions();
            AppLog.d(TAG,"partitions size:" +partitions.size());
            if(partitions != null && partitions.size() > 0){
                FileSystem currentFs = partitions.get(0).getFileSystem();
                Log.d(TAG, "Capacity: " + currentFs.getCapacity());
                Log.d(TAG, "Occupied Space: " + currentFs.getOccupiedSpace());
                Log.d(TAG, "Free Space: " + currentFs.getFreeSpace());
                Log.d(TAG, "Chunk size: " + currentFs.getChunkSize());
                UsbFileHelper.getInstance().setChunkSize(currentFs.getChunkSize());
                UsbFile root = currentFs.getRootDirectory();
                UsbFile[] usbFiles = root.listFiles();
                AppLog.d(TAG, "Chunk usbFiles: " + usbFiles);
                UsbFileHelper.getInstance().setRootUsbFile(root);
                UsbFileHelper.getInstance().setStorageDevice(curStorageDevice);
            }else {
                MyToast.show(activity,"读取分区失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeUsbDevice(){
        usbDeviceInited = false;
        if(curStorageDevice != null){
            curStorageDevice.close();
            curStorageDevice= null;
        }
    }


    public void startResetTimer(){
        MyCamera camera = CameraManager.getInstance().getCurCamera();
        if(camera != null && curStorageDevice != null){
            camera.startResetTimer(curStorageDevice);
        }
    }

    public void stopResetTimer(){
        MyCamera camera = CameraManager.getInstance().getCurCamera();
        if(camera != null){
            camera.stopResetTimer();
        }
    }

    public void startSDStatusPolling(){
        final MyCamera camera = CameraManager.getInstance().getCurCamera();
        if(camera != null && curStorageDevice != null && camera.getEventPollManager() != null){
            camera.getEventPollManager().startPollingForPb(curStorageDevice);
        }
    }


    public void stopSDStatusPolling(){
        MyCamera camera = CameraManager.getInstance().getCurCamera();
        if(camera != null&& camera.getEventPollManager() != null){
            camera.getEventPollManager().removeListener();
            camera.getEventPollManager().stopPolling();
        }
    }

    public void setEventPollListener(){
        final MyCamera camera = CameraManager.getInstance().getCurCamera();
        if(camera != null&& camera.getEventPollManager() != null){
            camera.getEventPollManager().setSdCardStatusChange(new EventPollManager.OnSdCardStatusChangeListener() {
                @Override
                public void onSdCardError(int errorCode) {
                    AppLog.d(TAG,"onSdCardError errorCode:" + errorCode);
                    if(errorCode == UsbSdCardStatus.SD_CARD_ERR_CARD_ERROR
                            || errorCode == UsbSdCardStatus.SD_CARD_ERR_CARD_LOCKED
                            || errorCode == UsbSdCardStatus.SD_CARD_ERR_INSUFFICIENT_DISK_SPACE
                            || errorCode == UsbSdCardStatus.SD_CARD_ERR_TO_POWER_OFF){
                        camera.getEventPollManager().setSdCardStatusChange(null);
                        final String errorInfo =  UsbSdCardStatus.getCardStatueInfo(activity,errorCode);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                AppDialog.showDialogWarn(activity, errorInfo, new AppDialog.OnDialogSureClickListener() {
                                    @Override
                                    public void onSure() { reback();
                                    }
                                });
                            }
                        },500);
                    }
                }

                @Override
                public void onSdStatus(boolean isExist) {
                    if(!isExist && activity.isAttach() && usbDeviceInited){
                        camera.getEventPollManager().setSdCardStatusChange(null);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                AppDialog.showDialogWarn(activity, R.string.dialog_card_removed, new AppDialog.OnDialogSureClickListener() {
                                    @Override
                                    public void onSure() {
                                        reback();
                                    }
                                });
                            }
                        });

                    }
                    AppLog.d(TAG,"onSdStatus isSDCardExist:" + isExist + " usbDeviceInited:" + usbDeviceInited + " isAttach:" + activity.isAttach());
                }

                @Override
                public void onRecStatusChange(boolean isRec) {

                }

                @Override
                public void onSdInsertError() {

                }
            });
        }
    }

    public void clearEventPollListener(){
        MyCamera camera = CameraManager.getInstance().getCurCamera();
        if(camera != null&& camera.getEventPollManager() != null){
            camera.getEventPollManager().setSdCardStatusChange(null);
        }
    }

    private void startHttpServer(final UsbFile root) {
        AppLog.d(TAG, "starting HTTP server");
        if(serverService == null) {
            Toast.makeText(activity, "serverService == null!", Toast.LENGTH_LONG).show();
            return;
        }
        if(serverService.isServerRunning()) {
            AppLog.d(TAG, "Stopping existing server service");
            serverService.stopServer();
        }
        // now start the server
        try {
            serverService.startServer(root, new AsyncHttpServer(8000));
//            Toast.makeText(USBMultiPbActivity.this, "HTTP server up and running", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            AppLog.e(TAG, "Error starting HTTP server");
            Toast.makeText(activity, "Could not start HTTP server", Toast.LENGTH_LONG).show();
        }
    }

    private void stopHttpServer(){
//        if(serverService!= null && serverService.isServerRunning()){
//            AppLog.d(TAG, "start stopServer");
//            serverService.stopServer();
//            serverService = null;
//            AppLog.d(TAG, "end stopServer");
//        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(serverService!= null && serverService.isServerRunning()){
                    AppLog.d(TAG, "start stopServer");
                    serverService.stopServer();
                    serverService = null;
                    AppLog.d(TAG, "end stopServer");
                }
            }
        }).start();


    }


    public void bindService(){
        AppLog.d(TAG, "bindService");
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                AppLog.d(TAG, "on service connected " + name);
                UsbFileHttpServerService.ServiceBinder binder = (UsbFileHttpServerService.ServiceBinder) service;
                serverService = binder.getService();
                UsbFile usbFile = UsbFileHelper.getInstance().getRootUsbFile();
                startHttpServer(usbFile);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                AppLog.d(TAG, "on service disconnected " + name);
                serverService = null;
            }
        };
        serviceIntent = new Intent(activity, UsbFileHttpServerService.class);
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            activity.getApplicationContext().startForegroundService(serviceIntent);
//        } else {
//            activity.getApplicationContext().startService(serviceIntent);
//        }
        activity.getApplicationContext().startService(serviceIntent);
        activity.getApplicationContext().bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void unbindService(){
        if(serviceConnection != null){
            activity.getApplicationContext().unbindService(serviceConnection);
            serviceConnection = null;
        }

    }


}
