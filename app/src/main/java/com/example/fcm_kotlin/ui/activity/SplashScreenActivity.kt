package com.example.fcm_kotlin.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.fcm_kotlin.databinding.ActivitySplashScreenBinding
import com.example.fcm_kotlin.manager.SharedPreferenceManager

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var sharedPreferenceManager: SharedPreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        bindView()
    }

    private fun bindView() {
        sharedPreferenceManager = SharedPreferenceManager(this)
        Handler().postDelayed({
            if (sharedPreferenceManager.isLoggedIn()) {
                val intent = Intent(
                    this@SplashScreenActivity,
                    MainActivity::class.java
                )
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(
                    this@SplashScreenActivity,
                    LoginActivity::class.java
                )
                startActivity(intent)
                finish()
            }
        }, 2300)
    }
}