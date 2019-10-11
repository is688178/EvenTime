package com.example.eventime.activities


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventime.R
import org.jetbrains.anko.find

class FragmentEventsAux : Fragment() {

    private lateinit var rvEvents: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_events_aux, container, false)

        bindViews(view)
        setupEventsRecyclerView()

        return view
    }

    private fun bindViews(view: View) {
        rvEvents = view.find(R.id.fragment_events_aux_rv_events)
    }

    private fun setupEventsRecyclerView() {
        //FETCH EVENTS

        val events = ArrayList<String>()
        events.add("Aerosmith concert")
        events.add("Chivas vs Puebla")
        events.add("Vicente Fernández")
        events.add("Feria de la birria")
        events.add("Alien covenant")

        rvEvents.adapter = AdapterRecyclerViewEvents(events)
        rvEvents.layoutManager = LinearLayoutManager(activity)
    }
}
