package com.korn.im.allin1.ui.controlers;

import android.animation.Animator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.korn.im.allin1.R;
import com.korn.im.allin1.pojo.Interlocutor;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class VkLoginViewController extends Controller {
    private static final String USER_STATE = "user";

    private Button vkLogInOutBtn;
    private Button vkSignInBtn;
    private TextView vkUserNameTextView;
    private ImageView vkImage;

    private static String logIn;
    private static String logOut;

    private Interlocutor user = null;

    public VkLoginViewController(Activity activity) {
        super(activity);
        if(logIn == null)
            logIn = activity.getResources().getString(R.string.log_in);
        if (logOut == null) logOut = activity.getResources().getString(R.string.log_out);
    }

    @Override
    public void restoreInstance(Bundle savedInstanceState) {
    }

    @Override
    public void saveInstance(Bundle outInstanceState) {

    }

    @Override
    public void create() {
        vkLogInOutBtn = (Button) getView(R.id.vkLogInOutBtn);
        vkSignInBtn = (Button) getView(R.id.vkSignInBtn);
        vkUserNameTextView = (TextView) getView(R.id.vkUserNameTextView);
        vkImage = (ImageView) getView(R.id.vkImage);
    }

    @Override
    public void prepare() {
    }

    @Override
    public void update() {
        if(user != null) {
            vkUserNameTextView.setText(user.getFullName());
            vkUserNameTextView.setVisibility(View.VISIBLE);
            vkSignInBtn.setVisibility(View.INVISIBLE);
            vkLogInOutBtn.setText(logOut);
            setImage(user.getBigImage());
        } else {
            vkUserNameTextView.setVisibility(View.INVISIBLE);
            vkSignInBtn.setVisibility(View.VISIBLE);
            vkLogInOutBtn.setText(logIn);
            setImage(R.drawable.vk_logo);
        }
    }

    @Override
    public void release() {

    }

    @Override
    public void destroy() {
        vkLogInOutBtn = null;
        vkSignInBtn = null;
        vkUserNameTextView = null;
        vkImage = null;
    }

    public Button getVkLogInOutBtn() {
        return vkLogInOutBtn;
    }

    public Button getVkSignInBtn() {
        return vkSignInBtn;
    }

    private void setImage(@DrawableRes int resId) {
        if(vkImage != null)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && vkImage.isAttachedToWindow()) {
            float w = vkImage.getWidth() / 2;
            float h = vkImage.getHeight() / 2;
            Animator anim = ViewAnimationUtils.createCircularReveal(vkImage, (int) w, (int) h
                    , 0, Math.max(w, h));
            vkImage.setImageResource(resId);
            anim.start();
        } else vkImage.setImageResource(resId);
    }

    public void setImage(String imageLink) {
        ImageLoader.getInstance().loadImage(imageLink, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if(vkImage != null)
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && vkImage.isAttachedToWindow()) {
                    float w = vkImage.getWidth() / 2;
                    float h = vkImage.getHeight() / 2;
                    Animator anim = ViewAnimationUtils.createCircularReveal(vkImage, (int) w, (int) h
                            , 0, Math.max(w, h));
                    vkImage.setImageBitmap(loadedImage);
                    anim.start();
                } else vkImage.setImageBitmap(loadedImage);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }

    public void setUser(Interlocutor user) {
        this.user = user;
    }
}
