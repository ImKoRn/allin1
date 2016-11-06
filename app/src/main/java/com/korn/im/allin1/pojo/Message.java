package com.korn.im.allin1.pojo;

/**
 * Created by korn on 26.08.16.
 */
public interface Message {
    int getId();
    String getContent();
    long getDate();
    boolean isRead();
    int getDialogId();
    boolean isOut();
    boolean isDeleted();
}
