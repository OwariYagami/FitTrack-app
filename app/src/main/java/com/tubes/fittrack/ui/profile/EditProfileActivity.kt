package com.tubes.fittrack.ui.profile

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.tubes.fittrack.api.ResponseUpdateProfil
import com.tubes.fittrack.api.ResponseUserProfile
import com.tubes.fittrack.api.RetrofitClient
import com.tubes.fittrack.auth.LoginActivity
import com.tubes.fittrack.databinding.ActivityEditProfileBinding
import com.tubes.fittrack.databinding.FragmentProfileBinding
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class EditProfileActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_PICK_IMAGE = 1
    }

    private var imageFile: File? = null

    lateinit var binding: ActivityEditProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_edit_profile)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userProfil(LoginActivity.email1)

        binding.ivProfile.setOnClickListener {
            pickImageFromGallery()
        }

        binding.btnSimpan.setOnClickListener {
            val email: String = LoginActivity.email1
            val name: String = binding.etName.text.toString()
            val usia: String = binding.etUsia.text.toString()
            val bBadan: String = binding.etBeratBadan.text.toString()
            val tBadan: String = binding.etTinggiBadan.text.toString()

            uploadPorfileData(email, imageFile, name, usia, "Pria", bBadan, tBadan)
        }
    }

    private fun pickImageFromGallery(){
        // Membuat intent untuk memilih gambar dari galeri
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*" // Menentukan tipe konten yang ingin dipilih (semua jenis gambar)

        // Menjalankan intent untuk memilih gambar
        startActivityForResult(intent, REQUEST_PICK_IMAGE)
    }

    private fun userProfil(email: String){
        RetrofitClient.instance.getUserProfil(email).enqueue(object : Callback<ResponseUserProfile>{
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
                            val imageUrl: String = "http://192.168.1.8:8000/image/$image"
                            Glide.with(this@EditProfileActivity)
                                .load(imageUrl)
                                .apply(RequestOptions().centerCrop())
                                .into(binding.ivProfile)
                        }

                        binding.etName.setText(name)


                        if (usia != null){
                            binding.etUsia.setText(usia.toString())
                        }

                        if (b_badan != null){
                            binding.etBeratBadan.setText(b_badan.toString())
                        }

                        if (t_badan != null){
                            binding.etTinggiBadan.setText(t_badan.toString())
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResponseUserProfile>, t: Throwable) {
                Toast.makeText(this@EditProfileActivity, t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    fun uploadPorfileData(
        email: String,
        imageFile: File?,
        name: String,
        usia: String,
        kelamin: String,
        b_badan: String,
        t_badan: String
    ){
        if (imageFile != null && imageFile.exists()){
            // Buat RequestBody untuk file gambar
            val mediaType = MediaType.parse("image/*")
            val requestFile: RequestBody = RequestBody.create(mediaType, imageFile)
            val imagePart: MultipartBody.Part = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)

            val nameBody: RequestBody = RequestBody.create(MediaType.parse("text/plain"), name)
            val usiaBody: RequestBody = RequestBody.create(MediaType.parse("text/plain"), usia)
            val kelaminBody: RequestBody = RequestBody.create(MediaType.parse("text/plain"), kelamin)
            val b_badanBody: RequestBody = RequestBody.create(MediaType.parse("text/plain"), b_badan)
            val t_badanBody: RequestBody = RequestBody.create(MediaType.parse("text/plain"), t_badan)

            RetrofitClient.instance.uploadProfile(email,
                imagePart,
                nameBody,
                usiaBody,
                kelaminBody,
                b_badanBody,
                t_badanBody
            ).enqueue(object : Callback<ResponseUpdateProfil>{
                override fun onResponse(
                    call: Call<ResponseUpdateProfil>,
                    response: Response<ResponseUpdateProfil>,
                ) {
                    val updateResponse = response.body()
                    val status = updateResponse?.status
                    val message = updateResponse?.message

                    if (status == true){
                        Toast.makeText(this@EditProfileActivity, "Profil $message", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseUpdateProfil>, t: Throwable) {
                    Toast.makeText(this@EditProfileActivity, t.message, Toast.LENGTH_SHORT).show()
                }

            })

        } else {
            val nameBody: RequestBody = RequestBody.create(MediaType.parse("text/plain"), name)
            val usiaBody: RequestBody = RequestBody.create(MediaType.parse("text/plain"), usia)
            val kelaminBody: RequestBody = RequestBody.create(MediaType.parse("text/plain"), kelamin)
            val b_badanBody: RequestBody = RequestBody.create(MediaType.parse("text/plain"), b_badan)
            val t_badanBody: RequestBody = RequestBody.create(MediaType.parse("text/plain"), t_badan)

            RetrofitClient.instance.uploadProfile(email,
                null,
                nameBody,
                usiaBody,
                kelaminBody,
                b_badanBody,
                t_badanBody
            ).enqueue(object : Callback<ResponseUpdateProfil>{
                override fun onResponse(
                    call: Call<ResponseUpdateProfil>,
                    response: Response<ResponseUpdateProfil>,
                ) {
                    val updateResponse = response.body()
                    val status = updateResponse?.status
                    val message = updateResponse?.message

                    if (status == true){
                        Toast.makeText(this@EditProfileActivity, "Profil $message", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseUpdateProfil>, t: Throwable) {
                    Toast.makeText(this@EditProfileActivity, t.message, Toast.LENGTH_SHORT).show()
                }

            })
        }
    }

    @SuppressLint("Recycle")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = data?.data
            if (imageUri != null) {
                binding.ivProfile.setImageURI(imageUri)
                val inputStream = this.contentResolver.openInputStream(imageUri)
                val cursor = this.contentResolver.query(imageUri, null, null, null, null)
                cursor?.use { c ->
                    val nameIndex = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (c.moveToFirst()){
                        val name = c.getString(nameIndex)
                        inputStream?.let { inputStream ->
                            val file = File(this.cacheDir, name)
                            val os = file.outputStream()
                            os.use {
                                inputStream.copyTo(it)
                            }

                            imageFile = file
                        }
                    }
                }

            }
        }
    }
}