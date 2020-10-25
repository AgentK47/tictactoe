package com.agentk.tictactoe;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.agentk.tictactoe.users.UserListActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.agentk.tictactoe.PrincipalPage.getDefaults;
import static com.agentk.tictactoe.PrincipalPage.setDefaults;

public class Grid5Activity extends AppCompatActivity {
	Context context = this;
    private InterstitialAd mInterstitialAd;
    TextView winnerMessage;
    MediaPlayers mp;
    LinearLayout play,twoPlayer;
    int a=0,b=0,c=0;
    int activePlayer = -1;
    int depth=0;
    int levelActual;
    int niveau;
    int joueur=0;
    boolean gameIsActive = true;
	boolean isMyTurn;
	String gameId;
    String[] pass;
    DatabaseHandler db;
    ImageView personnage;
    TextView playerScore;
    TextView drawScore;
    TextView androidScore;
    TextView playerName1;
    TextView playerName2;
    RelativeLayout playagain;
    List<Integer> winnerPosition;
    String[] papi,tictactomania;

    int[] gameState = new int[25];

    public void dropIn(View view) {

        ImageView counter = (ImageView) view;
        play.setVisibility(View.INVISIBLE);
        mp.playMusic(2);

        int tappedCounter = Integer.parseInt(counter.getTag().toString());

        if (gameState[tappedCounter] == 0 && gameIsActive) {

         if(niveau!=22)   gameState[tappedCounter] = activePlayer;

            counter.setTranslationY(-1000f);

            if(niveau==20) {
                if (activePlayer == 1) {

                    counter.setImageResource(R.drawable.pion);
                    counter.animate().translationYBy(1000f).rotation(360).setDuration(300);
                    activePlayer = -1;

                }else{
                    counter.setImageResource(R.drawable.pion1);
                    counter.animate().translationYBy(1000f).rotation(360).setDuration(300);
                    activePlayer = 1;
                }
            }else if(niveau==22) {
                        Log.d("player: ", String.valueOf(activePlayer));
                        Log.d("isMyTurn: ", String.valueOf(isMyTurn));
                GridLayout gridLayout = (GridLayout) findViewById(R.id.gridLayout);
                ( gridLayout.getChildAt(tappedCounter)).setTranslationY(-1000f);
                ((ImageView) gridLayout.getChildAt(tappedCounter)).setImageResource(0);
                ( gridLayout.getChildAt(tappedCounter)).animate().translationYBy(1000f).setDuration(300);
                        if(isMyTurn&&activePlayer==-1){
                            gameState[tappedCounter] = activePlayer;
                            counter.setTranslationY(-1000f);
                            counter.setImageResource(R.drawable.pion1);
                            counter.animate().translationYBy(1000f).setDuration(300);

                            FirebaseDatabase.getInstance().getReference().child("games")
                                    .child(gameId)
                                    .child(String.valueOf(tappedCounter))
                                    .setValue(activePlayer);
                            isMyTurn=!isMyTurn;
                        }
                        if(isMyTurn&&activePlayer==1){
                            gameState[tappedCounter] = activePlayer;
                            counter.setTranslationY(-1000f);
                            counter.setImageResource(R.drawable.pion);
                            counter.animate().translationYBy(1000f).setDuration(300);

                            FirebaseDatabase.getInstance().getReference().child("games")
                                    .child(gameId)
                                    .child(String.valueOf(tappedCounter))
                                    .setValue(activePlayer);
                            isMyTurn=!isMyTurn;
                        }
                        if(!isMyTurn) {
                            TextView msg = findViewById(R.id.msg);
                            msg.setText(R.string.opponent_turn);
                            play.setVisibility(View.VISIBLE);
                        }
                    } else{
						if (activePlayer == -1) {
                    counter.setImageResource(joueur==0?R.drawable.pion1:R.drawable.pion);
                    counter.animate().translationYBy(1000f).rotation(360).setDuration(300);
                    chatPersonnage();
                    activePlayer = 1;
						}
            }
            int r=CheckWinner();
            determineWinner(r);

            if (niveau<20&&r==0) {
                new ComputerMoves().execute();
            }


        }else {
            winnerMessage.setText(R.string.case_non_vide);
            play.setVisibility(View.VISIBLE);
        }

    }

    public void beginPlay(View view){
        ImageView pion=(ImageView)view;

        joueur=Integer.parseInt(pion.getTag().toString());
        RelativeLayout playerPosition=(RelativeLayout)findViewById(R.id.playerPosition);
        playerPosition.setVisibility(View.INVISIBLE);
        GridLayout gridLayout=(GridLayout)findViewById(R.id.gridLayout);
        gridLayout.setVisibility(View.VISIBLE);
        LinearLayout playerInfo=(LinearLayout)findViewById(R.id.playerInfo);
        playerInfo.setVisibility(View.VISIBLE);
        if(joueur==1&&niveau<20){depth=-1; new ComputerMoves().execute();}
		
		if(niveau==22){
                TextView msg = findViewById(R.id.msg);
                Log.d("playAgain:pl: ", String.valueOf(activePlayer));
                Log.d("playAgain:turn: ", String.valueOf(isMyTurn));
                if (activePlayer==-1) {
                    isMyTurn = false;
                    msg.setText(R.string.opponent_turn);
                } else {
                    isMyTurn = true;
                    msg.setText(R.string.your_turn);
                }
                FirebaseDatabase.getInstance().getReference().child("games")
                        .child(gameId)
                        .child("continue")
                        .setValue(1);

                FirebaseDatabase.getInstance().getReference().child("games")
                        .child(gameId)
                        .setValue(null);
        }
    }

    public void wonMsg(){
        niveau++;
        mp.playMusic(5);
        String s = String.valueOf(niveau);
        if (niveau > levelActual) {
            setDefaults("level", s, getApplicationContext());
        }
        TextView msg = (TextView) findViewById(R.id.msg1);
        ImageView   image=(ImageView)findViewById(R.id.image);
        String winningMsg=getResources().getString(R.string.congratulation)+" "+s;
        msg.setText(winningMsg);
        image.setImageResource(R.drawable.happy);
        Button playA = findViewById(R.id.button1);
        Button playB = findViewById(R.id.button);
        playA.setText(R.string.next_level);
        playA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.playMusic(1);
                final Dialog alertDialog = new Dialog(context);
                alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                alertDialog.setContentView(R.layout.levelinfo);
                pass = getResources().getStringArray(R.array.wins_array);
                // set the custom dialog components - text, image and button
                TextView text = alertDialog.findViewById(R.id.text);
                text.setText(pass[niveau-1]);
                text.setTextSize(18);

                Button dialogButton = alertDialog.findViewById(R.id.dialogButtonOK);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mp.playMusic(1);
                        if(niveau<10) {
                            Intent intent = new Intent(getApplicationContext(), Grid4Activity.class);
                            intent.putExtra("level", String.valueOf(niveau));
                            finish();
                            startActivity(intent);
                        }else{
                            Intent intent = new Intent(getApplicationContext(), Grid5Activity.class);
                            intent.putExtra("level", String.valueOf(niveau));
                            finish();
                            startActivity(intent);
                        }

                        alertDialog.dismiss();
                    }

                });
                alertDialog.show();
            }
        });

        playB.setText(R.string.continuer);
        playB.setOnClickListener(v->{
            mp.playMusic(1);
            niveau--;
            playAgain(v);
        });
        playagain.setVisibility(View.VISIBLE);
        Toast.makeText(this, getString(R.string.lvl) + s, Toast.LENGTH_SHORT).show();
        Score sc=db.getScore(niveau-1);

        if(sc==null){
            db.addScore(new Score(niveau-1,String.valueOf(b),String.valueOf(c),String.valueOf(a)));
        }else{
            if(Integer.parseInt(sc.getWon())<= b){
                sc.setLost(String.valueOf(a));sc.setDraw(String.valueOf(c));sc.setWon(String.valueOf(b));
                db.updateScore(sc);
            }
        }
    }

    public void lostMsg(){
        TextView msg = (TextView) findViewById(R.id.msg1);
        ImageView   image=(ImageView)findViewById(R.id.image);
        Button playA = (Button) findViewById(R.id.button1);
        Button playB = findViewById(R.id.button);
        playA.setText(R.string.restart_);
        playA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a=b=c=0;depth=0;
                playerScore.setText(String.valueOf(b));
                androidScore.setText(String.valueOf(a));
                drawScore.setText(String.valueOf(c));
                if(joueur==1&&gameIsActive){depth=-1; new ComputerMoves().execute();}

                final Dialog alertDialog = new Dialog(context);
                alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                alertDialog.setContentView(R.layout.levelinfo);
                pass = getResources().getStringArray(R.array.wins_array);
                // set the custom dialog components - text, image and button
                TextView text = alertDialog.findViewById(R.id.text);
                text.setText(pass[niveau-1]);
                text.setTextSize(18);

                Button dialogButton = alertDialog.findViewById(R.id.dialogButtonOK);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mp.playMusic(1);
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
                playAgain(v);
            }
        });
        playB.setText(R.string.restart);
        playB.setOnClickListener(v->{
            a=b=c=0;depth=0;
            playerScore.setText(String.valueOf(b));
            androidScore.setText(String.valueOf(a));
            drawScore.setText(String.valueOf(c));
            if(joueur==1&&gameIsActive){depth=-1; new ComputerMoves().execute();}
            playAgain(v);
        });
        image.setImageResource(R.drawable.sad);
        msg.setText(R.string.lost);

        playagain.setVisibility(View.VISIBLE);
    }

    public void determineWinner(final int result){
        final Handler handler = new Handler();

        if (result != 0) {

            // Someone has won!

            gameIsActive = false;

            String winner;

            if (result == 1) {
                if(joueur==1) winner = getString(R.string.white);
                else winner = getString(R.string.black);
                a++;
            }else {
                if(joueur==0) winner = getString(R.string.white);
                else winner = getString(R.string.black);
                b++;
            }

            winnerAnimation(winnerPosition);
            showMsg(winner + " " +getString(R.string.won));

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (niveau < 20)
                        levelSup(LevelActivity.levelsList);
                    else twoMsg(result);
                }},3000);

        } else {

            boolean gameIsOver = true;

            for (int counterState : gameState) {

                if (counterState == 0) gameIsOver = false;

            }

            if (gameIsOver) {
                gameIsActive = false;
                c++;
                showMsg(getString(R.string.draw_));

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (niveau < 20)
                            levelSup(LevelActivity.levelsList);
                        else twoMsg(result);
                    }},3000);
            }

        }

        playerScore.setText(String.valueOf(b));
        androidScore.setText(String.valueOf(a));
        drawScore.setText(String.valueOf(c));
    }

    public void twoMsg(int r){

        TextView msg = (TextView) findViewById(R.id.msg1);

        msg.setText( r==-1? playerName1.getText()+" "+getString(R.string.won):(r==1)?
                playerName2.getText()+" "+getString(R.string.won):getString(R.string.draw_));

        Button playA = (Button) findViewById(R.id.button1);

        playA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(niveau==22){

                    FirebaseDatabase.getInstance().getReference().child("games")
                            .child(gameId)
                            .child("exit")
                            .setValue(1);

                    FirebaseDatabase.getInstance().getReference().child("games")
                            .child(gameId)
                            .setValue(null);
                }
                finish();
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
            }
        });

        playagain.setVisibility(View.VISIBLE);
    }

    public void playAgain(View view) {

        gameIsActive = true;
        depth=0;
        RelativeLayout layout = (RelativeLayout)findViewById(R.id.rejouer);

        layout.setVisibility(View.INVISIBLE);

        if(niveau<=20) activePlayer = -1;
        else{
            if(niveau==22){
                TextView msg = findViewById(R.id.msg);
                Log.d("playAgain:pl: ", String.valueOf(activePlayer));
                Log.d("playAgain:turn: ", String.valueOf(isMyTurn));
                if (activePlayer==-1) {
                    isMyTurn = false;
                    msg.setText(R.string.opponent_turn);
                } else {
                    isMyTurn = true;
                    msg.setText(R.string.your_turn);
                }
                FirebaseDatabase.getInstance().getReference().child("games")
                        .child(gameId)
                        .child("continue")
                        .setValue(1);

                FirebaseDatabase.getInstance().getReference().child("games")
                        .child(gameId)
                        .setValue(null);
            }
        }

        Arrays.fill(gameState, 0);

        GridLayout gridLayout = (GridLayout)findViewById(R.id.gridLayout);

        for (int i = 0; i< gridLayout.getChildCount(); i++) {

            ((ImageView) gridLayout.getChildAt(i)).setImageResource(0);

        }
        if(joueur==1&&gameIsActive){depth=-1; new ComputerMoves().execute();}
    }

    public int CheckWinner()
    {
        // Check vertical wins
        for (int i=0; i < 5; i++)
        {
            if( gameState[i+10] == gameState[i+5] && gameState[i+10] == gameState[i+15] && gameState[i+10]!= 0)
            {
                winnerPosition.add(i+5);winnerPosition.add(i+10);winnerPosition.add(i+15);
                if( gameState[i] == gameState[i+5] ){ winnerPosition.add(i);return gameState[i+5];}
                if( gameState[i+20] == gameState[i+5] ){ winnerPosition.add(i+20);return gameState[i+5];}
            }
            winnerPosition.clear();
        }
        // Check horizontal wins
        for (int i=0; i<=20; i+=5)
        {
            if( gameState[i+2] == gameState[i+1] && gameState[i+2] == gameState[i+3] && gameState[i+2] != 0)
            {
                winnerPosition.add(i+2);winnerPosition.add(i+1);winnerPosition.add(i+3);
                if( gameState[i] == gameState[i+1] ){ winnerPosition.add(i);return gameState[i+1];}
                if( gameState[i+4] == gameState[i+1] ){ winnerPosition.add(i+4); return gameState[i+1];}
            }
            winnerPosition.clear();
        }

        // Check Top left to Bottom right win
        if (gameState[12] == gameState[6] && gameState[18] == gameState[6] && gameState[6]!= 0)
        {
            winnerPosition.add(6);winnerPosition.add(12);winnerPosition.add(18);
            if( gameState[0] == gameState[6] ){ winnerPosition.add(0);return gameState[6];}
            if( gameState[24] == gameState[6] ){winnerPosition.add(24); return gameState[6];}
            winnerPosition.clear();
        }

        // Check top Right to bottom left win
        if (gameState[12] == gameState[8] && gameState[8] == gameState[16] && gameState[8]!= 0)
        {
            winnerPosition.add(8);winnerPosition.add(12);winnerPosition.add(16);
            if( gameState[4] == gameState[8] ){winnerPosition.add(4); return gameState[8];}
            if( gameState[20] == gameState[8] ){winnerPosition.add(20);return gameState[8];}
            winnerPosition.clear();
        }

        int sameType = 1;
        // Check top Right to bottom left win (first 4 rows)
        for (int i=7; i<=15; i+=4)
        {
            if (gameState[i]== gameState[3] && gameState[i] != 0)
            {
                sameType++;
            }
        }
        if (sameType == 4)
        {
            winnerPosition.add(7);winnerPosition.add(11);winnerPosition.add(15);winnerPosition.add(3);
            return gameState[3];
        }
        winnerPosition.clear();

        sameType = 1;
        // Check top Right to bottom left win (second 4 rows)
        for (int i=13; i<=21; i+=4)
        {
            if (gameState[i]== gameState[9] && gameState[i] != 0)
            {
                sameType++;
            }
        }
        if (sameType == 4)
        {
            winnerPosition.add(9);winnerPosition.add(13);winnerPosition.add(17);winnerPosition.add(21);
            return gameState[9];
        }
        winnerPosition.clear();

        sameType = 1;
        // Check Top left to Bottom right win(first 4 rows)
        for (int i=11; i<=23; i+=6)
        {
            if (gameState[i] == gameState[5] && gameState[i]!= 0)
            {
                sameType++;
            }
        }
        if (sameType == 4)
        {
            winnerPosition.add(5);winnerPosition.add(17);winnerPosition.add(11);winnerPosition.add(23);
            return gameState[5];
        }
        winnerPosition.clear();

        sameType = 1;
        // Check Top left to Bottom right win(second 4 rows)
        for (int i=7; i<=19; i+=6)
        {
            if (gameState[i] == gameState[1] && gameState[i]!= 0)
            {
                sameType++;
            }
        }
        if (sameType == 4)
        {
            winnerPosition.add(7);winnerPosition.add(1);winnerPosition.add(13);winnerPosition.add(19);
            return gameState[1];
        }
        winnerPosition.clear();
        return 0;
    }

    public void winnerAnimation(List<Integer> winner){
        mp.playMusic(4);gameIsActive = false;
        for(int i=0;i<winner.size();i++) {
            GridLayout gridLayout = (GridLayout)findViewById(R.id.gridLayout);
            ScaleAnimation scal = new ScaleAnimation(0, 1f, 0, 1f, Animation.RELATIVE_TO_SELF, (float) 0.5, Animation.RELATIVE_TO_SELF, (float) 0.5);
            scal.setDuration(2000);
            scal.setFillAfter(false);
            ((ImageView) gridLayout.getChildAt(winner.get(i))).setAnimation(scal);
        }
    }

    public int minmax(int profondeur, int joueur) //fonction minmax
    {
        int gagnant = CheckWinner();
        if(gagnant != 0 || profondeur==0) return gagnant*joueur;
        int move = -1;
        int score = -2;

        for(int i = 0; i < gameState.length; i++) //pour tous les noeuds
        {
            if(gameState[i] == 0) //si vide
            {
                gameState[i] = joueur;//on essaie de positionner
                int thisScore = -minmax(profondeur-1,joueur*-1);
                if(thisScore > score) //minimiser
                {
                    score = thisScore;
                    move = i;
                }//Prendre celui qui n'avantage pas l'adversaire
                gameState[i] = 0;//Remettre la case vide
            }
        }
        if(move == -1) return 0;
        return score;
    }

    private class ComputerMoves extends AsyncTask<Void,Void,Integer>{
        @Override
        protected void onPreExecute() {
            winnerMessage.setText(R.string.thinking);
            play.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            int move = -1;
            int score = -2;
            activePlayer=1;
            if(depth<LevelActivity.gameLevel[niveau]){
                Random rand = new Random();
                move = rand.nextInt(25);
                if(gameState[move]!=0){
                    while (gameState[move] != 0) {
                        move = rand.nextInt(25);
                    }
                }
                depth++;
            }else {
                for (int i = 0; i < gameState.length; i++) //parcourir tous les noeuds
                {
                    if (gameState[i] == 0) //si vide
                    {
                        gameState[i] = 1;//positionner pour vérifier l'éficacité
                        int tempScore = -minmax((niveau==15?4:2),-1); //appelle de minmax
                        gameState[i] = 0;
                        if (tempScore > score) //maximiser
                        {
                            score = tempScore;
                            move = i;
                        }
                    }

                }
            }
            return move;
        }
        @Override
        protected void onPostExecute(Integer move){
            gameState[move] = 1;
            play.setVisibility(View.INVISIBLE);
            chatPersonnage();
            final int mv=move;
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {  mp.playMusic(3);
                    GridLayout gridLayout = (GridLayout) findViewById(R.id.gridLayout);
                    ((ImageView) gridLayout.getChildAt(mv)).setTranslationY(-1000f);
                    ((ImageView) gridLayout.getChildAt(mv)).setImageResource(joueur==0?R.drawable.pion:R.drawable.pion1);
                    ((ImageView) gridLayout.getChildAt(mv)).animate().translationYBy(1000f).setDuration(300);
                }
            }, 500);
            activePlayer = -1;
            determineWinner(CheckWinner());
        }
    }

    public void chatPersonnage(){
        switch (niveau){
            case 12:
                if(a==0&&b==0&&c==0) winnerMessage.setText(papi[9]);
                if(b==1) winnerMessage.setText(papi[10]);
                if(b==3) winnerMessage.setText(papi[11]);
                if(b==5) winnerMessage.setText(papi[8]);
                break;
            case 13:
                if(a==0&&b==0&&c==0) winnerMessage.setText(tictactomania[0]);
                if(c<=5) winnerMessage.setText(tictactomania[1]);
                if(b>=4) winnerMessage.setText(tictactomania[2]);
                break;
            case 14:
                if(a==0&&b==0&&c==0) winnerMessage.setText(tictactomania[5]);
                if(c<=5) winnerMessage.setText(tictactomania[6]);
                if(b>=6) winnerMessage.setText(tictactomania[7]);
                                break;
            case 15:
                if(a==0&&b==0&&c==0) winnerMessage.setText(tictactomania[10]);
                if(c>0) winnerMessage.setText(tictactomania[11]);
                if(b<=5) winnerMessage.setText(tictactomania[12]);

                break;
        }
        play.setVisibility(View.VISIBLE);
    }
    public void lostPersonnage(){
        switch (niveau){
            case 12:showMsg(papi[12]);break;
            case 13: showMsg(tictactomania[3]);break;
            case 14: showMsg(tictactomania[8]);break;
            case 15: showMsg(tictactomania[13]);break;
        }
    }
    public void wonPersonnage(){
        switch (niveau){
            case 12: showMsg(papi[13]);
                break;
            case 13: showMsg(tictactomania[4]);
                break;
            case 14: showMsg(tictactomania[9]);
                break;
            case 15: showMsg(tictactomania[14]);
                break;
        }
    }

    public void levelSup(ArrayList<Level>levels){
        levelActual= Integer.parseInt(getDefaults("level",getApplicationContext()));
        Level l=levels.get(niveau);
        if(niveau<=14) {
            if (b >= l.won && a <= l.lost && c <= l.draw) {
                wonPersonnage();wonMsg();
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
                gameIsActive = false;
            }
            if (a > l.lost || c > l.draw) {
                lostPersonnage();lostMsg();
//                if (mInterstitialAd.isLoaded()) { mInterstitialAd.show();  }
                gameIsActive = false;
            }
        }else{
            if (b >= l.won && a == l.lost && c <= l.draw) {
                wonPersonnage();
                final Dialog alertDialog = new Dialog(context);
                alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                // alertDialog.setTitle("Alert");
                alertDialog.setContentView(R.layout.winnerinfo);

                // set the custom dialog components - text, image and button
                TextView text = (TextView) alertDialog.findViewById(R.id.text);
                text.setText(R.string.congratulation_msg);
                text.setTextSize(18);
                Button dialogButton = alertDialog.findViewById(R.id.dialogButtonOK);

                dialogButton.setOnClickListener(v->{
                        alertDialog.dismiss();
                });

                alertDialog.show();

                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
                gameIsActive = false;
                Score sc=db.getScore(niveau);

                if(sc==null){
                    db.addScore(new Score(niveau,String.valueOf(b),String.valueOf(c),String.valueOf(a)));
                }else{
                    if(Integer.parseInt(sc.getWon())<= b){
                        sc.setLost(String.valueOf(a));sc.setDraw(String.valueOf(c));sc.setWon(String.valueOf(b));
                        db.updateScore(sc);
                    }
                }
            }
            if (a > l.lost || c > l.draw) {
                lostPersonnage(); lostMsg();
//                if (mInterstitialAd.isLoaded()) { mInterstitialAd.show();  }
                gameIsActive = false;
            }
        }
        if(joueur==1&&gameIsActive){depth=-1; new ComputerMoves().execute();}
    }

    public void showMsg(String msg){

        winnerMessage.setText(msg);

        play.setVisibility(View.VISIBLE);
        depth=0;
        if(niveau!=22) activePlayer=-1;
        Arrays.fill(gameState, 0);
        final GridLayout gridLayout = (GridLayout)findViewById(R.id.gridLayout);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < gridLayout.getChildCount(); i++) {
                    ((ImageView) gridLayout.getChildAt(i)).setImageResource(0);
                }
                gameIsActive = true;
            }
        }, 2000);
    }

    public void twoPlayers(View view){
        EditText player1=(EditText)findViewById(R.id.player1);
        EditText player2=(EditText)findViewById(R.id.player2);
        RelativeLayout playerPosition=(RelativeLayout)findViewById(R.id.playerPosition);
        playerPosition.setVisibility(View.INVISIBLE);

        if(player1.getText().toString().matches("")||player2.getText().toString().matches("")){
            Toast.makeText(getApplicationContext(),R.string.entrer_players, Toast.LENGTH_SHORT).show();
        }else {
            playerName1.setText(player1.getText());
            playerName2.setText(player2.getText());
            twoPlayer.setVisibility(View.INVISIBLE);
            GridLayout gridLayout=(GridLayout)findViewById(R.id.gridLayout);
            gridLayout.setVisibility(View.VISIBLE);
            LinearLayout playerInfo=(LinearLayout)findViewById(R.id.playerInfo);
            playerInfo.setVisibility(View.VISIBLE);
        }
    }
//Online Firebase

    public void setMe(int  joueur) {
        TextView msg = findViewById(R.id.msg);
        if (joueur==-1) {
            msg.setText(R.string.opponent_turn);
            activePlayer=-1;
        } else {
            isMyTurn = true;
            msg.setText(R.string.your_turn);
            activePlayer=1;
        }
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
        Log.d("setGameId: ",  gameId);
        FirebaseDatabase.getInstance().getReference().child("games")
                .child(gameId)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot.getValue() == null) {
                            return;
                        }
                        TextView msg = findViewById(R.id.msg);
                        String key = dataSnapshot.getKey();
                        int onlinePlayer= Integer.valueOf(String.valueOf(dataSnapshot.getValue()));
                        if (!key.equals("continue")&&!key.equals("exit")) {
                            int x = Integer.parseInt(key);
                            Integer shape = dataSnapshot.getValue(Integer.class);
                            Log.d("pionTag ", String.valueOf(x));
                            Log.d("OnlinePlayer ", String.valueOf(onlinePlayer));
                            if (gameState[x] == 0) {
                                gameState[x] = onlinePlayer;
                                GridLayout gridLayout = findViewById(R.id.gridLayout);
                                ( gridLayout.getChildAt(x)).setTranslationY(-1000f);
                                ((ImageView) gridLayout.getChildAt(x)).setImageResource(onlinePlayer==1?R.drawable.pion:R.drawable.pion1);
                                ( gridLayout.getChildAt(x)).animate().translationYBy(1000f).setDuration(300);
                                final Handler handler = new Handler();
                                int r=CheckWinner();
                                if(r!=0) {
                                    winnerAnimation(winnerPosition);
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            twoMsg(r);
                                        }},3000);
                                    if(r==1) a++;
                                    else b++;
                                }else{
                                    boolean gameIsOver = true;

                                    for (int counterState : gameState) {

                                        if (counterState == 0) gameIsOver = false;

                                    }
                                    if (gameIsOver) {
                                        gameIsActive = false;
                                        c++;
                                        showMsg(getString(R.string.draw_));

                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                               twoMsg(r);
                                            }},3000);
                                    }
                                }
                                play.setVisibility(View.INVISIBLE);
                                isMyTurn=!isMyTurn;
                            }
                        } else {
                            if (dataSnapshot.getKey().equals("continue")) {
                                Log.d("onChildAdded:pl: ", String.valueOf(activePlayer));
                                Log.d("onChildAdded:turn: ", String.valueOf(isMyTurn));
                                msg.setText(R.string.opponent_continued);
                                RelativeLayout playagain = findViewById(R.id.rejouer);
                                playagain.setVisibility(View.INVISIBLE);
                                play.setVisibility(View.INVISIBLE);
                                gameIsActive = true;
                                if (activePlayer==-1) {
                                    msg.setText(R.string.opponent_turn);
                                    isMyTurn = false;
                                } else {
                                    isMyTurn = true;
                                    msg.setText(R.string.your_turn);
                                }
                                Toast.makeText(context, R.string.game_continued, Toast.LENGTH_SHORT).show();
                                for(int i=0; i<gameState.length;i++) gameState[i]=0;
                                GridLayout gridLayout = findViewById(R.id.gridLayout);
                                for (int i = 0; i< gridLayout.getChildCount(); i++) {
                                    ((ImageView) gridLayout.getChildAt(i)).setImageResource(0);
                                }
                            }
                            if (dataSnapshot.getKey().equals("exit")) {
                                msg.setText(R.string.opponent_exit);
                                play.setVisibility(View.VISIBLE);
                                Toast.makeText(context, R.string.game_exit, Toast.LENGTH_SHORT).show();
                                finish();
                                if (mInterstitialAd.isLoaded()) {
                                    mInterstitialAd.show();
                                }
                                startActivity(new Intent(context, OnlineActivity.class));
                            }
                        }
                        playerScore.setText(String.valueOf(b));
                        androidScore.setText(String.valueOf(a));
                        drawScore.setText(String.valueOf(c));
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {


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
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid5);

        ActionBar actionBar=getSupportActionBar();
        actionBar.hide();

        Arrays.fill(gameState, 0);
        mp=new MediaPlayers(this);

        playerName1=findViewById(R.id.playerName1);
        playerName2=findViewById(R.id.playerName2);

        playerScore=findViewById(R.id.playerScore);
        drawScore=findViewById(R.id.drawScore);
        androidScore=findViewById(R.id.androidScore);
        winnerMessage = findViewById(R.id.msg);
        play = findViewById(R.id.showMsg);
        playagain = findViewById(R.id.rejouer);
        winnerPosition = new ArrayList<Integer>();

        niveau= Integer.parseInt(getIntent().getStringExtra("level"));

        twoPlayer = findViewById(R.id.twoPlayers);

        if(niveau<20){
            personnage=findViewById(R.id.personnage);
            personnage.setVisibility(View.VISIBLE);
            String match="level"+String.valueOf(niveau);
            int resID = getResources().getIdentifier(match, "drawable", getPackageName());
            personnage.setImageResource(resID);
            papi = getResources().getStringArray(R.array.papi);
            tictactomania = getResources().getStringArray(R.array.tictactomania);
        }

        if(niveau==20){
            twoPlayer.setVisibility(View.VISIBLE);
            RelativeLayout playerPosition=findViewById(R.id.playerPosition);
            playerPosition.setVisibility(View.INVISIBLE);
        }

		if(niveau==22){
            String gameId =getIntent().getStringExtra("gameId");
            String userName =getIntent().getStringExtra("userName");
            String opponent =getIntent().getStringExtra("fromName");

            setGameId(gameId);

            Log.i("onCreate: ",gameId );
            setMe(Integer.parseInt(getIntent().getStringExtra("me")));

            playerName1.setText(userName);
            playerName2.setText(opponent);
            RelativeLayout playerPosition=findViewById(R.id.playerPosition);
            playerPosition.setVisibility(View.INVISIBLE);
            GridLayout gridLayout=findViewById(R.id.gridLayout);
            gridLayout.setVisibility(View.VISIBLE);
            LinearLayout playerInfo=findViewById(R.id.playerInfo);
            playerInfo.setVisibility(View.VISIBLE);
            play.setVisibility(View.VISIBLE);
        }
		
        db = new DatabaseHandler(this);

        MobileAds.initialize(this, "ca-app-pub-4160408430329268~7387692204");

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-4160408430329268/6854554099");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }
}