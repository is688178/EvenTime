package com.example.eventime.activities.activities.main

import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import com.example.eventime.activities.beans.*
import com.parse.ParseFile
import com.parse.ParseGeoPoint
import com.parse.ParseObject
import com.parse.ParseQuery
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class PresenterMain(private val view: ContractMain.View) : ContractMain.Presenter {

    override fun fetchCategories() {
        val query = ParseQuery.getQuery<ParseObject>("Category")
        query.findInBackground { categories, e ->
            if (e == null) {
                val categoriesO = ArrayList<Category>()
                categories.map { category ->
                    //val c = File(category.getParseFile("icon")!!.url)
                    return@map Category(
                        category.objectId,
                        category["name"].toString(),
                        null,
                        false,
                        category.getParseFile("icon")
                    )
                }.toCollection(categoriesO)
                view.fillCategories(categoriesO)
            } else {
                Log.e("CATEGORIES FETCH", "Error: " + e.message)
            }
        }
    }

    override fun fetchEvents() {
        val query = ParseQuery.getQuery<ParseObject>("Event")
        query.whereEqualTo("private", false)
        query.include("Person")
        query.include("Category")
        query.findInBackground { events, e ->
            if (e == null) {
                val eventsO = ArrayList<Event>()
                events.map { event ->
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
                            p["image"] as ParseFile
                        )
                    } else {
                        null
                    }
                    val c = event.getParseObject("Category")
                    val category = if (c != null) {
                        Category(
                            c.objectId,
                            c["name"].toString(),
                            null,
                            false,
                            c["icon"] as ParseFile
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

                    /*var index = 0
                    val queryDate = ParseQuery.getQuery<ParseObject>("EventDate")
                    queryDate.whereEqualTo("Event", event)
                    queryDate.findInBackground { dates, e ->
                        if (e == null) {
                            val datesO = ArrayList<EventDate>()
                            val datesStr = HashSet<String>()
                            dates.forEach { dateP ->
                                val date = dateP.getDate("date")
                                /*if (!datesStr.contains(date)) {
                                    val eventDate = EventDate(
                                        dateP.objectId,
                                        date,
                                        false,
                                        ArrayList()
                                    )
                                    //eventDate.hours.add(date.hou)
                                } else {
                                    //add hour to date
                                }*/
                            }
                            eventsO[index].dates = datesO
                        } else {
                            Log.e("EVENT DATES FETCH", "Error: " + e.message!!)
                        }
                    }
                    index++*/



                    return@map eventO
                }.toCollection(eventsO)



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