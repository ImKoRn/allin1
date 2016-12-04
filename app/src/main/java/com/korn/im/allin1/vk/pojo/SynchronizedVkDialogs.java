package com.korn.im.allin1.vk.pojo;

import android.annotation.SuppressLint;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import com.korn.im.allin1.pojo.Dialogs;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Synchronized Vk dialog
 */
@SuppressLint("UseSparseArrays")
public class SynchronizedVkDialogs implements Dialogs<VkDialog, VkMessage> {
    private final Map<Integer, VkDialog> dialogs = Collections.synchronizedMap(new HashMap<>());
    private final Table<Integer, Integer, VkMessage> messages = HashBasedTable.create();
    private volatile int unreadDialogCount = 0;

    @Override
    public int getDialogsCount() {
        return dialogs.size();
    }

    @Override
    public int getUnreadDialogsCount() {
        return unreadDialogCount;
    }

    public void setUnreadDialogsCount(int unreadDialogs) {
        unreadDialogCount = unreadDialogs;
    }

    @Override
    public Map<Integer, VkDialog> getDialogs() {
        synchronized (dialogs) {
            return ImmutableMap.copyOf(dialogs);
        }
    }

    public void addDialogs(Map<Integer, VkDialog> newDialogs, boolean rewrite) {
        synchronized (dialogs) {
            if (rewrite) dialogs.clear();
            dialogs.putAll(newDialogs);
        }
    }

    @Override
    public VkDialog getDialog(int id) {
        return dialogs.get(id);
    }

    @Override
    public Table<Integer, Integer, VkMessage> getMessages() {
        synchronized (messages) {
            return ImmutableTable.copyOf(messages);
        }
    }

    @Override
    public Map<Integer, VkMessage> getDialogMessages(int dialogId) {
        synchronized (messages) {
            return ImmutableMap.copyOf(messages.row(dialogId));
        }
    }

    @Override
    public VkMessage getMessage(int dialogId, int messageId) {
        synchronized (messages) {
            return messages.get(dialogId, messageId);
        }
    }

    public void addDialog(VkDialog newDialog) {
        dialogs.put(newDialog.getId(), newDialog);
    }

    public VkDialogs getCopy() {
        synchronized (dialogs) {
            synchronized (messages) {
                return new VkDialogs(this);
            }
        }
    }

    public void addMessages(Table<Integer, Integer, VkMessage> messages,
                            boolean rewrite) {
        synchronized (this.messages) {
            if (rewrite) this.messages.clear();
            this.messages.putAll(messages);
        }
    }

    public void addMessagesToDialog(int id,
                                    Map<Integer, ? extends VkMessage> messages,
                                    boolean rewrite) {
        synchronized (dialogs) {
            synchronized (this.messages) {
                if (rewrite) this.messages.row(id).clear();
                this.messages.row(id).putAll(messages);
            }
        }
    }

    // Data stamps

    public int nextDialogsStamp() {
        synchronized (dialogs) {
            int stamp = 0;
            for (Map.Entry<Integer, VkDialog> entry : dialogs.entrySet())
                if (stamp > entry.getValue().getLastMessageId() || stamp == 0)
                    stamp = entry.getValue().getLastMessageId();
            return stamp;
        }
    }

    public int nextMessagesStamp(int id) {
        VkDialog dialog = dialogs.get(id);
        if (dialog == null) return 0;
        return dialog.getFirstMessageId();
    }

    public int getMessagesCount(int id) {
        return messages.row(id).size();
    }
}
