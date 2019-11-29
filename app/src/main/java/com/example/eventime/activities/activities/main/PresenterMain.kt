package com.example.eventime.activities.activities.main

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import com.example.eventime.R
import com.example.eventime.activities.beans.*
import com.example.eventime.activities.utils.DateHourUtils
import com.parse.ParseFile
import com.parse.ParseGeoPoint
import com.parse.ParseObject
import com.parse.ParseQuery
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class PresenterMain(private val view: ContractMain.View) : ContractMain.Presenter {

    override fun fetchCategories(context: Context) {
        val query = ParseQuery.getQuery<ParseObject>("Category")
        query.findInBackground { categories, e ->
            if (e == null) {
                val categoriesO = ArrayList<Category>()
                categories.map { category ->
                    return@map Category(
                        category.objectId,
                        category["name"].toString(),
                        false,
                        category.getParseFile("icon"),
                        category.getParseFile("iconW")
                    )
                }.toCollection(categoriesO)

                categoriesO.add(
                    0,
                    Category("", "Todos")
                )
                view.fillCategories(categoriesO)
            } else {
                Log.e("CATEGORIES FETCH", "Error: " + e.message)
            }
        }
    }

    override fun fetchEvents() {
        //FETCH EVENT DATES
        val cal = Calendar.getInstance()
        val queryDate = ParseQuery.getQuery<ParseObject>("EventDate")
        queryDate.include("Event")
        queryDate.include("Event.Person")
        queryDate.include("Event.Category")
        queryDate.addAscendingOrder("date")
        queryDate.whereGreaterThanOrEqualTo("date", cal.time)
        queryDate.findInBackground { dates, e ->
            if (e == null) {
                val eventsO = ArrayList<Event>()

                //Aux. to avoid duplicates because of Parse
                var first = true
                var lastEventId = ""
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.YEAR, 1800)
                var lastEventDate = calendar.time

                dates.forEach { date ->
                    val event = date.getParseObject("Event")

                    if (event != null &&
                        (first
                                || ( lastEventId != event.objectId.toString() )
                                || ( lastEventId == event.objectId.toString() && lastEventDate != date.getDate("date") )) ) {

                        lastEventId = event.objectId.toString()
                        lastEventDate = date.getDate("date")!!

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

                            eventsO.add(eventO)
                        }
                    } else {
                        Log.e("EVENTS FETCH", "Error: " + e?.message)
                    }

                    first = false
                }

                if (eventsO.size != 0) {
                    view.showEvents(eventsO)
                } else {
                    view.showNoEventsFound()
                }
            } else {
                Log.e("EVENTS FETCH", "Error: " + e.message!!)
            }
        }
    }
}