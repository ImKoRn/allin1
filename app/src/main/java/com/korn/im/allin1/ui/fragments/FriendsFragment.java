package com.korn.im.allin1.ui.fragments;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.korn.im.allin1.R;
import com.korn.im.allin1.accounts.AccountManager;
import com.korn.im.allin1.accounts.AccountType;
import com.korn.im.allin1.adapters.FriendsAdapter;
import com.korn.im.allin1.common.RecyclerPauseOnScrollListener;
import com.korn.im.allin1.pojo.User;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.turingtechnologies.materialscrollbar.DragScrollBar;

import java.util.Collection;
import java.util.Map;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class FriendsFragment extends Fragment {
    // Constants
    private static final String FRIENDS_MANAGER_STATE = "friends_llm_state";
    private static final String TAG = "FriendsFragment";

    // Ui
    private SwipeRefreshLayout swipeRefreshFriendsLayout;

    // Subscriptions
    private Subscription friendsUpdateSubscription;
    private Subscription friendsErrorsSubscription;

    // Members
    private Parcelable managerLastState;
    private LinearLayoutManager llm;
    private FriendsAdapter friendsAdapter;

    public FriendsFragment() {}

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null)
            managerLastState = savedInstanceState.getParcelable(FRIENDS_MANAGER_STATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        DragScrollBar dragScrollBar = (DragScrollBar) rootView.findViewById(R.id.fast_scroller);
        dragScrollBar.setDraggableFromAnywhere(true);

        swipeRefreshFriendsLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_friends);
        swipeRefreshFriendsLayout.setOnRefreshListener(() -> {
            swipeRefreshFriendsLayout.setRefreshing(true);
            AccountManager.getInstance()
                          .getAccount(AccountType.Vk)
                          .getApi()
                          .loadFriends();
        });
        swipeRefreshFriendsLayout.setColorSchemeColors(Color.MAGENTA);

        // Connect the recycler to the scroller (to let the scroller scroll the list)
        RecyclerView friendsList = (RecyclerView) rootView.findViewById(R.id.friends_list);
        friendsList.setAdapter(friendsAdapter = new FriendsAdapter(getContext(), FriendsAdapter.DEFAULT_CAPACITY));

        friendsList.setLayoutManager(llm = getLayoutManager());

        friendsList.addOnScrollListener(new RecyclerPauseOnScrollListener(ImageLoader.getInstance(),
                                                                          true,
                                                                          true));

        setHasOptionsMenu(true);
        return rootView;
    }

    public LinearLayoutManager getLayoutManager() {
        if(getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            return new LinearLayoutManager(getContext()) {
                @Override
                public boolean supportsPredictiveItemAnimations() {
                    return false;
                }
            };
        } else {
            return new GridLayoutManager(getContext(), 2) {
                @Override
                public boolean supportsPredictiveItemAnimations() {
                    return false;
                }
            };
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        friendsUpdateSubscription = subscribeOnFriendsUpdate();
        friendsErrorsSubscription = subscribeOnFriendsErrors();
        fetchFriends();
        swipeRefreshFriendsLayout.setRefreshing(isFriendsLoading());
    }

    @Override
    public void onPause() {
        super.onPause();

        if (friendsUpdateSubscription != null) friendsUpdateSubscription.unsubscribe();
        if (friendsErrorsSubscription != null) friendsErrorsSubscription.unsubscribe();

        managerLastState = llm.onSaveInstanceState();
        friendsAdapter.clear();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(FRIENDS_MANAGER_STATE, managerLastState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_friends, menu);
    }


    private void fetchFriends() {
        AccountManager.getInstance()
                      .getAccount(AccountType.Vk)
                      .getApi()
                      .fetchFriends()
                      .flatMap((Func1<Map<Integer, ? extends User>, Observable<Collection<? extends User>>>) integerMap -> Observable.just(integerMap.values()))
                      .subscribeOn(Schedulers.computation())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(users -> {
                                     friendsAdapter.setData(users);
                                     swipeRefreshFriendsLayout.setRefreshing(false);
                                 },
                                 throwable -> Toast.makeText(FriendsFragment.this.getActivity(),
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
                                            Log.i(TAG, "onResume: friends arrived");
                                            friendsAdapter.setData(users);
                                            swipeRefreshFriendsLayout.setRefreshing(false);
                                        },
                                        throwable -> Toast.makeText(FriendsFragment.this.getActivity(),
                                                                    throwable.getMessage(),
                                                                    Toast.LENGTH_SHORT).show());
    }

    private Subscription subscribeOnFriendsErrors() {
        return AccountManager.getInstance()
                             .getAccount(AccountType.Vk)
                             .getApi()
                             .getDataPublisher()
                             .friendsErrorsObservable()
                             .observeOn(AndroidSchedulers.mainThread())
                             .subscribe(throwable -> {
                                            Toast.makeText(FriendsFragment.this.getActivity(),
                                                           throwable.getMessage(),
                                                           Toast.LENGTH_SHORT).show();
                                            swipeRefreshFriendsLayout.setRefreshing(false);
                                        },
                                        throwable -> Toast.makeText(FriendsFragment.this.getActivity(),
                                                                    throwable.getMessage(),
                                                                    Toast.LENGTH_SHORT).show());
    }

    private boolean isFriendsLoading() {
        return AccountManager.getInstance()
                             .getAccount(AccountType.Vk)
                             .getApi()
                             .isFriendsLoading();
    }
}
