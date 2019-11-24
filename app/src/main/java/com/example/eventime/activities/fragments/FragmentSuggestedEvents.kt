package com.example.eventime.activities.fragments

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventime.R
import com.example.eventime.activities.adapters.AdapterRecyclerViewEvents
import com.example.eventime.activities.adapters.AdapterRecyclerViewParseEvent
import com.example.eventime.activities.beans.*
import com.example.eventime.activities.listeners.ClickListener
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.parse.ParseGeoPoint
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import org.jetbrains.anko.collections.forEachByIndex
import org.jetbrains.anko.find
import java.util.*

class FragmentSuggestedEvents : Fragment(), ClickListener {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var adapterRvEvents: AdapterRecyclerViewEvents
    private lateinit var containerContext: Context
    private var events = ArrayList<Event>()

    //Location
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private val PERMISSION_FINE_LOCATION = 101
    private val KILOMETERS_LIMIT = 10
    private var userGeoPointLocation: ParseGeoPoint? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sugested_events, container, false)

        containerContext = container!!.context
        mRecyclerView = view.find(R.id.fragment_sugested_events_rv_events)

        //Location
        getLocation()
        fetchSuggestedEvents()

        return view
    }

    override fun onClick(view: View, index: Int) {
        when (view.parent) {
            mRecyclerView -> {
                TODO("Interact with the now showed and real Event")
            }
        }
    }

    private fun getLocation() {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(containerContext)

        if (ActivityCompat.checkSelfPermission(
                containerContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient!!.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    userGeoPointLocation = ParseGeoPoint(location.latitude, location.longitude)
                }
            }
        } else {
            //Request Permissions
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_FINE_LOCATION)
        }
    }

    private fun fetchSuggestedEvents() {
        //FETCH EVENT DATES
        val cal = Calendar.getInstance()
        val currentUserId = ParseUser.getCurrentUser().objectId
        val intervals = arrayListOf<TimeInterval>()

        val queryPrivateDate = ParseQuery.getQuery<ParseObject>("EventDate")
        queryPrivateDate.include("Event")
        queryPrivateDate.include("Event.Person")
        queryPrivateDate.include("Event.Category")
        queryPrivateDate.orderByDescending("createdAt")
        queryPrivateDate.whereGreaterThanOrEqualTo("date", cal.time)
        queryPrivateDate.findInBackground { dates, e ->
            if (e == null) {
                dates.forEachIndexed { index, date ->
                    val event = date.getParseObject("Event")
                    if (event != null) {
                        if (event["private"] == true) {
                            // Event is private - only get time intervals
                            val l = event.getParseGeoPoint("location")
                            val location = if (l != null) {
                                Location(
                                    event["locationName"].toString(),
                                    l.latitude, l.longitude
                                )
                            } else {
                                null
                            }

                            val p = event.getParseUser("Person")
                            val person = if (p != null) {
                                Person(
                                    p.objectId,
                                    p["username"].toString(),
                                    "",
                                    null,
                                    p.getParseFile("image")
                                )
                            } else {
                                null
                            }


                            if (person != null)
                                if (person.personId == currentUserId) {
                                    val timeInterval = TimeInterval(
                                        dates[index]["date"] as Date,
                                        dates[(index + 1)]["date"] as Date
                                    )
                                    intervals.add(timeInterval)
                                }
                        }
                    } else {
                        Log.e("EVENTS FETCH", "Error: " + e?.message)
                    }
                }
            } else {
                Log.e("EVENTS FETCH", "Error: " + e.message!!)
            }
        }


        val queryDate = ParseQuery.getQuery<ParseObject>("EventDate")
        queryDate.include("Event")
        queryDate.include("Event.Person")
        queryDate.include("Event.Category")
        queryDate.orderByDescending("createdAt")
        queryDate.whereGreaterThanOrEqualTo("date", cal.time)
        queryDate.findInBackground { dates, e ->
            if (e == null) {
                val eventsO = ArrayList<Event>()
                dates.forEach { date ->
                    val event = date.getParseObject("Event")
                    if (event != null) {
                        if (event["private"] == false) {
                            val l = event.getParseGeoPoint("location")
                            val location = if (l != null) {
                                Location(
                                    event["locationName"].toString(),
                                    l.latitude, l.longitude
                                )
                            } else {
                                null
                            }

                            val imageParseFile = event.getParseFile("image")

                            val p = event.getParseUser("Person")
                            val person = if (p != null) {
                                Person(
                                    p.objectId,
                                    p["username"].toString(),
                                    "",
                                    null,
                                    p.getParseFile("image")
                                )
                            } else {
                                null
                            }
                            val c = event.getParseObject("Category")
                            val category = if (c != null) {
                                Category(
                                    c.objectId,
                                    c["name"].toString(),
                                    false,
                                    c.getParseFile("icon")
                                )
                            } else {
                                null
                            }

                            val dateO = date.getDate("date")
                            val calx = Calendar.getInstance()
                            calx.time = dateO!!
                            val datesO = ArrayList<EventDate>()
                            val eventDate = EventDate(
                                date.objectId,
                                calx,
                                false,
                                ArrayList()
                            )
                            eventDate.hours!!.add(cal)
                            datesO.add(eventDate)

                            val eventO = Event(
                                event.objectId,
                                event["name"].toString(),
                                location,
                                null,
                                event["description"].toString(),
                                datesO,
                                Calendar.getInstance(),
                                category,
                                false,
                                person,
                                false,
                                null,
                                Calendar.getInstance(),
                                imageParseFile,
                                event
                            )

                            var conflict = false
                            for (interval in intervals)
                                if (interval.conflict(dateO))
                                    conflict = true

                            if (!conflict && userGeoPointLocation != null && l != null)
                                if (userGeoPointLocation!!.distanceInKilometersTo(l) <= KILOMETERS_LIMIT)
                                    eventsO.add(eventO)

                        }
                    } else {
                        Log.e("EVENTS FETCH", "Error: " + e?.message)
                    }
                }

                this.events = eventsO
                setupEventsRecyclerView()
            } else {
                Log.e("EVENTS FETCH", "Error: " + e.message!!)
            }
        }


    }

    private fun setupEventsRecyclerView() {
        adapterRvEvents = AdapterRecyclerViewEvents(events, this, false)
        mRecyclerView.adapter = adapterRvEvents
        mRecyclerView.layoutManager = LinearLayoutManager(containerContext)
    }

    private fun setRecyclerView(view: View) {
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
                    } else if (userGeoPointLocation != null) {
                        //Also add to exclusion events too far
                        val event = o["Event"] as ParseObject
                        val eventLocation = event["location"] as ParseGeoPoint
                        if (userGeoPointLocation!!.distanceInKilometersTo(eventLocation) > KILOMETERS_LIMIT)
                            eventsToRemove.add(o)
                    }
                }
            }

            for (o in eventsToRemove)
                objects.remove(o)

            mRecyclerView.adapter = AdapterRecyclerViewParseEvent(objects)
            mRecyclerView.layoutManager = LinearLayoutManager(view.context)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_FINE_LOCATION ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("LOCATION PERMISSION", "Location permission granted.")
                } else {
                    Toast.makeText(
                        containerContext, "No permitiste usar tu ubicación, solo nos basaremos en tu tiempo libre.",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }
}

data class TimeInterval(val startDate: Date, val endDate: Date) {
    fun conflict(startPublicEventDate: Date): Boolean {
        return startPublicEventDate > startDate && startPublicEventDate < endDate
    }
}
