package com.tubes.fittrack.auth

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.tubes.fittrack.R
import com.tubes.fittrack.databinding.ActivityLoginBinding
import com.tubes.fittrack.databinding.ActivitySplachScreenBinding

class SplashScreenActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivitySplachScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splach_screen)
        binding = ActivitySplachScreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        binding.btnMulai.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.btn_mulai ->{
                val intentB = Intent(this@SplashScreenActivity, LoginActivity::class.java)
                startActivity(intentB)
            }
        }
    }

//    override fun onStop() {
//        super.onStop()
//        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
//        val editor = sharedPreferences.edit()
//        editor.putBoolean("isLoggedIn", false)
//        editor.apply()
////        Toast.makeText(this@SplashScreenActivity, "onStopSplash", Toast.LENGTH_SHORT).show()
//    }


}