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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.korn.im.allin1.R;
import com.korn.im.allin1.accounts.AccountManager;
import com.korn.im.allin1.adapters.FriendsAdapter;
import com.korn.im.allin1.common.RecyclerPauseOnScrollListener;
import com.korn.im.allin1.pojo.User;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.turingtechnologies.materialscrollbar.AlphabetIndicator;
import com.turingtechnologies.materialscrollbar.DragScrollBar;

import java.util.List;

import rx.Subscription;

public class FriendsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String FRIENDS_MANAGER_STATE = "friends_llm_state";

    private SwipeRefreshLayout swipeRefreshFriendsLayout;
    private RecyclerView friendsList;
    private FriendsAdapter friendsAdapter;
    private Subscription friendsSubscription;
    private Subscription friendsStatusChangeSubscription;
    private DragScrollBar dragScrollBar;
    private AlphabetIndicator alphabetIndicator;

    private LinearLayoutManager llm;
    private Parcelable managerLastState;

    public FriendsFragment() {}

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            managerLastState = savedInstanceState.getParcelable(FRIENDS_MANAGER_STATE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        alphabetIndicator = new AlphabetIndicator(getActivity());
        dragScrollBar = (DragScrollBar) rootView.findViewById(R.id.fast_scroller);
        dragScrollBar.setDraggableFromAnywhere(true);

        swipeRefreshFriendsLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_friends);
        swipeRefreshFriendsLayout.setOnRefreshListener(this);
        swipeRefreshFriendsLayout.setColorSchemeColors(Color.MAGENTA);

        // Connect the recycler to the scroller (to let the scroller scroll the list)
        friendsList = (RecyclerView) rootView.findViewById(R.id.friends_list);
        friendsList.setAdapter(friendsAdapter = new FriendsAdapter(getContext(),
                R.color.colorOnline, R.color.colorOffline, FriendsAdapter.DEFAULT_CAPACITY));

        friendsList.setLayoutManager(llm = getLayoutManager());

        friendsList.addOnScrollListener(new RecyclerPauseOnScrollListener(ImageLoader.getInstance(),
                true, true));

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
/*
        subscribeOnData();

        friendsStatusChangeSubscription = AccountManager.getInstance().getVkAccount().getEvents()
                .onFriendsStatusChangedEvent()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Set<Integer>>() {
                    @Override
                    public void call(Set<Integer> itemsIds) {
                        friendsAdapter.updateItems(itemsIds);
                    }
                });

    }

    private void subscribeOnData() {
        friendsSubscription = AccountManager.getInstance().getVkAccount().getEvents()
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
                                friendsAdapter.clear();
                                friendsAdapter.setData(AccountManager.getInstance()
                                        .getVkAccount().getDataManager().getFriends());
                                llm.onRestoreInstanceState(managerLastState);
                                managerLastState = null;
                                break;
                            }
                            case Event.REQUEST_TYPE_LOAD_NEXT: {
                                friendsAdapter.setData(AccountManager.getInstance()
                                        .getVkAccount().getDataManager().getFriends());
                                break;
                            }
                            case Event.REQUEST_TYPE_CACHE: {
                                friendsAdapter.setData(AccountManager.getInstance()
                                        .getVkAccount().getDataManager().getFriends());
                                llm.onRestoreInstanceState(managerLastState);
                                managerLastState = null;
                                break;
                            }
                        }
                        swipeRefreshFriendsLayout.setRefreshing(false);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        swipeRefreshFriendsLayout.setRefreshing(false);
                        subscribeOnData();
                    }
                });*/
    }

    @Override
    public void onPause() {
        super.onPause();
        if(friendsSubscription != null)
            friendsSubscription.unsubscribe();
        if(friendsStatusChangeSubscription != null)
            friendsStatusChangeSubscription.unsubscribe();

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.name_sort : {
                friendsAdapter.setComparator(User.NAME_CASE_NOT_INSENSITIVE);
                dragScrollBar.setVisibility(View.VISIBLE);
                dragScrollBar.addIndicator(alphabetIndicator, true);

                item.setChecked(true);
                return true;
            }
            case R.id.surname_sort: {
                friendsAdapter.setComparator(User.SURNAME_CASE_NOT_INSENSITIVE);
                dragScrollBar.setVisibility(View.VISIBLE);
                dragScrollBar.addIndicator(alphabetIndicator, true);

                item.setChecked(true);
                return true;
            }
            case R.id.popularity_sort: {
                friendsAdapter.setComparator(User.POPULARITY);
                dragScrollBar.removeIndicator();
                item.setChecked(true);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        swipeRefreshFriendsLayout.setRefreshing(true);
        AccountManager.getInstance().getVkAccount().getApi()
                .fetchFriends()
                .subscribe(vkUsers -> {
                    Toast.makeText(FriendsFragment.this.getActivity(), vkUsers.size() + "", Toast.LENGTH_SHORT).show();
                    friendsAdapter.setData((List<User>) (List) vkUsers);
                }, throwable -> {
                    Toast.makeText(FriendsFragment.this.getActivity(), "Error", Toast.LENGTH_SHORT).show();
                }, () -> Toast.makeText(FriendsFragment.this.getActivity(), "Compleete", Toast.LENGTH_SHORT).show());
    }
}
