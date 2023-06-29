package com.tubes.fittrack.caloriesApi

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.TextView
import com.tubes.fittrack.R
import com.tubes.fittrack.ui.notifications.MakananActivity
import kotlinx.coroutines.withContext

class FoodItemAdapter(private val activities: List<FoodItem>) : BaseAdapter() {
    override fun getCount(): Int {
        return activities.size
    }

    override fun getItem(position: Int): Any {
        return activities[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(parent.context).inflate(R.layout.list_makanan_layout, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val activity = activities[position]
        val name=activity.name
        val calories=activity.calories.toString()
        val serving=activity.serving_size_g.toString()
//        val intent = Intent(viewHolder.context,MakananActivity::class.java)
//        intent.putExtra("food_name",name)
//        intent.putExtra("food_calories",calories)
//        intent.putExtra("food_serving",serving)
//        viewHolder.context.startActivity(intent)
        view.setOnClickListener {
            (viewHolder.context as MakananActivity).displayClickedItem(name,calories,serving)
        }

        viewHolder.nameTextView.text = activity.name
        viewHolder.calorie.text = activity.calories.toString()
        viewHolder.serving.text = activity.serving_size_g.toString()




        return view
    }

    private class ViewHolder(view: View) {
        val nameTextView: TextView = view.findViewById(R.id.tv_nama_makanan)
        val calorie: TextView=view.findViewById(R.id.tv_kalori)
        val serving : TextView=view.findViewById(R.id.tv_servingsize)
        val context: Context=view.context
    }
}
