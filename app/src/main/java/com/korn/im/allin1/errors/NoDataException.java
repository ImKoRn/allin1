package com.korn.im.allin1.errors;

public class NoDataException extends Exception {
    private static final String DEFAULT_MESSAGE = "No data found";

    public NoDataException() {
        super(DEFAULT_MESSAGE);
    }

    public NoDataException(String detailMessage) {
        super(detailMessage);
    }
}
