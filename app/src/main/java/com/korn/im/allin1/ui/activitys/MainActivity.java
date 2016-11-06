package com.korn.im.allin1.ui.activitys;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.korn.im.allin1.accounts.Account;
import com.korn.im.allin1.accounts.AccountManager;
import com.korn.im.allin1.R;
import com.korn.im.allin1.adapters.PagerAdapter;

public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener, Account.OnLogOutListener {
    private PagerAdapter pagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager viewPager;

    //-------------------- Lifecycle ---------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkLogIn();
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initToolbar();

        pagerAdapter = new PagerAdapter(this, getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(PagerAdapter.ITEM_COUNT);


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            AccountManager.getInstance().getVkAccount().logOut();
            startLoginActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //--------------------- Behavior ----------------------------

    private void checkLogIn() {
        if(!AccountManager.getInstance().hasLoggedInAccount())
            startLoginActivity();
    }

    private void startLoginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(this, R.layout.toolbar_spiner_item,
                new String[] {"Vk", "Facebook", "Odnoklasniki"});
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinner.setAdapter(arrayAdapter);
    }

    //--------------------- Interfaces ---------------------------
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int i = 5;
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        int i = 5;
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        int i = 5;
    }

    @Override
    public void onLoggedOut() {
        checkLogIn();
    }
}
