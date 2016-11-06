package com.korn.im.allin1.adapters;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.korn.im.allin1.R;
import com.korn.im.allin1.ui.fragments.FriendsFragment;
import com.korn.im.allin1.ui.fragments.DialogsFragment;
import com.korn.im.allin1.ui.fragments.OnlineFriendsFragment;

/**
 * Created by korn on 03.08.16.
 */
public class PagerAdapter extends FragmentPagerAdapter {
    private static final int FRIENDS_POSITION = 0;
    private static final int ONLINE_FRIENDS_POSITION = 1;
    private static final int DIALOGS_POSITION = 2;

    public static final int ITEM_COUNT = 3;


    private String friendsSection;
    private String onlineFriendsSection;
    private String messagesSection;

    public PagerAdapter(Activity activity, FragmentManager fm) {
        super(fm);
        friendsSection = activity.getResources().getString(R.string.friends_section_title);
        onlineFriendsSection = activity.getResources().getString(R.string.online_friends_section_title);
        messagesSection = activity.getResources().getString(R.string.messages_section_title);
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case FRIENDS_POSITION : {
                return new FriendsFragment();
            }
            case ONLINE_FRIENDS_POSITION : {
                return new OnlineFriendsFragment();
            }
            case DIALOGS_POSITION : {
                return new DialogsFragment();
            }
        }

        return null;
    }

    @Override
    public int getCount() {
        return ITEM_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case FRIENDS_POSITION:
                return friendsSection;
            case ONLINE_FRIENDS_POSITION:
                return onlineFriendsSection;
            case DIALOGS_POSITION:
                return messagesSection;
        }
        return null;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return super.isViewFromObject(view, object);
    }
}
