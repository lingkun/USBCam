package com.icatch.usbcam.ui.usbpb.adapter;

import android.app.Activity;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.github.mjdev.libaums.fs.UsbFile;
import com.icatch.usbcam.R;
import com.icatch.usbcam.ui.usbpb.UsbPbItemInfo;
import com.icatch.usbcam.utils.GlideUtils;

import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


public class UsbPhotoPbViewPagerAdapter extends PagerAdapter {
    private static final String TAG = "PhotoPbViewPagerAdapter";
    private List<UsbPbItemInfo> filesList;
    private Activity context;
    private OnPhotoTapListener onPhotoTapListener;
    private String baseUrl = "http://localhost:8000/";

    public UsbPhotoPbViewPagerAdapter(Activity context, List<UsbPbItemInfo> filesList) {
        this.filesList = filesList;
        this.context = context;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (position < filesList.size()) {
            container.removeView((View) object);
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View v = View.inflate(context, R.layout.pb_photo_item, null);
        PhotoView photoView = v.findViewById(R.id.photo);
        if (photoView != null) {
//            UsbFileInputStream fileInputStream = new UsbFileInputStream(filesList.get(position).getFile());
//            Glide.with(context).load(fileInputStream).placeholder(R.drawable.pictures_no).into(photoView);
            UsbFile usbFile = filesList.get(position).getFile();
            Uri uri = Uri.parse(baseUrl + usbFile.getParent().getName() + "/" + usbFile.getName());
            GlideUtils.loadImageViewSize(context, uri, R.drawable.image_default, 1280, 720, photoView);
            photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float v, float v1) {
                    if (onPhotoTapListener != null) {
                        onPhotoTapListener.onPhotoTap();
                    }
                }

                @Override
                public void onOutsidePhotoTap() {

                }
            });
        }
        container.addView(v, 0);
        return v;
    }

    @Override
    public int getCount() {
        return filesList.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    public interface OnPhotoTapListener {
        void onPhotoTap();
    }

    public void setOnPhotoTapListener(OnPhotoTapListener onPhotoTapListener) {
        this.onPhotoTapListener = onPhotoTapListener;
    }

}