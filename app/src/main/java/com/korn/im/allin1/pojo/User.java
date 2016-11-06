package com.korn.im.allin1.pojo;

import android.os.Parcelable;

import java.util.Comparator;

/**
 * Created by korn on 25.08.16.
 */
public interface User extends Parcelable {
    int getId();

    String getName();
    String getSurname();
    String getFullName();
    String getFullSurnameName();

    boolean isOnline();

    boolean isOnlineMobile();

    String getSmallImage();
    String getMediumImage();
    String getBigImage();

    int getPopIndex();

    Comparator<User> NAME_CASE_NOT_INSENSITIVE = (first, second) -> first.getName().compareToIgnoreCase(second.getName());

    Comparator<User> SURNAME_CASE_NOT_INSENSITIVE = (first, second) -> first.getSurname().compareToIgnoreCase(second.getSurname());

    Comparator<User> POPULARITY = (first, second) -> first.getPopIndex() - second.getPopIndex();
}
