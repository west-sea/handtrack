package com.example.myapplication;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class InfoActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info); // 'info.xml' 레이아웃을 설정

        // 'countdown.mp3' BGM 재생
        mediaPlayer = MediaPlayer.create(this, R.raw.countdown);
        mediaPlayer.start();

        // 3초 후에 다른 활동으로 전환
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 여기에 전환할 활동을 지정 (예: MainActivity)
                Intent intent = new Intent(InfoActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // 현재 활동 종료
            }
        }, 4500); // 3000ms = 3초
    }

    @Override
    protected void onDestroy() {
        // 활동이 종료될 때 미디어 플레이어 해제
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }
}
