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
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.plugin.gms.gcm.PushPlugin;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * This class used to subscribe and unsubscribe to topics.
 */
public class UpstreamHelper {
    public static final String TAG = "UpstreamHelper";

    private final Context mContext;

    public UpstreamHelper(Context context) {
        mContext = context;
    }

    /**
     *
     * @param senderId the project id used by the app's server
     * @param msgId the registration message id
     * @param ttl time to idle live
     * @param extras bundle with extra parameters
     */
    public void sendMessage(final String senderId, final String msgId, final String ttl,
                            final Bundle extras) {
        final GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(mContext);
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    if (isNotEmpty(ttl)) {
                        try {
                            gcm.send(senderId + "@gcm.googleapis.com", msgId,
                                    Long.parseLong(ttl), extras);
                        } catch (NumberFormatException ex) {
                            Log.i(TAG,
                                    "Error sending upstream message: could not parse ttl", ex);
                            return "Error sending upstream message: could not parse ttl";
                        }
                    } else {
                        gcm.send(senderId + "@gcm.googleapis.com", msgId, extras);
                    }
                    Log.i(TAG, "Successfully sent upstream message");
                    return null;
                } catch (IOException ex) {
                    Log.i(TAG, "Error sending upstream message", ex);
                    return "Error sending upstream message:" + ex.getMessage();
                }
            }
/*
            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    Toast.makeText(activity,
                            "send message failed: " + result,
                            Toast.LENGTH_LONG).show();
                }
            }
            */
        }.execute(null, null, null);
    }

    protected static boolean isNotEmpty(String s) {
        return s != null && !"".equals(s.trim());
    }
}
