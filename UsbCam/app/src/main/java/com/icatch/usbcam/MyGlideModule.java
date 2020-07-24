package com.icatch.usbcam;

import android.content.Context;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.AppGlideModule;

/**
 * @author b.jiang
 * @date 2019/3/15
 * @description
 */
@GlideModule
public class MyGlideModule extends AppGlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        int memoryCacheSizeBytes = 1024 * 1024 * 20; // 20mb
//        int bitmapPoolSize = 1024 * 1024 * 20; // 20mb
        builder.setMemoryCache(new LruResourceCache(memoryCacheSizeBytes));
//        builder.setBitmapPool(new LruBitmapPool(bitmapPoolSize));
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}