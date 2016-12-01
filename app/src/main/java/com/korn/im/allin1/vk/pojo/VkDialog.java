package com.korn.im.allin1.vk.pojo;


import com.korn.im.allin1.pojo.Dialog;
import com.korn.im.allin1.pojo.Interlocutor;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents Vk dialog
 */
public class VkDialog implements Dialog, Interlocutor {
    static final String MESSAGE_FIELD = "message";
    private static final String UNREAD_FIELD = "unread";
    private static final String TITLE_FIELD = "title";
    private static final String PHOTO50_FIELD = "photo_50";
    private static final String PHOTO100_FIELD = "photo_100";
    private static final String PHOTO200_FIELD = "photo_200";
    private static final String DEFAULT_PHOTO = "https://vk.com/images/camera_50.png";

    private static final String MESSAGE_ID_FIELD = "id";
    private static final String MESSAGE_USER_ID_FIELD = "user_id";
    private static final String MESSAGE_CHAT_ID_FIELD = "chat_id";

    // General fields
    private final int id;
    private volatile int unreadCount;
    private volatile int lastMessageId;

    // Chat fields
    private final String fullName;
    private final String photo50;
    private final String photo100;
    private final String photo200;
    private final boolean isChat;

    VkDialog(JSONObject dialogJson) throws JSONException {
        JSONObject messageJson = dialogJson.getJSONObject(MESSAGE_FIELD);
        int chat_id = messageJson.optInt(MESSAGE_CHAT_ID_FIELD, -1);
        id = chat_id != -1 ? chat_id + 2000000000 : messageJson.getInt(MESSAGE_USER_ID_FIELD);
        unreadCount = dialogJson.optInt(UNREAD_FIELD, 0);
        lastMessageId = messageJson.optInt(MESSAGE_ID_FIELD);
        isChat = (messageJson.optInt(MESSAGE_CHAT_ID_FIELD, -1) != -1);
        fullName = messageJson.getString(TITLE_FIELD);
        photo50 = messageJson.optString(PHOTO50_FIELD, DEFAULT_PHOTO);
        photo100 = messageJson.optString(PHOTO100_FIELD, DEFAULT_PHOTO);
        photo200 = messageJson.optString(PHOTO200_FIELD, DEFAULT_PHOTO);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public boolean isChat() {
        return isChat;
    }

    @Override
    public int getUnreadCount() {
        return unreadCount;
    }

    @Override
    public int getLastMessageId() {
        return lastMessageId;
    }

    @Override
    public String getFullName() {
        return fullName;
    }

    @Override
    public boolean isOnline() {
        return false;
    }

    @Override
    public boolean isOnlineMobile() {
        return false;
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
        return o != null && o instanceof VkDialog && id == ((VkDialog) o).id && unreadCount == (((VkDialog) o).unreadCount);
    }
}
