package com.example.eventime.activities.activities.CreatePublicEvent

import com.example.eventime.activities.beans.Event

class ContractCreatePublicEvent {
    interface View {
        fun returnToMainAfterSaveEvent()
    }

    interface Presenter {
        fun saveEvent(event: Event)
    }
}