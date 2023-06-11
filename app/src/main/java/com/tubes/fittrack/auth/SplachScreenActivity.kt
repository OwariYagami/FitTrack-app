package com.tubes.fittrack.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.tubes.fittrack.R

class SplachScreenActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var btnIntent : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splach_screen)

        btnIntent = findViewById(R.id.btn_mulai)

        btnIntent.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.btn_mulai ->{
                val intentB = Intent(this@SplachScreenActivity, LoginActivity::class.java)
                startActivity(intentB)
            }
        }
    }


}