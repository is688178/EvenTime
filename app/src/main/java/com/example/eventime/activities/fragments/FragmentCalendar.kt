package com.example.eventime.activities.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.eventime.R
import com.example.eventime.activities.adapters.CategoryViewHolder
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.calendar_day_layout.view.*
import kotlinx.android.synthetic.main.calendar_event_item_view.*
import kotlinx.android.synthetic.main.fragment_calendar.*
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.temporal.WeekFields
import com.example.eventime.activities.activities.create_public_event.ActivityCreatePrivateEvent
import kotlinx.android.synthetic.main.fragment_calendar_event.view.*
import org.jetbrains.anko.support.v4.startActivity
import java.util.*

data class Event(val id: String, val text: String, val date: LocalDate)


class AdapterRecyclerViewCalendar(val onClick: (Event) -> Unit) :
    RecyclerView.Adapter<AdapterRecyclerViewCalendar.CalendarEventsViewHolder>() {

    val events = mutableListOf<Event>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarEventsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.calendar_event_item_view, parent, false)

        return CategoryViewHolder(view, clickListener)

       // return CalendarEventsViewHolder(parent.inflate(R.layout.calendar_event_item_view))
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

        fun bind(event: Event) {
            itemEventText.text = event.text
        }
    }

}



class FragmentCalendar : Fragment(), View.OnClickListener  {
    private lateinit var container: Context
    private val events = mutableMapOf<LocalDate, List<Event>>()
    private var selectedDate: LocalDate? = null

    private val eventsAdapter = AdapterRecyclerViewCalendar {
        AlertDialog.Builder(requireContext())
            .setMessage("Delete this event?")
            .setPositiveButton("Delete") { _, _ ->
                deleteEvent(it)
            }
            .setNegativeButton("Close", null)
            .show()
    }

    override fun onClick(p0: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar_event, container, false)
        this.container = container!!.context

        return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentMonth = YearMonth.now()
        val firstMonth = currentMonth.minusMonths(10)
        val lastMonth = currentMonth.plusMonths(10)
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek


        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val textView = view.calendarDayText
            val dotView = view.calendarDotView

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
                container.textView.text = day.date.dayOfMonth.toString()
                if (day.owner == DayOwner.THIS_MONTH) {
                    container.textView.setTextColor(Color.WHITE)
                } else {
                    container.textView.setTextColor(Color.GRAY)
                }
            }
        }


    }


    private fun deleteEvent(event: Event) {
        val date = event.date
        events[date] = events[date].orEmpty().minus(event)
        updateAdapterForDate(date)
    }

    private fun updateAdapterForDate(date: LocalDate) {
        eventsAdapter.events.clear()
        eventsAdapter.events.addAll(events[date].orEmpty())
        eventsAdapter.notifyDataSetChanged()
        //exThreeSelectedDateText.text = selectionFormatter.format(date)
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



}














    /*private lateinit var container: Context
    private lateinit var  msg: String
    private lateinit var beginTime: Calendar
    private lateinit var endTime: Calendar

    companion object {
        const val CREATE_EVENT = 1000
    }

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(com.example.eventime.R.layout.fragment_calendar, container, false)
        this.container = container!!.context
        view.calendar.setOnDateChangeListener{ _, year, month, dayOfMonth ->
            // Note that months are indexed from 0. So, 0 means January, 1 means february, 2 means march etc.
            msg = ("$dayOfMonth-"+(month + 1)+"-$year")
            beginTime = Calendar.getInstance()
            beginTime.set(year, month, dayOfMonth, 12, 0)

            endTime = Calendar.getInstance()
            endTime.set(year, month, dayOfMonth, 13, 0)

        }

       /*if (ContextCompat.checkSelfPermission(this.container, Manifest.permission.WRITE_CALENDAR)
            != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ActivityMain(),
                    Manifest.permission.WRITE_CALENDAR)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(ActivityMain(),
                    arrayOf(Manifest.permission.WRITE_CALENDAR),
                    MY_PERMISSIONS_REQUEST_WRITE_CALENCAR)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
                view.fragment_calendar_add_event.setOnClickListener {

                    val intent = Intent(Intent.ACTION_INSERT)
                        .setData(CalendarContract.Events.CONTENT_URI)
                        .putExtra(CalendarContract.Events.TITLE, "New Event")
                        .putExtra(CalendarContract.Events.EVENT_LOCATION, "Location")
                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, System.currentTimeMillis()) //beginTime)
                        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, System.currentTimeMillis() + (60*60*1000)) //endTime)//
                    startActivityForResult(intent, CREATE_EVENT)
                }
        }*/
            /*            val intent = Intent(Intent.ACTION_INSERT)
                            .setData(CalendarContract.Events.CONTENT_URI)
                            .putExtra(CalendarContract.Events.TITLE, "New Event")
                            .putExtra(CalendarContract.Events.EVENT_LOCATION, "Location")
                            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime) //System.currentTimeMillis())
                            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime)//System.currentTimeMillis() + (60*60*1000))*/

        view.fragment_calendar_add_event.setOnClickListener {

            val intent = Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, "New Event")
                .putExtra(CalendarContract.Events.EVENT_LOCATION, "Location")
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, System.currentTimeMillis()) //beginTime)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, System.currentTimeMillis() + (60*60*1000)) //endTime)//
            startActivityForResult(intent, CREATE_EVENT)
            startActivity<ActivityCreatePrivateEvent>()
        }

        return view
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


/*if (ContextCompat.checkSelfPermission(this.container, Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this.container,
                        Manifest.permission.WRITE_CALENDAR)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(this.container,
                        arrayOf(Manifest.permission.WRITE_CALENDAR),
                        MY_PERMISSIONS_REQUEST_WRITE_CALENCAR)

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                val intent = Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.Events.TITLE, "New Event")
                    .putExtra(CalendarContract.Events.EVENT_LOCATION, "Location")
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime) //System.currentTimeMillis())
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime)//System.currentTimeMillis() + (60*60*1000))

                startActivityForResult(intent, CREATE_EVENT)
            }

            /*

/*val beginTime = Calendar.getInstance()
       beginTime.set(2012, 0, 19, 7, 30)

       val endTime = Calendar.getInstance()
       endTime.set(2012, 0, 19, 8, 30)

       val intent = Intent(Intent.ACTION_INSERT)
           .setData(Events.CONTENT_URI)
           .putExtra(
               CalendarContract.EXTRA_EVENT_BEGIN_TIME,
               beginTime.getTimeInMillis()
           )
           .putExtra(
               CalendarContract.EXTRA_EVENT_END_TIME,
               endTime.getTimeInMillis()
           )
           .putExtra(Events.TITLE, "Yoga")
           .putExtra(Events.DESCRIPTION, "Group class")
           .putExtra(Events.EVENT_LOCATION, "The gym")
           .putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY)
           .putExtra(Intent.EXTRA_EMAIL, "rowan@example.com,trevor@example.com")

       startActivity(intent)*/

        view.fragment_calendar_add_event.setOnClickListener {

            val intent = Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, "New Event")
                .putExtra(CalendarContract.Events.EVENT_LOCATION, "Location")
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime) //System.currentTimeMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime)//System.currentTimeMillis() + (60*60*1000))

            startActivity(intent)
        }
 */