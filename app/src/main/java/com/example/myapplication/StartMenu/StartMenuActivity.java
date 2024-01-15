package com.example.myapplication.StartMenu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.FaceMesh.FaceMeshActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.NextActivity;
import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityStartMenuBinding;


public class StartMenuActivity extends AppCompatActivity {

    private ActivityStartMenuBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityStartMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Button 생성
        Button tutorial_Btn, tetris_Btn, facemesh_Btn;
        tutorial_Btn = findViewById(R.id.Tutorial_btn);
        tetris_Btn = findViewById(R.id.Tetris_btn);
        facemesh_Btn = findViewById(R.id.FaceMesh_btn);

        tutorial_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartMenuActivity.this, NextActivity.class);
                startActivity(intent);
            }
        });

        tetris_Btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartMenuActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        facemesh_Btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartMenuActivity.this, FaceMeshActivity.class);
                startActivity(intent);
            }
        });
    }
}