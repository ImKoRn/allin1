package com.korn.im.allin1.vk.pojo;

import android.util.Log;

import com.korn.im.allin1.pojo.Message;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Vk message representation
 */
public class VkMessage implements Message {
    private static final String ID_FIELD = "id";
    private static final String USER_ID_FIELD = "user_id";
    private static final String BODY_FIELD = "body";
    private static final String DATE_FIELD = "date";
    private static final String READ_FIELD = "read_state";
    private static final String OUT_FIELD = "out";
    private static final String DELETED_FIELD = "deleted";
    private static final String CHAT_ID = "chat_id";

    // General fields
    private final int id;
    private final int userId;
    private final int dialogId;
    private final long date;
    private final String content;
    private final boolean isOut;
    private final boolean isRead;

    // Optional fields
    private final boolean deleted;
    private final int chatId;

    public VkMessage(JSONObject source) throws JSONException {
        // General fields
        Log.w("Message",
              "VkMessage: " + source.toString());
        id = source.getInt(ID_FIELD);
        userId = source.getInt(USER_ID_FIELD);
        content = source.getString(BODY_FIELD);
        date = source.getLong(DATE_FIELD);
        isRead = source.getInt(READ_FIELD) == 1;
        isOut = source.getInt(OUT_FIELD) == 1;

        // Optional fields
        deleted = source.optInt(DELETED_FIELD, 0) == 1;
        chatId = source.optInt(CHAT_ID, -1);

        dialogId = isChatMessage() ? chatId + 2000000000 : userId;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getDialogId() {
        return dialogId;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public long getDate() {
        return date;
    }

    @Override
    public boolean isRead() {
        return isRead;
    }

    @Override
    public boolean isOut() {
        return isOut;
    }

    boolean isDeleted() {
        return deleted;
    }

    boolean isChatMessage() {
        return chatId != -1 ;
    }
}
