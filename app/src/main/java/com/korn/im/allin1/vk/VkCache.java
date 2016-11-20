package com.korn.im.allin1.vk;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.util.Pair;

import com.google.common.collect.ImmutableMap;
import com.korn.im.allin1.accounts.Cache;
import com.korn.im.allin1.errors.NoDataException;
import com.korn.im.allin1.pojo.Interlocutor;
import com.korn.im.allin1.vk.pojo.SynchronizedVkDialogs;
import com.korn.im.allin1.vk.pojo.VkDialog;
import com.korn.im.allin1.vk.pojo.VkDialogs;
import com.korn.im.allin1.vk.pojo.VkUser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func2;

@SuppressLint("UseSparseArrays")
class VkCache implements Cache<VkUser, VkDialogs, VkDialog, Interlocutor> {

    private volatile boolean hasFriends = false;
    private final Map<Integer, VkUser> friends = Collections.synchronizedMap(new HashMap<>());

    private volatile boolean hasDialogs = false;
    private final SynchronizedVkDialogs dialogs = new SynchronizedVkDialogs();

    private volatile boolean hasInterlocutors = false;
    private final Map<Integer, Interlocutor> interlocutors = Collections.synchronizedMap(new HashMap<>());

    VkCache() {}

    @Override
    public Observable<Map<Integer, VkUser>> getFriends() {
        return Observable.create(new Observable.OnSubscribe<Map<Integer, VkUser>>() {
            @Override
            public void call(Subscriber<? super Map<Integer, VkUser>> subscriber) {
                if (!hasFriends) {
                    subscriber.onError(new NoDataException());
                    return;
                }
                Map<Integer, VkUser> snapshot;
                synchronized (friends) {
                    snapshot = ImmutableMap.copyOf(friends);
                }
                subscriber.onNext(snapshot);
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public void saveFriends(@NonNull final Map<Integer, VkUser> friends, final boolean rewrite) {
        if(rewrite) synchronized (this.friends) {
            this.friends.clear();
            this.friends.putAll(friends);
        }
        else this.friends.putAll(friends);
        hasFriends = true;
    }

    @Override
    public Observable<VkUser> getFriend(final int id) {
        return Observable.create(new Observable.OnSubscribe<VkUser>() {
            @Override
            public void call(Subscriber<? super VkUser> subscriber) {
                VkUser friend;
                if (!hasFriends || (friend = friends.get(id)) == null) {
                    subscriber.onError(new NoDataException());
                    return;
                }
                subscriber.onNext(friend);
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public void saveFriend(final VkUser friend) {
        friends.put(friend.getId(), friend);
        hasFriends = true;
    }

    @Override
    public Observable<Pair<VkDialogs, ? extends Map<Integer, ? extends Interlocutor>>> getDialogs() {
        return Observable.zip(Observable.create(subscriber -> {
            if (!hasDialogs) {
                subscriber.onError(new NoDataException());
                return;
            }
            subscriber.onNext(dialogs.getCopy());
            subscriber.onCompleted();
        }), getInterlocutors(), new Func2<VkDialogs, Map<Integer, Interlocutor>, Pair<VkDialogs,? extends Map<Integer,Interlocutor>>>() {
            @Override
            public Pair<VkDialogs, ? extends Map<Integer, Interlocutor>> call(VkDialogs vkDialogs, Map<Integer,Interlocutor> interlocutorMap) {
                return Pair.create(vkDialogs, interlocutorMap);
            }
        });
    }

    @Override
    public void saveDialogs(final @NonNull VkDialogs dialogs, final boolean rewrite) {
        this.dialogs.addDialogs(dialogs.getDialogs(), rewrite);
        hasFriends = true;
    }

    @Override
    public Observable<VkDialog> getDialog(final int id) {
        return Observable.create(subscriber -> {
            VkDialog dialog;
            if (!hasDialogs || (dialog = dialogs.getDialog(id)) == null) {
                subscriber.onError(new NoDataException());
                return;
            }
            subscriber.onNext(dialog);
            subscriber.onCompleted();
        });
    }

    @Override
    public void saveDialog(final VkDialog dialog) {
        dialogs.addDialog(dialog);
        hasDialogs = true;
    }

    @Override
    public Observable<Interlocutor> getInterlocutor(final int id) {
        return Observable.create(subscriber -> {
            Interlocutor interlocutor;
            if (!hasInterlocutors || (interlocutor = interlocutors.get(id)) == null) {
                subscriber.onError(new NoDataException());
                return;
            }
            subscriber.onNext(interlocutor);
            subscriber.onCompleted();
        });
    }

    @Override
    public Observable<Map<Integer, Interlocutor>> getInterlocutors() {
        return Observable.create(subscriber -> {
            if (!hasInterlocutors) {
                subscriber.onError(new NoDataException());
                return;
            }
            Map<Integer, Interlocutor> snapshot;
            synchronized (interlocutors) {
                snapshot = ImmutableMap.copyOf(interlocutors);
            }
            subscriber.onNext(snapshot);
            subscriber.onCompleted();
        });
    }

    @Override
    public void saveInterlocutor(Interlocutor interlocutor) {
        interlocutors.put(interlocutor.getId(), interlocutor);
        hasInterlocutors = true;
    }

    @Override
    public void saveInterlocutors(Map<Integer, ? extends Interlocutor> interlocutors, boolean rewrite) {
        synchronized (this.interlocutors) {
            synchronized (this.friends) {
                if (rewrite) this.interlocutors.clear();
                for (Map.Entry<Integer, ? extends Interlocutor> entry : interlocutors.entrySet())
                    if (!this.friends.containsKey(entry.getKey()))
                        this.interlocutors.put(entry.getKey(), entry.getValue());
            }
        }
        hasInterlocutors = true;
    }

    int getNextDialogsStamp() {
        return dialogs.nextDialogsStamp();
    }
}
