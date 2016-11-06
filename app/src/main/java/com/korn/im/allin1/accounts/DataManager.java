package com.korn.im.allin1.accounts;

import com.korn.im.allin1.pojo.Dialog;
import com.korn.im.allin1.pojo.Dialogs;
import com.korn.im.allin1.pojo.User;

import java.util.Collection;

/**
 * Created by korn on 18.09.16.
 */
public interface DataManager {
    <T extends Dialogs> T getDialogs();
    <T extends Dialog> T getDialog(int id);

    <T extends User> Collection<T> getFriends();
    <T extends User> T getFriend(int id);

    boolean hasMoreDialogsToUpdate();
    boolean hasMoreFriendsToUpdate();
    boolean hasMoreMessagesToUpdate(int id);
}
