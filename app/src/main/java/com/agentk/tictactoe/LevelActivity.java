package com.agentk.tictactoe;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import static com.agentk.tictactoe.PrincipalPage.getDefaults;

public class LevelActivity extends AppCompatActivity {
    Context context = this;
    MediaPlayers mp;
    static ArrayList<Level> levelsList = new ArrayList<Level>();
    static int[] gameLevel={0,2,1,1,0,4,3,3,2,1,0,2,2,2,1,0};
    static int levelActual;
    GridView grid;
    String[] web ;
    String[] pass;


    public void fillGrid(){
        levelActual= Integer.parseInt(getDefaults("level",getApplicationContext()));
        LevelGrid adapter = new LevelGrid(LevelActivity.this, web);
        grid=(GridView)findViewById(R.id.grid);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
                mp.playMusic(1);
                final Dialog alertDialog = new Dialog(context);
               // alertDialog.setTitle("Alert");
                alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                alertDialog.setContentView(R.layout.levelinfo);

                // set the custom dialog components - text, image and button
                TextView text = (TextView) alertDialog.findViewById(R.id.text);
                text.setText(pass[position]);
                text.setTextSize(18);

                Button dialogButton = (Button) alertDialog.findViewById(R.id.dialogButtonOK);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mp.playMusic(1);
                        if(position+1>levelActual){
                            Toast.makeText(getApplicationContext(), R.string.levelless, Toast.LENGTH_SHORT).show();
                        }else {
                            if(position+1<5) {
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.putExtra("level", String.valueOf(position+1));
                                startActivity(intent);
                            }
                            if(position+1>=5&&position+1<11) {
                                Intent intent = new Intent(getApplicationContext(), Grid4Activity.class);
                                intent.putExtra("level", String.valueOf(position+1));
                                startActivity(intent);
                            }
                            if(position+1>=11) {
                                Intent intent = new Intent(getApplicationContext(), Grid5Activity.class);
                                intent.putExtra("level", String.valueOf(position+1));
                                startActivity(intent);
                            }
                        }
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();


            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);

        ActionBar actionBar=getSupportActionBar();
        actionBar.hide();

        mp=new MediaPlayers(this);

        Resources res = getResources();
        web = res.getStringArray(R.array.levels_array);
        pass = res.getStringArray(R.array.wins_array);

        levelsList.add(new Level(0,0,0));
        levelsList.add(new Level(6,0,0));  //l1
        levelsList.add(new Level(5,2,0));  //l2
        levelsList.add(new Level(5,1,0));  //l3
        levelsList.add(new Level(0,10,0));  //l4
        //grid 4x4
        levelsList.add(new Level(5,2,0));  //l5
        levelsList.add(new Level(5,0,0));  //l6
        levelsList.add(new Level(7,5,0));  //l7
        levelsList.add(new Level(8,5,0));  //l8
        levelsList.add(new Level(10,5,0));  //l9
        levelsList.add(new Level(0,10,0));  //l10
        //grid 5x5
        levelsList.add(new Level(5,1,0));  //l11
        levelsList.add(new Level(5,0,0));  //l12
        levelsList.add(new Level(7,5,0));  //l13
        levelsList.add(new Level(8,5,0));  //l14
        levelsList.add(new Level(5,2,0));  //l15

        fillGrid();
    }
    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        fillGrid();
    }
}
