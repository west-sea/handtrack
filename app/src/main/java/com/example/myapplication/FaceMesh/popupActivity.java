package com.example.myapplication.FaceMesh;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.myapplication.R;

public class popupActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set your custom layout for the dialog
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialogue);

        Button closeButton = findViewById(R.id.closeButton);
        TextView faceScoreTextView = findViewById(R.id.faceScore);
        TextView bestScoreTextView = findViewById(R.id.bestScore);

        // Set the text of faceScore TextView with the value from MyGlobals.getInstance().getScore
        faceScoreTextView.setText(String.valueOf(MyGlobals.getInstance().getTime()));

        closeButton.setOnClickListener(view -> {
            Intent intent = new Intent(popupActivity.this, FaceMeshActivity.class);
            startActivity(intent);
            finish();
        }); // Close the dialog when the button is clicked

        MyGlobals.getInstance().setScore(0);
    }
}
