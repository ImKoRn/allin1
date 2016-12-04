package com.korn.im.allin1.vk;

import android.util.Pair;

import com.korn.im.allin1.pojo.Interlocutor;
import com.korn.im.allin1.vk.pojo.VkDialogs;
import com.korn.im.allin1.vk.pojo.VkMessage;
import com.korn.im.allin1.vk.pojo.VkUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class VkJsonParser {
    private static final String RESPONSE = "response";
    private static final String ITEMS = "items";
    private static final String INTERLOCUTORS = "profiles";

    static Map<Integer, VkUser> parseUsers(final JSONObject jsonObject) throws JSONException {
        JSONObject response = jsonObject.optJSONObject(RESPONSE);
        JSONArray usersArray;
        if (response == null)
            usersArray = jsonObject.getJSONArray(RESPONSE);
        else usersArray = response.getJSONArray(ITEMS);
        return parseUsers(usersArray);
    }

    private static Map<Integer, VkUser> parseUsers(JSONArray items) throws JSONException {
        if (items.length() == 0) return Collections.emptyMap();
        if (items.length() == 1) {
            VkUser vkUser = new VkUser(items.getJSONObject(0));
            return Collections.singletonMap(vkUser.getId(), vkUser);
        }

        Map<Integer, VkUser> users = new HashMap<>(items.length());
        for (int i = 0; i < items.length(); i++) {
            VkUser vkUser = new VkUser(items.getJSONObject(i));
            users.put(vkUser.getId(), vkUser);
        }
        return users;
    }

    @SuppressWarnings("unchecked")
    static Pair<VkDialogs, Map<Integer, Interlocutor>> parseDialogs(JSONObject jsonObject) throws JSONException {
        jsonObject = jsonObject.optJSONObject(RESPONSE);
        return Pair.create(new VkDialogs(jsonObject), (Map) parseUsers(jsonObject.optJSONArray(INTERLOCUTORS)));
    }

    static Map<Integer, VkMessage> parseMessages(JSONObject jsonObject) throws
                                                                               JSONException {
        JSONObject response = jsonObject.optJSONObject(RESPONSE);
        JSONArray messagesArray;
        if (response == null)
            messagesArray = jsonObject.getJSONArray(RESPONSE);
        else messagesArray = response.getJSONArray(ITEMS);
        return parseMessages(messagesArray);
    }

    private static Map<Integer, VkMessage> parseMessages(JSONArray messagesArray) throws
                                                                                  JSONException {
        if (messagesArray.length() == 0) return Collections.emptyMap();
        if (messagesArray.length() == 1) {
            VkMessage message = new VkMessage(messagesArray.getJSONObject(0));
            return Collections.singletonMap(message.getId(), message);
        }
        Map<Integer, VkMessage> messages = new HashMap<>(messagesArray.length());
        for (int i = 0; i < messagesArray.length(); i++) {
            VkMessage message = new VkMessage(messagesArray.getJSONObject(i));
            messages.put(message.getId(), message);
        }
        return messages;
    }
}
