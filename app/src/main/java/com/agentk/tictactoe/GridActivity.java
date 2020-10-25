package com.agentk.tictactoe;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import static com.agentk.tictactoe.PrincipalPage.getDefaults;

public class GridActivity extends AppCompatActivity {
    MediaPlayers mp;
int levelActual;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);

        ActionBar actionBar=getSupportActionBar();
        actionBar.hide();

        mp=new MediaPlayers(this);

        Button grid3=findViewById(R.id.grid3);
        Button grid4=findViewById(R.id.grid4);
        Button grid5=findViewById(R.id.grid5);


        grid3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.playMusic(1);
                Intent secondeActivite = new Intent(getApplicationContext(), MainActivity.class);
                secondeActivite.putExtra("level", "20");
                startActivity(secondeActivite);
            }
        });
        grid4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.playMusic(1);
                levelActual = Integer.parseInt(getDefaults("level", getApplicationContext()));
                if(levelActual<5){//5
                    Toast.makeText(getApplicationContext(), R.string.end_lvl4, Toast.LENGTH_SHORT).show();
                }else {
                    Intent secondeActivite = new Intent(getApplicationContext(), Grid4Activity.class);
                    secondeActivite.putExtra("level", "20");
                    startActivity(secondeActivite);
                }
            }
        });
        grid5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.playMusic(1);
                levelActual = Integer.parseInt(getDefaults("level", getApplicationContext()));
                if(levelActual<10){//10
                    Toast.makeText(getApplicationContext(), R.string.end_lvl9, Toast.LENGTH_SHORT).show();
                }else {
                    Intent secondeActivite = new Intent(getApplicationContext(), Grid5Activity.class);
                    secondeActivite.putExtra("level", "20");
                    startActivity(secondeActivite);
                }
            }
        });
    }
}
