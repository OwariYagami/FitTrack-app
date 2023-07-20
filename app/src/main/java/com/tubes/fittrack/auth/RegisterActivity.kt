package com.tubes.fittrack.auth

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
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
    private var isPasswordVisible = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_register)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnDaftar.setOnClickListener {
            val name: String = binding.etName.text.toString()
            val email: String = binding.etEmail.text.toString()
            val password: String = binding.etPassword.text.toString()
            val KonfirmasiPassword: String = binding.etKonfirmpassword.text.toString()
            registerUser(name, email, password, KonfirmasiPassword)

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
        val pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.titleText = "Loading"
        pDialog.setCancelable(false)
        pDialog.show()

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
                        pDialog.dismiss()
                        SweetAlertDialog(this@RegisterActivity, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("$message")
                            .setContentText("silahkan login")
                            .show()
                    } else {
                        pDialog.dismiss()
                        SweetAlertDialog(this@RegisterActivity, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("$message")
                            .show()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseRegister>, t: Throwable) {
                pDialog.dismiss()
                Toast.makeText(this@RegisterActivity, t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }
    fun onShowHidePasswordClick(view: View) {
        if (isPasswordVisible) {
            // Hide Password
            binding.etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            binding.ivShowhide.setImageResource(R.drawable.eyeslash)
        } else {
            // Show Password
            binding.etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            binding.ivShowhide.setImageResource(R.drawable.ic_eye)
        }

        // Toggle the flag
        isPasswordVisible = !isPasswordVisible

        // Move cursor to the end of the text
        binding.etPassword.setSelection(binding.etPassword.text.length)
    }

    fun onShowHidePasswordClick2(view: View) {
        if (isPasswordVisible) {
            // Hide Password
            binding.etKonfirmpassword.transformationMethod = PasswordTransformationMethod.getInstance()
            binding.ivShowhide2.setImageResource(R.drawable.eyeslash)
        } else {
            // Show Password
            binding.etKonfirmpassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            binding.ivShowhide2.setImageResource(R.drawable.ic_eye)
        }

        // Toggle the flag
        isPasswordVisible = !isPasswordVisible

        // Move cursor to the end of the text
        binding.etKonfirmpassword.setSelection(binding.etKonfirmpassword.text.length)
    }
}