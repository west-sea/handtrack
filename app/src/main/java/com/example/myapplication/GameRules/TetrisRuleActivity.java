package com.example.myapplication.GameRules;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;

public class TetrisRuleActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private View linearLayout;
    private boolean isImage1 = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info); // 'info.xml' 레이아웃을 설정

        // StartMenu에서 받아왔는지 확인
        String StartMenu = getIntent().getStringExtra("tossFromStartMenu");

        //이미지 깜박이기
        linearLayout = findViewById(R.id.info);

        // 일정 시간마다 배경 이미지 변경
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isImage1) {
                    linearLayout.setBackgroundResource(R.drawable.info4);
                } else {
                    linearLayout.setBackgroundResource(R.drawable.info3);
                }
                isImage1 = !isImage1;

                // 반복 호출 (원하는 시간으로 변경 가능)
                handler.postDelayed(this, 100);
            }
        }, 500);


        if(StartMenu != null && StartMenu.equals("FromStartMenu")){

        } else {
            Log.d("from tutorial", "yes");
            // 'countdown.mp3' BGM 재생
            mediaPlayer = MediaPlayer.create(this, R.raw.countdown);
            mediaPlayer.start();

            // 3초 후에 다른 활동으로 전환
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // 여기에 전환할 활동을 지정 (예: MainActivity)
                    Intent intent = new Intent(TetrisRuleActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // 현재 활동 종료
                }
            }, 4500); // 3000ms = 3초
        }
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
