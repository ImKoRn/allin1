package com.korn.im.allin1.accounts;

import android.app.Activity;

import com.korn.im.allin1.pojo.Dialog;
import com.korn.im.allin1.pojo.Dialogs;
import com.korn.im.allin1.pojo.Interlocutor;
import com.korn.im.allin1.pojo.Message;
import com.korn.im.allin1.pojo.User;

/**
 * Created by korn on 03.08.16.
 */
public interface Account <
        TMessage extends Message,
        TUser extends User,
        TDialogs extends Dialogs<TDialog, TMessage>,
        TDialog extends Dialog,
        TInterlocutor extends Interlocutor> {

    boolean isLoggedIn();

    void logIn(Activity activity);

    void logOut();

    Api<TMessage, TUser, TDialogs, TDialog, TInterlocutor> getApi();
}
