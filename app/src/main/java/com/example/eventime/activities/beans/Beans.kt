package com.example.eventime.activities.beans

import android.graphics.Bitmap
import android.util.Log
import com.parse.ParseFile
import com.parse.ParseObject
import java.util.*
import kotlin.collections.ArrayList

data class Event(
    val eventId: String?,
    val name: String,
    val location: Location?,
    val image: Bitmap?,
    val description: String,
    var dates: ArrayList<EventDate>,
    val publicationDate: Calendar,
    val category: Category?,
    val privateEvent: Boolean,

    val postedPerson: Person?,
    var firstOfCategory: Boolean?,
    val comments: ArrayList<Comment>?,
    val nextDateHour: Calendar?,
    var parseFileImage: ParseFile? = null,
    var parseObject: ParseObject = ParseObject("")
) {
    override fun equals(other: Any?): Boolean {
        try {
            if(other == null)
                return false

            val otherEvent = other as Event

            return this.privateEvent == otherEvent.privateEvent
                    && this.postedPerson == otherEvent.postedPerson
                    && this.category == otherEvent.category
                    && this.location == otherEvent.location
                    && this.name == otherEvent.name
                    && this.description == otherEvent.description

        } catch (e : Exception) {
            Log.e("ERROR EQUALS", "Cant CAST")
            return false
        }
    }
}


data class Location(
    val name: String,
    val latitude: Double,
    val longitude: Double
)

data class EventDate(
    val eventDateId: String?,
    val date: Calendar,
    val startDate: Boolean,
    val hours: ArrayList<Calendar>?
)

data class Comment(
    val commentId: String?,
    val person: Person,
    val eventRating: Int,
    val date: Calendar,
    val description: String/*,
    val event: Event*/
)

data class Person(
    val personId: String?,
    val name: String,
    val lastname: String,
    //val lastname: String
    val photo: Bitmap?,
    val photoParseFile: ParseFile? = null
)

data class Category(
    val categoryId: String?,
    val name: String,
    var selected: Boolean = false,
    val iconParseFile: ParseFile? = null,
    val iconWParseFile: ParseFile? = null,
    val parseObject: ParseObject = ParseObject("")
)