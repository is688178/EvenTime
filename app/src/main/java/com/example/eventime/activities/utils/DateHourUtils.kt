package com.example.eventime.activities.utils

import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.min

class DateHourUtils {
    companion object {
        const val PREVIOUS_HOUR = -1
        const val LATER_HOUR = 1
        const val SAME_HOUR = 0
        const val PREVIOUS_DATE = -1
        const val LATER_DATE = 1
        const val SAME_DATE = 0

        /*private val sdf = SimpleDateFormat("dd/MM/yyyy")
        private val sdf2 = SimpleDateFormat("yyyy-MM-dd")*/

        private const val ZERO = "0"
        private const val SLASH = "/"
        private const val COLON = ":"
        private const val AM = "am"
        private const val PM = "pm"

        private const val JANUARY = "Enero"
        private const val FEBRUARY = "Febrero"
        private const val MARCH = "Marzo"
        private const val APRIL = "Abril"
        private const val MAY = "Mayo"
        private const val JUNE = "Junio"
        private const val JULY = "Julio"
        private const val AUGUST = "Agosto"
        private const val SEPTEMBER = "Septiembre"
        private const val OCTOBER = "Octubre"
        private const val NOVEMBER = "Noviembre"
        private const val DECEMBER = "Diciembre"

        fun createDateFromString(dateStr: String) : Calendar {
            val values = dateStr.split('/')
            val day = values[0].toInt()
            val month = values[1].toInt() - 1
            val year = values[2].toInt()

            val date = Calendar.getInstance()
            date.set(year, month, day)
            return date
        }

        fun createHourFromString(hourStr: String) : Calendar {
            val values = hourStr.split(':')
            val hour = values[0].toInt()
            val minute = values[1].toInt()

            val date = Calendar.getInstance()
            date.set(Calendar.HOUR_OF_DAY, hour)
            date.set(Calendar.MINUTE, minute)
            return date
        }

        fun formatDateToShowFormat(cal: Calendar) : String {
            val day = cal.get(Calendar.DAY_OF_MONTH)
            val month = cal.get(Calendar.MONTH)
            val year = cal.get(Calendar.YEAR)

            val cMonth = month + 1
            val formattedDay = if (day < 10) "$ZERO$day" else day
            val formattedMonth = if (cMonth < 10) "$ZERO$cMonth" else cMonth

            return "$formattedDay$SLASH$formattedMonth$SLASH$year"
        }

        fun formatDateToMonthNameShowFormat(cal: Calendar) : String {
            val day = cal.get(Calendar.DAY_OF_MONTH)
            val month = cal.get(Calendar.MONTH) + 1
            val year = cal.get(Calendar.YEAR)

            val formattedDay = if (day < 10) "$ZERO$day" else day
            val formattedMonth = when (month) {
                1 -> JANUARY
                2 -> FEBRUARY
                3 -> MARCH
                4 -> APRIL
                5 -> MAY
                6 -> JUNE
                7 -> JULY
                8 -> AUGUST
                9 -> SEPTEMBER
                10 -> OCTOBER
                11 -> NOVEMBER
                12 -> DECEMBER
                else -> "---"
            }

            return "$formattedDay de $formattedMonth $year"
        }

        fun formatHourToShowFormat(cal: Calendar) : String {
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            val minute = cal.get(Calendar.MINUTE)
            var cHour = hour
            var period = AM
            if (hour > 12) {
                period = PM
                cHour = hour - 12
            }
            if (cHour == 0){
                cHour = 12
            }
            val formattedHour = if (cHour < 10) "$ZERO$cHour" else cHour
            val formattedMinute = if (minute < 10) "$ZERO$minute" else minute.toString()
            return "$formattedHour$COLON$formattedMinute $period"
        }

        fun formatHourToString(cal: Calendar) : String {
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            val minute = cal.get(Calendar.MINUTE)

            return "$hour$COLON$minute"
        }


        fun joinHoursToString(hours: ArrayList<Calendar>, showFormat: Boolean) : String {
            var hoursStr = ""
            if (showFormat) {
                hours.forEach { hour ->
                    hoursStr += "${formatHourToShowFormat(hour)}, "
                }
                hoursStr = hoursStr.substring(0, hoursStr.length - 2)
            } else {
                hours.forEach { hour ->
                    hoursStr += "${formatHourToString(hour)},"
                }
                hoursStr = hoursStr.substring(0, hoursStr.length - 1)
            }
            return hoursStr
        }


        fun compareDates(dateC1: Calendar, dateC2: Calendar) : Int {
            val year1 = dateC1.get(Calendar.YEAR)
            val month1 = dateC1.get(Calendar.MONTH)
            val day1 = dateC1.get(Calendar.DAY_OF_MONTH)
            val year2 = dateC2.get(Calendar.YEAR)
            val month2 = dateC2.get(Calendar.MONTH)
            val day2 = dateC2.get(Calendar.DAY_OF_MONTH)

            if (year1 > year2 || (year1 == year2 && month1 > month2) ||
                (year1 == year2 && month1 == month2 && day1 > day2)) {
                return LATER_DATE
            } else if (year1 < year2 || (year1 == year2 && month1 < month2) ||
                (year1 == year2 && month1 == month2 && day1 < day2)) {
                return PREVIOUS_DATE
            }
            return SAME_DATE
        }


        fun compareHours(hourC1: Calendar, hourC2: Calendar) : Int {
            val hour1 = hourC1.get(Calendar.HOUR_OF_DAY)
            val minute1 = hourC1.get(Calendar.MINUTE)
            val hour2 = hourC2.get(Calendar.HOUR_OF_DAY)
            val minute2 = hourC2.get(Calendar.MINUTE)

            if (hour1 > hour2 || (hour1 == hour2 && minute1 > minute2))
                return LATER_HOUR
            if (hour1 < hour2 || (hour1 == hour2 && minute1 < minute2))
                return PREVIOUS_HOUR
            return SAME_HOUR
        }

        fun dateInRange(date: Calendar, initDate: Calendar, finDate: Calendar): Boolean {
            val initComparison = compareDates(date, initDate)
            val finComparison = compareDates(date, finDate)
            return (initComparison == SAME_DATE || initComparison == LATER_DATE) &&
                    (finComparison == SAME_DATE || finComparison == PREVIOUS_DATE)
        }

        fun joinDatesToString(dates: ArrayList<Calendar>) : String {
            var datesStr = ""
            if (dates.isNotEmpty()) {
                dates.forEach { hour ->
                    datesStr += "${formatDateToShowFormat(hour)},"
                }
                datesStr = datesStr.substring(0, datesStr.length - 1)
            }
            return datesStr
        }

        fun splitDatesToArrayList(datesStr: String) : ArrayList<Calendar> {
            val dates = ArrayList<Calendar>()
            datesStr.split(',').forEach { hourStr ->
                /*val values = hourStr.split('/')
                val cal = Calendar.getInstance()
                cal.set(Calendar.DAY_OF_MONTH, values[0].toInt())
                cal.set(Calendar.MONTH, values[1].toInt() - 1)
                cal.set(Calendar.YEAR, values[2].toInt())
                dates.add(cal)*/
                val date = createDateFromString(hourStr)
                dates.add(date)
            }
            return dates
        }


        fun splitHoursToArrayList(hoursStr: String) : ArrayList<Calendar> {
            val hours = ArrayList<Calendar>()
            hoursStr.split(',').forEach {hourStr ->
                /*val values = hourStr.split(':')
                val cal = Calendar.getInstance()
                cal.set(Calendar.HOUR_OF_DAY, values[0].toInt())
                cal.set(Calendar.MINUTE, values[1].toInt())*/

                val hour = createHourFromString(hourStr)
                hours.add(hour)
            }
            return hours
        }
    }
}