package com.korn.im.allin1.vk.pojo;

import android.util.Pair;

import com.korn.im.allin1.pojo.Interlocutor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VkJsonParser {
    public static final String RESPONSE = "response";
    private static final String ITEMS = "items";
    private static final String INTERLOCUTORS = "profiles";

    public static Map<Integer, VkUser> parseUsers(JSONObject jsonObject) throws JSONException {
        return parseUsers(jsonObject.getJSONObject(RESPONSE).getJSONArray(ITEMS));
    }

    private static Map<Integer, VkUser> parseUsers(JSONArray items) throws JSONException {
        Map<Integer, VkUser> users = new HashMap<>(items.length());
        VkUser vkUser;
        for (int i = 0; i < items.length(); i++) {
            vkUser = new VkUser(items.getJSONObject(i));
            users.put(vkUser.getId(), vkUser);
        }
        return users;
    }

    public static Pair<VkDialogs, Map<Integer, ? extends Interlocutor>> parseDialogs(JSONObject jsonObject) throws JSONException {
        jsonObject = jsonObject.optJSONObject(RESPONSE);
        return Pair.create(new VkDialogs(jsonObject),
                parseUsers(jsonObject.optJSONArray(INTERLOCUTORS)));
    }
}
