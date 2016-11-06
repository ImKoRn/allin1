/*
package com.korn.im.allin1.ui.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.korn.im.allin1.R;
import com.korn.im.allin1.accounts.AccountManager;
import com.korn.im.allin1.accounts.Event;
import com.korn.im.allin1.adapters.AdvancedAdapter;
import com.korn.im.allin1.adapters.MessagesAdapter;
import com.korn.im.allin1.common.MarginDecoration;
import com.korn.im.allin1.pojo.Interlocutor;
import com.korn.im.allin1.ui.controlers.SendMessageController;
import com.korn.im.allin1.ui.customview.SocialCircularImageView;
import com.korn.im.allin1.vk.VkRequestUtil;
import com.korn.im.allin1.vk.pojo.VkMessage;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;
import java.util.Map;
import java.util.Set;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

*/
/**
 * Created by korn on 03.09.16.
 *//*

public class DialogActivity extends AppCompatActivity {
    public final static String CURRENT_DIALOG_ID = "dialog_id";

    private SendMessageController sendMessageController = new SendMessageController(this);
    private RecyclerView messagesList;
    private MessagesAdapter messagesAdapter;
    private LinearLayoutManager llm;

    private Subscription newMessageSubscription;
    private Subscription typingSubscription;
    private Subscription messagesSubscription;
    private Subscription messagesFlagsUpdateSubscription;

    private SocialCircularImageView interlocutorIcon;
    private TextView interlocutorName;

    private int currentDialogId;
    private Interlocutor interlocutor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);

        if(!AccountManager.getInstance().hasLoggedInAccount())
            startLoginActivity();

        setContentView(R.layout.activity_dialog);
        sendMessageController.create();

        initToolbar();

        messagesList = (RecyclerView) findViewById(R.id.messages_list);
        messagesList.addItemDecoration(new MarginDecoration(this, R.dimen.messages_space, false));
        llm = new LinearLayoutManager(this);
        llm.setReverseLayout(true);
        messagesList.setLayoutManager(llm);
        messagesList.setAdapter(messagesAdapter = new MessagesAdapter(this, messagesList, llm));
        */
/*messagesAdapter.setOnNeedMoreListener(new AdvancedAdapter.OnNeedMoreListener() {
            @Override
            public void onNeedMore() {
                AccountManager.getInstance().getVkAccount().getApi().nextMessages(interlocutor.getId());
            }
        });*//*


        currentDialogId = getIntent().getIntExtra(CURRENT_DIALOG_ID, -1);
        interlocutorIcon = (SocialCircularImageView) findViewById(R.id.interlocutorIcon);
        interlocutorName = (TextView) findViewById(R.id.interlocutorName);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        sendMessageController.saveInstance(outState);
        outState.putInt(CURRENT_DIALOG_ID, currentDialogId);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        sendMessageController.restoreInstance(savedInstanceState);
        currentDialogId = savedInstanceState.getInt(CURRENT_DIALOG_ID, -1);
    }

    private void bindDialogWithData() {

        interlocutor = AccountManager.getInstance().getVkAccount()
                .getDataManager().getDialogs().getInterlocutor(AccountManager.getInstance().getVkAccount()
                        .getDataManager().getDialog(currentDialogId).getId());
        ImageLoader.getInstance().displayImage(interlocutor.getMediumImage(), interlocutorIcon);
        interlocutorIcon.setShowOnlineMark(interlocutor.isOnline(), interlocutor.isOnlineMobile());
        interlocutorName.setText(interlocutor.getFullName());

        messagesAdapter.setHasMore(AccountManager.getInstance().getVkAccount()
                .getDataManager().getDialog(currentDialogId).isHasNextMessages());
        messagesAdapter.setNotifyAboutLoading(true);
        if(AccountManager.getInstance().getVkAccount()
                .getDataManager()
                .getDialog(currentDialogId)
                .getMessages().size() < VkRequestUtil.DEFAULT_MESSAGES_COUNT) {
            AccountManager.getInstance().getVkAccount().getApi().nextMessages(interlocutor.getId());
        }
        else synchronized (AccountManager.getInstance().getVkAccount().getDataManager().getDialogs()) {
            messagesAdapter.setData(AccountManager.getInstance().getVkAccount()
                    .getDataManager().getDialog(currentDialogId).getMessages());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dialog, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sendMessageController.prepare();
        sendMessageController.setOnMessageListener(new SendMessageController.OnMessageListener() {
            @Override
            public void onMessage(String message) {
                sendMessageController.clear();
                AccountManager.getInstance().getVkAccount().getApi()
                        .sendMessage(interlocutor.getId(), new VkMessage(message));
            }
        });

        newMessageSubscription = AccountManager.getInstance().getVkAccount().getEvents()
                .onNewMessagesArrivedEvent()
                .filter(new Func1<Set<Integer>, Boolean>() {
                    @Override
                    public Boolean call(Set<Integer> integers) {
                        return integers.contains(interlocutor.getId());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Set<Integer>>() {
                    @Override
                    public void call(Set<Integer> integers) {
                        synchronized (AccountManager.getInstance().getVkAccount()
                                .getDataManager().getDialogs()) {
                            messagesAdapter.setData(AccountManager.getInstance().getVkAccount()
                                    .getDataManager().getDialog(currentDialogId).getMessages());
                        }
                        if(llm.findFirstVisibleItemPosition() != 0)
                            messagesList.smoothScrollToPosition(0);
                        else messagesList.scrollToPosition(0);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.getMessage();
                    }
                });

        messagesSubscription = AccountManager.getInstance().getVkAccount().getEvents()
                .onDataEvents()
                .filter(new Func1<Event, Boolean>() {
                    @Override
                    public Boolean call(Event event) {
                        return event.getRequestDataType() == Event.REQUEST_DATA_TYPE_MESSAGES &&
                                event.getAttachedId() == interlocutor.getId();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Event>() {
                    @Override
                    public void call(Event event) {
                        switch (event.getRequestType()) {
                            case Event.REQUEST_TYPE_LOAD_NEXT: {
                                if (AccountManager.getInstance().getVkAccount().getDataManager()
                                        .hasMoreMessagesToUpdate(interlocutor.getId())) {
                                    messagesAdapter.setHasMore(true);
                                    messagesAdapter.setNotifyAboutLoading(true);
                                }
                                else messagesAdapter.setHasMore(false);
                                if(messagesAdapter.getActualItemCount() == 0)
                                    messagesList.scrollToPosition(0);
                                synchronized (AccountManager.getInstance().getVkAccount()
                                        .getDataManager().getDialogs()) {
                                    messagesAdapter.setData(AccountManager.getInstance().getVkAccount()
                                            .getDataManager().getDialog(currentDialogId).getMessages());
                                }
                                break;
                            }
                            case Event.REQUEST_TYPE_UPDATE: {
                                synchronized (AccountManager.getInstance().getVkAccount()
                                        .getDataManager().getDialogs()) {
                                    messagesAdapter.setData(AccountManager.getInstance().getVkAccount()
                                            .getDataManager().getDialog(currentDialogId).getMessages());
                                }
                                break;
                            }
                            case Event.REQUEST_TYPE_CACHE: {
                                synchronized (AccountManager.getInstance().getVkAccount()
                                        .getDataManager().getDialogs()) {
                                    messagesAdapter.setData(AccountManager.getInstance().getVkAccount()
                                            .getDataManager().getDialog(currentDialogId).getMessages());
                                }
                                break;
                            }
                        }
                    }
                });


        messagesFlagsUpdateSubscription = AccountManager.getInstance().getVkAccount().getEvents()
                .onMessagesFlagsChangedEvent()
                .filter(new Func1<Map<Integer, List<Pair<Integer, Integer>>>, Boolean>() {
                    @Override
                    public Boolean call(Map<Integer, List<Pair<Integer, Integer>>> integerListMap) {
                        return integerListMap.keySet().contains(interlocutor.getId());
                    }
                })
                .flatMap(new Func1<Map<Integer, List<Pair<Integer, Integer>>>, Observable<List<Pair<Integer, Integer>>>>() {
                    @Override
                    public Observable<List<Pair<Integer, Integer>>>
                        call(Map<Integer, List<Pair<Integer, Integer>>> integerListMap) {
                        return Observable.just(integerListMap.get(interlocutor.getId()));
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Pair<Integer, Integer>>>() {
                    @Override
                    public void call(List<Pair<Integer, Integer>> pairs) {
                        messagesAdapter.clear();
                        synchronized (AccountManager.getInstance().getVkAccount()
                                .getDataManager().getDialogs()) {
                            messagesAdapter.setData(AccountManager.getInstance().getVkAccount()
                                    .getDataManager().getDialog(currentDialogId).getMessages());
                        }
                    }
                });

        typingSubscription = AccountManager.getInstance().getVkAccount().getEvents()
                .onTypingEvent()
                .filter(new Func1<Set<Integer>, Boolean>() {
                    @Override
                    public Boolean call(Set<Integer> integers) {
                        return integers.contains(currentDialogId);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Set<Integer>>() {
                    @Override
                    public void call(Set<Integer> integers) {

                    }
                });

        bindDialogWithData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sendMessageController.release();

        if (typingSubscription != null)
            typingSubscription.unsubscribe();

        if (newMessageSubscription != null)
            newMessageSubscription.unsubscribe();

        if (messagesSubscription != null)
            messagesSubscription.unsubscribe();

        if (messagesFlagsUpdateSubscription != null)
            messagesFlagsUpdateSubscription.unsubscribe();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void startLoginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
*/
