package com.korn.im.allin1.vk;

import android.util.Pair;

import com.korn.im.allin1.accounts.DataLoader;
import com.korn.im.allin1.pojo.Interlocutor;
import com.korn.im.allin1.vk.pojo.VkDialog;
import com.korn.im.allin1.vk.pojo.VkDialogs;
import com.korn.im.allin1.vk.pojo.VkJsonParser;
import com.korn.im.allin1.vk.pojo.VkUser;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONException;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import rx.Observable;
import rx.subjects.AsyncSubject;

class VkDataLoader implements DataLoader<VkUser, VkDialogs, VkDialog, Interlocutor> {
    private static final String UNREAL_STATE_ERROR_MSG = "Unreal state";
    private ExecutorService parsingExecutorService = Executors.newFixedThreadPool(3);

    //Friend downloading
    private final AtomicReference<AsyncSubject<Map<Integer, VkUser>>> loadFriendsSubject
            = new AtomicReference<>(null);
    //Dialogs downloading
    private final AtomicReference<AsyncSubject<Pair<VkDialogs, Map<Integer, Interlocutor>>>> loadDialogsSubject
            = new AtomicReference<>(null);

    VkDataLoader() {}

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
                                         throw new IllegalStateException(UNREAL_STATE_ERROR_MSG);
                                 }
                             });
                         }

                         @Override
                         public void onError(VKError error) {
                             subject.onError(new Error(error.errorMessage));
                             if (!loadFriendsSubject.compareAndSet(subject, null))
                                 throw new IllegalStateException(UNREAL_STATE_ERROR_MSG);
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

    @Override
    public Observable<Pair<VkDialogs, Map<Integer, Interlocutor>>>
    loadDialogs(int lastDialogStamp, int size) {
        for (;;) {
            AsyncSubject<Pair<VkDialogs, Map<Integer, Interlocutor>>> subject = loadDialogsSubject.get();
            if (subject != null) return subject.asObservable();
            else if (loadDialogsSubject.compareAndSet(null, subject = AsyncSubject.create()))
                loadDialogsRequest(subject, lastDialogStamp, size);
        }
    }

    private void loadDialogsRequest(
            AsyncSubject<Pair<VkDialogs, Map<Integer, Interlocutor>>> subject,
            int lastDialogStamp,
            int size) {
        VkRequestUtil.createDialogsRequest(lastDialogStamp == 0 ? 0 : 1, lastDialogStamp, size)
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
                                         throw new IllegalStateException(UNREAL_STATE_ERROR_MSG);
                                 }
                             });
                         }

                         @Override
                         public void onError(VKError error) {
                             subject.onError(new Exception(error.errorMessage));
                             if (!loadDialogsSubject.compareAndSet(subject, null))
                                 throw new IllegalStateException(UNREAL_STATE_ERROR_MSG);
                         }
                     });
    }

    @Override
    public Observable<VkDialog> loadDialog(int id) {
        return Observable.empty();
    }
}
