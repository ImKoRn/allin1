package com.korn.im.allin1.accounts;

import android.util.Pair;

import com.korn.im.allin1.pojo.Dialog;
import com.korn.im.allin1.pojo.Dialogs;
import com.korn.im.allin1.pojo.Interlocutor;
import com.korn.im.allin1.pojo.Message;
import com.korn.im.allin1.pojo.User;

import java.util.Map;

import rx.Observable;

/**
* The interface describes way to async getting information
* and checking of existing information
* */
//TODO add comments

public interface Api {
    /**
     * Fetch friends from cache or database
     * <p>
     * If data not exist {@link rx.Subscriber#onError(Throwable)}
     * with {@link com.korn.im.allin1.errors.NoDataException} will be called
     *
     * @return Observable with Map of {@link User}'s mapped by {@link User#getId()}
     *
     * @see #loadFriends()
     * @see #fetchFriend(int)
     * */
    Observable<? extends Map<Integer, ? extends User>> fetchFriends();
    /**
     * Load friends from Ethernet and deliver result by {@link DataPublisher#friendsObservable()}
     * Do not send a new request is not executed until the previous.
     * @see #isFriendsLoading()
     * @see #fetchFriends()
     * @see #fetchFriend(int)
     * */
    void loadFriends();

    boolean isFriendsLoading();

    /**
     * Fetch friend from cache or database if not exist load it from Internet
     * @return {@link Observable} with {@link User}
     * @see #loadFriends()
     * @see #fetchFriends()
     * */
    Observable<? extends User> fetchFriend(int id);

    /**
     * Fetch dialogs from cache or database
     * <p>
     * If data not exist {@link rx.Subscriber#onError(Throwable)}
     * with {@link com.korn.im.allin1.errors.NoDataException} will be called
     *
     * @return {@link Observable} with Object extends {@link Dialogs}
     * */
    Observable<? extends Pair<? extends Dialogs, ? extends Map<Integer, ? extends Interlocutor>>> fetchDialogs();

    /**
     * Load dialogs from Internet and sends result via {@link DataPublisher#dialogsObservable()}
     * {@link Observable} with {@link Pair} of Objects extends {@link Dialogs} and {@link Map} of {@link Interlocutor} objects
     * */
    void loadDialogs();

    void loadNextDialogs();

    /**
     * Fetch dialog from cache or database if not exist load it from Internet
     * @return {@link Observable} with Object extends {@link Dialog}
     * */
    Observable<? extends Dialog> fetchDialog(int id);

    Observable<? extends Dialog> loadDialog(int id);

    Observable<? extends Map<Integer, ? extends Message>> fetchMessages(int id);

    /**
     * Fetch interlocutor from cache or database if not exist load it from Internet
     * @return {@link Observable} with Object extends {@link Interlocutor}
     * */
    Observable<? extends Interlocutor> fetchInterlocutor(int id);

    Observable<Map<Integer, Interlocutor>> fetchInterlocutors();

    Observable<? extends Interlocutor> loadInterlocutor(int id);

    Observable<Map<Integer, ? extends Interlocutor>> loadInterlocutors(int... id);


    DataPublisher<? extends User,
            ? extends Dialogs,
            ? extends Dialog,
            ? extends Interlocutor> getDataPublisher();

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
}
