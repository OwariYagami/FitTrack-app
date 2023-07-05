package com.tubes.fittrack.ui.home

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.tubes.fittrack.api.Activity
import com.tubes.fittrack.api.Food
import com.tubes.fittrack.api.ResponseMakananAktivitas
import com.tubes.fittrack.api.ResponseUserProfile
import com.tubes.fittrack.api.RetrofitClient
import com.tubes.fittrack.auth.LoginActivity
import com.tubes.fittrack.databinding.FragmentHomeBinding
import com.tubes.fittrack.databinding.FragmentProfileBinding
import com.tubes.fittrack.ui.notifications.MakananActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private var user_kalori: Int? = 0
    private var current_kalori: Float? = 0f

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val sharedPreferences = context?.getSharedPreferences("userPref", Context.MODE_PRIVATE)

        val email: String? = sharedPreferences?.getString("email", "")
        getMakananAktivitas(email!!)
        getMakananAktivitaskemarin(email!!)
        if(user_kalori!=null){
            binding.progressbar.max = user_kalori!!
        }else{
            binding.progressbar.max = 0
        }



        val current = current_kalori!!.toInt()
        binding.tvCurrentCal.setText(String.format("%.1f", current_kalori))
        ObjectAnimator.ofInt(binding.progressbar, "progress", current!!)
            .setDuration(2000)
            .start()

        userProfil(email!!, _binding!!)
        binding.tvLihatRiwayat.setOnClickListener {
            val intent = Intent(activity, RiwayatActivity::class.java)
            startActivity(intent)
        }



        return root
    }
    private fun getMakananAktivitaskemarin(email:String){
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
                            if(makananList!=null){
                                val totalkalorimakanan=calculateTotalKalori(makananList)
                                binding.tvCalo.setText(String.format("%.1f", totalkalorimakanan))
                            }else{
                                binding.tvCalo.text="--"
                            }

                            if(aktivitasList!=null){
                                val totalkaloriaktivitas=calculateTotalKaloribakar(aktivitasList)
                                binding.tvBurn.setText(String.format("%.1f", totalkaloriaktivitas))
                                val persenaktivitas=(totalkaloriaktivitas / user_kalori!!) * 100
                                binding.tvActi.setText(String.format("%.1f", persenaktivitas))
                            }else{
                                binding.tvBurn.text="--"
                                binding.tvCalo.text="--"
                                binding.tvActi.text="--"
                            }



                        }
                    }else{
                        Toast.makeText(requireContext(), "Data Tidak ditemukan", Toast.LENGTH_SHORT).show()
                        binding.tvCalo.text="--"
                        binding.tvBurn.text="--"
                        binding.tvActi.text="--"
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
    private fun getMakananAktivitas(email: String) {
        RetrofitClient.instance.getDataMakananAktivitas(email)
            .enqueue(object : Callback<ResponseMakananAktivitas> {
                override fun onResponse(
                    call: Call<ResponseMakananAktivitas>,
                    response: Response<ResponseMakananAktivitas>,
                ) {
                    if (response.isSuccessful) {
                        val responseMakananAktivitas = response.body()
                        val status = responseMakananAktivitas?.status
                        if (status == true) {
                            if (responseMakananAktivitas != null) {
                                val data = responseMakananAktivitas.data
                                val makananList = data.makanan
                                val aktivitasList = data.aktivitas
                                val totalKalori = calculateTotalKalori(makananList)
                                current_kalori = totalKalori
                            }
                        } else {
                            Toast.makeText(
                                requireContext(),
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

                override fun onFailure(call: Call<ResponseMakananAktivitas>, t: Throwable) {
                    println("Failure: ${t.message}")
                }

            })
    }

    private fun calculateTotalKalori(makananList: List<Food>): Float {
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

    private fun userProfil(email: String, binding: FragmentHomeBinding) {
        val pDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.titleText = "Loading Profil"
        pDialog.setCancelable(false)
        pDialog.show()
        RetrofitClient.instance.getUserProfil(email).enqueue(object :
            Callback<ResponseUserProfile> {
            override fun onResponse(
                call: Call<ResponseUserProfile>,
                response: Response<ResponseUserProfile>,
            ) {
                if (response.isSuccessful) {
                    val responseUserProfile = response.body()
                    val status = responseUserProfile?.status
                    val name = responseUserProfile?.name
                    val data = responseUserProfile?.data
                    if (status == true) {
                        val kelamin: String? = data?.kelamin
                        val b_badan: Int? = data?.b_badan
                        val t_badan: Int? = data?.t_badan
                        user_kalori = data?.kalori

                        binding.tvWelcome.setText("Selamat Datang, $name")
                        if (user_kalori != null) {
                            binding.tvTargetCal.text = "/" + user_kalori.toString()
                        } else {
                            binding.tvTargetCal.text = "/--"
                        }
                        if (b_badan != null) {
                            binding.tvWeight.setText(b_badan.toString())
                        } else {
                            binding.tvWeight.setText("--")
                        }

                        if (t_badan != null) {
                            binding.tvHeight.setText(t_badan.toString())
                        } else {
                            binding.tvHeight.setText("--")
                        }

                        binding.tvGender.text = if (kelamin != null) {
                            kelamin.toString()
                        } else {
                            "--"
                        }

                        if (b_badan != null && t_badan != null) {
                            val imt = calculateBMI(b_badan.toDouble(), t_badan.toDouble())
                            binding.tvImt.setText(String.format("%.1f", imt))
                            binding.tvCategory.setText(interpretasiBMI(imt))

                        } else {
                            binding.tvImt.setText("--")
                            binding.tvCategory.setText("--")
                        }
                    }
                }
                pDialog.dismiss()
            }

            override fun onFailure(call: Call<ResponseUserProfile>, t: Throwable) {
                pDialog.dismiss()
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    fun calculateBMI(berat: Double, tinggi: Double): Double {
        val tinggiMeter = tinggi / 100
        return berat / (tinggiMeter * tinggiMeter)
    }

    fun interpretasiBMI(bmi: Double): String {
        return when {
            bmi < 18.5 -> "Underweight"
            bmi < 25.0 -> "Normal"
            bmi < 30.0 -> "Overweight"
            else -> "Obesity"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}