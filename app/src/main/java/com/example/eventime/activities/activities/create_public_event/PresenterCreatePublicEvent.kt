package com.example.eventime.activities.activities.create_public_event

import android.util.Log
import com.example.eventime.activities.beans.Category
import com.example.eventime.activities.beans.Event
import com.parse.*
import java.util.*
import kotlin.collections.ArrayList


class PresenterCreatePublicEvent(private val view: ContractCreatePublicEvent.View) :
    ContractCreatePublicEvent.Presenter {

    override fun saveEvent(event: Event) {
        val eventObj = ParseObject("Event")
        eventObj.put("name", event.name)
        eventObj.put("description", event.description)
        eventObj.put("locationName", event.location!!.name)
        eventObj.put("location", ParseGeoPoint(event.location.latitude, event.location.longitude))
        if (event.image != null) {
            eventObj.put("image", event.parseFileImage!!)
        }
        eventObj.put("publicationDate", event.publicationDate.time)
        eventObj.put("private", event.privateEvent)
        eventObj.put("Person", ParseUser.getCurrentUser())
        eventObj.put("Category", event.category!!.parseObject)

        event.dates.forEach { eventDate ->
            if (eventDate.hours != null) {
                eventDate.hours.forEach {hour ->
                    val date = eventDate.date
                    date[Calendar.HOUR_OF_DAY] = hour[Calendar.HOUR_OF_DAY]
                    date[Calendar.MINUTE] = hour[Calendar.MINUTE]

                    val hourObject = ParseObject("EventDate")
                    hourObject.put("date", date.time)
                    hourObject.put("Event", eventObj)
                    hourObject.saveInBackground()
                }
            }
        }
    }

    override fun fetchCategories() {
        val query = ParseQuery.getQuery<ParseObject>("Category")
        query.findInBackground { categories, e ->
            if (e == null) {
                val categoriesO = ArrayList<Category>()
                categories.map { category ->
                    return@map Category(
                        category.objectId,
                        category["name"].toString(),
                        null,
                        false,
                        category.getParseFile("icon"),
                        category
                    )
                }.toCollection(categoriesO)
                view.fillCategoriesField(categoriesO)
            } else {
                Log.e("CATEGORIES FETCH", "Error: " + e.message)
            }
        }
    }
}