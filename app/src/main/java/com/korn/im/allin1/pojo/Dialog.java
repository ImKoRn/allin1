package com.korn.im.allin1.pojo;
import android.os.Parcelable;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * Created by korn on 26.08.16.
 */
public interface Dialog extends Parcelable {
    int getId();

    boolean isChat();

    <T extends Message> List<T> getMessages();

    <T extends Message> T getMessage(int id);

    int getUnreadCount();

    long getDate();

    boolean isHasNextMessages();

    Comparator<Dialog> TIME_ORDERED = new Comparator<Dialog>() {
        @Override
        public int compare(Dialog lhs, Dialog rhs) {
            return (int) (rhs.getDate() - lhs.getDate());
        }
    };

    Comparator<Dialog> FIRST_UNREAD_TIME_ORDERED = new Comparator<Dialog>() {
        @Override
        public int compare(Dialog lhs, Dialog rhs) {
            if(lhs.getUnreadCount() == 0)
                if(rhs.getUnreadCount() == 0)
                    return TIME_ORDERED.compare(lhs, rhs);
                else return -1;
            else if(rhs.getUnreadCount() == 0)
                return 1;
            else return TIME_ORDERED.compare(lhs, rhs);
        }
    };
}
