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
import com.korn.im.allin1.adapters.advancedadapter.AdvancedAdapter;
import com.korn.im.allin1.adapters.advancedadapter.Holder;
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

public class DialogsAdapter extends AdvancedAdapter<DialogsAdapter.DialogHolder, Holder> {
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
    protected void onBindHolder(DialogHolder holder,
                                int position) {
        Dialog dialog = data.get(position);
        holder.bind(dialog,
                    interlocutors.get(dialog.getId()),
                    messages.get(dialog.getId(), dialog.getLastMessageId()));
    }

    @Override
    public int getActualItemCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public Holder createOnLoadingHolder(ViewGroup parent) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.loader_layout, parent, false),
                          this) {
            @Override
            public int getId() {
                return -1;
            }

            @Override
            public void onClick(View v) {}

            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        };
    }

    @Override
    protected DialogHolder createHolder(ViewGroup parent,
                                        int viewType) {
        return new DialogHolder(LayoutInflater.from(context).inflate(R.layout.vk_dialog_item, parent, false), this);
    }

    public void setData(Dialogs<Dialog, Message> dialogs, Map<Integer, ? extends Interlocutor> interlocutors) {
        this.interlocutors = interlocutors;
        this.messages = dialogs.getMessages();
        data.beginBatchedUpdates();
        for (int i = 0; i < data.size(); i++) {
            if (dialogs.getDialogs().containsKey(data.get(i).getId())) {
                data.recalculatePositionOfItemAt(i);
            } else data.removeItemAt(i--);
        }
        for (Map.Entry<Integer, Dialog> entry : dialogs.getDialogs()
                                                       .entrySet())
            data.add(entry.getValue());
        currentTime = System.currentTimeMillis() / 1000L;
        data.endBatchedUpdates();
    }

    public void clear() {
        data.clear();
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

    class DialogHolder extends Holder {
        private SocialCircularImageView userIconImageView;
        private TextView userNameTextView;
        private TextView lastMessageTextView;
        private TextView dateTimeTextView;
        private TextView unreadCountTextView;
        private Dialog dialog;

        DialogHolder(View itemView,
                     AdvancedAdapter<? extends Holder, ? extends Holder> advancedAdapter) {
            super(itemView, advancedAdapter, R.id.root);
            userIconImageView = (SocialCircularImageView) itemView.findViewById(R.id.userIcon);
            userNameTextView = (TextView) itemView.findViewById(R.id.userName);
            lastMessageTextView = (TextView) itemView.findViewById(R.id.lastMessage);
            dateTimeTextView = (TextView) itemView.findViewById(R.id.dateTime);
            unreadCountTextView = (TextView) itemView.findViewById(R.id.unreadCount);
        }

        @Override
        public int getId() {
            return dialog.getId();
        }

        private void bind(Dialog dialog, Interlocutor interlocutor, Message lastMessage) {
            this.dialog = dialog;

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
    }
}
