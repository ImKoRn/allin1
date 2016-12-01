package com.korn.im.allin1.accounts;

import android.content.Context;

import com.korn.im.allin1.vk.VkAccount;
import com.vk.sdk.VKSdk;

import java.util.HashMap;
import java.util.Map;

public class AccountManager {
    // Constants
    private static final String NOT_INITIALIZED_ERROR = "Not initialized, call AccountManager.init() first";

    // Instance
    private static AccountManager instance = new AccountManager();

    // Members
    private HashMap<AccountType, Account> accounts = new HashMap<>();
    private Editor editor;
    private boolean initialized = false;

    public static AccountManager getInstance() {
        if (!instance.initialized) throw new IllegalStateException(NOT_INITIALIZED_ERROR);
        return instance;
    }

    private AccountManager() {}

    public static void init(Context context) {
        tryInitVk(context);
        instance.initialized = true;
    }

    private static void tryInitVk(Context context) {
        VKSdk.initialize(context);
        VkAccount vkAccount = new VkAccount();
        if (vkAccount.isLoggedIn()) instance.accounts.put(vkAccount.getAccountType(), vkAccount);
    }


    public Account getAccount(AccountType accountType) {
        Account account = accounts.get(accountType);
        if (account == null) return null;
        if (!account.isLoggedIn()) {
            accounts.remove(accountType);
            return null;
        }
        return account;
    }

    public boolean hasAccounts() {
        return accounts.size() != 0;
    }

    public Editor edit() {
        if (editor == null)
            editor = new Editor();
        return this.editor;
    }

    public class Editor {
        private Editor() {}

        public void addAccount(Account account) {
            accounts.put(account.getAccountType(), account);
        }

        public void closeAccount(AccountType accountType) {
            Account account = accounts.remove(accountType);
            if (account != null) account.logOut();
        }

        public void closeAllAccounts() {
            for (Map.Entry<AccountType, Account> accountEntry : accounts.entrySet())
                accountEntry.getValue()
                            .logOut();
            accounts.clear();
        }
    }
}
