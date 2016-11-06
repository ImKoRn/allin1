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
import com.korn.im.allin1.accounts.Event;
import com.korn.im.allin1.adapters.AdvancedAdapter;
import com.korn.im.allin1.adapters.FriendsAdapter;
import com.korn.im.allin1.adapters.OnlineFriendsAdapter;
import com.korn.im.allin1.common.RecyclerPauseOnScrollListener;
import com.korn.im.allin1.pojo.User;
import com.korn.im.allin1.vk.VkRequestUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.turingtechnologies.materialscrollbar.AlphabetIndicator;
import com.turingtechnologies.materialscrollbar.DragScrollBar;

import java.util.List;
import java.util.Set;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by korn on 08.08.16.
 */
public class OnlineFriendsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String ONLINE_FRIENDS_MANAGER_STATE = "llm_state";

    private SwipeRefreshLayout swipeRefreshOnlineFriendsLayout;
    private OnlineFriendsAdapter onlineFriendsAdapter;
    private RecyclerView onlineFriendsList;
    private Subscription onlineFriendsSubscription;
    private Subscription friendsStatusChangeSubscription;

    private LinearLayoutManager llm;
    private Parcelable managerLastState;

    public OnlineFriendsFragment() {
    }

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
        AlphabetIndicator alphabetIndicator = new AlphabetIndicator(getActivity());
        DragScrollBar dragScrollBar = (DragScrollBar) rootView.findViewById(R.id.fast_scroller);
        dragScrollBar.addIndicator(alphabetIndicator, true);
        dragScrollBar.setDraggableFromAnywhere(true);

        swipeRefreshOnlineFriendsLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_online_friends);
        swipeRefreshOnlineFriendsLayout.setOnRefreshListener(this);
        swipeRefreshOnlineFriendsLayout.setColorSchemeColors(Color.MAGENTA);

        // Connect the recycler to the scroller (to let the scroller scroll the list)
        onlineFriendsList = (RecyclerView) rootView.findViewById(R.id.online_friends_list);

        onlineFriendsList.setLayoutManager(llm = getLayoutManager());

        onlineFriendsList.setAdapter(onlineFriendsAdapter = new OnlineFriendsAdapter(getContext(),
                R.color.colorOnline, R.color.colorOffline, FriendsAdapter.DEFAULT_CAPACITY));
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
/*

    @Override
    public void onResume() {
        super.onResume();

        subscribeOnData();

        friendsStatusChangeSubscription = AccountManager.getInstance().getVkAccount().getEvents()
                .onFriendsStatusChangedEvent()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Set<Integer>>() {
                    @Override
                    public void call(Set<Integer> itemsIds) {
                        onlineFriendsAdapter.updateItems(itemsIds);
                    }
                });

        AccountManager.getInstance().getVkAccount().getApi().cachedFriends();
    }

    private void subscribeOnData() {
        onlineFriendsSubscription = AccountManager.getInstance().getVkAccount().getEvents()
                .onDataEvents()
                .filter(new Func1<Event, Boolean>() {
                    @Override
                    public Boolean call(Event event) {
                        return event.getRequestDataType() == Event.REQUEST_DATA_TYPE_FRIENDS;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Event>() {
                    @Override
                    public void call(Event event) {
                        switch (event.getRequestType()) {
                            case Event.REQUEST_TYPE_UPDATE: {
                                managerLastState = llm.onSaveInstanceState();
                                onlineFriendsAdapter.clear();
                                onlineFriendsAdapter.setData(AccountManager.getInstance()
                                        .getVkAccount().getDataManager().getFriends());
                                llm.onRestoreInstanceState(managerLastState);
                                managerLastState = null;
                                break;
                            }
                            case Event.REQUEST_TYPE_LOAD_NEXT: {
                                onlineFriendsAdapter.setData(AccountManager.getInstance()
                                        .getVkAccount().getDataManager().getFriends());
                                break;
                            }
                            case Event.REQUEST_TYPE_CACHE: {
                                onlineFriendsAdapter.setData(AccountManager.getInstance()
                                        .getVkAccount().getDataManager().getFriends());
                                llm.onRestoreInstanceState(managerLastState);
                                managerLastState = null;
                                break;
                            }
                        }
                        swipeRefreshOnlineFriendsLayout.setRefreshing(false);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        swipeRefreshOnlineFriendsLayout.setRefreshing(false);
                        subscribeOnData();
                    }
                });
    }
*/

    @Override
    public void onPause() {
        super.onPause();
        if(onlineFriendsSubscription != null)
            onlineFriendsSubscription.unsubscribe();

        if(friendsStatusChangeSubscription != null)
            friendsStatusChangeSubscription.unsubscribe();

        managerLastState = llm.onSaveInstanceState();
        onlineFriendsAdapter.clear();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ONLINE_FRIENDS_MANAGER_STATE, managerLastState);
    }

    @Override
    public void onRefresh() {
        swipeRefreshOnlineFriendsLayout.setRefreshing(true);
    }
}
