package com.agentk.tictactoe.users;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.agentk.tictactoe.OnlineActivity;
import com.agentk.tictactoe.R;
import com.agentk.tictactoe.databinding.ActivityUserListBinding;
import com.agentk.tictactoe.model.User;
import com.agentk.tictactoe.push_notifications.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.agentk.tictactoe.PrincipalPage.getDefaults;
import static com.agentk.tictactoe.PrincipalPage.setDefaults;

public class UserListActivity extends AppCompatActivity {
    private static final String LOG_TAG = "UserListActivity";
    Context context=this;
    private List<User> users = new ArrayList<>();
    private Adapter adapter;
    private String grid;
    private String status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUserListBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_user_list);
        //       ActionBar actionBar=getSupportActionBar();
        //      actionBar.hide();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        grid=getDefaults("grid",getApplicationContext());
        Log.d( "onCreateGrid: ",grid);
        adapter = new Adapter(this, users, grid);
        binding.list.setAdapter(adapter);
        binding.list.setLayoutManager(new LinearLayoutManager(this));

        TextView emptyList=binding.emptyList;
        emptyList.setVisibility(View.GONE);
        // any time that connectionsRef's value is null (i.e. has no children) I am offline
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myConnectionsRef = database.getReference().child("users").child(uid).child("connections");

        // stores the timestamp of my last disconnect (the last time I was seen online)
        final DatabaseReference lastOnlineRef = database.getReference().child("users").child(uid).child("lastOnline");

        // change status to 0
        final DatabaseReference lastOnlineSt = database.getReference().child("users").child(uid).child("status");

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    DatabaseReference con = myConnectionsRef.push();
                    // when this device disconnects, remove it
                    con.onDisconnect().removeValue();
                    // when I disconnect, update the last time I was seen online
                    lastOnlineRef.onDisconnect().setValue(ServerValue.TIMESTAMP);
                    //update the status to 0
                    lastOnlineSt.onDisconnect().setValue("0");
                    // add this device to my connections list
                    // this value could contain info about the device or a timestamp too
                    con.setValue(Boolean.TRUE);
                    System.out.println("connected");
                    FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("status")
                            .setValue("1");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled at .info/connected");
            }
        });


        fetchUsers();
        fetchUser();
        if(users.size()==0) emptyList.setVisibility(VISIBLE);
    }

    public interface SimpleCallback<T> {
        void callback(T data);
    }



    private void getStatus(@NonNull SimpleCallback<String> finishedCallback){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                .child("status")
                .addValueEventListener(new ValueEventListener (){

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        finishedCallback.callback(String.valueOf(dataSnapshot.getValue()));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    private void delete(){

        Dialog alertDialog = new Dialog(context);

        alertDialog.setContentView(R.layout.delinfo);
        // set the custom dialog components - text, image and button
        TextView text = (TextView) alertDialog.findViewById(R.id.text);

        ProgressBar pb=(ProgressBar) alertDialog.findViewById(R.id.progressBar2);
        pb.setVisibility(View.GONE);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        text.setText(getString(R.string.delete_account)+" "+user.getEmail());
        text.setTextSize(18);

        alertDialog.show();
        Button dialogButton = (Button) alertDialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(V->{
            pb.setVisibility(View.VISIBLE);
            FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                    .removeValue();
            AuthCredential credential = EmailAuthProvider
                    .getCredential(user.getEmail(), "123456");
            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            user.delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(context, getString(R.string.account_deleted), Toast.LENGTH_LONG).show();
                                                startActivity(new Intent(context, OnlineActivity.class));
                                                finish();
                                            }
                                        }
                                    });
                        }
                    });
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_online:
                Dialog alertDialog = new Dialog(context);

                alertDialog.setContentView(R.layout.delinfo);
                // set the custom dialog components - text, image and button
                TextView text = alertDialog.findViewById(R.id.text);

                ProgressBar pb= alertDialog.findViewById(R.id.progressBar2);
                pb.setVisibility(View.GONE);

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String uid = user.getUid();
                String msgText=getString(R.string.account_profile) + " "+(status=="0"?getString(R.string._online):getString(R.string._offline));
                text.setText(msgText);
                text.setTextSize(18);

                alertDialog.show();
                Button dialogButton = alertDialog.findViewById(R.id.dialogButtonOK);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(V->{
                    pb.setVisibility(View.VISIBLE);
                    if(status.equals("0")){
                        FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("status")
                                .setValue("1");
                    }else{
                        FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("status")
                                .setValue("0");
                    }

                    alertDialog.dismiss();
                });
                return true;

            case R.id.action_delete:
                UserListActivity.this.delete();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_bar, menu);
        MenuItem onlineStatus = menu.findItem(R.id.action_online);
//        MenuItem delete = menu.findItem(R.id.action_delete);
        getStatus(new SimpleCallback<String>() {
            @Override
            public void callback(String data) {
                if (data.equals("1")) {
                    status="1";
                    onlineStatus.setIcon(R.drawable.online);
                } else {
                    status="0";
                    onlineStatus.setIcon(R.drawable.offline);
                }
            }
        });


        /*
        menu.add(status=="1"?"Online":"Offline")
                .setIcon(status=="1"?R.drawable.online:R.drawable.offline)
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        UserListActivity.this.userStatus();
                        return true;
                    }
                })
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add("Delete your Account")
                .setIcon(android.R.drawable.ic_delete)
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        UserListActivity.this.delete();
                        return true;
                    }
                })
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                */
        return true;
    }

    private void fetchUser(){
        FirebaseDatabase.getInstance().getReference().child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
              users.clear();
              fetchUsers();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void fetchUsers() {
        FirebaseDatabase.getInstance().getReference().child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User user = snapshot.getValue(User.class);
                            if (!snapshot.getKey().equals(Util.getCurrentUserId())) {
                                if(user.getStatus() != null && user.getStatus().equals("1"))
                                users.add(user);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
