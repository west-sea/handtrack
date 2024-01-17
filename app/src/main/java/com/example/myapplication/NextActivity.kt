package com.example.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.GameRules.TetrisRuleActivity
import com.example.myapplication.databinding.ActivityNextBinding
import com.google.mediapipe.solutioncore.CameraInput
import com.google.mediapipe.solutioncore.SolutionGlSurfaceView
import com.google.mediapipe.solutions.hands.Hands
import com.google.mediapipe.solutions.hands.HandsOptions
import com.google.mediapipe.solutions.hands.HandsResult

class NextActivity : AppCompatActivity(), GestureActionListener {

    // Trial 횟수
    private var numOfTrial = 3

    //튜토리얼 상태 변수 추가
    private var isLeftGestureDetected = false
    private var isRightGestureDetected = false
    private var isRockGestureDetected = false
    // 변수 선언
    private var isNextActivityLaunched = false
    private var first = true

    //튜토리얼 상태 횟수 변수
    private var LeftGesture = 0
    private var RightGesture = 0
    private var RockGesture = 0

    private lateinit var binding: ActivityNextBinding
    private lateinit var hands : Hands
    private lateinit var cameraInput: CameraInput
    private lateinit var glSurfaceView: SolutionGlSurfaceView<HandsResult>

    private val CAMERA_PERMISSION_CODE = 1001


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNextBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Check for camera permission
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // Permission already granted
            setupStreamingModePipeline()
            glSurfaceView.post { startCamera() }
            glSurfaceView.visibility = View.VISIBLE

        } else {
            // Request camera permission if not granted
            requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        }
        val topImageView: ImageView = findViewById(R.id.tutorial)
        topImageView.setImageResource(R.drawable.tutorialback)
        topImageView.visibility = View.VISIBLE
        topImageView.bringToFront()

        val textView: TextView = findViewById(R.id.gesture)
        textView.visibility = View.VISIBLE
        textView.bringToFront()

        val textView2: TextView = findViewById(R.id.textview_tutorial)
        textView2.visibility = View.VISIBLE
        textView2.bringToFront()
    }

    override fun onRockGesture() {
        if (isRightGestureDetected) {
            isRightGestureDetected = false
            runOnUiThread {
                if(RightGesture != 3) {
                    binding.gesture.text = "LEFT"
                    binding.textviewTutorial.text = "Trial Left: $numOfTrial"
                }
            }
            RockGesture += 1
            isRockGestureDetected = true
            numOfTrial -= 1
        }
        if (RightGesture == 3) {
            if (!isNextActivityLaunched) {
                isNextActivityLaunched = true
                goToNextActivity()
            }
        }
    }

    override fun onRightGesture() {
        if (isLeftGestureDetected) { // Left 제스처가 먼저 감지되었는지 확인
            isLeftGestureDetected = false
            runOnUiThread {
                binding.gesture.text = "ROCK"

            }
            // Right 제스처가 감지되면, Rock 제스처를 기다리도록 설정
            RightGesture += 1
            isRightGestureDetected = true
        }
    }

    override fun onLeftGesture() {
        if(isRockGestureDetected || first) {
            isRockGestureDetected = false
            first = false
            runOnUiThread {
                binding.gesture.text = "RIGHT"
                binding.textviewTutorial.text = "Trial Left: $numOfTrial"
            }
            LeftGesture += 1
            isLeftGestureDetected = true
        }
    }

    override fun onScissorGesture() {
        TODO("Not yet implemented")
    }

    override fun onResume(){
        super.onResume()
        isNextActivityLaunched = false
    }

    // Handle permission request result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    setupStreamingModePipeline()
                    glSurfaceView.post { startCamera() }
                    glSurfaceView.visibility = View.VISIBLE
                } else {
                    // Permission denied
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
                    finish()  // Close the app or handle it appropriately
                }
            }
        }
    }

    private fun setupStreamingModePipeline() {
        hands = Hands(
            this@NextActivity,
            HandsOptions.builder()
                .setStaticImageMode(false)
                .setMaxNumHands(1)
                .setRunOnGpu(true)
                .build()
        )
        hands.setErrorListener { message, e -> Log.e("TAG", "MediaPipe Hands error: $message") }

        cameraInput = CameraInput(this@NextActivity)
        cameraInput.setNewFrameListener { hands.send(it) }

        glSurfaceView = SolutionGlSurfaceView(this@NextActivity, hands.glContext,
            hands.glMajorVersion)
        glSurfaceView.setSolutionResultRenderer(HandsResultGlRenderer(this))
        glSurfaceView.setRenderInputImage(true)

        hands.setResultListener {
            glSurfaceView.setRenderData(it)
            glSurfaceView.requestRender()
        }

        glSurfaceView.post(this::startCamera)

        glSurfaceView.setSolutionResultRenderer(HandsResultGlRenderer(this))


        // activity_main.xml에 선언한 FrameLayout에 화면을 띄우는 코드
        binding.previewDisplayLayout.apply {
            removeAllViewsInLayout()
            addView(glSurfaceView)
            glSurfaceView.visibility = View.VISIBLE
            requestLayout()
        }
    }

    private fun startCamera() {
        cameraInput.start(
            this@NextActivity,
            hands.glContext,
            CameraInput.CameraFacing.FRONT,
            glSurfaceView.width,
            glSurfaceView.height
        )
    }

    private fun goToNextActivity(){
        val intent = Intent(this@NextActivity, TetrisRuleActivity::class.java)
        startActivity(intent)
        finish();
    }

}