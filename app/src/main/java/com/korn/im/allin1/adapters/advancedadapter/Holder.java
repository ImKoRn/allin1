package com.korn.im.allin1.adapters.advancedadapter;

import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public abstract class Holder extends RecyclerView.ViewHolder
        implements View.OnClickListener, View.OnLongClickListener {
    private AdvancedAdapter<? extends Holder, ? extends Holder> advancedAdapter;

    public Holder(View itemView,
                  AdvancedAdapter<? extends Holder, ? extends Holder> advancedAdapter) {
        super(itemView);
        this.advancedAdapter = advancedAdapter;
    }

    public Holder(View itemView,
                  AdvancedAdapter<? extends Holder, ? extends Holder> advancedAdapter,
                  @IdRes int id) {
        this(itemView, advancedAdapter);

        ViewGroup root = (ViewGroup) itemView.findViewById(id);
        root.setOnClickListener(this);
        root.setOnLongClickListener(this);
    }

    public abstract int getId();

    @Override
    public void onClick(View v) {
        if (advancedAdapter.itemClickListener != null)
            advancedAdapter.itemClickListener.onClick(v, getId());
    }

    @Override
    public boolean onLongClick(View v) {
        if (advancedAdapter.itemLongClickListener != null)
            advancedAdapter.itemLongClickListener.onClick(v, getId());
        return true;
    }
}
