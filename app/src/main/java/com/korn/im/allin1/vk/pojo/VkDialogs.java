package com.korn.im.allin1.vk.pojo;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.util.Log;

import com.korn.im.allin1.pojo.Dialog;
import com.korn.im.allin1.pojo.Dialogs;
import com.vk.sdk.api.model.VKApiGetDialogResponse;
import com.vk.sdk.api.model.VKList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by korn on 14.08.16.
 */
@SuppressLint("ParcelCreator")
@SuppressWarnings("unchecked")
public class VkDialogs extends VKApiGetDialogResponse implements Dialogs<VkDialog> {
    private static final String TAG = "VkDialogs";
    private List<VkDialog> dialogs;
    private Map<Integer, VkInterlocutor> interlocutors;

    public VkDialogs(JSONObject from) {
        parse(from);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {}

    @Override
    public VKApiGetDialogResponse parse(JSONObject source) {
        JSONObject response = source.optJSONObject("response");
        this.count = response.optInt("count");
        this.unread_dialogs = response.optInt("unread_dialogs");
        this.dialogs = new ArrayList<>(new VKList<>(response.optJSONArray("items"), VkDialog.class));
        parseInterlocutors(response.optJSONArray("profiles"));
        return this;
    }

    private void parseInterlocutors(JSONArray usersJson) {
        VkUser user;
        interlocutors = new HashMap<>(usersJson.length());
        for (int i = 0; i < usersJson.length(); i++)
            try {
                user = new VkUser(usersJson.getJSONObject(i));
                interlocutors.put(user.getId(), user);
            } catch (JSONException e) {
                Log.e(TAG, "One of data corrupted");
            }

        for (Dialog dialog  : dialogs)
            if (dialog.isChat())
                interlocutors.put(dialog.getId(), (VkInterlocutor) dialog);
    }

    @Override
    public int getUnreadDialogsCount() {
        return unread_dialogs;
    }

    @Override
    public int size() {
        return dialogs.size();
    }

    public Collection<VkInterlocutor> getInterlocutors() {
        return interlocutors.values();
    }

    public VkInterlocutor getInterlocutor(int id) {
        return interlocutors.get(id);
    }

    @Override
    public List<VkDialog> getDialogs() {
        return dialogs;
    }

    @Override
    public VkDialog getDialog(int id) {
        for (VkDialog dialog : dialogs)
            if (dialog.getId() == id)
                return dialog;

        return null;
    }

    public void addInterlocutor(VkInterlocutor interlocutor) {
        interlocutors.put(interlocutor.getId(), interlocutor);
    }

    public void addDialog(VkDialog dialog) {
        dialogs.add(dialog);
    }
}
