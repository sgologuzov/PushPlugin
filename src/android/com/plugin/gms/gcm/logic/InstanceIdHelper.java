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
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.plugin.gms.gcm.PushPlugin;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * This class used to register and unregister the app for GCM.
 * Registration involves getting the app's instance id and using it to request a token with
 * the scope {@link GoogleCloudMessaging.INSTANCE_ID_SCOPE} and the audience set to the project's
 * id.
 */
public class InstanceIdHelper {
    public static final String TAG = "InstanceIdHelper";


    private final Context mContext;

    public InstanceIdHelper(Context context) {
        mContext = context;
    }

    /**
     * Register for GCM
     *
     * @param senderId the project id used by the app's server
     */
    public void getGcmTokenInBackground(final String senderId) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    String token =
                            InstanceID.getInstance(mContext).getToken(senderId,
                                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    Log.i(TAG, "registration succeeded." +
                            "\nsenderId: " + senderId + "\ntoken: " + token);
                    JSONObject json = new JSONObject().put("event", "registered");
                    json.put("regid", token);

                    Log.v(TAG, "onRegistered: " + json.toString());

                    // Send this JSON data to the JavaScript application above EVENT should be set to the msg type
                    // In this case this is the registration ID
                    PushPlugin.sendJavascript(json);
                } catch (final IOException e) {
                    Log.i(TAG, "registration failed." +
                            "\nsenderId: " + senderId + "\nerror: " + e.getMessage());
                } catch( JSONException e) {
                    // No message to the user is sent, JSON failed
                    Log.e(TAG, "getGcmTokenInBackground: JSON exception");
                }
                return null;
            }
        }.execute();
    }

    /**
     * Unregister by deleting the token
     *
     * @param senderId the project id used by the app's server
     */
    public void deleteGcmTokeInBackground(final String senderId) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    InstanceID.getInstance(mContext).deleteToken(senderId,
                            GoogleCloudMessaging.INSTANCE_ID_SCOPE);
                    Log.i(TAG, "delete token succeeded." +
                            "\nsenderId: " + senderId);
                } catch (final IOException e) {
                    Log.i(TAG, "remove token failed." +
                            "\nsenderId: " + senderId + "\nerror: " + e.getMessage());
                }
                return null;
            }
        }.execute();
    }
}
