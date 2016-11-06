package com.korn.im.allin1.adapters;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.v7.util.SortedList;
import android.support.v7.widget.util.SortedListAdapterCallback;

import com.korn.im.allin1.pojo.User;

import java.util.List;
import java.util.Set;

/**
 * Created by korn on 08.08.16.
 */
public class OnlineFriendsAdapter extends FriendsAdapter {

    private static final String TAG = "OnlineFriendsAdapter";

    public OnlineFriendsAdapter(Context context, @ColorRes int colorOnline, @ColorRes int colorOffline, int capacity) {
        super(context, colorOnline, colorOffline);
        data = new SortedList<>(User.class, new SortedList.BatchedCallback<>(new SortedListAdapterCallback<User>(this) {
            @Override
            public int compare(User o1, User o2) {
                return comparator.compare(o1, o2);
            }

            @Override
            public boolean areContentsTheSame(User oldItem, User newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areItemsTheSame(User item1, User item2) {
                return item1.getId() == item2.getId();
            }
        }), capacity);
    }

    @Override
    public void setData(List<User> newUsersList) {
        dataSource = newUsersList;
        data.beginBatchedUpdates();
        synchronized (dataSource) {
            for (User user : dataSource)
            if (user.isOnline())
                data.add(user);
            else data.remove(user);
        }
        data.endBatchedUpdates();
    }

    @Override
    public void updateItems(Set<Integer> itemsIds) {
        if (dataSource == null)
            return;

        data.beginBatchedUpdates();
        synchronized (dataSource) {
            for (User user : dataSource)
                if (itemsIds.contains(user.getId())) {
                    if (user.isOnline()) data.add(user);
                    else data.remove(user);
                }
        }
        data.endBatchedUpdates();
    }
}
