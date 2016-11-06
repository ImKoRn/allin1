package com.korn.im.allin1.pojo;

/**
 * Created by korn on 27.08.16.
 */
public interface Interlocutor {
    int getId();

    String getFullName();

    boolean isOnline();
    boolean isOnlineMobile();

    String getSmallImage();
    String getMediumImage();
    String getBigImage();
}
