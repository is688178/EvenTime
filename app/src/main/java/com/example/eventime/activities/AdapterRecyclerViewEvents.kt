package com.example.eventime.activities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eventime.R
import org.jetbrains.anko.find

class AdapterRecyclerViewEvents(private val events: ArrayList<String>): RecyclerView.Adapter<EventViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)

        return EventViewHolder(view)
    }

    override fun getItemCount(): Int = events.size

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(events[position])
    }
}

class EventViewHolder(view: View): RecyclerView.ViewHolder(view) {
    private var eventTitle: TextView = view.find(R.id.item_event_tv_event_title)

    fun bind(event: String) {
        eventTitle.text = event
        //-----
    }
}