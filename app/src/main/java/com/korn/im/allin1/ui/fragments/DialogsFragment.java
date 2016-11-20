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
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.korn.im.allin1.R;
import com.korn.im.allin1.accounts.AccountManager;
import com.korn.im.allin1.adapters.DialogsAdapter;
import com.korn.im.allin1.common.MarginDecoration;
import com.korn.im.allin1.pojo.Dialogs;
import com.korn.im.allin1.pojo.Interlocutor;

import java.util.Map;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class DialogsFragment extends Fragment {
    private static final String DIALOGS_MANAGER_STATE = "dialogs_llm_state";
    private SwipeRefreshLayout swipeRefreshDialogsLayout;
    private DialogsAdapter dialogsAdapter;

    private LinearLayoutManager llm;
    private Parcelable managerLastState;
    private Subscription dialogsUpdateSubscription;

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
            AccountManager.getInstance()
                          .getAccount()
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

        //dialogsAdapter.setItemClickListener((v, id) -> startDialogActivity(id));

        return rootView;
    }

    /*private void startDialogActivity(int id) {
        Intent intent = new Intent(getActivity(), DialogActivity.class);
        intent.putExtra(DialogActivity.CURRENT_DIALOG_ID, id);
        startActivity(intent);
    }*/

    @Override
    public void onResume() {
        super.onResume();
        dialogsUpdateSubscription = subscribeOnDialogsUpdate();
        fetchDialogs();
    }

    private void fetchDialogs() {
        AccountManager.getInstance()
                      .getAccount()
                      .getApi()
                      .fetchDialogs()
                      .subscribeOn(Schedulers.computation())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe((Action1<Pair<? extends Dialogs, ? extends Map<Integer, ? extends Interlocutor>>>) pair -> {
                          dialogsAdapter.setData(pair.first, pair.second);
                      });
    }

    private Subscription subscribeOnDialogsUpdate() {
        return AccountManager.getInstance()
                             .getAccount()
                             .getApi()
                             .getDataPublisher()
                             .dialogsObservable()
                             .subscribeOn(Schedulers.computation())
                             .observeOn(AndroidSchedulers.mainThread())
                             .subscribe((Action1<Pair<? extends Dialogs, ? extends Map<Integer, ? extends Interlocutor>>>) pair -> {
                                 dialogsAdapter.setData(pair.first, pair.second);
                                 swipeRefreshDialogsLayout.setRefreshing(false);
                             });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (dialogsUpdateSubscription != null) dialogsUpdateSubscription.unsubscribe();
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
}
