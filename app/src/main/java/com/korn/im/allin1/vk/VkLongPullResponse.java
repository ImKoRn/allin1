package com.korn.im.allin1.vk;

import android.util.Log;
import android.util.Pair;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;

/**
 * Created by korn on 09.08.16.
 */
public class VkLongPullResponse {
    public static final int NO_FAILED = 0;

    public int ts;
    public VkLongPullEvent data;

    public int failed = NO_FAILED;


    public static class VkLongPullResponseDeserializer implements JsonDeserializer<VkLongPullResponse> {
        @Override
        public VkLongPullResponse deserialize(JsonElement json, Type typeOfT,
                                              JsonDeserializationContext context) throws JsonParseException {
            VkLongPullResponse response = new VkLongPullResponse();
            JsonPrimitive jsonPrimitive;
            JsonObject jsonObject = json.getAsJsonObject();

            if ((jsonPrimitive = jsonObject.getAsJsonPrimitive("ts")) != null)
                response.ts = jsonPrimitive.getAsInt();

            if ((jsonPrimitive = jsonObject.getAsJsonPrimitive("failed")) != null) {
                response.failed = jsonPrimitive.getAsInt();
                return response;
            }

            Log.v("Long pull", json.toString());

            response.data = getUpdates(jsonObject.getAsJsonArray("updates"));

            return response;
        }

        public VkLongPullEvent getUpdates(JsonArray updatesJsonArray) {
            if(updatesJsonArray.size() == 0)
                return null;

            Log.i("VkLongPullService", updatesJsonArray.toString());

            VkLongPullEvent updates = new VkLongPullEvent();
            JsonArray event;
            Log.i("TAG", updatesJsonArray.toString());
            for (JsonElement jsonElement : updatesJsonArray) {
                event = jsonElement.getAsJsonArray();
                switch (event.get(0).getAsInt()) {
                    /*case 0 : {


                        break;
                    }
                    case 1 : {


                        break;
                    }*/
                    case 2 : {
                        /*if(event.size() != 4)
                            break;
                        updates.addMessageFlagEvent(event.get(3).getAsInt(),
                                new Pair<>(
                                        event.get(2).getAsInt(),
                                        event.get(1).getAsInt()
                                )
                        );*/
                        break;
                    }
                    case 3 : {
                        /*if(event.size() != 4)
                            break;
                        updates.addMessageFlagEvent(event.get(3).getAsInt(),
                                new Pair<>(
                                        event.get(1).getAsInt(),
                                        -event.get(2).getAsInt()
                                )
                        );*/
                        break;
                    }
                    case 6 : {
                        updates.addMessageReadEvent(event.get(1).getAsInt(),
                                new Pair<>(
                                        event.get(2).getAsInt(),
                                        false
                                )
                        );
                        break;
                    }
                    case 7 : {
                        updates.addMessageReadEvent(event.get(1).getAsInt(),
                                new Pair<>(
                                        event.get(2).getAsInt(),
                                        true
                                )
                        );
                        break;
                    }
                    case 8 : {
                        //updates.addFriendStatusEvent(-event.get(1).getAsInt(), event.get(2).getAsInt() & VkUser.MASK);
                        break;
                    }
                    case 9 : {
                        //updates.addFriendStatusEvent(-event.get(1).getAsInt(), VkUser.OFFLINE);
                        break;
                    }
                    case 61 : {
                        updates.addDialogWriteEvent(event.get(1).getAsInt(), event.get(1).getAsInt());
                        break;
                    }
                    case 62 : {
                        updates.addDialogWriteEvent(event.get(2).getAsInt(), event.get(1).getAsInt());
                        break;
                    }
                }
            }

            return updates;
        }
    }
}
