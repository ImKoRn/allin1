package com.korn.im.allin1.accounts;

import com.korn.im.allin1.pojo.Dialog;
import com.korn.im.allin1.pojo.Dialogs;
import com.korn.im.allin1.pojo.Interlocutor;
import com.korn.im.allin1.pojo.Message;
import com.korn.im.allin1.pojo.User;
import com.korn.im.allin1.vk.VkAccount;

public class AccountManager {
    private Account<
            ? extends Message,
            ? extends User,
            ? extends Dialogs,
            ? extends Dialog,
            ? extends Interlocutor> vkAccount = new VkAccount();
    private static AccountManager instance = new AccountManager();

    public static AccountManager getInstance() {
        return instance;
    }

    private AccountManager() {}

    public Account <
            ? extends Message,
            ? extends User,
            ? extends Dialogs,
            ? extends Dialog,
            ? extends Interlocutor>
    getAccount() {
        return vkAccount;
    }

    public void addAccount(Account<
            ? extends Message,
            ? extends User,
            ? extends Dialogs,
            ? extends Dialog,
            ? extends Interlocutor> account) {
        vkAccount = account;
    }

    public boolean hasAccounts() {
        return vkAccount != null;
    }
}
