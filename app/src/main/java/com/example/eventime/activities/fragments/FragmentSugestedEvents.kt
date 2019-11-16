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
import com.example.eventime.activities.adapters.AdapterPublicEvent
import com.example.eventime.activities.adapters.AdapterRecyclerViewEvents
import com.example.eventime.activities.beans.Event
import com.example.eventime.activities.beans.EventDate
import com.example.eventime.activities.beans.Location
import com.example.eventime.activities.listeners.ClickListener
import com.parse.ParseObject
import com.parse.ParseQuery
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.intentFor
import org.jetbrains.anko.support.v4.startActivity

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

        val query = ParseQuery.getQuery<ParseObject>("Event")

        TODO("include in query eventDate  from  another class, App is cycling in requests right now...")



        query.findInBackground { objects, _ ->
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
