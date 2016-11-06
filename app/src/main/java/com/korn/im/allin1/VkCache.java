package com.korn.im.allin1;

import android.annotation.SuppressLint;

import com.korn.im.allin1.accounts.newaccount.Cache;
import com.korn.im.allin1.vk.pojo.SynchronizedVkDialogs;
import com.korn.im.allin1.vk.pojo.VkDialog;
import com.korn.im.allin1.vk.pojo.VkInterlocutor;
import com.korn.im.allin1.vk.pojo.VkUser;
import com.korn.im.allin1.vk.pojo.newvkpojo.VkDialogs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VkCache implements Cache<VkUser, VkDialogs, VkDialog, VkInterlocutor> {
    private final Object friendsLock = new Object();
    private Map<Integer, VkUser> friends;
    private SynchronizedVkDialogs dialogs;
    private Map<Integer, VkInterlocutor> interlocutors;

    @SuppressLint("UseSparseArrays")
    public VkCache() {
        /*this.friends = Collections.synchronizedMap(new LinkedHashMap<Integer, VkUser>());
        this.dialogs = new SynchronizedVkDialogs();
        this.interlocutors = Collections.synchronizedMap(new LinkedHashMap<Integer, VkInterlocutor>());*/
    }

    @Override
    public VkUser getOwner() {
        return null;
    }

    @Override
    public void saveOwner(VkUser owner) {

    }

    @Override
    public List<VkUser> getFriends() {
        if (friends == null)
            return null;

        List<VkUser> list;
        synchronized (friendsLock) {
            list = new ArrayList<>(friends.values());
        }

        return list;
    }

    @Override
    public void saveFriends(List<VkUser> friends) {
        if (this.friends == null)
            this.friends = Collections.synchronizedMap(new LinkedHashMap<Integer, VkUser>());
        synchronized (friendsLock) {
            for (VkUser friend : friends) this.friends.put(friend.getId(), friend);
        }
    }

    @Override
    public VkUser getFriend(int id) {
        return friends.get(id);
    }

    @Override
    public void saveFriend(VkUser user) {
        friends.put(user.getId(), user);
    }

    @Override
    public VkDialogs getDialogs() {
        if (dialogs == null)
            return null;

        VkDialogs dialogs;
        synchronized (this.dialogs) {
            dialogs = new VkDialogs(this.dialogs);
        }
        return dialogs;
    }

    @Override
    public void saveDialogs(VkDialogs dialogs) {
        this.dialogs.addDialogs(dialogs.getDialogs());
    }

    @Override
    public VkDialog getDialog(int id) {
        return dialogs.getDialog(id);
    }

    @Override
    public void saveDialog(VkDialog dialog) {
        dialogs.addDialog(dialog);
    }

    @Override
    public VkInterlocutor getInterlocutor(int id) {
        return interlocutors.get(id);
    }

    @Override
    public void saveInterlocutor(VkInterlocutor interlocutor) {
        interlocutors.put(interlocutor.getId(), interlocutor);
    }

    @Override
    public void saveInterlocutors(List<VkInterlocutor> interlocutors) {
        synchronized (this.interlocutors) {
            for (VkInterlocutor interlocutor : interlocutors) {
                this.interlocutors.put(interlocutor.getId(), interlocutor);
            }
        }
    }
}
