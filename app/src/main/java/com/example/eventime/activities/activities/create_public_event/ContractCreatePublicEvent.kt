package com.example.eventime.activities.activities.create_public_event

import com.example.eventime.activities.beans.Category
import com.example.eventime.activities.beans.Event

class ContractCreatePublicEvent {
    interface View {
        fun returnToMainAfterSaveEvent()
        fun fillCategoriesField(categories: ArrayList<Category>)
    }

    interface Presenter {
        fun saveEvent(event: Event)
        fun fetchCategories()
    }
}