package com.example.eventime.activities.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import com.example.eventime.R
import com.example.eventime.activities.beans.Category
import com.example.eventime.activities.beans.Event
import com.example.eventime.activities.listeners.ClickListener
import com.example.eventime.activities.utils.DateHourUtils
import com.google.android.material.appbar.CollapsingToolbarLayout
import org.jetbrains.anko.find
import java.util.*
import kotlin.collections.ArrayList

class AdapterRecyclerViewEvents(private var events: ArrayList<Event>, private val clickListener: ClickListener,
                                private val suggested: Boolean) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var eventsCopy = ArrayList(events)
    private var filteredEvents = ArrayList<Event>()
    private var lastDateFilterTypeApplied = TODAY_FILTER
    private lateinit var lastCategoryFilterApplied: Category

    companion object {
        const val EVENT_ITEM = 0
        const val EVENT_CATEGORY_ITEM = 1

        const val NO_FILTER = 0
        const val CATEGORY_FILTER = 1
        const val DATE_FILTER = 2

        const val TODAY_FILTER = 3
        const val THIS_WEEK_FILTER = 4
        const val NEXT_WEEK_FILTER = 5
    }

    private var filterApplied = NO_FILTER

    override fun getItemViewType(position: Int): Int {
        return if (suggested && events[position].firstOfCategory!!) {
            EVENT_CATEGORY_ITEM
        } else {
            EVENT_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (suggested && viewType == EVENT_CATEGORY_ITEM) {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.item_event_with_category_title,
                parent, false
            )
            EventCategoryViewHolder(view, clickListener)
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
        } else if (holder is EventCategoryViewHolder) {
            holder.bind(events[position])
        }
    }

    fun filterEventsCategory(category: Category): Boolean {
        events.clear()

        lastCategoryFilterApplied = category
        if (category.name == "Todos") {
            eventsCopy.forEach {event ->
                events.add(event)
            }
            //notifyDataSetChanged()
            filterApplied = NO_FILTER
            filterEventsDate(lastDateFilterTypeApplied)
            return true
        }

        for (event in filteredEvents) {
            if (event.category!!.name == category.name) {
                events.add(event)
            }
        }
        filterApplied = CATEGORY_FILTER

        notifyDataSetChanged()

        return events.isNotEmpty()
    }

    fun filterEventsDate(dateFilterType: Int): Boolean {
        events.clear()

        lastDateFilterTypeApplied = dateFilterType
        val initDate = Calendar.getInstance()
        val finDate = Calendar.getInstance()
        when (dateFilterType) {
            TODAY_FILTER -> {}
            THIS_WEEK_FILTER -> {
                finDate.add(Calendar.DAY_OF_MONTH, 7 - finDate.get(Calendar.DAY_OF_WEEK))
            }
            NEXT_WEEK_FILTER -> {
                finDate.add(Calendar.DAY_OF_MONTH, 14 - finDate.get(Calendar.DAY_OF_WEEK))
                initDate.add(Calendar.DAY_OF_MONTH, 8 - initDate.get(Calendar.DAY_OF_WEEK))
            }
        }

        for (event in eventsCopy) {
            val eventDate = event.dates[0].date
            if (DateHourUtils.dateInRange(eventDate, initDate, finDate)) {
                events.add(event)
            }
        }
        filteredEvents = ArrayList(events)
        if (filterApplied != NO_FILTER) {
            filterEventsCategory(lastCategoryFilterApplied)
        }

        notifyDataSetChanged()

        return events.isNotEmpty()
    }
}

class EventCategoryViewHolder(view: View, clickListener: ClickListener) :
    EventViewHolder(view, clickListener) {
    private var tvCategoryName: TextView =
        view.find(R.id.item_event_with_category_title_tv_category)

    override fun bind(event: Event) {
        tvCategoryName.text = event.category!!.name

        super.bind(event)
    }
}

open class EventViewHolder(private val view: View, private val clickListener: ClickListener) :
    RecyclerView.ViewHolder(view),
    View.OnClickListener {
    private var flContent: FrameLayout = view.find(R.id.item_event_fl_content)
    private var eventTitle: TextView = view.find(R.id.item_event_tv_event_title)
    private var eventLocationName: TextView = view.find(R.id.item_event_tv_event_location_name)
    private var eventDateHour: TextView = view.find(R.id.item_event_tv_event_date_hour)

    init {
        view.setOnClickListener(this)
    }

    open fun bind(event: Event) {
        Glide
            .with(view.context)
            .load(event.parseFileImage?.url)
            .into(object : CustomViewTarget<FrameLayout, Drawable>(flContent) {
                override fun onLoadFailed(errorDrawable: Drawable?) {}
                override fun onResourceCleared(placeholder: Drawable?) {}
                override fun onResourceReady(resource: Drawable,transition: Transition<in Drawable>?) {
                    flContent.background = resource
                }
            })

        eventTitle.text = event.name
        eventLocationName.text = event.location!!.name
        val eventDate = event.dates[0].date
        eventDateHour.text = "${DateHourUtils.formatDateToMonthNameShowFormat(eventDate)} " +
                "${DateHourUtils.formatHourToShowFormat(eventDate)}"
    }

    override fun onClick(view: View?) {
        clickListener.onClick(view!!, adapterPosition)
    }
}