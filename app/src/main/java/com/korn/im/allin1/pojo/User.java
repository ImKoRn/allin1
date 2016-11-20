package com.korn.im.allin1.pojo;

import java.util.Comparator;

/**
 * Represents user
 */
public interface User {
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

    Comparator<User> NAME_CASE_NOT_INSENSITIVE = (first, second) -> first.getName().compareToIgnoreCase(second.getName());
    Comparator<User> SURNAME_CASE_NOT_INSENSITIVE = (first, second) -> first.getSurname().compareToIgnoreCase(second.getSurname());
}
