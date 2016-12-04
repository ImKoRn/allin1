package com.korn.im.allin1.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.korn.im.allin1.R;
import com.korn.im.allin1.adapters.advancedadapter.AdvancedAdapter;
import com.korn.im.allin1.adapters.advancedadapter.Holder;
import com.korn.im.allin1.pojo.Message;

import java.util.Map;

public class MessagesAdapter extends AdvancedAdapter<MessagesAdapter.MessageHolder, Holder> {
    private static final int INCOMING = 1;
    private static final int OUTGOING = 2;

    private SortedList<Message> data;

    private Context context;

    public MessagesAdapter(Context context, RecyclerView recyclerView, LinearLayoutManager llm) {
        super(recyclerView, llm);
        this.context = context;
        data = new SortedList<>(Message.class, new SortedList.BatchedCallback<>(
                new SortedListAdapterCallback<Message>(this) {
            @Override
            public int compare(Message first, Message second) {
                return second.getId() - first.getId();
            }

            @Override
            public boolean areContentsTheSame(Message oldItem, Message newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areItemsTheSame(Message item1, Message item2) {
                return item1.getId() == item2.getId() || item1 == item2;
            }
        }));
    }

    @Override
    public int getActualItemCount() {
        return data == null? 0 : data.size();
    }

    @Override
    protected int whatIsItemViewType(int position) {
        return data.get(position).isOut() ? OUTGOING : INCOMING;
    }

    @Override
    protected MessageHolder createHolder(ViewGroup parent,
                                         int viewType) {
        int layoutId = viewType == OUTGOING ? R.layout.outcoming_message_item : R.layout.incoming_message_item;
        return new MessageHolder(LayoutInflater.from(context)
                                               .inflate(layoutId,
                                                        parent,
                                                        false),
                                 this);
    }

    @Override
    protected void onBindHolder(MessageHolder holder,
                                int position) {
        holder.bind(data.get(position));
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

    public void setData(Map<Integer, ? extends Message> messages) {
        data.beginBatchedUpdates();
        for (Map.Entry<Integer, ? extends Message> message : messages.entrySet())
            data.add(message.getValue());
        data.endBatchedUpdates();
    }

    public void clear() {
        data.clear();
    }

    class MessageHolder extends Holder {
        private TextView messageText;
        MessageHolder(View itemView,
                      AdvancedAdapter<? extends Holder, ? extends Holder> advancedAdapter) {
            super(itemView, advancedAdapter);
            messageText = (TextView) itemView.findViewById(R.id.messageText);
        }

        void bind(Message message) {
            messageText.setText(message.getContent());
            messageText.setTextColor(message.isRead() ? Color.BLACK : Color.RED);
        }

        @Override
        public int getId() {
            return 0;
        }
    }
}
