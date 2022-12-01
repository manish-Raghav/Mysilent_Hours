package com.example.silllentkt.activity.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.silllentkt.R

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val handler = Handler(Looper.getMainLooper() )
        handler.postDelayed({
            val  intet = Intent(this ,MainActivity::class.java)
            startActivity(intet)
            finish()
        },3000)
    }
}