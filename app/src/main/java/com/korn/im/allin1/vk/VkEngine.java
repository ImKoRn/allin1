/*
package com.korn.im.allin1.vk;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.Pair;
import com.korn.im.allin1.accounts.Api;
import com.korn.im.allin1.accounts.Event;
import com.korn.im.allin1.accounts.Events;
import com.korn.im.allin1.pojo.Dialog;
import com.korn.im.allin1.pojo.Message;
import com.korn.im.allin1.pojo.User;
import com.korn.im.allin1.services.VkLongPullService;
import com.korn.im.allin1.vk.pojo.VkDialog;
import com.korn.im.allin1.vk.pojo.VkDialogs;
import com.korn.im.allin1.vk.pojo.VkInterlocutor;
import com.korn.im.allin1.vk.pojo.VkMessage;
import com.korn.im.allin1.vk.pojo.VkUser;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

*/
/**
 * Created by korn on 04.08.16.
 *//*

public class VkEngine implements Api, Events, VkLongPullService.OnServiceStopListener {
    private VKAccessToken token;

    private Subscription longPullSubscription;
    private VkLongPullService longPullService;

    //Api updates
    private PublishSubject<Event> dataEvents = PublishSubject.create();

    // Long pull updates
    private PublishSubject<Set<Integer>> typingEvents = PublishSubject.create();
    private PublishSubject<Set<Integer>> friendsStatusEvents = PublishSubject.create();
    private PublishSubject<Set<Integer>> newMessagesEvents = PublishSubject.create();
    private PublishSubject<Map<Integer, List<Pair<Integer, Integer>>>> messageFlagEvents = PublishSubject.create();

    private final VkDataManager dataManager = new VkDataManager();

    //-------------------------------------------------------------------------------------------------------
    public VkEngine(VKAccessToken token) {
        this.token = token;
    }

    @Override
    public Observable<Event> onDataEvents() {
        if (dataEvents.hasThrowable())
            dataEvents = PublishSubject.create();

        return dataEvents.asObservable();
    }

    @Override
    public Observable<Set<Integer>> onTypingEvent() {
        return typingEvents.asObservable();
    }

    @Override
    public Observable<Set<Integer>> onFriendsStatusChangedEvent() {
        return friendsStatusEvents.asObservable();
    }

    @Override
    public Observable<Set<Integer>> onNewMessagesArrivedEvent() {
        return newMessagesEvents.asObservable();
    }

    @Override
    public Observable<Map<Integer, List<Pair<Integer, Integer>>>> onMessagesFlagsChangedEvent() {
        return messageFlagEvents.asObservable();
    }

    @Override
    public void nextDialogs() {
        fetchDialogs(dataManager.getDialogs().getDialogs().size(), VkRequestUtil.DEFAULT_DIALOGS_COUNT,
                Event.REQUEST_TYPE_LOAD_NEXT);
    }

    public void updateDialogs() {
        fetchDialogs(0, dataManager.getDialogs().getDialogs().size() == 0 ?
                VkRequestUtil.DEFAULT_DIALOGS_COUNT : dataManager.getDialogs().getDialogs().size(),
                Event.REQUEST_TYPE_UPDATE);
    }

    @SuppressWarnings("unchecked")
    public void fetchDialogs(int offset, final int count, final int requestType) {
        VKRequest request = VkRequestUtil.createDialogsRequest(offset, count);
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(final VKResponse response) {
                super.onComplete(response);

                VkDialogs dialogs = (VkDialogs) response.parsedModel;

                dataManager.addDialogs(dialogs, requestType == Event.REQUEST_TYPE_UPDATE);

                if(requestType == Event.REQUEST_TYPE_LOAD_NEXT)
                     dataManager.setHaveMoreDialogsToUpdate(dialogs.getDialogs().size() == count);

                dataEvents.onNext(new Event(
                        requestType,
                        Event.REQUEST_DATA_TYPE_DIALOGS,
                        VkAccount.ACCOUNT_TYPE,
                        Integer.valueOf(token.userId)));
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                dataEvents.onError(new Exception(error.errorMessage));
            }
        });
    }

    @Override
    public void nextFriends() {
        fetchFriends(dataManager.getFriends().size(), VkRequestUtil.DEFAULT_FRIENDS_COUNT, Event.REQUEST_TYPE_LOAD_NEXT);
    }

    @Override
    public void cachedFriends() {
        if(dataManager.getFriends().size() != 0)
            dataEvents.onNext(new Event(
                    Event.REQUEST_TYPE_CACHE,
                    Event.REQUEST_DATA_TYPE_FRIENDS,
                    VkAccount.ACCOUNT_TYPE,
                    Integer.valueOf(token.userId)));
    }

    @Override
    public void updateFriends() {
        fetchFriends(0, dataManager.getFriends().size() == 0 ?
                VkRequestUtil.DEFAULT_FRIENDS_COUNT : dataManager.getFriends().size(), Event.REQUEST_TYPE_UPDATE);
    }

    @SuppressWarnings("unchecked")
    public void fetchFriends(int offset, final int count, final int requestType) {
        final VKRequest request = VkRequestUtil.createFriendsRequest(token.userId,
                offset, count);
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                List<User> list = (List<User>) response.parsedModel;

                if(requestType == Event.REQUEST_TYPE_LOAD_NEXT)
                    dataManager.setHaveMoreFriendsToUpdate(list.size() == count);

                dataManager.addFriends((List<VkUser>) response.parsedModel,
                        requestType == Event.REQUEST_TYPE_UPDATE);

                dataEvents.onNext(new Event(
                        requestType,
                        Event.REQUEST_DATA_TYPE_FRIENDS,
                        VkAccount.ACCOUNT_TYPE,
                        Integer.valueOf(token.userId)));
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                dataEvents.onError(new Exception(error.errorMessage));
            }
        });
    }

    @Override
    public void cachedDialogs() {
        if(dataManager.getDialogs().getDialogs().size() != 0)
            dataEvents.onNext(new Event(
                    Event.REQUEST_TYPE_CACHE,
                    Event.REQUEST_DATA_TYPE_DIALOGS,
                    VkAccount.ACCOUNT_TYPE,
                    Integer.valueOf(token.userId)));
    }

    @Override
    public void sendMessage(int to, Message message) {
        VkMessage vkMessage = new VkMessage(message);
        VkRequestUtil.enqueueRequest(VkRequestUtil.createSendMessageRequest(to, message), new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public void nextMessages(final int fromId) {
        final Dialog dialog = dataManager.getDialog(fromId);
        final VKRequest request = VkRequestUtil.createMessagesRequest(
                fromId,
                dialog.getMessages().get(dialog.getMessages().size() - 1).getId(),
                VkRequestUtil.DEFAULT_MESSAGES_COUNT);

        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                List<VkMessage> messages = (List<VkMessage>) response.parsedModel;

                synchronized (dataManager.getDialogs()) {
                    VkDialog dialog = dataManager.getDialog(fromId);

                    if (dialog != null) {
                        dialog.setHasNextMessages(messages.size() == VkRequestUtil.DEFAULT_MESSAGES_COUNT);
                        if (dialog.getMessages().get(dialog.getMessages().size() - 1).getId() ==
                                messages.get(0).getId()) dialog.getMessages().remove(dialog.getMessages().size() - 1);
                        for (int i = 0; i < messages.size(); i++) {
                            dialog.getMessages().add(messages.get(i));
                            if(!messages.get(i).isRead())
                                if (messages.get(i).isOut())
                                    dialog.setFirstOutUnreadIndex(dialog.getMessages().size() - 1);
                                else dialog.setFirstInUnreadIndex(dialog.getMessages().size() - 1);
                        }
                    } else return;
                }

                dataEvents.onNext(new Event(
                        Event.REQUEST_TYPE_LOAD_NEXT,
                        Event.REQUEST_DATA_TYPE_MESSAGES,
                        VkAccount.ACCOUNT_TYPE,
                        fromId
                ));
            }

            @Override
            public void onError(VKError error) {

            }
        });
    }

    @Override
    public Observable<User> onOwnerInfoUpdated() {
        return getUserInfo(token.userId).doOnNext(new Action1<User>() {
            @Override
            public void call(User user) {
                dataManager.setOwnerInfo(user);
            }
        });
    }

    protected Observable<User> getUserInfo(String userId) {
        return getUsers(userId).flatMap(new Func1<List<User>, Observable<User>>() {
            @Override
            public Observable<User> call(List<User> users) {
                if(users.size() == 0) return Observable.error(new Exception("User not exist"));
                return Observable.just(users.get(0));
            }
        });
    }

    @SuppressWarnings("unchecked")
    protected Observable<List<User>> getUsers(final String usersId) {
        return Observable.create(new Observable.OnSubscribe<List<User>>() {
            @Override
            public void call(final Subscriber<? super List<User>> subscriber) {
                VKRequest request = VkRequestUtil.createUsersRequest(usersId);
                request.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        subscriber.onNext((List<User>) response.parsedModel);
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onError(VKError error) {
                        super.onError(error);
                        subscriber.onError(new Exception(error.errorMessage));
                    }
                });
            }
        });
    }

    public void closeApi() {
        dataEvents.onCompleted();
        messageFlagEvents.onCompleted();
        typingEvents.onCompleted();
        friendsStatusEvents.onCompleted();
        newMessagesEvents.onCompleted();
        stopService();
    }


    @Override
    public void onStop() {
        longPullSubscription.unsubscribe();
        longPullService = null;
    }

    public VkDataManager getDataManager() {
        return dataManager;
    }

    //--------------------- Long pull service --------------
    public void runServices(Context context) {
        if(!isLongPullServiceRun()) {
            context.startService(new Intent(context, VkLongPullService.class));
        }
    }

    public void stopService() {
        if(isLongPullServiceRun())
            longPullService.stop();
    }

    public void setLongPullService(VkLongPullService service) {
        longPullService = service;
        longPullService.setStopListener(this);
        longPullSubscription = longPullService.getEventObservable()
                .observeOn(Schedulers.computation())
                .subscribe(new Action1<VkLongPullEvent>() {
                    @Override
                    public void call(VkLongPullEvent vkLongPullEvent) {
                        if (typingEvents.hasObservers())
                            typingEvents.onNext(vkLongPullEvent.getWritersMap().keySet());

                        if(!vkLongPullEvent.getFriendsStatusMap().isEmpty())
                            friendsStatusEvents.onNext(updateFriendsStatus(vkLongPullEvent.getFriendsStatusMap()));

                        if(!vkLongPullEvent.getDialogsUpdate().isEmpty())
                            newMessagesEvents.onNext(addNewMessages(vkLongPullEvent.getDialogsUpdate()));

                        if(!vkLongPullEvent.getMessageReadEvents().isEmpty())
                            updateMessagesReadState(vkLongPullEvent.getMessageReadEvents());

                        if(!vkLongPullEvent.getMessageFlagEvents().isEmpty())
                            messageFlagEvents.onNext(updateMessages(vkLongPullEvent.getMessageFlagEvents()));
                    }
                });
    }


    private void updateMessagesReadState(Map<Integer, List<Pair<Integer, Boolean>>> messageReadEvents) {
        VkDialog dialog;
        VkMessage message;
        synchronized (dataManager.getDialogs()) {
            for (Integer key : messageReadEvents.keySet()) {
                if((dialog = dataManager.getDialog(key)) != null) {
                    for (Pair<Integer, Boolean> pair : messageReadEvents.get(key)) {
                        for (ListIterator<VkMessage> it =
                             dialog.getMessages().listIterator( 1 + (
                                     pair.second ?
                                             dialog.getFirstOutUnreadIndex() :
                                             dialog.getFirstInUnreadIndex()));
                             it.hasPrevious();) {
                            message = it.previous();
                            if(message.isOut() == pair.second) {
                                message.setRead(true);
                            }
                        }
                        if (pair.second)
                            dialog.setFirstOutUnreadIndex(-1);
                        else {
                            dialog.setFirstInUnreadIndex(-1);
                            dialog.setUnreadCount(0);
                        }
                    }
                }
            }
        }
    }

    private Map<Integer, List<Pair<Integer, Integer>>> updateMessages
            (Map<Integer, List<Pair<Integer, Integer>>> messagesUpdate) {
*/
/*
        VkDialog dialog;

        for (Integer integer : messagesUpdate.keySet()) {
            Log.i("TAG", integer.toString());
        }

        synchronized (dataManager.getDialogs()) {
            for (Integer key : messagesUpdate.keySet())
                if ((dialog = dataManager.getDialog(key)) != null)
                    for (VkMessage message : dialog.getMessages())
                        for (Pair<Integer, Integer> pair : messagesUpdate.get(key))
                            if (pair.first == message.getId())
                                updateMessage(message, pair.second);
        }*//*


        return messagesUpdate;
    }

    private void updateMessage(VkMessage message, Integer flag) {
        Log.i("TAG", message.getContent() + " updating");
        boolean set = true;
        if (flag < 0) {
            set = false;
            flag = -flag;
        }

        if (flag >= 256) {
            flag-=256;
            message.setDeleted(set || !message.isDeleted());
        }

        if (flag >= 128) {
            flag-=128;
            message.setDeleted(set || !message.isDeleted());
        }

        if (flag >= 64) {
            flag-=64;
            message.setDeleted(set || !message.isDeleted());
        }

        if (flag >= 8) {
            flag-=32;
            //message.setDeleted(set || !message.isDeleted());
        }

        if (flag >= 1) message.setRead(set || !message.isDeleted());
    }

    private Set<Integer> addNewMessages(VkDialogsUpdate dialogsUpdate) {

        Set<Integer> resultIds = new HashSet<>();

        synchronized (dataManager.getDialogs()) {
            for (VkUser vkUser : dialogsUpdate.getUsersList())
                dataManager.getDialogs().addInterlocutor(vkUser);

            VkDialog dialog;
            for (VkMessage message : dialogsUpdate.getMessagesList()) {
                if ((dialog = dataManager.getDialog(message.getDialogId())) != null)
                    dialog.addMessage(message);
                else {
                    VkDialog newDialog = new VkDialog(message);
                    dataManager.addDialog(newDialog);
                }
                resultIds.add(message.getDialogId());
            }
        }

        return resultIds;
    }

    private Set<Integer> updateFriendsStatus(Map<Integer, Integer> friendsStatusMap) {
        VkInterlocutor interlocutor;
        VkUser user;

        synchronized (dataManager.getFriends()) {
            synchronized (dataManager.getDialogs()) {
                for (Integer key : friendsStatusMap.keySet()) {
                    if ((user = dataManager.getFriend(key)) != null) {
                        user.setOnline(friendsStatusMap.get(key) != VkUser.OFFLINE);
                        user.setOnlineMobile(friendsStatusMap.get(key) < VkUser.ONLINE_DESKTOP);
                    }

                    if((interlocutor = dataManager.getDialogs().getInterlocutor(key)) != null) {
                        interlocutor.setOnline(friendsStatusMap.get(key) != VkUser.OFFLINE);
                        interlocutor.setOnlineMobile(friendsStatusMap.get(key) < VkUser.ONLINE_DESKTOP);
                    }
                }
            }
        }

        return friendsStatusMap.keySet();
    }

    public boolean isLongPullServiceRun() {
        return longPullService != null && longPullService.isRun();
    }

    //-------------------- Interfaces -----------------------
}
*/
