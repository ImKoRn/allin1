package com.korn.im.allin1.accounts;

import android.util.Pair;

import com.korn.im.allin1.pojo.Dialog;
import com.korn.im.allin1.pojo.Dialogs;
import com.korn.im.allin1.pojo.Interlocutor;
import com.korn.im.allin1.pojo.Message;
import com.korn.im.allin1.pojo.User;

import java.util.Map;

import rx.Observable;

public interface DataLoader<
        TMessage extends Message,
        TUser extends User,
        TDialogs extends Dialogs,
        TDialog extends Dialog,
        TInterlocutor extends Interlocutor> {
    Observable<? extends Map<Integer, TUser>> loadFriends();

    Observable<Map<Integer, TUser>> loadUsers(int... id);

    Observable<Pair<TDialogs, Map<Integer, TInterlocutor>>> loadDialogs(int offset, int size);

    Observable<TDialog> loadDialog(int id);

    Observable<Pair<Integer, Map<Integer, TMessage>>> loadMessages(int i,
                                                                   int id,
                                                                   int lastMessageStamp);
}
