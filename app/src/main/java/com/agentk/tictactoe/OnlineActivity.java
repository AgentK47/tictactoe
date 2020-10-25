package com.agentk.tictactoe;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.agentk.tictactoe.model.User;
import com.agentk.tictactoe.users.UserListActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import static android.text.TextUtils.isEmpty;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_SHORT;
import static com.agentk.tictactoe.PrincipalPage.getDefaults;
import static com.agentk.tictactoe.PrincipalPage.setDefaults;
import static com.agentk.tictactoe.push_notifications.Util.getCurrentUserId;
import static com.agentk.tictactoe.push_notifications.Util.savePushToken;

public class OnlineActivity extends AppCompatActivity {
    MediaPlayers mp;
    Context context=this;
    EditText input_name;
    EditText input_email;
    EditText input_password;
    ProgressBar progress;
    Dialog alertDialog;
    Button verifcode;
    private boolean logginIn;

    public void startMultilayer() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (!arePlayServicesOk()) {
            return;
        }
        if (isAnonymous()) {
            alertDialog.show();
        } else {

            Task<Void> usertask = auth.getCurrentUser().reload();
            usertask.addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    FirebaseUser userVer = auth.getCurrentUser();
                    if(userVer.isEmailVerified()) {
                        startActivity(new Intent(context, UserListActivity.class));
                    }else{
                        Toast.makeText(OnlineActivity.this,
                                "Verify your Email, Please " ,
                                Toast.LENGTH_SHORT).show();

                        verifcode.setVisibility(View.VISIBLE);

                        //mettre un bouton pour re-envoyer le code de verification si besoin
                        verifcode.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mp.playMusic(1);
                                userVer.sendEmailVerification()
                                        .addOnCompleteListener(OnlineActivity.this, new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {
                                                // Re-enable button
                                                //                              findViewById(R.id.verify_email_button).setEnabled(true);

                                                if (task.isSuccessful()) {
                                                    Toast.makeText(OnlineActivity.this,
                                                            "Verification email sent to " + userVer.getEmail(),
                                                            Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Log.e("OnlineActivityTag", "sendEmailVerification", task.getException());
                                                    Toast.makeText(OnlineActivity.this,
                                                            "Failed to send verification email.",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                            }
                        });
                    }
                }

            });

        }
    }

    public void loginWithEmail(View view) {
        String email = input_email.getText().toString().trim();
        String name = input_name.getText().toString();

 //       String password = input_password.getText().toString();

        Log.d("loginWithEmail: ",email);
        mp.playMusic(1);
        if (logginIn) {
            return;
        }
        if (isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, getString(R.string.correct_email), LENGTH_SHORT).show();
            return;
        }
        if (isEmpty(name) || name.trim().length() == 0) {
            Toast.makeText(this, getString(R.string.correct_name), LENGTH_SHORT).show();
            return;
        }
        /*
        if (password.length() < 6) {
            Toast.makeText(this, getString(R.string.correct_password), LENGTH_SHORT).show();
            return;
        }
        */
        logginIn = true;
        showProgressDialog();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, "123456")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        final FirebaseUser user = auth.getCurrentUser();
                        user.sendEmailVerification()
                                .addOnCompleteListener(this, new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        // Re-enable button
          //                              findViewById(R.id.verify_email_button).setEnabled(true);

                                        if (task.isSuccessful()) {
                                            Toast.makeText(OnlineActivity.this,
                                                    "Verification email sent to " + user.getEmail(),
                                                    Toast.LENGTH_SHORT).show();
                                        } else {
                                            Log.e("OnlineActivityTag", "sendEmailVerification", task.getException());
                                            Toast.makeText(OnlineActivity.this,
                                                    "Failed to send verification email.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        String uid = auth.getCurrentUser().getUid();

                        User userN = new User(name,"-1");
                        FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                                .setValue(userN);

                        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                        savePushToken(refreshedToken, uid);


                    } else {
                        Log.d("onlineActivity", "loginWithEmail: unsuccessful"+task.getException().getMessage());
                        auth.signInWithEmailAndPassword(email, "123456")
                                .addOnCompleteListener(task1 -> {
                                    if (!isAnonymous()) {
                                        final FirebaseUser userVer = auth.getCurrentUser();
                                        if(userVer.isEmailVerified()){
                                            Log.d("onlineActivity", "loginWithEmail: ");
                                            String uid = auth.getCurrentUser().getUid();

                                            User user = new User(name,"1");
                                            FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                                                    .setValue(user);

                                            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                                            savePushToken(refreshedToken, uid);

                                            startActivity(new Intent(this, UserListActivity.class));
                                        }else{
                                            Toast.makeText(OnlineActivity.this,
                                                    "Verify your Email, Please " ,
                                                    Toast.LENGTH_SHORT).show();
                                            userVer.sendEmailVerification()
                                                    .addOnCompleteListener(this, new OnCompleteListener() {
                                                        @Override
                                                        public void onComplete(@NonNull Task task) {
                                                            // Re-enable button
                                                            //                              findViewById(R.id.verify_email_button).setEnabled(true);

                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(OnlineActivity.this,
                                                                        "Verification email sent to " + userVer.getEmail(),
                                                                        Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Log.e("OnlineActivityTag", "sendEmailVerification", task.getException());
                                                                Toast.makeText(OnlineActivity.this,
                                                                        "Failed to send verification email.",
                                                                        Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });

                                        }
                                    }
                                });

      /*                  auth.signInWithEmailAndPassword(email, "123456")
                                .addOnCompleteListener(task1 -> {
                                    if (!isAnonymous()) {
                                        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

                                        savePushToken(refreshedToken, getCurrentUserId());

                                        startActivity(new Intent(this, UserListActivity.class));
                                        FirebaseDatabase.getInstance().getReference().child("users").child(getCurrentUserId())
                                                .child("status")
                                                .onDisconnect()     // Set up the disconnect hook
                                                .setValue("0");
                                    }
                                });*/
                    }

                    alertDialog.dismiss();
                });
    }

    private void showProgressDialog() {
        progress.setVisibility(VISIBLE);
    }

 //   private void hideProgressDialog() { progress.setVisibility(GONE);    }

    private boolean isAnonymous() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return currentUser == null || currentUser.isAnonymous();
    }

    private boolean arePlayServicesOk() {
        final GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        final int resultCode = googleAPI.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(resultCode)) {
                googleAPI.getErrorDialog(this, resultCode, 5000).show();
            }
            return false;
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online);

        ActionBar actionBar=getSupportActionBar();
        actionBar.hide();

        mp=new MediaPlayers(this);

        alertDialog = new Dialog(context);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.reg_info);

        input_name=alertDialog.findViewById(R.id.input_name);
        input_email=alertDialog.findViewById(R.id.input_email);
        input_password=alertDialog.findViewById(R.id.input_password);
        progress=alertDialog.findViewById(R.id.progress);
        input_password.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);

        Button grid3=findViewById(R.id.grid3);
        Button grid4=findViewById(R.id.grid4);
        Button grid5=findViewById(R.id.grid5);
        verifcode=findViewById(R.id.verifcode);
        verifcode.setVisibility(View.GONE);

        grid3.setOnClickListener(v -> {
                mp.playMusic(1);
                setDefaults("grid","3",context);
                startMultilayer();
            });

        grid4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.playMusic(1);
                setDefaults("grid","4",context);
                startMultilayer();
            }
        });

        grid5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.playMusic(1);
                setDefaults("grid","5",context);
                startMultilayer();
            }
        });


    }
}
