package com.tubes.fittrack.ui.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ExpandableListView
import com.tubes.fittrack.R
import com.tubes.fittrack.databinding.ActivityHelpBinding

class HelpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHelpBinding
    private lateinit var adapter: HelpAdapter
    private lateinit var expandableListView: ExpandableListView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        expandableListView = binding.expandableListView
        // Buat data pertanyaan dan jawaban
        val data: Map<String, List<String>> = getData()

        // Buat adapter dan set pada ExpandableListView
        adapter = HelpAdapter(this, data)
        expandableListView.setAdapter(adapter)




    }
    // Metode untuk membuat data pertanyaan dan jawaban
    private fun getData(): Map<String, List<String>> {
        val faqData: MutableMap<String, List<String>> = HashMap()

        // Tambahkan pertanyaan dan jawaban ke dalam map
        val pertanyaan1 = "Saya tidak tahu apa itu kalori?"
        val jawaban1 = "Kalori merupakan nilai yang menunjukkan jumlah energi yang diperoleh dari makanan dan minuman."
        val pertanyaan2 = "Bagaimana saya mengetahui jumlah kalori harian saya?"
        val jawaban2 = "Anda bisa menghitung kalori harian, pada menu profile, kemudian edit profile, maka anda bisa mengisi tujuan dan intensitas aktivitas sebagai kalkulasi perhitungan"
        val pertanyaan3 = "Bagaimana saya menambahkan makanan yang saya konsumsi?"
        val jawaban3 = "Anda bisa menambahkan makanan dan aktivitas yang anda konsumsi dan lakukan pada menu aktivitas"
        val pertanyaan4 = "Apa itu Indeks Massa Tubuh?"
        val jawaban4 = "Indeks massa tubuh adalah ukuran yang digunakan untuk mengetahui status gizi seseorang yang didapatkan dari perbandingan berat dan tinggi badan."
        val pertanyaan5 = "Dimana saya bisa mengecek progress yang sudah berlalu?"
        val jawaban5 = "Anda bisa melihat riwayat progress pada menu Beranda, kemudian tekan Lihat, maka anda bisa melihat riwayat berdasarkan tanggal "
        faqData[pertanyaan1] = listOf(jawaban1)
        faqData[pertanyaan2] = listOf(jawaban2)
        faqData[pertanyaan3] = listOf(jawaban3)
        faqData[pertanyaan4] = listOf(jawaban4)
        faqData[pertanyaan5] = listOf(jawaban5)

        return faqData
    }
}