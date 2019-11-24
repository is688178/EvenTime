package com.example.eventime.activities.activities.eventDetails

import android.content.Intent
import com.example.eventime.activities.activities.main.ActivityMain
import com.example.eventime.activities.beans.*
import com.parse.ParseObject
import com.parse.ParseQuery
import java.util.*
import kotlin.collections.ArrayList

class PresenterEventDetails(private val view: ContractEventDetails.View) : ContractEventDetails.Presenter {
    override fun fetchEvent(eventId: String) {
        val queryEvent = ParseQuery.getQuery<ParseObject>("Event")
        queryEvent.whereEqualTo("objectId", eventId)
        queryEvent.getFirstInBackground { event, e ->
            if (e == null) {
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

                val datesO = ArrayList<EventDate>()

                val eventO = Event(
                    event.objectId,
                    event["name"].toString(),
                    location,
                    null,
                    event["description"].toString(),
                    datesO,
                    Calendar.getInstance(),
                    null,
                    false,
                    null,
                    false,
                    ArrayList(),
                    Calendar.getInstance(),
                    imageParseFile,
                    event
                )

                val commentsQuery = ParseQuery.getQuery<ParseObject>("Comment")
                commentsQuery.whereEqualTo("Event", eventId)
                commentsQuery.include("Person")
                commentsQuery.findInBackground { comments, e ->
                    if (e == null) {
                        comments.forEach {comment ->
                            val person = comment.getParseObject("Person")
                            val personO = if (person != null) {
                                Person(
                                    "",
                                    person["name"].toString(),
                                    "",
                                    null,
                                    person.getParseFile("image")
                                )
                            } else {
                                Person(
                                    "",
                                    "Anonimo",
                                    "",
                                    null,
                                    null
                                )
                            }

                            val date = comment.getDate("date")
                            val cal = Calendar.getInstance()
                            cal.time = date
                            val commentO = Comment(
                                comment.objectId,
                                personO,
                                0,
                                cal,
                                comment["description"].toString()
                            )

                            eventO.comments?.add(commentO)
                        }
                    } else {
                        //SHOW ERROR
                    }
                }
                view.showEvent(eventO)
            } else {

            }
        }
    }
}