package com.korn.im.allin1.vk;

import android.util.Pair;

import com.korn.im.allin1.accounts.DbManager;
import com.korn.im.allin1.pojo.Interlocutor;
import com.korn.im.allin1.vk.pojo.VkDialog;
import com.korn.im.allin1.vk.pojo.VkDialogs;
import com.korn.im.allin1.vk.pojo.VkUser;

import java.util.Map;

import rx.Observable;

//TODO create DB table addition
class VkDbManager implements DbManager<VkUser, VkDialogs, VkDialog, Interlocutor> {
    @Override
    public Observable<Map<Integer, VkUser>> getFriends() {
        return Observable.create(subscriber -> {
            subscriber.onError(new Exception("Not found"));
        });
    }

    @Override
    public void saveFriends(Map<Integer, VkUser> vkUsers) {

    }

    @Override
    public Observable<VkUser> getFriend(int id) {
        return Observable.error(new Throwable("No data"));
    }

    @Override
    public void saveFriend(VkUser user) {

    }

    @Override
    public Observable<Pair<VkDialogs, ? extends Map<Integer, ? extends Interlocutor>>> getDialogs() {
        return Observable.error(new Throwable("No data"));
    }

    @Override
    public void saveDialogs(VkDialogs dialogs) {

    }

    @Override
    public Observable<VkDialog> getDialog(int id) {
        return Observable.error(new Throwable("No data"));
    }

    @Override
    public void saveDialog(VkDialog dialog) {

    }

    @Override
    public Observable<Map<Integer, Interlocutor>> getInterlocutors() {
        return Observable.error(new Throwable("No data"));
    }


    @Override
    public void saveInterlocutors(Map<Integer, ? extends Interlocutor> vkInterlocutors) {

    }

    @Override
    public Observable<Interlocutor> getInterlocutor(int id) {
        return Observable.error(new Throwable("No data"));
    }

    @Override
    public void saveInterlocutor(Interlocutor interlocutor) {

    }
}
