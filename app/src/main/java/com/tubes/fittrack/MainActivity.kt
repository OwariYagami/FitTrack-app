package com.tubes.fittrack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.tubes.fittrack.auth.LoginActivity
import com.tubes.fittrack.databinding.ActivityMainBinding
import com.tubes.fittrack.ui.home.HomeFragment
import com.tubes.fittrack.ui.profile.ProfileFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var backPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_dashboard,
                R.id.navigation_notifications,
                R.id.navigation_profile
            )
        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        if (intent.hasExtra("FROM_RIWAYAT_ACTIVITY")) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, HomeFragment())
                .commit()
        }else if (intent.hasExtra("FROM_EDIT_PROFILE_ACTIVITY")) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ProfileFragment())
                .commit()
        }
    }

    override fun onBackPressed() {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val currentFragment = navController.currentDestination

        // Cek apakah fragment saat ini adalah fragment Home
        if (currentFragment?.id == R.id.navigation_home) {

            // Jika fragment saat ini adalah fragment Home, panggil metode logout dan pindah ke halaman login
            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("Mau Keluar Mas?")
                .setConfirmText("Yes,do it!!")
                .setConfirmClickListener { sDialog ->
                    sDialog.dismissWithAnimation()
                    finish()
                }
                .setCancelButton(
                    "Cancel"
                ) { sDialog -> sDialog.dismissWithAnimation() }
                .show()

        } else {
            // Jika fragment saat ini bukanlah fragment Home, biarkan sistem meng-handle tombol kembali
            super.onBackPressed()
        }
    }


//    override fun onBackPressed() {
//        super.onBackPressed()
//        // Menghapus history aktivitas LoginActivity sehingga pengguna tidak dapat kembali ke halaman login dengan menekan tombol back
//        val intent = Intent(this, LoginActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
//        startActivity(intent)
////        finish()
//    }

//    override fun onDestroy() {
//        super.onDestroy()
//        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
//        val editor = sharedPreferences.edit()
//        editor.putBoolean("isLoggedIn", false)
//        editor.apply()
////        Toast.makeText(this@MainActivity, "onDestroyMain", Toast.LENGTH_SHORT).show()
//    }
//
//    override fun onStop() {
//        super.onStop()
//        val sharedPreferences = getSharedPreferences("userPref", Context.MODE_PRIVATE)
//        val editor = sharedPreferences.edit()
//        editor.putBoolean("isLoggedIn", false)
//        editor.apply()
////        Toast.makeText(this@MainActivity, "onStopSplash", Toast.LENGTH_SHORT).show()
//    }
}