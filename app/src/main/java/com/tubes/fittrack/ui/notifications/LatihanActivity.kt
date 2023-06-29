package com.tubes.fittrack.ui.notifications

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.tubes.fittrack.R
import com.tubes.fittrack.caloriesApi.FoodItemAdapter
import com.tubes.fittrack.databinding.ActivityLatihanBinding
import com.tubes.fittrack.databinding.ActivityLoginBinding
import com.tubes.fittrack.ninjasApi.ActivityItem
import com.tubes.fittrack.ninjasApi.ApiClient
import com.tubes.fittrack.ninjasApi.ApiClient.apiService
import com.tubes.fittrack.ninjasApi.LatihanItemAdapter
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
            Toast.makeText(this@LatihanActivity, "Save :"+binding.etNamaLatihan.text.toString()+" ,"+binding.etDurasi.text.toString()
                    +" , "+binding.etKalori.text.toString(), Toast.LENGTH_SHORT).show()
        }


    }

    fun displayClickedItem(item1: String, item2: String, item3: String) {
        binding.etNamaLatihan.setText(item1)
        binding.etDurasi.setText(item2)
        binding.etKalori.setText(item3)
    }


}