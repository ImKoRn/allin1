package com.korn.im.allin1.retrofit;

import com.korn.im.allin1.vk.VkLongPullResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by korn on 09.08.16.
 */
public interface VkLongPullVkRequest {
    //Act
    String DEFAULT_ACT = "a_check";

    //Wait
    int DEFAULT_WAIT = 25;

    //Modes
    int ATTACHMENT_MODE = 2;
    int EXTENDED_IVENTS_MODE = 8;
    int RETURN_TPS_MODE = 32;
    int RETURN_ONLINE_FLAG = 64;
    int RETURN_RUNDOM_ID_MODE = 128;

    @GET
    Call<VkLongPullResponse> request(@Url String serverUrl,
                                     @Query(value = "act") String act,
                                     @Query(value = "key") String key,
                                     @Query(value = "ts") int ts,
                                     @Query(value = "wait") int wait,
                                     @Query(value = "mode") int mode);
}