package com.korn.im.allin1;

import com.korn.im.allin1.accounts.newaccount.DbManager;
import com.korn.im.allin1.vk.pojo.VkDialog;
import com.korn.im.allin1.vk.pojo.newvkpojo.VkDialogs;
import com.korn.im.allin1.vk.pojo.VkInterlocutor;
import com.korn.im.allin1.vk.pojo.VkUser;

import java.util.List;

import rx.Observable;
//TODO create DB table addition
public class VkDbManager implements DbManager<VkUser, VkDialogs, VkDialog, VkInterlocutor> {
    @Override
    public Observable<VkUser> getOwner() {
        return null;
    }

    @Override
    public void saveOwner(VkUser owner) {

    }

    @Override
    public Observable<List<VkUser>> getFriends() {
        return Observable.error(new Throwable("No data"));
    }

    @Override
    public void saveFriends(List<VkUser> vkUsers) {

    }

    @Override
    public Observable<VkUser> getFriend(int id) {
        return Observable.error(new Throwable("No data"));
    }

    @Override
    public void saveFriend(VkUser user) {

    }

    @Override
    public Observable<VkDialogs> getDialogs() {
        return null;
    }

    @Override
    public void saveDialogs(VkDialogs dialogs) {

    }

    @Override
    public Observable<VkDialog> getDialog(int id) {
        return null;
    }

    @Override
    public void saveDialog(VkDialog dialog) {

    }

    @Override
    public Observable<List<VkInterlocutor>> getInterlocutors() {
        return null;
    }

    @Override
    public void saveInterlocutors(List<VkInterlocutor> vkInterlocutors) {

    }

    @Override
    public Observable<VkInterlocutor> getInterlocutor(int id) {
        return Observable.error(new Throwable("No data"));
    }

    @Override
    public void saveInterlocutor(VkInterlocutor interlocutor) {

    }
}
