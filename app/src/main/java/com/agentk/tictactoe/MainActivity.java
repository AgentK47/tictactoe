package com.agentk.tictactoe;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import static com.agentk.tictactoe.PrincipalPage.getDefaults;
import static com.agentk.tictactoe.PrincipalPage.setDefaults;
/*
Niveau=20 two players
Niveau=22 online players
won() return 1 if players won -1 if computer and 0 if no winner
 */
public class MainActivity extends AppCompatActivity {
    Context context = this;
    private InterstitialAd mInterstitialAd;
    public int player=0;
    int[] gameState={0,0,0,0,0,0,0,0,0};
    int wins[][] = {{0,1,2},{3,4,5},{6,7,8},{0,3,6},{1,4,7},{2,5,8},{0,4,8},{2,4,6}};//les états de solutions possibles
    int tour=0;
    int result=0;
    int a=0,b=0,c=0;
    int winPosition;
    int depth=0;
    int joueur=0;
    String[] pass;
    boolean boardActive=true;
    boolean isMyTurn;
    DatabaseHandler db;
    TextView playerScore;
    TextView drawScore;
    TextView androidScore;
    TextView winnerMessage;
    TextView playerName1;
    TextView playerName2;
    ImageView personnage;
    RelativeLayout playagain;
    MediaPlayers mp;
    LinearLayout play,twoPlayer;
    int levelActual;
    int niveau;
    String gameId;
    private AdView mAdView;
    String[] vignon,john;

    public int minmax(int joueur) //fonction minmax
    {
        int gagnant = won();
        if(gagnant != 0) return gagnant*joueur;
        int move = -1;
        int score = -2;

        for(int i = 0; i < gameState.length; i++) //pour tous les noeuds
        {
            if(gameState[i] == 0) //si vide
            {
                gameState[i] = joueur;//on essaie de positionner
                int thisScore = -minmax(joueur*-1);
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

    public void computerMove()
    {
        int move = -1;
        int score = -2;
        player=1;
        if(depth<LevelActivity.gameLevel[niveau]+joueur){
            Random rand = new Random();
            move = rand.nextInt(9);
            if(gameState[move]!=0){
                while (gameState[move] != 0) {
                    move = rand.nextInt(9);
                }
            }
            depth++;
        }else {
            for (int i = 0; i < gameState.length; i++) //parcourir tous les noeuds
            {
                if (gameState[i] == 0) //si vide
                {
                    gameState[i] = 1;//positionner pour vérifier l'éficacité
                    int tempScore = -minmax(-1); //appelle de minmax
                    gameState[i] = 0;
                    if (tempScore > score) //maximiser
                    {
                        score = tempScore;
                        move = i;
                    }
                }

            }
        }
        //retourne un score basé sur minmax tree .
        if(tour<9){

            final int m=move;
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mp.playMusic(3);
                                        GridLayout gridLayout = findViewById(R.id.gridLayout);
                                        ( gridLayout.getChildAt(m)).setTranslationY(-1000f);
                                        ((ImageView) gridLayout.getChildAt(m)).setImageResource(joueur==0?R.drawable.pion:R.drawable.pion1);
                                        ( gridLayout.getChildAt(m)).animate().translationYBy(1000f).setDuration(300);
                                    }
            },300);
            tour++;
            gameState[move] = 1;
        }
        player = -1;
    }

    public int won(){
        winPosition=0;
        for(int[] game: wins) {
            winPosition++;
            if (gameState[game[0]] != 0 && gameState[game[0]] == gameState[game[1]] && gameState[game[0]] == gameState[game[2]]) {
                return gameState[game[0]];
            }
        }
        return 0;
    }

    public void winnerAnimation(int []winner){
        mp.playMusic(4);
        for(int i=0;i<3;i++) {
            GridLayout gridLayout = findViewById(R.id.gridLayout);
            ScaleAnimation scal = new ScaleAnimation(0, 1f, 0, 1f, Animation.RELATIVE_TO_SELF, (float) 0.5, Animation.RELATIVE_TO_SELF, (float) 0.5);
            scal.setDuration(2000);
            scal.setFillAfter(false);
            ( gridLayout.getChildAt(winner[i])).setAnimation(scal);
        }
    }
    public void chatPersonnage(){
        switch (niveau){
            case 1:
                if(a==0&&b==0&&c==0) winnerMessage.setText(vignon[0]);
                if(b==1) winnerMessage.setText(vignon[1]);
                if(b==3) winnerMessage.setText(vignon[2]);
                if(b==5) winnerMessage.setText(vignon[5]);
                break;
            case 2:
                if(a==0&&b==0&&c==0) winnerMessage.setText(vignon[6]);
                if(c==2) winnerMessage.setText(vignon[7]);
                if(b==3) winnerMessage.setText(vignon[8]);
                if(b==4) winnerMessage.setText(vignon[5]);
                break;
            case 3:
                if(a==0&&b==0&&c==0) winnerMessage.setText(vignon[11]);
                if(c==1) winnerMessage.setText(vignon[12]);
                if(b==3) winnerMessage.setText(vignon[13]);
                if(b==4) winnerMessage.setText(vignon[5]);
                break;
            case 4:
                if(a==0&&b==0&&c==0) winnerMessage.setText(john[0]);
                if(c==4) winnerMessage.setText(john[1]);
                if(c==6) winnerMessage.setText(john[2]);
                if(c==9) winnerMessage.setText(john[5]);
                break;
        }
        play.setVisibility(View.VISIBLE);
    }
    public void lostPersonnage(){
        switch (niveau){
            case 1:showMsg(vignon[3]);break;
            case 2: showMsg(vignon[9]);break;
            case 3: showMsg(vignon[14]);break;
            case 4: showMsg(john[3]);break;
        }
    }
    public void wonPersonnage(){
        switch (niveau){
            case 1: showMsg(vignon[4]);
                break;
            case 2: showMsg(vignon[10]);
                break;
            case 3: showMsg(vignon[15]);
                break;
            case 4: showMsg(john[4]);
                break;
        }
    }
    public void levelSup(ArrayList<Level>levels){
        levelActual= Integer.parseInt(getDefaults("level",getApplicationContext()));
        Level l=levels.get(niveau);
        if(niveau<4) {
            if (b >= l.won && a <= l.lost && c <= l.draw) {
                boardActive=false;
                wonPersonnage();
                wonMsg();
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
            }
            if (a > l.lost || c > l.draw) {
                boardActive=false;
                lostPersonnage();
                lostMsg();
            }
        }else{
            if (b == l.won && a == l.lost && c >= l.draw) {
                boardActive=false;
                wonPersonnage();
                wonMsg();
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
            }
            if (a > l.lost ) {
                boardActive=false;
                lostPersonnage();
                lostMsg();
            }
        }
        if(joueur==1&&boardActive){depth=-1;computerMove();}
    }

    public void twoMsg(int r){

        TextView msg = findViewById(R.id.msg1);

        msg.setText( r==-1? playerName1.getText()+" "+getString(R.string.won):(r==1)?
                playerName2.getText()+" "+getString(R.string.won):getString(R.string.draw_));

        Button playA = findViewById(R.id.button1);

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

    public void wonMsg(){
        niveau++;
        mp.playMusic(5);
        String s = String.valueOf(niveau);
        if (niveau > levelActual) {
            setDefaults("level", s, getApplicationContext());
        }
        TextView msg = findViewById(R.id.msg1);
        ImageView   image= findViewById(R.id.image);
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
                                                 if (niveau < 5) {
                                                     Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                     intent.putExtra("level", String.valueOf(niveau));
                                                     finish();
                                                     startActivity(intent);
                                                 } else {
                                                     Intent intent = new Intent(getApplicationContext(), Grid4Activity.class);
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
        Toast.makeText(this, getResources().getString(R.string.lvl) + s, Toast.LENGTH_SHORT).show();
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
        TextView msg = findViewById(R.id.msg1);
        ImageView   image= findViewById(R.id.image);
        Button playA = findViewById(R.id.button1);
        Button playB = findViewById(R.id.button);
        playA.setText(R.string.restart_);
        playA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.playMusic(1);
                a=b=c=0;
                playerScore.setText(String.valueOf(b));
                androidScore.setText(String.valueOf(a));
                drawScore.setText(String.valueOf(c));

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
            a=b=c=0;
            mp.playMusic(1);
            playerScore.setText(String.valueOf(b));
            androidScore.setText(String.valueOf(a));
            drawScore.setText(String.valueOf(c));
            playAgain(v);
        });
        image.setImageResource(R.drawable.sad);
        msg.setText(R.string.lost);
        playagain.setVisibility(View.VISIBLE);
    }

    public void beginPlay(View view){
        ImageView pion=(ImageView)view;

        joueur=Integer.parseInt(pion.getTag().toString());
        RelativeLayout playerPosition=findViewById(R.id.playerPosition);
        playerPosition.setVisibility(View.INVISIBLE);
        GridLayout gridLayout=findViewById(R.id.gridLayout);
        gridLayout.setVisibility(View.VISIBLE);
        LinearLayout playerInfo=findViewById(R.id.playerInfo);
        playerInfo.setVisibility(View.VISIBLE);
        if(joueur==1){depth=-1;computerMove();}
    }

    public void dropIn(View view){
        ImageView pion=(ImageView)view;

        mp.playMusic(2);
        play.setVisibility(View.INVISIBLE);
        int x=Integer.parseInt(pion.getTag().toString());
        if(gameState[x] == 0 && boardActive) {
            if (won() == 0 && tour < 9 ) {
                if(niveau==20) {
                    if (player == -1 && (tour) % 2 == 0) {
                        gameState[x] = player;
                        pion.setTranslationY(-1000f);
                        pion.setImageResource(R.drawable.pion1);
                        pion.animate().translationYBy(1000f).setDuration(300);
                        tour++;
                        player = 1;
                    }else{
                        gameState[x] = player;
                        pion.setTranslationY(-1000f);
                        pion.setImageResource(R.drawable.pion);
                        pion.animate().translationYBy(1000f).setDuration(300);
                        tour++;
                        player = -1;
                    }
                }else{
                    if(niveau==22) {
                        Log.d("player: ", String.valueOf(player));
                        Log.d("isMyTurn: ", String.valueOf(isMyTurn));
                        GridLayout gridLayout = (GridLayout) findViewById(R.id.gridLayout);
                        ( gridLayout.getChildAt(x)).setTranslationY(-1000f);
                        ((ImageView) gridLayout.getChildAt(x)).setImageResource(0);
                        ( gridLayout.getChildAt(x)).animate().translationYBy(1000f).setDuration(300);
                        if(isMyTurn&&player==-1){
                            gameState[x] = player;
                            pion.setTranslationY(-1000f);
                            pion.setImageResource(R.drawable.pion1);
                            pion.animate().translationYBy(1000f).setDuration(300);

                            FirebaseDatabase.getInstance().getReference().child("games")
                                    .child(gameId)
                                    .child(String.valueOf(x))
                                    .setValue(player);
                            isMyTurn=!isMyTurn;
                        }
                        if(isMyTurn&&player==1){
                            gameState[x] = player;
                            pion.setTranslationY(-1000f);
                            pion.setImageResource(R.drawable.pion);
                            pion.animate().translationYBy(1000f).setDuration(300);

                            FirebaseDatabase.getInstance().getReference().child("games")
                                    .child(gameId)
                                    .child(String.valueOf(x))
                                    .setValue(player);
                            isMyTurn=!isMyTurn;
                        }
                        if(!isMyTurn) {
                            TextView msg = findViewById(R.id.msg);
                            msg.setText(R.string.opponent_turn);
                            play.setVisibility(View.VISIBLE);
                        }
                    }else {
                        if (player == -1) {
                            gameState[x] = player;
                            pion.setTranslationY(-1000f);
                            pion.setImageResource(joueur == 0 ? R.drawable.pion1 : R.drawable.pion);
                            pion.animate().translationYBy(1000f).setDuration(300);
                            tour++;
                            player = 1;
                        }
                        chatPersonnage();
                    }
                }

                final Handler handler = new Handler();
                      if (niveau<20 && won()==0)    computerMove();
                            result = won();

                                    if (result == 1 || result == -1) {
                                        winnerAnimation(wins[winPosition-1]);
                                        boardActive=false;
                                        String winner ;
                                        if (result == 1) {
                                            if(joueur==1) winner = getString(R.string.white);
                                            else winner = getString(R.string.black);
                                            a++;
                                        }else {
                                            if(joueur==0) winner = getString(R.string.white);
                                            else winner = getString(R.string.black);
                                            b++;boardActive=true;
                                        }

                                        showMsg(winner + " " +getString(R.string.won));
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (niveau < 20)
                                                    levelSup(LevelActivity.levelsList);
                                                else twoMsg(result);
                                            }},3000);

                                    }
                                    if (result == 0 && gridFull()) {
                                        boardActive=false;
                                        c++;
                                        showMsg(getString(R.string.draw_));
                                        boardActive=true;
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (niveau < 20)
                                                    levelSup(LevelActivity.levelsList);
                                                else twoMsg(result);
                                            }},3000);
                                    }
                                    playerScore.setText(String.valueOf(b));
                                    androidScore.setText(String.valueOf(a));
                                    drawScore.setText(String.valueOf(c));
            }
        }else {
            winnerMessage.setText(R.string.case_non_vide);
            play.setVisibility(View.VISIBLE);
        }
    }

    boolean gridFull(){
        boolean b=true;
        for(int i=0;i<gameState.length;i++){
            if(gameState[i]==0) b=false;
        }
        return b;
    }

    public void showMsg(String msg){
        play =  findViewById(R.id.showMsg);
        winnerMessage.setText(msg);
        play.setVisibility(View.VISIBLE);
        depth=0;
        if(niveau!=22) player=-1;
        for(int i=0; i<gameState.length;i++) gameState[i]=0;
        final GridLayout gridLayout = findViewById(R.id.gridLayout);
        tour=0;
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
            for (int i = 0; i< gridLayout.getChildCount(); i++){
            ((ImageView) gridLayout.getChildAt(i)).setImageResource(0);
            }
            }
            }, 2000);
    }

    public void playAgain(View v){
        RelativeLayout playagain = findViewById(R.id.rejouer);
        playagain.setVisibility(View.INVISIBLE);
        boardActive=true;
        if(niveau<=20) player=-1;
        else{
            if(niveau==22){
                TextView msg = findViewById(R.id.msg);
                Log.d("playAgain:pl: ", String.valueOf(player));
                Log.d("playAgain:turn: ", String.valueOf(isMyTurn));
                if (player==-1) {
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
        for(int i=0; i<gameState.length;i++) gameState[i]=0;
        GridLayout gridLayout = findViewById(R.id.gridLayout);
        tour=0;
        for (int i = 0; i< gridLayout.getChildCount(); i++) {
            ((ImageView) gridLayout.getChildAt(i)).setImageResource(0);
        }
        if(joueur==1&&boardActive&&niveau<20){depth=-1;computerMove();}
    }

    public void twoPlayers(View view){

        EditText player1=findViewById(R.id.player1);
        EditText player2=findViewById(R.id.player2);
        RelativeLayout playerPosition=findViewById(R.id.playerPosition);
        playerPosition.setVisibility(View.INVISIBLE);

        if(player1.getText().toString().matches("")||player2.getText().toString().matches("")){
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.entrer_players), Toast.LENGTH_SHORT).show();
        }else {
            playerName1.setText(player1.getText());
            playerName2.setText(player2.getText());
            twoPlayer.setVisibility(View.INVISIBLE);
            GridLayout gridLayout=findViewById(R.id.gridLayout);
            gridLayout.setVisibility(View.VISIBLE);
            LinearLayout playerInfo=findViewById(R.id.playerInfo);
            playerInfo.setVisibility(View.VISIBLE);
        }
    }
//Online Firebase

    public void setMe(int  joueur) {
        TextView msg = findViewById(R.id.msg);
        if (joueur==-1) {
            msg.setText(R.string.opponent_turn);
            player=-1;
        } else {
            isMyTurn = true;
            msg.setText(R.string.your_turn);
            player=1;
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

                                int r=won();
                                if(r!=0) {
                                    Handler handler=new Handler();
                                    winnerAnimation(wins[winPosition-1]);
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            twoMsg(r);
                                        }
                                    },3000);
                                    if(r==1) a++;
                                    else b++;
                                }else{
                                    if(gridFull()){ twoMsg(0); c++;}
                                }
                                play.setVisibility(View.INVISIBLE);
                                isMyTurn=!isMyTurn;
                            }
                        } else {
                            if (dataSnapshot.getKey().equals("continue")) {
                                Log.d("onChildAdded:pl: ", String.valueOf(player));
                                Log.d("onChildAdded:turn: ", String.valueOf(isMyTurn));
                                msg.setText(R.string.opponent_continued);
                                RelativeLayout playagain = findViewById(R.id.rejouer);
                                playagain.setVisibility(View.INVISIBLE);
                                play.setVisibility(View.INVISIBLE);
                                boardActive=true;
                                if (player==-1) {
                                    msg.setText(R.string.opponent_turn);
                                    isMyTurn = false;
                                } else {
                                    isMyTurn = true;
                                    msg.setText(R.string.your_turn);
                                }
                                Toast.makeText(context, R.string.game_continued, Toast.LENGTH_SHORT).show();
                                for(int i=0; i<gameState.length;i++) gameState[i]=0;
                                GridLayout gridLayout = findViewById(R.id.gridLayout);
                                tour=0;
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
        setContentView(R.layout.activity_main);

        ActionBar actionBar=getSupportActionBar();
        actionBar.hide();

        MobileAds.initialize(this, "ca-app-pub-4160408430329268~7387692204");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-4160408430329268/6854554099");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mp=new MediaPlayers(this);

        playerName1=findViewById(R.id.playerName1);
        playerName2=findViewById(R.id.playerName2);

        playerScore=findViewById(R.id.playerScore);
        drawScore=findViewById(R.id.drawScore);
        androidScore=findViewById(R.id.androidScore);
        play =  findViewById(R.id.showMsg);
        winnerMessage =  findViewById(R.id.msg);

        db = new DatabaseHandler(this);

        playagain =  findViewById(R.id.rejouer);
        niveau= Integer.parseInt(getIntent().getStringExtra("level"));

        twoPlayer =  findViewById(R.id.twoPlayers);

        if(niveau<20){
            personnage=findViewById(R.id.personnage);
            personnage.setVisibility(View.VISIBLE);
            String match="level"+String.valueOf(niveau);
            int resID = getResources().getIdentifier(match, "drawable", getPackageName());
            personnage.setImageResource(resID);
            vignon = getResources().getStringArray(R.array.vignon);
            john = getResources().getStringArray(R.array.john);
            player=-1;
        }

        if(niveau==20){
            twoPlayer.setVisibility(View.VISIBLE);
            RelativeLayout playerPosition=findViewById(R.id.playerPosition);
            playerPosition.setVisibility(View.INVISIBLE);
            player=-1;
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

    }
}