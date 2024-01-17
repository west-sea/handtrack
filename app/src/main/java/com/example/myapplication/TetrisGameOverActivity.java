package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class TetrisGameOverActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_tetris_game_over);

        String Score = getIntent().getStringExtra("tossScore");
        String BestScore = getIntent().getStringExtra("tossBestScore");

        Button retry_Btn = findViewById(R.id.tetrisCloseButton);
        TextView tetrisScore = findViewById(R.id.tetrisScore);
        TextView tetirsBestScore = findViewById(R.id.tetrisBestScore);

        // Activity 넘어오면서 정보 받아오고 받아적기 - toss 등등
        tetrisScore.setText(Score);
        tetirsBestScore.setText(BestScore);

        retry_Btn.setOnClickListener(View -> {
            Intent intent = new Intent(TetrisGameOverActivity.this, MainActivity.class);
            // Activity 넘어가면서 Score 초기화 - 전에 finish()해서 안해도 될듯?
            startActivity(intent);
            finish();
        });



    }
}