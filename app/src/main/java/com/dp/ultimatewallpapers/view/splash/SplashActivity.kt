package com.dp.ultimatewallpapers.view.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.*
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dp.ultimatewallpapers.R
import com.dp.ultimatewallpapers.view.home.view.HomeScreenActivity

class SplashActivity : AppCompatActivity() {

    lateinit var imgSplashLogo:ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        statusBarColor(R.color.black)
        imgSplashLogo = findViewById(R.id.imgSplashLogo)
        setAnimation(imgSplashLogo)
    }

    private fun setAnimation(progressBar: ImageView) {
        val animZoomOut = AnimationUtils.loadAnimation(
                applicationContext,
                R.anim.zoom_out
        )
        val animZoomIn = AnimationUtils.loadAnimation(
                applicationContext,
                R.anim.zoom_in
        )
        val animSet = AnimationSet(true)
        animSet.interpolator = LinearInterpolator()
        animSet.fillAfter = true
        animSet.isFillEnabled = true
        val animRotate = RotateAnimation(
                0.0f, 360.0f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f
        )
        animRotate.duration = 2500
        animRotate.fillAfter = true
        // animRotate.setRepeatCount(Animation.RELATIVE_TO_SELF);
        // animRotate.setRepeatMode(Animation.RESTART);
        animSet.addAnimation(animRotate)
        animSet.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                imgSplashLogo.startAnimation(animZoomIn)
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        progressBar.startAnimation(animSet)
        animZoomIn.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                imgSplashLogo.startAnimation(animZoomOut)
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        animZoomOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                startActivity(
                        Intent(
                                this@SplashActivity,
                                HomeScreenActivity::class.java
                        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                )
                finish()
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
    }


    @SuppressLint("NewApi")
    fun statusBarColor(id: Int) {
        window.decorView.systemUiVisibility = 0
        window.statusBarColor = ContextCompat.getColor(this, id)
    }

    @SuppressLint("NewApi")
    fun statusBarColor(id: Int, boolean: Boolean) {
        if (boolean) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = ContextCompat.getColor(this, id)
        }
    }

}