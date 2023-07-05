package com.tubes.fittrack.ui.home

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tubes.fittrack.R
import com.tubes.fittrack.api.ResponseMakananAktivitas
import com.tubes.fittrack.api.RetrofitClient
import com.tubes.fittrack.auth.LoginActivity
import com.tubes.fittrack.databinding.ActivityRiwayatBinding
import com.tubes.fittrack.ui.notifications.AktivitasAdapter
import com.tubes.fittrack.ui.notifications.MakananAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RiwayatActivity : AppCompatActivity() {
    private lateinit var binding:ActivityRiwayatBinding
    private lateinit var makananAdapter: MakananAdapter
    private lateinit var makananRecyclerView: RecyclerView

    private lateinit var aktivitasAdapter: AktivitasAdapter
    private lateinit var aktivitasRecyclerView: RecyclerView

    private lateinit var spinnerTanggal: Spinner
    private lateinit var tanggalAdapter: ArrayAdapter<String>
    private lateinit var tanggalList: List<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityRiwayatBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        // Inisialisasi Spinner dan adapter
        spinnerTanggal = binding.spinnerTanggal
        tanggalList = emptyList()
        tanggalAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tanggalList)
        tanggalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTanggal.adapter = tanggalAdapter


        // Set listener untuk Spinner
        spinnerTanggal.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedDate = tanggalList[position]
                // Panggil metode untuk menampilkan data berdasarkan tanggal yang dipilih

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Kosongkan aksi yang dilakukan jika tidak ada item yang dipilih
            }
        }
        //Data Makanan
        makananRecyclerView=binding.recycler2
        makananRecyclerView.layoutManager= LinearLayoutManager(this@RiwayatActivity)
        makananAdapter= MakananAdapter()
        makananRecyclerView.adapter=makananAdapter

        //Data Aktivitas
        aktivitasRecyclerView=binding.recycler1
        aktivitasRecyclerView.layoutManager= LinearLayoutManager(this@RiwayatActivity)
        aktivitasAdapter= AktivitasAdapter()
        aktivitasRecyclerView.adapter=aktivitasAdapter
        val sharedPreferences =getSharedPreferences("userPref", Context.MODE_PRIVATE)

        val email: String? = sharedPreferences?.getString("email","")
        getMakananAktivitas(email!!)
    }

   private fun getMakananAktivitas(email:String){
        RetrofitClient.instance.getDatakemarin(email).enqueue(object :
            Callback<ResponseMakananAktivitas> {
            override fun onResponse(
                call: Call<ResponseMakananAktivitas>,
                response: Response<ResponseMakananAktivitas>,
            ) {
                if(response.isSuccessful){
                    val responseMakananAktivitas = response.body()
                    val status=responseMakananAktivitas?.status
                    if (status==true){
                        if(responseMakananAktivitas != null){
                            val data=responseMakananAktivitas.data
                            val makananList=data.makanan
                            val aktivitasList=data.aktivitas




                            aktivitasAdapter.setData(aktivitasList)
                            makananAdapter.setData(makananList)
                        }
                    }else{
                        Toast.makeText(this@RiwayatActivity, "Data Tidak ditemukan", Toast.LENGTH_SHORT).show()
                    }

                }else{
                    val errorBody = response.errorBody()
                    if (errorBody != null) {
                        val errorMessage = errorBody.string()
                        println("Error: $errorMessage")
                    }
                }
            }

            override fun onFailure(call: Call<ResponseMakananAktivitas>, t: Throwable) {
                println("Failure: ${t.message}")
            }

        })
    }
}