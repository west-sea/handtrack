package com.example.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.FaceMesh.FaceMeshActivity
import com.example.myapplication.databinding.ActivityNextBinding
import com.example.myapplication.MainActivity
import com.google.mediapipe.solutioncore.CameraInput
import com.google.mediapipe.solutioncore.SolutionGlSurfaceView
import com.google.mediapipe.solutions.hands.Hands
import com.google.mediapipe.solutions.hands.HandsOptions
import com.google.mediapipe.solutions.hands.HandsResult

class NextActivity : AppCompatActivity(), GestureActionListener {

    // 변수 선언
    private var isNextActivityLaunched = false
    private var isFaceActivityLaunched = false

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
    }

    override fun onRockGesture() {
        if(!isNextActivityLaunched){
            isNextActivityLaunched = true
            goToNextActivity()
        }
    }

    override fun onRightGesture() {
        if(!isFaceActivityLaunched){
            isFaceActivityLaunched = true
            goToFaceActivity()
        }
    }

    override fun onLeftGesture() {
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
        val intent = Intent(this@NextActivity, MainActivity::class.java)
        startActivity(intent)
    }

    private fun goToFaceActivity(){
        val intent = Intent(this@NextActivity, FaceMeshActivity::class.java)
        startActivity(intent)
    }

}