package com.tubes.fittrack.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.tubes.fittrack.R

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var btnIntent : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnIntent = findViewById(R.id.btn_register)

        btnIntent.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.btn_register ->{
                val intentB = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intentB)
            }
        }
    }
}