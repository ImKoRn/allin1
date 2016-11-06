package com.korn.im.allin1.vk.pojo.newvkpojo;

import android.annotation.SuppressLint;

import com.korn.im.allin1.pojo.Dialogs;
import com.korn.im.allin1.vk.pojo.VkDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ParcelCreator")
@SuppressWarnings("unchecked")
public class VkDialogs implements Dialogs<VkDialog> {
    private static final String DIALOGS_FIELD = "dialogs";
    private static final String COUNT_FIELD = "dialogs";
    private static final String UNREAD_DIALOGS_FIELD = "unread_dialogs";
    private static final String ITEMS_FIELD = "dialogs";

    private List<VkDialog> dialogs;
    //private int count;
    private int unreadDialogsCount;

    public VkDialogs(Dialogs dialogs) {
        this.unreadDialogsCount = dialogs.getUnreadDialogsCount();
        this.dialogs = new ArrayList<>(dialogs.getDialogs());
    }

    public VkDialogs(JSONObject from) {
        parse(from);
    }

    public void parse(JSONObject source) {
        JSONObject dialogs = source.optJSONObject(DIALOGS_FIELD);
        //this.count = dialogs.optInt(COUNT_FIELD);
        this.unreadDialogsCount = dialogs.optInt(UNREAD_DIALOGS_FIELD);
        parseDialogs(dialogs.optJSONArray(ITEMS_FIELD));
    }

    private void parseDialogs(JSONArray dialogs) {
        this.dialogs = new ArrayList<>(dialogs.length());
        for (int i = 0; i < dialogs.length(); i++)
            try {
                this.dialogs.add(new VkDialog(dialogs.getJSONObject(i)));
            } catch (JSONException ignored) {}
    }

    @Override
    public int getUnreadDialogsCount() {
        return unreadDialogsCount;
    }

    @Override
    public int size() {
        return dialogs.size();
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

    public void addDialog(VkDialog dialog) {
        dialogs.add(dialog);
    }
}
