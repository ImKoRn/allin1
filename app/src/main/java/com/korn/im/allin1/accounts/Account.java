package com.korn.im.allin1.accounts;

import android.app.Activity;

/**
 * Created by korn on 03.08.16.
 */
public abstract class Account {
    private OnLogOutListener listener;

    public abstract boolean isLoggedIn();

    public abstract void logIn(Activity activity);

    public void logOut() {
        if(listener != null) listener.onLoggedOut();

    }

    public abstract Api getApi();

    public abstract Events getEvents();

    public void removeOnLogOutListenerListener(OnLogOutListener listener) {
        if(this.listener == listener)
            this.listener = null;
    }

    public void setOnLogOutListenerListener(OnLogOutListener listener) {
        this.listener = listener;
    }

    public abstract DataManager getDataManager();

    public interface OnLogOutListener {
        void onLoggedOut();
    }
}
