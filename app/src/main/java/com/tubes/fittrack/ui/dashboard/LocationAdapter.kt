package com.tubes.fittrack.ui.dashboard

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.tubes.fittrack.MapsActivity
import com.tubes.fittrack.R
import com.tubes.fittrack.api.DataLocation
import com.tubes.fittrack.api.RetrofitClient

class LocationAdapter(private val dataLocation: ArrayList<DataLocation>): RecyclerView.Adapter<LocationAdapter.ViewHolder>(){
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var nameLocation: TextView = itemView.findViewById(R.id.tv_latihan)
        var gambar: ImageView = itemView.findViewById(R.id.gambar)
        var lihat: Button = itemView.findViewById(R.id.btn_lihat)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_tempat, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataLocation.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val location = dataLocation[position]
        val imageUrl: String = RetrofitClient.IMAGE_URL + location.foto
        holder.nameLocation.text = location.name
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .apply(RequestOptions().fitCenter())
            .into(holder.gambar)
        holder.lihat.setOnClickListener {
            val intent = Intent(holder.itemView.context, MapsActivity::class.java)
            intent.putExtra("longitude", location.longitude)
            intent.putExtra("latitude", location.latitude)
            intent.putExtra("name", location.name)
            holder.itemView.context.startActivity(intent)
        }
    }
}

