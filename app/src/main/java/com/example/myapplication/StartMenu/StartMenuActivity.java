package com.example.myapplication.StartMenu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.FaceMesh.FaceMeshActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.NextActivity;
import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityStartMenuBinding;


public class StartMenuActivity extends AppCompatActivity {

    private ActivityStartMenuBinding binding;
    private Button tutorial_Btn;
    private Button tetris_Btn;
    private Button facemesh_Btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityStartMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Button 생성
        //Button tutorial_Btn, tetris_Btn, facemesh_Btn;
        tutorial_Btn = findViewById(R.id.Tutorial_btn);
        tetris_Btn = findViewById(R.id.Tetris_btn);
        facemesh_Btn = findViewById(R.id.FaceMesh_btn);

        tutorial_Btn.setTextColor(getResources().getColor(R.color.white));
        tetris_Btn.setTextColor(getResources().getColor(R.color.white));
        facemesh_Btn.setTextColor(getResources().getColor(R.color.white));

        tutorial_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeButtonTextColor(tutorial_Btn);
                Intent intent = new Intent(StartMenuActivity.this, NextActivity.class);
                startActivity(intent);
            }
        });

        tetris_Btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                changeButtonTextColor(tetris_Btn);
                Intent intent = new Intent(StartMenuActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        facemesh_Btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                changeButtonTextColor(facemesh_Btn);
                Intent intent = new Intent(StartMenuActivity.this, FaceMeshActivity.class);
                startActivity(intent);
            }
        });
    }

    private void changeButtonTextColor(Button clickedButton) {
        // Reset text color for all buttons
        tutorial_Btn.setTextColor(getResources().getColor(R.color.white));
        tetris_Btn.setTextColor(getResources().getColor(R.color.white));
        facemesh_Btn.setTextColor(getResources().getColor(R.color.white));

        // Change text color for the clicked button
        clickedButton.setTextColor(getResources().getColor(R.color.gray));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                clickedButton.setTextColor(getResources().getColor(R.color.white));
            }
        }, 1000);
    }

    @Override
    public void onBackPressed() {
        tutorial_Btn.setTextColor(getResources().getColor(R.color.white));
        tetris_Btn.setTextColor(getResources().getColor(R.color.white));
        facemesh_Btn.setTextColor(getResources().getColor(R.color.white));
        super.onBackPressed();
    }

}