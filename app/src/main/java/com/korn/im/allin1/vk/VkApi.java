package com.korn.im.allin1.vk;

import android.util.Log;
import android.util.Pair;

import com.korn.im.allin1.accounts.Api;
import com.korn.im.allin1.accounts.DataManager;
import com.korn.im.allin1.accounts.DataPublisher;
import com.korn.im.allin1.pojo.Dialog;
import com.korn.im.allin1.pojo.Dialogs;
import com.korn.im.allin1.pojo.Interlocutor;
import com.korn.im.allin1.pojo.Message;
import com.korn.im.allin1.pojo.User;
import com.korn.im.allin1.vk.pojo.VkDialog;
import com.korn.im.allin1.vk.pojo.VkDialogs;
import com.korn.im.allin1.vk.pojo.VkMessage;
import com.korn.im.allin1.vk.pojo.VkUser;

import java.util.Map;

import rx.Observable;

class VkApi implements Api<VkMessage, VkUser, VkDialogs, VkDialog, Interlocutor> {
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

    // Friends data fetching

    /**
     * Fetch friends from cache or database if not exist load it from Internet
     * @return Observable with List of {@link VkUser}
     * */
    @Override
    public Observable<? extends Map<Integer, VkUser>> fetchFriends() {
        return dataManager.getFriends();
    }

    /**
     * @return Observable with List of {@link VkUser}
     * */
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

    // Dialogs data fetching

    /**
     * @return Observable with {@link VkDialogs}
     * */
    @Override
    public Observable<Pair<VkDialogs, ? extends Map<Integer, ? extends Interlocutor>>> fetchDialogs() {
        return dataManager.getDialogs();
    }

    @Override
    public void loadDialogs() {
        if (!isDialogsLoading) {
            isDialogsLoading = true;
            Log.i(TAG, "loadDialogs: dialogs start loading");
            dataPublisher.publishDialogsWhenArrive(dataLoader.loadDialogs(-1, VkRequestUtil.DEFAULT_DIALOGS_COUNT)
                                                             .doOnError(throwable -> isDialogsLoading = false)
                                                             .flatMap(dialogs -> {
                                                                 Observable<Pair<VkDialogs, ? extends Map<Integer, ? extends Interlocutor>>>
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
                                                                 Observable<Pair<VkDialogs, ? extends Map<Integer, ? extends Interlocutor>>>
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
    public Observable<Interlocutor> fetchInterlocutor(int id) {
        return dataManager.getInterlocutor(id);
    }

    private Observable<VkUser> loadUser(int id) {
        return dataLoader.loadUsers(id);
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
    public DataPublisher<? extends Message, ? extends User, ? extends Dialogs, ? extends Dialog, ? extends Interlocutor>
    getDataPublisher() {
        return dataPublisher;
    }

    @Override
    public boolean isFriendsLoading() {
        return isFriendsLoading;
    }

    private Observable<Map<Integer, Interlocutor>> fetchInterlocutors() {
        return dataManager.getInterlocutors();
    }

    // Save methods

    private Observable<Pair<VkDialogs, ? extends Map<Integer, ? extends Interlocutor>>>
    saveDialogs(Pair<VkDialogs, ? extends Map<Integer, ? extends Interlocutor>> dialogs, boolean rewrite) {
        if (dialogs.first.size() > 0)
            dataManager.saveDialogs(dialogs, rewrite);
        return dataManager.getDialogs();
    }

    private Observable<? extends Map<Integer, VkUser>> saveFriends(Map<Integer, VkUser> users, boolean rewrite) {
        if (users.size() > 0)
            dataManager.saveFriends(users, rewrite);
        return dataManager.getFriends();
    }
}
