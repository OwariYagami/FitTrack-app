package com.tubes.fittrack.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.tubes.fittrack.api.DataLocation
import com.tubes.fittrack.api.ResponseLocation
import com.tubes.fittrack.api.RetrofitClient
import com.tubes.fittrack.databinding.FragmentDashboardBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    private var list = ArrayList<DataLocation>()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        getLocation()

        return root
    }

    fun getLocation() {
        RetrofitClient.instance.getLocation().enqueue(object : Callback<ResponseLocation>{
            override fun onResponse(
                call: Call<ResponseLocation>,
                response: Response<ResponseLocation>,
            ) {
                if (response.isSuccessful){
                    val responseLocation = response.body()
                    val status = responseLocation?.status
                    val data = responseLocation?.data
                    if (status == true){
                        if (data != null){
                            list.addAll(data)
                            showRecylerList()
                        }
                    }

                }
            }

            override fun onFailure(call: Call<ResponseLocation>, t: Throwable) {
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    fun showRecylerList(){
        binding.rvLocation.layoutManager = LinearLayoutManager(requireContext())
        val locationAdapter = LocationAdapter(list)
        binding.rvLocation.adapter = locationAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}