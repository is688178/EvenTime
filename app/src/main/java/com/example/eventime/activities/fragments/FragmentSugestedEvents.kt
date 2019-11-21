package com.example.eventime.activities.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventime.R
import com.example.eventime.activities.adapters.AdapterPublicEvent
import com.example.eventime.activities.listeners.ClickListener
import com.example.eventime.activities.utils.DateHourUtils
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import org.jetbrains.anko.find
import java.util.*

class FragmentSugestedEvents : Fragment(), ClickListener {

    private lateinit var containerContext: Context
    private lateinit var mRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sugested_events, container, false)

        this.containerContext = container!!.context
        mRecyclerView = view.find(R.id.fragment_sugested_events_rv_events)


        val query = ParseQuery.getQuery<ParseObject>("EventDate")
        query.include("Event")
        // Events endDate are saved 1 second later than its startDate, I need this order for index use
        query.orderByDescending("createdAt")
        // Only recommend events that had not happen
        query.whereGreaterThanOrEqualTo("date", Calendar.getInstance().time)
        query.findInBackground { objects, _ ->

            // Removing event not created by the user
            val userId = ParseUser.getCurrentUser().objectId
            val eventsToRemove = arrayListOf<ParseObject>()
            val intervals = arrayListOf<TimeInterval>()

            // First get TimeIntervals of privateEvents of the User
            for ((index, o) in objects.withIndex()) {
                val event = o["Event"] as ParseObject
                val eventUser = event["Person"] as ParseUser
                val eventIsPrivate = event["private"] as Boolean
                if (eventUser.objectId == userId && eventIsPrivate) {
                    val timeInterval = TimeInterval(
                        objects[index]["date"] as Date,
                        objects[(index + 1)]["date"] as Date
                    )
                    intervals.add(timeInterval)
                    eventsToRemove.add(o)
                } else if (eventIsPrivate)
                    eventsToRemove.add(o)
            }

            // Remove privateEvents of all users
            for (o in eventsToRemove)
                objects.remove(o)

            // Get conflicts with the User private Events and public Events StartDate
            for (o in objects) {
                val startPublicEventDate = o["date"] as Date
                for (interval in intervals) {
                    if (interval.conflict(startPublicEventDate)) {
                        eventsToRemove.add(o)
                        break
                    }
                }
            }

            for (o in eventsToRemove)
                objects.remove(o)

            mRecyclerView.adapter = AdapterPublicEvent(objects)
            mRecyclerView.layoutManager = LinearLayoutManager(view.context)
        }

        return view
    }

    override fun onClick(view: View, index: Int) {
        when (view.parent) {
            mRecyclerView -> {
                TODO("Interact with the now showed and real Event")
            }
        }
    }
}

data class TimeInterval(val startDate: Date, val endDate: Date) {
    fun conflict(startPublicEventDate: Date): Boolean {
        return startPublicEventDate > startDate && startPublicEventDate < endDate
    }
}
