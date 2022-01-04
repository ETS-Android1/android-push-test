package com.example.pushdemo;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sendbird.android.OnPushTokenReceiveListener;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.SendBirdPushHandler;
import com.sendbird.android.SendBirdPushHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;

public class MyFirebaseMessagingService extends SendBirdPushHandler {

    private static final String TAG = "MyFirebaseMsgService";
    private static final AtomicReference<String> pushToken = new AtomicReference<>();

    @Override
    public void onNewToken(String token) {
        Log.i(TAG, "TOKEN: " + token);
        pushToken.set(token);
    }

    @Override
    public void onMessageReceived(Context context, RemoteMessage remoteMessage) {
        String channelUrl = null;
        try {
            Log.i(TAG, "NEW PUSH MESSAGE");
            if (remoteMessage.getData().containsKey("sendbird")) {
                JSONObject sendbird = new JSONObject(remoteMessage.getData().get("sendbird"));
                JSONObject channel = (JSONObject) sendbird.get("channel");
                channelUrl = (String) channel.get("channel_url");
                //decideIfShowMessageOrNot(context, remoteMessage.getData().get("message"), channelUrl);
                // Mark as delivered
                //markAsDeliverFromPush(remoteMessage);
                //callGoogle();
                Log.i(TAG, sendbird.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void callGoogle() {
        try {
            String url = "https://my-json-feed";
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * No matter if this device is connected or not to the Websockets,
     * this method will set this message as delivered.
     */
    private void markAsDeliverFromPush(RemoteMessage remoteMessage) {
        SendBird.markAsDelivered(remoteMessage.getData());
    }

    @Override
    protected boolean isUniquePushToken() {
        return false;
    }

    private void decideIfShowMessageOrNot(Context context, String message, String channelUrl) {
        Log.i(TAG, "New message received: " + message + " for channel: " + channelUrl);
    }

    @Override
    protected boolean alwaysReceiveMessage() {
        return true;
    }


    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//            if (pd.isShowing()){
//                pd.dismiss();
//            }
//            txtJson.setText(result);
//        }


    }


}
