package com.kizitonwose.calendarviewsample


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventime.R
import com.example.eventime.activities.activities.create_private_event.ActivityCreatePrivateEvent
import com.example.eventime.activities.activities.eventDetails.ActivityEventDetails
import com.example.eventime.activities.adapters.AdapterRecyclerViewEvents
import com.example.eventime.activities.beans.Category
import com.example.eventime.activities.beans.EventDate
import com.example.eventime.activities.beans.Location
import com.example.eventime.activities.beans.Person
import com.example.eventime.activities.listeners.ClickListener
import com.example.eventime.activities.utils.DateHourUtils
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.calendar_day_legend.view.*
import kotlinx.android.synthetic.main.example_3_calendar_day.view.*
import kotlinx.android.synthetic.main.exmaple_3_fragment.*
import kotlinx.android.synthetic.main.home_activity.*
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.intentFor
import org.jetbrains.anko.support.v4.startActivity
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

private val Context.inputMethodManager
    get() = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

data class Event(val id: String, val text: String, val date: LocalDate)

class Example3EventsAdapter(val onClick: (Event) -> Unit) :
    RecyclerView.Adapter<Example3EventsAdapter.Example3EventsViewHolder>() {

    val events = mutableListOf<Event>()
    private lateinit var itemEventText: TextView

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Example3EventsViewHolder {
        return Example3EventsViewHolder(parent.inflate(R.layout.example_3_event_item_view))
    }

    override fun onBindViewHolder(viewHolder: Example3EventsViewHolder, position: Int) {
        viewHolder.bind(events[position])
    }

    override fun getItemCount(): Int = events.size

    inner class Example3EventsViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

        init {
            itemView.setOnClickListener {
                onClick(events[adapterPosition])
            }
        }

        fun bind(event: Event) {
            itemEventText = containerView.find(R.layout.item_event)
            itemEventText.text = event.text
        }
    }

}

class Example3Fragment : BaseFragment(), HasBackButton, ClickListener {

    private lateinit var adapterRvEvents: AdapterRecyclerViewEvents
    private lateinit var containerContext: Context
    private var eventos = ArrayList<com.example.eventime.activities.beans.Event>()
    private lateinit var mRecyclerView: RecyclerView

    private val eventsAdapter = Example3EventsAdapter {
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.example_3_dialog_delete_confirmation)
            .setPositiveButton(R.string.delete) { _, _ ->
                deleteEvent(it)
            }
            .setNegativeButton(R.string.close, null)
            .show()
    }


    private val inputDialog by lazy {
        val editText = AppCompatEditText(requireContext())
        val layout = FrameLayout(requireContext()).apply {
            val padding = dpToPx(20, requireContext())
            setPadding(padding, padding, padding, padding)
            addView(editText, FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
        }

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.example_3_input_dialog_title))
            .setView(layout)
            .setPositiveButton(R.string.save) { _, _ ->
                saveEvent(editText.text.toString())
                // Prepare EditText for reuse.
                editText.setText("")
            }
            .setNegativeButton(R.string.close, null)
            .create()
            .apply {
                setOnShowListener {
                    // Show the keyboard
                    editText.requestFocus()
                    context.inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                }
                setOnDismissListener {
                    // Hide the keyboard
                    context.inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                }
            }
    }

    override val titleRes: Int = R.string.example_3_title

    private var selectedDate: LocalDate? = null
    private val today = LocalDate.now()

    private val titleSameYearFormatter = DateTimeFormatter.ofPattern("MMMM")
    private val titleFormatter = DateTimeFormatter.ofPattern("MMM yyyy")
    private val selectionFormatter = DateTimeFormatter.ofPattern("d MMM yyyy")
    private val events = mutableMapOf<LocalDate, List<Event>>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.exmaple_3_fragment, container, false)
        this.containerContext = container!!.context
        return view

        //return inflater.inflate(R.layout.exmaple_3_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //exThreeRv.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        //exThreeRv.adapter = eventsAdapter
        //exThreeRv.addItemDecoration(
            DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)



        mRecyclerView = view.find(R.id.fragment_calendar_rv_userCreatedEvents)
        fetchUserEvents()

        val daysOfWeek = daysOfWeekFromLocale()
        val currentMonth = YearMonth.now()
        exThreeCalendar.setup(currentMonth.minusMonths(10), currentMonth.plusMonths(10), daysOfWeek.first())
        exThreeCalendar.scrollToMonth(currentMonth)

        if (savedInstanceState == null) {
            exThreeCalendar.post {
                // Show today's events initially.
                selectDate(today)
            }
        }

        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val textView = view.exThreeDayText
            val dotView = view.exThreeDotView

            init {
                view.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH) {
                        selectDate(day.date)
                    }
                }
            }
        }
        exThreeCalendar.dayBinder = object : DayBinder<DayViewContainer> {
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
                            textView.setTextColorRes(R.color.white)
                            textView.setBackgroundResource(R.drawable.example_3_today_bg)
                            dotView.makeInVisible()
                        }
                        selectedDate -> {
                            textView.setTextColorRes(R.color.black)
                            textView.setBackgroundResource(R.drawable.example_3_selected_bg)
                            dotView.makeInVisible()
                        }
                        else -> {
                            textView.setTextColorRes(R.color.white)
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

        exThreeCalendar.monthScrollListener = {
            selectDate(it.yearMonth.atDay(1))
        }

        class MonthViewContainer(view: View) : ViewContainer(view) {
            val legendLayout = view.legendLayout
        }
        exThreeCalendar.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                // Setup each header day text if we have not done that already.
                if (container.legendLayout.tag == null) {
                    container.legendLayout.tag = month.yearMonth
                    container.legendLayout.children.map { it as TextView }.forEachIndexed { index, tv ->
                        tv.text = daysOfWeek[index].name.first().toString()
                        tv.setTextColorRes(R.color.white)
                    }
                }
            }
        }

        exThreeAddButton.setOnClickListener {
            startActivity<ActivityCreatePrivateEvent>()
        }
    }


    override fun onClick(view: View, index: Int) {
        when (view.parent) {
            mRecyclerView -> {
                startActivity(
                    intentFor<ActivityEventDetails>(
                        "eventId" to eventos[index].eventId,
                        "eventDateId" to eventos[index].dates[0].eventDateId,
                        "date" to DateHourUtils.formatDateToShowFormat(eventos[index].dates[0].date),
                        "hour" to DateHourUtils.formatHourToString(eventos[index].dates[0].date)
                    )
                )
            }
        }
    }

    private fun selectDate(date: LocalDate) {
        if (selectedDate != date) {
            val oldDate = selectedDate
            selectedDate = date
            oldDate?.let { exThreeCalendar.notifyDateChanged(it) }
            exThreeCalendar.notifyDateChanged(date)
            updateAdapterForDate(date)
        }
    }

    private fun saveEvent(text: String) {
        if (text.isBlank()) {
            Toast.makeText(requireContext(), R.string.example_3_empty_input_text, Toast.LENGTH_LONG).show()
        } else {
            selectedDate?.let {
                events[it] = events[it].orEmpty().plus(Event(UUID.randomUUID().toString(), text, it))
                updateAdapterForDate(it)
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
        exThreeSelectedDateText.text = selectionFormatter.format(date)
    }

    override fun onStart() {
        super.onStart()
        //(activity as AppCompatActivity).homeToolbar.setBackgroundColor(requireContext().getColorCompat(R.color.example_3_toolbar_color))
        requireActivity().window.statusBarColor = requireContext().getColorCompat(R.color.example_3_statusbar_color)
    }

    override fun onStop() {
        super.onStop()
//        (activity as AppCompatActivity).homeToolbar.setBackgroundColor(requireContext().getColorCompat(R.color.colorPrimary))
        requireActivity().window.statusBarColor = requireContext().getColorCompat(R.color.colorPrimaryDark)
    }

    private fun fetchUserEvents() {
        //FETCH EVENT DATES
        val cal = Calendar.getInstance()
        val currentUserId = ParseUser.getCurrentUser().objectId

        val queryDate = ParseQuery.getQuery<ParseObject>("EventDate")
        queryDate.include("Event")
        queryDate.include("Event.Person")
        queryDate.include("Event.Category")
        queryDate.addAscendingOrder("date")
        queryDate.whereGreaterThanOrEqualTo("date", cal.time)
        queryDate.findInBackground { dates, e ->
            if (e == null) {
                val eventsO = java.util.ArrayList<com.example.eventime.activities.beans.Event>()

                //Aux. to avoid duplicates because of Parse
                var first = true
                var lastEventId = ""
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.YEAR, 1800)
                var lastEventDate = calendar.time

                dates.forEach { date ->
                    val event = date.getParseObject("Event")

                    if (event != null &&
                        (first
                                || ( lastEventId != event.objectId.toString() )
                                || ( lastEventId == event.objectId.toString() && lastEventDate != date.getDate("date") )) ) {

                        lastEventId = event.objectId.toString()
                        lastEventDate = date.getDate("date")!!

                        if (event["private"] == true && date.getBoolean("startDate")) {
                            val l = event.getParseGeoPoint("location")
                            val location = if (l != null) {
                                Location(
                                    event["locationName"].toString(),
                                    l.latitude, l.longitude
                                )
                            } else {
                                null
                            }

                            val imageParseFile = event.getParseFile("image")

                            val p = event.getParseUser("Person")
                            val person = if (p != null) {
                                Person(
                                    p.objectId,
                                    p["username"].toString(),
                                    "",
                                    null,
                                    p.getParseFile("image")
                                )
                            } else {
                                null
                            }
                            val c = event.getParseObject("Category")
                            val category = if (c != null) {
                                Category(
                                    c.objectId,
                                    c["name"].toString(),
                                    false,
                                    c.getParseFile("icon")
                                )
                            } else {
                                null
                            }

                            val dateO = date.getDate("date")
                            val calx = Calendar.getInstance()
                            calx.time = dateO!!
                            val datesO = java.util.ArrayList<EventDate>()
                            val eventDate = EventDate(
                                date.objectId,
                                calx,
                                false,
                                java.util.ArrayList()
                            )
                            eventDate.hours!!.add(cal)
                            datesO.add(eventDate)

                            val eventO = com.example.eventime.activities.beans.Event(
                                event.objectId,
                                event["name"].toString(),
                                location,
                                null,
                                event["description"].toString(),
                                datesO,
                                Calendar.getInstance(),
                                category,
                                false,
                                person,
                                false,
                                null,
                                Calendar.getInstance(),
                                imageParseFile,
                                event
                            )

                            if (person != null) {
                                if (person.personId == currentUserId)
                                    eventsO.add(eventO)
                            }

                        }

                    } else {
                        Log.e("EVENTS FETCH", "Error: " + e?.message)
                    }

                    first = false
                }

                eventos = eventsO
                setupEventsRecyclerView()
            } else {
                Log.e("EVENTS FETCH", "Error: " + e.message!!)
            }
        }
    }



    private fun setupEventsRecyclerView() {
        adapterRvEvents = AdapterRecyclerViewEvents(eventos, this, false)
        mRecyclerView.adapter = adapterRvEvents
        mRecyclerView.layoutManager = LinearLayoutManager(containerContext)
    }
}
