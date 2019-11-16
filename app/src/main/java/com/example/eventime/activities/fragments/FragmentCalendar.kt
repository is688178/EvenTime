package com.example.eventime.activities.fragments

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.eventime.activities.activities.create_public_event.ActivityCreatePrivateEvent
import kotlinx.android.synthetic.main.fragment_calendar_event.view.*
import org.jetbrains.anko.support.v4.startActivity
import java.util.*


class FragmentCalendar : Fragment(){

    private lateinit var container: Context
    private lateinit var  msg: String
    private lateinit var beginTime: Calendar
    private lateinit var endTime: Calendar

    companion object {
        const val CREATE_EVENT = 1000
    }

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(com.example.eventime.R.layout.fragment_calendar, container, false)
        this.container = container!!.context
        view.calendar.setOnDateChangeListener{ _, year, month, dayOfMonth ->
            // Note that months are indexed from 0. So, 0 means January, 1 means february, 2 means march etc.
            msg = ("$dayOfMonth-"+(month + 1)+"-$year")
            beginTime = Calendar.getInstance()
            beginTime.set(year, month, dayOfMonth, 12, 0)

            endTime = Calendar.getInstance()
            endTime.set(year, month, dayOfMonth, 13, 0)

        }

        view.fragment_calendar_add_event.setOnClickListener {

            /*            val intent = Intent(Intent.ACTION_INSERT)
                            .setData(CalendarContract.Events.CONTENT_URI)
                            .putExtra(CalendarContract.Events.TITLE, "New Event")
                            .putExtra(CalendarContract.Events.EVENT_LOCATION, "Location")
                            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime) //System.currentTimeMillis())
                            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime)//System.currentTimeMillis() + (60*60*1000))*/

            startActivity<ActivityCreatePrivateEvent>()
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            CREATE_EVENT -> {
                when(resultCode) {
                    RESULT_OK -> print(data)
                    RESULT_CANCELED -> Log.d("TAG", "CANCELED")
                }
            }
        }
    }


}
