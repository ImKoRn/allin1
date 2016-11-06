package com.korn.im.allin1.vk.pojo;

import android.annotation.SuppressLint;

import com.korn.im.allin1.pojo.Message;
import com.korn.im.allin1.vk.pojo.attachments.VkPhotoAttachment;
import com.vk.sdk.api.model.VKApiMessage;
import com.vk.sdk.api.model.VKList;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by korn on 26.08.16.
 */
@SuppressLint("ParcelCreator")
public class VkMessage extends VKApiMessage implements Message {
    private int chat_id = -1;
    private List<VkMessage> fwd_messages;
    private List<VkPhotoAttachment> attachments;

    public VkMessage(JSONObject object) throws JSONException {
        parse(object);
    }

    public VkMessage(String content) {
        read_state = false;
        body = content;
    }

    public VkMessage(Message message) {
        body = message.getContent();
        out = message.isOut();
        id = message.getId();
        date = message.getDate();
        read_state = message.isRead();
    }


    @Override
    public VKApiMessage parse(JSONObject source) throws JSONException {
        chat_id = source.optInt("chat_id", -1);
        id = source.optInt("id");
        user_id = source.optInt("user_id");
        date = source.optLong("date");
        read_state = source.optInt("read_state", 0) == 1;
        out = source.optInt("out", 0) == 1;
        title = source.optString("title");
        body = source.optString("body");
        attachments = new VKList<>(source.optJSONArray("attachments"), VkPhotoAttachment.class);
        fwd_messages = new VKList<>(source.optJSONArray("fwd_messages"), VkMessage.class);
        //emoji = ParseUtils.parseBoolean(source, "emoji");
        deleted = source.optInt("deleted", 0) == 1;
        return this;
    }

    public void setDialogId(int id) {
        user_id = id;
    }

    @Override
    public String getContent() {
        return body;
    }

    @Override
    public long getDate() {
        return date;
    }


    @Override
    public synchronized boolean isRead() {
        return read_state;
    }

    @Override
    public int getDialogId() {
        return isChatMessage()? chat_id + 2000000000 : user_id;
    }

    @Override
    public boolean isOut() {
        return out;
    }

    @Override
    public synchronized boolean isDeleted() {
        return deleted;
    }

    public synchronized void setRead(boolean read) {
        read_state = read;
    }

    public synchronized void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isChatMessage() {
        return chat_id != -1;
    }
}
