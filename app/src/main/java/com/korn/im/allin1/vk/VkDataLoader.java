package com.korn.im.allin1.vk;

import android.util.Pair;

import com.korn.im.allin1.accounts.DataLoader;
import com.korn.im.allin1.pojo.Interlocutor;
import com.korn.im.allin1.vk.pojo.VkDialog;
import com.korn.im.allin1.vk.pojo.VkDialogs;
import com.korn.im.allin1.vk.pojo.VkMessage;
import com.korn.im.allin1.vk.pojo.VkUser;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import rx.Observable;
import rx.subjects.AsyncSubject;

class VkDataLoader implements DataLoader<VkMessage, VkUser, VkDialogs, VkDialog, Interlocutor> {
    private static final String IMPOSSIBLE_STATE_ERROR_MSG = "Impossible state";

    // Executors
    private ExecutorService parsingExecutorService = Executors.newFixedThreadPool(3);

    // Friend downloading
    private final AtomicReference<AsyncSubject<Map<Integer, VkUser>>> loadFriendsSubject
            = new AtomicReference<>(null);

    // Dialogs downloading
    private final AtomicReference<AsyncSubject<Pair<VkDialogs, Map<Integer, Interlocutor>>>> loadDialogsSubject
            = new AtomicReference<>(null);

    // Messages downloading
    private final ConcurrentHashMap<Integer, AsyncSubject<Pair<Integer, Map<Integer, VkMessage>>>> loadMessagesMap
            = new ConcurrentHashMap<>();//Collections.synchronizedMap(new HashMap<>());

    //----------------------------------------- Members end ---------------------------------------

    // Friends downloading
    @Override
    public Observable<Map<Integer, VkUser>> loadFriends() {
        for (;;) {
            AsyncSubject<Map<Integer, VkUser>> subject = loadFriendsSubject.get();
            if (subject != null) return subject.asObservable();
            else if (loadFriendsSubject.compareAndSet(null, subject = AsyncSubject.create()))
                loadFriendsRequest(subject);
        }
    }

    private void loadFriendsRequest(final AsyncSubject<Map<Integer, VkUser>> subject) {
        VkRequestUtil.createFriendsRequest(null, 0, Integer.MAX_VALUE)
                     .executeWithListener(new VKRequest.VKRequestListener() {
                         @Override
                         public void onComplete(VKResponse response) {
                             parsingExecutorService.execute(() -> {
                                 try {
                                     subject.onNext(VkJsonParser.parseUsers(response.json));
                                     subject.onCompleted();
                                 } catch (JSONException e) {
                                     subject.onError(e);
                                 } finally {
                                     if (!loadFriendsSubject.compareAndSet(subject, null))
                                         throw new IllegalStateException(IMPOSSIBLE_STATE_ERROR_MSG);
                                 }
                             });
                         }

                         @Override
                         public void onError(VKError error) {
                             subject.onError(new Error(error.errorMessage));
                             if (!loadFriendsSubject.compareAndSet(subject, null))
                                 throw new IllegalStateException(IMPOSSIBLE_STATE_ERROR_MSG);
                         }
                     });
    }

    @Override
    public Observable<Map<Integer, VkUser>> loadUsers(int... id) {
        if (id.length == 0) return Observable.empty();

        return Observable.create(subscriber -> VkRequestUtil
                .createUserRequest(id)
                .executeWithListener(new VKRequest.VKRequestListener() {
                                         @Override
                                         public void onComplete(VKResponse response) {
                                             parsingExecutorService.execute(() -> {
                                                 try {
                                                     subscriber.onNext(VkJsonParser.parseUsers(response.json));
                                                     subscriber.onCompleted();
                                                 } catch (JSONException e) {
                                                     subscriber.onError(e);
                                                 }
                                             });
                                         }

                                         @Override
                                         public void onError(VKError error) {
                                             subscriber.onError(new RuntimeException(error.errorMessage));
                                         }
                                     }));
    }

    // Dialogs downloading
    @Override
    public Observable<Pair<VkDialogs, Map<Integer, Interlocutor>>>
    loadDialogs(int lastDialogStamp, int count) {
        for (;;) {
            AsyncSubject<Pair<VkDialogs, Map<Integer, Interlocutor>>> subject = loadDialogsSubject.get();
            if (subject != null) return subject.asObservable();
            else if (loadDialogsSubject.compareAndSet(null, subject = AsyncSubject.create()))
                loadDialogsRequest(subject, count, lastDialogStamp);
        }
    }

    private void loadDialogsRequest(
            AsyncSubject<Pair<VkDialogs, Map<Integer, Interlocutor>>> subject,
            int count,
            int lastDialogStamp) {
        VkRequestUtil.createDialogsRequest(lastDialogStamp == 0 ? 0 : 1,
                                           count,
                                           lastDialogStamp)
                     .executeWithListener(new VKRequest.VKRequestListener() {
                         @Override
                         public void onComplete(VKResponse response) {
                             parsingExecutorService.execute(() -> {
                                 try {
                                     subject.onNext(VkJsonParser.parseDialogs(response.json));
                                     subject.onCompleted();
                                 } catch (JSONException e) {
                                     subject.onError(e);
                                 } finally {
                                     if (!loadDialogsSubject.compareAndSet(subject, null))
                                         throw new IllegalStateException(IMPOSSIBLE_STATE_ERROR_MSG);
                                 }
                             });
                         }

                         @Override
                         public void onError(VKError error) {
                             subject.onError(new Exception(error.errorMessage));
                             if (!loadDialogsSubject.compareAndSet(subject, null))
                                 throw new IllegalStateException(IMPOSSIBLE_STATE_ERROR_MSG);
                         }
                     });
    }

    @Override
    public Observable<VkDialog> loadDialog(int id) {
        return Observable.empty();
    }

    // Messages downloading
    @Override
    public Observable<Pair<Integer, Map<Integer, VkMessage>>> loadMessages(final int id,
                                                                           final int count,
                                                                           final int lastMessageStamp) {
        AsyncSubject<Pair<Integer, Map<Integer, VkMessage>>> subject = loadMessagesMap.get(id);
        if (subject != null) return subject.asObservable();
        else {
            AsyncSubject<Pair<Integer, Map<Integer, VkMessage>>> oldSubject =
                    loadMessagesMap.putIfAbsent(id, subject = AsyncSubject.create());
            if (oldSubject != null)
                subject = oldSubject;
        }
        return loadMessagesRequest(subject, id, count, lastMessageStamp);
    }

    private Observable<Pair<Integer, Map<Integer, VkMessage>>> loadMessagesRequest(final AsyncSubject<Pair<Integer, Map<Integer, VkMessage>>> subject,
                                                                                   final int id,
                                                                                   final int count,
                                                                                   final int lastMessageStamp) {
        VkRequestUtil.createMessagesRequest(id,
                                            lastMessageStamp,
                                            count,
                                            lastMessageStamp == 0 ? 0 : 1)
                     .executeWithListener(new VKRequest.VKRequestListener() {
                         @Override
                         public void onComplete(VKResponse response) {
                             super.onComplete(response);
                             parsingExecutorService.execute(() -> {
                                 try {
                                     Map<Integer, VkMessage> m = VkJsonParser.parseMessages(response.json);
                                     subject.onNext(Pair.create(id,
                                                                m));
                                     subject.onCompleted();
                                 } catch (JSONException e) {
                                     subject.onError(e);
                                     e.printStackTrace();
                                 } finally {
                                     loadMessagesMap.remove(id, subject);
                                 }
                             });
                         }

                         @Override
                         public void onError(VKError error) {
                             subject.onError(new Exception(error.errorMessage));
                             loadMessagesMap.remove(id, subject);
                         }
                     });
        return subject;
    }
}
