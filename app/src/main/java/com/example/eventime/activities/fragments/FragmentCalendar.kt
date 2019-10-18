package com.example.eventime.activities.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.eventime.R
import kotlinx.android.synthetic.main.activity_calendar_event.*
import kotlinx.android.synthetic.main.activity_calendar_event.view.*
import android.os.LocaleList
import java.text.SimpleDateFormat
import java.time.LocalDate


class FragmentCalendar : Fragment(){

    private lateinit var container: Context
    private lateinit var  msg: String

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(com.example.eventime.R.layout.activity_calendar_event, container, false)
        this.container = container!!.context

        view.calendar.setOnDateChangeListener{ view, year, month, dayOfMonth ->
            // Note that months are indexed from 0. So, 0 means January, 1 means february, 2 means march etc.
            msg = ("$dayOfMonth-"+(month + 1)+"-$year")
            println(msg)
        }

        /*val parser =  SimpleDateFormat("yyyy-MM-dd")
        val formatter = SimpleDateFormat("dd.MM.yyyy")
        val startTime = formatter.format((parser.parse(msg))*/

        view.fragment_calendar_add_event.setOnClickListener {
            val intent = Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, "New Event")
                .putExtra(CalendarContract.Events.EVENT_LOCATION, "Location")
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, System.currentTimeMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, System.currentTimeMillis() + (60*60*1000))
            startActivity(intent)

            //startActivityFor ¿Result// er ue regresa
            //onActivityforResult
        }

        return view
    }


}