package com.korn.im.allin1;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.korn.im.allin1.accounts.newaccount.Account;
import com.korn.im.allin1.accounts.newaccount.Api;
import com.korn.im.allin1.vk.pojo.VkDialog;
import com.korn.im.allin1.vk.pojo.VkInterlocutor;
import com.korn.im.allin1.vk.pojo.VkUser;
import com.korn.im.allin1.vk.pojo.newvkpojo.VkDialogs;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;

public class VkAccount implements Account<VkUser, VkDialogs, VkDialog, VkInterlocutor> {
    private VkApi api;

    public static final int ACCOUNT_TYPE = 1;

    private VKAccessTokenTracker vkAccessTokenTracker = new VKAccessTokenTracker() {
        @Override
        public void onVKAccessTokenChanged(@Nullable VKAccessToken oldToken, @Nullable VKAccessToken newToken) {
            if(newToken == null)
                logOut();
        }
    };

    public VkAccount() {
        if(isLoggedIn())
            initAccount(VKAccessToken.currentToken());
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
        this.api = new VkApi(token.userId);
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data, VKCallback<VKAccessToken> listener) {
        return VKSdk.onActivityResult(requestCode, resultCode, data, listener);
    }

    @Override
    public Api<VkUser, VkDialogs, VkDialog, VkInterlocutor> getApi() {
        return api;
    }
}
