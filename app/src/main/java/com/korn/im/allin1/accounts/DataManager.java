package com.korn.im.allin1.accounts;

import android.util.Pair;

import com.korn.im.allin1.pojo.Dialog;
import com.korn.im.allin1.pojo.Dialogs;
import com.korn.im.allin1.pojo.Interlocutor;
import com.korn.im.allin1.pojo.Message;
import com.korn.im.allin1.pojo.User;

import java.util.Map;

import rx.Observable;
import rx.schedulers.Schedulers;

public class DataManager<
        TMessage extends Message,
        TUser extends User,
        TDialogs extends Dialogs<TDialog, TMessage>,
        TDialog extends Dialog,
        TInterlocutor extends Interlocutor> {

    private final DbManager<TMessage,TUser, TDialogs, TDialog, TInterlocutor> dbManager;
    private final Cache<TMessage, TUser, TDialogs, TDialog, TInterlocutor> cache;

    public DataManager(Cache<TMessage, TUser, TDialogs, TDialog, TInterlocutor> cache,
                       DbManager<TMessage,TUser, TDialogs, TDialog, TInterlocutor> dbManager) {
        this.cache = cache;
        this.dbManager = dbManager;
    }

    public Observable<Map<Integer, TUser>> getFriends() {
        return cache.getFriends()
                    .onErrorResumeNext(dbManager.getFriends()
                                                .doOnNext(users -> cache.saveFriends(users, false)));
    }

    public void saveFriends(Map<Integer, TUser> users, boolean rewrite) {
        cache.saveFriends(users, rewrite);
        dbManager.saveFriends(users);
    }

    public Observable<TUser> getFriend(int id) {
        return cache.getFriend(id)
                    .onErrorResumeNext(dbManager.getFriend(id)
                                                .doOnNext(cache::saveFriend));
    }

    public Observable<Pair<TDialogs, Map<Integer, TInterlocutor>>> getDialogs() {
        return cache.getDialogs()
                    .onErrorResumeNext(dbManager.getDialogs()
                                                .doOnNext((dialogsAndInterlocutors) -> {
                                                    cache.saveDialogs(dialogsAndInterlocutors.first, false);
                                                    cache.saveInterlocutors(dialogsAndInterlocutors.second, false);
                                                }));
    }

    public void saveDialogs(Pair<TDialogs, Map<Integer, TInterlocutor>> dialogsAndInterlocutors, boolean rewrite) {
        dbManager.saveDialogs(dialogsAndInterlocutors.first);
        dbManager.saveInterlocutors(dialogsAndInterlocutors.second);
        cache.saveDialogs(dialogsAndInterlocutors.first, rewrite);
        cache.saveInterlocutors(dialogsAndInterlocutors.second, rewrite);
    }

    public Observable<TDialog> getDialog(int id) {
        return cache.getDialog(id)
                    .onErrorResumeNext(dbManager.getDialog(id)
                                                .observeOn(Schedulers.computation())
                                                .doOnNext(this::saveDialog));
    }

    public void saveDialog(TDialog dialog) {
        dbManager.saveDialog(dialog);
        cache.saveDialog(dialog);
    }

    public Observable<TInterlocutor> getInterlocutor(int id) {
        return cache.getInterlocutor(id)
                    .onErrorResumeNext(dbManager.getInterlocutor(id)
                                                .observeOn(Schedulers.computation())
                                                .doOnNext(this::saveInterlocutor));
    }

    public void saveInterlocutors(Map<Integer, ? extends TInterlocutor> interlocutors, boolean rewrite) {
        dbManager.saveInterlocutors(interlocutors);
        cache.saveInterlocutors(interlocutors, rewrite);
    }

    private void saveInterlocutor(TInterlocutor interlocutor) {
        dbManager.saveInterlocutor(interlocutor);
        cache.saveInterlocutor(interlocutor);
    }

    public Observable<Map<Integer, TInterlocutor>> getInterlocutors() {
        return cache.getInterlocutors()
                    .onErrorResumeNext(dbManager.getInterlocutors());
    }

    public Observable<Map<Integer, TMessage>> getMessages(int id) {
        return cache.getMessages(id)
                    .onErrorResumeNext(dbManager.getMessages(id)
                                                .doOnNext(messages -> cache.saveMessages(id, messages)));
    }
}
