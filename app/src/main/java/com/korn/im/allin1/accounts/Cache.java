package com.korn.im.allin1.accounts;

import android.util.Pair;

import com.korn.im.allin1.pojo.Dialog;
import com.korn.im.allin1.pojo.Dialogs;
import com.korn.im.allin1.pojo.Interlocutor;
import com.korn.im.allin1.pojo.Message;
import com.korn.im.allin1.pojo.User;

import java.util.Map;

import rx.Observable;

public interface Cache<
        TMessage extends Message,
        TUser extends User,
        TDialogs extends Dialogs,
        TDialog extends Dialog,
        TInterlocutor extends Interlocutor> {

    Observable<Map<Integer, TUser>> getFriends();
    void saveFriends(final Map<Integer, TUser> friends, final boolean rewrite);

    Observable<TUser> getFriend(final int id);
    void saveFriend(final TUser friend);

    Observable<Pair<TDialogs, Map<Integer, TInterlocutor>>> getDialogs();
    void saveDialogs(final TDialogs dialogs, final boolean rewrite);

    Observable<TDialog> getDialog(final int id);
    void saveDialog(TDialog dialog);

    Observable<Map<Integer, TMessage>> getMessages(final int id);
    void saveMessages(final int id, final Map<Integer, ? extends TMessage> messages);

    Observable<TInterlocutor> getInterlocutor(final int id);
    Observable<Map<Integer, TInterlocutor>> getInterlocutors();

    void saveInterlocutor(final TInterlocutor interlocutor);
    void saveInterlocutors(final Map<Integer, ? extends TInterlocutor> interlocutors, final boolean rewrite);

}
