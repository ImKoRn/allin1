package com.korn.im.allin1.accounts;

import android.util.Pair;

import com.korn.im.allin1.pojo.Dialog;
import com.korn.im.allin1.pojo.Dialogs;
import com.korn.im.allin1.pojo.Interlocutor;
import com.korn.im.allin1.pojo.Message;
import com.korn.im.allin1.pojo.User;

import java.util.Map;

import rx.Observable;

public interface DataPublisher<
        TMessage extends Message,
        TUser extends User,
        TDialogs extends Dialogs<TDialog, TMessage>,
        TDialog extends Dialog,
        TInterlocutor extends Interlocutor> {
    Observable<? extends Map<Integer, TUser>> friendsObservable();

    Observable<Throwable> friendsErrorsObservable();

    Observable<Pair<TDialogs, ? extends Map<Integer, ? extends TInterlocutor>>> dialogsObservable();

    Observable<Throwable> dialogsErrorsObservable();
}
