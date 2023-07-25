package com.tubes.fittrack.ui.notifications

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.tubes.fittrack.MainActivity
import com.tubes.fittrack.R
import com.tubes.fittrack.api.ResponseTambahMakanan
import com.tubes.fittrack.api.RetrofitClient
import com.tubes.fittrack.auth.LoginActivity
import com.tubes.fittrack.caloriesApi.FoodItemAdapter
import com.tubes.fittrack.databinding.ActivityLatihanBinding
import com.tubes.fittrack.databinding.ActivityLoginBinding
import com.tubes.fittrack.ninjasApi.ActivityItem
import com.tubes.fittrack.ninjasApi.ApiClient
import com.tubes.fittrack.ninjasApi.ApiClient.apiService
import com.tubes.fittrack.ninjasApi.LatihanItemAdapter
import com.tubes.fittrack.ui.home.HomeFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response

class LatihanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLatihanBinding
    private lateinit var adapter: ArrayAdapter<String>
    val apiKey = "WXHNixkWnzfwDt788KwmSQ==TiyqAmEbtpAYlIYJ"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLatihanBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.btnCari.setOnClickListener {
            val activity: String = binding.etCariLatihan.text.toString()
            apiService.getCaloriesBurned(apiKey, activity)
                .enqueue(object : Callback<List<ActivityItem>> {
                    override fun onResponse(
                        call: Call<List<ActivityItem>>,
                        response: Response<List<ActivityItem>>,
                    ) {
                        if (response.isSuccessful) {
                            val activities = response.body()
                            if (activities != null) {
                                val listView: ListView = findViewById(R.id.latihanItemListview)
                                val activityAdapter = LatihanItemAdapter(activities)
                                listView.adapter = activityAdapter
                            }
                        } else {
                            val errorBody = response.errorBody()
                            if (errorBody != null) {
                                val errorMessage = errorBody.string()
                                Toast.makeText(
                                    this@LatihanActivity,
                                    "$errorMessage",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                    }

                    override fun onFailure(call: Call<List<ActivityItem>>, t: Throwable) {
                        println("Failure: ${t.message}")
                        Toast.makeText(this@LatihanActivity, " ${t.message}", Toast.LENGTH_LONG)
                            .show()

                    }

                })

        }

        binding.btnSimpan.setOnClickListener {
            val sharedPreferences =getSharedPreferences("userPref", Context.MODE_PRIVATE)

            val email: String? = sharedPreferences?.getString("email","")
            val latihan: String = binding.etNamaLatihan.text.toString()
            val durasi: String = binding.etDurasi.text.toString()
            val kalori: String = binding.etKalori.text.toString()
            postLatihan(email!!,latihan,durasi,kalori)
            Toast.makeText(this@LatihanActivity, "Save :"+binding.etNamaLatihan.text.toString()+" ,"+binding.etDurasi.text.toString()
                    +" , "+binding.etKalori.text.toString(), Toast.LENGTH_SHORT).show()
        }

        binding.ivBack.setOnClickListener {
            val intent = Intent(this@LatihanActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun displayClickedItem(item1: String, item2: String, item3: String) {
        binding.etNamaLatihan.setText(item1)
        binding.etDurasi.setText(item2)
        binding.etKalori.setText(item3)
    }
    private fun postLatihan(email:String,nama:String,durasi:String,kalori:String){
        val pDialog = SweetAlertDialog(this@LatihanActivity, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.titleText = "Loading"
        pDialog.setCancelable(false)
        pDialog.show()
        RetrofitClient.instance.tambahLatihan(email,nama,durasi, kalori).enqueue(object : Callback<ResponseTambahMakanan>{
            override fun onResponse(
                call: Call<ResponseTambahMakanan>,
                response: Response<ResponseTambahMakanan>
            ) {
                val tambahResponse = response.body()
                val status = tambahResponse?.status
                val message = tambahResponse?.message
                if (status == true) {
                    Toast.makeText(this@LatihanActivity, "Aktivitas $message", Toast.LENGTH_SHORT).show()
                    pDialog.dismiss()
                }
            }

            override fun onFailure(call: Call<ResponseTambahMakanan>, t: Throwable) {
                pDialog.dismiss()
                Toast.makeText(this@LatihanActivity, t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }



}