package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        // Задержка 2.5 секунды (2500 миллисекунд)
        Handler(Looper.getMainLooper()).postDelayed({
            // Переход на MainActivity
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)

            // Закрываем SplashActivity
            finish()
        }, 2500) // 2500 мс = 2.5 секунды
    }
}