package com.example.eventime.activities.fragments

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_calendar_event.view.*
import java.util.*
import java.util.jar.Manifest


class FragmentCalendar : Fragment(){

    private lateinit var container: Context
    private lateinit var  msg: String
    private lateinit var beginTime: Calendar
    private lateinit var endTime: Calendar

    companion object {
        const val CREATE_EVENT = 1000
    }

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(com.example.eventime.R.layout.fragment_calendar_event, container, false)
        this.container = container!!.context
        view.calendar.setOnDateChangeListener{ view, year, month, dayOfMonth ->
            // Note that months are indexed from 0. So, 0 means January, 1 means february, 2 means march etc.
            msg = ("$dayOfMonth-"+(month + 1)+"-$year")
            beginTime = Calendar.getInstance()
            beginTime.set(year, month, dayOfMonth, 12, 0)

            endTime = Calendar.getInstance()
            endTime.set(year, month, dayOfMonth, 13, 0)

        }

        view.fragment_calendar_add_event.setOnClickListener {

            val intent = Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, "New Event")
                .putExtra(CalendarContract.Events.EVENT_LOCATION, "Location")
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime) //System.currentTimeMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime)//System.currentTimeMillis() + (60*60*1000))

            startActivity(intent)
        }


        return view
    }

    /*override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_CONTACTS -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }*/

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


/*if (ContextCompat.checkSelfPermission(this.container, Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this.container,
                        Manifest.permission.WRITE_CALENDAR)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(this.container,
                        arrayOf(Manifest.permission.WRITE_CALENDAR),
                        MY_PERMISSIONS_REQUEST_WRITE_CALENCAR)

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                val intent = Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.Events.TITLE, "New Event")
                    .putExtra(CalendarContract.Events.EVENT_LOCATION, "Location")
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime) //System.currentTimeMillis())
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime)//System.currentTimeMillis() + (60*60*1000))

                startActivityForResult(intent, CREATE_EVENT)
            }

            /*












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