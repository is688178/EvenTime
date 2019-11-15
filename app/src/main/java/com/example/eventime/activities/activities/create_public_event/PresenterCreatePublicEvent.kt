package com.example.eventime.activities.activities.CreatePublicEvent

import com.example.eventime.activities.beans.Event
import com.parse.ParseObject

class PresenterCreatePublicEvent(private val view: ContractCreatePublicEvent.View): ContractCreatePublicEvent.Presenter {

    override fun saveEvent(event: Event) {
        val eventObj = ParseObject("Event")
        eventObj.put("name", event.name)
        eventObj.put("description", event.description)
        eventObj.put("locationName", event.locationName)
        eventObj.put("location", event.location)
        eventObj.put("image", event.image)
        eventObj.put("publicationDate", event.publicationDate)
        eventObj.put("private", event.privateEvent)
        eventObj.put("person", event.person)
        eventObj.put("category", event.category)
        eventObj.saveInBackground()

        event.dates.forEach { date ->
            val dateObject = ParseObject("EventDate")
            dateObject.put("startDate", date.date)
            dateObject.put("event", event)
            dateObject.saveInBackground()
        }
    }
}