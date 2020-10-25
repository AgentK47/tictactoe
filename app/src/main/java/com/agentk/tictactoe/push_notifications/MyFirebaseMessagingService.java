package com.agentk.tictactoe.push_notifications;

/**
 * Created by AgentK on 13/02/2018.
 */

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.agentk.tictactoe.Grid4Activity;
import com.agentk.tictactoe.Grid5Activity;
import com.agentk.tictactoe.MainActivity;
import com.agentk.tictactoe.OnlineActivity;
import com.agentk.tictactoe.R;
import com.agentk.tictactoe.model.User;
import com.agentk.tictactoe.users.UserListActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static android.support.v4.app.NotificationCompat.PRIORITY_MAX;
import static com.agentk.tictactoe.push_notifications.Util.getCurrentUserId;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String LOG_TAG = "MyFirebaseMessaging";
    public static final String INVITE = "invite";
    public static final String GUEST = "guest";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String fromPushId = remoteMessage.getData().get("fromPushId");
        String fromId = remoteMessage.getData().get("fromId");
        String fromName = remoteMessage.getData().get("fromName");
        String type = remoteMessage.getData().get("type");
        String grid = remoteMessage.getData().get("grid");
        Log.d(LOG_TAG, "onMessageReceived: ");
        Log.d(LOG_TAG, type);

        if (type.equals("invite")) {
            handleInviteIntent(fromPushId, fromId, fromName, grid);
        } else if (type.equals("accept")) {
            Log.i(LOG_TAG, "votre invitation a été accepté ");
            DatabaseReference db = FirebaseDatabase.getInstance().getReference();
            db.child("users")
                    .child(getCurrentUserId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User me = dataSnapshot.getValue(User.class);

                            startActivity(new Intent(getBaseContext(),grid.equals("3")? MainActivity.class:grid.equals("4")? Grid4Activity.class: Grid5Activity.class)
                                    .putExtra("level", "22")
                                    .putExtra("me", "1")
                                    .putExtra("userName", fromName)
                                    .putExtra("fromName", me.getName())
                                    .putExtra("gameId", getCurrentUserId() + "-" + fromId + "-" + grid)
                                    .putExtra("withId", fromId));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            System.out.println("The read failed: " + databaseError.getCode());
                        }
                    });

        } else if (type.equals("reject")) {
            // todo update to Oreo notifications
            Log.i(LOG_TAG, "votre invitation a été rejeté ");
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this, GUEST)
                            .setSmallIcon(R.drawable.icone)
                            .setPriority(PRIORITY_MAX)
                            .setContentTitle(String.format("%s "+getString(R.string.game_rejected), fromName));

            Intent resultIntent = new Intent(this, OnlineActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(OnlineActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
            mBuilder.setAutoCancel(true);
    //        mBuilder.build().flags |= Notification.FLAG_AUTO_CANCEL;
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(1, mBuilder.build());
        }
    }

    private void handleInviteIntent(String fromPushId, String fromId, String fromName, String grid) {

        Intent rejectIntent = new Intent(getApplicationContext(), MyReceiver.class)
                .setAction("reject")
                .putExtra("withId", fromId)
                .putExtra("to", fromPushId);

        PendingIntent pendingIntentReject = PendingIntent.getBroadcast(this, 0, rejectIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent acceptIntent = new Intent(getApplicationContext(), MyReceiver.class)
                .setAction("accept")
                .putExtra("withId", fromId)
                .putExtra("fromName", fromName)
                .putExtra("grid", grid)
                .putExtra("to", fromPushId);
        PendingIntent pendingIntentAccept = PendingIntent.getBroadcast(this, 2, acceptIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String gameId = fromId + "-" + getCurrentUserId()+ "-" + grid;
        FirebaseDatabase.getInstance().getReference().child("games")
                .child(gameId)
                .setValue(null);

        Intent resultIntent = new Intent(getBaseContext(),grid.equals("3")? MainActivity.class:grid.equals("4")? Grid4Activity.class: Grid5Activity.class)
                .putExtra("level", "22")
                .putExtra("withId", fromId)
                .putExtra("gameId", gameId)
                .putExtra("userName", fromName)
                .putExtra("fromName", fromName)
                .putExtra("me", "-1")
                .putExtra("to", fromPushId);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(OnlineActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        Notification build = new NotificationCompat.Builder(this, INVITE)
                .setSmallIcon(R.drawable.icone)
                .setPriority(PRIORITY_MAX)
                .setContentTitle(String.format("%s "+getString(R.string.game_invite), fromName))
                .addAction(android.R.drawable.ic_input_add, getString(R.string.game_accept), pendingIntentAccept)
                .setVibrate(new long[3000])
                .setChannelId(INVITE)
                .setContentIntent(resultPendingIntent)
                .addAction(android.R.drawable.ic_delete, getString(R.string.game_reject), pendingIntentReject)
                .setAutoCancel(true)
                .build();


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(INVITE, INVITE, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        notificationManager.notify(1, build);
   //     notificationManager.cancel(1);
    }
}
