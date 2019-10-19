package com.example.eventime.activities.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
//import com.example.eventime.R
import kotlinx.android.synthetic.main.fragment_calendar_event.view.*
import java.util.*


class FragmentCalendar : Fragment(){

    private lateinit var container: Context
    private lateinit var  msg: String
    private lateinit var beginTime: Calendar
    private lateinit var endTime: Calendar

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(com.example.eventime.R.layout.fragment_calendar_event, container, false)
        this.container = container!!.context
        //view.calendar.selectedDates
        view.calendar.setOnDateChangeListener{ view, year, month, dayOfMonth ->
            // Note that months are indexed from 0. So, 0 means January, 1 means february, 2 means march etc.
            msg = ("$dayOfMonth-"+(month + 1)+"-$year")
            beginTime = Calendar.getInstance()
            beginTime.set(year, month, dayOfMonth, 12, 0)

            endTime = Calendar.getInstance()
            endTime.set(year, month, dayOfMonth, 13, 0)
            //println(msg)

            /*val intent = Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, "New Event")
                .putExtra(CalendarContract.Events.EVENT_LOCATION, "Location")
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, System.currentTimeMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, System.currentTimeMillis() + (60*60*1000))
            startActivity(intent)*/

        }

        view.fragment_calendar_add_event.setOnClickListener {
            print("BEGIN*****************************"+beginTime)
            print("END********************************"+endTime)
            val intent = Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, "New Event")
                .putExtra(CalendarContract.Events.EVENT_LOCATION, "Location")
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime) //System.currentTimeMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime)//System.currentTimeMillis() + (60*60*1000))
            startActivity(intent)

            //startActivityFor Result// ver que regresa
            //onActivityforResult
        }

        /*val beginTime = Calendar.getInstance()
        beginTime.set(2012, 0, 19, 7, 30)

        val endTime = Calendar.getInstance()
        endTime.set(2012, 0, 19, 8, 30)

        val intent = Intent(Intent.ACTION_INSERT)
            .setData(Events.CONTENT_URI)
            .putExtra(
                CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                beginTime.getTimeInMillis()
            )
            .putExtra(
                CalendarContract.EXTRA_EVENT_END_TIME,
                endTime.getTimeInMillis()
            )
            .putExtra(Events.TITLE, "Yoga")
            .putExtra(Events.DESCRIPTION, "Group class")
            .putExtra(Events.EVENT_LOCATION, "The gym")
            .putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY)
            .putExtra(Intent.EXTRA_EMAIL, "rowan@example.com,trevor@example.com")

        startActivity(intent)*/



        return view
    }


}