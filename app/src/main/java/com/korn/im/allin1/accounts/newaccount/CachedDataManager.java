package com.korn.im.allin1.accounts.newaccount;

import com.korn.im.allin1.pojo.Dialog;
import com.korn.im.allin1.pojo.Dialogs;
import com.korn.im.allin1.pojo.Interlocutor;
import com.korn.im.allin1.pojo.User;

import java.util.List;

import rx.Observable;

public class CachedDataManager <
        TUser extends User,
        TDialogs extends Dialogs,
        TDialog extends Dialog,
        TInterlocutor extends Interlocutor,
        TDataBaseManager extends DbManager<TUser, TDialogs, TDialog, TInterlocutor>,
        TCache extends Cache<TUser, TDialogs, TDialog, TInterlocutor>> {

    private TDataBaseManager dbManager;
    private TCache cache;

    public CachedDataManager(TDataBaseManager dbManager, TCache cache) {
        this.dbManager = dbManager;
        this.cache = cache;
    }

    public Observable<List<TUser>> getFriends() {
        List<TUser> list = cache.getFriends();
        if (list == null)
            return dbManager.getFriends().doOnNext(users -> cache.saveFriends(users));

        return Observable.just(list);
    }

    public void saveFriends(List<TUser> users) {
        dbManager.saveFriends(users);
        cache.saveFriends(users);
    }

    public Observable<TUser> getFriend(int id) {
        TUser user = cache.getFriend(id);
        if (user == null)
            return dbManager.getFriend(id);

        return Observable.just(user);
    }

    public void saveFriend(TUser user) {
        dbManager.saveFriend(user);
        cache.saveFriend(user);
    }

    public Observable<TDialogs> getDialogs() {
        TDialogs dialogs = cache.getDialogs();
        if (dialogs == null)
            return dbManager.getDialogs();

        return Observable.just(dialogs);
    }

    public void saveDialogs(TDialogs dialogs) {
        dbManager.saveDialogs(dialogs);
        cache.saveDialogs(dialogs);
    }

    public Observable<TDialog> getDialog(int id) {
        TDialog dialog = cache.getDialog(id);
        if (dialog == null)
            return dbManager.getDialog(id);

        return Observable.just(dialog);
    }

    public void saveDialog(TDialog dialog) {
        dbManager.saveDialog(dialog);
        cache.saveDialog(dialog);
    }

    public Observable<TInterlocutor> getInterlocutor(int id) {
        TInterlocutor interlocutor = cache.getInterlocutor(id);
        if (interlocutor == null)
            return dbManager.getInterlocutor(id);

        return Observable.just(interlocutor);
    }

    public void saveInterlocutors(List<TInterlocutor> interlocutors) {
        dbManager.saveInterlocutors(interlocutors);
        cache.saveInterlocutors(interlocutors);
    }

    public Observable<TUser> getOwner() {
        TUser owner = cache.getOwner();
        if (owner == null)
            return dbManager.getOwner();
        return Observable.just(owner);
    }

    public void saveOwner(TUser owner) {
        dbManager.saveOwner(owner);
        cache.saveOwner(owner);
    }
}
