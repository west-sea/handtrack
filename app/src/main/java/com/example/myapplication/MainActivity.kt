package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.mediapipe.solutioncore.CameraInput
import com.google.mediapipe.solutioncore.SolutionGlSurfaceView
import com.google.mediapipe.solutions.hands.Hands
import com.gun0912.tedpermission.PermissionListener
import com.google.mediapipe.solutions.hands.HandsOptions
import com.google.mediapipe.solutions.hands.HandsResult
import com.example.myapplication.HandsResultGlRenderer
import com.google.mediapipe.formats.proto.LandmarkProto
import com.gun0912.tedpermission.normal.TedPermission



class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var hands : Hands
    private lateinit var cameraInput: CameraInput
    private lateinit var glSurfaceView: SolutionGlSurfaceView<HandsResult>

    private val CAMERA_PERMISSION_CODE = 1001

    private val REQUIRED_PERMISSIONS = mutableListOf(Manifest.permission.INTERNET,
        Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_NETWORK_STATE).toTypedArray()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
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
            this@MainActivity,
            HandsOptions.builder()
                .setStaticImageMode(false)
                .setMaxNumHands(1)
                .setRunOnGpu(true)
                .build()
        )
        hands.setErrorListener { message, e -> Log.e("TAG", "MediaPipe Hands error: $message") }

        cameraInput = CameraInput(this@MainActivity)
        cameraInput.setNewFrameListener { hands.send(it) }

        glSurfaceView = SolutionGlSurfaceView(this@MainActivity, hands.glContext,
            hands.glMajorVersion)
        glSurfaceView.setSolutionResultRenderer(HandsResultGlRenderer())
        glSurfaceView.setRenderInputImage(true)

        hands.setResultListener {
            glSurfaceView.setRenderData(it)
            glSurfaceView.requestRender()
        }

        glSurfaceView.post(this::startCamera)


        //binding 문제 때문에 추가
        //var binding = ActivityMainBinding.inflate(layoutInflater)
        //setContentView(binding.root)

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
            this@MainActivity,
            hands.glContext,
            CameraInput.CameraFacing.FRONT,
            glSurfaceView.width,
            glSurfaceView.height
        )
    }
}