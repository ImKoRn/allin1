package com.korn.im.allin1.vk.pojo;

import com.korn.im.allin1.pojo.Interlocutor;
import com.korn.im.allin1.pojo.User;

import org.json.JSONException;
import org.json.JSONObject;

public class VkUser implements User, Interlocutor {
    private static final String ID_FIELDS = "id";
    private static final String NAME_FIELD = "first_name";
    private static final String SURNAME_FIELD = "last_name";
    private static final String PHOTO50_FIELD = "photo_50";
    private static final String PHOTO100_FIELD = "photo_100";
    private static final String PHOTO200_FIELD = "photo_200";
    private static final String PHOTO200_ORIGIN_FIELD = "photo_200_orig";
    private static final String ONLINE_FIELD = "online";
    private static final String ONLINE_MOBILE_FIELD = "online_mobile";

    /*public static final int OFFLINE = -1;
    public static final int ONLINEDESKTOP = 1;
    public static final int MASK = 0xFF;*/

    // VkUser profile fields
    // General fields
    private final int id;
    private final String name;
    private final String surname;

    // Optional fields
    private final boolean online;
    private final boolean onlineMobile;

    // Photos url
    private final String photo50;
    private final String photo100;
    private final String photo200;

    public VkUser(JSONObject from) throws JSONException {
        // General fields
        id = from.getInt(ID_FIELDS);
        name = from.getString(NAME_FIELD);
        surname = from.getString(SURNAME_FIELD);

        // Optional fields
        photo50 = from.getString(PHOTO50_FIELD);
        photo100 = from.getString(PHOTO100_FIELD);
        photo200 = from.optString(PHOTO200_FIELD, from.optString(PHOTO200_ORIGIN_FIELD, ""));

        online = from.optInt(ONLINE_FIELD, 0) == 1;
        onlineMobile = from.optInt(ONLINE_MOBILE_FIELD, 0) == 1;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSurname() {
        return surname;
    }

    @Override
    public String getFullName() {
        return String.format("%s %s", name, surname);
    }

    @Override
    public String getFullSurnameName() {
        return String.format("%s %s", surname, name);
    }

    @Override
    public  boolean isOnline() {
        return online;
    }

    @Override
    public  boolean isOnlineMobile() {
        return onlineMobile;
    }

    @Override
    public String getSmallImage() {
        return photo50;
    }

    @Override
    public String getMediumImage() {
        return photo100;
    }

    @Override
    public String getBigImage() {
        return photo200;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null)
            return false;

        if(o instanceof VkUser) {
            return id == ((VkUser) o).id;
        }

        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return id >> 1 & 2;
    }
}
