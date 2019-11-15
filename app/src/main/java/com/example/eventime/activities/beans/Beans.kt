package com.example.eventime.activities.beans

import android.graphics.Bitmap
import com.parse.ParseFile
import com.parse.ParseObject
import java.io.File
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
    //val nextDate: Calendar,
    //val nextHour: Calendar
    val nextDateHour: Calendar?,
    var parseFileImage: ParseFile? = null,
    var parseObject: ParseObject = ParseObject("")
)

data class Location(
    val name: String,
    val latitude: Double,
    val longitude: Double
)

data class EventDate(
    val eventDateId: String?,
    val date: Calendar,
    val startDate: Boolean,
    //val event: Event,
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
    val icon: Bitmap?,
    var selected: Boolean = false,
    val photoParseFile: ParseFile? = null,
    val parseObject: ParseObject = ParseObject("")
)