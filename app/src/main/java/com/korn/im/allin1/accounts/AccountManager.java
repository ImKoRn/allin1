package com.korn.im.allin1.accounts;

import android.content.Context;
import android.support.annotation.NonNull;

import com.korn.im.allin1.VkAccount;
import com.vk.sdk.VKSdk;

public class AccountManager {
    private VkAccount vkAccount;

    private static AccountManager instance;

    public static AccountManager getInstance() {
        if (instance == null)
            throw new IllegalStateException("Not initialized, use init()");
        return instance;
    }

    public static void init(@NonNull Context context) {
        instance = new AccountManager(context);
    }

    private AccountManager(Context context) {
        initApi(context);

        initAccounts();
    }

    private void initAccounts() {
        vkAccount = new VkAccount();
    }

    private void initApi(Context context) {
        VKSdk.initialize(context);
    }

    public boolean hasLoggedInAccount() {

        return vkAccount.isLoggedIn();
    }

    public VkAccount getVkAccount() {
        return vkAccount;
    }
}
