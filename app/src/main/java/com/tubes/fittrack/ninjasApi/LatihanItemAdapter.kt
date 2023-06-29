package com.tubes.fittrack.ninjasApi

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.tubes.fittrack.R
import com.tubes.fittrack.caloriesApi.FoodItemAdapter
import com.tubes.fittrack.ui.notifications.LatihanActivity
import com.tubes.fittrack.ui.notifications.MakananActivity

class LatihanItemAdapter(private val latihan: List<ActivityItem>) : BaseAdapter() {
    override fun getCount(): Int {
        return latihan.size
    }

    override fun getItem(position: Int): Any {
       return latihan[position]
    }

    override fun getItemId(position: Int): Long {
       return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val viewHolder: LatihanItemAdapter.ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(parent.context).inflate(R.layout.list_latihan_layout, parent, false)
            viewHolder = LatihanItemAdapter.ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as LatihanItemAdapter.ViewHolder
        }

        val activity = latihan[position]
        val nama_latihan=activity.name
        val durasi_latihan=activity.duration_minutes.toString()
        val kalori_latihan=activity.calories_per_hour.toString()

        view.setOnClickListener{
            (viewHolder.context as LatihanActivity).displayClickedItem(nama_latihan,durasi_latihan,kalori_latihan)
        }
        viewHolder.nama.text=nama_latihan
        viewHolder.durasi.text=durasi_latihan
        viewHolder.kaloribkr.text=kalori_latihan


        return view;
    }

    private class ViewHolder(view: View) {
        val nama: TextView = view.findViewById(R.id.tv_nama_latihan)
        val durasi: TextView =view.findViewById(R.id.tv_durasi)
        val kaloribkr : TextView =view.findViewById(R.id.tv_kaloribkr)
        val context: Context =view.context
    }
}