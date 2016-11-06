package com.korn.im.allin1.common;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

import java.util.List;

/**
 * Created by korn on 02.09.16.
 */
public class ImageChecker {
    public static List<Bitmap> getImage(String imageUri) {
        return MemoryCacheUtils.findCachedBitmapsForImageUri(imageUri, ImageLoader.getInstance().getMemoryCache());
    }
}
