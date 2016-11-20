package com.korn.im.allin1.ui.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.korn.im.allin1.R;
import com.korn.im.allin1.accounts.AccountManager;
import com.korn.im.allin1.ui.controlers.VkLoginViewController;
import com.korn.im.allin1.vk.VkAccount;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.api.VKError;

import rx.android.schedulers.AndroidSchedulers;

public class LoginActivity extends AppCompatActivity {
    private VkLoginViewController vkLoginViewController;

    private ViewGroup root;
    private MenuItem nextMenuItem;

    //--------------------- Activity lifecycle --------------------------
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        root = (ViewGroup) findViewById(R.id.root);

        vkLoginViewController = new VkLoginViewController(this);
        vkLoginViewController.create();

        vkLoginViewController.getVkLogInOutBtn().setOnClickListener(view -> {

            if (AccountManager.getInstance().getAccount().isLoggedIn()) {
                AccountManager.getInstance().getAccount().logOut();
                vkLoginViewController.setUser(null);
                vkLoginViewController.update();
            }
            else AccountManager.getInstance().getAccount().logIn(LoginActivity.this);
        });
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        vkLoginViewController.restoreInstance(savedInstanceState);
        vkLoginViewController.update();
    }

    @Override
    protected void onResume() {
        super.onResume();
        vkLoginViewController.prepare();
        syncLogInStatus();
    }

    @Override
    protected void onPause() {
        super.onPause();
        vkLoginViewController.release();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        vkLoginViewController.saveInstance(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        vkLoginViewController.destroy();
    }

    //------------------------ Log in status    -------------------------

    private void syncLogInStatus() {
        if(nextMenuItem != null) nextMenuItem.setVisible(AccountManager.getInstance().getAccount().isLoggedIn());
    }

    //------------------------ Requests response ------------------------

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(!((VkAccount) AccountManager.getInstance().getAccount()).onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                res.save();
                ((VkAccount) AccountManager.getInstance().getAccount()).initAccount(res);
                AccountManager.getInstance().getAccount().getApi().fetchInterlocutor(Integer.parseInt(res.userId))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(user -> {
                            vkLoginViewController.setUser(user);
                            vkLoginViewController.update();
                            syncLogInStatus();
                        });
            }

            @Override
            public void onError(VKError error) {
                //TODO Fix this error check
                Snackbar.make(root, error.errorMessage, Snackbar.LENGTH_SHORT).show();
            }
        })) super.onActivityResult(requestCode, resultCode, data);
    }


    //------------------------- Options menu ------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login_screen, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        nextMenuItem = menu.findItem(R.id.nextLoginMenuBtn);
        if(nextMenuItem != null)
            nextMenuItem.setVisible(AccountManager.getInstance().hasAccounts());
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nextLoginMenuBtn : {
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
