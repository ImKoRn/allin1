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
import com.korn.im.allin1.pojo.Message;

import java.util.Collection;

/**
 * Created by korn on 03.09.16.
 */
public class MessagesAdapter extends AdvancedAdapter {
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
    public int getItemViewType(int position) {
        int type = super.getItemViewType(position);

        if (type != 0) return type;

        return data.get(position).isOut()? OUTGOING : INCOMING;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        Holder holder = super.onCreateViewHolder(parent, viewType);
        if (holder != null) return holder;

        View view = LayoutInflater.from(context).inflate(viewType == INCOMING?
                R.layout.incoming_message_item : R.layout.outcoming_message_item, parent, false);

        return new MessageHolder(view);
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
    public void onBindViewHolder(Holder holder, int position) {
        if(position == getActualItemCount()) return;

        ((MessageHolder) holder).bind(data.get(position));
    }

    public void setData(Collection<? extends Message> messages) {
        data.beginBatchedUpdates();
        for (Message message : messages)
            data.add(message);
        data.endBatchedUpdates();
    }

    public void clear() {
        data.clear();
    }

    public class MessageHolder extends Holder {
        private TextView messageText;
        public MessageHolder(View itemView) {
            super(itemView);
            messageText = (TextView) itemView.findViewById(R.id.messageText);
        }

        public void bind(Message message) {
            messageText.setText(message.getContent());
            messageText.setTextColor(message.isRead() ? Color.BLACK : Color.RED);
        }
    }
}
