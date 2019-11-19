package com.example.eventime.activities.activities.create_public_event

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eventime.R
import com.example.eventime.activities.beans.Location
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.parse.*
import org.jetbrains.anko.find
import java.util.*


class ActivityCreatePrivateEvent : AppCompatActivity(), View.OnClickListener {

    private lateinit var mETName: EditText
    private lateinit var mETDescription: EditText
    private lateinit var mETLocation: EditText
    private lateinit var location: Location

    private lateinit var mBtnAddDate: ImageButton
    private lateinit var mTVDate: TextView
    private lateinit var mBtnAddStartHour: ImageButton
    private lateinit var mTVStartHour: TextView
    private lateinit var mBtnAddEndHour: ImageButton
    private lateinit var mTVEndHour: TextView
    private lateinit var mBtnSave: ImageButton

    private lateinit var date: Calendar
    private lateinit var startHour: Calendar
    private lateinit var endHour: Calendar

    private var locationIsSet = false
    private var dateIsSet = false
    private var startHourIsSet = false
    private var endHourIsSet = false


    companion object {
        const val AUTOCOMPLETE_REQUEST = 1100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_private_event)

        bindViews()
        setOnClickListeners()
    }

    private fun bindViews() {
        mETName = find(R.id.activity_create_private_event_et_name)
        mETDescription = find(R.id.activity_create_private_event_et_description)
        mETLocation = find(R.id.activity_create_private_event_et_location)

        mBtnAddDate = find(R.id.activity_create_private_event_btn_add_date)
        mTVDate = find(R.id.activity_create_private_event_tv_date)
        mBtnAddStartHour = find(R.id.activity_create_private_event_btn_add_startTime)
        mTVStartHour = find(R.id.activity_create_private_event_tv_startTime)
        mBtnAddEndHour = find(R.id.activity_create_private_event_btn_add_end_Time)
        mTVEndHour = find(R.id.activity_create_private_event_tv_endTime)
        mBtnSave = find(R.id.activity_create_private_event_btn_save)
    }

    private fun setOnClickListeners() {
        mBtnAddDate.setOnClickListener(this)
        mBtnAddStartHour.setOnClickListener(this)
        mBtnAddEndHour.setOnClickListener(this)
        mBtnSave.setOnClickListener(this)
        mETLocation.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            mETLocation.id -> {
                if (!Places.isInitialized()) {
                    Places.initialize(applicationContext, getString(R.string.google_maps_key))
                }

                val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
                val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                    .build(this)
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST)
            }
            mBtnAddDate.id -> {
                showDatePickerDialog()
            }
            mBtnAddStartHour.id -> {
                showHourPickerDialog(mTVStartHour)
            }
            mBtnAddEndHour.id -> {
                showHourPickerDialog(mTVEndHour)
            }
            mBtnSave.id -> {
                if (locationIsSet && dateIsSet && startHourIsSet && endHourIsSet) {
                    if (startHour.time > endHour.time) {
                        Toast.makeText(
                            this,
                            "La hora de inicio no puede ser posterior a la del final.",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }

                    savePrivateEventInParse()
                    
                    finish()

                } else {
                    Toast.makeText(
                        this,
                        "Falta llenar algun campo...",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun showDatePickerDialog() {
        val c = Calendar.getInstance()
        val dayToSet = c.get(Calendar.DAY_OF_MONTH)
        val monthToSet = c.get(Calendar.MONTH)
        val yearToSet = c.get(Calendar.YEAR)

        val dialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                val cal = Calendar.getInstance()
                cal.set(year, month, dayOfMonth)
                date = Calendar.getInstance()
                date.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                date.set(Calendar.MONTH, month)
                date.set(Calendar.YEAR, year)
                val dateString = "$dayOfMonth/${month + 1}/$year"
                mTVDate.text = dateString
                dateIsSet = true
            }, yearToSet, monthToSet, dayToSet
        )
        dialog.datePicker.minDate = System.currentTimeMillis()
        dialog.show()
    }

    private fun showHourPickerDialog(hourTV: TextView) {
        val c = Calendar.getInstance()
        val currentHour = c.get(Calendar.HOUR_OF_DAY)
        val currentMinute = c.get(Calendar.MINUTE)

        TimePickerDialog(
            this,
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                val time = Calendar.getInstance()
                time.set(Calendar.HOUR_OF_DAY, hourOfDay)
                time.set(Calendar.MINUTE, minute)
                val hourString = "$hourOfDay:$minute"
                hourTV.text = hourString
                if (hourTV.id == mTVStartHour.id) {
                    startHour = Calendar.getInstance()
                    startHour.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    startHour.set(Calendar.MINUTE, minute)
                    startHourIsSet = true
                } else {
                    endHour = Calendar.getInstance()
                    endHour.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    endHour.set(Calendar.MINUTE, minute)
                    endHourIsSet = true
                }
            }, currentHour, currentMinute, false
        ).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                AUTOCOMPLETE_REQUEST -> {
                    val extras = data.extras
                    if (extras != null) {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        mETLocation.setText(place.name)
                        location = Location(
                            place.name!!,
                            place.latLng!!.latitude,
                            place.latLng!!.longitude
                        )
                        locationIsSet = true
                    }
                }
            }
        }
    }

    private fun savePrivateEventInParse() {
        val calendarStartDate = date.clone() as Calendar
        calendarStartDate.set(Calendar.HOUR_OF_DAY, startHour.get(Calendar.HOUR_OF_DAY))
        calendarStartDate.set(Calendar.MINUTE, startHour.get(Calendar.MINUTE))

        val calendarEndDate = date.clone() as Calendar
        calendarEndDate.set(Calendar.HOUR_OF_DAY, endHour.get(Calendar.HOUR_OF_DAY))
        calendarEndDate.set(Calendar.MINUTE, endHour.get(Calendar.MINUTE))

        val parseObjectEvent = ParseObject("Event")
        parseObjectEvent.put("locationName", location.name)
        parseObjectEvent.put("private", true)
        parseObjectEvent.put("name", mETName.text.toString())
        parseObjectEvent.put("Person", ParseUser.getCurrentUser())

        val geoPoint = ParseGeoPoint(location.latitude, location.longitude)
        parseObjectEvent.put("location", geoPoint)

        parseObjectEvent.put("description", mETDescription.text.toString())

        parseObjectEvent.saveInBackground(object : SaveCallback {
            override fun done(e: ParseException?) {
                if (e == null) {
                    val parseObjectEventDateStart = ParseObject("EventDate")
                    parseObjectEventDateStart.put("date", calendarStartDate.time)
                    parseObjectEventDateStart.put("Event", parseObjectEvent)
                    parseObjectEventDateStart.put("startDate", true)
                    parseObjectEventDateStart.saveInBackground(object : SaveCallback {
                        override fun done(e: ParseException?) {
                            if (e != null)
                                Log.e("ERROR SAVE StartDate", e.message.toString())
                        }
                    })

                    val parseObjectEventDateEnd = ParseObject("EventDate")
                    parseObjectEventDateEnd.put("date", calendarEndDate.time)
                    parseObjectEventDateEnd.put("Event", parseObjectEvent)
                    parseObjectEventDateEnd.put("startDate", false)
                    parseObjectEventDateEnd.saveInBackground(object : SaveCallback {
                        override fun done(e: ParseException?) {
                            if (e != null)
                                Log.e("ERROR SAVE StartDate", e.message.toString())
                        }
                    })
                } else
                    Log.d("ERROR SAVE PARSE", e.toString())
            }
        })
    }

}