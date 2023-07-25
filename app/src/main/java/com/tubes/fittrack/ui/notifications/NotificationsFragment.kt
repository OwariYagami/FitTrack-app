package com.tubes.fittrack.ui.notifications

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tubes.fittrack.api.ResponseMakananAktivitas
import com.tubes.fittrack.api.RetrofitClient
import com.tubes.fittrack.auth.LoginActivity
import com.tubes.fittrack.databinding.FragmentNotificationsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var makananAdapter: MakananAdapter
    private lateinit var makananRecyclerView: RecyclerView

    private lateinit var aktivitasAdapter: AktivitasAdapter
    private lateinit var aktivitasRecyclerView: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnTmbhLatihan.setOnClickListener {
            val intent=Intent(activity,LatihanActivity::class.java)
            startActivity(intent)
        }
        binding.btnTmbhMakanan.setOnClickListener {
            val intent=Intent(activity,MakananActivity::class.java)
            startActivity(intent)
        }
        //Data Makanan
        makananRecyclerView=binding.recycler2
        makananRecyclerView.layoutManager=LinearLayoutManager(requireContext())
        makananAdapter= MakananAdapter()
        makananRecyclerView.adapter=makananAdapter

        //Data Aktivitas
        aktivitasRecyclerView=binding.recycler1
        aktivitasRecyclerView.layoutManager=LinearLayoutManager(requireContext())
        aktivitasAdapter= AktivitasAdapter()
        aktivitasRecyclerView.adapter=aktivitasAdapter
        val sharedPreferences = context?.getSharedPreferences("userPref", Context.MODE_PRIVATE)

        val email: String? = sharedPreferences?.getString("email","")
        getMakananAktivitas(email!!)
        binding.refresh.setOnRefreshListener {
            getMakananAktivitas(email!!)
            binding.refresh.isRefreshing=false
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

   private fun getMakananAktivitas(email:String){
        RetrofitClient.instance.getDataMakananAktivitas(email).enqueue(object : Callback<ResponseMakananAktivitas>{
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
                            if(aktivitasList!=null){
                                aktivitasAdapter.setData(aktivitasList)
                            }else{
                                Toast.makeText(requireContext(), "Data Tidak ditemukan", Toast.LENGTH_SHORT).show()

                            }

                            makananAdapter.setData(makananList)
                        }
                    }else{
                        Toast.makeText(requireContext(), "Data Tidak ditemukan", Toast.LENGTH_SHORT).show()
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