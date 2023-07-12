package com.tubes.fittrack.ui.profile

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.tubes.fittrack.R
import com.tubes.fittrack.api.ResponseUserProfile
import com.tubes.fittrack.api.RetrofitClient
import com.tubes.fittrack.auth.LoginActivity
import com.tubes.fittrack.auth.RegisterActivity
import com.tubes.fittrack.databinding.ActivityLoginBinding
import com.tubes.fittrack.databinding.FragmentProfileBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,

    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnHelp.setOnClickListener {
            val intent = Intent(activity, HelpActivity::class.java)
            startActivity(intent)
        }
        binding.btnEdit.setOnClickListener {
            val intent = Intent(activity, EditProfileActivity::class.java)
            startActivity(intent)
        }

        val sharedPreferences = context?.getSharedPreferences("userPref", Context.MODE_PRIVATE)

        val email: String? = sharedPreferences?.getString("email","")
        userProfil(email!!, binding)
        binding.btnLogout.setOnClickListener {
            logoutAndNavigateToLogin()
        }
        return root;
    }

    private fun userProfil(email: String, binding: FragmentProfileBinding){
        val pDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.titleText = "Loading Profil"
        pDialog.setCancelable(false)
        pDialog.show()
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
                            val imageUrl: String = RetrofitClient.IMAGE_URL + image
                            Glide.with(requireContext())
                                .load(imageUrl)
                                .apply(RequestOptions().centerCrop())
                                .into(binding.ivProfile)
                        }

                        binding.tvName.setText(name)


                        if (usia != null){
                            binding.tvAge.setText("$usia Tahun")
                        }

                        if (b_badan != null){
                            binding.tvWeight.setText("$b_badan Kg")
                        }

                        if (t_badan != null){
                            binding.tvHeight.setText("$t_badan Cm")
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

    private fun logoutAndNavigateToLogin() {
        // Lakukan tindakan logout di sini, seperti membersihkan data login
        SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
            .setTitleText("Are you sure?")
            .setContentText("Apakah anda yakin ingin Logout?")
            .setConfirmText("Yes,do it!!")
            .setConfirmClickListener { sDialog ->
                sDialog.dismissWithAnimation()
                val sharedPreferences = context?.getSharedPreferences("userPref", Context.MODE_PRIVATE)
                val editor = sharedPreferences?.edit()
                editor?.putBoolean("isLoggedIn", false)
                editor?.putString("email","")
                editor?.apply()

                // Pindah ke halaman login
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                activity?.finish() // Hapus aktivitas ini dari stack agar pengguna tidak dapat kembali ke MainActivity dengan tombol back


            }
            .setCancelButton(
                "Cancel"
            ) { sDialog -> sDialog.dismissWithAnimation() }
            .show()
        // Contoh: Membersihkan status login di SharedPreferences
       }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


}