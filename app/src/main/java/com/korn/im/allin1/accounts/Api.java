package com.korn.im.allin1.accounts;

import android.util.Pair;

import com.korn.im.allin1.pojo.Dialog;
import com.korn.im.allin1.pojo.Dialogs;
import com.korn.im.allin1.pojo.Interlocutor;
import com.korn.im.allin1.pojo.Message;
import com.korn.im.allin1.pojo.User;
import com.korn.im.allin1.vk.pojo.VkUser;

import java.util.Map;

import rx.Observable;

/**
* The interface describes way to async getting information
* and checking of existing information
* */
public interface Api<
        TMessage extends Message,
        TUser extends User,
        TDialogs extends Dialogs<TDialog, TMessage>,
        TDialog extends Dialog,
        TInterlocutor extends Interlocutor> {
    /**
     * Fetch friends from cache or database
     * @return Observable with Map of {@link User}'s mapped by {@link User#getId()}
     * @see #loadFriends()
     * @see #fetchFriend(int)
     * */
    Observable<? extends Map<Integer, VkUser>> fetchFriends();
    /**
     * Load friends from Ethernet and deliver result by {@link DataPublisher#friendsObservable()}
     * Do not send a new request is not executed until the previous.
     * @see #isFriendsLoading()
     * @see #fetchFriends()
     * @see #fetchFriend(int)
     * */
    void loadFriends();

    /**
     * Fetch friend from cache or database if not exist load it from Internet
     * @return Observable with {@link User}
     * @see #loadFriends()
     * @see #fetchFriends()
     * */
    Observable<TUser> fetchFriend(int id);

    /**
     * Fetch dialogs from cache or database
     * @return Observable with Object extends {@link Dialogs}
     * */
    Observable<Pair<TDialogs, ? extends Map<Integer, ? extends TInterlocutor>>> fetchDialogs();

    /**
     * Load dialogs from Internet
     * @return Observable with Pair of Object extends {@link Dialogs} and Map of {@link Interlocutor} objects
     * */
    void loadDialogs();

    void loadNextDialogs();

    /**
     * Fetch dialog from cache or database if not exist load it from Internet
     * @return Observable with Object extends {@link Dialog}
     * */
    Observable<TDialog> fetchDialog(int id);

    Observable<TDialog> loadDialog(int id);

    /**
     * Fetch interlocutor from cache or database if not exist load it from Internet
     * @return Observable with Object extends {@link Interlocutor}
     * */
    Observable<TInterlocutor> fetchInterlocutor(int id);

    /**
     * Check if exist next page of dialogs
     * @see Dialogs
     * @return true if next page of dialogs exist
     * */
    boolean hasMoreDialogsToUpdate();

    /**
     * Check if exist next page of friends
     * @see User
     * @return true if next page of friends exist
     * */
    boolean hasMoreFriendsToUpdate();

    /**
     * Check if exist next page of messages
     * @value id - dialog id of messages to check
     * @see Message
     * @return true if next page of messages exist
     * */
    boolean hasMoreMessagesToUpdate(int id);

    DataPublisher<
            ? extends Message,
            ? extends User,
            ? extends Dialogs,
            ? extends Dialog,
            ? extends Interlocutor> getDataPublisher();

    boolean isFriendsLoading();
}
