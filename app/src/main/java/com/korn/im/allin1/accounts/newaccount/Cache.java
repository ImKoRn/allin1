package com.korn.im.allin1.accounts.newaccount;

import com.korn.im.allin1.pojo.Dialog;
import com.korn.im.allin1.pojo.Dialogs;
import com.korn.im.allin1.pojo.Interlocutor;
import com.korn.im.allin1.pojo.User;
import com.korn.im.allin1.vk.pojo.VkInterlocutor;

import java.util.List;

public interface Cache<
        TUser extends User,
        TDialogs extends Dialogs,
        TDialog extends Dialog,
        TInterlocutor extends Interlocutor> {

    TUser getOwner();
    void saveOwner(TUser owner);

    List<TUser> getFriends();
    void saveFriends(List<TUser> users);

    TUser getFriend(int id);
    void saveFriend(TUser user);

    TDialogs getDialogs();
    void saveDialogs(TDialogs dialogs);

    TDialog getDialog(int id);
    void saveDialog(TDialog dialog);

    TInterlocutor getInterlocutor(int id);

    void saveInterlocutor(VkInterlocutor interlocutor);
    void saveInterlocutors(List<TInterlocutor> interlocutors);
}
