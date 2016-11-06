package com.korn.im.allin1.vk;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by korn on 09.08.16.
 */
public class VkLongPullEvent {
    private HashMap<Integer, Integer> dialogsWriteMap;
    private HashMap<Integer, Integer> friendsStatusMap;
    private HashMap<Integer, List<Pair<Integer, Integer>>> messagesFlagsMap;

    private VkDialogsUpdate dialogsUpdate;
    private HashMap<Integer, List<Pair<Integer, Boolean>>> messagesReadsMap;

    public void addDialogWriteEvent(int id, int who) {
        if(dialogsWriteMap == null)
            dialogsWriteMap = new HashMap<>();

        dialogsWriteMap.put(id, who);
    }

    public Map<Integer, Integer> getWritersMap() {
        if (dialogsWriteMap == null)
            return Collections.emptyMap();

        return dialogsWriteMap;
    }

    public void addFriendStatusEvent(int id, int status) {
        if(friendsStatusMap == null)
            friendsStatusMap = new HashMap<>();

        friendsStatusMap.put(id, status);
    }

    public Map<Integer, Integer> getFriendsStatusMap() {
        if (friendsStatusMap == null)
            return Collections.emptyMap();

        return friendsStatusMap;
    }

    public void addMessageFlagEvent(int id, Pair<Integer, Integer> flag) {
        if(messagesFlagsMap == null)
            messagesFlagsMap = new HashMap<>();

        List<Pair<Integer, Integer>> list = messagesFlagsMap.get(id);
        if (list == null) list = new ArrayList<>();
        list.add(flag);
        messagesFlagsMap.put(id, list);
    }

    public Map<Integer, List<Pair<Integer, Integer>>> getMessageFlagEvents() {
        if (messagesFlagsMap == null)
            return Collections.emptyMap();

        return messagesFlagsMap;
    }

    public void addMessageReadEvent(int id, Pair<Integer, Boolean> outgoing) {
        if(messagesReadsMap == null)
            messagesReadsMap = new HashMap<>();

        List<Pair<Integer, Boolean>> list = messagesReadsMap.get(id);
        if (list == null) list = new LinkedList<>();
        list.add(outgoing);
        messagesReadsMap.put(id, list);
    }

    public Map<Integer, List<Pair<Integer, Boolean>>> getMessageReadEvents() {
        if (messagesReadsMap == null)
            return Collections.emptyMap();

        return messagesReadsMap;
    }

    public void addDialogsUpdate(VkDialogsUpdate dialogsUpdate) {
        this.dialogsUpdate = dialogsUpdate;
    }

    public VkDialogsUpdate getDialogsUpdate() {
        return dialogsUpdate;
    }
}
