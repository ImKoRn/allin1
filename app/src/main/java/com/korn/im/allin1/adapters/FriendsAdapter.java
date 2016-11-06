package com.korn.im.allin1.adapters;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.korn.im.allin1.R;
import com.korn.im.allin1.ui.customview.SocialCircularImageView;
import com.korn.im.allin1.pojo.User;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.turingtechnologies.materialscrollbar.INameableAdapter;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Created by korn on 04.08.16.
 */
public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendsHolder> implements INameableAdapter {
    public static final int DEFAULT_CAPACITY = 100;
    private static final String TAG = "FriendsAdapter";
    protected final Context context;
    protected final int offlineColor;
    protected final int onlineColor;

    protected Comparator<User> comparator = User.POPULARITY;

    protected SortedList<User> data;
    protected List<User> dataSource;

    public FriendsAdapter(Context context, @ColorRes int onlineColorRes, @ColorRes int offlineColorRes) {
        this.context = context;
        this.onlineColor = context.getResources().getColor(onlineColorRes);
        this.offlineColor =  context.getResources().getColor(offlineColorRes);
    }

    public FriendsAdapter(Context context, @ColorRes int onlineColorRes, @ColorRes int offlineColorRes, int capacity) {
        this(context, onlineColorRes, offlineColorRes);

        data = new SortedList<>(User.class, new SortedList.BatchedCallback<>(new SortedListAdapterCallback<User>(this) {
            @Override
            public int compare(User o1, User o2) {
                if (comparator == null)
                    return 0;

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
    public FriendsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.vk_friend_item, parent, false);
        return new FriendsHolder(view);
    }

    @Override
    public void onBindViewHolder(FriendsHolder holder, int position) {
        holder.bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data == null? 0 : data.size();
    }

    public void setData(List<User> newUsersList) {
        dataSource = newUsersList;
        data.beginBatchedUpdates();
        synchronized (dataSource) {
            data.addAll(dataSource);
        }
        data.endBatchedUpdates();
    }

    public void setComparator(Comparator<User> comparator) {
        this.comparator = comparator;
        if(dataSource == null) return;

        data.beginBatchedUpdates();
        synchronized (dataSource) {
            data.clear();
            data.addAll(dataSource);
        }
        data.endBatchedUpdates();
    }

    @Override
    public Character getCharacterForElement(int element) {
        if(data.size() > element && element >= 0) {
            synchronized (dataSource) {
                if(comparator == User.NAME_CASE_NOT_INSENSITIVE)
                    return data.get(element).getName().charAt(0);
                else return data.get(element).getSurname().charAt(0);
            }
        } else return  ' ';
    }

    public void updateItems(Set<Integer> itemsIds) {
        if (dataSource == null)
            return;

        data.beginBatchedUpdates();
        synchronized (dataSource) {
            for (User user : dataSource) {
                if (itemsIds.contains(user.getId())) {
                    data.updateItemAt(data.indexOf(user), user);
                    Log.i(TAG, String.format("%s going %b", user, user.isOnline()));
                }
            }
        }
        data.endBatchedUpdates();
    }

    public void clear() {
        data.clear();
    }

    //Classes and Interfaces
    public class FriendsHolder extends RecyclerView.ViewHolder {
        private SocialCircularImageView userIcon;
        private TextView userName;
        private User user;

        public FriendsHolder(View itemView) {
            super(itemView);
            userIcon = (SocialCircularImageView) itemView.findViewById(R.id.userIcon);
            userName = (TextView) itemView.findViewById(R.id.userName);
        }

        public void bind(User user) {
            this.user = user;
            ImageLoader.getInstance().displayImage(user.getMediumImage(), userIcon);
            userIcon.setShowOnlineMark(user.isOnline(), user.isOnlineMobile());
            userName.setText(comparator == User.NAME_CASE_NOT_INSENSITIVE? user.getFullName() : user.getFullSurnameName());
        }
    }
}
