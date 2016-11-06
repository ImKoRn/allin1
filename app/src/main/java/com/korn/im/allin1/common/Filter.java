package com.korn.im.allin1.common;

/**
 * Created by korn on 06.08.16.
 */
public interface Filter<T> {
    boolean apply(T obj);
    boolean empty();
}
