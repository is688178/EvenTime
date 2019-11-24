package com.example.eventime.activities.activities.eventDetails

import com.example.eventime.activities.beans.Event

interface ContractEventDetails {
    interface View {
        fun showEvent(event: Event)
    }

    interface Presenter {
        fun fetchEvent(eventId: String)
    }
}