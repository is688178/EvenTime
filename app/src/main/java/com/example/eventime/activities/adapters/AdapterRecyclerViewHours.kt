package com.example.eventime.activities.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eventime.R
import com.example.eventime.activities.utils.DateHourUtils
import org.jetbrains.anko.find
import java.util.*
import kotlin.collections.ArrayList

class AdapterRecyclerViewHours(private val hours: ArrayList<Calendar>) : RecyclerView.Adapter<HoursViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoursViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_hour, parent, false)

        return HoursViewHolder(view)
    }

    override fun getItemCount(): Int = hours.size

    override fun onBindViewHolder(holder: HoursViewHolder, position: Int) {
        holder.bind(hours[position])
    }
}

class HoursViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val tvHour: TextView = view.find(R.id.item_hour_tv_hour)

    fun bind(time: Calendar) {
        tvHour.text = DateHourUtils.formatHourToShowFormat(time)
    }
}