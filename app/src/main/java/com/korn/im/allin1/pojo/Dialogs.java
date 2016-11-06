package com.korn.im.allin1.pojo;

import java.util.List;

/**
 * Created by korn on 26.08.16.
 */
public interface Dialogs<TDialog extends Dialog> {
    int size();
    int getUnreadDialogsCount();

    List<TDialog> getDialogs();
    TDialog getDialog(int id);
}
