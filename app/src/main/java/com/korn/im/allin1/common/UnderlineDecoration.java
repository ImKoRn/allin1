package com.korn.im.allin1.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.ColorRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.korn.im.allin1.R;

/**
 * Created by korn on 04.08.16.
 */
public class UnderlineDecoration extends RecyclerView.ItemDecoration {
    private ColorDrawable divider = new ColorDrawable(Color.BLACK);
    private boolean draw = false;
    private int startViewId;
    private int endViewId;

    public UnderlineDecoration(Context context, int startViewId, int endViewId, @ColorRes int color) {
        divider.setColor(context.getResources().getColor(color));
        this.startViewId = startViewId;
        this.endViewId = endViewId;
        draw = true;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        int dividerLeft;
        int dividerRight;
        int dividerTop;
        int dividerBottom;

        int childCount = parent.getChildCount();
        View child;
        View inChild;
        for (int i = 0; i < childCount - 1; i++) {
            child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            dividerTop = child.getBottom() + params.bottomMargin;
            dividerBottom = dividerTop + 1;

            inChild = child.findViewById(startViewId);
            dividerRight = dividerLeft = inChild.getLeft() + inChild.getPaddingLeft();
            inChild = child.findViewById(endViewId);
            dividerRight += inChild.getWidth() - inChild.getPaddingRight();
            divider.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom);
            divider.draw(c);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if(parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1)
            return;

        outRect.bottom = 1;
    }
}
