package com.korn.im.allin1.ui.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.korn.im.allin1.R;

public class SocialImageView extends ImageView implements OnlineImageView {
    private static final boolean DEFAULT_ENABLE = false;
    private static final boolean DEFAULT_ENABLE_MOBILE = false;
    private static final float DEFAULT_RADIUS = 0;

    private final float circleRadius;
    private final float borderRadius;

    private final Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private boolean showOnlineMark;
    private boolean showMobileOnlineMark;

    public SocialImageView(Context context) {
        this(context, null);
    }

    public SocialImageView(Context context,
                           AttributeSet attrs) {
        this(context,
              attrs, 0);
    }

    public SocialImageView(Context context,
                           AttributeSet attrs,
                           int defStyleAttr) {
        super(context,
              attrs,
              defStyleAttr);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.SocialImageView, defStyleAttr, 0);

        showOnlineMark = attributes.getBoolean(R.styleable.SocialImageView_social_enableOnlineMark, DEFAULT_ENABLE);
        showMobileOnlineMark = attributes.getBoolean(R.styleable.SocialImageView_social_enableMobileMark, DEFAULT_ENABLE_MOBILE);

        circlePaint.setColor(attributes.getColor(R.styleable.SocialImageView_social_onlineMarkCircleColor, Color.GREEN));
        borderPaint.setColor(attributes.getColor(R.styleable.SocialImageView_social_onlineMarkBorderColor, Color.WHITE));


        borderRadius = attributes.getDimension(R.styleable.SocialImageView_social_onlineMarkBorderRadius,
                                               DEFAULT_RADIUS);
        circleRadius = attributes.getDimension(R.styleable.SocialImageView_social_onlineMarkCircleRadius,
                                               DEFAULT_RADIUS);
         attributes.recycle();

        if (borderRadius < circleRadius) throw new IllegalArgumentException("Border can't be less then circle");

    }

    @Override
    public void setShowOnlineMark(boolean show,
                                  boolean mobile) {
        showOnlineMark = show;
        showMobileOnlineMark = mobile;
    }

    @Override
    public ImageView getImageView() {
        return this;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int canvasSize = 0;
        if (!isInEditMode()) {
            canvasSize = canvas.getWidth();
            if (canvas.getHeight() > canvasSize) {
                canvasSize = canvas.getHeight();
            }
        }

        if(showOnlineMark) {
            float d = canvasSize - borderRadius;
            canvas.drawCircle(d, d, borderRadius, borderPaint);
            canvas.drawCircle(d, d, circleRadius, circlePaint);
        }
    }
}
