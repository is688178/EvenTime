package com.example.eventime.activities.messages

import android.content.Context
import com.example.eventime.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class Message {
    companion object {
        fun showMessage() {

        }

        fun showValidationErrorMessage(context: Context, message: ValidationError) {
            val stringId = when (message) {
                ValidationError.SET_AL_LEAST_ONE_HOUR -> {
                    R.string.set_at_least_on_hour
                } ValidationError.HOUR_ALREADY_EXISTS -> {
                    R.string.hour_already_exists
                } ValidationError.DATE_ALREADY_EXISTS -> {
                    R.string.date_already_exists
                } ValidationError.MISSING_DATA -> {
                    R.string.missing_data
                }
            }


            val message = context.getString(stringId)
            MaterialAlertDialogBuilder(context)
                .setTitle(context.getString(R.string.alert_dialog_validation_error_title))
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.accept_text)) {_,_ ->}
                .show()
        }
    }
}