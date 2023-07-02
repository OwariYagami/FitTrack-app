package com.tubes.fittrack.ui.notifications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tubes.fittrack.R
import com.tubes.fittrack.api.Activity
import com.tubes.fittrack.api.Food

class AktivitasAdapter :RecyclerView.Adapter<AktivitasAdapter.ViewHolder>(){
    private val activityList: MutableList<Activity> = mutableListOf()

    fun setData(activityList: List<Activity>) {
        this.activityList.clear()
        this.activityList.addAll(activityList)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AktivitasAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.latihan_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: AktivitasAdapter.ViewHolder, position: Int) {
        val activity = activityList[position]
        holder.bindActivity(activity)
    }

    override fun getItemCount(): Int {
      return activityList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.tv_latihan)
        private val durasi: TextView = itemView.findViewById(R.id.tv_rep)


        fun bindActivity(aktivitas: Activity) {
            nameTextView.text = aktivitas.name
            durasi.text = aktivitas.durasi.toString()

        }
    }

}