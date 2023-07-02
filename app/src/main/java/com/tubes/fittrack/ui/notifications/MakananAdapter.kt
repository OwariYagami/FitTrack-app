package com.tubes.fittrack.ui.notifications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tubes.fittrack.R
import com.tubes.fittrack.api.Activity
import com.tubes.fittrack.api.Food

class MakananAdapter : RecyclerView.Adapter<MakananAdapter.ViewHolder>(){
    private val makananList: MutableList<Food> = mutableListOf()


    fun setData(makananList: List<Food>) {
        this.makananList.clear()
        this.makananList.addAll(makananList)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MakananAdapter.ViewHolder {
        val view =LayoutInflater.from(parent.context).inflate(R.layout.asupan_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: MakananAdapter.ViewHolder, position: Int) {
        val food = makananList[position]
        holder.bindFood(food)
    }

    override fun getItemCount(): Int {
        return makananList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.tv_asupan)
        private val takaranTextView: TextView = itemView.findViewById(R.id.tv_value)
//        private val kaloriTextView: TextView = itemView.findViewById(R.id.tv_kalori)

        fun bindFood(food: Food) {
            nameTextView.text = food.name
            takaranTextView.text = food.takaran
//            kaloriTextView.text = food.kalori
        }
    }
}