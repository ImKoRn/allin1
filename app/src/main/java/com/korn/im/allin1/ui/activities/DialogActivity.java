package com.korn.im.allin1.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.korn.im.allin1.R;
import com.korn.im.allin1.accounts.AccountManager;
import com.korn.im.allin1.accounts.AccountType;
import com.korn.im.allin1.accounts.Api;
import com.korn.im.allin1.adapters.MessagesAdapter;
import com.korn.im.allin1.pojo.Interlocutor;
import com.korn.im.allin1.pojo.Message;
import com.korn.im.allin1.ui.customview.SocialCircularImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Map;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class DialogActivity extends AppCompatActivity {
    public static final String CURRENT_DIALOG_ID = "curDialogId";

    // Subscriptions
    private Subscription messagesLoadSubscriptions;

    // Ui
    private SocialCircularImageView interlocutorIcon;
    private TextView interlocutorName;

    // Members
    private int interlocutorId;
    private Interlocutor interlocutor;
    private Map<Integer, ? extends Message> messages;
    private LinearLayoutManager llm;
    private MessagesAdapter messagesAdapter;
    private ProgressBar progressBar;
    private RecyclerView listOfMessages;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);

        interlocutorId = getIntent().getIntExtra(CURRENT_DIALOG_ID, -1);

        setContentView(R.layout.activity_dialog);

        setSupportActionBar((android.support.v7.widget.Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        interlocutorIcon = (SocialCircularImageView) findViewById(R.id.interlocutorIcon);
        interlocutorName = (TextView) findViewById(R.id.interlocutorName);

        listOfMessages = (RecyclerView) findViewById(R.id.messages_list);
        listOfMessages.setLayoutManager(llm = new LinearLayoutManager(this));
        listOfMessages.setAdapter(messagesAdapter = new MessagesAdapter(this, listOfMessages, llm));
        messagesAdapter.setOnNeedMoreListener(() -> {
            switch (loadMessages(Api.Request.LOAD)) {
                case LOADING : {
                    messagesAdapter.setNotifyAboutLoading(false);
                    break;
                }
                case NOTHING_TO_LOAD : {
                    messagesAdapter.setCanLoadData(false);
                    break;
                }
                case BUSY : {
                    messagesAdapter.setNotifyAboutLoading(false);
                }
            }
        });
        llm.setReverseLayout(true);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null)
            interlocutorId = savedInstanceState.getInt(CURRENT_DIALOG_ID, -1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        messagesLoadSubscriptions = subscribeOnMessagesUpdate();
        fetchInterlocutor();
        fetchMessages();
    }

    private Subscription subscribeOnMessagesUpdate() {
        return AccountManager.getInstance()
                             .getAccount(AccountType.Vk)
                             .getApi()
                             .getEventsManager()
                             .messagesObservable()
                             .subscribeOn(Schedulers.computation())
                             .observeOn(AndroidSchedulers.mainThread())
                             .subscribe(messages -> {
                                 DialogActivity.this.messages = messages.second;
                                 showMessages();
                                 messagesAdapter.setCanLoadData(canLoadMessages());
                                 messagesAdapter.setNotifyAboutLoading(true);
                             });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (messagesLoadSubscriptions != null) messagesLoadSubscriptions.unsubscribe();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null)
            outState.putInt(CURRENT_DIALOG_ID, interlocutorId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home : {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchInterlocutor() {
        AccountManager.getInstance()
                      .getAccount(AccountType.Vk)
                      .getApi()
                      .fetchInterlocutor(interlocutorId)
                      .subscribe((Action1<Interlocutor>) interlocutor -> {
                          this.interlocutor = interlocutor;
                          showInterlocutor();
                      });
    }

    private void fetchMessages() {
        AccountManager.getInstance()
                      .getAccount(AccountType.Vk)
                      .getApi()
                      .fetchMessages(interlocutorId)
                      .subscribe(messages -> {
                                     this.messages = messages;
                                     showMessages();
                                     messagesAdapter.setCanLoadData(canLoadMessages());
                                     messagesAdapter.setNotifyAboutLoading(true);
                                 },
                                 throwable -> loadMessages(Api.Request.LOAD_FIRST));
    }

    private boolean canLoadMessages() {
        return AccountManager.getInstance()
                             .getAccount(AccountType.Vk)
                             .getApi()
                             .canLoadMessages(interlocutorId);
    }

    private Api.Response loadMessages(Api.Request request) {
        return AccountManager.getInstance()
                             .getAccount(AccountType.Vk)
                             .getApi()
                             .loadMessages(interlocutorId, request);
    }

    private void showMessages() {
        progressBar.setVisibility(View.GONE);
        listOfMessages.setVisibility(View.VISIBLE);
        messagesAdapter.setData(messages);
    }

    private void showInterlocutor() {
        interlocutorIcon.setShowOnlineMark(interlocutor.isOnline(), interlocutor.isOnlineMobile());
        ImageLoader.getInstance().displayImage(interlocutor.getMediumImage(), interlocutorIcon);
        interlocutorName.setText(interlocutor.getFullName());
    }
}
