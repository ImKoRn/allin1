package com.korn.im.allin1.accounts;

import android.app.Activity;

public abstract class Account {

    public abstract boolean isLoggedIn();

    public abstract void logIn(Activity activity);

    public abstract void logOut();

    public abstract Api getApi();

    public abstract AccountType getAccountType();
}
