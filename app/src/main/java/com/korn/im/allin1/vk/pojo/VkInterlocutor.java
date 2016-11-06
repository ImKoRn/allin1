package com.korn.im.allin1.vk.pojo;

import com.korn.im.allin1.pojo.Interlocutor;

/**
 * Created by korn on 18.09.16.
 */
public interface VkInterlocutor extends Interlocutor {
    void setOnline(boolean online);

    void setOnlineMobile(boolean online);
}
