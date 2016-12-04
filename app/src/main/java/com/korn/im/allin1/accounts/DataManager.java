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
        TInterlocutor extends Interlocutor,
        TCache extends Cache<TMessage, TUser, TDialogs, TDialog, TInterlocutor>,
        TDbCache extends DbCache<TMessage,TUser, TDialogs, TDialog, TInterlocutor>> {

    private final TCache cache;
    private final TDbCache dbCache;

    public DataManager(TCache cache,
                       TDbCache dbCache) {
        this.cache = cache;
        this.dbCache = dbCache;
    }

    public Observable<Map<Integer, TUser>> getFriends() {
        return cache.getFriends()
                    .onErrorResumeNext(dbCache.getFriends()
                                              .doOnNext(users -> cache.saveFriends(users, false)));
    }

    public void saveFriends(Map<Integer, TUser> users, boolean rewrite) {
        cache.saveFriends(users, rewrite);
        dbCache.saveFriends(users);
    }

    public Observable<TUser> getFriend(int id) {
        return cache.getFriend(id)
                    .onErrorResumeNext(dbCache.getFriend(id)
                                              .doOnNext(cache::saveFriend));
    }

    public Observable<Pair<TDialogs, Map<Integer, TInterlocutor>>> getDialogs() {
        return cache.getDialogs()
                    .onErrorResumeNext(dbCache.getDialogs()
                                              .doOnNext((dialogsAndInterlocutors) -> {
                                                    cache.saveDialogs(dialogsAndInterlocutors.first, false);
                                                    cache.saveInterlocutors(dialogsAndInterlocutors.second, false);
                                                }));
    }

    public void saveDialogs(Pair<TDialogs, Map<Integer, TInterlocutor>> dialogsAndInterlocutors, boolean rewrite) {
        dbCache.saveDialogs(dialogsAndInterlocutors.first);
        dbCache.saveInterlocutors(dialogsAndInterlocutors.second);
        cache.saveDialogs(dialogsAndInterlocutors.first, rewrite);
        cache.saveInterlocutors(dialogsAndInterlocutors.second, rewrite);
    }

    public Observable<TDialog> getDialog(int id) {
        return cache.getDialog(id)
                    .onErrorResumeNext(dbCache.getDialog(id)
                                              .observeOn(Schedulers.computation())
                                              .doOnNext(this::saveDialog));
    }

    public void saveDialog(TDialog dialog) {
        dbCache.saveDialog(dialog);
        cache.saveDialog(dialog);
    }

    public Observable<TInterlocutor> getInterlocutor(int id) {
        return cache.getInterlocutor(id)
                    .onErrorResumeNext(dbCache.getInterlocutor(id)
                                              .observeOn(Schedulers.computation())
                                              .doOnNext(this::saveInterlocutor));
    }

    public void saveInterlocutors(Map<Integer, ? extends TInterlocutor> interlocutors, boolean rewrite) {
        dbCache.saveInterlocutors(interlocutors);
        cache.saveInterlocutors(interlocutors, rewrite);
    }

    private void saveInterlocutor(TInterlocutor interlocutor) {
        dbCache.saveInterlocutor(interlocutor);
        cache.saveInterlocutor(interlocutor);
    }

    public Observable<Map<Integer, TInterlocutor>> getInterlocutors() {
        return cache.getInterlocutors()
                    .onErrorResumeNext(dbCache.getInterlocutors());
    }

    public Observable<Pair<Integer, Map<Integer, TMessage>>> getMessages(int id) {
        return cache.getMessages(id)
                    .onErrorResumeNext(dbCache.getMessages(id)
                                              .doOnNext(messages -> cache.saveMessages(id, messages.second,
                                                                                       false)));
    }

    public void saveMessages(Integer id,
                             Map<Integer, TMessage> messages,
                             boolean rewrite) {
        dbCache.saveMessages(id, messages, rewrite);
        cache.saveMessages(id, messages, rewrite);
    }

    protected final TCache getCache() {
        return cache;
    }

    protected final TDbCache getDbCache() {
        return dbCache;
    }
}
