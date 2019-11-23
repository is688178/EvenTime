package com.example.eventime.activities.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventime.R
import com.parse.ParseFile
import com.parse.ParseObject

class AdapterRecyclerViewParseEvent (private val events: List<ParseObject>): RecyclerView.Adapter<NameViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NameViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event_parse, parent, false)
        return NameViewHolder(view)
    }

    override fun getItemCount(): Int = events.size //Change the number of items showed in

    override fun onBindViewHolder(holder: NameViewHolder, position: Int) {
        holder.bind(events[position])
    }
}

class NameViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    private val image: ImageView = view.findViewById(R.id.item_event_parse_iv_photo)
    private val title: TextView = view.findViewById(R.id.item_event_parse_tv_title)
    private val date: TextView = view.findViewById(R.id.item_event_parse_tv_date_hour)
    private val location: TextView = view.findViewById(R.id.item_event_tv_event_parse_location)

    fun bind(po: ParseObject) {
        val event = po.get("Event") as ParseObject
        val parseFile: ParseFile = event.get("image") as ParseFile
        Glide.with(view).load(parseFile.url).into(image)
        title.text = event["name"] as String
        date.text = po["date"].toString()
        location.text = event["locationName"] as String
    }
}