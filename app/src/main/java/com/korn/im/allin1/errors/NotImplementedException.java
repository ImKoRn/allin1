package com.korn.im.allin1.errors;

public class NotImplementedException extends Exception {
    private static final String DEFAULT_MESSAGE = "Not implemented";

    public NotImplementedException() {
        super(DEFAULT_MESSAGE);
    }

    public NotImplementedException(String detailMessage) {
        super(detailMessage);
    }
}
