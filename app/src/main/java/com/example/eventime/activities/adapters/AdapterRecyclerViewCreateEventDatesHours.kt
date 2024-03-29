package com.example.eventime.activities.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eventime.R
import com.example.eventime.activities.beans.EventDate
import com.example.eventime.activities.listeners.ClickListener
import com.example.eventime.activities.utils.DateHourUtils
import org.jetbrains.anko.find

class AdapterRecyclerViewCreateEventDatesHours(private val dates: ArrayList<EventDate>,
                                               private val clickListener: ClickListener):
        RecyclerView.Adapter<DateHourViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateHourViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_date_hours_create_public_event,
            parent, false)

        return DateHourViewHolder(view, clickListener)
    }

    override fun getItemCount(): Int = dates.size

    override fun onBindViewHolder(holder: DateHourViewHolder, position: Int) {
        holder.bind(dates[position])
    }
}

class DateHourViewHolder(view: View, private val clickListener: ClickListener): RecyclerView.ViewHolder(view) ,
        View.OnClickListener {
    private val tvDate: TextView = view.find(R.id.item_date_hours_create_public_event_tv_date)
    private val tvHours: TextView = view.find(R.id.item_date_hours_create_public_event_tv_hours)

    init {
        view.setOnClickListener(this)
    }

    fun bind(date: EventDate) {
        tvDate.text = DateHourUtils.formatDateToShowFormat(date.date)
        tvHours.text = DateHourUtils.joinHoursToString(date.hours!!, true)
    }

    override fun onClick(view: View?) {
        clickListener.onClick(view!!, adapterPosition)
    }
}