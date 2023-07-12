package com.tubes.fittrack.ui.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import com.tubes.fittrack.R

class HelpAdapter (private val context: Context,private val data: Map<String, List<String>>):BaseExpandableListAdapter()
{
    override fun getGroupCount(): Int {
        return data.keys.size
    }

    override fun getChildrenCount(p0: Int): Int {
        val groupKey = getGroup(p0) as String
        return data[groupKey]?.size ?: 0
    }

    override fun getGroup(p0: Int): Any {
        return data.keys.toList()[p0]
    }

    override fun getChild(p0: Int, p1: Int): Any {
        val groupKey = getGroup(p0) as String
        return data[groupKey]?.get(p1) ?: ""
    }

    override fun getGroupId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getChildId(p0: Int, p1: Int): Long {
        return p1.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getGroupView(p0: Int, p1: Boolean, p2: View?, p3: ViewGroup?): View {
        val headerTitle = getGroup(p0) as String
        val view: View = p2 ?: LayoutInflater.from(context).inflate(
            R.layout.list_title, p3,false
        )
        val headerTextView: TextView = view.findViewById(R.id.headerTitle)
        headerTextView.text = headerTitle
        return view
    }

    override fun getChildView(p0: Int, p1: Int, p2: Boolean, p3: View?, p4: ViewGroup?): View {
        val childTitle = getChild(p0, p1) as String
        val view: View = p3 ?: LayoutInflater.from(context).inflate(
            R.layout.list_detail,
            p4,
            false
        )
        val childTextView: TextView = view.findViewById(R.id.childTitle)
        childTextView.text = childTitle
        return view
    }

    override fun isChildSelectable(p0: Int, p1: Int): Boolean {
       return true
    }
}