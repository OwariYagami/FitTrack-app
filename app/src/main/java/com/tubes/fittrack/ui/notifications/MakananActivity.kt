package com.tubes.fittrack.ui.notifications

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.tubes.fittrack.R
import com.tubes.fittrack.api.ResponseTambahMakanan
import com.tubes.fittrack.api.ResponseUserProfile
import com.tubes.fittrack.api.RetrofitClient
import com.tubes.fittrack.auth.LoginActivity
import com.tubes.fittrack.ninjasApi.ApiClient.apiService
import com.tubes.fittrack.caloriesApi.DetailResponse
import com.tubes.fittrack.caloriesApi.FoodItem
import com.tubes.fittrack.caloriesApi.FoodItemAdapter
import retrofit2.*
import com.tubes.fittrack.databinding.ActivityMakananBinding
import com.tubes.fittrack.ninjasApi.ActivityItem


class MakananActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMakananBinding
    private lateinit var adapter: ArrayAdapter<String>
    val apiKey = "WXHNixkWnzfwDt788KwmSQ==TiyqAmEbtpAYlIYJ"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMakananBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnCari.setOnClickListener {
            val query: String = binding.etCariMakanan.text.toString()
            Toast.makeText(this@MakananActivity, query, Toast.LENGTH_SHORT).show()
            apiService.getFoodInfo(apiKey, query)
                .enqueue(object : Callback<List<FoodItem>> {
                    override fun onResponse(
                        call: Call<List<FoodItem>>,
                        response: Response<List<FoodItem>>,
                    ) {
                        if (response.isSuccessful) {
                            val activities = response.body()
                            if (activities != null) {
                                val listView: ListView = findViewById(R.id.foodItemsListView)
                                val activityAdapter = FoodItemAdapter(activities)
                                listView.adapter = activityAdapter
                            }
                        } else {
                            val errorBody = response.errorBody()
                            if (errorBody != null) {
                                val errorMessage = errorBody.string()
                                Toast.makeText(
                                    this@MakananActivity,
                                    "$errorMessage",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                    }

                    override fun onFailure(call: Call<List<FoodItem>>, t: Throwable) {
                        println("Failure: ${t.message}")
                        Toast.makeText(this@MakananActivity, " ${t.message}", Toast.LENGTH_LONG)
                            .show()

                    }

                })
        }
        binding.btnSimpan.setOnClickListener {
            val sharedPreferences =getSharedPreferences("userPref", Context.MODE_PRIVATE)

            val email: String? = sharedPreferences?.getString("email","")
            val makanan: String = binding.listMakanan.text.toString()
            val takaran: String = binding.etTakaran.text.toString()
            val kalori: String = binding.etKalori.text.toString()
            postMakanan(email!!,makanan,takaran,kalori)
            Toast.makeText(this@MakananActivity, "Save :"+binding.listMakanan.text.toString()+" ,"+binding.etKalori.text.toString()
                +" , "+binding.etTakaran.text.toString(), Toast.LENGTH_SHORT).show()
        }

    }

    fun displayClickedItem(item1: String,item2: String,item3: String) {
        binding.etKalori.setText(item2)
        binding.etTakaran.setText(item3)
        binding.listMakanan.setText(item1)
    }

    fun postMakanan(email:String,nama:String,takaran:String,kalori:String){
        val pDialog = SweetAlertDialog(this@MakananActivity, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.titleText = "Loading"
        pDialog.setCancelable(false)
        pDialog.show()
        RetrofitClient.instance.tambahMakanan(email,nama,takaran, kalori).enqueue(object : Callback<ResponseTambahMakanan>{
            override fun onResponse(
                call: Call<ResponseTambahMakanan>,
                response: Response<ResponseTambahMakanan>
            ) {
                val tambahResponse = response.body()
                val status = tambahResponse?.status
                val message = tambahResponse?.message
                if (status == true) {
                    Toast.makeText(this@MakananActivity, "Makanan $message", Toast.LENGTH_SHORT).show()
                    pDialog.dismiss()
                }
            }

            override fun onFailure(call: Call<ResponseTambahMakanan>, t: Throwable) {
                pDialog.dismiss()
                Toast.makeText(this@MakananActivity, t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }


}