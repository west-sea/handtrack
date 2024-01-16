package com.example.myapplication.StartMenu


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.airbnb.lottie.LottieAnimationView
import com.example.myapplication.R

class SplashActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val animationView = findViewById<LottieAnimationView>(R.id.animationView)
        animationView.setAnimation("splash.json")
        animationView.loop(true)
        animationView.playAnimation()
        Handler().postDelayed({ // Start MainActivity after the splash duration
            //val intent = Intent(this, LoginSdk::class.java)
            val intent = Intent(this, StartMenuActivity::class.java)
            startActivity(intent)
            finish()
        }, SPLASH_DURATION.toLong())
    }

    companion object {
        private const val SPLASH_DURATION = 3500 // 3 seconds
    }
}