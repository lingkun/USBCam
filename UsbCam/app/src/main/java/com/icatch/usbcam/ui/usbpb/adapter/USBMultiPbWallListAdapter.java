package com.icatch.usbcam.ui.usbpb.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.mjdev.libaums.fs.UsbFile;
import com.github.mjdev.libaums.fs.UsbFileInputStream;
import com.icatch.usbcam.R;
import com.icatch.usbcam.common.mode.OperationMode;
import com.icatch.usbcam.common.type.FileType;
import com.icatch.usbcam.ui.usbpb.UsbPbItemInfo;
import com.icatch.usbcam.utils.GlideUtils;
import com.icatchtek.baseutil.log.AppLog;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by b.jiang on 2017/5/19.
 */

public class USBMultiPbWallListAdapter extends BaseAdapter {
    private String TAG = "USBMultiPbWallListAdapter";
    private Activity context;
    private List<UsbPbItemInfo> list;
    private OperationMode curMode = OperationMode.MODE_BROWSE;
    private FileType fileType;
    private String baseUrl= "http://localhost:8000/";
    private boolean isScroll = false;
    public void setScroll(boolean scroll) {
        isScroll = scroll;
    }

    public USBMultiPbWallListAdapter(Activity context, List<UsbPbItemInfo> list, FileType fileType) {
        this.context = context;
        this.list = list;
        this.fileType = fileType;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        AppLog.d(TAG,"getview position=" + position + " convertView=" + convertView);
        ViewHolder holder = null;
        if (convertView == null) {
            if(fileType == FileType.FILE_VIDEO || fileType == FileType.FILE_LOCK){
                convertView = LayoutInflater.from(context).inflate(R.layout.item_local_video_wall_list, null);
            }else {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_local_photo_wall_list, null);
            }

            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.local_photo_thumbnail_list);
//            holder.mTextView = (TextView) convertView.findViewById(R.id.photo_wall_header);
//            holder.mLayout = (RelativeLayout) convertView.findViewById(R.id.local_photo_wall_header_layout);
            holder.imageNameTextView = (TextView) convertView.findViewById(R.id.local_photo_name);
            holder.imageSizeTextView = (TextView) convertView.findViewById(R.id.local_photo_size);
            holder.imageDateTextView = (TextView) convertView.findViewById(R.id.local_photo_date);
            holder.mCheckImageView = (ImageView) convertView.findViewById(R.id.local_photo_wall_list_edit);
            holder.mIsPanoramaSign = (ImageView) convertView.findViewById(R.id.is_panorama);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.imageNameTextView.setText(list.get(position).getFileName());
        holder.imageSizeTextView.setText(list.get(position).getFileSize());
        holder.imageDateTextView.setText(list.get(position).getFileDateMMSS());
//        if (list.get(position).isPanorama()) {
//            holder.mIsPanoramaSign.setVisibility(View.VISIBLE);
//        } else {
//            holder.mIsPanoramaSign.setVisibility(View.GONE);
//        }
        if (curMode == OperationMode.MODE_EDIT) {
            holder.mCheckImageView.setVisibility(View.VISIBLE);
            if (list.get(position).isItemChecked) {
                holder.mCheckImageView.setImageResource(R.drawable.ic_check_box_blue);
            } else {
                holder.mCheckImageView.setImageResource(R.drawable.ic_check_box_blank_grey);
            }
        } else {
            holder.mCheckImageView.setVisibility(View.GONE);
        }
        String name = list.get(position).getFileName();
        ImageView imageView = holder.imageView;
        UsbFile usbFile = list.get(position).getFile();
        Uri uri = Uri.parse(baseUrl  + usbFile.getParent().getName()+ "/"+ usbFile.getName());
        if (fileType == FileType.FILE_PHOTO) {
            GlideUtils.loadImageViewSize(context, uri, R.drawable.image_default, 100, 100, imageView);
        }
        return convertView;
    }

    public void setOperationMode(OperationMode operationMode) {
        this.curMode = operationMode;
    }

    public void changeSelectionState(int position) {
        list.get(position).isItemChecked = list.get(position).isItemChecked == true ? false : true;
        this.notifyDataSetChanged();
    }

    public List<UsbPbItemInfo> getSelectedList() {
        LinkedList<UsbPbItemInfo> checkedList = new LinkedList<UsbPbItemInfo>();

        for (int ii = 0; ii < list.size(); ii++) {
            if (list.get(ii).isItemChecked) {
                checkedList.add(list.get(ii));
            }
        }
        return checkedList;
    }

    public void quitEditMode() {
        this.curMode = OperationMode.MODE_BROWSE;
        for (int ii = 0; ii < list.size(); ii++) {
            list.get(ii).isItemChecked = false;
        }
        this.notifyDataSetChanged();
    }

    public void selectAllItems() {
        for (int ii = 0; ii < list.size(); ii++) {
            list.get(ii).isItemChecked = true;
        }
        this.notifyDataSetChanged();
    }

    public void cancelAllSelections() {
        for (int ii = 0; ii < list.size(); ii++) {
            list.get(ii).isItemChecked = false;
        }
        this.notifyDataSetChanged();
    }

    public int getSelectedCount() {
        int checkedNum = 0;
        for (int ii = 0; ii < list.size(); ii++) {
            if (list.get(ii).isItemChecked) {
                checkedNum++;
            }
        }
        return checkedNum;
    }

    public static class ViewHolder {
        public ImageView imageView;
        public TextView imageNameTextView;
        public TextView imageSizeTextView;
        public TextView imageDateTextView;
        public ImageView mCheckImageView;
        public ImageView mIsPanoramaSign;
    }
}

