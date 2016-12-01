package com.korn.im.allin1.vk;

import android.util.Pair;

import com.korn.im.allin1.accounts.DataPublisher;
import com.korn.im.allin1.pojo.Interlocutor;
import com.korn.im.allin1.vk.pojo.VkDialog;
import com.korn.im.allin1.vk.pojo.VkDialogs;
import com.korn.im.allin1.vk.pojo.VkUser;

import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.subjects.PublishSubject;

class VkDataPublisher implements DataPublisher<VkUser, VkDialogs, VkDialog, Interlocutor> {
    private final PublishSubject<Map<Integer, VkUser>> friendsSubject = PublishSubject.create();
    private final PublishSubject<Throwable> friendsErrorSubject = PublishSubject.create();

    private final PublishSubject<Pair<VkDialogs, Map<Integer, Interlocutor>>>
            dialogsSubject = PublishSubject.create();
    private final PublishSubject<Throwable> dialogsErrorSubject = PublishSubject.create();

    @Override
    public Observable<Map<Integer, VkUser>> friendsObservable() {
        return friendsSubject.asObservable();
    }

    @Override
    public Observable<Throwable> friendsErrorsObservable() {
        return friendsErrorSubject.asObservable();
    }

    @Override
    public Observable<Pair<VkDialogs, Map<Integer, Interlocutor>>> dialogsObservable() {
        return dialogsSubject.asObservable();
    }

    @Override
    public Observable<Throwable> dialogsErrorsObservable() {
        return dialogsErrorSubject.asObservable();
    }


    void publishFriendsWhenArrive(Observable<? extends Map<Integer, VkUser>> friendsObservable) {
        friendsObservable.subscribe(new FriendSubscriber());
    }

    void publishDialogsWhenArrive(Observable<Pair<VkDialogs, Map<Integer, Interlocutor>>>
                                   dialogsObservable) {
        dialogsObservable.subscribe(new DialogsSubscriber());
    }

    private class FriendSubscriber extends Subscriber<Map<Integer, VkUser>> {
        @Override
        public void onCompleted() {
            unsubscribe();
        }

        @Override
        public void onError(Throwable e) {
            friendsErrorSubject.onNext(e);
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
            dialogsErrorSubject.onNext(e);
        }

        @Override
        public void onNext(Pair<VkDialogs, Map<Integer, Interlocutor>> dialogs) {
            dialogsSubject.onNext(dialogs);
        }
    }
}