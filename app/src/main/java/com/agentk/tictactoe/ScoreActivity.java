package com.agentk.tictactoe;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;

import java.util.List;

public class ScoreActivity extends AppCompatActivity {
    GridView grid;
    DatabaseHandler db;
    static List<Score> score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        ActionBar actionBar=getSupportActionBar();
        actionBar.hide();
        db = new DatabaseHandler(this);

        score=db.getAllScores();
        if(score==null) db.addScore(new Score(1,"0","0","0"));
        ScoreGrid adapter = new ScoreGrid(ScoreActivity.this, score);
        grid=(GridView)findViewById(R.id.grid);
        grid.setAdapter(adapter);
    }
}
