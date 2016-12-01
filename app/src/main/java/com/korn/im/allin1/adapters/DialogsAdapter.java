package com.korn.im.allin1.adapters;

import android.content.Context;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.collect.Table;
import com.korn.im.allin1.R;
import com.korn.im.allin1.pojo.Dialog;
import com.korn.im.allin1.pojo.Dialogs;
import com.korn.im.allin1.pojo.Interlocutor;
import com.korn.im.allin1.pojo.Message;
import com.korn.im.allin1.ui.customview.SocialCircularImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
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

    private final SortedList<Dialog> data;
    private Map<Integer, ? extends Interlocutor> interlocutors;
    private Table<Integer, Integer, Message> messages;

    public DialogsAdapter(Context context,
                          RecyclerView recyclerView,
                          LinearLayoutManager llm) {
        super(recyclerView, llm);
        this.context = context;
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
        if (interlocutors.get(dialog.getId()) == null)
            dialog.getId();
        Interlocutor interlocutor = interlocutors.get(dialog.getId());
        Message message = messages.get(dialog.getId(), dialog.getLastMessageId());
        ((DialogHolder) holder).bind(dialog, interlocutor, message);
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

    public void setData(Dialogs<Dialog, Message> dialogs, Map<Integer, ? extends Interlocutor> interlocutors) {
        this.interlocutors = interlocutors;
        this.messages = dialogs.getMessages();
        data.beginBatchedUpdates();
        for (int i = 0; i < data.size(); i++) {
            Dialog dialog = data.get(i);
            if (dialogs.getDialogs().containsKey(dialog.getId())) {
                data.recalculatePositionOfItemAt(i);
            } else data.removeItemAt(i--);
        }
        data.addAll(dialogs.getDialogs().values());
        data.endBatchedUpdates();
        currentTime = System.currentTimeMillis() / 1000L;
    }

    public void clear() {
        data.clear();
    }

    private class DialogHolder extends Holder {
        private SocialCircularImageView userIconImageView;
        private TextView userNameTextView;
        private TextView lastMessageTextView;
        private TextView dateTimeTextView;
        private TextView unreadCountTextView;

        private DialogHolder(View itemView) {
            super(itemView, R.id.root);
            userIconImageView = (SocialCircularImageView) itemView.findViewById(R.id.userIcon);
            userNameTextView = (TextView) itemView.findViewById(R.id.userName);
            lastMessageTextView = (TextView) itemView.findViewById(R.id.lastMessage);
            dateTimeTextView = (TextView) itemView.findViewById(R.id.dateTime);
            unreadCountTextView = (TextView) itemView.findViewById(R.id.unreadCount);
        }

        private void bind(Dialog dialog, Interlocutor interlocutor, Message lastMessage) {
            ImageLoader.getInstance().displayImage(interlocutor.getMediumImage(), userIconImageView);
            userIconImageView.setShowOnlineMark(interlocutor.isOnline(), interlocutor.isOnlineMobile());
            userNameTextView.setText(interlocutor.getFullName());
            dateTimeTextView.setText(unixToHumanDate(lastMessage.getDate()));
            lastMessageTextView.setText(lastMessage.getContent());
            if(dialog.getUnreadCount() > 0) {
                unreadCountTextView.setText(Integer.toString(dialog.getUnreadCount()));
                unreadCountTextView.setVisibility(View.VISIBLE);
            } else unreadCountTextView.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onClick(View v) {
            if(itemClickListener != null)
                itemClickListener.onClick(v, data.get(getAdapterPosition()).getId());
        }

        @Override
        public boolean onLongClick(View v) {
            if(itemLongClickListener != null)
                itemLongClickListener.onClick(v, data.get(getAdapterPosition()).getId());
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
