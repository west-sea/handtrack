package com.example.myapplication;

import android.content.Context;
import android.media.MediaPlayer;

import com.example.myapplication.R;

public class music {

    private MediaPlayer[] mediaPlayers;

    public music(Context context){
        mediaPlayers = new MediaPlayer[3];
        mediaPlayers[0] = MediaPlayer.create(context, R.raw.bgm);
        mediaPlayers[1] = MediaPlayer.create(context, R.raw.gameover);
        mediaPlayers[2] = MediaPlayer.create(context, R.raw.ongaku1);
    }

    public MediaPlayer getMusic(int index){
        if (index >= 0 && index < mediaPlayers.length){
            return mediaPlayers[index];
        } else {
            throw new IndexOutOfBoundsException("Invalod MediaPlayer index");
        }
    }
}
