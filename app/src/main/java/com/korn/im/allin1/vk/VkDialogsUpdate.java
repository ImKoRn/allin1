package com.korn.im.allin1.vk;

import com.korn.im.allin1.vk.pojo.VkMessage;
import com.korn.im.allin1.vk.pojo.VkUser;
import com.vk.sdk.api.model.VKList;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by korn on 27.08.16.
 */
public class VkDialogsUpdate {
    private List<VkMessage> messagesList;
    private List<VkUser> usersList;

    public VkDialogsUpdate(JSONObject object) {
        parse(object);
    }

    public List<VkMessage> getMessagesList() {
        return messagesList;
    }

    public List<VkUser> getUsersList() {
        return usersList;
    }

    private void parse(JSONObject object) {
        object = object.optJSONObject("response");
        messagesList = new VKList<>(
                object.optJSONObject("messages").optJSONArray("items"), VkMessage.class);

        usersList = new VKList<>(object.optJSONArray("profiles"), VkUser.class);
    }

    public boolean isEmpty() {
        return usersList.isEmpty() && messagesList.isEmpty();
    }
}
