package com.korn.im.allin1.vk.pojo;

import android.annotation.SuppressLint;
import android.os.Parcel;
import com.korn.im.allin1.pojo.Dialog;
import com.korn.im.allin1.pojo.Interlocutor;
import com.korn.im.allin1.pojo.Message;
import com.vk.sdk.api.model.VKApiDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by korn on 09.08.16.
 */
@SuppressLint("ParcelCreator")
@SuppressWarnings("unchecked")
public class VkDialog extends VKApiDialog implements Dialog, VkInterlocutor {
    private volatile int interlocutorId;
    private final List<VkMessage> messages = Collections.synchronizedList(new LinkedList<VkMessage>());

    private boolean isChat = false;
    private String fullName = "";
    private String photo_50 = "";
    private String photo_100 = "";
    private String photo_200 = "";

    private volatile boolean hasNextMessages = true;
    private volatile int firstOutUnreadIndex = -1;
    private volatile int firstInUnreadIndex = -1;

    public VkDialog(JSONObject source) throws JSONException {
        parse(source);
    }

    public VkDialog(VkMessage vkMessage) {
        isChat = vkMessage.isChatMessage();
        interlocutorId = vkMessage.getDialogId();
        messages.add(0, vkMessage);
        unread = !vkMessage.isOut() && !vkMessage.isRead() ? 1 : 0;
    }

    @Override
    public int getId() {
        return interlocutorId;
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
    public boolean isChat() {
        return isChat;
    }

    @Override
    public List<VkMessage> getMessages() {
        return messages;
    }

    @Override
    public VkMessage getMessage(int id) {
        return messages.get(id);
    }

    @Override
    public int getUnreadCount() {
        return unread;
    }


    @Override
    public long getDate() {
        return messages.get(0).getDate();
    }

    @Override
    public VKApiDialog parse(JSONObject from) throws JSONException {
        unread = from.optInt("unread");

        from = from.optJSONObject("message");
        VkMessage message = new VkMessage(from);
        messages.add(0, message);

        firstInUnreadIndex = !message.isOut() && !message.isRead()? 0 : -1;
        firstOutUnreadIndex = message.isOut() && !message.isRead()? 0 : -1;

        if(message.isChatMessage()) {
            isChat = true;
            interlocutorId = message.getDialogId();
            fullName = from.optString("title");
            photo_50 = from.optString("photo_50");
            photo_100 = from.optString("photo_100");
            photo_200 = from.optString("photo_200");
        }
        else {
            isChat = false;
            interlocutorId = message.user_id;
        }
        return this;
    }

    @Override
    public boolean isHasNextMessages() {
        return hasNextMessages;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null)
            return false;

        if(o instanceof VkDialog) {
            VkDialog d = (VkDialog) o;
            return interlocutorId == d.interlocutorId &&
                    unread == d.unread;
        }
        else return false;
    }

    @Override
    public void setOnline(boolean online) {}

    @Override
    public void setOnlineMobile(boolean online) {}

    public void setUnreadCount(int count) {
        unread = count;
    }

    public void setHasNextMessages(boolean hasNextMessages) {
        this.hasNextMessages = hasNextMessages;
    }

    public void addMessage(VkMessage message) {
        synchronized (messages) {
            messages.add(0, message);
            if(!message.isRead()) {
                if (message.isOut())
                    firstOutUnreadIndex++;
                else {
                    unread++;
                    firstInUnreadIndex++;
                }
            }
        }
    }

    public int getFirstOutUnreadIndex() {
        return firstOutUnreadIndex;
    }

    public void setFirstOutUnreadIndex(int firstOutUnreadIndex) {
        this.firstOutUnreadIndex = firstOutUnreadIndex;
    }

    public int getFirstInUnreadIndex() {
        return firstInUnreadIndex;
    }

    public void setFirstInUnreadIndex(int firstInUnreadIndex) {
        this.firstInUnreadIndex = firstInUnreadIndex;
    }
}
