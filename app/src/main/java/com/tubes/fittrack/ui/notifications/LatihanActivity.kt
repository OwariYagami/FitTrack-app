package com.tubes.fittrack.ui.notifications

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.tubes.fittrack.R
import com.tubes.fittrack.databinding.ActivityLatihanBinding
import com.tubes.fittrack.databinding.ActivityLoginBinding
import com.tubes.fittrack.ninjasApi.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LatihanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLatihanBinding
    private lateinit var adapter: ArrayAdapter<String>
    private val spinnerItems = arrayOf("Item 1", "Item 2", "Item 3")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLatihanBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        fetchActivities()
        adapter = ArrayAdapter(this@LatihanActivity, android.R.layout.simple_spinner_item, mutableListOf())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter=adapter
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedActivity = parent.getItemAtPosition(position) as String
                Toast.makeText(this@LatihanActivity, "Selected item: $selectedActivity", Toast.LENGTH_SHORT).show()

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Tidak ada yang dipilih
            }
        }



    }

    private fun fetchActivities() {
        GlobalScope.launch(Dispatchers.Main) {
            try {

                val response = ApiClient.apiService.getActivities()
                if (response.isSuccessful) {
                    val activityResponse = response.body()
                    val activities = activityResponse?.activities
                    if (!activities.isNullOrEmpty()) {
                        adapter.addAll(activities)
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    // Error handling for unsuccessful response
                }
            } catch (e: Exception) {
                // Error handling for network or other exceptions
            }
        }
    }
}