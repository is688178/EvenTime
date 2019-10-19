package com.example.eventime.activities.fragments


import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ViewAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventime.R
import com.example.eventime.activities.activities.ActivityCreatePublicEvent
import com.example.eventime.activities.activities.ActivityEventDetails
import com.example.eventime.activities.adapters.AdapterRecyclerViewCategories
import com.example.eventime.activities.adapters.AdapterRecyclerViewEvents
import com.example.eventime.activities.beans.*
import com.example.eventime.activities.listeners.ClickListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.item_category.view.*
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.intentFor
import org.jetbrains.anko.support.v4.startActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList

class FragmentEvents : Fragment(), TabLayout.OnTabSelectedListener, ClickListener, View.OnClickListener {

    private lateinit var tabLayout: TabLayout
    private lateinit var rvCategories: RecyclerView
    private lateinit var vaSwitcher: ViewAnimator
    private lateinit var rvEvents: RecyclerView
    private lateinit var fabAddEvent: FloatingActionButton

    private var categories = ArrayList<String>()
    private lateinit var selectedCategory: String
    private var selectedCategoryView: View? = null

    private lateinit var adapterRvEvents: AdapterRecyclerViewEvents

    private lateinit var containerContext: Context

    companion object {
        const val TODAY_TAB = 0
        const val THIS_WEEK_TAB = 1
        const val NEXT_WEEK_TAB = 2

        const val SHOW_EVENTS = 0
        const val SHOW_NO_EVENTS_FOUNT = 1
    }

    private val events = ArrayList<Event>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_events, container, false)

        this.containerContext = container!!.context
        bindViews(view)
        setupTabLayout()
        setupCategoriesRecyclerView()
        setupEventsRecyclerView()

        fabAddEvent.setOnClickListener(this)

        //TODAY FILTER
        onTabSelected(tabLayout.getTabAt(0))

        return view
    }

    private fun bindViews(view: View) {
        tabLayout = view.find(R.id.fragment_events_tab_layout)
        rvCategories = view.find(R.id.fragment_events_rv_categories)
        vaSwitcher = view.find(R.id.fragment_events_va_switcher)
        rvEvents = view.find(R.id.fragment_events_rv_events)
        fabAddEvent = view.find(R.id.fragment_events_fab_add_event)
    }

    private fun setupTabLayout() {
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_title_events_today)))
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_title_events_this_week)))
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_title_events_next_week)))
        tabLayout.addOnTabSelectedListener(this)
    }

    private fun setupCategoriesRecyclerView() {
        //FETCH CATEGORIES??? --- STATIC CATEGORIES???

        categories = ArrayList<String>()
        categories.add("Todos")
        categories.add("Musica")
        categories.add("Deportes")
        categories.add("Gastronomia")
        categories.add("Cultural")
        categories.add("Social")
        categories.add("Familia")

        selectedCategory = categories[0]

        rvCategories.adapter = AdapterRecyclerViewCategories(categories, this)
        val lmCategories = LinearLayoutManager(containerContext)
        lmCategories.orientation = LinearLayoutManager.HORIZONTAL
        rvCategories.layoutManager = lmCategories
    }

    @SuppressLint("NewApi")
    private fun setupEventsRecyclerView() {
        //FETCH EVENTS

        val hours = ArrayList<String>()
        hours.add("10:00 pm")
        val dates = ArrayList<EventDate>()
        dates.add(EventDate((LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).toString(), hours))

        val event = Event("Aerosmith concert", Location("Auditorio Telmex"), R.drawable.concert, "Es un concierto",
            dates, ArrayList(), "Musica")

        val event2 = Event("Exposición de arte", Location("Casa de la cultura"), R.drawable.concert, "Exposición de pinturas",
            dates, ArrayList(), "Cultural")

        dates.add(EventDate("24/10/2019", hours))
        val event3 = Event("Feria de la birria", Location("Centro"), R.drawable.concert2, "Birria de la buena!",
            dates, ArrayList(), "Gastronomia")

        //events = ArrayList<Event>()
        events.add(event)
        events.add(event)
        events.add(event2)
        events.add(event3)
        events.add(event3)

        adapterRvEvents = AdapterRecyclerViewEvents(events, this, false, null)
        rvEvents.adapter = adapterRvEvents
        rvEvents.layoutManager = LinearLayoutManager(containerContext)
    }

    //LISTENERS

    override fun onClick(view: View, index: Int) {
        when (view.parent) {
            rvCategories -> {
                if (selectedCategory != categories[index]) {
                    selectedCategory = categories[index]
                    if (selectedCategoryView != null) {
                        selectedCategoryView?.item_category_iv_category_icon?.background =
                            containerContext.getDrawable(R.drawable.background_dark_gray_circle_category)
                    }
                    view.item_category_iv_category_icon.background =
                        containerContext.getDrawable(R.drawable.background_white_circle_category)
                    selectedCategoryView = view

                    /*if (adapterRvEvents.filterEventsCategory(selectedCategory)) {
                        vaSwitcher.displayedChild = SHOW_EVENTS
                    } else {
                        vaSwitcher.displayedChild = SHOW_NO_EVENTS_FOUNT
                    }*/
                }
            } rvEvents -> {
                startActivity(intentFor<ActivityEventDetails>("eventName" to events[index].name))
                //Toast.makeText(containerContext, "Event!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            fabAddEvent.id -> {
                startActivity<ActivityCreatePublicEvent>()
            }
        }
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        /*val dateFilter = when (tab!!.position) {
            TODAY_TAB -> {
                AdapterRecyclerViewEvents.TODAY_FILTER
            } THIS_WEEK_TAB -> {
                AdapterRecyclerViewEvents.THIS_WEEK_FILTER
            } NEXT_WEEK_TAB -> {
                AdapterRecyclerViewEvents.NEXT_WEEK_FILTER
            } else -> {
                AdapterRecyclerViewEvents.TODAY_FILTER
            }
        }
        if (adapterRvEvents.filterEventsDate(dateFilter)) {
            vaSwitcher.displayedChild = SHOW_EVENTS
        } else {
            vaSwitcher.displayedChild = SHOW_NO_EVENTS_FOUNT
        }*/
    }
    override fun onTabReselected(tab: TabLayout.Tab?) {}
    override fun onTabUnselected(tab: TabLayout.Tab?) {}
}
