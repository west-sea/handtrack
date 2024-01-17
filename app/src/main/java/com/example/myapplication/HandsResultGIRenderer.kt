package com.example.myapplication

import android.opengl.GLES20
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.google.mediapipe.formats.proto.LandmarkProto
import com.google.mediapipe.solutioncore.ResultGlRenderer
import com.google.mediapipe.solutions.hands.Hands
import com.google.mediapipe.solutions.hands.HandsResult
import com.gun0912.tedpermission.provider.TedPermissionProvider.context
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.*

import com.example.myapplication.GestureActionListener


/** A custom implementation of [ResultGlRenderer] to render [HandsResult].  */
//class HandsResultGlRenderer(private val gestureActionListener: GestureActionListener?) : ResultGlRenderer<HandsResult?> {
class HandsResultGlRenderer(private val gestureActionListener: GestureActionListener) : ResultGlRenderer<HandsResult?> {

    // 시간 인식 조절
    private var lastExecutionTime = 0L
    private val throttleInterval = 50L

    // 변수 설정
    private var program = 0
    private var positionHandle = 0
    private var projectionMatrixHandle = 0
    private var colorHandle = 0

    // Loading and compiling OpenGL shader programs
    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        return shader
    }
    //

    //HandLandmarks: 두 점 사이 거리 구하기
    fun distance(p1: LandmarkProto.NormalizedLandmark, p2: LandmarkProto.NormalizedLandmark): Float {
        val deltaX = p2.x - p1.x
        val deltaY = p2.y - p1.y
        return sqrt(deltaX.pow(2) + deltaY.pow(2))
    }

    //HandLandmarks: 두 점 사이 기울기 구하기
    fun slope(p1: LandmarkProto.NormalizedLandmark, p2: LandmarkProto.NormalizedLandmark): Float{
        val deltaX = p2.x - p1.x
        val deltaY = p2.y - p1.y

        return if(abs(deltaX)<0.01){
            10000000000F
        } else {
            abs(deltaY/deltaX)
        }
    }

    // Rendering Setup
    override fun setupRendering() {
        program = GLES20.glCreateProgram()
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER)
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)
        positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        projectionMatrixHandle = GLES20.glGetUniformLocation(program, "uProjectionMatrix")
        colorHandle = GLES20.glGetUniformLocation(program, "uColor")
    }

    // Rendering Result
    override fun renderResult(result: HandsResult?, projectionMatrix: FloatArray) {
        // 랜더링 결과 시간 주기
        val currentTime = System.currentTimeMillis()
        if(currentTime - lastExecutionTime < throttleInterval){
            return
        }
        lastExecutionTime = currentTime

        // 기본값 null 설정
        if (result == null) {
            return
        }

        // 도형 그리기
        GLES20.glUseProgram(program)
        GLES20.glUniformMatrix4fv(projectionMatrixHandle, 1, false, projectionMatrix, 0)
        GLES20.glLineWidth(CONNECTION_THICKNESS)

        val numHands = result.multiHandLandmarks().size

        // 손 인식되면, for문 실행
        for (i in 0 until numHands) {
            // 좌-우 손 인식
            val isLeftHand = result.multiHandedness()[i].label == "Left"
            drawConnections(
                result.multiHandLandmarks()[i].landmarkList,
                if (isLeftHand) LEFT_HAND_CONNECTION_COLOR else RIGHT_HAND_CONNECTION_COLOR
            )


            //주먹 쥐기
            if((distance(result.multiHandLandmarks()[i].landmarkList[4], result.multiHandLandmarks()[i].landmarkList[9]) < distance(result.multiHandLandmarks()[i].landmarkList[3], result.multiHandLandmarks()[i].landmarkList[9])) && (distance(result.multiHandLandmarks()[i].landmarkList[8], result.multiHandLandmarks()[i].landmarkList[0]) < distance(result.multiHandLandmarks()[i].landmarkList[6], result.multiHandLandmarks()[i].landmarkList[0])) && (distance(result.multiHandLandmarks()[i].landmarkList[12], result.multiHandLandmarks()[i].landmarkList[0]) < distance(result.multiHandLandmarks()[i].landmarkList[10], result.multiHandLandmarks()[i].landmarkList[0])) && (distance(result.multiHandLandmarks()[i].landmarkList[16], result.multiHandLandmarks()[i].landmarkList[0]) < distance(result.multiHandLandmarks()[i].landmarkList[14], result.multiHandLandmarks()[i].landmarkList[0])) && (distance(result.multiHandLandmarks()[i].landmarkList[20], result.multiHandLandmarks()[i].landmarkList[0]) < distance(result.multiHandLandmarks()[i].landmarkList[18], result.multiHandLandmarks()[i].landmarkList[0]))) {
                MyGlobals.getInstance().fold = 1;
                Log.v("gesture: ", "rock")

//                Handler(Looper.getMainLooper()).post {
//                    val toast = Toast.makeText(context, "rock", Toast.LENGTH_SHORT)
//                    toast.show()
//                    Handler(Looper.getMainLooper()).postDelayed({ toast.cancel() }, 1000)
//                }
                gestureActionListener.onRockGesture();
            }

            //펴기
//            else if(!(distance(result.multiHandLandmarks()[i].landmarkList[4], result.multiHandLandmarks()[i].landmarkList[9]) < distance(result.multiHandLandmarks()[i].landmarkList[3], result.multiHandLandmarks()[i].landmarkList[9])) && !(distance(result.multiHandLandmarks()[i].landmarkList[8], result.multiHandLandmarks()[i].landmarkList[0]) < distance(result.multiHandLandmarks()[i].landmarkList[6], result.multiHandLandmarks()[i].landmarkList[0])) && !(distance(result.multiHandLandmarks()[i].landmarkList[12], result.multiHandLandmarks()[i].landmarkList[0]) < distance(result.multiHandLandmarks()[i].landmarkList[10], result.multiHandLandmarks()[i].landmarkList[0])) && !(distance(result.multiHandLandmarks()[i].landmarkList[16], result.multiHandLandmarks()[i].landmarkList[0]) < distance(result.multiHandLandmarks()[i].landmarkList[14], result.multiHandLandmarks()[i].landmarkList[0])) && !(distance(result.multiHandLandmarks()[i].landmarkList[20], result.multiHandLandmarks()[i].landmarkList[0]) < distance(result.multiHandLandmarks()[i].landmarkList[18], result.multiHandLandmarks()[i].landmarkList[0]))) {
//                MyGlobals.getInstance().fold = 1;
//                Log.v("gesture: ", "paper")
//                Handler(Looper.getMainLooper()).post {
//                    val toast = Toast.makeText(context, "paper", Toast.LENGTH_SHORT)
//                    toast.show()
//                    Handler(Looper.getMainLooper()).postDelayed({ toast.cancel() }, 1000)
//                }
//            }
            //가위
            else if((distance(result.multiHandLandmarks()[i].landmarkList[4], result.multiHandLandmarks()[i].landmarkList[9]) < distance(result.multiHandLandmarks()[i].landmarkList[3], result.multiHandLandmarks()[i].landmarkList[9])) && !(distance(result.multiHandLandmarks()[i].landmarkList[8], result.multiHandLandmarks()[i].landmarkList[0]) < distance(result.multiHandLandmarks()[i].landmarkList[6], result.multiHandLandmarks()[i].landmarkList[0])) && !(distance(result.multiHandLandmarks()[i].landmarkList[12], result.multiHandLandmarks()[i].landmarkList[0]) < distance(result.multiHandLandmarks()[i].landmarkList[10], result.multiHandLandmarks()[i].landmarkList[0])) && (distance(result.multiHandLandmarks()[i].landmarkList[16], result.multiHandLandmarks()[i].landmarkList[0]) < distance(result.multiHandLandmarks()[i].landmarkList[14], result.multiHandLandmarks()[i].landmarkList[0])) && (distance(result.multiHandLandmarks()[i].landmarkList[20], result.multiHandLandmarks()[i].landmarkList[0]) < distance(result.multiHandLandmarks()[i].landmarkList[18], result.multiHandLandmarks()[i].landmarkList[0]))) {
                MyGlobals.getInstance().fold = 1;
                Log.v("gesture: ", "scissor")
//                Handler(Looper.getMainLooper()).post {
//                    val toast = Toast.makeText(context, "scissor", Toast.LENGTH_SHORT)
//                    toast.show()
//                    Handler(Looper.getMainLooper()).postDelayed({ toast.cancel() }, 1000)
//                }
                gestureActionListener.onScissorGesture();
            }

//            else if(slope(result.multiHandLandmarks()[i].landmarkList[0], result.multiHandLandmarks()[i].landmarkList[9]) > 3){
//                if( result.multiHandLandmarks()[i].landmarkList[9].y > result.multiHandLandmarks()[i].landmarkList[0].y){
//                    Handler(Looper.getMainLooper()).post {
//                        val toast = Toast.makeText(context, "Down", Toast.LENGTH_SHORT)
//                        Log.v("gesture: ", "Down")
//                        toast.show()
//                        Handler(Looper.getMainLooper()).postDelayed({ toast.cancel() }, 500)
//                    }
//                } else{
//                    Handler(Looper.getMainLooper()).post {
//                        val toast = Toast.makeText(context, "Up", Toast.LENGTH_SHORT)
//                        toast.show()
//                        Handler(Looper.getMainLooper()).postDelayed({ toast.cancel() }, 500)
//                    }
//                }
//            }
            else if (
                (slope(result.multiHandLandmarks()[i].landmarkList[0], result.multiHandLandmarks()[i].landmarkList[9]) < 3) && (slope(result.multiHandLandmarks()[i].landmarkList[0], result.multiHandLandmarks()[i].landmarkList[9]) > 0)
            )
            {
                if (result.multiHandLandmarks()[i].landmarkList[9].x > result.multiHandLandmarks()[i].landmarkList[0].x){
//                    Handler(Looper.getMainLooper()).post {
//                        val toast = Toast.makeText(context, "Right", Toast.LENGTH_SHORT)
//                        toast.show()
//                        Handler(Looper.getMainLooper()).postDelayed({ toast.cancel() }, 500)
//                    }
                    gestureActionListener.onRightGesture();
                } else {
//                    Handler(Looper.getMainLooper()).post {
//                        val toast = Toast.makeText(context, "left", Toast.LENGTH_SHORT)
//                        toast.show()
//                        Handler(Looper.getMainLooper()).postDelayed({ toast.cancel() }, 500)
//                    }
                    gestureActionListener.onLeftGesture();
                }
            }

            // 각 HandLandmarks x, y, z 좌표 로그 표시
            for(ind in result.multiHandLandmarks()[i].landmarkList.indices) {
                val lm = result.multiHandLandmarks()[i].landmarkList[ind]
                Log.d(TAG, "LandMark[$ind] | x : ${lm.x}, y : ${lm.y}, z : ${lm.z}")
            }

            // 각 point에 대한 circle 그림 그리기
            for (landmark in result.multiHandLandmarks()[i].landmarkList) {
                // Draws the landmark.
                drawCircle(
                    landmark.x,
                    landmark.y,
                    if (isLeftHand) LEFT_HAND_LANDMARK_COLOR else RIGHT_HAND_LANDMARK_COLOR
                )
                //Log.d(TAG, "LandMark | x : ${landmark.x}, y : ${landmark.y}, z : ${landmark.z}")
                // Draws a hollow circle around the landmark.
                drawHollowCircle(
                    landmark.x,
                    landmark.y,
                    if (isLeftHand) LEFT_HAND_HOLLOW_CIRCLE_COLOR
                    else RIGHT_HAND_HOLLOW_CIRCLE_COLOR
                )
            }
        }
    }

    /**
     * Deletes the shader program.
     *
     *
     * This is only necessary if one wants to release the program
    while keeping the context around.
     */
    fun release() {
        GLES20.glDeleteProgram(program)
    }

    private fun drawConnections(
        handLandmarkList: List<LandmarkProto.NormalizedLandmark>,
        colorArray: FloatArray
    ) {
        GLES20.glUniform4fv(colorHandle, 1, colorArray, 0)
        for (c in Hands.HAND_CONNECTIONS) {
            val start = handLandmarkList[c.start()]
            val end = handLandmarkList[c.end()]
            val vertex = floatArrayOf(start.x, start.y, end.x, end.y)
            val vertexBuffer = ByteBuffer.allocateDirect(vertex.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertex)
            vertexBuffer.position(0)
            GLES20.glEnableVertexAttribArray(positionHandle)
            GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT,
                false, 0, vertexBuffer)
            GLES20.glDrawArrays(GLES20.GL_LINES, 0, 2)
        }
    }

    private fun drawCircle(x: Float, y: Float, colorArray: FloatArray) {
        GLES20.glUniform4fv(colorHandle, 1, colorArray, 0)
        val vertexCount = NUM_SEGMENTS + 2
        val vertices = FloatArray(vertexCount * 3)
        vertices[0] = x
        vertices[1] = y
        vertices[2] = 0f
        for (i in 1 until vertexCount) {
            val angle = 2.0f * i * Math.PI.toFloat() / NUM_SEGMENTS
            val currentIndex = 3 * i
            vertices[currentIndex] = x + (LANDMARK_RADIUS * Math.cos(angle.toDouble())).toFloat()
            vertices[currentIndex + 1] =
                y + (LANDMARK_RADIUS * Math.sin(angle.toDouble())).toFloat()
            vertices[currentIndex + 2] = 0f
        }
        val vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(vertices)
        vertexBuffer.position(0)
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT,
            false, 0, vertexBuffer)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertexCount)
    }

    private fun drawHollowCircle(x: Float, y: Float, colorArray: FloatArray) {
        GLES20.glUniform4fv(colorHandle, 1, colorArray, 0)
        val vertexCount = NUM_SEGMENTS + 1
        val vertices = FloatArray(vertexCount * 3)
        for (i in 0 until vertexCount) {
            val angle = 2.0f * i * Math.PI.toFloat() / NUM_SEGMENTS
            val currentIndex = 3 * i
            vertices[currentIndex] =
                x + (HOLLOW_CIRCLE_RADIUS * Math.cos(angle.toDouble())).toFloat()
            vertices[currentIndex + 1] =
                y + (HOLLOW_CIRCLE_RADIUS * Math.sin(angle.toDouble())).toFloat()
            vertices[currentIndex + 2] = 0f
        }
        val vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(vertices)
        vertexBuffer.position(0)
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT,
            false, 0, vertexBuffer)
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, vertexCount)
    }

    companion object {
        private const val TAG = "HandsResultGlRenderer"
        private val LEFT_HAND_CONNECTION_COLOR = floatArrayOf(0.2f, 1f, 0.2f, 1f)
        private val RIGHT_HAND_CONNECTION_COLOR = floatArrayOf(1f, 0.2f, 0.2f, 1f)
        private const val CONNECTION_THICKNESS = 25.0f
        private val LEFT_HAND_HOLLOW_CIRCLE_COLOR = floatArrayOf(0.2f, 1f, 0.2f, 1f)
        private val RIGHT_HAND_HOLLOW_CIRCLE_COLOR = floatArrayOf(1f, 0.2f, 0.2f, 1f)
        private const val HOLLOW_CIRCLE_RADIUS = 0.01f
        private val LEFT_HAND_LANDMARK_COLOR = floatArrayOf(1f, 0.2f, 0.2f, 1f)
        private val RIGHT_HAND_LANDMARK_COLOR = floatArrayOf(0.2f, 1f, 0.2f, 1f)
        private const val LANDMARK_RADIUS = 0.008f
        private const val NUM_SEGMENTS = 120
        private const val VERTEX_SHADER = ("uniform mat4 uProjectionMatrix;\n"
                + "attribute vec4 vPosition;\n"
                + "void main() {\n"
                + "  gl_Position = uProjectionMatrix * vPosition;\n"
                + "}")
        private const val FRAGMENT_SHADER = ("precision mediump float;\n"
                + "uniform vec4 uColor;\n"
                + "void main() {\n"
                + "  gl_FragColor = uColor;\n"
                + "}")
    }
}