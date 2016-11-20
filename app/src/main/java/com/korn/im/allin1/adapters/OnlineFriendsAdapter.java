package com.korn.im.allin1.adapters;

import android.content.Context;
import android.support.v7.util.SortedList;
import android.support.v7.widget.util.SortedListAdapterCallback;

import com.korn.im.allin1.pojo.User;

import java.util.Collection;

public class OnlineFriendsAdapter extends FriendsAdapter {
    public OnlineFriendsAdapter(Context context, int capacity) {
        super(context);
        data = new SortedList<>(User.class, new SortedList.BatchedCallback<>(new SortedListAdapterCallback<User>(this) {
            @Override
            public int compare(User o1, User o2) {
                return User.NAME_CASE_NOT_INSENSITIVE.compare(o1, o2);
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
    public void setData(Collection<? extends User> newUsersList) {
        data.beginBatchedUpdates();
        for (User user : newUsersList)
            if (user.isOnline())
                data.add(user);
            else data.remove(user);
        data.endBatchedUpdates();
    }
}
