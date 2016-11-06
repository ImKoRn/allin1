package com.korn.im.allin1;

import com.korn.im.allin1.vk.pojo.VkUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VkJsonParser {
    public static final String ITEMS = "items";
    public static final String RESPONSE = "response";

    public static List<VkUser> parseUsers(JSONObject jsonObject) {
        return parseUsers(jsonObject.optJSONObject(RESPONSE).optJSONArray(ITEMS));
    }

    public static List<VkUser> parseUsers(JSONArray items) {
        List<VkUser> users = new ArrayList<>(items.length());
        for (int i = 0; i < items.length(); i++)
            try {
                users.add(new VkUser(items.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        return users;
    }
}
