package com.agentk.tictactoe.push_notifications;

/**
 * Created by AgentK on 13/02/2018.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.agentk.tictactoe.Grid4Activity;
import com.agentk.tictactoe.Grid5Activity;
import com.agentk.tictactoe.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.agentk.tictactoe.model.User;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.agentk.tictactoe.push_notifications.Util.getCurrentUserId;


public class MyReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = "MyReceiver";
    String userName,fromName;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "onReceive: " + intent.getAction());
        FirebaseDatabase.getInstance().getReference().child("users")
                .child(getCurrentUserId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User me = dataSnapshot.getValue(User.class);
                        userName=me.getName();
                        OkHttpClient client = new OkHttpClient();

                        String to = intent.getExtras().getString("to");

                        String grid = intent.getExtras().getString("grid");

                        fromName = intent.getExtras().getString("fromName");

                        String withId = intent.getExtras().getString("withId");

                        String format = String
                                .format("https://us-central1-coral-atom-188404.cloudfunctions.net/sendNotification?to=%s&fromPushId=%s&fromId=%s&fromName=%s&grid=%s&type=%s", to, me.getPushId(), getCurrentUserId(), me.getName(),grid, intent.getAction());

                        Log.d(LOG_TAG, "onDataChange: " + format);
                        Request request = new Request.Builder()
                                .url(format)
                                .build();

                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {

                            }
                        });

                        if (intent.getAction().equals("accept")) {
                            String gameId = withId + "-" + getCurrentUserId()+ "-" +grid;
                            FirebaseDatabase.getInstance().getReference().child("games")
                                    .child(gameId)
                                    .setValue(null);

                            context.startActivity(new Intent(context,grid.equals("3")? MainActivity.class:grid.equals("4")? Grid4Activity.class: Grid5Activity.class)
                                    .putExtra("level", "22")
                                    .putExtra("me", "-1")
                                    .putExtra("gameId", gameId)
                                    .putExtra("userName", userName)
                                    .putExtra("fromName", fromName));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }
}