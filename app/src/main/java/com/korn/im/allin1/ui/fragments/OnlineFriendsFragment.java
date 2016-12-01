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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.korn.im.allin1.R;
import com.korn.im.allin1.accounts.AccountManager;
import com.korn.im.allin1.accounts.AccountType;
import com.korn.im.allin1.adapters.FriendsAdapter;
import com.korn.im.allin1.adapters.OnlineFriendsAdapter;
import com.korn.im.allin1.common.RecyclerPauseOnScrollListener;
import com.korn.im.allin1.pojo.User;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.turingtechnologies.materialscrollbar.AlphabetIndicator;
import com.turingtechnologies.materialscrollbar.DragScrollBar;

import java.util.Collection;
import java.util.Map;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class OnlineFriendsFragment extends Fragment {
    private static final String ONLINE_FRIENDS_MANAGER_STATE = "llm_state";

    // Ui
    private SwipeRefreshLayout swipeRefreshOnlineFriendsLayout;

    // Subscriptions
    private Subscription friendsUpdateSubscription;
    private Subscription friendsErrorsSubscription;

    // Members
    private OnlineFriendsAdapter onlineFriendsAdapter;
    private LinearLayoutManager llm;
    private Parcelable managerLastState;

    public OnlineFriendsFragment() {}

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            managerLastState = savedInstanceState.getParcelable(ONLINE_FRIENDS_MANAGER_STATE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_online_friends, container, false);
        DragScrollBar dragScrollBar = (DragScrollBar) rootView.findViewById(R.id.fast_scroller);
        dragScrollBar.addIndicator(new AlphabetIndicator(getActivity()), true);
        dragScrollBar.setDraggableFromAnywhere(true);

        swipeRefreshOnlineFriendsLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_online_friends);
        swipeRefreshOnlineFriendsLayout.setOnRefreshListener(() -> {
            swipeRefreshOnlineFriendsLayout.setRefreshing(true);
            fetchFriends();
        });
        swipeRefreshOnlineFriendsLayout.setColorSchemeColors(Color.MAGENTA);

        // Connect the recycler to the scroller (to let the scroller scroll the list)
        RecyclerView onlineFriendsList = (RecyclerView) rootView.findViewById(
                R.id.online_friends_list);

        onlineFriendsList.setLayoutManager(llm = getLayoutManager());
        onlineFriendsList.setAdapter(onlineFriendsAdapter = new OnlineFriendsAdapter(getContext(), FriendsAdapter.DEFAULT_CAPACITY));
        onlineFriendsList.addOnScrollListener(new RecyclerPauseOnScrollListener(ImageLoader.getInstance(),
                                                                                true, true));
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
        swipeRefreshOnlineFriendsLayout.setRefreshing(isFriendsLoading());
    }

    @Override
    public void onPause() {
        super.onPause();
        if (friendsUpdateSubscription != null) friendsUpdateSubscription.unsubscribe();
        if (friendsErrorsSubscription != null) friendsErrorsSubscription.unsubscribe();

        managerLastState = llm.onSaveInstanceState();
        onlineFriendsAdapter.clear();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ONLINE_FRIENDS_MANAGER_STATE, managerLastState);
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
                                     onlineFriendsAdapter.setData(users);
                                     swipeRefreshOnlineFriendsLayout.setRefreshing(false);
                                     llm.onRestoreInstanceState(managerLastState);
                                     managerLastState = null;
                                 },
                                 throwable -> Toast.makeText(OnlineFriendsFragment.this.getActivity(),
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
                                            onlineFriendsAdapter.setData(users);
                                            swipeRefreshOnlineFriendsLayout.setRefreshing(false);
                                        },
                                        throwable -> Toast.makeText(OnlineFriendsFragment.this.getActivity(),
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
                                            Toast.makeText(OnlineFriendsFragment.this.getActivity(),
                                                           throwable.getMessage(),
                                                           Toast.LENGTH_SHORT).show();
                                            swipeRefreshOnlineFriendsLayout.setRefreshing(false);
                                        },
                                        throwable -> Toast.makeText(OnlineFriendsFragment.this.getActivity(),
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
