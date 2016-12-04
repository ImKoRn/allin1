package com.korn.im.allin1.vk;

import android.util.Pair;

import com.korn.im.allin1.accounts.DbCache;
import com.korn.im.allin1.errors.NoDataException;
import com.korn.im.allin1.pojo.Interlocutor;
import com.korn.im.allin1.vk.pojo.VkDialog;
import com.korn.im.allin1.vk.pojo.VkDialogs;
import com.korn.im.allin1.vk.pojo.VkMessage;
import com.korn.im.allin1.vk.pojo.VkUser;

import java.util.Map;

import rx.Observable;

//TODO create DB interactions
class VkDbCache implements DbCache<VkMessage, VkUser, VkDialogs, VkDialog, Interlocutor> {
    @Override
    public Observable<Map<Integer, VkUser>> getFriends() {
        return Observable.create(subscriber -> {
            subscriber.onError(new Exception("Not found"));
        });
    }

    @Override
    public void saveFriends(final Map<Integer, VkUser> vkUsers) {

    }

    @Override
    public Observable<VkUser> getFriend(final int id) {
        return Observable.error(new Throwable("No data"));
    }

    @Override
    public void saveFriend(final VkUser user) {

    }

    @Override
    public Observable<Pair<VkDialogs, Map<Integer, Interlocutor>>> getDialogs() {
        return Observable.error(new Throwable("No data"));
    }

    @Override
    public void saveDialogs(VkDialogs dialogs) {

    }

    @Override
    public Observable<VkDialog> getDialog(final int id) {
        return Observable.error(new Throwable("No data"));
    }

    @Override
    public void saveDialog(final VkDialog dialog) {

    }

    @Override
    public Observable<Map<Integer, Interlocutor>> getInterlocutors() {
        return Observable.error(new Throwable("No data"));
    }

    @Override
    public void saveInterlocutors(Map<Integer, ? extends Interlocutor> interlocutors) {

    }

    @Override
    public Observable<Interlocutor> getInterlocutor(final int id) {
        return Observable.error(new Throwable("No data"));
    }

    @Override
    public void saveInterlocutor(Interlocutor interlocutor) {

    }

    @Override
    public Observable<Pair<Integer, Map<Integer, VkMessage>>> getMessages(final int id) {
        return Observable.error(new NoDataException());
    }

    @Override
    public void saveMessages(final int id,
                             final Map<Integer, ? extends VkMessage> messages,
                             boolean rewrite) {

    }
}
