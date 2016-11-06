package com.korn.im.allin1.accounts;

/**
 * Created by korn on 10.09.16.
 */
public class Event {
    public static final int REQUEST_TYPE_CACHE = -1;
    public static final int REQUEST_TYPE_LOAD_NEXT = 0;
    public static final int REQUEST_TYPE_UPDATE = 1;

    public static final int REQUEST_DATA_TYPE_DIALOGS = 1;
    public static final int REQUEST_DATA_TYPE_FRIENDS = 2;
    public static final int REQUEST_DATA_TYPE_MESSAGES = 3;

    private final int requestType;
    private final int requestDataType;
    private final int accountType;
    private final int attachedId;

    public Event(int requestType, int requestDataType, int accountType, int attachedId) {
        this.requestType = requestType;
        this.requestDataType = requestDataType;
        this.accountType = accountType;
        this.attachedId = attachedId;
    }

    public int getRequestType() {
        return requestType;
    }

    public int getRequestDataType() {
        return requestDataType;
    }

    public int getAccountType() {
        return accountType;
    }

    public int getAttachedId() {
        return attachedId;
    }
}
