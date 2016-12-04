package com.korn.im.allin1.adapters;

import android.content.Context;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.korn.im.allin1.R;
import com.korn.im.allin1.pojo.User;
import com.korn.im.allin1.ui.customview.OnlineImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
//import com.turingtechnologies.materialscrollbar.INameableAdapter;

import java.util.Map;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendsHolder> {//implements INameableAdapter {
    public static final int DEFAULT_CAPACITY = 100;

    private final Context context;

    protected SortedList<User> data;

    public FriendsAdapter(Context context) {
        this.context = context;
    }

    public FriendsAdapter(Context context, int capacity) {
        this.context = context;
        data = new SortedList<>(User.class, new SortedList.BatchedCallback<>(new SortedListAdapterCallback<User>(this) {
            @Override
            public int compare(User o1, User o2) {
                int c = User.NAME_CASE_NOT_INSENSITIVE.compare(o1, o2);
                if (c == 0) c = User.SURNAME_CASE_NOT_INSENSITIVE.compare(o1, o2);
                if (c == 0) c = o1.getId() - o2.getId();
                return c;
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
        return new FriendsHolder(LayoutInflater.from(context).inflate(R.layout.vk_friend_item, parent, false));
    }

    @Override
    public void onBindViewHolder(FriendsHolder holder, int position) {
        holder.bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data == null? 0 : data.size();
    }

    public void setData(Map<Integer, ? extends User> newUsersList) {
        data.beginBatchedUpdates();
        for (int i = 0; i < data.size(); i++)
            if (newUsersList.containsKey(data.get(i).getId()))
                data.recalculatePositionOfItemAt(i);
            else data.removeItemAt(i--);
        for (Map.Entry<Integer, ? extends User> entry : newUsersList.entrySet())
            data.add(entry.getValue());
        data.endBatchedUpdates();
    }

    /*@Override
    public Character getCharacterForElement(int element) {
        if(data.getDialogsCount() > element && element >= 0) {
            return data.get(element).getName().charAt(0);
        } else return  ' ';
    }*/

    public void clear() {
        data.clear();
    }

    //Classes and Interfaces
    class FriendsHolder extends RecyclerView.ViewHolder {
        private OnlineImageView userIcon;
        private TextView userName;

        FriendsHolder(View itemView) {
            super(itemView);
            userIcon = (OnlineImageView) itemView.findViewById(R.id.userIcon);
            userName = (TextView) itemView.findViewById(R.id.userName);
        }

        void bind(User user) {
            ImageLoader.getInstance().displayImage(user.getMediumImage(), userIcon.getImageView());
            userIcon.setShowOnlineMark(user.isOnline(), user.isOnlineMobile());
            userName.setText(user.getFullName());
        }
    }
}
