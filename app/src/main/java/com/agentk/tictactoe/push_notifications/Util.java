package com.agentk.tictactoe.push_notifications;

/**
 * Created by AgentK on 13/02/2018.
 */

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class Util {
    public static void savePushToken(String refreshedToken, String userId) {
        FirebaseDatabase.getInstance().getReference().child("users")
                .child(userId)
                .child("pushId")
                .setValue(refreshedToken);
    }

    public static String getCurrentUserId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null || currentUser.isAnonymous()) {
            return "";
        } else {
            return currentUser.getUid();
        }
    }
}