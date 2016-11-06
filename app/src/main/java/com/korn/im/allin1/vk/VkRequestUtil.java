package com.korn.im.allin1.vk;

import android.annotation.SuppressLint;

import com.korn.im.allin1.pojo.Message;
import com.korn.im.allin1.vk.pojo.VkDialogs;
import com.korn.im.allin1.vk.pojo.VkMessage;
import com.korn.im.allin1.vk.pojo.VkUser;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKParser;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKList;

import org.json.JSONObject;

/**
 * Created by korn on 08.08.16.
 */
public class VkRequestUtil {
    //Event
    private static final String GET_DIALOGS = "messages.getDialogs";
    private static final String GET_USERS = "users.get";
    private static final String GET_FRIENDS = "friends.get";
    private static final String GET_USER = "users.get";
    private static final String EXECUTE = "execute";
    private static final String MESSAGES_SEND = "messages.send";
    private static final String VK_LONG_PULL = "messages.getLongPollServer";
    private static final String MESSAGES_GET_HISTORY = "messages.getHistory";
    private static final String VK_LONG_PULL_HISTORY = "messages.getLongPollHistory";

    //Const
    public static final int DEFAULT_MESSAGES_COUNT = 30;
    public static final int DEFAULT_FRIENDS_COUNT = 0;
    public static final int DEFAULT_DIALOGS_COUNT = 20;
    private static final String DEFAULT_USER_PARAMS = "online, online_mobile, photo_50, photo_100, photo_200, photo_200_orig";
    //TODO Change addition fields of getting profiles
    private static final String DIALOGS_WITH_USERS_REQUEST_CODE =
            "var dialogs = API.messages.getDialogs({\"offset\" : %s, \"count\" : %s}); var profiles = API.users.get({\"user_ids\" : dialogs.items@.message@.user_id});\n" +
                    "return {\"dialogs\" : dialogs} + {\"profiles\" : profiles};";
    private static final int CONNECT_TO_VK_LONG_PULL_ATTEMPTS = 3;

    //Fields
    public static final String OFFSET = "offset";
    private static final String COUNT = "count";
    private static final String CODE = "code";
    private static final String FIELD_PHOTO_200_ORIG = "photo_200_orig";
    private static final String NEED_PTS_PARAM = "need_pts";

    //----------------------------------------------------------------------------------------------

    private static VKRequest lastRequest;

    public static VKRequest createDialogsRequest(int offset, int count) {
        @SuppressLint("DefaultLocale")
        VKRequest request = new VKRequest(EXECUTE,
                VKParameters.from(
                        CODE, String.format(DIALOGS_WITH_USERS_REQUEST_CODE,
                                offset,
                                count,
                                DEFAULT_USER_PARAMS)
                )
        );
        request.setResponseParser(new VKParser() {
            @Override
            public Object createModel(JSONObject object) {
                return new VkDialogs(object);
            }
        });
        request.setUseLooperForCallListener(false);
        return request;
    }

    public static VKRequest createUsersRequest(String usersId) {
        VKRequest request = new VKRequest(GET_USERS,
                VKParameters.from(
                        VKApiConst.USER_IDS, usersId,
                        VKApiConst.FIELDS, DEFAULT_USER_PARAMS
                )
        );
        request.setResponseParser(new VKParser() {
            @Override
            public Object createModel(JSONObject object) {
                return new VKList<>(object, VkUser.class);
            }
        });
        request.setUseLooperForCallListener(false);
        return request;
    }

    public static VKRequest createFriendsRequest(String userId, int offset, int friendsCount) {
        VKRequest request = new VKRequest(GET_FRIENDS,
                VKParameters.from(
                        VKApiConst.USER_ID, userId,
                        OFFSET, offset,
                        COUNT, friendsCount,
                        VKApiConst.FIELDS, createParams(DEFAULT_USER_PARAMS),
                        "order", "hints"
                )
        );
        return request;
    }

    public static VKRequest createLongPullConnectionRequest() {
        VKRequest request = new VKRequest(VK_LONG_PULL,
                VKParameters.from(NEED_PTS_PARAM, 1));
        request.setUseLooperForCallListener(false);
        request.attempts = CONNECT_TO_VK_LONG_PULL_ATTEMPTS;
        return request;
    }

    public static VKRequest createLongPullHistoryRequest(int ts, int pts) {
        VKRequest request = new VKRequest(VK_LONG_PULL_HISTORY,
                VKParameters.from(
                        "ts", ts,
                        "pts", pts,
                        VKApiConst.FIELDS, DEFAULT_USER_PARAMS
                )
        );
        request.setResponseParser(new VKParser() {
            @Override
            public Object createModel(JSONObject object) {
                return new VkDialogsUpdate(object);
            }
        });
        request.setUseLooperForCallListener(false);
        return request;
    }

    public static VKRequest createSendMessageRequest(int to, Message message) {
        VKRequest request = new VKRequest(MESSAGES_SEND,
                VKParameters.from(
                        "peer_id", to,
                        "message", message.getContent()
                )
        );
        request.setResponseParser(new VKParser() {
            @Override
            public Object createModel(JSONObject object) {
                return object.optInt("response");
            }
        });
        request.setUseLooperForCallListener(false);
        return request;
    }

    public static void enqueueRequest(final VKRequest request, final VKRequest.VKRequestListener listener) {
        VKRequest previousRequest = lastRequest;
        lastRequest = request;
        if (previousRequest != null)
            request.executeAfterRequest(previousRequest, new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    listener.onComplete(response);
                    if (lastRequest == request)
                        lastRequest = null;
                }

                @Override
                public void onError(VKError error) {
                    listener.onError(error);
                    if (lastRequest == request)
                        lastRequest = null;
                }

                @Override
                public void onProgress(VKRequest.VKProgressType progressType, long bytesLoaded, long bytesTotal) {
                    listener.onProgress(progressType, bytesLoaded, bytesTotal);
                }
            });
        else {
            request.executeWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    listener.onComplete(response);
                    if (lastRequest == request)
                        lastRequest = null;
                }

                @Override
                public void onError(VKError error) {
                    listener.onError(error);
                    if (lastRequest == request)
                        lastRequest = null;
                }

                @Override
                public void onProgress(VKRequest.VKProgressType progressType, long bytesLoaded, long bytesTotal) {
                    listener.onProgress(progressType, bytesLoaded, bytesTotal);
                }
            });
        }
    }

    // util
    private static String createParams(String... params) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String param : params)
            stringBuilder.append(param).append(',');

        return stringBuilder.toString();
    }

    public static VKRequest createMessagesRequest(int interlocutorId, int fromMessage, int count) {
        VKRequest request = new VKRequest(MESSAGES_GET_HISTORY,
                VKParameters.from(
                        "peer_id", interlocutorId,
                        "count", count,
                        "start_message_id", fromMessage
                )
        );
        request.setResponseParser(new VKParser() {
            @Override
            public Object createModel(JSONObject object) {
                return new VKList<>(object, VkMessage.class);
            }
        });
        request.setUseLooperForCallListener(false);
        return request;
    }

    public static VKRequest createUserRequest(String userId) {
        VKRequest request = new VKRequest(GET_USER,
                VKParameters.from(
                        "user_ids", userId,
                        "fields", createParams(DEFAULT_USER_PARAMS)
                )
        );

        return request;
    }
}
