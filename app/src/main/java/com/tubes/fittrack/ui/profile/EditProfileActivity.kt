package com.tubes.fittrack.ui.profile


import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.tubes.fittrack.MainActivity
import com.tubes.fittrack.R
import android.R as R2
import com.tubes.fittrack.api.ResponseUpdateProfil
import com.tubes.fittrack.api.ResponseUserProfile
import com.tubes.fittrack.api.RetrofitClient
import com.tubes.fittrack.auth.LoginActivity
import com.tubes.fittrack.databinding.ActivityEditProfileBinding
import com.tubes.fittrack.databinding.FragmentProfileBinding
import com.tubes.fittrack.ui.home.HomeFragment
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
    private lateinit var spinnerJenisKelamin: Spinner
    private val spinnerItems = arrayOf("Bulking", "Diet")
    private val spinnerItems2 = arrayOf("Tingkat Rendah", "Sedang", "Tinggi", "Sangat Tinggi")
    private val jenisKelaminArray = arrayOf("Pria", "Wanita")

    private lateinit var selecttujuan: String
    private lateinit var selectintensitas: String
    private lateinit var selectkelamin: String
    private  var email: String?=""

    lateinit var binding: ActivityEditProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_edit_profile)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnUpdateKalori.setOnClickListener {
            showCustomDialog()
        }





        val sharedPreferences = getSharedPreferences("userPref", Context.MODE_PRIVATE)

       email = sharedPreferences?.getString("email", "")
        userProfil(email!!)

        binding.ivProfile.setOnClickListener {
            pickImageFromGallery()
        }
        spinnerJenisKelamin = binding.spinnerJk
        val adapter3 = ArrayAdapter(this, android.R.layout.simple_spinner_item, jenisKelaminArray)
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerJenisKelamin.adapter = adapter3
        spinnerJenisKelamin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long,
            ) {
                selectkelamin = parent.getItemAtPosition(position).toString()
                Toast.makeText(
                    applicationContext,
                    "Selected item: $selectkelamin",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        binding.btnSimpan.setOnClickListener {

            val name: String = binding.etName.text.toString()
            val usia: String = binding.etUsia.text.toString()
            val bBadan: String = binding.etBeratBadan.text.toString()
            val tBadan: String = binding.etTinggiBadan.text.toString()
            val kalori: String = binding.etKaloriHarian.text.toString()
            val kelamin: String = selectkelamin.toString()
            uploadPorfileData(email!!, imageFile, name, usia, kelamin, bBadan, tBadan, kalori)
        }
        binding.btnBack.setOnClickListener {
            val intent = Intent(this@EditProfileActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    // Fungsi untuk menghitung kebutuhan kalori harian
    fun calculateDailyCalorieNeeds(gender: String, activityLevel: String, goal: String): Double {
        val bmr: Double
        val activityFactor: Double
        val goalFactor: Double
        val weightinput = binding.etBeratBadan.text.toString()
        val heightinput = binding.etTinggiBadan.text.toString()
        val ageinput = binding.etUsia.text.toString()
        val weight = weightinput.toInt()
        val height = heightinput.toInt()
        val age = ageinput.toInt()
        // Menghitung BMR berdasarkan jenis kelamin
        if (gender.equals("Wanita", ignoreCase = true)) {
            bmr = 655 + (9.6 * weight) + (1.8 * height) - (4.7 * age)
        } else { // Pria
            bmr = 66 + (13.7 * weight) + (5 * height) - (6.8 * age)
        }

        // Menentukan faktor aktivitas berdasarkan tingkat aktivitas
        when (activityLevel) {
            "Tingkat Rendah" -> activityFactor = 1.2
            "Sedang" -> activityFactor = 1.375
            "Tinggi" -> activityFactor = 1.55
            "Sangat Tinggi" -> activityFactor = 1.725
            else -> activityFactor = 1.2
        }

        // Menentukan faktor tujuan berdasarkan tujuan yang dipilih
        when (goal) {
            "Bulking" -> goalFactor = 1.2
            "Diet" -> goalFactor = 0.8
            else -> goalFactor = 1.0
        }

        // Menghitung kebutuhan kalori harian
        val dailyCalorieNeeds = bmr * activityFactor * goalFactor

        return dailyCalorieNeeds
    }

    private fun pickImageFromGallery() {
        // Membuat intent untuk memilih gambar dari galeri
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*" // Menentukan tipe konten yang ingin dipilih (semua jenis gambar)

        // Menjalankan intent untuk memilih gambar
        startActivityForResult(intent, REQUEST_PICK_IMAGE)
    }

    private fun userProfil(email: String) {
        val pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.titleText = "Loading Profil"
        pDialog.setCancelable(false)
        pDialog.show()
        RetrofitClient.instance.getUserProfil(email)
            .enqueue(object : Callback<ResponseUserProfile> {
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
//                        val id: Int? = data?.id
//                        val id_user: Int? = data?.id_user
                            val usia: Int? = data?.usia
                            val jenisKelamin = data?.kelamin
                            val index = jenisKelaminArray.indexOf(jenisKelamin)
                            spinnerJenisKelamin.setSelection(index)

                            val b_badan: Int? = data?.b_badan
                            val t_badan: Int? = data?.t_badan
                            val image: String? = data?.image
                            val kalori: Int?=data?.kalori

                            if (image != null) {
                                val imageUrl: String = RetrofitClient.IMAGE_URL + image
                                Glide.with(this@EditProfileActivity)
                                    .load(imageUrl)
                                    .apply(RequestOptions().centerCrop())
                                    .into(binding.ivProfile)
                            }

                            binding.etName.setText(name)


                            if (usia != null) {
                                binding.etUsia.setText(usia.toString())
                            }

                            if (b_badan != null) {
                                binding.etBeratBadan.setText(b_badan.toString())
                            }

                            if (t_badan != null) {
                                binding.etTinggiBadan.setText(t_badan.toString())
                            }
                            if (kalori != null) {
                                binding.etKaloriHarian.setText(kalori.toString())
                            }
                        }
                    }
                    pDialog.dismiss()
                }

                override fun onFailure(call: Call<ResponseUserProfile>, t: Throwable) {
                    pDialog.dismiss()
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
        t_badan: String,
        kalori: String,
    ) {
        val pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        if (imageFile != null && imageFile.exists()) {

            pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
            pDialog.titleText = "Update Profil"
            pDialog.setCancelable(false)
            pDialog.show()
            // Buat RequestBody untuk file gambar
            val mediaType = MediaType.parse("image/*")
            val requestFile: RequestBody = RequestBody.create(mediaType, imageFile)
            val imagePart: MultipartBody.Part =
                MultipartBody.Part.createFormData("image", imageFile.name, requestFile)

            val nameBody: RequestBody = RequestBody.create(MediaType.parse("text/plain"), name)
            val usiaBody: RequestBody = RequestBody.create(MediaType.parse("text/plain"), usia)
            val kelaminBody: RequestBody =
                RequestBody.create(MediaType.parse("text/plain"), kelamin)
            val b_badanBody: RequestBody =
                RequestBody.create(MediaType.parse("text/plain"), b_badan)
            val t_badanBody: RequestBody =
                RequestBody.create(MediaType.parse("text/plain"), t_badan)
            val k_kalori: RequestBody = RequestBody.create(MediaType.parse("text/plain"), kalori)
            RetrofitClient.instance.uploadProfile(
                email,
                imagePart,
                nameBody,
                usiaBody,
                kelaminBody,
                b_badanBody,
                t_badanBody,
                k_kalori
            ).enqueue(object : Callback<ResponseUpdateProfil> {
                override fun onResponse(
                    call: Call<ResponseUpdateProfil>,
                    response: Response<ResponseUpdateProfil>,
                ) {
                    val updateResponse = response.body()
                    val status = updateResponse?.status
                    val message = updateResponse?.message

                    if (status == true) {
                        pDialog.dismiss()
                        Toast.makeText(
                            this@EditProfileActivity,
                            "Profil $message",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ResponseUpdateProfil>, t: Throwable) {
                    Toast.makeText(this@EditProfileActivity, t.message, Toast.LENGTH_SHORT).show()
                }

            })

        } else {
            pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
            pDialog.titleText = "Update Profil"
            pDialog.setCancelable(false)
            pDialog.show()
            val nameBody: RequestBody = RequestBody.create(MediaType.parse("text/plain"), name)
            val usiaBody: RequestBody = RequestBody.create(MediaType.parse("text/plain"), usia)
            val kelaminBody: RequestBody =
                RequestBody.create(MediaType.parse("text/plain"), kelamin)
            val b_badanBody: RequestBody =
                RequestBody.create(MediaType.parse("text/plain"), b_badan)
            val t_badanBody: RequestBody =
                RequestBody.create(MediaType.parse("text/plain"), t_badan)
            val k_kalori: RequestBody = RequestBody.create(MediaType.parse("text/plain"), kalori)
            RetrofitClient.instance.uploadProfile(
                email,
                null,
                nameBody,
                usiaBody,
                kelaminBody,
                b_badanBody,
                t_badanBody,
                k_kalori
            ).enqueue(object : Callback<ResponseUpdateProfil> {
                override fun onResponse(
                    call: Call<ResponseUpdateProfil>,
                    response: Response<ResponseUpdateProfil>,
                ) {
                    val updateResponse = response.body()
                    val status = updateResponse?.status
                    val message = updateResponse?.message

                    if (status == true) {
                        pDialog.dismiss()
                        Toast.makeText(
                            this@EditProfileActivity,
                            "Profil $message",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ResponseUpdateProfil>, t: Throwable) {
                    Toast.makeText(this@EditProfileActivity, t.message, Toast.LENGTH_SHORT).show()
                }

            })
        }
    }
    fun showCustomDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_form_kalori, null)
        val hitung = dialogView.findViewById<Button>(R.id.btn_hitung)
        val ok = dialogView.findViewById<Button>(R.id.btn_ok)
        val cancel = dialogView.findViewById<ImageView>(R.id.btn_cancel)
        val kalori_harian = dialogView.findViewById<TextView>(R.id.et_kalori_harian)
        val spinner: Spinner = dialogView.findViewById(R.id.spinner_tujuan)
        val adapter = ArrayAdapter(this, R2.layout.simple_spinner_item, spinnerItems)
        adapter.setDropDownViewResource(R2.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long,
            ) {
                selecttujuan = parent.getItemAtPosition(position).toString()

            }


            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
        val spinner2: Spinner = dialogView.findViewById(R.id.spinner_intensitas)
        val adapter2 = ArrayAdapter(this, R2.layout.simple_spinner_item, spinnerItems2)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner2.adapter = adapter2

        spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long,
            ) {
                selectintensitas = parent.getItemAtPosition(position).toString()

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setView(dialogView)

        val alertDialog = alertDialogBuilder.create()
        alertDialog.window?.attributes?.windowAnimations = R.style.animation
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
        ok.setOnClickListener{
            val name: String = binding.etName.text.toString()
            val usia: String = binding.etUsia.text.toString()
            val bBadan: String = binding.etBeratBadan.text.toString()
            val tBadan: String = binding.etTinggiBadan.text.toString()
            val kalori: String = binding.etKaloriHarian.text.toString()
            val kelamin: String = selectkelamin.toString()


            uploadPorfileData(email!!, imageFile, name, usia, kelamin, bBadan, tBadan, kalori)


        }
        cancel.setOnClickListener{
            alertDialog.dismiss()
        }
        hitung.setOnClickListener {
//            hitungKalori(selecttujuan,selectintensitas)
            kalori_harian.setText(
                String.format(
                    "%.1f",
                    calculateDailyCalorieNeeds(selectkelamin, selectintensitas, selecttujuan)
                )
            )

            binding.etKaloriHarian.setText(
                String.format("%.1f", calculateDailyCalorieNeeds(selectkelamin, selectintensitas, selecttujuan))
            )
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
                    if (c.moveToFirst()) {
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