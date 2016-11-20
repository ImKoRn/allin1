package com.korn.im.allin1.pojo;
import java.util.Comparator;

/**
 * VkDialog representation
 */
public interface Dialog {
    int getId();
    boolean isChat();
    int getUnreadCount();
    int getLastMessageId();

    Comparator<Dialog> TIME_ORDERED = (lhs, rhs) -> (int) (rhs.getLastMessageId() - lhs.getLastMessageId());
}
