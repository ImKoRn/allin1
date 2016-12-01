package com.korn.im.allin1.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import com.korn.im.allin1.R;
import com.korn.im.allin1.accounts.AccountManager;
import com.korn.im.allin1.accounts.AccountType;
import com.korn.im.allin1.ui.controlers.VkLoginViewController;
import com.korn.im.allin1.vk.VkAccount;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.api.VKError;

import rx.android.schedulers.AndroidSchedulers;

public class LoginActivity extends AppCompatActivity {
    // Constants
    private static final String UNEXPECTED_ERROR = "Unexpected error occurred";
    private static final String SERVER_ERROR = "Can't connect to server, place check ethernet connection";
    private static final String INTERNAL_ERROR = "Internal error";
    private static final String API_ERROR = "Server error";

    // Members
    private VkLoginViewController vkLoginViewController;
    private VkAccount vkAccount;
    private ViewGroup root;
    private MenuItem nextMenuItem;

    //--------------------- Activity lifecycle --------------------------
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme); // Splash screen

        createTemplateAccounts();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        root = (ViewGroup) findViewById(R.id.root);

        vkLoginViewController = new VkLoginViewController(this);
        vkLoginViewController.create();

        vkLoginViewController.getVkLogInOutBtn().setOnClickListener(view -> {
            if (vkAccount.isLoggedIn()) {
                vkAccount.logOut();
                vkLoginViewController.setUser(null);
                vkLoginViewController.update();
            }
            else vkAccount.logIn(LoginActivity.this);
        });
    }

    private void createTemplateAccounts() {
        VkAccount vkAccount = (VkAccount) AccountManager.getInstance().getAccount(AccountType.Vk);
        if (vkAccount == null) vkAccount = new VkAccount();

        this.vkAccount = vkAccount;
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
        if(nextMenuItem != null) nextMenuItem.setVisible(vkAccount.isLoggedIn());
    }

    //------------------------ Requests response ------------------------

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(!(vkAccount.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                res.save();
                vkAccount.initAccount(res);
                vkAccount.getApi()
                         .loadInterlocutor(Integer.parseInt(res.userId))
                         .observeOn(AndroidSchedulers.mainThread())
                         .subscribe(user -> {
                                        vkLoginViewController.setUser(user);
                                        vkLoginViewController.update();
                                        syncLogInStatus();
                                    },
                                    throwable -> Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show());
                AccountManager.getInstance()
                              .edit()
                              .addAccount(vkAccount);
            }

            @Override
            public void onError(VKError error) {
                String errMsg;
                switch (error.errorCode) {
                    case VKError.VK_CANCELED : {
                        errMsg = null;
                        break;
                    }
                    case VKError.VK_REQUEST_HTTP_FAILED : {
                        errMsg = SERVER_ERROR;
                        break;
                    }
                    case VKError.VK_JSON_FAILED : {
                        errMsg = INTERNAL_ERROR;
                        break;
                    }
                    case VKError.VK_API_ERROR : {
                        errMsg = API_ERROR;
                        break;
                    }
                    default: errMsg = UNEXPECTED_ERROR;
                }
                if (errMsg != null) Snackbar.make(root, error.errorMessage, Snackbar.LENGTH_SHORT).show();
            }
        }))) super.onActivityResult(requestCode, resultCode, data);
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
