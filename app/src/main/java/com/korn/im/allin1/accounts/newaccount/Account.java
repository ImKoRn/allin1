package com.korn.im.allin1.accounts.newaccount;

import android.app.Activity;

import com.korn.im.allin1.pojo.Dialog;
import com.korn.im.allin1.pojo.Dialogs;
import com.korn.im.allin1.pojo.Interlocutor;
import com.korn.im.allin1.pojo.User;

/**
 * Created by korn on 03.08.16.
 */
public interface Account <
        TUser extends User,
        TDialogs extends Dialogs,
        TDialog extends Dialog,
        TInterlocutor extends Interlocutor> {

    boolean isLoggedIn();

    void logIn(Activity activity);

    void logOut();

    Api<TUser, TDialogs, TDialog, TInterlocutor> getApi();
}
