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
import com.korn.im.allin1.vk.pojo.VkMessage;
import com.korn.im.allin1.vk.pojo.VkUser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;

@SuppressLint("UseSparseArrays")
class VkCache implements Cache<VkMessage, VkUser, VkDialogs, VkDialog, Interlocutor> {
    // Friends
    private volatile boolean hasFriends = false;
    private final Map<Integer, VkUser> friends = Collections.synchronizedMap(new HashMap<>());

    // Dialogs and messages
    private volatile boolean hasDialogs = false;
    private final SynchronizedVkDialogs dialogs = new SynchronizedVkDialogs();

    // Interlocutors
    private volatile boolean hasInterlocutors = false;
    private final Map<Integer, Interlocutor> interlocutors = Collections.synchronizedMap(new HashMap<>());
    //----------------------------------------- Members end ---------------------------------------

    // Friends methods
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

    // Dialogs methods
    @Override
    public Observable<Pair<VkDialogs, Map<Integer, Interlocutor>>> getDialogs() {
        return Observable.create(subscriber -> {
            if (!hasDialogs) {
                subscriber.onError(new NoDataException());
                return;
            }
            VkDialogs dialogsSnapshot = dialogs.getCopy();
            ImmutableMap.Builder<Integer, Interlocutor> interlocutors = new ImmutableMap.Builder<>();
            synchronized (this.interlocutors) {
                interlocutors.putAll(this.interlocutors);
            }
            synchronized (this.friends) {
                interlocutors.putAll(this.friends);
            }
            for (Map.Entry<Integer, VkDialog> entry : dialogsSnapshot.getDialogs().entrySet())
                if (entry.getValue().isChat())
                    interlocutors.put(entry.getValue().getId(), entry.getValue());

            Pair<VkDialogs, Map<Integer, Interlocutor>> res = Pair.create(dialogsSnapshot, interlocutors.build());
            subscriber.onNext(res);
            subscriber.onCompleted();
        });
    }

    @Override
    public void saveDialogs(final @NonNull VkDialogs dialogs, final boolean rewrite) {
        this.dialogs.addDialogs(dialogs.getDialogs(), rewrite);
        this.dialogs.addMessages(dialogs.getMessages(), rewrite);
        hasDialogs = true;
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

    // Messages methods
    @Override
    public Observable<Pair<Integer, Map<Integer, VkMessage>>> getMessages(final int id) {
        return Observable.create(subscriber -> {
            subscriber.onNext(Pair.create(id, dialogs.getDialogMessages(id)));
            subscriber.onCompleted();
        });
    }

    @Override
    public void saveMessages(final int id,
                             final Map<Integer, ? extends VkMessage> messages,
                             boolean rewrite) {
        dialogs.addMessagesToDialog(id, messages, rewrite);
    }

    // Interlocutors methods
    @Override
    public Observable<Interlocutor> getInterlocutor(final int id) {
        return Observable.create(subscriber -> {
            Interlocutor interlocutor = null;
            if (hasFriends)
                interlocutor = friends.get(id);

            if (interlocutor == null && hasInterlocutors)
                interlocutor = interlocutors.get(id);

            if (interlocutor == null && hasDialogs)
                interlocutor = dialogs.getDialog(id);

            if (interlocutor == null) {
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
            ImmutableMap.Builder<Integer, Interlocutor> snapshot = ImmutableMap.builder();
            if (hasInterlocutors) synchronized (interlocutors) {
                snapshot.putAll(interlocutors);
            }

            if (hasFriends) synchronized (friends) {
                snapshot.putAll(friends);
            }

            subscriber.onNext(snapshot.build());
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

    // Data stamps
    int getDialogsStamp() {
        return dialogs.nextDialogsStamp();
    }

    int getMessagesStamp(int id) {
        return dialogs.nextMessagesStamp(id);
    }

    public int getDialogsCount() {
        return dialogs.getDialogsCount();
    }

    public int getMessagesCount(int id) {
        return dialogs.getMessagesCount(id);
    }
}
