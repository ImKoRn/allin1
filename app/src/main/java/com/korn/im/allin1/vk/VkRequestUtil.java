package com.korn.im.allin1.vk;

import com.korn.im.allin1.pojo.Message;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;


/**
 * Util class for creating request's
 */
class VkRequestUtil {
    private static final String TAG = "VkRequestUtil";
    // Event
    private static final String GET_DIALOGS = "messages.getDialogs";
    private static final String GET_USERS = "users.get";
    private static final String GET_FRIENDS = "friends.get";
    private static final String GET_USER = "users.get";
    private static final String EXECUTE = "execute";
    private static final String MESSAGES_SEND = "messages.send";
    private static final String VK_LONG_PULL = "messages.getLongPollServer";
    private static final String MESSAGES_GET_HISTORY = "messages.getHistory";
    private static final String VK_LONG_PULL_HISTORY = "messages.getLongPollHistory";

    private static final String DEFAULT_USER_PARAMS = "online, online_mobile, photo_50, photo_100, photo_200, photo_200_orig";
    //TODO Change addition fields of getting profiles
    private static final String DIALOGS_WITH_USERS_REQUEST_CODE =
                    "var dialogs = API.messages.getDialogs({\"offset\" : %s, \"start_message_id\" : %s, \"count\" : %s}); " +
                    "var profiles = API.users.get({\"user_ids\" : dialogs.items@.message@.user_id, \"fields\" : \"%s\"}); " +
                    "return {\"dialogs\" : dialogs} + {\"profiles\" : profiles};";
    private static final int CONNECT_TO_VK_LONG_PULL_ATTEMPTS = 3;

    // Fields
    private static final String OFFSET = "offset";
    private static final String COUNT = "count";
    private static final String CODE = "code";
    private static final String FIELD_PHOTO_200_ORIG = "photo_200_orig";
    private static final String NEED_PTS_PARAM = "need_pts";

    //----------------------------------------------------------------------------------------------

    static VKRequest createDialogsRequest(int offset,
                                          int count,
                                          int lastDialogStamp) {
        String s  = String.format(DIALOGS_WITH_USERS_REQUEST_CODE,
                                  offset,
                                  lastDialogStamp,
                                  count,
                                  DEFAULT_USER_PARAMS);
        return new VKRequest(EXECUTE,
                             VKParameters.from(CODE,s));
    }

    private static VKRequest createUsersRequest(String usersId) {
        return new VKRequest(GET_USERS,
                VKParameters.from(
                        VKApiConst.USER_IDS, usersId,
                        VKApiConst.FIELDS, DEFAULT_USER_PARAMS
                )
        );
    }

    static VKRequest createFriendsRequest(String userId,
                                          int offset,
                                          int friendsCount) {
        return new VKRequest(GET_FRIENDS,
                             VKParameters.from(VKApiConst.USER_ID, userId == null ? "" : userId,
                                               OFFSET, offset,
                                               COUNT, friendsCount,
                                               VKApiConst.FIELDS, createParams(DEFAULT_USER_PARAMS),
                                               "order", "hints"));
    }

    public static VKRequest createLongPullConnectionRequest() {
        VKRequest request = new VKRequest(VK_LONG_PULL,
                VKParameters.from(NEED_PTS_PARAM, 1));
        request.setUseLooperForCallListener(false);
        request.attempts = CONNECT_TO_VK_LONG_PULL_ATTEMPTS;
        return request;
    }

    public static VKRequest createLongPullHistoryRequest(int ts, int pts) {
        return new VKRequest(VK_LONG_PULL_HISTORY,
                VKParameters.from(
                        "ts", ts,
                        "pts", pts,
                        VKApiConst.FIELDS, DEFAULT_USER_PARAMS
                )
        );
    }

    public static VKRequest createSendMessageRequest(int to, Message message) {
        return new VKRequest(MESSAGES_SEND,
                VKParameters.from(
                        "peer_id", to,
                        "message", message.getContent()
                )
        );
    }

    /*public static void enqueueRequest(final VKRequest request, final VKRequest.VKRequestListener listener) {
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
*/
    // util
    private static String createParams(String... params) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String param : params)
            stringBuilder.append(param).append(',');

        return stringBuilder.toString();
    }

    public static VKRequest createMessagesRequest(int interlocutorId, int lastMessageStamp, int count, int offset) {
        return new VKRequest(MESSAGES_GET_HISTORY,
                VKParameters.from(
                        "peer_id", interlocutorId,
                        "start_message_id", lastMessageStamp,
                        "count", count,
                        "offset", offset
                )
        );
    }

    public static VKRequest createUserRequest(String userId) {
        return new VKRequest(GET_USER,
                VKParameters.from(
                        "user_ids", userId,
                        "fields", createParams(DEFAULT_USER_PARAMS)
                )
        );
    }

    public static VKRequest createUserRequest(int[] ids) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int id : ids)
            stringBuilder.append(id).append(',');
        return createUsersRequest(stringBuilder.toString());
    }
}
