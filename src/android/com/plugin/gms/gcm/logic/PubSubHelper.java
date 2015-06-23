/*
Copyright 2015 Google Inc. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.plugin.gms.gcm.logic;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.plugin.gms.gcm.PushPlugin;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * This class used to subscribe and unsubscribe to topics.
 */
public class PubSubHelper {
    public static final String TAG = "PubSubHelper";

    private final Context mContext;

    public PubSubHelper(Context context) {
        mContext = context;
    }

    /**
     *
     * @param senderId the project id used by the app's server
     * @param gcmToken the registration token obtained by registering
     * @param topic the topic to subscribe to
     * @param extras bundle with extra parameters
     */
    public void subscribeTopic(final String senderId, final String gcmToken,
                               final String topic, final Bundle extras) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    GcmPubSub.getInstance(mContext).subscribe(gcmToken, topic, extras);
                    Log.i(TAG, "topic subscription succeeded."
                            + "\ngcmToken: " + gcmToken
                            + "\ntopic: " + topic
                            + "\nextras: " + extras);
                    JSONObject json = new JSONObject().put("event", "subscribed");
                    json.put("topic", topic);

                    Log.v(TAG, "subscribeTopic: " + json.toString());

                    // Send this JSON data to the JavaScript application above EVENT should be set to the msg type
                    // In this case this is the registration ID
                    PushPlugin.sendJavascript(json);
                } catch (IOException e) {
                    Log.i(TAG, "topic subscription failed."
                            + "\nerror: " + e.getMessage()
                            + "\ngcmToken: " + gcmToken
                            + "\ntopic: " + topic
                            + "\nextras: " + extras);
                } catch( JSONException e) {
                    // No message to the user is sent, JSON failed
                    Log.e(TAG, "subscribeTopic: JSON exception");
                }
                return null;
            }
        }.execute();
    }

    /**
     *
     * @param senderId the project id used by the app's server
     * @param gcmToken the registration token obtained by registering
     * @param topic the topic to unsubscribe from
     */
    public void unsubscribeTopic(final String senderId, final String gcmToken, final String topic) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    GcmPubSub.getInstance(mContext).unsubscribe(gcmToken, topic);
                    Log.i(TAG, "topic unsubscription succeeded."
                            + "\ngcmToken: " + gcmToken
                            + "\ntopic: " + topic);
                    JSONObject json = new JSONObject().put("event", "unsubscribed");
                    json.put("topic", topic);

                    Log.v(TAG, "unsubscribeTopic: " + json.toString());

                    // Send this JSON data to the JavaScript application above EVENT should be set to the msg type
                    // In this case this is the registration ID
                    PushPlugin.sendJavascript(json);
                } catch (IOException e) {
                    Log.i(TAG, "topic unsubscription failed."
                            + "\nerror: " + e.getMessage()
                            + "\ngcmToken: " + gcmToken
                            + "\ntopic: " + topic);
                } catch( JSONException e) {
                    // No message to the user is sent, JSON failed
                    Log.e(TAG, "unsubscribeTopic: JSON exception");
                }
                return null;
            }
        }.execute();
    }

}
