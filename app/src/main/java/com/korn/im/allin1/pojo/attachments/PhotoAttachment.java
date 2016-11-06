package com.korn.im.allin1.pojo.attachments;

public interface PhotoAttachment extends Attachment {
    int getId();
    String getText();
    int getWidth();
    int getHeight();
    String getSmallPhoto();
    String getMediumPhoto();
    String getBigPhoto();
    long getDate();
}
