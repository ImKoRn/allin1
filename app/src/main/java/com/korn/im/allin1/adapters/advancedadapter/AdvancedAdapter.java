package com.korn.im.allin1.adapters.advancedadapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public abstract class AdvancedAdapter<THolder extends Holder, TOnLoadHolder extends Holder>
        extends RecyclerView.Adapter<Holder> {
    private static final int LOAD_MORE_VIEW = -1;

    OnItemClickListener itemClickListener;
    OnItemClickListener itemLongClickListener;
    private OnNeedMoreListener onNeedMoreListener;
    private boolean canLoadData = false;
    private boolean notifiedAboutLoading = false;

    public AdvancedAdapter(RecyclerView recyclerView, final LinearLayoutManager layoutManager) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (onNeedMoreListener != null && canLoadData && !notifiedAboutLoading &&
                        layoutManager.findLastVisibleItemPosition() >= getActualItemCount() - 1) {
                    onNeedMoreListener.onNeedMore();
                    notifiedAboutLoading = true;
                }
            }
        });
    }

    @Override
    public final int getItemViewType(int position) {
        return position == getActualItemCount() ? LOAD_MORE_VIEW : whatIsItemViewType(position);
    }

    @Override
    public final int getItemCount() {
        return getActualItemCount() + (canLoadData ? 1 : 0);
    }

    /**
     * If {@link #notifyAboutLoading()} = true, {@link #canLoadData()} = true and viewType =
     * */
    @Override
    public final Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return viewType == LOAD_MORE_VIEW ? createOnLoadingHolder(parent) : createHolder(parent, viewType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void onBindViewHolder(Holder holder,
                                 int position) {
        if (getActualItemCount() == position)
            onBindOnLoadingHolder((TOnLoadHolder) holder);
        else onBindHolder((THolder) holder, position);
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setItemLongClickListener(OnItemClickListener itemLongClickListener) {
        this.itemLongClickListener = itemLongClickListener;
    }

    public void setCanLoadData(boolean canLoadData) {
        if(this.canLoadData = canLoadData) notifyItemInserted(getItemCount());
        else notifyItemRemoved(getItemCount());
    }

    public void setOnNeedMoreListener(OnNeedMoreListener onNeedMoreListener) {
        this.onNeedMoreListener = onNeedMoreListener;
    }

    public void setNotifyAboutLoading(boolean notifyAboutLoading) {
        this.notifiedAboutLoading = !notifyAboutLoading;
    }

    public boolean notifyAboutLoading() {
        return !this.notifiedAboutLoading;
    }

    public boolean canLoadData() {
        return canLoadData;
    }

    protected void onBindOnLoadingHolder(TOnLoadHolder holder) {}

    protected void onBindHolder(THolder holder,
                                int position) {}

    /**
     * @return actual adapter item count
     * */
    public abstract int getActualItemCount();

    /**
    * @return int what describe viewType used for {@link #createHolder(ViewGroup, int)}
    * */
    protected int whatIsItemViewType(int position) {
        return 0;
    }

    /**
     * @return THolder for adapter
     * */
    protected abstract THolder createHolder(ViewGroup parent, int viewType);

    protected abstract TOnLoadHolder createOnLoadingHolder(ViewGroup parent);

    public interface OnItemClickListener {
        void onClick(View view, int id);
    }

    public interface OnNeedMoreListener {
        void onNeedMore();
    }
}
