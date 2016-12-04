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
     * @see #loadFriends(Request)
     * @see #fetchFriend(int)
     * */
    Observable<? extends Map<Integer, ? extends User>> fetchFriends();
    /**
     * Load friends from Ethernet and deliver result by {@link EventManager#friendsObservable()}
     * Do not send a new request is not executed until the previous.
     * @see #getFriendsLoadingState()
     * @see #fetchFriends()
     * @see #fetchFriend(int)
     * */
    Response loadFriends(Request request);

    /**
     * Fetch friend from cache or database if not exist load it from Internet
     * @return {@link Observable} with {@link User}
     * @see #loadFriends(Request)
     * @see #fetchFriends()
     * */
    Observable<? extends User> fetchFriend(int id);

    /**
     * Fetch dialogs from cache or database
     * <p>
     * If data not exist {@link rx.Subscriber#onError(Throwable)}
     * with {@link com.korn.im.allin1.errors.NoDataException} will be called
     * */
    Observable<? extends Pair<? extends Dialogs, ? extends Map<Integer, ? extends Interlocutor>>> fetchDialogs();

    /**
     * Load dialogs from Internet and sends result via {@link EventManager#dialogsObservable()}
     * {@link Observable} with {@link Pair} of Objects extends {@link Dialogs} and {@link Map} of {@link Interlocutor} objects
     * */
    Response loadDialogs(Request request);

    /**
     * Load dialogs after last dialog in cache from Internet and sends result via {@link EventManager#dialogsObservable()}
     * {@link Observable} with {@link Pair} of Objects extends {@link Dialogs} and {@link Map} of {@link Interlocutor} objects
     *
    void loadNextDialogs();*/

    /**
     * Fetch dialog from cache or database
     * <p>
     * If data not exist {@link rx.Subscriber#onError(Throwable)}
     * with {@link com.korn.im.allin1.errors.NoDataException} will be called
     * */
    Observable<? extends Dialog> fetchDialog(int id);

    Observable<? extends Dialog> loadDialog(int id);

    /**
     * Fetch dialog from cache or database if not exist load it from Internet
     * <p>
     * If data not exist {@link rx.Subscriber#onError(Throwable)}
     * with {@link com.korn.im.allin1.errors.NoDataException} will be called
     * */
    Observable<? extends Map<Integer, ? extends Message>> fetchMessages(int id);

    /**
     * Load messages from Internet and sends result via {@link EventManager#messagesObservable()}
     * {@link Observable} with {@link Pair} of interlocutor id and {@link Map} of {@link Message} by their id
     * */
    Response loadMessages(int id, Request request);

    /**
     * Fetch interlocutor from cache or database
     * <p>
     * If data not exist {@link rx.Subscriber#onError(Throwable)}
     * with {@link com.korn.im.allin1.errors.NoDataException} will be called
     * */
    Observable<? extends Interlocutor> fetchInterlocutor(int id);

    Observable<Map<Integer, Interlocutor>> fetchInterlocutors();

    Observable<? extends Interlocutor> loadInterlocutor(int id);

    Observable<Map<Integer, ? extends Interlocutor>> loadInterlocutors(int... id);


    EventManager<
                ? extends Message,
                ? extends User,
                ? extends Dialogs,
                ? extends Dialog,
                ? extends Interlocutor> getEventsManager();

    /**
     * Check if exist next page of dialogs
     * @see Dialogs
     * @return true if next page of dialogs exist
     * */
    boolean canLoadDialogs();

    /**
     * Check if exist next page of friends
     * @see User
     * @return true if next page of friends exist
     * */
    boolean canLoadFriends();

    /**
     * Check if exist next page of messages
     * @value id - dialog id of messages to check
     * @see Message
     * @return true if next page of messages exist
     * */
    boolean canLoadMessages(int id);

    /**
     * Check {@link #loadFriends(Request)} request state
     * @return {@link State#NOTHING} - if idle <p>
     *         {@link State#LOADING } - if loading next page of data <p>
     *         {@link State#RELOADING} - if reloading data
     * */
    State getFriendsLoadingState();

    /**
     * Check {@link #loadDialogs(Request)} request state
     * @return {@link State#NOTHING} - if idle <p>
     *         {@link State#LOADING } - if loading next page of data <p>
     *         {@link State#RELOADING} - if reloading data
     * */
    State getDialogsLoadingState();

    /**
     * Check {@link #loadMessages(int, Request)} request state
     * @return {@link State#NOTHING} - if idle <p>
     *         {@link State#LOADING } - if loading next page of data <p>
     *         {@link State#RELOADING} - if reloading data
     * */
    State getMessagesLoadingState(int id);

    enum State {
        NOTHING,
        FIRST_LOADING,
        LOADING,
        RELOADING
    }

    enum Request {
        LOAD_FIRST {
            @Override
            public State getRequestState() {
                return State.FIRST_LOADING;
            }
        },
        LOAD {
            @Override
            public State getRequestState() {
                return State.LOADING;
            }
        },
        RELOAD {
            @Override
            public State getRequestState() {
                return State.RELOADING;
            }
        };

        public abstract State getRequestState();
    }

    enum Response {
        LOADING,
        BUSY,
        NOTHING_TO_LOAD,
        NOTHING_TO_RELOAD
    }
}
