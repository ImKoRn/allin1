package com.korn.im.allin1.vk;

import com.korn.im.allin1.accounts.DataManager;
import com.korn.im.allin1.pojo.Interlocutor;
import com.korn.im.allin1.vk.pojo.VkDialog;
import com.korn.im.allin1.vk.pojo.VkDialogs;
import com.korn.im.allin1.vk.pojo.VkMessage;
import com.korn.im.allin1.vk.pojo.VkUser;

class VkDataManager extends DataManager
        <VkMessage, VkUser, VkDialogs, VkDialog, Interlocutor, VkCache, VkDbCache> {
    VkDataManager(VkCache cache,
                  VkDbCache dbManager) {
        super(cache,
              dbManager);
    }

    int getDialogsStamp() {
        return getCache().getDialogsStamp();
    }

    int getMessagesStamp(int id) {
        return getCache().getMessagesStamp(id);
    }

    public int getDialogsCount() {
        return getCache().getDialogsCount();
    }

    public int getMessagesCount(int id) {
        return getCache().getMessagesCount(id);
    }
}
