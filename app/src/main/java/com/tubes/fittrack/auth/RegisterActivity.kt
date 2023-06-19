package com.tubes.fittrack.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.tubes.fittrack.MainActivity
import com.tubes.fittrack.R
import com.tubes.fittrack.api.ResponseRegister
import com.tubes.fittrack.api.RetrofitClient
import com.tubes.fittrack.databinding.ActivityRegisterBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_register)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnDaftar.setOnClickListener {
            val email: String = binding.etEmail.text.toString()
            val password: String = binding.etPassword.text.toString()
            val KonfirmasiPassword: String = binding.etKonfirmpassword.text.toString()
            registerUser("anonim", email, password, KonfirmasiPassword)

        }

        binding.btnMasuk.setOnClickListener{
            val intent = Intent(this@RegisterActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun registerUser(
        name: String,
        email: String,
        password: String,
        KonfirmasiPassword: String
    ){
        RetrofitClient.instance.register(
            name,
            email,
            password,
            KonfirmasiPassword
        ).enqueue(object : Callback<ResponseRegister>{
            override fun onResponse(
                call: Call<ResponseRegister>,
                response: Response<ResponseRegister>,
            ) {
                if (response.isSuccessful){
                    val registerResponse = response.body()
                    val status = registerResponse?.status
                    val message = registerResponse?.message

                    if (status == true){
                        Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseRegister>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }
}