package com.tubes.fittrack.ui.home

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.compose.ui.graphics.Canvas
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.tubes.fittrack.MainActivity
import com.tubes.fittrack.R
import com.tubes.fittrack.api.Activity
import com.tubes.fittrack.api.Food
import com.tubes.fittrack.api.ResponseMakananAktivitas
import com.tubes.fittrack.api.ResponseUserProfile
import com.tubes.fittrack.api.RetrofitClient
import com.tubes.fittrack.auth.LoginActivity
import com.tubes.fittrack.databinding.ActivityRiwayatBinding
import com.tubes.fittrack.databinding.ActivityShareBinding
import com.tubes.fittrack.databinding.FragmentHomeBinding
import com.tubes.fittrack.databinding.FragmentProfileBinding
import com.tubes.fittrack.ui.notifications.MakananActivity
import com.tubes.fittrack.ui.profile.EditProfileActivity
import com.tubes.fittrack.ui.profile.ShareActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private var binding2: ActivityShareBinding?=null
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
        userProfil(email!!, _binding!!)
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


        binding.tvLihatRiwayat.setOnClickListener {
            val intent = Intent(activity, RiwayatActivity::class.java)
            startActivity(intent)
        }

        binding2 = ActivityShareBinding.inflate(layoutInflater)
        val view = binding.root
        binding.cl1.setOnClickListener {
            val intent = Intent(requireContext(), ShareActivity::class.java)
            startActivity(intent)
        }
        binding.btnUpdateBb.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            startActivity(intent)
        }
        binding.refresh.setOnRefreshListener {
            userProfil(email!!,binding!!)
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
            binding.refresh.isRefreshing=false
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
                                val sharedPreferences = requireContext().getSharedPreferences("userPref", Context.MODE_PRIVATE)
                                val editor = sharedPreferences.edit()
                                editor.putFloat("kalori",totalKalori)
                                editor.apply()
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
                            SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Informasi")
                                .setContentText("Anda belum mengisi data diri, isi terlebih dahulu !!")
                                .setConfirmText("Baik")
                                .setConfirmClickListener { sDialog ->
                                    sDialog.dismissWithAnimation()
                                    val intent =
                                        Intent(requireContext(), EditProfileActivity::class.java)
                                    startActivity(intent)
                                }.show()

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
                            binding.tvDeskripsi1.text="IMT anda menunjukkan "+interpretasiBMI(imt)
                            val imtresult=interpretasiBMI(imt)
                            val imtdesc=getBMIDescription(imtresult)

                            binding.tvDeskripsi2.text=imtdesc


                        } else {
                            binding.tvImt.setText("--")
                            binding.tvCategory.setText("--")
                        }

                        if(b_badan==0){
                            val intent = Intent(activity, EditProfileActivity::class.java)
                            startActivity(intent)
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
    fun getBMIDescription(bmiResult: String): String {
        return when (bmiResult) {
            "Underweight" -> "Anda dapat menggunakan aplikasi ini untuk Bulking(Menaikkan Berat Badan) untuk mencapai berat badan ideal."
            "Normal" -> "Anda dapat menggunakan aplikasi ini untuk Bulking (Menaikkan berat badan), Diet (Menurunkan berat badan) sesuai keinginan anda, atau menjaga berat badan anda."
            "Overweight" -> "Anda dapat menggunakan aplikasi ini untuk diet atau menjaga berat badan anda."
            "Obesity" -> "Anda dapat menggunakan aplikasi ini untuk melakukan diet agar berat badan anda kembali normal dan tidak mengganggu kesehatan anda"
            else -> "Tidak dapat menemukan interpretasi BMI Anda."
        }
    }

    private fun createImageWithKalori(kalori: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(500, 200, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)

        // Gambar latar belakang
        canvas.drawColor(Color.WHITE)

        // Konfigurasi tampilan teks
        val textSize = 48f
        val textColor = Color.BLACK

        // Gambar teks
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.textSize = textSize
        paint.color = textColor
        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER

        // Menggambar teks jumlah kalori di tengah gambar
        val x = canvas.width / 2f
        val y = (canvas.height / 2f) - ((paint.descent() + paint.ascent()) / 2f)
        canvas.drawText("Kalori: $kalori", x, y, paint)

        return bitmap
    }
    private fun saveImageToExternalStorage(bitmap: Bitmap): Uri? {
        val imagesDir = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File(imagesDir, "kalori_image.jpg")

        return try {
            val outputStream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            // Gunakan FileProvider untuk membuat URI yang aman
            FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.fileprovider", imageFile)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
    private fun saveImageToCache(bitmap: Bitmap): Uri? {
        val cachePath = File(requireContext().externalCacheDir, "images")
        cachePath.mkdirs() // Make sure the directory exists

        val imageFile = File(cachePath, "kalori_image.jpg")
        return try {
            val outputStream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            Uri.fromFile(imageFile)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun getBitmapFromLayout(layoutResId: Int): Bitmap {
        val view = LayoutInflater.from(requireContext()).inflate(layoutResId, null)
        val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        view.measure(widthMeasureSpec, heightMeasureSpec)
        val width = view.measuredWidth
        val height = view.measuredHeight
        view.layout(0, 0, width, height)

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)
//        canvas.drawColor(Color.WHITE) // Set background color if needed
        view.draw(canvas)

        return bitmap
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}