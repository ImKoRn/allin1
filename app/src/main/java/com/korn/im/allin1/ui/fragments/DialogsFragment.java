package com.korn.im.allin1.ui.fragments;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.korn.im.allin1.R;
import com.korn.im.allin1.accounts.AccountManager;
import com.korn.im.allin1.accounts.AccountType;
import com.korn.im.allin1.adapters.DialogsAdapter;
import com.korn.im.allin1.common.MarginDecoration;
import com.korn.im.allin1.pojo.Dialogs;
import com.korn.im.allin1.pojo.Interlocutor;
import com.korn.im.allin1.pojo.User;
import com.korn.im.allin1.ui.activities.DialogActivity;

import java.util.Collection;
import java.util.Map;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class DialogsFragment extends Fragment {
    // Constants
    private static final String DIALOGS_MANAGER_STATE = "dialogs_llm_state";

    // Ui
    private SwipeRefreshLayout swipeRefreshDialogsLayout;

    // Subscriptions
    private Subscription dialogsUpdateSubscription;
    private Subscription dialogsErrorSubscription;
    private Subscription friendsUpdateSubscription;

    // Members
    private LinearLayoutManager llm;
    private Parcelable managerLastState;
    private DialogsAdapter dialogsAdapter;

    public DialogsFragment() {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null)
            managerLastState = savedInstanceState.getParcelable(DIALOGS_MANAGER_STATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_dialogs, container, false);
        swipeRefreshDialogsLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_dialogs);
        swipeRefreshDialogsLayout.setOnRefreshListener(() -> {
            swipeRefreshDialogsLayout.setRefreshing(true);
            managerLastState = llm.onSaveInstanceState();
            AccountManager.getInstance()
                          .getAccount(AccountType.Vk)
                          .getApi()
                          .loadDialogs();
        });
        swipeRefreshDialogsLayout.setColorSchemeColors(Color.MAGENTA);

        // Connect the recycler to the scroller (to let the scroller scroll the list)
        RecyclerView dialogsList = (RecyclerView) rootView.findViewById(R.id.dialogs_list);
        llm = new LinearLayoutManager(getContext()){
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        dialogsList.setAdapter(dialogsAdapter = new DialogsAdapter(getContext(),
                                                                   dialogsList,
                                                                   llm));

        dialogsList.setLayoutManager(llm);
        dialogsList.addItemDecoration(new MarginDecoration(getContext(), R.dimen.dialogsMargin, true));
/*
        dialogsAdapter.setOnNeedMoreListener(new AdvancedAdapter.OnNeedMoreListener() {
            @Override
            public void onNeedMore() {
                AccountManager.getInstance().getVkAccount().getApi().nextDialogs();
            }
        });*/

        dialogsAdapter.setItemLongClickListener((v, id) -> {
            PopupMenu popupMenu = new PopupMenu(getActivity(), v, Gravity.END);
            popupMenu.inflate(R.menu.menu_dialog_item);
            popupMenu.show();
        });

        dialogsAdapter.setItemClickListener((v, id) -> startDialogActivity(id));

        return rootView;
    }

    private void startDialogActivity(int id) {
        Intent intent = new Intent(getActivity(), DialogActivity.class);
        intent.putExtra(DialogActivity.CURRENT_DIALOG_ID, id);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        dialogsUpdateSubscription = subscribeOnDialogsUpdate();
        dialogsErrorSubscription = subscribeOnDialogsError();
        friendsUpdateSubscription = subscribeOnFriendsUpdate();
        fetchDialogs();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (dialogsUpdateSubscription != null) dialogsUpdateSubscription.unsubscribe();
        if (dialogsErrorSubscription != null) dialogsErrorSubscription.unsubscribe();
        if (friendsUpdateSubscription != null) friendsUpdateSubscription.unsubscribe();
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

    private void fetchDialogs() {
        AccountManager.getInstance()
                      .getAccount(AccountType.Vk)
                      .getApi()
                      .fetchDialogs()
                      .subscribeOn(Schedulers.computation())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe((Action1<Pair<? extends Dialogs, ? extends Map<Integer, ? extends Interlocutor>>>) pair -> {
                                     dialogsAdapter.setData(pair.first, pair.second);
                                     llm.onRestoreInstanceState(managerLastState);
                                     managerLastState = null;
                                 },
                                 throwable -> Toast.makeText(DialogsFragment.this.getActivity(),
                                                             throwable.getMessage(),
                                                             Toast.LENGTH_SHORT).show());
}

    private Subscription subscribeOnDialogsUpdate() {
        return AccountManager.getInstance()
                             .getAccount(AccountType.Vk)
                             .getApi()
                             .getDataPublisher()
                             .dialogsObservable()
                             .subscribeOn(Schedulers.computation())
                             .observeOn(AndroidSchedulers.mainThread())
                             .subscribe(pair -> {
                                            dialogsAdapter.setData(pair.first, pair.second);
                                            swipeRefreshDialogsLayout.setRefreshing(false);
                                            llm.onRestoreInstanceState(managerLastState);
                                        },
                                        throwable -> Toast.makeText(DialogsFragment.this.getActivity(),
                                                                    throwable.getMessage(),
                                                                    Toast.LENGTH_SHORT).show());
    }

    private Subscription subscribeOnFriendsUpdate() {
        return AccountManager.getInstance()
                             .getAccount(AccountType.Vk)
                             .getApi()
                             .getDataPublisher()
                             .friendsObservable()
                             .flatMap((Func1<Map<Integer, ? extends User>, Observable<Collection<? extends User>>>)
                                              friends -> Observable.just(friends.values()))
                             .subscribeOn(Schedulers.computation())
                             .observeOn(AndroidSchedulers.mainThread())
                             .subscribe(users -> {
                                            fetchDialogs();
                                        },
                                        throwable -> Toast.makeText(DialogsFragment.this.getActivity(),
                                                                    throwable.getMessage(),
                                                                    Toast.LENGTH_SHORT).show());
    }

    private Subscription subscribeOnDialogsError() {
        return AccountManager.getInstance()
                             .getAccount(AccountType.Vk)
                             .getApi()
                             .getDataPublisher()
                             .dialogsErrorsObservable()
                             .subscribe(throwable -> {
                                            swipeRefreshDialogsLayout.setRefreshing(false);
                                        },
                                        throwable -> {});
    }
}
