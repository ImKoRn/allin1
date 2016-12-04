package com.korn.im.allin1.ui.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.korn.im.allin1.R;

public class SocialCircularImageView extends CircularImageView implements OnlineImageView {
    private static final boolean DEFAULT_ENABLE = false;
    private static final boolean DEFAULT_ENABLE_MOBILE = false;

    private static final float CONST = 0.70710678118f / 2;
    private static final float DEFAULT_RADIUS = 0;
    private float mobileBorderRadius;
    private float mobileInsideRadius;

    private boolean showOnlineMark;
    private boolean showMobileOnlineMark;

    private float mobileBorderWidth;
    private float mobileBorderHeight;
    private float mobileBorderSize;

    private final float borderRadius;
    private final float circleRadius;

    private final Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Paint insideMobilePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint borderMobilePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float dx;
    private float dy;

    private float mdx;
    private float mdy;
    private float mdxe;
    private float mdye;

    private final Path outPath = new Path();
    private final Path inPath = new Path();

    public SocialCircularImageView(Context context) {
        this(context, null);
    }

    public SocialCircularImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SocialCircularImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.SocialImageView, defStyleAttr, 0);

        showOnlineMark = attributes.getBoolean(R.styleable.SocialImageView_social_enableOnlineMark, DEFAULT_ENABLE);
        showMobileOnlineMark = attributes.getBoolean(R.styleable.SocialImageView_social_enableMobileMark, DEFAULT_ENABLE_MOBILE);

        circlePaint.setColor(attributes.getColor(R.styleable.SocialImageView_social_onlineMarkCircleColor, Color.GREEN));
        borderPaint.setColor(attributes.getColor(R.styleable.SocialImageView_social_onlineMarkBorderColor, Color.WHITE));
        insideMobilePaint.setColor(attributes.getColor(R.styleable.SocialImageView_social_onlineMobileMarkInsideColor, Color.GREEN));
        borderMobilePaint.setColor(attributes.getColor(R.styleable.SocialImageView_social_onlineMobileMarkBorderColor, Color.WHITE));

        borderRadius = attributes.getDimension(R.styleable.SocialImageView_social_onlineMarkBorderRadius,
                DEFAULT_RADIUS);
        circleRadius = attributes.getDimension(R.styleable.SocialImageView_social_onlineMarkCircleRadius,
                DEFAULT_RADIUS);
        mobileBorderWidth = attributes.getDimension(R.styleable.SocialImageView_social_onlineMobileMarkBorderWidth, DEFAULT_RADIUS);
        mobileBorderHeight = attributes.getDimension(R.styleable.SocialImageView_social_onlineMobileMarkBorderHeight, DEFAULT_RADIUS);
        mobileBorderSize = attributes.getDimension(R.styleable.SocialImageView_social_onlineMobileMarkBorderSize, DEFAULT_RADIUS);
        mobileBorderRadius = attributes.getDimension(R.styleable.SocialImageView_social_onlineMobileMarkBorderRadius, DEFAULT_RADIUS);
        mobileInsideRadius = attributes.getDimension(R.styleable.SocialImageView_social_onlineMobileMarkInsideRadius, DEFAULT_RADIUS);

        attributes.recycle();
    }

    @Override
    public void setShowOnlineMark(boolean show, boolean mobile) {
        showOnlineMark = show;
        showMobileOnlineMark = mobile;
    }

    @Override
    public ImageView getImageView() {
        return this;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        outPath.reset();
        inPath.reset();
    }

    private Path composeRoundedRectPath(Path path, RectF rect, float topLeftRadius, float topRightRadius,
                                        float bottomRightRadius, float bottomLeftRadius) {
        topLeftRadius = topLeftRadius < 0 ? 0 : topLeftRadius;
        topRightRadius = topRightRadius < 0 ? 0 : topRightRadius;
        bottomLeftRadius = bottomLeftRadius < 0 ? 0 : bottomLeftRadius;
        bottomRightRadius = bottomRightRadius < 0 ? 0 : bottomRightRadius;

        path.moveTo(rect.left + topLeftRadius/2 , rect.top);
        path.lineTo(rect.right - topRightRadius/2,rect.top);
        path.quadTo(rect.right, rect.top, rect.right, rect.top + topRightRadius/2);
        path.lineTo(rect.right ,rect.bottom - bottomRightRadius/2);
        path.quadTo(rect.right ,rect.bottom, rect.right - bottomRightRadius/2, rect.bottom);
        path.lineTo(rect.left + bottomLeftRadius/2,rect.bottom);
        path.quadTo(rect.left,rect.bottom,rect.left, rect.bottom - bottomLeftRadius/2);
        path.lineTo(rect.left,rect.top + topLeftRadius/2);
        path.quadTo(rect.left,rect.top, rect.left + topLeftRadius/2, rect.top);
        path.close();

        return path;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(showOnlineMark) {
            dx = canvasSize / 2 + canvasSize * CONST;
            dy = canvasSize / 2 + canvasSize * CONST;

            if (showMobileOnlineMark) {
                mdx = dx - mobileBorderWidth / 2;
                mdxe = mdx + mobileBorderWidth;
                mdy = dy - mobileBorderHeight / 2;
                mdye = mdy + mobileBorderHeight;
                canvas.drawPath(getOutPath(), borderMobilePaint);
                canvas.drawPath(getInPath(), insideMobilePaint);
                canvas.drawRect(mdx + mobileBorderSize * 1.5f, mdy + mobileBorderSize * 2,
                        mdxe - mobileBorderSize * 1.5f, mdye - mobileBorderSize * 2, borderMobilePaint);
            } else {
                canvas.drawCircle(dx, dy, borderRadius, borderPaint);
                canvas.drawCircle(dx, dy, circleRadius, circlePaint);
            }

        }
    }

    private Path getInPath() {
        if(inPath.isEmpty())
            return composeRoundedRectPath(
                inPath, new RectF(mdx + mobileBorderSize, mdy + mobileBorderSize,
                        mdxe - mobileBorderSize, mdye - mobileBorderSize),
                    mobileInsideRadius, mobileInsideRadius, mobileInsideRadius, mobileInsideRadius);
        return inPath;
    }

    private Path getOutPath() {
        if(outPath.isEmpty())
            return composeRoundedRectPath(outPath,
                    new RectF(mdx, mdy, mdxe, mdye),
                    mobileBorderRadius, mobileBorderRadius, mobileBorderRadius, mobileBorderRadius);
        return outPath;
    }
}
