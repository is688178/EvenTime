package com.example.eventime.activities.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eventime.R
import com.example.eventime.activities.beans.Event
import com.example.eventime.activities.listeners.ClickListener
import org.jetbrains.anko.find
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList

class AdapterRecyclerViewEvents(private var events: ArrayList<Event>, private val clickListener: ClickListener,
                                private val sugested: Boolean, private val itemTypes: ArrayList<Int>?) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var eventsCopy = ArrayList(events)
    private var filteredEvents = ArrayList<Event>()

    companion object {
        const val EVENT_ITEM = 0
        const val CATEGORY_ITEM = 1

        const val NO_FILTER = 0
        const val CATERORY_FILTER = 1
        const val DATE_FILTER = 2

        const val TODAY_FILTER = 3
        const val THIS_WEEK_FILTER = 4
        const val NEXT_WEEK_FILTER = 5
    }


    //IF THERE WILL BE MORE FILTERS, USE A COLLECTION
    private var filterApplied = NO_FILTER

    override fun getItemViewType(position: Int): Int {
        return if (sugested) {
            itemTypes!![position]
        } else {
            EVENT_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (sugested && viewType == CATEGORY_ITEM) {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.item_sugested_event_category_title,
                parent, false
            )
            SugestedEventCategoryViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.item_event, parent,
                false
            )
            EventViewHolder(view, clickListener)
        }


    }

    override fun getItemCount(): Int = events.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is EventViewHolder) {
            holder.bind(events[position])
        } else if (holder is SugestedEventCategoryViewHolder) {
            holder.bind(events[position])
        }
    }

    fun filterEventsCategory(category: String): Boolean {
        events.clear()

        if (category == "Todos") {
            events = ArrayList(eventsCopy)
            notifyDataSetChanged()
            filterApplied = NO_FILTER
            return true
        }

        if (filterApplied != NO_FILTER && filterApplied != CATERORY_FILTER) {
            for (event in filteredEvents) {
                if (event.category == category) {
                    events.add(event)
                }
            }
            filterApplied = CATERORY_FILTER
        } else {
            for (event in eventsCopy) {
                if (event.category == category) {
                    events.add(event)
                }
            }
            filteredEvents = ArrayList(events)
        }

        notifyDataSetChanged()

        return events.isNotEmpty()
    }

    //FILTER EVENTS BY DATE
    @SuppressLint("NewApi")
    fun filterEventsDate(dateFilterType: Int): Boolean {
        events.clear()
        val initDate = when (dateFilterType) {
            NEXT_WEEK_FILTER -> {
                LocalDate.now().plusDays(8 - LocalDate.now().dayOfWeek.value.toLong())
            } else -> {
                LocalDate.now()
            }
        }
        val finDate = when (dateFilterType) {
            TODAY_FILTER -> {
                LocalDate.now()
            } THIS_WEEK_FILTER -> {
                LocalDate.now().plusDays(7 - LocalDate.now().dayOfWeek.value.toLong())
            } NEXT_WEEK_FILTER -> {
                LocalDate.now().plusDays(14 - LocalDate.now().dayOfWeek.value.toLong())
            } else -> {
                LocalDate.now()
            }
        }


        if (filterApplied != NO_FILTER && filterApplied != DATE_FILTER) {
            for (event in filteredEvents) {
                val eventDate = LocalDate.parse(event.dates[0].date, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                if (eventDate in initDate..finDate) {
                    events.add(event)
                }
            }
            filterApplied = DATE_FILTER
        } else {
            for (event in eventsCopy) {
                val eventDate = LocalDate.parse(event.dates[0].date, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                if (eventDate in initDate..finDate) {
                    events.add(event)
                }
            }
            filteredEvents = ArrayList(events)
        }

        /*
        if (filterApplied != NO_FILTER && filterApplied != DATE_FILTER) {
            filteredEvents.forEach { event->
                var flag = false
                event.dates.forEach {
                    val eventDate = LocalDate.parse(it.date, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    if (eventDate in initDate..finDate) {
                        flag = true
                    }
                }

                if (flag) events.add(event)
            }
            filterApplied = DATE_FILTER
        } else {
            eventsCopy.forEach { event->
                var flag = false
                event.dates.forEach {
                    val eventDate = LocalDate.parse(it.date, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    if (eventDate in initDate..finDate) {
                        flag = true
                    }
                }

                if (flag) events.add(event)
            }
            filteredEvents = ArrayList(events)
        }
        * */

        notifyDataSetChanged()

        return events.isNotEmpty()
    }
}

class SugestedEventCategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private var tvCategoryName: TextView = view.find(R.id.item_sugested_event_category_tv_category)

    fun bind(event: Event) {
        tvCategoryName.text = event.category
    }
}

class EventViewHolder(private val view: View, private val clickListener: ClickListener) :
    RecyclerView.ViewHolder(view),
    View.OnClickListener {
    private var flContent: FrameLayout = view.find(R.id.item_event_fl_content)
    private var eventTitle: TextView = view.find(R.id.item_event_tv_event_title)
    private var eventLocationName: TextView = view.find(R.id.item_event_tv_event_location_name)
    private var eventDateHour: TextView = view.find(R.id.item_event_tv_event_date_hour)

    init {
        view.setOnClickListener(this)
    }

    @SuppressLint("NewApi")
    fun bind(event: Event) {
        flContent.background = view.context.getDrawable(event.image)
        eventTitle.text = event.name
        eventLocationName.text = event.location.name
        val eventDate = LocalDate.parse(event.dates[0].date, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        eventDateHour.text = "${eventDate.dayOfMonth} ${eventDate.month} ${event.dates[0].hours[0]}"
    }

    override fun onClick(view: View?) {
        clickListener.onClick(view!!, adapterPosition)
    }
}