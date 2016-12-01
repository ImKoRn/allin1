package com.korn.im.allin1.vk.pojo;

import android.util.Pair;

import com.korn.im.allin1.pojo.Interlocutor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class VkJsonParser {
    private static final String RESPONSE = "response";
    private static final String ITEMS = "items";
    private static final String INTERLOCUTORS = "profiles";

    public static Map<Integer, VkUser> parseUsers(final JSONObject jsonObject) throws JSONException {
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
    public static Pair<VkDialogs, Map<Integer, Interlocutor>> parseDialogs(JSONObject jsonObject) throws JSONException {
        jsonObject = jsonObject.optJSONObject(RESPONSE);
        return Pair.create(new VkDialogs(jsonObject), (Map) parseUsers(jsonObject.optJSONArray(INTERLOCUTORS)));
    }
}
