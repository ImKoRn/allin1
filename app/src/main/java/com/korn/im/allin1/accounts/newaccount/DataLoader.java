package com.korn.im.allin1.accounts.newaccount;

import android.util.Pair;

import com.korn.im.allin1.pojo.Dialog;
import com.korn.im.allin1.pojo.Dialogs;
import com.korn.im.allin1.pojo.Interlocutor;
import com.korn.im.allin1.pojo.User;

import java.util.List;

import rx.Observable;

public interface DataLoader<
        TUser extends User,
        TDialogs extends Dialogs,
        TDialog extends Dialog,
        TInterlocutor extends Interlocutor> {
    Observable<List<TUser>> loadFriends();
    Observable<List<TUser>> loadNextPageOfFriends();

    Observable<TUser> loadUser(int id);

    Observable<Pair<TDialogs, List<TInterlocutor>>> loadDialogs();
    Observable<Pair<TDialogs, List<TInterlocutor>>> loadNextPageOfDialogs();

    Observable<TDialog> loadDialog(int id);
}
