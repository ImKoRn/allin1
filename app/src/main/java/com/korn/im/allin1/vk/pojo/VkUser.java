package com.korn.im.allin1.vk.pojo;

import android.annotation.SuppressLint;

import com.korn.im.allin1.pojo.User;
import com.vk.sdk.api.model.VKApiUser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by korn on 25.08.16.
 */
@SuppressLint("ParcelCreator")
public class VkUser extends VKApiUser implements User, VkInterlocutor {
    public static final int OFFLINE = -1;
    public static final int ONLINE_DESKTOP = 1;
    public static final int MASK = 0xFF;
    private int popularityIndex = 0;
    private String surnameName = "";

    public VkUser() {
    }

    public VkUser(JSONObject from) throws JSONException {
        super(from);
        surnameName = getSurname() + " " + getName();
    }

    @Override
    public VKApiUser parse(JSONObject from) {
        super.parse(from);
        if(photo_200.equals(""))
            photo_200 = photo_200_orig;
        return this;
    }

    @Override
    public String getName() {
        return first_name;
    }

    @Override
    public String getSurname() {
        return last_name;
    }

    @Override
    public String getFullName() {
        return this.toString();
    }

    @Override
    public String getFullSurnameName() {
        return surnameName;
    }

    @Override
    public synchronized void setOnline(boolean online) {
        this.online = online;
    }

    @Override
    public synchronized boolean isOnline() {
        return online;
    }

    @Override
    public synchronized boolean isOnlineMobile() {
        return online_mobile;
    }

    @Override
    public synchronized void setOnlineMobile(boolean isOnlineMobile) {
        online_mobile = isOnlineMobile;
    }

    @Override
    public String getSmallImage() {
        return photo_50;
    }

    @Override
    public String getMediumImage() {
        return photo_100;
    }

    @Override
    public String getBigImage() {
        return photo_200;
    }

    @Override
    public int getPopIndex() {
        return popularityIndex;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null)
            return false;

        if(o instanceof VkUser) {
            VkUser c = (VkUser) o;

            return  id == c.id &&
                    online == c.online &&
                    online_mobile == c.online_mobile &&
                    popularityIndex == c.popularityIndex &&
                    photo_50.equals(c.photo_50) &&
                    photo_100.equals(c.photo_100) &&
                    photo_200.equals(c.photo_200) &&
                    first_name.equals(c.first_name) &&
                    last_name.equals(c.last_name);
        }

        return super.equals(o);
    }

    public void setPopIndex(int index) {
        popularityIndex = index;
    }
}
