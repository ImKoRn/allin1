package com.korn.im.allin1.accounts;

import android.util.Pair;

import com.korn.im.allin1.pojo.User;

import java.util.List;
import java.util.Map;
import java.util.Set;

import rx.Observable;

/**
 * Describes feedback of {@link Api} methods
 */
public interface Events {
    Observable<User> onOwnerInfoUpdated();
    Observable<Event> onDataEvents();
    Observable<Set<Integer>> onTypingEvent();
    Observable<Set<Integer>> onFriendsStatusChangedEvent();
    Observable<Set<Integer>> onNewMessagesArrivedEvent();
    Observable<Map<Integer, List<Pair<Integer, Integer>>>> onMessagesFlagsChangedEvent();
}
