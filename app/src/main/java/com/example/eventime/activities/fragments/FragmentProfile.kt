package com.example.eventime.activities.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventime.R
import com.example.eventime.activities.activities.eventDetails.ActivityEventDetails
import com.example.eventime.activities.activities.main.LogoutListener
import com.example.eventime.activities.adapters.AdapterRecyclerViewEvents
import com.example.eventime.activities.beans.*
import com.example.eventime.activities.listeners.ClickListener
import com.example.eventime.activities.utils.DateHourUtils
import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.intentFor
import java.util.*
import kotlin.collections.ArrayList

class FragmentProfile : Fragment(), ClickListener, View.OnClickListener {
    private lateinit var listener: LogoutListener
    private lateinit var mEmail: TextView
    private lateinit var mUserName: TextView
    private lateinit var mImageView: ImageView
    private lateinit var mCloseSession: Button
    private lateinit var mRecyclerView: RecyclerView

    private lateinit var adapterRvEvents: AdapterRecyclerViewEvents
    private lateinit var containerContext: Context
    private var events = ArrayList<Event>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as LogoutListener
        } catch (error: ClassCastException) {
            Log.e("FragmentProfile", "The activity must implement LogoutListener, $error")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        this.containerContext = container!!.context

        mEmail = view.find(R.id.fragment_profile_tv_email)
        mUserName = view.find(R.id.fragment_profile_tv_user_name)
        mCloseSession = view.find(R.id.fragment_profile_btn_logout)
        mImageView = view.find(R.id.ic_user)
        mRecyclerView = view.find(R.id.fragment_profile_rv_userCreatedEvents)

        serUserImage()
        setUserName()
        setUserEmail()
        fetchUserEvents()

        mCloseSession.setOnClickListener(this)

        return view
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.fragment_profile_btn_logout -> {
                listener.logout()
            }
        }
    }

    private fun serUserImage() {
        if (ParseUser.getCurrentUser() != null) {
            try {
                val parseFile: ParseFile = ParseUser.getCurrentUser().get("image") as ParseFile
                Glide.with(this).load(parseFile.url).into(mImageView)
            } catch (exception: Exception) {
                Log.e("DEBUG User Name", exception.message.toString())
            }
        }
    }

    private fun setUserName() {
        if (ParseUser.getCurrentUser() != null) {
            try {
                mUserName.text = ParseUser.getCurrentUser().username
            } catch (exception: Exception) {
                Log.e("DEBUG User Name", exception.message.toString())
            }
        }
    }

    private fun setUserEmail() {
        if (ParseUser.getCurrentUser() != null) {
            try {
                mEmail.text = ParseUser.getCurrentUser().email
            } catch (exception: Exception) {
                Log.e("DEBUG User Email", exception.message.toString())
            }
        }
    }

    override fun onClick(view: View, index: Int) {
        when (view.parent) {
            mRecyclerView -> {
                startActivity(
                    intentFor<ActivityEventDetails>(
                        "eventId" to events[index].eventId,
                        "eventDateId" to events[index].dates[0].eventDateId,
                        "date" to DateHourUtils.formatDateToShowFormat(events[index].dates[0].date),
                        "hour" to DateHourUtils.formatHourToString(events[index].dates[0].date)
                    )
                )
            }
        }
    }

    private fun fetchUserEvents() {
        //FETCH EVENT DATES
        val cal = Calendar.getInstance()
        val currentUserId = ParseUser.getCurrentUser().objectId

        val queryDate = ParseQuery.getQuery<ParseObject>("EventDate")
        queryDate.include("Event")
        queryDate.include("Event.Person")
        queryDate.include("Event.Category")
        queryDate.addAscendingOrder("date")
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

                            if (person != null) {
                                if (person.personId == currentUserId)
                                    eventsO.add(eventO)
                            }
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
}