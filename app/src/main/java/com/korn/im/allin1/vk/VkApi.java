package com.korn.im.allin1.vk;

import android.util.Log;
import android.util.Pair;

import com.korn.im.allin1.accounts.Api;
import com.korn.im.allin1.accounts.DataManager;
import com.korn.im.allin1.errors.NoDataException;
import com.korn.im.allin1.pojo.Dialogs;
import com.korn.im.allin1.pojo.Interlocutor;
import com.korn.im.allin1.vk.pojo.VkDialog;
import com.korn.im.allin1.vk.pojo.VkDialogs;
import com.korn.im.allin1.vk.pojo.VkMessage;
import com.korn.im.allin1.vk.pojo.VkUser;

import java.util.Map;

import rx.Observable;

class VkApi implements Api {
    private static final String TAG = "VkApi";
    private final DataManager<VkMessage, VkUser, VkDialogs, VkDialog, Interlocutor> dataManager;
    private final VkDataLoader dataLoader;
    private final VkDataPublisher dataPublisher = new VkDataPublisher();
    private final VkCache vkCache;

    private volatile boolean hasMoreDialogsToUpdate = true;
    private volatile boolean hasMoreMessagesToUpdate = true;

    private volatile boolean isFriendsLoading = false;
    private volatile boolean isDialogsLoading = false;

    VkApi() {
        this.vkCache = new VkCache();
        this.dataManager = new DataManager<>(vkCache, new VkDbManager());
        this.dataLoader = new VkDataLoader();
    }

    // Friends data
    @Override
    public Observable<? extends Map<Integer, VkUser>> fetchFriends() {
        return dataManager.getFriends();
    }

    @Override
    public void loadFriends() {
        if (!isFriendsLoading) {
            isFriendsLoading = true;
            Log.i(TAG, "loadFriends: friends start loading");
            dataPublisher.publishFriendsWhenArrive(dataLoader.loadFriends()
                                                             .doOnError(throwable -> isFriendsLoading = false)
                                                             .flatMap(friends -> {
                                                                 Observable<? extends Map<Integer, VkUser>> savedFriends = saveFriends(friends, true);
                                                                 isFriendsLoading = false;
                                                                 Log.i(TAG, "loadFriends: friends stop loading");
                                                                 return savedFriends;
                                                             }));
        }
    }

    @Override
    public Observable<VkUser> fetchFriend(int id) {
        return dataManager.getFriend(id);
    }

    // Dialogs data
    @Override
    public Observable<? extends Pair<? extends Dialogs, ? extends Map<Integer, ? extends Interlocutor>>> fetchDialogs() {
        return dataManager.getDialogs();
    }

    @Override
    public void loadDialogs() {
        if (!isDialogsLoading) {
            isDialogsLoading = true;
            Log.i(TAG, "loadDialogs: dialogs start loading");
            dataPublisher.publishDialogsWhenArrive(dataLoader.loadDialogs(0, VkRequestUtil.DEFAULT_DIALOGS_COUNT)
                                                             .doOnError(throwable -> isDialogsLoading = false)
                                                             .flatMap(dialogs -> {
                                                                 Observable<Pair<VkDialogs, Map<Integer, Interlocutor>>>
                                                                         savedDialogs = saveDialogs(dialogs, true);
                                                                 isDialogsLoading = false;
                                                                 hasMoreDialogsToUpdate = (dialogs.first.size() == VkRequestUtil.DEFAULT_DIALOGS_COUNT);
                                                                 Log.i(TAG, "loadDialogs: dialogs stop loading");
                                                                 return savedDialogs;
                                                             }));
        }
    }

    @Override
    public void loadNextDialogs() {
        if (!isDialogsLoading) {
            isDialogsLoading = true;
            Log.i(TAG, "loadDialogs: dialogs start loading");
            dataPublisher.publishDialogsWhenArrive(dataLoader.loadDialogs(vkCache.getNextDialogsStamp(), VkRequestUtil.DEFAULT_DIALOGS_COUNT)
                                                             .doOnError(throwable -> isDialogsLoading = false)
                                                             .flatMap(dialogs -> {
                                                                 Observable<Pair<VkDialogs, Map<Integer, Interlocutor>>>
                                                                         savedDialogs = saveDialogs(dialogs, true);
                                                                 isDialogsLoading = false;
                                                                 hasMoreDialogsToUpdate = (dialogs.first.size() == VkRequestUtil.DEFAULT_DIALOGS_COUNT);
                                                                 Log.i(TAG, "loadDialogs: dialogs stop loading");
                                                                 return savedDialogs;
                                                             }));
        }
    }

    @Override
    public Observable<VkDialog> fetchDialog(int id) {
        return dataManager.getDialog(id);
    }

    @Override
    public Observable<VkDialog> loadDialog(int id) {
        return dataLoader.loadDialog(id).doOnNext(dataManager::saveDialog);
    }

    @Override
    public Observable<Map<Integer, VkMessage>> fetchMessages(int id) {
        return dataManager.getMessages(id);
    }

    @Override
    public Observable<Interlocutor> fetchInterlocutor(int id) {
        return dataManager.getInterlocutor(id);
    }

    @Override
    public Observable<? extends Interlocutor> loadInterlocutor(int id) {
        return dataLoader.loadUsers(id)
                         .flatMap(this::saveInterlocutors)
                         .flatMap(integerMap -> {
                             Interlocutor interlocutor = integerMap.get(id);
                             if (interlocutor == null) return Observable.error(new NoDataException());
                             else return Observable.just(interlocutor);
                         });
    }

    @Override
    public Observable<Map<Integer, ? extends Interlocutor>> loadInterlocutors(int... id) {
        return dataLoader.loadUsers(id).flatMap(this::saveInterlocutors);
    }

    @Override
    public Observable<Map<Integer, Interlocutor>> fetchInterlocutors() {
        return dataManager.getInterlocutors();
    }

    // Check to next load methods
    @Override
    public boolean hasMoreDialogsToUpdate() {
        return hasMoreDialogsToUpdate;
    }

    @Override
    public boolean hasMoreFriendsToUpdate() {
        return false;
    }

    @Override
    public boolean hasMoreMessagesToUpdate(int id) {
        return hasMoreMessagesToUpdate;
    }

    @Override
    public VkDataPublisher getDataPublisher() {
        return dataPublisher;
    }

    @Override
    public boolean isFriendsLoading() {
        return isFriendsLoading;
    }

    // Save methods
    private Observable<? extends Map<Integer, VkUser>> saveFriends(Map<Integer, VkUser> users, boolean rewrite) {
        if (users.size() > 0)
            dataManager.saveFriends(users, rewrite);
        return dataManager.getFriends();
    }

    private Observable<Pair<VkDialogs, Map<Integer, Interlocutor>>>
    saveDialogs(Pair<VkDialogs, Map<Integer, Interlocutor>> dialogs, boolean rewrite) {
        if (dialogs.first.size() > 0)
            dataManager.saveDialogs(dialogs, rewrite);
        return dataManager.getDialogs();
    }

    private Observable<Map<Integer, Interlocutor>> saveInterlocutors(Map<Integer, ? extends Interlocutor> interlocutors) {
        if (interlocutors.size() > 0) dataManager.saveInterlocutors(interlocutors, false);
        return dataManager.getInterlocutors();
    }
}
