package com.icatch.usbcam.ui.localpb.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.icatch.usbcam.R;
import com.icatch.usbcam.bean.LocalPbItemInfo;
import com.icatch.usbcam.common.mode.OperationMode;
import com.icatch.usbcam.common.type.FileType;
import com.icatch.usbcam.utils.GlideUtils;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by b.jiang on 2017/5/19.
 */

public class LocalMultiPbWallListAdapter extends BaseAdapter {
    private String TAG = "LocalMultiPbWallListAdapter";
    private Activity context;
    private List<LocalPbItemInfo> list;
    private OperationMode curMode = OperationMode.MODE_BROWSE;
    private FileType fileType;

    public void setScroll(boolean scroll) {
        isScroll = scroll;
    }

    private boolean isScroll = false;

    public LocalMultiPbWallListAdapter(Activity context, List<LocalPbItemInfo> list, FileType fileType) {
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
        String curFileDate = list.get(position).getFileDate();
        View view;
        if (convertView == null) {
            if(fileType == FileType.FILE_VIDEO){
                view = LayoutInflater.from(context).inflate(R.layout.item_local_video_wall_list, null);
            }else {
                view = LayoutInflater.from(context).inflate(R.layout.item_local_photo_wall_list, null);
            }
        } else {
            view = convertView;
        }
        ImageView imageView = (ImageView) view.findViewById(R.id.local_photo_thumbnail_list);
        TextView mTextView = (TextView) view.findViewById(R.id.photo_wall_header);
//        RelativeLayout mLayout = (RelativeLayout) view.findViewById(R.id.local_photo_wall_header_layout);
        TextView imageNameTextView = (TextView) view.findViewById(R.id.local_photo_name);
        TextView imageSizeTextView = (TextView) view.findViewById(R.id.local_photo_size);
        TextView imageDateTextView = (TextView) view.findViewById(R.id.local_photo_date);
        ImageView mCheckImageView = (ImageView) view.findViewById(R.id.local_photo_wall_list_edit);
        ImageView mIsPanoramaSign = (ImageView) view.findViewById(R.id.is_panorama);
        imageNameTextView.setText(list.get(position).getFileName());
        imageSizeTextView.setText(list.get(position).getFileSize());
        imageDateTextView.setText(list.get(position).getFileDateMMSS());
//
        if (list.get(position).isPanorama()) {
            mIsPanoramaSign.setVisibility(View.VISIBLE);
        } else {
            mIsPanoramaSign.setVisibility(View.GONE);
        }
        if (curMode == OperationMode.MODE_EDIT) {
            mCheckImageView.setVisibility(View.VISIBLE);
            if (list.get(position).isItemChecked) {
                mCheckImageView.setImageResource(R.drawable.ic_check_box_blue);
            } else {
                mCheckImageView.setImageResource(R.drawable.ic_check_box_blank_grey);
            }
        } else {
            mCheckImageView.setVisibility(View.GONE);
        }
//        if (position == 0 || !list.get(position - 1).getFileDate().equals(curFileDate)) {
//            mLayout.setVisibility(View.VISIBLE);
//            mTextView.setText(list.get(position).getFileDate());
//        } else {
//            mLayout.setVisibility(View.GONE);
//        }
        File file = list.get(position).file;
        if (file != null) {
            if(fileType == FileType.FILE_PHOTO) {
                GlideUtils.loadImageViewSize(context,file,R.drawable.image_default,100,100,imageView);
            }else {
                GlideUtils.loadImageViewSize(context,file,R.drawable.video_default,100,100,imageView);
            }
        }
        return view;
    }

    public void setOperationMode(OperationMode operationMode) {
        this.curMode = operationMode;
    }

    public void changeSelectionState(int position) {
        list.get(position).isItemChecked = list.get(position).isItemChecked == true ? false : true;
        this.notifyDataSetChanged();
    }

    public List<LocalPbItemInfo> getSelectedList() {
        LinkedList<LocalPbItemInfo> checkedList = new LinkedList<LocalPbItemInfo>();

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

    @Override
    public boolean isEnabled(int position) {
        return isAllItemEnable;
    }

    boolean isAllItemEnable = true;

    public void disableAllItem() {
        isAllItemEnable = false;
        notifyDataSetChanged();
    }
    public void enableAllItem() {
        isAllItemEnable = true;
        notifyDataSetChanged();
    }
}

