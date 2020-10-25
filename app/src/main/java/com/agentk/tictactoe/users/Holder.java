package com.agentk.tictactoe.users;

/**
 * Created by AgentK on 13/02/2018.
 */

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.agentk.tictactoe.databinding.UserListItemBinding;
import com.agentk.tictactoe.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.agentk.tictactoe.push_notifications.Util.getCurrentUserId;


public class Holder extends RecyclerView.ViewHolder {
    public UserListItemBinding binding;

    public Holder(View itemView) {
        super(itemView);
        binding = DataBindingUtil.bind(itemView);
        binding.invite.setOnClickListener(v -> {
            DatabaseReference db = FirebaseDatabase.getInstance().getReference();
            db.child("users")
                    .child(getCurrentUserId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User me = dataSnapshot.getValue(User.class);

                            OkHttpClient client = new OkHttpClient();

                //            String grid = intent.getExtras().getString("grid");

          //                  Log.d( "Grid: ",binding.getUser().getPushId());
                            String grid=binding.getUser().getGrid();
                            String to = binding.getUser().getPushId();
                            Request request = new Request.Builder()
                                    .url(String
                                            .format("https://us-central1-coral-atom-188404.cloudfunctions.net/sendNotification?to=%s&fromPushId=%s&fromId=%s&fromName=%s&grid=%s&type=%s", to, me.getPushId(),getCurrentUserId() ,me.getName(),grid, "invite"))
                                    .build();

                            client.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Log.d( "onFailure: ", String.valueOf(e));
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        });
    }
}
