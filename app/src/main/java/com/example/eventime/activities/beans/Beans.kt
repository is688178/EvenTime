package com.example.eventime.activities.beans

data class Event(
    val name: String,
    val location: Location,
        /*val nextDate: String,
        val nextHour: String,*/
        val image: String,
    val description: String,
    val dates: ArrayList<EventDate>,
    val comments: ArrayList<Comment>,
    val category: String)//Category)

data class Location(
        val name: String
        /*val latitude: String,
        val longitude: String*/
)

data class EventDate(
        val date: String,
        val hours: ArrayList<String>
)

data class Comment(
    val person: Person,
    val rate: Int,
    val date: String,
    val description: String
)

data class Person(
        val name: String,
        val lastname: String,
        //val lastname: String
        val photo: String
)

//data class Category()