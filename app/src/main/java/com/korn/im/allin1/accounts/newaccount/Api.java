package com.korn.im.allin1.accounts.newaccount;

import com.korn.im.allin1.pojo.Dialog;
import com.korn.im.allin1.pojo.Dialogs;
import com.korn.im.allin1.pojo.Interlocutor;
import com.korn.im.allin1.pojo.User;
import com.korn.im.allin1.vk.pojo.VkUser;

import java.util.List;

import rx.Observable;

public interface Api<TUser extends User,
        TDialogs extends Dialogs,
        TDialog extends Dialog,
        TInterlocutor extends Interlocutor> {
    Observable<List<TUser>> fetchFriends();
    Observable<List<TUser>> fetchNextPageOfFriends();
    Observable<TUser> fetchFriend(int id);

    Observable<VkUser> fetchOwner();

    Observable<TDialogs> fetchDialogs();
    Observable<TDialog> fetchDialog(int id);

    Observable<TInterlocutor> fetchInterlocutor(int id);

    Observable<TUser> fetchUser(int id);

    boolean hasMoreDialogsToUpdate();
    boolean hasMoreFriendsToUpdate();
    boolean hasMoreMessagesToUpdate(int id);
}
