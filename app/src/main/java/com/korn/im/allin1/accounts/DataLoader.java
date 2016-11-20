package com.korn.im.allin1.accounts;

import android.util.Pair;

import com.korn.im.allin1.pojo.Dialog;
import com.korn.im.allin1.pojo.Dialogs;
import com.korn.im.allin1.pojo.Interlocutor;
import com.korn.im.allin1.pojo.User;
import com.korn.im.allin1.vk.pojo.VkDialogs;

import java.util.Map;

import rx.Observable;

public interface DataLoader<
        TUser extends User,
        TDialogs extends Dialogs,
        TDialog extends Dialog,
        TInterlocutor extends Interlocutor> {
    Observable<? extends Map<Integer, TUser>> loadFriends();

    Observable<TUser> loadUsers(int... id);

    Observable<Pair<VkDialogs, ? extends Map<Integer, ? extends Interlocutor>>> loadDialogs(int offset, int size);

    Observable<TDialog> loadDialog(int id);
}
