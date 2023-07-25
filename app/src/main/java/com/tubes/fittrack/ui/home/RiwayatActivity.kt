package com.tubes.fittrack.ui.home

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.tubes.fittrack.MainActivity
import com.tubes.fittrack.R
import com.tubes.fittrack.api.Activity
import com.tubes.fittrack.api.Food
import com.tubes.fittrack.api.ResponseMakananAktivitas
import com.tubes.fittrack.api.ResponseRiwayat
import com.tubes.fittrack.api.ResponseTanggal
import com.tubes.fittrack.api.RetrofitClient
import com.tubes.fittrack.api.Tanggal
import com.tubes.fittrack.auth.LoginActivity
import com.tubes.fittrack.databinding.ActivityRiwayatBinding
import com.tubes.fittrack.ui.notifications.AktivitasAdapter
import com.tubes.fittrack.ui.notifications.MakananAdapter
import com.tubes.fittrack.ui.profile.ProfileFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RiwayatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRiwayatBinding
    private lateinit var makananAdapter: MakananAdapter
    private lateinit var makananRecyclerView: RecyclerView

    private lateinit var aktivitasAdapter: AktivitasAdapter
    private lateinit var aktivitasRecyclerView: RecyclerView

    private lateinit var spinnerTanggal: Spinner
    private lateinit var tanggalAdapter: ArrayAdapter<String>
    private lateinit var tanggalList: List<Tanggal>
    private lateinit var tanggalStrings: List<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRiwayatBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val sharedPreferences = getSharedPreferences("userPref", Context.MODE_PRIVATE)
        val email: String? = sharedPreferences?.getString("email", "")
        getTanggal(email!!)
        // Inisialisasi Spinner dan adapter
        spinnerTanggal = binding.spinnerTanggal
        tanggalAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item)
        tanggalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTanggal.adapter = tanggalAdapter


        // Set listener untuk Spinner
        spinnerTanggal.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long,
            ) {

                val selectedTanggal = tanggalList[position].tanggal.toString()
                Toast.makeText(this@RiwayatActivity, "$selectedTanggal", Toast.LENGTH_SHORT).show()
                getMakananAktivitas(selectedTanggal, email!!)


            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Kosongkan aksi yang dilakukan jika tidak ada item yang dipilih
            }
        }
        //Data Makanan
        makananRecyclerView = binding.recycler2
        makananRecyclerView.layoutManager = LinearLayoutManager(this@RiwayatActivity)
        makananAdapter = MakananAdapter()
        makananRecyclerView.adapter = makananAdapter

        //Data Aktivitas
        aktivitasRecyclerView = binding.recycler1
        aktivitasRecyclerView.layoutManager = LinearLayoutManager(this@RiwayatActivity)
        aktivitasAdapter = AktivitasAdapter()
        aktivitasRecyclerView.adapter = aktivitasAdapter


        binding.ivBack.setOnClickListener {
            val intent = Intent(this@RiwayatActivity, MainActivity::class.java)

            startActivity(intent)
            finish()
        }

    }

    private fun getMakananAktivitas(tanggal: String, email: String) {
        val pDialog = SweetAlertDialog(this@RiwayatActivity, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.titleText = "Loading Profil"
        pDialog.setCancelable(false)
        pDialog.show()
        RetrofitClient.instance.getRiwayat(tanggal, email).enqueue(object :
            Callback<ResponseRiwayat> {
            override fun onResponse(
                call: Call<ResponseRiwayat>,
                response: Response<ResponseRiwayat>,
            ) {
                if (response.isSuccessful) {
                    val responseMakananAktivitas = response.body()
                    val status = responseMakananAktivitas?.status
                    if (status == true) {
                        if (responseMakananAktivitas != null) {
                            val data = responseMakananAktivitas.data
                            val makananList = data.makanan
                            val aktivitasList = data.aktivitas
                            aktivitasAdapter.setData(aktivitasList)
                            makananAdapter.setData(makananList)
                            if(makananList!=null){
                                val totalkalorimakanan=calculateTotalKaloriMakanan(makananList)
                                binding.tvTotalKaloriMakanan.setText(String.format("%.1f", totalkalorimakanan))
                            }else{
                                binding.tvTotalKaloriMakanan.text="--"
                            }

                            if(aktivitasList!=null){
                                val totalkaloriaktivitas=calculateTotalKaloribakar(aktivitasList)
                                binding.tvTotalKaloriAktivitas.setText(String.format("%.1f", totalkaloriaktivitas))

                            }else{
                                binding.tvTotalKaloriAktivitas.text="--"
                            }
                        }
                    } else {
                        Toast.makeText(
                            this@RiwayatActivity,
                            "Data Tidak ditemukan",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else {
                    val errorBody = response.errorBody()
                    if (errorBody != null) {
                        val errorMessage = errorBody.string()
                        println("Error: $errorMessage")
                    }
                }
                pDialog.dismiss()
            }

            override fun onFailure(call: Call<ResponseRiwayat>, t: Throwable) {
                pDialog.dismiss()
                println("Failure: ${t.message}")
            }

        })
    }

    private fun getTanggal(email: String) {
        RetrofitClient.instance.getTanggal(email).enqueue(object : Callback<ResponseTanggal> {
            override fun onResponse(
                call: Call<ResponseTanggal>,
                response: Response<ResponseTanggal>,
            ) {
                if (response.isSuccessful) {
                    val responseTanggal = response.body()
                    val status = responseTanggal?.status
                    if (status == true) {
                        if (responseTanggal != null) {
                            tanggalList = responseTanggal?.data ?: emptyList()
                            tanggalList?.let {
                                tanggalStrings = it.map { tanggalData -> tanggalData.tanggal }
                                tanggalAdapter.clear()
                                tanggalAdapter.addAll(tanggalStrings)
                                tanggalAdapter.notifyDataSetChanged()
                            }
                        }
                    } else {
                        Toast.makeText(
                            this@RiwayatActivity,
                            "Data Tidak ditemukan",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else {
                    val errorBody = response.errorBody()
                    if (errorBody != null) {
                        val errorMessage = errorBody.string()
                        println("Error: $errorMessage")
                    }
                }
            }

            override fun onFailure(call: Call<ResponseTanggal>, t: Throwable) {
                println("Failure: ${t.message}")
            }

        })
    }

    private fun calculateTotalKaloriMakanan(makananList: List<Food>): Float {
        var totalKalori = 0f
        for (makanan in makananList) {
            val kalori = makanan.kalori.toFloat()
            totalKalori += kalori
        }
        return totalKalori
    }

    private fun calculateTotalKaloribakar(aktivitasList: List<Activity>): Float {
        var totalKalori = 0f
        for (aktivitas in aktivitasList) {
            val kalori = aktivitas.kalori.toFloat()
            totalKalori += kalori
        }

        return totalKalori
    }
}