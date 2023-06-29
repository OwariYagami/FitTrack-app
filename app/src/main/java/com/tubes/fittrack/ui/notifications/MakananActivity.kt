package com.tubes.fittrack.ui.notifications

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.tubes.fittrack.R
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
            Toast.makeText(this@MakananActivity, "Save :"+binding.listMakanan.text.toString()+" ,"+binding.etKalori.text.toString()
                +" , "+binding.etTakaran.text.toString(), Toast.LENGTH_SHORT).show()
        }

    }

    fun displayClickedItem(item1: String,item2: String,item3: String) {
        binding.etKalori.setText(item2)
        binding.etTakaran.setText(item3)
        binding.listMakanan.setText(item1)
    }


}