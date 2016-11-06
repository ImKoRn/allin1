package com.korn.im.allin1.accounts.newaccount;

import com.korn.im.allin1.pojo.Dialog;
import com.korn.im.allin1.pojo.Dialogs;
import com.korn.im.allin1.pojo.Interlocutor;
import com.korn.im.allin1.pojo.User;

import java.util.List;

import rx.Observable;

public interface DbManager <
        TUser extends User,
        TDialogs extends Dialogs,
        TDialog extends Dialog,
        TInterlocutor extends Interlocutor> {
     Observable<TUser> getOwner();
     void saveOwner(TUser owner);

     Observable<List<TUser>> getFriends();
     void saveFriends(List<TUser> users);
     Observable<TUser> getFriend(int id);
     void saveFriend(TUser user);

     Observable<TDialogs> getDialogs();
     void saveDialogs(TDialogs dialogs);
     Observable<TDialog> getDialog(int id);
     void saveDialog(TDialog dialog);


     Observable<List<TInterlocutor>> getInterlocutors();
     void saveInterlocutors(List<TInterlocutor> interlocutors);
     Observable<TInterlocutor> getInterlocutor(int id);
     void saveInterlocutor(TInterlocutor interlocutor);
}
