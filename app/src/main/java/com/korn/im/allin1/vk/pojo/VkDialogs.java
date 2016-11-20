package com.korn.im.allin1.vk.pojo;


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import com.korn.im.allin1.pojo.Dialogs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class VkDialogs implements Dialogs<VkDialog, VkMessage> {
    private static final String DIALOGS_FIELD = "dialogs";
    private static final String ITEMS_FIELD = "items";

    private static final String UNREAD_DIALOGS_FIELD = "unread_dialogs";

    private final ImmutableTable<Integer, Integer, VkMessage> messages;
    private final ImmutableMap<Integer, VkDialog> dialogs;
    private final int unreadDialogsCount;

    public VkDialogs(final Dialogs<VkDialog, VkMessage> dialogs) {
        this.unreadDialogsCount = dialogs.getUnreadDialogsCount();
        this.dialogs = ImmutableMap.copyOf(dialogs.getDialogs());
        this.messages = ImmutableTable.copyOf(dialogs.getMessages());
    }

    VkDialogs(final JSONObject from) throws JSONException {
        JSONObject dialogs = from.optJSONObject(DIALOGS_FIELD);
        this.unreadDialogsCount = dialogs.optInt(UNREAD_DIALOGS_FIELD);

        JSONArray dialogsArray = dialogs.getJSONArray(ITEMS_FIELD);

        ImmutableMap.Builder<Integer, VkDialog> dialogsBuilder = ImmutableMap.builder();
        ImmutableTable.Builder<Integer, Integer, VkMessage> messagesBuilder = ImmutableTable.builder();

        JSONObject jsonDialog;
        VkDialog dialog;
        VkMessage message;

        for (int i = 0; i < dialogs.length(); i++) {
            jsonDialog = dialogsArray.getJSONObject(i);
            dialog = new VkDialog(jsonDialog);
            dialogsBuilder.put(dialog.getId(), dialog);
            message = new VkMessage(jsonDialog.getJSONObject(VkDialog.MESSAGE_FIELD));
            messagesBuilder.put(message.getDialogId(), message.getId(), message);
        }

        this.dialogs = dialogsBuilder.build();
        this.messages = messagesBuilder.build();
    }

    @Override
    public int getUnreadDialogsCount() {
        return unreadDialogsCount;
    }

    @Override
    public int size() {
        return dialogs.size();
    }

    @Override
    public Map<Integer, VkDialog> getDialogs() {
        return dialogs;
    }

    @Override
    public VkDialog getDialog(int id) {
        return dialogs.get(id);
    }

    @Override
    public ImmutableTable<Integer, Integer, VkMessage> getMessages() {
        return messages;
    }

    @Override
    public Map<Integer, VkMessage> getDialogMessages(int dialogId) {
        return messages.row(dialogId);
    }

    @Override
    public VkMessage getMessage(int dialogId, int messageId) {
        return messages.get(dialogId, messageId);
    }

}
