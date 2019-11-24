package com.example.eventime.activities.beans

import android.graphics.Bitmap
import com.parse.ParseFile
import com.parse.ParseObject
import java.util.*
import kotlin.collections.ArrayList
import android.os.Parcel
import android.os.Parcelable

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
    var selected: Boolean = false,
    val iconParseFile: ParseFile? = null,
    val iconWParseFile: ParseFile? = null,
    val parseObject: ParseObject = ParseObject("")
)
/*
data class Event(var eventId: String?,
                 var name: String,
                 var location: Location?,
                 var image: Bitmap?,
                 var  description: String,
                 var dates: ArrayList<EventDate>,
                 val publicationDate: Calendar,
                 val category: Category?,
                 val privateEvent: Boolean,

                 val postedPerson: Person?,
                 var firstOfCategory: Boolean?,
                 val comments: ArrayList<Comment>?,
                 var parseFileImage: ParseFile? = null,
                 var parseObject: ParseObject = ParseObject("")
) : Parcelable {

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<Event> = object : Parcelable.Creator<Event>{
            override fun newArray(size: Int): Array<Event?> = arrayOfNulls(size)
            override fun createFromParcel(source: Parcel): Event = Event(source)
        }
    }

    constructor(source: Parcel): this (
        source.readInt(),
        source.readString(),
        source.readString(),
        source.readParcelable(Event::class.java.classLoader)
    )

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.let {
            dest.writeString(eventId)
            dest.writeString(name)
            dest.writeParcelable(location)
            dest.writeValue(image)
            dest.writeString(description)
            dest.writeParcelable(dates[0])
        }
    }

    private fun readFromParcel(inO: Parcel) {
        eventId = inO.readString()
        name = inO.readString()!!
        location = inO.readParcelable(Location::class.java.classLoader)
        image = inO.readValue(Bitmap::class.java.classLoader) as Bitmap
        description = inO.readString()!!
        dates = ArrayList()
        dates.add(inO.readParcelable(EventDate::class.java.classLoader))
    }
    
    override fun describeContents(): Int = 0
}

data class Location(
    val name: String,
    val latitude: Double,
    val longitude: Double
):Parcelable {
    companion object {
        @JvmField val CREATOR: Parcelable.Creator<Location> = object : Parcelable.Creator<Location>{
            override fun newArray(size: Int): Array<Location?> = arrayOfNulls(size)
            override fun createFromParcel(source: Parcel): Location = Location(source)
        }
    }

    constructor(source: Parcel): this (
        source.readInt(),
        source.readString(),
        source.readString(),
        source.readParcelable(Event::class.java.classLoader)
    )

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.let {
            dest.writeString(eventId)
            dest.writeString(name)
            dest.writeParcelable(location)
            dest.writeValue(image)
            dest.writeString(description)
            dest.writeParcelable(dates[0])
        }
    }

    private fun readFromParcel(inO: Parcel) {
        eventId = inO.readString()
        name = inO.readString()!!
        location = inO.readParcelable(Location::class.java.classLoader)
        image = inO.readValue(Bitmap::class.java.classLoader) as Bitmap
        description = inO.readString()!!
        dates = ArrayList()
        dates.add(inO.readParcelable(EventDate::class.java.classLoader))
    }

    override fun describeContents(): Int = 0
}
*/