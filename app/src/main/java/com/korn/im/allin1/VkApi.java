package com.korn.im.allin1;

import com.korn.im.allin1.accounts.newaccount.Api;
import com.korn.im.allin1.accounts.newaccount.CachedDataManager;
import com.korn.im.allin1.accounts.newaccount.DataLoader;
import com.korn.im.allin1.vk.pojo.VkDialog;
import com.korn.im.allin1.vk.pojo.VkInterlocutor;
import com.korn.im.allin1.vk.pojo.VkUser;
import com.korn.im.allin1.vk.pojo.newvkpojo.VkDialogs;

import java.util.List;

import rx.Observable;

public class VkApi implements Api<VkUser, VkDialogs, VkDialog, VkInterlocutor> {
    private CachedDataManager<VkUser, VkDialogs, VkDialog, VkInterlocutor, VkDbManager, VkCache> cachedDataManager;
    private DataLoader<VkUser, VkDialogs, VkDialog, VkInterlocutor> dataLoader;

    private boolean hasMoreDialogsToUpdate = true;
    private boolean hasMoreMessagesToUpdate = true;

    private String userId;

    public VkApi(String userId) {
        this.userId = userId;
        this.cachedDataManager = new CachedDataManager<>(new VkDbManager(), new VkCache());
        this.dataLoader = new VkDataLoader(userId);
    }

    @Override
    public Observable<List<VkUser>> fetchFriends() {
        return cachedDataManager
                .getFriends()
                .onErrorResumeNext(dataLoader
                                .loadFriends()
                                .doOnNext(users -> {
                                    if (users.size() != 0) {
                                        cachedDataManager.saveFriends(users);
                                        hasMoreMessagesToUpdate = true;
                                    } else hasMoreMessagesToUpdate = false;
                                })
                );
    }

    @Override
    public Observable<List<VkUser>> fetchNextPageOfFriends() {
        throw new Error("Not implemented");
    }

    @Override
    public Observable<VkUser> fetchFriend(int id) {
        return cachedDataManager
                .getFriend(id)
                .onErrorResumeNext(dataLoader
                        .loadUser(id)
                        .doOnNext(user -> cachedDataManager.saveFriend(user))
                );
    }

    @Override
    public Observable<VkUser> fetchOwner() {
        return cachedDataManager
                .getOwner()
                .onErrorResumeNext(dataLoader
                        .loadUser(Integer.parseInt(userId))
                        .doOnNext(owner -> cachedDataManager.saveOwner(owner))
                );
    }

    @Override
    public Observable<VkDialogs> fetchDialogs() {
        return cachedDataManager
                .getDialogs()
                .onErrorResumeNext(dataLoader
                        .loadDialogs()
                        .doOnNext(dialogsAndInterlocutors -> {
                            if (dialogsAndInterlocutors.first.size() != 0) {
                                cachedDataManager.saveDialogs(dialogsAndInterlocutors.first);
                                cachedDataManager.saveInterlocutors(dialogsAndInterlocutors.second);
                                hasMoreDialogsToUpdate = true;
                            } else hasMoreDialogsToUpdate = false;
                        })
                        .map(dialogsAndInterlocutors -> dialogsAndInterlocutors.first)
                );
    }

    @Override
    public Observable<VkDialog> fetchDialog(int id) {
        return cachedDataManager
                .getDialog(id)
                .onErrorResumeNext(dataLoader
                        .loadDialog(id)
                        .doOnNext(dialog -> cachedDataManager.saveDialog(dialog))
                );
    }

    @Override
    public Observable<VkInterlocutor> fetchInterlocutor(int id) {
        return cachedDataManager
                .getInterlocutor(id);
    }

    @Override
    public Observable<VkUser> fetchUser(int id) {
        return fetchFriend(id);
    }

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
}
