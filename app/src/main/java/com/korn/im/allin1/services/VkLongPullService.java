/*
package com.korn.im.allin1.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.gson.GsonBuilder;
import com.korn.im.allin1.retrofit.VkLongPullVkRequest;
import com.korn.im.allin1.vk.VkLongPullEvent;
import com.korn.im.allin1.vk.VkLongPullResponse;
import com.korn.im.allin1.vk.VkRequestUtil;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.subjects.PublishSubject;


*/
/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 *//*

public class VkLongPullService extends IntentService {
    private static final String TAG = "VkLongPullService";
    //Connection to long pull constants
    private static final String RESPONSE_FIELD = "response";
    private static final String SERVER_FIELD = "server";
    private static final String KEY_FIELD = "key";
    private static final String TS_FIELD = "ts";

    private static final String PTS_FILED = "pts";
    private static final String BASE_URL = "https://";

    //private final PublishSubject<VkLongPullData>;

    private static String server;
    private static String key;
    private static int ts;
    private static int pts;


    private static boolean connected = false;

    private OnServiceStopListener stopListener;
    private boolean run = true;
    private PublishSubject<VkLongPullEvent> eventSubject = PublishSubject.create();

    private VkLongPullResponse longPullResponse;
    private VkLongPullVkRequest service;

    public VkLongPullService() {
        super("VkLongPullService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null)
            return;

        */
/*AccountManager.getInstance().getVkAccount()
                .getEngine().setLongPullService(this);*//*


        connectToVkLongPull();

        initRetrofit();

        while (run) try {
            Log.i(TAG, "Binding to Vk Server");
            longPullResponse = service.request(BASE_URL + server, VkLongPullVkRequest.DEFAULT_ACT,
                            key, ts, pts, VkLongPullVkRequest.ATTACHMENT_MODE +
                                    VkLongPullVkRequest.RETURN_ONLINE_FLAG)
                    .execute().body();
            ts = longPullResponse.ts;

            Log.i(TAG, "Check vk mail");
            VkRequestUtil.createLongPullHistoryRequest(ts, pts)
                    .executeSyncWithListener(new VKRequest.VKRequestListener() {
                        @Override
                        public void onError(VKError error) {
                            super.onError(error);
                        }

                        @Override
                        public void onComplete(VKResponse response) {
                            super.onComplete(response);
                            longPullResponse.data.addDialogsUpdate((VkDialogsUpdate) response.parsedModel);
                            pts = response.json.optJSONObject("response").optInt("new_pts", pts);

                            if (run && eventSubject.hasObservers())
                                eventSubject.onNext(longPullResponse.data);
                        }
                    });
        } catch (IOException e) {
            if (run) Log.e(TAG, "DATA LOST");
            else break;
        }
    }

    public void stop() {
        run = false;
    }

    public Observable<VkLongPullEvent> getEventObservable() {
        return eventSubject.asObservable();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(stopListener != null)
            stopListener.onStop();
        stopListener = null;
        eventSubject.onCompleted();
    }

    private void connectToVkLongPull() {
        if(connected)
            return;

        VkRequestUtil.createLongPullConnectionRequest()
                .executeSyncWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                JSONObject responseJson = response.json.optJSONObject(RESPONSE_FIELD);
                server = responseJson.optString(SERVER_FIELD);
                key = responseJson.optString(KEY_FIELD);
                ts = responseJson.optInt(TS_FIELD);
                pts = responseJson.optInt(PTS_FILED);
                connected = true;
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                stopSelf();
            }
        });
    }

    private void initRetrofit() {
        if(service != null)
            return;

        service = new Retrofit.Builder()
                .baseUrl("http://place/")
                .client(new OkHttpClient.Builder()
                        .readTimeout(1, TimeUnit.MINUTES)
                        .connectTimeout(1, TimeUnit.MINUTES)
                        .writeTimeout(1, TimeUnit.MINUTES)
                        .build())
                .addConverterFactory(GsonConverterFactory
                        .create(new GsonBuilder()
                                .registerTypeAdapter(VkLongPullResponse.class,
                                        new VkLongPullResponse.VkLongPullResponseDeserializer())
                                .create()))
                .build().create(VkLongPullVkRequest.class);
    }

    public boolean isRun() {
        return run;
    }

    public void setStopListener(OnServiceStopListener stopListener) {
        this.stopListener = stopListener;
    }

    public interface OnServiceStopListener {
        void onStop();
    }
}
*/
