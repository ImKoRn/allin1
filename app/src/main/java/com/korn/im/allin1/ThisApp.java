package com.korn.im.allin1;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.squareup.leakcanary.LeakCanary;
import com.vk.sdk.VKSdk;

import java.io.File;
import java.io.IOException;

public class ThisApp extends Application {
    private static final String IMAGE_CACHE_DIR = "images";
    private static final String TAG = "Application";

    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
        VKSdk.initialize(this);

        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(this)
                .memoryCache(new UsingFreqLimitedMemoryCache(15 * 1024 * 1024))
                .defaultDisplayImageOptions(new DisplayImageOptions.Builder()
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .resetViewBeforeLoading(true)
                        .displayer(new FadeInBitmapDisplayer(500, true, true, false))
                        .showImageOnFail(R.drawable.camera_b)
                        .build());
        try {
            config.diskCache(new LruDiskCache(getDiskCacheDir(),
                    new Md5FileNameGenerator(), 15 * 1024 * 1024));
        } catch (IOException | IllegalStateException e) {
            Log.w(TAG, e.getMessage());
        }

        ImageLoader.getInstance().init(config.build());
    }

    File getDiskCacheDir() throws IllegalStateException {
        File cacheFile = new File(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                !Environment.isExternalStorageRemovable() ? getExternalCacheDir() :
                getCacheDir(), IMAGE_CACHE_DIR);

        if(!cacheFile.exists() && !cacheFile.mkdirs() ||
                !cacheFile.isDirectory() && !cacheFile.delete() && !cacheFile.mkdirs())
            throw new IllegalStateException("The folder cache can not be created");
        return cacheFile;
    }
}
