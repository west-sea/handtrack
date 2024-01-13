package com.example.myapplication.FaceMesh

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityFaceMeshBinding
import com.google.mediapipe.solutioncore.CameraInput
import com.google.mediapipe.solutioncore.SolutionGlSurfaceView
import com.google.mediapipe.solutions.facemesh.FaceMesh
import com.google.mediapipe.solutions.facemesh.FaceMeshOptions
import com.google.mediapipe.solutions.facemesh.FaceMeshResult

class FaceMeshActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFaceMeshBinding
    private lateinit var faceMesh: FaceMesh
    private lateinit var cameraInput: CameraInput
    private lateinit var glSurfaceView: SolutionGlSurfaceView<FaceMeshResult>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFaceMeshBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setup()
    }

    private fun setup(){
        Log.i(TAG, "Starting setup...")
        faceMesh = FaceMesh(
            this,
            FaceMeshOptions.builder()
                .setStaticImageMode(false)
                .setRefineLandmarks(true)
                .setRunOnGpu(true)
                .build()
        )
        Log.i(TAG, "FaceMesh initialized...")
        faceMesh.setErrorListener { message, _ ->
            Log.e(
                TAG,
                "Error MediaPipe Face Mesh -> $message"
            )
        }
        Log.i(TAG, "FaceMesh initialized... -> Done")

        Log.i(TAG, "Camera check  initializing...")
        cameraInput = CameraInput(this)
        cameraInput.setNewFrameListener {
            faceMesh.send(it)
        }
        Log.i(TAG, "Camera check  initializing... -> Done")

        // Initializes a new Gl surface view with a user-defined FaceMeshResultGlRenderer.
        Log.i(TAG, "GlSurfaceView initializing...")
        glSurfaceView = SolutionGlSurfaceView(
            this,
            faceMesh.glContext,
            faceMesh.glMajorVersion
        )
        Log.i(TAG, "GlSurfaceView initializing... -> Done")
        Log.i(TAG, "Setting up GlSurfaceView...")
        glSurfaceView.setSolutionResultRenderer(FaceMeshResultGlRenderer())
        glSurfaceView.setRenderInputImage(true)
        faceMesh.setResultListener {
            glSurfaceView.setRenderData(it)
            glSurfaceView.requestRender()
        }
        Log.i(TAG, "Setting up GlSurfaceView... -> Done")

        // The runnable to start camera after the gl surface view is attached.
        // For video input source, videoInput.start() will be called when the video uri is available.
        glSurfaceView.post {
            startCamera()
        }
        // Updates the preview layout.
        binding.frameLayout.removeAllViewsInLayout();
        binding.frameLayout.addView(glSurfaceView)
        glSurfaceView.visibility = View.VISIBLE
        binding.frameLayout.requestLayout()
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


}