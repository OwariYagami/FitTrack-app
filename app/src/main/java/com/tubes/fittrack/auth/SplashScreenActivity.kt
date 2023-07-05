package com.tubes.fittrack.auth

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.tubes.fittrack.MainActivity
import com.tubes.fittrack.R
import com.tubes.fittrack.databinding.ActivityLoginBinding
import com.tubes.fittrack.databinding.ActivitySplachScreenBinding

class SplashScreenActivity : AppCompatActivity() {
    private val splashTimeOut: Long =
        1000 // Durasi splash screen dalam milidetik (misalnya, 3 detik)
    private lateinit var binding: ActivitySplachScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splach_screen)
        binding = ActivitySplachScreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        // Handler untuk menangani penundaan berpindah ke aktivitas utama
        Handler().postDelayed({
            val mainIntent = Intent(this, LoginActivity::class.java)
            startActivity(mainIntent)
            finish()
        }, splashTimeOut)

//        val sharedPreferences = getSharedPreferences("userPref", Context.MODE_PRIVATE)
//        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

//        if (isLoggedIn) {
//
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//            finish()
//        } else {
//            val intent = Intent(this, LoginActivity::class.java)
//            startActivity(intent)
//            finish()
//        }


    }
}
















