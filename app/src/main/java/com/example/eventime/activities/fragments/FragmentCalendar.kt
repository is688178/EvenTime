package com.example.eventime.activities.fragments

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventime.R
import com.example.eventime.activities.activities.create_private_event.ActivityCreatePrivateEvent
import com.example.eventime.activities.utils.makeInVisible
import com.example.eventime.activities.utils.makeVisible
import com.example.eventime.activities.utils.setTextColorRes
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
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.WeekFields
import java.util.*


private val Context.inputMethodManager
    get() = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

data class PrivateEvent(val id: String, val text: String, val date: LocalDate)

class CalendarEventsAdapter(val onClick: (PrivateEvent) -> Unit) :
    RecyclerView.Adapter<CalendarEventsAdapter.CalendarEventsViewHolder>() {
    val events = mutableListOf<PrivateEvent>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarEventsViewHolder {
        val view = LayoutInflater.from(parent.context) .inflate(R.layout.calendar_event_item_view, parent, false)
        return CalendarEventsViewHolder(view)
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
    private var selectedDate: LocalDate? = null
    private val today = LocalDate.now()
    private val events = mutableMapOf<LocalDate, List<PrivateEvent>>()
    private val selectionFormatter = DateTimeFormatter.ofPattern("d MMM yyyy")

    companion object {
        const val CREATE_EVENT = 1000
    }

    private val eventsAdapter = CalendarEventsAdapter {

    }



    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)
        this.container = container!!.context

        view.fragment_calendar_add_event.setOnClickListener {
            startActivity<ActivityCreatePrivateEvent>()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CalendarViewRv.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        CalendarViewRv.adapter = eventsAdapter
        CalendarViewRv.addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))

        val currentMonth = YearMonth.now()
        val firstMonth = currentMonth.minusMonths(10)
        val lastMonth = currentMonth.plusMonths(10)
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        calendarView.setup(firstMonth, lastMonth, firstDayOfWeek)
        calendarView.scrollToMonth(currentMonth)

        if (savedInstanceState == null) {
            calendarView.post {
                // Show today's events initially.
                selectDate(today)
            }
        }

        calendarView.inDateStyle = InDateStyle.ALL_MONTHS
        calendarView.outDateStyle = OutDateStyle.END_OF_ROW
        calendarView.scrollMode = ScrollMode.PAGED
        calendarView.orientation = RecyclerView.HORIZONTAL

        calendarView.maxRowCount = 6
        calendarView.hasBoundaries = true


        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val textView = view.calendarDayText
            val dotView = view.CalendarDotView

            init {
                view.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH) {
                        selectDate(day.date)
                    }
                }
            }
        }

        calendarView.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {

                container.day = day
                val textView = container.textView
                val dotView = container.dotView

                textView.text = day.date.dayOfMonth.toString()

                if (day.owner == DayOwner.THIS_MONTH) {
                    textView.makeVisible()
                    when (day.date) {
                        today -> {
                            textView.setTextColorRes(R.color.colorWhite)
                            textView.setBackgroundResource(R.drawable.calendar_today_bg)
                            dotView.makeInVisible()
                        }
                        selectedDate -> {
                            textView.setTextColorRes(R.color.color_primary_blue)
                            textView.setBackgroundResource(R.drawable.calendar_selected_bg)
                            dotView.makeInVisible()
                        }
                        else -> {
                            textView.setTextColorRes(R.color.color_black)
                            textView.background = null
                            dotView.isVisible = events[day.date].orEmpty().isNotEmpty()
                        }
                    }
                } else {
                    textView.makeInVisible()
                    dotView.makeInVisible()
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


    private fun selectDate(date: LocalDate) {
        if (selectedDate != date) {
            val oldDate = selectedDate
            selectedDate = date
            oldDate?.let { calendarView.notifyDateChanged(it) }
            calendarView.notifyDateChanged(date)
            updateAdapterForDate(date)
        }
    }

    private fun updateAdapterForDate(date: LocalDate) {
        eventsAdapter.events.clear()
        eventsAdapter.events.addAll(events[date].orEmpty())
        eventsAdapter.notifyDataSetChanged()
        SelectedDateText.text = selectionFormatter.format(date)
    }






}
