package com.example.eventime.activities.activities.eventDetails

import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import com.example.eventime.activities.activities.main.ActivityMain
import com.example.eventime.activities.beans.*
import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
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

                val queryComments = ParseQuery.getQuery<ParseObject>("Comment")
                queryComments.orderByDescending("date")
                queryComments.whereEqualTo("Event", event)
                queryComments.include("Person")
                queryComments.findInBackground { objectsComments, e ->
                    if (e == null) {
                        for (oc in objectsComments) {
                            val user = oc["Person"] as ParseUser
                            val parseImage = user["image"] as ParseFile
                            val bitmap = BitmapFactory.decodeStream(parseImage.dataStream)
                            val person = Person(user.objectId, user.username, "", bitmap, parseImage)
                            val calendar = Calendar.getInstance()
                            calendar.time = oc["date"] as Date
                            val comment = Comment(oc.objectId, person, 5, calendar, oc["description"] as String)

                            eventO.comments?.add(comment)
                        }
                        view.showEvent(eventO)
                    } else {
                        Log.e("ERROR FIND COMMENT", e.message.toString())
                    }
                }


/*                val commentsQuery = ParseQuery.getQuery<ParseObject>("Comment")
                commentsQuery.whereEqualTo("Event", eventId)
                commentsQuery.include("Person")
                commentsQuery.findInBackground { comments, e ->
                    if (e == null) {
                        comments.forEach { comment ->
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
                }*/



            } else {
                Log.e("ERROR FIND EVENT", e.message.toString())
            }
        }
    }


}