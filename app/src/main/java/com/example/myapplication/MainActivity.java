package com.example.myapplication;

import static com.example.myapplication.Draw.Down;
import static com.example.myapplication.Draw.score;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

//handtracker import
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.StartMenu.StartMenuActivity;
import com.google.mediapipe.solutioncore.CameraInput;
import com.google.mediapipe.solutioncore.SolutionGlSurfaceView;
import com.google.mediapipe.solutions.hands.Hands;
import com.google.mediapipe.solutions.hands.HandsOptions;
import com.google.mediapipe.solutions.hands.HandsResult;

public class MainActivity extends AppCompatActivity implements GestureActionListener {

    //
    private boolean isGameOverActivityLaunched = false;
    //
    private TextView commandText;
    private ImageView commandImage;
    //
    Button resetButton;
    Handler handler = new Handler();
    public static boolean startFlag = true;
    music ms;
    MediaPlayer bgm;
    MediaPlayer gameover;
    Draw dw;
    TextView scoreLabel;
    TextView highScoreLabel;
    SharedPreferences sp;
    int highScore;
    public static boolean holdButtonclick = false;

    //hand tracker 선언
    private boolean isNextActivityLaunched = false;
    //private ActivityMainBinding binding;
    private Hands hands;
    private CameraInput cameraInput;
    private SolutionGlSurfaceView<HandsResult> glSurfaceView;

    private static final int CAMERA_PERMISSION_CODE = 1001;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_NETWORK_STATE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        commandText = findViewById(R.id.textViewcommand);
        commandImage = findViewById(R.id.commandImage);
        // 테트리스 초기화
        initTetrisComponents();
        // 핸드 트래킹 초기화
        initHandTracking();
    }

    // 테트리스 컴포넌트 초기화
    private void initTetrisComponents() {
        // Tetris 떨어지는 판
        dw = findViewById(R.id.Draw);
        //
        dw.showfield(Draw.Stational);

        ms = new music(getApplicationContext());
        bgm = ms.getMusic(0);
        gameover = ms.getMusic(1);

        if (startFlag) {
            timerset();
            bgm.start();
        }

        blocks.randomNumber();

        resetButton = findViewById(R.id.resetButton);
        setresetButton(resetButton);


        scoreLabel = findViewById(R.id.scoreLabel);
        highScoreLabel = findViewById(R.id.highScoreLabel);
        sp = getSharedPreferences("GAME_DATA", MODE_PRIVATE);
        highScore = sp.getInt("High_Score", 0);
    }

    // 핸드 트래킹 컴포넌트 초기화
    private void initHandTracking() {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            setupStreamingModePipeline();
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    //hand tracker 함수들
    public void onRockGestureDetected() {
        if (!isNextActivityLaunched) {
            isNextActivityLaunched = true;
            goToNextActivity();
        }
    }

    @Override
    public void onRightGesture() {
        performRightAction();
    }

    @Override
    public void onLeftGesture() {
        performLeftAction();
    }

    @Override
    public void onRockGesture() {
        performRotateAction();
    }

    @Override
    public void onScissorGesture() {
        performDownAction();
    }

    private void performRightAction() {
        // Code to perform the right action
        dw.showfield(Draw.Right);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                commandText.setText("   Right");
                commandImage.setImageResource(R.drawable.baseline_keyboard_double_arrow_right_24);
            }
        });
    }

    private void performLeftAction() {
        // Code to perform the left action
        dw.showfield(Draw.Left);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                commandText.setText("   Left");
                commandImage.setImageResource(R.drawable.baseline_keyboard_double_arrow_left_24);
            }
        });
    }

    private void performRotateAction() {
        dw.showfield(Draw.rotate);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                commandText.setText("   Rotate");
                commandImage.setImageResource(R.drawable.baseline_rotate_right_24);
            }
        });
    }

    private void performDownAction() {
        dw.showfield(Down);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                commandText.setText("   Down");
                commandImage.setImageResource(R.drawable.baseline_keyboard_double_arrow_down_24);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        isNextActivityLaunched = false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupStreamingModePipeline();
                glSurfaceView.post(this::startCamera);
                glSurfaceView.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void setupStreamingModePipeline() {
        hands = new Hands(
                this,
                HandsOptions.builder()
                        .setStaticImageMode(false)
                        .setMaxNumHands(1)
                        .setRunOnGpu(true)
                        .build()
        );

        hands.setErrorListener((message, e) -> Log.e("TAG", "MediaPipe Hands error: " + message));

        cameraInput = new CameraInput(this);
        cameraInput.setNewFrameListener(hands::send);

        // glSurfaceView 생성 및 설정
        glSurfaceView = new SolutionGlSurfaceView<>(this, hands.getGlContext(), hands.getGlMajorVersion());
        glSurfaceView.setSolutionResultRenderer(new HandsResultGlRenderer(this));
        glSurfaceView.setRenderInputImage(true);

        hands.setResultListener(result -> {
            glSurfaceView.setRenderData(result);
            glSurfaceView.requestRender();
        });

        // FrameLayout을 찾고 glSurfaceView를 그 안에 추가
        FrameLayout frameLayout = findViewById(R.id.preview_display_layout2);
        frameLayout.addView(glSurfaceView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        glSurfaceView.setVisibility(View.VISIBLE);

        glSurfaceView.post(this::startCamera);
        glSurfaceView.setSolutionResultRenderer(new HandsResultGlRenderer(this));

    }

    private void startCamera() {
        cameraInput.start(
                this,
                hands.getGlContext(),
                CameraInput.CameraFacing.FRONT,
                glSurfaceView.getWidth(),
                glSurfaceView.getHeight()
        );
    }
    private void goToNextActivity() {
        Intent intent = new Intent(this, NextActivity.class);
        startActivity(intent);
    }

    void setresetButton(Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dw.reset();
                try {
                    bgm.prepare();
                } catch (Exception e) {
                }
                bgm.start();
            }
        });
    }

    void timerset() {
        //resetButton = findViewById(R.id.resetButton);
        if (startFlag) {
            final Runnable r = new Runnable() {
                @Override
                public void run() {

                    dw.showfield(Down);
                    handler.postDelayed(this, 1000);

                    TextView scoreText = findViewById(R.id.scoreText);
                    scoreText.setText(String.valueOf(score));
                    TextView gameOverText = findViewById(R.id.gameOverText);

                    if (dw.gameOverFlag) {
                        setresetButton(resetButton);
                        resetButton.setVisibility(View.VISIBLE);
                        gameOverText.setText(R.string.gameOver);
                        resetButton.setText("Reset");
                        bgm.stop();
                        gameover.start();
                        if (score >= highScore) {
                            highScoreLabel.setText("High Score :" + score);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putInt("High_Score", score);
                            editor.apply();
                        } else {
                            highScoreLabel.setText("High Socre :" + highScore);
                            scoreLabel.setText("Score :" + score);
                        }

                        if(!isGameOverActivityLaunched) {
                            isGameOverActivityLaunched = true;
                            Intent intent = new Intent(MainActivity.this, TetrisGameOverActivity.class);
                            intent.putExtra("tossScore", String.valueOf(score));
                            if (score >= highScore) {
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putInt("High_Score", score);
                                editor.apply();
                                intent.putExtra("tossBestScore", String.valueOf(score));
                            } else {
                                intent.putExtra("tossBestScore", String.valueOf(highScore));
                            }
                            dw.reset();
                            startActivity(intent);
                            finish();
                        }
                    }
                }
            };
            handler.post(r);
        }
    }

    // 뒤로가기 누르면 음악이 멈춤!
    @Override
    public void onBackPressed() {
        // Stop the music if it's playing
        if (bgm != null && bgm.isPlaying()) {
            bgm.stop();
            // Optionally, you can also release the MediaPlayer resource
            bgm.release();
            bgm = null;
        }
        // Create an intent to start StartMenuActivity
        Intent intent = new Intent(this, StartMenuActivity.class);
        startActivity(intent);

        // Close the current activity
        finish();
        // Continue with the regular back button behavior
        super.onBackPressed();
    }
}

