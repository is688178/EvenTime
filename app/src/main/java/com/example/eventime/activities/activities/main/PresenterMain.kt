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
                    //val c = File(category.getParseFile("icon")!!.url)
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
        val eventsStr = HashSet<String>()
        var count = 0
        val query = ParseQuery.getQuery<ParseObject>("Event")
        query.whereEqualTo("private", false)
        query.include("Person")
        query.include("Category")
        query.findInBackground { events, e ->
            if (e == null) {
                val eventsO = ArrayList<Event>()
                events.map { event ->
                    eventsStr.add(event.objectId)
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
                            p["username"].toString(),//name
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


                    val eventO = Event(
                        event.objectId,
                        event["name"].toString(),
                        location,
                        null,
                        event["description"].toString(),
                        ArrayList(),
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

                    //FETCH EVENT DATES
                    val queryDate = ParseQuery.getQuery<ParseObject>("EventDate")
                    queryDate.whereEqualTo("Event", event)
                    //queryDate.orderByAscending("date")
                    queryDate.findInBackground { dates, e ->
                        if (e == null) {
                            val datesO = ArrayList<EventDate>()
                            val datesStr = HashSet<String>()
                            dates.forEach { dateP ->
                                val date = dateP.getDate("date")
                                val cal = Calendar.getInstance()
                                cal.time = date!!
                                if (!datesStr.contains(DateHourUtils.formatDateToShowFormat(cal))) {
                                    datesStr.add(DateHourUtils.formatDateToShowFormat(cal))
                                    val eventDate = EventDate(
                                        dateP.objectId,
                                        cal,
                                        false,
                                        ArrayList()
                                    )
                                    eventDate.hours!!.add(cal)
                                    datesO.add(eventDate)
                                } else {
                                    val dateIndex =
                                        datesStr.indexOf(DateHourUtils.formatDateToShowFormat(cal))
                                    datesO[dateIndex].hours!!.add(cal)
                                }
                            }

                            val evIndex = eventsStr.indexOf(dates[0].getParseObject("Event")!!.objectId)
                            eventsO[evIndex].dates = datesO

                            count++
                            if (count == eventsO.size) {
                                if (eventsO.size != 0) {
                                    view.showEvents(eventsO)
                                } else {
                                    view.showNoEventsFound()
                                }
                            }
                        } else {
                            Log.e("EVENT DATES FETCH", "Error: " + e.message!!)
                        }
                    }

                    return@map eventO
                }.toCollection(eventsO)
            } else {
                Log.e("EVENTS FETCH", "Error: " + e.message!!)
            }
        }
    }
}