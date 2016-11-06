package com.korn.im.allin1.ui.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.korn.im.allin1.accounts.AccountManager;
import com.korn.im.allin1.R;
import com.korn.im.allin1.ui.controlers.VkLoginViewController;
import com.korn.im.allin1.pojo.User;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.api.VKError;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by korn on 03.08.16.
 */
public class LoginActivity extends AppCompatActivity implements VKCallback<VKAccessToken> {
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

        vkLoginViewController.getVkLogInOutBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AccountManager.getInstance().getVkAccount().isLoggedIn()) {
                    AccountManager.getInstance().getVkAccount()
                            .logOut();
                    vkLoginViewController.setUser(null);
                    vkLoginViewController.update();
                }
                else AccountManager.getInstance().getVkAccount()
                        .logIn(LoginActivity.this);
            }
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
        if(nextMenuItem != null)
            nextMenuItem.setVisible(AccountManager.getInstance().hasLoggedInAccount());
    }

    //------------------------ Requests response ------------------------

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(!AccountManager.getInstance().getVkAccount().onActivityResult(requestCode, resultCode, data, this))
            super.onActivityResult(requestCode, resultCode, data);
    }


    //------------------------ Vk response -------------------------------
    @Override
    public void onResult(VKAccessToken res) {
        res.save();
        AccountManager.getInstance().getVkAccount().initAccount(res);
        AccountManager.getInstance().getVkAccount().getApi().fetchOwner()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<User>() {
                    @Override
                    public void call(User user) {
                        vkLoginViewController.setUser(user);
                        vkLoginViewController.update();
                        syncLogInStatus();
                    }
                });
    }

    @Override
    public void onError(VKError error) {
        //TODO Fix this error check
        Snackbar.make(root, error.errorMessage, Snackbar.LENGTH_SHORT).show();
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
            nextMenuItem.setVisible(AccountManager.getInstance().hasLoggedInAccount());
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
