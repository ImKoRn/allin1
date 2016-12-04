package com.korn.im.allin1.vk;

import android.util.Pair;

import com.korn.im.allin1.accounts.EventManager;
import com.korn.im.allin1.pojo.Interlocutor;
import com.korn.im.allin1.vk.pojo.VkDialog;
import com.korn.im.allin1.vk.pojo.VkDialogs;
import com.korn.im.allin1.vk.pojo.VkMessage;
import com.korn.im.allin1.vk.pojo.VkUser;

import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.subjects.PublishSubject;

class VkEventManager implements EventManager<VkMessage, VkUser, VkDialogs, VkDialog, Interlocutor> {
    // Friends events
    // Friends loading events
    private final PublishSubject<Map<Integer, VkUser>> friendsSubject = PublishSubject.create();
    // Friends loading errors events
    private final PublishSubject<Throwable> friendsErrorsSubject = PublishSubject.create();

    // Dialogs events
    // Dialogs loading events
    private final PublishSubject<Pair<VkDialogs, Map<Integer, Interlocutor>>>
            dialogsSubject = PublishSubject.create();
    // Dialogs loading errors events
    private final PublishSubject<Throwable> dialogsErrorsSubject = PublishSubject.create();

    // Messages events
    // Messages loading events
    private final PublishSubject<Pair<Integer, Map<Integer, VkMessage>>> messagesSubject = PublishSubject.create();
    // Messages loading errors events
    private final PublishSubject<Throwable> messagesErrorsSubject = PublishSubject.create();

    //------------------------------------ Members end --------------------------------------------

    // Friends events
    @Override
    public Observable<Map<Integer, VkUser>> friendsObservable() {
        return friendsSubject.asObservable();
    }

    @Override
    public Observable<Throwable> friendsErrorsObservable() {
        return friendsErrorsSubject.asObservable();
    }

    // Dialogs events
    @Override
    public Observable<Pair<VkDialogs, Map<Integer, Interlocutor>>> dialogsObservable() {
        return dialogsSubject.asObservable();
    }

    @Override
    public Observable<Throwable> dialogsErrorsObservable() {
        return dialogsErrorsSubject.asObservable();
    }

    // Messages events
    @Override
    public Observable<Pair<Integer, Map<Integer, VkMessage>>> messagesObservable() {
        return messagesSubject.asObservable();
    }

    @Override
    public Observable<Throwable> messagesErrorsObservable() {
        return messagesErrorsSubject.asObservable();
    }

    // Publish data events
    void publishFriendsWhenArrive(Observable<? extends Map<Integer, VkUser>> friendsObservable) {
        friendsObservable.subscribe(new FriendSubscriber());
    }

    void publishDialogsWhenArrive(Observable<Pair<VkDialogs, Map<Integer, Interlocutor>>>
                                   dialogsObservable) {
        dialogsObservable.subscribe(new DialogsSubscriber());
    }

    void publishMessagesWhenArrive(Observable<Pair<Integer, Map<Integer, VkMessage>>> messagesObservable) {
        messagesObservable.subscribe(new MessagesSubscriber());
    }

    // Classes for subscribing on events
    private class FriendSubscriber extends Subscriber<Map<Integer, VkUser>> {
        @Override
        public void onCompleted() {
            unsubscribe();
        }

        @Override
        public void onError(Throwable e) {
            friendsErrorsSubject.onNext(e);
        }

        @Override
        public void onNext(Map<Integer, VkUser> friends) {
            friendsSubject.onNext(friends);
        }
    }

    private class DialogsSubscriber extends Subscriber<Pair<VkDialogs, Map<Integer, Interlocutor>>> {
        @Override
        public void onCompleted() {
            unsubscribe();
        }

        @Override
        public void onError(Throwable e) {
            dialogsErrorsSubject.onNext(e);
        }

        @Override
        public void onNext(Pair<VkDialogs, Map<Integer, Interlocutor>> dialogs) {
            dialogsSubject.onNext(dialogs);
        }
    }

    private class MessagesSubscriber extends Subscriber<Pair<Integer, Map<Integer, VkMessage>>> {

        @Override
        public void onCompleted() {
            unsubscribe();
        }

        @Override
        public void onError(Throwable e) {
            messagesErrorsSubject.onNext(e);
        }

        @Override
        public void onNext(Pair<Integer, Map<Integer, VkMessage>> messages) {
            messagesSubject.onNext(messages);
        }
    }
}