package com.korn.im.allin1.vk.pojo;

import android.annotation.SuppressLint;

import com.korn.im.allin1.pojo.Dialogs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by korn on 02.09.16.
 */
@SuppressLint("UseSparseArrays")
@SuppressWarnings("unchecked")
public class SynchronizedVkDialogs implements Dialogs<VkDialog> {
    private final Map<Integer, VkDialog> dialogs = Collections.synchronizedMap(new HashMap<Integer, VkDialog>());
    private volatile int unreadDialogCount = 0;
    private volatile int size = 0;

    @Override
    public int size() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public int getUnreadDialogsCount() {
        return unreadDialogCount;
    }

    public void setUnreadDialogsCount(int unreadDialogs) {
        unreadDialogCount = unreadDialogs;
    }

    @Override
    public List<VkDialog> getDialogs() {
        return new ArrayList<>(dialogs.values());
    }

    public void addDialogs(Collection<VkDialog> newDialogs) {
        synchronized (dialogs) {
            for (VkDialog dialog : newDialogs) dialogs.put(dialog.getId(), dialog);
        }
    }

    @Override
    public VkDialog getDialog(int id) {
        return dialogs.get(id);
    }

    public void addDialog(VkDialog newDialog) {
        dialogs.put(newDialog.getId(), newDialog);
    }
}
