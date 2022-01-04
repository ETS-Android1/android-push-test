package com.example.pushdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
//import com.google.firebase.iid.FirebaseInstanceId;
//import com.google.firebase.iid.InstanceIdResult;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelListQuery;
import com.sendbird.android.Member;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.SendBirdPushHandler;
import com.sendbird.android.SendBirdPushHelper;
import com.sendbird.android.User;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    Context mContext = this;

    String mAppID = "6A6CF887-E6F6-4763-84B6-BEFD2410EE42";
    String mUserId = "walter";

    Button butConnect;
    TextView textMessage;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setElementsOnScreen();
        initAll();
    }

    private void setElementsOnScreen() {
        // Button to connect to Sendbird
        butConnect = (Button) findViewById(R.id.butConnect);
        butConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectToSendbird();
            }
        });
        // Textview
        textMessage = (TextView) findViewById(R.id.textMessage);
    }

    private void initAll() {
        // Init Firebase
        FirebaseApp.initializeApp(this);
        // Init Sendbird
        SendBird.init(mAppID, mContext);
        SendBird.setLoggerLevel(98765);  // Messaging SDK log

    }

    private void connectToSendbird() {
        SendBird.connect(mUserId, new SendBird.ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {
                // Define for sendbird which is the handler you want to use for Push messages
                SendBirdPushHelper.registerPushHandler(new MyFirebaseMessagingService());
                // Subscribe to events in the channel
                setChannelHandler();
                // Do some screen changes
                hideAndShowElementsOnScreenAfterConnection();
                // Want to detect going to the background automatically?
                toggleAutoBackgroundDetection(true);
                // List my channels
                listGroupChannels();
            }
        });
    }

    /**
     * If you turn this feature ON, then Sendbird will never
     * disconnect fom SDK even if the application is in the
     * background.
     * IMPORTANT: Android OS can kill your app if it's in the
     * background without notice.
     */
    private void toggleAutoBackgroundDetection(boolean value) {
        SendBird.setAutoBackgroundDetection(value);
    }

    private void hideAndShowElementsOnScreenAfterConnection() {
        butConnect.setVisibility(View.GONE);
        textMessage.setVisibility(View.VISIBLE);
    }

    private void setChannelHandler() {
        SendBird.addChannelHandler("123456789---", new SendBird.ChannelHandler() {
            @Override
            public void onMessageReceived(BaseChannel baseChannel, BaseMessage baseMessage) {
                Log.i(TAG, "CHANNEL HANDLER: New message received");
            }
        });
    }

    private void listGroupChannels() {
        GroupChannelListQuery listQuery = GroupChannel.createMyGroupChannelListQuery();
        listQuery.setIncludeEmpty(true);
        listQuery.setMemberStateFilter(GroupChannelListQuery.MemberStateFilter.JOINED);
        listQuery.setOrder(GroupChannelListQuery.Order.LATEST_LAST_MESSAGE);    // CHRONOLOGICAL, LATEST_LAST_MESSAGE, CHANNEL_NAME_ALPHABETICAL, and METADATA_VALUE_ALPHABETICAL
        listQuery.setLimit(100);
        listQuery.next(new GroupChannelListQuery.GroupChannelListQueryResultHandler() {
            @Override
            public void onResult(List<GroupChannel> list, SendBirdException e) {
                if (e != null) {
                    Log.i(TAG, "Error");
                    e.printStackTrace();
                } else {
                    List<GroupChannel> channels = list;
                    for (int i=0; i < channels.size(); i++) {
                        GroupChannel ch = channels.get( i );
                        List<Member> members = ch.getMembers();
                    }
                }
            }
        });
    }

}