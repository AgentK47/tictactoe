package com.agentk.tictactoe;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Created by AgentK on 22/01/2018.
 */

public class MediaPlayers {
    MediaPlayer mp1,mp2,mp3,mp4,mp5;
    Context context;
    public MediaPlayers(Context context) {
        this.context=context;
        mp1 = MediaPlayer.create(context, R.raw.click);
        mp2 = MediaPlayer.create(context, R.raw.digi_plink);
        mp3 = MediaPlayer.create(context, R.raw.deep_kick);
        mp4 = MediaPlayer.create(context, R.raw.casio_pops);
        mp5 = MediaPlayer.create(context, R.raw.applause);
    }
    public void playMusic(int i){
        if(PrincipalPage.musicActive)
       switch (i){
           case 1: mp1.start();     break;
           case 2: mp2.start();     break;
           case 3: mp3.start();     break;
           case 4: mp4.start();     break;
           case 5: mp5.start();     break;
       }
    }
}
