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
import com.example.eventime.activities.activities.ActivityEventDetails
import com.example.eventime.activities.adapters.AdapterRecyclerViewEvents
import com.example.eventime.activities.beans.Event
import com.example.eventime.activities.beans.EventDate
import com.example.eventime.activities.beans.Location
import com.example.eventime.activities.listeners.ClickListener
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.intentFor
import org.jetbrains.anko.support.v4.startActivity

class FragmentSugestedEvents : Fragment(), ClickListener {

    private lateinit var containerContext: Context
    private lateinit var rvEvents: RecyclerView

    private val events = ArrayList<Event>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sugested_events, container, false)

        this.containerContext = container!!.context

        bindViews(view)
        setupRecyclerViewEvents()

        return view
    }

    private fun bindViews(view: View) {
        rvEvents = view.find(R.id.fragment_sugested_events_rv_events)
    }

    private fun setupRecyclerViewEvents() {
        val hours = ArrayList<String>()
        hours.add("10:00 pm")
        val dates = ArrayList<EventDate>()
        dates.add(EventDate("12/12/2019", hours))
        val event = Event("Aerosmith concert", Location("Auditorio Telmex"), R.drawable.concert, "Es un concierto",
            dates, ArrayList(), "Musica")

        val event2 = Event("Exposición de arte", Location("Casa de la cultura"), R.drawable.concert, "Exposición de pinturas",
            dates, ArrayList(), "Cultural")

        val event3 = Event("Feria de la birria", Location("Centro"), R.drawable.concert, "Birria de la buena!",
            dates, ArrayList(), "Gastronomia")


        events.add(event)
        events.add(event)
        events.add(event)
        events.add(event)
        events.add(event2)
        events.add(event2)
        events.add(event2)
        events.add(event2)
        events.add(event2)
        events.add(event2)
        events.add(event3)
        events.add(event3)
        events.add(event3)



        var lastCategory = ""
        val itemTypes = ArrayList<Int>()

        events.forEach {
            if (it.category != lastCategory) {
                itemTypes.add(AdapterRecyclerViewEvents.CATEGORY_ITEM)
                lastCategory = it.category
            } else {
                itemTypes.add(AdapterRecyclerViewEvents.EVENT_ITEM)
            }
        }

        rvEvents.adapter = AdapterRecyclerViewEvents(events, this, true, itemTypes)
        rvEvents.layoutManager = LinearLayoutManager(containerContext)
    }

    override fun onClick(view: View, index: Int) {
        when (view.parent) {
            rvEvents -> {
                //startActivity<ActivityEventDetails>()

                startActivity(intentFor<ActivityEventDetails>("eventName" to events[index].name))
            }
        }
    }
}
