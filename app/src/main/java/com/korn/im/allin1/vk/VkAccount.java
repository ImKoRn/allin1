/*
package com.korn.im.allin1.vk;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.korn.im.allin1.accounts.Account;
import com.korn.im.allin1.accounts.AccountManager;
import com.korn.im.allin1.accounts.Api;
import com.korn.im.allin1.accounts.DataManager;
import com.korn.im.allin1.accounts.Events;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;

public class VkAccount extends Account {
    public static final int ACCOUNT_TYPE = 1;

    private VkEngine vkEngine = null;
    private VKAccessTokenTracker vkAccessTokenTracker = new VKAccessTokenTracker() {
        @Override
        public void onVKAccessTokenChanged(@Nullable VKAccessToken oldToken, @Nullable VKAccessToken newToken) {
            AccountManager.getInstance().getVkAccount().initAccount(newToken);
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
        if (vkEngine != null) {
            vkEngine.closeApi();
            vkEngine = null;
        }
        super.logOut();
    }

    @Override
    public Api getApi() {
        return vkEngine;
    }

    @Override
    public Events getEvents() {
        return vkEngine;
    }

    @Override
    public DataManager getDataManager() {
        if (vkEngine == null)
            return null;
        else return vkEngine.getDataManager();
    }

    public void initAccount(VKAccessToken token) {
        if(token == null) return;

        if(!vkAccessTokenTracker.isTracking())
            vkAccessTokenTracker.startTracking();
        vkEngine = new VkEngine(token);
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data, VKCallback<VKAccessToken> listener) {
        return VKSdk.onActivityResult(requestCode, resultCode, data, listener);
    }

    public VkEngine getEngine() {
        return vkEngine;
    }
}
*/
