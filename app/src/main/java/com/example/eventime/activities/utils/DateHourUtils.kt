package com.example.eventime.activities.utils

import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class FormatDateHour {
    companion object {
        private val sdf = SimpleDateFormat("dd/MM/yyyy")
        private val sdf2 = SimpleDateFormat("yyyy-MM-dd")

        private const val ZERO = "0"
        private const val SLASH = "/"
        private const val COLON = ":"
        private const val AM = "am"
        private const val PM = "pm"

        const val DAY = "day"
        const val MONTH = "month"
        const val YEAR = "year"

        fun formatDateToShowFormat(day: Int, month: Int, year: Int) : String {
            /*val cMonth = month + 1
            val formattedDay = if (day < 10) "$ZERO$day" else day
            val formattedMonth = if (cMonth < 10) "$ZERO$cMonth" else cMonth

            return "$formattedDay$SLASH$formattedMonth$SLASH$year"*/

            val dateStr = parseDate(day, month, year)

            return sdf.format(dateStr)
        }

        /*fun formatHourToShowFormat(hour: Int, minute: Int) : String {
            var cHour = hour
            var period = AM
            if (hour > 12) {
                period = PM
                cHour = hour - 12
            }
            val formattedHour = if (cHour < 10) "$ZERO$cHour" else cHour
            val formattedMinute = if (minute < 10) "$ZERO$minute" else minute.toString()
            return "$formattedHour$COLON$formattedMinute $period"
        }*/

        fun formatHourToShowFormat(time: Calendar) : String {
            val hour = time.get(Calendar.HOUR)
            val minute = time.get(Calendar.MINUTE)
            var cHour = hour
            var period = AM
            if (hour > 12) {
                period = PM
                cHour = hour - 12
            }
            val formattedHour = if (cHour < 10) "$ZERO$cHour" else cHour
            val formattedMinute = if (minute < 10) "$ZERO$minute" else minute.toString()
            return "$formattedHour$COLON$formattedMinute $period"
        }

        fun parseDate(day:Int, month:Int, year:Int) : Date {
            val cMonth = month + 1
            val formattedDay = if (day < 10) "$ZERO$day" else day
            val formattedMonth = if (cMonth < 10) "$ZERO$cMonth" else cMonth

            return sdf.parse("$formattedDay$SLASH$formattedMonth$SLASH$year")!!
        }

        fun unformatDate(date: String): HashMap<String, Int>? {
            val values = HashMap<String, Int>()
            if (date.length == 10){
                values[DAY] = (date.subSequence(0, 2).toString()).toInt()
                values[MONTH] = (date.subSequence(3, 5).toString()).toInt() - 1
                values[YEAR] = (date.subSequence(6, 10).toString()).toInt()

            } else {
                return null
            }
            return values
        }
    }
}