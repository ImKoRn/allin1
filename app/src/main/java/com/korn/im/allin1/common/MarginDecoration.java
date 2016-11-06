package com.korn.im.allin1.common;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by korn on 04.08.16.
 */
public class MarginDecoration extends RecyclerView.ItemDecoration {
    private float margin;
    private Context context;
    private boolean onlyMiddle;

    public MarginDecoration(Context context, @DimenRes int margin, boolean onlyMiddle) {
        this(context, context.getResources().getDimension(margin), onlyMiddle);
    }

    public MarginDecoration(Context context, float margin, boolean onlyMiddle) {
        this.context = context;
        this.margin = margin;
        this.onlyMiddle = onlyMiddle;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (onlyMiddle) {
            if(parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1)
                return;
        } else {
            if(parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1)
                outRect.top = (int) margin;
        }

        outRect.bottom = (int) margin;
    }
}
