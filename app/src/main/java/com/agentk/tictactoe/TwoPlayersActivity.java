package com.agentk.tictactoe;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import static com.agentk.tictactoe.LevelActivity.levelActual;
import static com.agentk.tictactoe.PrincipalPage.getDefaults;

public class TwoPlayersActivity extends AppCompatActivity {

    MediaPlayers mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_players);
        ActionBar actionBar=getSupportActionBar();
        actionBar.hide();

        mp=new MediaPlayers(this);

        Button phone=findViewById(R.id.phone);
        //       Button bluetooth=findViewById(R.id.bluetooth);
        Button online=findViewById(R.id.online);

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.playMusic(1);
                Intent secondeActivite = new Intent(getApplicationContext(), GridActivity.class);
                startActivity(secondeActivite);
            }
        });
        /*
        bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.playMusic(1);
                Intent secondeActivite = new Intent(getApplicationContext(), BluetoothActivity.class);
                startActivity(secondeActivite);
            }
        });
        */
        online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.playMusic(1);
                Intent secondeActivite = new Intent(getApplicationContext(), OnlineActivity.class);
                startActivity(secondeActivite);
            }
        });
    }
}
