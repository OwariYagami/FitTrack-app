package com.tubes.fittrack.auth

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.tubes.fittrack.MainActivity
import com.tubes.fittrack.R
import com.tubes.fittrack.api.RetrofitClient
import com.tubes.fittrack.api.Users
import com.tubes.fittrack.databinding.ActivityLoginBinding
import com.tubes.fittrack.ui.home.HomeFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {


    private lateinit var binding: ActivityLoginBinding

    companion object {
        var name1: String = ""
        var email1: String = ""
        var status1 = false

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
//        if (status1) {
//            val intent = Intent(this@LoginActivity, MainActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
//            startActivity(intent)
//        }

        binding.btnMasuk.setOnClickListener {
            val emailUser: String = binding.etEmail.text.toString()
            val passUser: String = binding.etPassword.text.toString()
            loginUser(emailUser, passUser)

        }

        binding.btnRegister.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        val sharedPreferences = getSharedPreferences("userPref", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            // Pengguna sudah login, beralih ke halaman home
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Mengakhiri aktivitas login agar pengguna tidak dapat kembali ke halaman login dengan menekan tombol back
        }


    }

    private fun loginUser(email: String, password: String) {
        val pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.titleText = "Loading"
        pDialog.setCancelable(false)
        pDialog.show()
        RetrofitClient.instance.login(email, password).enqueue(object : Callback<Users> {
            override fun onResponse(call: Call<Users>, response: Response<Users>) {
                if (response.isSuccessful) {
                    val userResponse = response.body()
                    val status = userResponse?.status
                    val user = userResponse?.user

                    // Tampilkan status, name, dan email
                    if (status == true && user != null) {
                        val name = user.name
                        val email = user.email

                        name1 = name
                        email1 = email
                        status1 = status

                        val sharedPreferences = getSharedPreferences("userPref", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putBoolean("isLoggedIn", status1)
                        editor.putString("email",email)
                        editor.apply()

                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                    }
                } else {
                    pDialog.dismiss()
                    SweetAlertDialog(this@LoginActivity, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("Login Gagal")
                        .show()
                }
                pDialog.dismiss()
            }

            override fun onFailure(call: Call<Users>, t: Throwable) {
                pDialog.dismiss()
                SweetAlertDialog(this@LoginActivity, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Login Gagal")
                    .show()
//                Toast.makeText(this@LoginActivity, t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }
}