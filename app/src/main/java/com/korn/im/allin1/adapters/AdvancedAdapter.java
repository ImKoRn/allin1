package com.korn.im.allin1.adapters;

import android.support.annotation.IdRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.korn.im.allin1.R;

/**
 * Created by korn on 09.09.16.
 */
public abstract class AdvancedAdapter
        extends RecyclerView.Adapter<AdvancedAdapter.Holder> {
    protected static final int LOAD_MORE_VIEW = -1;

    protected OnItemClickListener itemClickListener;
    protected OnItemClickListener itemLongClickListener;
    private OnNeedMoreListener onNeedMoreListener;
    private boolean hasMore = false;
    private boolean notifiedAboutLoading = false;

    public AdvancedAdapter(RecyclerView recyclerView, final LinearLayoutManager layoutManager) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (onNeedMoreListener != null && hasMore && !notifiedAboutLoading &&
                        layoutManager.findLastVisibleItemPosition() >= getActualItemCount() - 1) {
                    onNeedMoreListener.onNeedMore();
                    notifiedAboutLoading = true;
                }
            }
        });
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setItemLongClickListener(OnItemClickListener itemLongClickListener) {
        this.itemLongClickListener = itemLongClickListener;
    }

    public void setHasMore(boolean hasMore) {
        if(this.hasMore = hasMore) notifyItemInserted(getItemCount());
        else notifyItemRemoved(getItemCount());
    }

    public void setOnNeedMoreListener(OnNeedMoreListener onNeedMoreListener) {
        this.onNeedMoreListener = onNeedMoreListener;
    }

    public abstract int getActualItemCount();

    public void setNotifyAboutLoading(boolean notifyAboutLoading) {
        this.notifiedAboutLoading = !notifyAboutLoading;
    }

    @Override
    public int getItemViewType(int position) {
        return position == getActualItemCount() ? LOAD_MORE_VIEW : 0;
    }

    @Override
    public int getItemCount() {
        return getActualItemCount() + (hasMore ? 1 : 0);
    }

    public boolean isHasMoreObjects() {
        return hasMore;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return viewType == LOAD_MORE_VIEW ? createOnLoadingHolder(parent) : null;
    }

    public class Holder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {
        private ViewGroup root;

        public Holder(View itemView) {
            super(itemView);
        }

        public Holder(View itemView, @IdRes int id) {
            this(itemView);
            root = (ViewGroup) itemView.findViewById(id);

            root.setOnClickListener(this);
            root.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null)
                itemClickListener.onClick(v, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            if (itemLongClickListener != null)
                itemLongClickListener.onClick(v, getAdapterPosition());
            return true;
        }
    }

    public abstract Holder createOnLoadingHolder(ViewGroup parent);

    public interface OnItemClickListener {
        void onClick(View v, int id);
    }

    public interface OnNeedMoreListener {
        void onNeedMore();
    }
}
