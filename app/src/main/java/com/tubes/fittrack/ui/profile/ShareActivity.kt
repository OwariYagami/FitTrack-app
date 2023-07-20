package com.tubes.fittrack.ui.profile

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.tubes.fittrack.R
import com.tubes.fittrack.api.ResponseUserProfile
import com.tubes.fittrack.api.RetrofitClient

import com.tubes.fittrack.databinding.ActivityShareBinding

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ShareActivity : AppCompatActivity() {
    private lateinit var binding:ActivityShareBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShareBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val sharedPreferences =getSharedPreferences("userPref", Context.MODE_PRIVATE)
        val kalori:Float?=sharedPreferences?.getFloat("kalori",0f)
        binding.tvKalori.setText(String.format("%.1f", kalori))
        binding.tvKalori.invalidate()
        val email: String? = sharedPreferences?.getString("email","")
        userProfil(email!!,binding)
        binding.btnShare.setOnClickListener {
            binding.btnShare.visibility = View.INVISIBLE
            val view = binding.constraintLayout2
            val bitmap = getBitmapFromView(view)
            // Simpan gambar ke penyimpanan eksternal
            val imageUri = saveImageToExternalStorage(bitmap)

            if (imageUri != null) {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "image/jpeg"
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
                startActivity(Intent.createChooser(shareIntent,"Share To"))
                binding.btnShare.visibility = View.VISIBLE
            } else {
                // Penanganan jika gambar tidak dapat disimpan
            }
        }

    }

    private fun userProfil(email: String, binding: ActivityShareBinding){
        val pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
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
                if (response.isSuccessful){
                    val responseUserProfile = response.body()
                    val status = responseUserProfile?.status
                    val name = responseUserProfile?.name
                    val data = responseUserProfile?.data
                    if (status == true){
//                        val id: Int? = data?.id
//                        val id_user: Int? = data?.id_user
                        val usia: Int? = data?.usia
//                        val kelamin: String? = data?.kelamin
                        val b_badan: Int? = data?.b_badan
                        val t_badan: Int? = data?.t_badan
                        val image: String? = data?.image

                        if (image != null){
                            val imageUrl: String = RetrofitClient.IMAGE_URL + image
                            Glide.with(this@ShareActivity)
                                .load(imageUrl)
                                .apply(RequestOptions().centerCrop())
                                .into(binding.ivProfile)
                        }

                        binding.tvUsername.setText(name)


                     
                    }
                }
                pDialog.dismiss()
            }

            override fun onFailure(call: Call<ResponseUserProfile>, t: Throwable) {
                pDialog.dismiss()
                Toast.makeText(this@ShareActivity, t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun saveImageToExternalStorage(bitmap: Bitmap): Uri? {
        val imagesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File(imagesDir, "kalori_image.jpg")

        return try {
            val outputStream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            // Gunakan FileProvider untuk membuat URI yang aman
            FileProvider.getUriForFile(this@ShareActivity, "${packageName}.fileprovider", imageFile)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
    private fun getBitmapFromView(view: View): Bitmap {
        val width = view.width
        val height = view.height

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)

        return bitmap
    }
    private fun getBitmapFromLayout(layoutResId: Int): Bitmap {
        val view = LayoutInflater.from(this@ShareActivity).inflate(layoutResId, null)
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
}