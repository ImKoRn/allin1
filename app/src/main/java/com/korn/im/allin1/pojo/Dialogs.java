package com.korn.im.allin1.pojo;

import com.google.common.collect.Table;
import java.util.Map;

/**
 * Dialogs representation
 */
public interface Dialogs<TDialog extends Dialog, TMessage extends Message> {
    int size();
    int getUnreadDialogsCount();

    Map<Integer, TDialog> getDialogs();
    TDialog getDialog(int id);
    Table<Integer, Integer, TMessage> getMessages();
    Map<Integer, TMessage> getDialogMessages(int dialogId);
    TMessage getMessage(int dialogId, int messageId);
}
