package com.korn.im.allin1.accounts;

import com.korn.im.allin1.pojo.Message;

/**
 * Created by korn on 04.08.16.
 */
public interface Api {
    void cachedFriends();
    void updateFriends();
    void nextFriends();

    void cachedDialogs();
    void updateDialogs();
    void nextDialogs();

    void sendMessage(int interlocutorId, Message message);
    void nextMessages(int interlocutorId);
}
