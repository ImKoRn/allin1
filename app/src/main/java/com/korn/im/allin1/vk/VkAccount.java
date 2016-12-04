package com.korn.im.allin1.vk;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.korn.im.allin1.accounts.Account;
import com.korn.im.allin1.accounts.AccountType;
import com.korn.im.allin1.accounts.Api;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;

import static com.korn.im.allin1.constants.DefaultItemsCount.DIALOGS_COUNT;
import static com.korn.im.allin1.constants.DefaultItemsCount.MESSAGES_COUNT;

public class VkAccount extends Account {
    private VkApi api;

    private VKAccessTokenTracker vkAccessTokenTracker = new VKAccessTokenTracker() {
        @Override
        public void onVKAccessTokenChanged(@Nullable VKAccessToken oldToken, @Nullable VKAccessToken newToken) {
            if(newToken == null) logOut();
        }
    };

    public VkAccount() {
        if(isLoggedIn()) initAccount(VKAccessToken.currentToken());
    }

    @Override
    public boolean isLoggedIn() {
        return VKSdk.isLoggedIn();
    }

    @Override
    public void logIn(Activity activity) {
        if(!isLoggedIn())
            VKSdk.login(activity, VKScope.FRIENDS, VKScope.MESSAGES);
    }

    @Override
    public void logOut() {
        VKSdk.logout();
        vkAccessTokenTracker.stopTracking();
    }


    public void initAccount(VKAccessToken token) {
        if(token == null) return;

        if(!vkAccessTokenTracker.isTracking())
            vkAccessTokenTracker.startTracking();
        this.api = new VkApi(DIALOGS_COUNT,
                             MESSAGES_COUNT);
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data, VKCallback<VKAccessToken> listener) {
        return VKSdk.onActivityResult(requestCode, resultCode, data, listener);
    }

    @Override
    public Api getApi() {
        return api;
    }

    @Override
    public AccountType getAccountType() {
        return AccountType.Vk;
    }
}
