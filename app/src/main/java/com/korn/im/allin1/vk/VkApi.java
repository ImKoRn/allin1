package com.korn.im.allin1.vk;

import android.util.Pair;

import com.korn.im.allin1.accounts.Api;
import com.korn.im.allin1.errors.NoDataException;
import com.korn.im.allin1.pojo.Dialogs;
import com.korn.im.allin1.pojo.Interlocutor;
import com.korn.im.allin1.vk.pojo.VkDialog;
import com.korn.im.allin1.vk.pojo.VkDialogs;
import com.korn.im.allin1.vk.pojo.VkMessage;
import com.korn.im.allin1.vk.pojo.VkUser;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;

import static com.korn.im.allin1.accounts.Api.Request.LOAD;
import static com.korn.im.allin1.accounts.Api.Request.RELOAD;
import static com.korn.im.allin1.accounts.Api.State.FIRST_LOADING;
import static com.korn.im.allin1.accounts.Api.State.NOTHING;

class VkApi implements Api {
    //private static final String TAG = "VkApi";

    // Core objects
    private final VkDataManager dataManager;
    private final VkDataLoader dataLoader;
    private final VkEventManager eventManager;

    // Default values
    private final int defaultDialogsCount;
    private final int defaultMessagesCount;

    // Information flags about the ability of data loading
    private volatile boolean canLoadDialogs = true;
    private Map<Integer, Boolean> canLoadMessages = new ConcurrentHashMap<>();

    // Information flags about current loading state
    private volatile State friendsLoadingState = NOTHING;
    private volatile State dialogsLoadingState = NOTHING;
    private Map<Integer, State> messagesLoadingStateMap = new ConcurrentHashMap<>();

    //----------------------------------------- Members end ---------------------------------------

    // Constructors
    VkApi(final int defaultDialogsCount, final int defaultMessagesCount) {
        this.dataManager = new VkDataManager(new VkCache(), new VkDbCache());
        this.dataLoader = new VkDataLoader();
        this.eventManager = new VkEventManager();
        this.defaultDialogsCount = defaultDialogsCount;
        this.defaultMessagesCount = defaultMessagesCount;
    }

    // Friends methods
    @Override
    public Observable<? extends Map<Integer, VkUser>> fetchFriends() {
        return dataManager.getFriends();
    }

    @Override
    public Response loadFriends(final Request request) {
        if (friendsLoadingState == NOTHING) {
            friendsLoadingState = FIRST_LOADING;
            eventManager.publishFriendsWhenArrive(dataLoader.loadFriends()
                                                            .doOnError(throwable -> friendsLoadingState = NOTHING)
                                                            .flatMap(friends -> saveFriends(friends, true))
                                                            .doOnNext(friends -> friendsLoadingState = NOTHING));
        }
        return Response.LOADING;
    }

    @Override
    public Observable<VkUser> fetchFriend(final int id) {
        return dataManager.getFriend(id);
    }

    // Dialogs methods
    @Override
    public Observable<? extends Pair<? extends Dialogs, ? extends Map<Integer, ? extends Interlocutor>>> fetchDialogs() {
        return dataManager.getDialogs();
    }

    @Override
    public Response loadDialogs(final Request request) {
        if (dialogsLoadingState == NOTHING) {
            dialogsLoadingState = request.getRequestState();

            if (request == LOAD && !canLoadDialogs()) {
                dialogsLoadingState = NOTHING;
                return Response.NOTHING_TO_LOAD;
            }

            final int count = request == RELOAD ? dataManager.getDialogsCount() : defaultDialogsCount;

            if (request == RELOAD && count == 0) {
                dialogsLoadingState = NOTHING;
                return Response.NOTHING_TO_RELOAD;
            }

            final int stamp = request == LOAD ? dataManager.getDialogsStamp() : 0;

            eventManager.publishDialogsWhenArrive(dataLoader.loadDialogs(stamp, count)
                                                            .doOnError(throwable -> dialogsLoadingState = NOTHING)
                                                            .flatMap(dialogs -> {
                                                                if (request != RELOAD)
                                                                    canLoadDialogs = (dialogs.first.getDialogsCount() == count);
                                                                return saveDialogs(dialogs, request != LOAD);
                                                            })
                                                            .doOnNext(dialogs -> dialogsLoadingState = NOTHING));
            return Response.LOADING;
        }
        return dialogsLoadingState == request.getRequestState() ? Response.LOADING : Response.BUSY;
    }

    @Override
    public Observable<VkDialog> fetchDialog(final int id) {
        return dataManager.getDialog(id);
    }

    @Override
    public Observable<VkDialog> loadDialog(final int id) {
        return dataLoader.loadDialog(id).doOnNext(dataManager::saveDialog);
    }

    // Messages methods
    @Override
    public Observable<Map<Integer, VkMessage>> fetchMessages(final int id) {
        return dataManager.getMessages(id).flatMap(messages -> Observable.just(messages.second));
    }

    @Override
    public Response loadMessages(final int id, final Request request) {
        if (getMessagesLoadingState(id) == NOTHING) {
            messagesLoadingStateMap.put(id, request.getRequestState());
            if (request == LOAD && !canLoadMessages(id)) {
                messagesLoadingStateMap.put(id, NOTHING);
                return Response.NOTHING_TO_LOAD;
            }

            final int count = request == RELOAD ? dataManager.getMessagesCount(id) : defaultMessagesCount;

            if (request == RELOAD && count == 0) {
                messagesLoadingStateMap.put(id, NOTHING);
                return Response.NOTHING_TO_RELOAD;
            }

            final int stamp = request == LOAD ? dataManager.getMessagesStamp(id) : 0;
            eventManager.publishMessagesWhenArrive(dataLoader.loadMessages(id,
                                                                           count,
                                                                           stamp)
                                                             .doOnError(throwable -> messagesLoadingStateMap.put(id, NOTHING))
                                                             .flatMap(messages -> {
                                                                 if (request != RELOAD)
                                                                     canLoadMessages.put(id,
                                                                                         messages.second.size() == count);
                                                                 return saveMessages(messages, request != LOAD);
                                                             })
                                                             .doOnNext(messages -> messagesLoadingStateMap.put(id, NOTHING)));
            return Response.LOADING;
        }
        return getMessagesLoadingState(id) == request.getRequestState() ? Response.LOADING : Response.BUSY;
    }

    // Interlocutors methods
    @Override
    public Observable<Interlocutor> fetchInterlocutor(int id) {
        return dataManager.getInterlocutor(id);
    }

    @Override
    public Observable<? extends Interlocutor> loadInterlocutor(int id) {
        return dataLoader.loadUsers(id)
                         .flatMap(this::saveInterlocutors)
                         .flatMap(integerMap -> {
                             Interlocutor interlocutor = integerMap.get(id);
                             if (interlocutor == null) return Observable.error(new NoDataException());
                             else return Observable.just(interlocutor);
                         });
    }

    @Override
    public Observable<Map<Integer, ? extends Interlocutor>> loadInterlocutors(int... id) {
        return dataLoader.loadUsers(id).flatMap(this::saveInterlocutors);
    }

    @Override
    public Observable<Map<Integer, Interlocutor>> fetchInterlocutors() {
        return dataManager.getInterlocutors();
    }

    // Events
    @Override
    public VkEventManager getEventsManager() {
        return eventManager;
    }

    // Check if can load data methods
    @Override
    public boolean canLoadDialogs() {
        return canLoadDialogs;
    }

    @Override
    public boolean canLoadFriends() {
        return false;
    }

    @Override
    public boolean canLoadMessages(int id) {
        Boolean b = canLoadMessages.get(id);
        if (b == null) return true;
        return b;
    }

    // Check if data loading now
    @Override
    public State getFriendsLoadingState() {
        return friendsLoadingState;
    }

    @Override
    public State getDialogsLoadingState() {
        return dialogsLoadingState;
    }

    @Override
    public State getMessagesLoadingState(int id) {
        State s = messagesLoadingStateMap.get(id);
        if (s == null) return NOTHING;
        return s;
    }

    // Save data methods
    private Observable<? extends Map<Integer, VkUser>> saveFriends(Map<Integer, VkUser> users, boolean rewrite) {
        if (users.size() > 0)
            dataManager.saveFriends(users, rewrite);
        return dataManager.getFriends();
    }

    private Observable<Pair<VkDialogs, Map<Integer, Interlocutor>>>
    saveDialogs(Pair<VkDialogs, Map<Integer, Interlocutor>> dialogs, boolean rewrite) {
        if (dialogs.first.getDialogsCount() > 0)
            dataManager.saveDialogs(dialogs, rewrite);
        return dataManager.getDialogs();
    }

    private Observable<Map<Integer, Interlocutor>> saveInterlocutors(Map<Integer, ? extends Interlocutor> interlocutors) {
        if (interlocutors.size() > 0) dataManager.saveInterlocutors(interlocutors, false);
        return dataManager.getInterlocutors();
    }

    private Observable<Pair<Integer, Map<Integer, VkMessage>>> saveMessages(Pair<Integer, Map<Integer, VkMessage>> messages,
                                                                            boolean rewrite) {
        if (messages.second.size() > 0) dataManager.saveMessages(messages.first, messages.second, rewrite);
        return dataManager.getMessages(messages.first);
    }
}
