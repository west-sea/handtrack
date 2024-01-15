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
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

//handtracker import
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import com.google.mediapipe.solutioncore.CameraInput;
import com.google.mediapipe.solutioncore.SolutionGlSurfaceView;
import com.google.mediapipe.solutions.hands.Hands;
import com.google.mediapipe.solutions.hands.HandsOptions;
import com.google.mediapipe.solutions.hands.HandsResult;

public class MainActivity extends AppCompatActivity implements GestureActionListener {

    //
    private boolean isGamePaused = false;
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
    private static final String[] REQUIRED_PERMISSIONS = new String[] {
            Manifest.permission.INTERNET,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_NETWORK_STATE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 테트리스 초기화
        initTetrisComponents();
        // 핸드 트래킹 초기화
        initHandTracking();
    }

    // 테트리스 컴포넌트 초기화
    private void initTetrisComponents() {
        dw = findViewById(R.id.Draw);
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
    public void onRockGesture() {performRotateAction();}

    @Override
    public void onScissorGesture() {performDownAction();}

    private void performRightAction() {
        // Code to perform the right action
        dw.showfield(Draw.Right);
    }

    private void performLeftAction() {
        // Code to perform the left action
        dw.showfield(Draw.Left);
    }

    private void performRotateAction(){

        dw.showfield(Draw.rotate);
    }

    private void performDownAction(){
        dw.showfield(Down);
        Log.d("Down", "Down");
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


    //移動機能定義メソッド
    void setButtonFunction(Button button, final int motion) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dw.showfield(motion);
            }
        });
    }

    //ホールドボタンの機能定義メソッド
    void setHoldButtonFunction(Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holdButtonclick = true;
            }
        });
    }

    //高速落下ボタンの機能定義メソッド
    void setDownButtonFunction(Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                while (true) {
                    Draw.offsety++;
                    if (dw.canMove(0, 1, blocks.nowBlock) == false) {
                        dw.blockFixt();
                        dw.checkfield();
                        break;
                    }
                }
            }
        });
    }

    //リセットボタンの機能定義メソッド
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

    //一定間隔の時間で処理を行うメソッド
    void timerset() {
        //resetButton = findViewById(R.id.resetButton);
        if (startFlag) {
            final Runnable r = new Runnable() {
                @Override
                public void run() {

                    //一定周期の落下
                    dw.showfield(Down);
                    handler.postDelayed(this, 1000);

                    //スコアの表示
                    TextView scoreText = findViewById(R.id.scoreText);
                    scoreText.setText(String.valueOf(score));

                    TextView gameOverText = findViewById(R.id.gameOverText);

                    //ゲームオーバーの時の処理
                    if (dw.gameOverFlag) {
                        //リセットボタンの機能定義、ボタンの表示
                        setresetButton(resetButton);
                        resetButton.setVisibility(View.VISIBLE);
                        //テキストセット
                        gameOverText.setText(R.string.gameOver);
                        resetButton.setText("Reset");
                        //音楽停止と再生
                        bgm.stop();
                        gameover.start();
                        //ハイスコア表示、更新処理
                        if (score >= highScore) {
                            highScoreLabel.setText("High Score :" + score);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putInt("High_Score", score);
                            editor.apply();
                        } else {
                            highScoreLabel.setText("High Socre :" + highScore);
                            scoreLabel.setText("Score :" + score);
                        }
                    } else {
                        //ボタン,テキストの非表示
                        gameOverText.setText("");
                        resetButton.setVisibility(View.INVISIBLE);
                        highScoreLabel.setText("");
                        scoreLabel.setText("");
                    }
                }
            };
            handler.post(r);
        } else {
            resetButton.setVisibility(View.INVISIBLE);
        }
    }

}

