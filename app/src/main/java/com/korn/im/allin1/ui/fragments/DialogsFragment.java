package com.korn.im.allin1.ui.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.korn.im.allin1.R;
import com.korn.im.allin1.accounts.AccountManager;
import com.korn.im.allin1.adapters.AdvancedAdapter;
import com.korn.im.allin1.adapters.DialogsAdapter;
import com.korn.im.allin1.common.MarginDecoration;

import rx.Subscription;

/**
 * A simple {@link Fragment} subclass.
 */
public class DialogsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String DIALOGS_MANAGER_STATE = "dialogs_llm_state";
    private SwipeRefreshLayout swipeRefreshDialogsLayout;
    private RecyclerView dialogsList;
    private DialogsAdapter dialogsAdapter;

    private Subscription dialogsSubscription;
    private Subscription newMessagesSubscription;
    private Subscription interlocutorsSubscription;

    private LinearLayoutManager llm;
    private Parcelable managerLastState;

    public DialogsFragment() {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            managerLastState = savedInstanceState.getParcelable(DIALOGS_MANAGER_STATE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_dialogs, container, false);
        swipeRefreshDialogsLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_dialogs);
        swipeRefreshDialogsLayout.setOnRefreshListener(this);
        swipeRefreshDialogsLayout.setColorSchemeColors(Color.MAGENTA);

        // Connect the recycler to the scroller (to let the scroller scroll the list)
        dialogsList = (RecyclerView) rootView.findViewById(R.id.dialogs_list);
        llm = new LinearLayoutManager(getContext()){
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        dialogsList.setAdapter(dialogsAdapter = new DialogsAdapter(getContext(),
                R.color.colorOnline, R.color.colorOffline, dialogsList, llm));

        dialogsList.setLayoutManager(llm);
        dialogsList.addItemDecoration(new MarginDecoration(getContext(), R.dimen.dialogsMargin, true));
/*
        dialogsAdapter.setOnNeedMoreListener(new AdvancedAdapter.OnNeedMoreListener() {
            @Override
            public void onNeedMore() {
                AccountManager.getInstance().getVkAccount().getApi().nextDialogs();
            }
        });*/

        dialogsAdapter.setItemLongClickListener(new AdvancedAdapter.OnItemClickListener() {
            @Override
            public void onClick(View v, int id) {
                PopupMenu popupMenu = new PopupMenu(getActivity(), v, Gravity.END);
                popupMenu.inflate(R.menu.menu_dialog_item);
                popupMenu.show();
            }
        });

        dialogsAdapter.setItemClickListener(new AdvancedAdapter.OnItemClickListener() {
            @Override
            public void onClick(View v, int id) {
                startDialogActivity(id);
            }
        });

        return rootView;
    }

    private void startDialogActivity(int id) {
        /*Intent intent = new Intent(getActivity(), DialogActivity.class);
        intent.putExtra(DialogActivity.CURRENT_DIALOG_ID, id);
        startActivity(intent);*/
    }

    @Override
    public void onResume() {
        super.onResume();

        subscribeOnData();

        /*interlocutorsSubscription = AccountManager.getInstance().getVkAccount().getEvents()
                .onFriendsStatusChangedEvent()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Set<Integer>>() {
                    @Override
                    public void call(Set<Integer> integers) {
                        dialogsAdapter.updateItems(integers);
                    }
                });
        newMessagesSubscription = AccountManager.getInstance().getVkAccount()
                .getEvents()
                .onNewMessagesArrivedEvent()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Set<Integer>>() {
                    @Override
                    public void call(Set<Integer> integers) {
                        managerLastState = llm.onSaveInstanceState();
                        dialogsAdapter.updateItems(integers);
                        llm.onRestoreInstanceState(managerLastState);
                        managerLastState = null;
                    }
                });

        AccountManager.getInstance().getVkAccount().getApi().cachedDialogs();*/
    }

    private void subscribeOnData() {
        /*dialogsSubscription = AccountManager.getInstance().getVkAccount().getEvents()
                .onDataEvents()
                .filter(new Func1<Event, Boolean>() {
                    @Override
                    public Boolean call(Event event) {
                        return event.getRequestDataType() == Event.REQUEST_DATA_TYPE_DIALOGS;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Event>() {
                    @Override
                    public void call(Event event) {
                        switch (event.getRequestType()) {
                            case Event.REQUEST_TYPE_UPDATE: {
                                managerLastState = llm.onSaveInstanceState();
                                dialogsAdapter.clear();
                                dialogsAdapter.setData(AccountManager.getInstance().getVkAccount().getDataManager().getDialogs());
                                llm.onRestoreInstanceState(managerLastState);
                                managerLastState = null;
                            }

                            case Event.REQUEST_TYPE_LOAD_NEXT: {
                                if (AccountManager.getInstance().getVkAccount().getDataManager().hasMoreDialogsToUpdate()) {
                                    dialogsAdapter.setHasMore(true);
                                    dialogsAdapter.setNotifyAboutLoading(true);
                                }
                                else dialogsAdapter.setHasMore(false);

                                dialogsAdapter.setData(AccountManager.getInstance().getVkAccount().getDataManager().getDialogs());
                            }

                            case Event.REQUEST_TYPE_CACHE: {
                                if (AccountManager.getInstance().getVkAccount().getDataManager().hasMoreDialogsToUpdate()) {
                                    dialogsAdapter.setHasMore(true);
                                    dialogsAdapter.setNotifyAboutLoading(true);
                                }
                                else dialogsAdapter.setHasMore(false);
                                dialogsAdapter.setData(AccountManager.getInstance().getVkAccount().getDataManager().getDialogs());
                                llm.onRestoreInstanceState(managerLastState);
                                managerLastState = null;
                            }
                        }
                        swipeRefreshDialogsLayout.setRefreshing(false);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        swipeRefreshDialogsLayout.setRefreshing(false);
                        subscribeOnData();
                    }
                });*/
    }

    @Override
    public void onPause() {
        super.onPause();
        if(dialogsSubscription != null)
            dialogsSubscription.unsubscribe();

        if(newMessagesSubscription != null)
            newMessagesSubscription.unsubscribe();

        if(interlocutorsSubscription != null)
            interlocutorsSubscription.unsubscribe();

        managerLastState = llm.onSaveInstanceState();
        dialogsAdapter.clear();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(DIALOGS_MANAGER_STATE, managerLastState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_dialogs, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onRefresh() {
        swipeRefreshDialogsLayout.setRefreshing(true);
        AccountManager.getInstance().getVkAccount().getApi()
                .fetchDialogs()
                .subscribe(vkDialogs -> {
                    dialogsAdapter.setData(vkDialogs);
                });
    }
}
