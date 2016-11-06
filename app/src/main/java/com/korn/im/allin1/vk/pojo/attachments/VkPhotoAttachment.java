package com.korn.im.allin1.vk.pojo.attachments;

import com.korn.im.allin1.pojo.attachments.PhotoAttachment;
import com.vk.sdk.api.model.VKApiPhoto;

public class VkPhotoAttachment extends VKApiPhoto implements PhotoAttachment {
    @Override
    public String getText() {
        return text;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public String getSmallPhoto() {
        return photo_130;
    }

    @Override
    public String getMediumPhoto() {
        return photo_604;
    }

    @Override
    public String getBigPhoto() {
        return photo_1280;
    }

    @Override
    public long getDate() {
        return date;
    }

    @Override
    public String getType() {
        return TYPE_PHOTO;
    }
}
