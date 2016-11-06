package com.korn.im.allin1.pojo.attachments;

public interface Attachment {
    String TYPE_PHOTO = "photo";
    String TYPE_VIDEO = "video";
    String TYPE_MUSIC = "music";
    String TYPE_AUDIO = "audio";

    String getType();
}
