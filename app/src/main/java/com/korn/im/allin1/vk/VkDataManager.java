/*
package com.korn.im.allin1.vk;

import com.korn.im.allin1.accounts.DataManager;
import com.korn.im.allin1.common.Owner;
import com.korn.im.allin1.pojo.Dialog;
import com.korn.im.allin1.pojo.Dialogs;
import com.korn.im.allin1.pojo.User;
import com.korn.im.allin1.vk.pojo.SynchronizedVkDialogs;
import com.korn.im.allin1.vk.pojo.VkDialog;
import com.korn.im.allin1.vk.pojo.VkDialogs;
import com.korn.im.allin1.vk.pojo.VkUser;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

*/
/**
 * Created by korn on 04.09.16.
 *//*

@SuppressWarnings("unchecked")
public class VkDataManager implements DataManager {
    //Dialogs container
    private final SynchronizedVkDialogs dialogsData = new SynchronizedVkDialogs();

    //User friends containers
    private final Map<Integer, VkUser> friendsDataMap = Collections.synchronizedMap(new HashMap<Integer, VkUser>());

    private final Owner owner = new Owner();

    private volatile boolean haveMoreFriendsToUpdate = true;
    private volatile boolean haveMoreDialogsToUpdate = true;

    //Friends
    @Override
    public Collection<VkUser> getFriends() {
        return friendsDataMap.values();
    }

    @Override
    public VkUser getFriend(int id) {
        return friendsDataMap.get(id);
    }

    public void addFriends(Collection<? extends VkUser> collection, boolean overwrite) {
        synchronized (friendsDataMap) {
            if(overwrite) friendsDataMap.clear();

            int pop = friendsDataMap.size();
            for (VkUser user : collection) {
                friendsDataMap.put(user.getId(), user);
                user.setPopIndex(++pop);
            }
        }
    }

    //Dialogs
    @Override
    public SynchronizedVkDialogs getDialogs() {
        return dialogsData;
    }

    @Override
    public VkDialog getDialog(int id) {
        return dialogsData.getDialog(id);
    }

    public void addDialogs(VkDialogs dialogs, boolean rewrite) {
        synchronized (dialogsData) {
            dialogsData.setSize(dialogs.size());
            dialogsData.setUnreadDialogsCount(dialogs.getUnreadDialogsCount());

            if(rewrite) {
                dialogsData.getDialogs().clear();
                dialogsData.getInterlocutors().clear();
            }

            dialogsData.addDialogs(dialogs.getDialogs());
            dialogsData.addInterlocutors(dialogs.getInterlocutors());
        }
    }

    public void addDialog(VkDialog newDialog) {
        dialogsData.addDialog(newDialog);
    }

    //Owner
    public void setOwnerInfo(User newOwner) {
        owner.updateInfo(newOwner);
    }

    @Override
    public boolean hasMoreDialogsToUpdate() {
        return haveMoreDialogsToUpdate;
    }

    @Override
    public boolean hasMoreFriendsToUpdate() {
        return haveMoreFriendsToUpdate;
    }

    @Override
    public boolean hasMoreMessagesToUpdate(int dialogId) {
        return dialogsData.getDialog(dialogId).isHasNextMessages();
    }

    public void setHaveMoreFriendsToUpdate(boolean haveMoreFriendsToUpdate) {
        this.haveMoreFriendsToUpdate = haveMoreFriendsToUpdate;
    }

    public User getOwnerInfo() {
        return owner;
    }

    public void setHaveMoreDialogsToUpdate(boolean haveMoreDialogsToUpdate) {
        this.haveMoreDialogsToUpdate = haveMoreDialogsToUpdate;
    }
}
*/
