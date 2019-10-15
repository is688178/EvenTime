package com.example.eventime.activities.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eventime.R
import com.example.eventime.activities.beans.Event
import com.example.eventime.activities.listeners.ClickListener
import org.jetbrains.anko.find

class AdapterRecyclerViewEvents(private val events: ArrayList<Event>, private val clickListener: ClickListener): RecyclerView.Adapter<EventViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)

        return EventViewHolder(view, clickListener)
    }

    override fun getItemCount(): Int = events.size

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(events[position])
    }

    fun filterEventsCategory(category: String) {

    }

    //FILTER EVENTS BY DATE
}

class EventViewHolder(view: View, private val clickListener: ClickListener): RecyclerView.ViewHolder(view),
        View.OnClickListener {
    private var eventTitle: TextView = view.find(R.id.item_event_tv_event_title)
    private var eventLocationName: TextView = view.find(R.id.item_event_tv_event_location_name)
    private var eventDateHour: TextView = view.find(R.id.item_event_tv_event_date_hour)

    init {
        view.setOnClickListener(this)
    }

    fun bind(event: Event) {
        eventTitle.text = event.name
        eventLocationName.text = event.location.name
//        eventDateHour.text = "${event.dates[0].date} ${event.dates[0].hours[0]}"
    }

    override fun onClick(view: View?) {
        clickListener.onClick(view!!, adapterPosition)
    }
}