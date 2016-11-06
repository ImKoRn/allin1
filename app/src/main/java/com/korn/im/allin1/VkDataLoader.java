package com.korn.im.allin1;

import android.util.Log;
import android.util.Pair;

import com.korn.im.allin1.accounts.newaccount.DataLoader;
import com.korn.im.allin1.vk.VkRequestUtil;
import com.korn.im.allin1.vk.pojo.VkDialog;
import com.korn.im.allin1.vk.pojo.VkInterlocutor;
import com.korn.im.allin1.vk.pojo.VkUser;
import com.korn.im.allin1.vk.pojo.newvkpojo.VkDialogs;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class VkDataLoader implements DataLoader<VkUser, VkDialogs, VkDialog, VkInterlocutor> {
    private String userId;

    public VkDataLoader(String userId) {
        this.userId = userId;
    }

    @Override
    public Observable<List<VkUser>> loadFriends() {
        return Observable.create(new Observable.OnSubscribe<JSONObject>() {
            @Override
            public void call(Subscriber<? super JSONObject> subscriber) {
                {
                    VkRequestUtil
                            .createFriendsRequest(userId, 0, Integer.MAX_VALUE)
                            .executeWithListener(new VKRequest.VKRequestListener() {
                                @Override
                                public void onComplete(VKResponse response) {
                                    subscriber.onNext(response.json);
                                    subscriber.onCompleted();
                                }

                                @Override
                                public void onError(VKError error) {
                                    subscriber.onError(new Error(error.errorMessage));
                                }
                            });
                }
            }
        }).map(VkJsonParser::parseUsers);
    }

    @Override
    public Observable<List<VkUser>> loadNextPageOfFriends() {
        return Observable.error(new Error());
    }

    @Override
    public Observable<VkUser> loadUser(int id) {
        return Observable.create((Observable.OnSubscribe<JSONObject>) subscriber -> VkRequestUtil
                .createUserRequest(userId)
                .executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        subscriber.onNext(response.json);
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onError(VKError error) {
                        subscriber.onError(new Error(error.errorMessage));
                    }
                })).map(jsonObject -> {
                    try {
                        return new VkUser(jsonObject
                                .optJSONArray(VkJsonParser.RESPONSE)
                                .optJSONObject(0));
                    } catch (JSONException e) {
                        throw new Error(e.getMessage());
                    }
                });
    }

    @Override
    public Observable<Pair<VkDialogs, List<VkInterlocutor>>> loadDialogs() {
        return Observable.error(new Error());
    }

    @Override
    public Observable<Pair<VkDialogs, List<VkInterlocutor>>> loadNextPageOfDialogs() {
        return Observable.error(new Error());
    }

    @Override
    public Observable<VkDialog> loadDialog(int id) {
        return Observable.error(new Error());
    }
}
