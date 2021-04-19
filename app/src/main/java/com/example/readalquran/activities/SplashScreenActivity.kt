package com.example.readalquran.activities

import android.content.Intent
import android.view.LayoutInflater
import androidx.constraintlayout.motion.widget.MotionLayout
import com.example.readalquran.custom.BaseActivityBinding
import com.example.readalquran.databinding.ActivitySplashscreenBinding

class SplashScreenActivity : BaseActivityBinding<ActivitySplashscreenBinding>() {
    override val bindingInflater: (LayoutInflater) -> ActivitySplashscreenBinding
        get() = ActivitySplashscreenBinding::inflate

    override fun setupView(binding: ActivitySplashscreenBinding) {
        binding.motionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
            }

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
            }
        })
    }
}