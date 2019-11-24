package com.example.eventime.activities.fragments

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.eventime.R
import com.example.eventime.activities.activities.create_private_event.ActivityCreatePrivateEvent
import com.kizitonwose.calendarview.model.*
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.calendar_day_layout.view.*
import kotlinx.android.synthetic.main.fragment_calendar.*
import kotlinx.android.synthetic.main.fragment_calendar_event.view.*
import org.jetbrains.anko.support.v4.startActivity
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.temporal.WeekFields
import java.util.*


private val Context.inputMethodManager
    get() = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

data class PrivateEvent(val id: String, val text: String, val date: LocalDate)

class CalemdarEventsAdapter(val onClick: (PrivateEvent) -> Unit) :
    RecyclerView.Adapter<CalemdarEventsAdapter.CalendarEventsViewHolder>() {

    val events = mutableListOf<PrivateEvent>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarEventsViewHolder {
        val view = LayoutInflater.from(parent.context) .inflate(R.layout.event_item_view, parent, false)
        return CalendarEventsViewHolder(view)
        //return CalendarEventsViewHolder(parent.inflate(R.layout.event_item_view))

    }

    override fun onBindViewHolder(viewHolder: CalendarEventsViewHolder, position: Int) {
        viewHolder.bind(events[position])
    }

    override fun getItemCount(): Int = events.size

    inner class CalendarEventsViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

        init {
            itemView.setOnClickListener {
                onClick(events[adapterPosition])
            }
        }

        fun bind(event: PrivateEvent) {

        }
    }

}



class FragmentCalendar : Fragment(){

    private lateinit var container: Context
    private lateinit var  msg: String
    private lateinit var beginTime: Calendar
    private lateinit var endTime: Calendar

    companion object {
        const val CREATE_EVENT = 1000
    }

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(com.example.eventime.R.layout.fragment_calendar, container, false)
        this.container = container!!.context
        /*view.calendar.setOnDateChangeListener{ _, year, month, dayOfMonth ->
            // Note that months are indexed from 0. So, 0 means January, 1 means february, 2 means march etc.
            msg = ("$dayOfMonth-"+(month + 1)+"-$year")
            beginTime = Calendar.getInstance()
            beginTime.set(year, month, dayOfMonth, 12, 0)

            endTime = Calendar.getInstance()
            endTime.set(year, month, dayOfMonth, 13, 0)

        }*/

        view.fragment_calendar_add_event.setOnClickListener {
            startActivity<ActivityCreatePrivateEvent>()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentMonth = YearMonth.now()
        val firstMonth = currentMonth.minusMonths(10)
        val lastMonth = currentMonth.plusMonths(10)
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        calendarView.setup(firstMonth, lastMonth, firstDayOfWeek)
        calendarView.scrollToMonth(currentMonth)

        calendarView.inDateStyle = InDateStyle.ALL_MONTHS
        calendarView.outDateStyle = OutDateStyle.END_OF_ROW
        calendarView.scrollMode = ScrollMode.PAGED
        calendarView.orientation = RecyclerView.HORIZONTAL

        calendarView.maxRowCount = 6
        calendarView.hasBoundaries = true


        class DayViewContainer(view: View) : ViewContainer(view) {
            val textView = view.calendarDayText

            // Without the kotlin android extensions plugin
            // val textView = view.findViewById<TextView>(R.id.calendarDayText)
        }

        calendarView.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.textView.text = day.date.dayOfMonth.toString()
                if (day.owner == DayOwner.THIS_MONTH) {
                    container.textView.setTextColor(Color.WHITE)
                } else {
                    container.textView.setTextColor(Color.GRAY)
                }
            }
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            CREATE_EVENT -> {
                when(resultCode) {
                    RESULT_OK -> print(data)
                    RESULT_CANCELED -> Log.d("TAG", "CANCELED")
                }
            }
        }
    }




}
