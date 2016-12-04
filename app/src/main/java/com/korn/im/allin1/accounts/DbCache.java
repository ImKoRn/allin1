package com.korn.im.allin1.accounts;

import android.util.Pair;

import com.korn.im.allin1.pojo.Dialog;
import com.korn.im.allin1.pojo.Dialogs;
import com.korn.im.allin1.pojo.Interlocutor;
import com.korn.im.allin1.pojo.Message;
import com.korn.im.allin1.pojo.User;

import java.util.Map;

import rx.Observable;

public interface DbCache<
        TMessage extends Message,
        TUser extends User,
        TDialogs extends Dialogs,
        TDialog extends Dialog,
        TInterlocutor extends Interlocutor> {
     Observable<Map<Integer, TUser>> getFriends();
     void saveFriends(Map<Integer, TUser> users);
     Observable<TUser> getFriend(int id);
     void saveFriend(TUser user);

     Observable<Pair<TDialogs, Map<Integer, TInterlocutor>>> getDialogs();
     void saveDialogs(TDialogs dialogs);
     Observable<TDialog> getDialog(int id);
     void saveDialog(TDialog dialog);


     Observable<Map<Integer, TInterlocutor>> getInterlocutors();
     void saveInterlocutors(Map<Integer, ? extends TInterlocutor> interlocutors);
     Observable<TInterlocutor> getInterlocutor(int id);
     void saveInterlocutor(TInterlocutor interlocutor);

     Observable<Pair<Integer, Map<Integer, TMessage>>> getMessages(final int id);
     void saveMessages(final int id,
                       final Map<Integer, ? extends TMessage> messages,
                       boolean rewrite);
}
