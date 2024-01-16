package com.example.myapplication.FaceMesh

import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityFaceMeshBinding
import com.google.android.material.snackbar.Snackbar
import com.google.mediapipe.solutioncore.CameraInput
import com.google.mediapipe.solutioncore.SolutionGlSurfaceView
import com.google.mediapipe.solutions.facemesh.FaceMesh
import com.google.mediapipe.solutions.facemesh.FaceMeshOptions
import com.google.mediapipe.solutions.facemesh.FaceMeshResult

class FaceMeshActivity : AppCompatActivity() {

    private val TAG: String = javaClass.name
    private lateinit var binding: ActivityFaceMeshBinding
    private lateinit var faceMesh: FaceMesh
    private lateinit var cameraInput: CameraInput
    private lateinit var glSurfaceView: SolutionGlSurfaceView<FaceMeshResult>
    private lateinit var ballView: CustomBallView

    //타이머 기능 추가
    private var startTime = 0L
    private var timerHandler = Handler(Looper.getMainLooper())
    private var timerRunnable: Runnable = object : Runnable {
        override fun run() {
            val millis = System.currentTimeMillis() - startTime
            val seconds = (millis / 1000).toInt()
            val minutes = seconds / 60
            val displayTime = String.format("%02d:%02d", minutes, seconds % 60)

            // 업데이트된 시간을 화면에 표시
            binding.time.text = displayTime

            timerHandler.postDelayed(this, 500)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFaceMeshBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkCameraPermission()
        Log.i(
            TAG,
            "Android SDK Version -> ${Build.VERSION.SDK_INT} / ${Build.VERSION.CODENAME}"
        )
        setup()

        //retry button 코드
        binding.retryButton.setOnClickListener {
            resetGame()
        }
    }

    private fun setup() {
        ballView = CustomBallView(this)
        binding.frameLayout.addView(ballView)
        ballView.bringToFront()

        faceMesh = FaceMesh(
            this,
            FaceMeshOptions.builder()
                .setStaticImageMode(false)
                .setRefineLandmarks(true)
                .setRunOnGpu(true)
                .build()
        )

        cameraInput = CameraInput(this)
        cameraInput.setNewFrameListener {
            faceMesh.send(it)
        }

        glSurfaceView = SolutionGlSurfaceView(
            this,
            faceMesh.glContext,
            faceMesh.glMajorVersion
        )
        glSurfaceView.setSolutionResultRenderer(FaceMeshResultGlRenderer())
        glSurfaceView.setRenderInputImage(true)

        faceMesh.setResultListener {result ->

            //입 벌림 인식 코드
            result.multiFaceLandmarks()?.forEach { faceLandmarks ->
                // 입 벌림 정도 계산을 위한 랜드마크 인덱스 찾기
                val upperLip = faceLandmarks.landmarkList[13] // 상단 입술 랜드마크
                val lowerLip = faceLandmarks.landmarkList[14] // 하단 입술 랜드마크
                val leftEnd = faceLandmarks.landmarkList[78] // 왼쪽 끝 랜드마크
                val rightEnd = faceLandmarks.landmarkList[308] // 오른쪽 끝 랜드마크

                // New log statements to output the values of these landmarks
//                Log.d(TAG, "Upper Lip Landmark: X=${upperLip.x}, Y=${upperLip.y}, Z=${upperLip.z}")
//                Log.d(TAG, "Lower Lip Landmark: X=${lowerLip.x}, Y=${lowerLip.y}, Z=${lowerLip.z}")
//                Log.d(TAG, "Left End Landmark: X=${leftEnd.x}, Y=${leftEnd.y}, Z=${leftEnd.z}")
//                Log.d(TAG, "Right End Landmark: X=${rightEnd.x}, Y=${rightEnd.y}, Z=${rightEnd.z}")


                //공 먹기 구현
                ballView.updateBallsWithMouthCoordinates(upperLip.y, lowerLip.y, leftEnd.x, rightEnd.x)

//                if (ball.top < upperLip.y && ball.bottom > lowerLip.y && ball.left > leftEnd.x && ball.right < rightEnd.x) {
//                    // 공이 입 안에 있으면 사라지게 처리
//                }


                // 입 벌림 정도 계산
                val mouthOpenness = Math.abs(upperLip.y - lowerLip.y)

                // 입 벌림 정도가 0.05를 넘어가면 "MOUTH OPEN" 텍스트 설정
                if (mouthOpenness > 0.05) {
                    runOnUiThread {
                        binding.openmouth.text = "MOUTH OPEN"
                    }
                } else {
                    runOnUiThread {
                        binding.openmouth.text = "" // 또는 다른 상태의 텍스트를 설정할 수 있습니다.
                    }
                }
                Log.d("score", "Score: "+MyGlobals.getInstance().score)
                //binding.score.text =MyGlobals.getInstance().score?.toString()
                runOnUiThread {
                    binding.score.text = MyGlobals.getInstance().score?.toString() ?: "0"
                }
                if(MyGlobals.getInstance().score == MyGlobals.getInstance().getBallnumber()) {
                    //게임 오버 코드 작성
                    stopTimer() // 타이머 중지
                    val finalTime = binding.time.text.toString() // 최종 시간 획득
                    runOnUiThread {
                        binding.openmouth.text = "GAME OVER\nTime: $finalTime" // 게임 오버 메시지와 함께 시간 표시
                    }
                }

            }

            glSurfaceView.setRenderData(result)
            glSurfaceView.requestRender()
        }
        Log.i(TAG, "Setting up GlSurfaceView... -> Done")

        // The runnable to start camera after the gl surface view is attached.
        // For video input source, videoInput.start() will be called when the video uri is available.
        glSurfaceView.post {
            startCamera()
        }
        // Updates the preview layout.
        binding.frameLayout.removeAllViewsInLayout()
        binding.frameLayout.addView(glSurfaceView)
        glSurfaceView.visibility = View.VISIBLE
        binding.frameLayout.requestLayout()

        //ballView = CustomBallView(this)
        binding.frameLayout.addView(ballView)
//
        ballView.bringToFront()

        val topImageView: ImageView = findViewById(R.id.topImageView)
        topImageView.setImageResource(R.drawable.faceback)
        topImageView.visibility = View.VISIBLE
        topImageView.bringToFront()

        //타이머 시작
        startTimer()
    }

    private fun startCamera() {
        cameraInput.start(
            this,
            faceMesh.glContext,
            CameraInput.CameraFacing.FRONT,
            glSurfaceView.width,
            glSurfaceView.height
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun checkCameraPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) -> {
                val requestPermissionLauncher = registerForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted: Boolean ->
                    if (!isGranted) {
                        Snackbar.make(
                            binding.root,
                            "Grant camera permission in order to use the app",
                            Snackbar.LENGTH_INDEFINITE
                        ).show()
                    }
                }
                requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Restarts the camera and the opengl surface rendering.
        cameraInput = CameraInput(this)
        cameraInput.setNewFrameListener {
            faceMesh.send(it)
        }
        glSurfaceView.post { startCamera() }
    }

    override fun onPause() {
        super.onPause()
        cameraInput.close()
    }
    private fun startTimer() {
        startTime = System.currentTimeMillis()
        timerHandler.postDelayed(timerRunnable, 0)
    }

    private fun stopTimer() {
        timerHandler.removeCallbacks(timerRunnable)
    }

    private fun resetGame() {
        ActivityUtils.restartActivity(this);
    }
}