package com.example.eventime.activities.fragments


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ViewAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.eventime.R
import com.example.eventime.activities.activities.create_public_event.ActivityCreatePublicEvent
import com.example.eventime.activities.activities.eventDetails.ActivityEventDetails
import com.example.eventime.activities.activities.main.ContractMain
import com.example.eventime.activities.activities.main.PresenterMain
import com.example.eventime.activities.adapters.AdapterRecyclerViewCategories
import com.example.eventime.activities.adapters.AdapterRecyclerViewEvents
import com.example.eventime.activities.beans.*
import com.example.eventime.activities.listeners.ClickListener
import com.example.eventime.activities.utils.DateHourUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.intentFor
import org.jetbrains.anko.support.v4.startActivity
import kotlin.collections.ArrayList

class FragmentEvents : Fragment(), TabLayout.OnTabSelectedListener, ClickListener, View.OnClickListener,
        ContractMain.View, SwipeRefreshLayout.OnRefreshListener {

    private lateinit var tabLayout: TabLayout
    private lateinit var rvCategories: RecyclerView
    private lateinit var vaSwitcher: ViewAnimator
    private lateinit var swipeToRefresh: SwipeRefreshLayout
    private lateinit var rvEvents: RecyclerView
    private lateinit var fabAddEvent: FloatingActionButton

    private var categories = ArrayList<Category>()
    private lateinit var categoriesAdapter: AdapterRecyclerViewCategories
    private lateinit var selectedCategory: Category

    private lateinit var adapterRvEvents: AdapterRecyclerViewEvents
    private lateinit var containerContext: Context

    companion object {
        const val TODAY_TAB = 0
        const val THIS_WEEK_TAB = 1
        const val NEXT_WEEK_TAB = 2

        //const val SHOW_PROGRESS_BAR = 0
        const val SHOW_EVENTS = 1
        const val SHOW_NO_EVENTS_FOUNT = 2
    }

    private var events = ArrayList<Event>()
    private val presenter = PresenterMain(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_events, container, false)

        this.containerContext = container!!.context
        bindViews(view)
        setupTabLayout()
        setListeners()

        /*ParseUser.logInInBackground("uriel", "uriel"){ parseUser, e ->
            if (e == null) {// Hooray! The user is logged in.
                Toast.makeText(containerContext, "Hecho", Toast.LENGTH_LONG).show()
                val parseACL = ParseACL(parseUser)
                parseACL.publicReadAccess = true
                parseUser.setACL(parseACL)
            } else {
                // Signup failed. Look at the ParseException to see what happened.
            }
        }*/

        presenter.fetchCategories(containerContext)
        presenter.fetchEvents()

        return view
    }

    private fun bindViews(view: View) {
        tabLayout = view.find(R.id.fragment_events_tab_layout)
        rvCategories = view.find(R.id.fragment_events_rv_categories)
        vaSwitcher = view.find(R.id.fragment_events_va_switcher)
        swipeToRefresh = view.find(R.id.fragment_events_swipe)
        rvEvents = view.find(R.id.fragment_events_rv_events)
        fabAddEvent = view.find(R.id.fragment_events_fab_add_event)
    }

    private fun setListeners() {
        swipeToRefresh.setOnRefreshListener(this)
        fabAddEvent.setOnClickListener(this)
    }

    private fun setupTabLayout() {
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_title_events_today)))
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_title_events_this_week)))
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_title_events_next_week)))
        tabLayout.addOnTabSelectedListener(this)
    }

    private fun setupCategoriesRecyclerView() {
        categoriesAdapter = AdapterRecyclerViewCategories(categories, this)
        rvCategories.adapter = categoriesAdapter
        val lmCategories = LinearLayoutManager(containerContext)
        lmCategories.orientation = LinearLayoutManager.HORIZONTAL
        rvCategories.layoutManager = lmCategories
    }

    private fun setupEventsRecyclerView() {
        adapterRvEvents = AdapterRecyclerViewEvents(events, this, false)
        rvEvents.adapter = adapterRvEvents
        rvEvents.layoutManager = LinearLayoutManager(containerContext)
    }

    //VIEW INTERFACE IMPLEMENTATION


    override fun fillCategories(categories: ArrayList<Category>) {
        this.categories = categories
        setupCategoriesRecyclerView()
        selectedCategory = this.categories[0]
        this.categories[0].selected = true
    }

    override fun showEvents(events: ArrayList<Event>) {
        this.events = events
        setupEventsRecyclerView()
        vaSwitcher.displayedChild = SHOW_EVENTS
        onTabSelected(tabLayout.getTabAt(0))
        swipeToRefresh.isRefreshing = false
    }

    override fun showNoEventsFound() {
        vaSwitcher.displayedChild = SHOW_NO_EVENTS_FOUNT
    }

    //LISTENERS

    override fun onClick(view: View, index: Int) {
        when (view.parent) {
            rvCategories -> {
                if (selectedCategory != categories[index]) {
                    categories[categories.indexOf(selectedCategory)].selected = false
                    categories[index].selected = true
                    selectedCategory = categories[index]
                    categoriesAdapter.notifyDataSetChanged()

                    if (adapterRvEvents.filterEventsCategory(selectedCategory)) {
                        vaSwitcher.displayedChild = SHOW_EVENTS
                    } else {
                        vaSwitcher.displayedChild = SHOW_NO_EVENTS_FOUNT
                    }
                }
            } rvEvents -> {
                startActivity(intentFor<ActivityEventDetails>("eventId" to events[index].eventId,
                    "eventDateId" to events[index].dates[0].eventDateId,
                    "date" to DateHourUtils.formatDateToShowFormat(events[index].dates[0].date),
                    "hour" to DateHourUtils.formatHourToString(events[index].dates[0].date)))
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
        val dateFilter = when (tab!!.position) {
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
        }
    }
    override fun onTabReselected(tab: TabLayout.Tab?) {}
    override fun onTabUnselected(tab: TabLayout.Tab?) {}

    override fun onRefresh() {
        presenter.fetchEvents()
    }
}