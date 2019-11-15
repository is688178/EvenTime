package com.example.eventime.activities.activities.main

import com.example.eventime.activities.beans.Category
import com.example.eventime.activities.beans.Event

interface ContractMain {
    interface View {
        fun fillCategories(categories: ArrayList<Category>)
        fun showEvents(events: ArrayList<Event>)
        fun showNoEventsFound()
    }

    interface Presenter {
        fun fetchCategories()
        fun fetchEvents()
    }
}