package com.korn.im.allin1.pojo;

/**
 * Message representation
 */
public interface Message {
    int getId();
    int getDialogId();

    String getContent();
    String getTitle();

    long getDate();

    boolean isRead();
    boolean isOut();
}
