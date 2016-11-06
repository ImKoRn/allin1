package com.korn.im.allin1.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.korn.im.allin1.R;
import com.korn.im.allin1.pojo.Dialog;
import com.korn.im.allin1.pojo.Dialogs;
import com.korn.im.allin1.pojo.Interlocutor;
import com.korn.im.allin1.ui.customview.SocialCircularImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

/**
 * Created by korn on 05.08.16.
 */
public class DialogsAdapter extends AdvancedAdapter {
    private static final int MINUTE = 60;
    private static final int HOUR = MINUTE * 60;
    private static final int DAY = HOUR * 24;
    private final Calendar dateFormat = Calendar.getInstance(TimeZone.getTimeZone("GMT+3"));
    private long currentTime;

    private final Context context;

    private Comparator<Dialog> comparator = Dialog.TIME_ORDERED;

    private Dialogs dataSource;
    private final SortedList<Dialog> data;

    private int offlineColor = Color.WHITE;
    private int onlineColor = Color.GREEN;

    public DialogsAdapter(Context context, @ColorRes int onlineColorRes, @ColorRes int offlineColorRes,
                          RecyclerView recyclerView, LinearLayoutManager llm) {
        super(recyclerView, llm);
        this.context = context;
        onlineColor = context.getResources().getColor(onlineColorRes);
        offlineColor = context.getResources().getColor(offlineColorRes);
        data = new SortedList<>(Dialog.class, new SortedList.BatchedCallback<>(new SortedListAdapterCallback<Dialog>(this) {
            @Override
            public int compare(Dialog first, Dialog second) {
                return comparator.compare(first, second);
            }

            @Override
            public boolean areContentsTheSame(Dialog oldItem, Dialog newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areItemsTheSame(Dialog item1, Dialog item2) {
                return item1.getId() == item2.getId();
            }
        }));
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        Holder holder = super.onCreateViewHolder(parent, viewType);
        if (holder != null) return holder;

        View view = LayoutInflater.from(context).inflate(R.layout.vk_dialog_item, parent, false);
        return new DialogHolder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        if(position == getActualItemCount()) return;
        Dialog dialog = data.get(position);
        //((DialogHolder) holder).bind(dialog, dataSource.getInterlocutor(dialog.getId()));
    }

    @Override
    public Holder createOnLoadingHolder(ViewGroup parent) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.loader_layout, parent, false)) {
            @Override
            public void onClick(View v) {}

            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        };
    }

    @Override
    public int getActualItemCount() {
        return data == null ? 0 : data.size();
    }

    public void setData(Dialogs dialogs) {
        currentTime = System.currentTimeMillis() / 1000L;
        this.dataSource = dialogs;
        data.beginBatchedUpdates();
        synchronized (dataSource) {
            data.addAll(dialogs.getDialogs());
        }
        data.endBatchedUpdates();
    }

    public void clear() {
        data.clear();
    }

    public void updateItems(Set<Integer> items) {
        if(dataSource == null)
            return;

        currentTime = System.currentTimeMillis() / 1000L;

        data.beginBatchedUpdates();

        data.endBatchedUpdates();
    }

    public class DialogHolder extends Holder {
        private SocialCircularImageView userIcon;
        private TextView userName;
        private TextView lastMessage;
        private TextView dateTime;
        private TextView unreadCount;

        public DialogHolder(View itemView) {
            super(itemView, R.id.root);
            userIcon = (SocialCircularImageView) itemView.findViewById(R.id.userIcon);
            userName = (TextView) itemView.findViewById(R.id.userName);
            lastMessage = (TextView) itemView.findViewById(R.id.lastMessage);
            dateTime = (TextView) itemView.findViewById(R.id.dateTime);
            unreadCount = (TextView) itemView.findViewById(R.id.unreadCount);
        }

        public void bind(Dialog dialog, Interlocutor interlocutor) {
            ImageLoader.getInstance().displayImage(interlocutor.getMediumImage(), userIcon);
            userIcon.setBorderColor(interlocutor.isOnline()? onlineColor : offlineColor);
            userIcon.setShowOnlineMark(interlocutor.isOnline(), interlocutor.isOnlineMobile());
            userName.setText(interlocutor.getFullName());
            lastMessage.setText(dialog.getMessages().get(0).getContent());
            if(dialog.getUnreadCount() > 0) {
                unreadCount.setText(Integer.toString(dialog.getUnreadCount()));
                unreadCount.setVisibility(View.VISIBLE);
            } else unreadCount.setVisibility(View.INVISIBLE);
            dateTime.setText(unixToHumanDate(dialog.getMessages().get(0).getDate()));
        }

        @Override
        public void onClick(View v) {
            synchronized (dataSource) {
                if(itemClickListener != null)
                    itemClickListener.onClick(v, data.get(getAdapterPosition()).getId());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            synchronized (dataSource) {
                if(itemLongClickListener != null)
                    itemLongClickListener.onClick(v, data.get(getAdapterPosition()).getId());
            }
            return true;
        }
    }

    private String unixToHumanDate(long itemDate) {
        final long time = currentTime - itemDate;
        dateFormat.setTimeInMillis(itemDate * 1000);

        if(time < MINUTE)
            return "Now";

        if(time < DAY)
            return String.format(Locale.US, "%02d:%02d",
                    dateFormat.get(Calendar.HOUR_OF_DAY), dateFormat.get(Calendar.MINUTE));

        if(time < dateFormat.getActualMaximum(Calendar.DAY_OF_YEAR) * DAY)
            return String.format(Locale.US, "%s %d",
                    dateFormat.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US),
                    dateFormat.get(Calendar.DAY_OF_MONTH));

        return String.format(Locale.US, "%s %d %d",
                dateFormat.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US),
                dateFormat.get(Calendar.DAY_OF_MONTH),
                dateFormat.get(Calendar.YEAR));
    }
}
