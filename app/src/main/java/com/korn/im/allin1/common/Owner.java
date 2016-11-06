package com.korn.im.allin1.common;

import android.annotation.SuppressLint;
import android.os.Parcel;

import com.korn.im.allin1.pojo.User;

/**
 * Created by korn on 04.09.16.
 */
@SuppressLint("ParcelCreator")
public class Owner implements User {
    private int id;
    private String name;
    private String surname;
    private String fullName;
    private String fullSurnameName;
    private String smallImage;
    private String mediumImage;
    private String bigImage;

    @Override
    public synchronized int getId() {
        return id;
    }

    @Override
    public synchronized String getName() {
        return name;
    }

    @Override
    public synchronized String getSurname() {
        return surname;
    }

    @Override
    public synchronized String getFullName() {
        return fullName;
    }

    @Override
    public synchronized String getFullSurnameName() {
        return fullSurnameName;
    }

    public void setOnline(boolean online) {
    }

    @Override
    public boolean isOnline() {
        return true;
    }

    @Override
    public synchronized boolean isOnlineMobile() {
        return false;
    }

    public void setOnlineMobile(boolean isOnlineMobile) {
    }

    @Override
    public synchronized String getSmallImage() {
        return smallImage;
    }

    @Override
    public synchronized String getMediumImage() {
        return mediumImage;
    }

    @Override
    public synchronized String getBigImage() {
        return bigImage;
    }

    @Override
    public int getPopIndex() {
        return 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    public synchronized void updateInfo(User user) {
        id = user.getId();
        name = user.getName();
        surname = user.getSurname();
        fullName = user.getFullName();
        fullSurnameName = user.getFullSurnameName();
        smallImage = user.getSmallImage();
        mediumImage = user.getMediumImage();
        bigImage = user.getBigImage();
    }
}
