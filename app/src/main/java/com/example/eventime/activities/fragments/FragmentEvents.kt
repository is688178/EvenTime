package com.example.eventime.activities.fragments


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventime.R
import com.example.eventime.activities.activities.ActivityEventDetails
import com.example.eventime.activities.adapters.AdapterRecyclerViewCategories
import com.example.eventime.activities.adapters.AdapterRecyclerViewEvents
import com.example.eventime.activities.beans.*
import com.example.eventime.activities.listeners.ClickListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.item_category.view.*
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.startActivity
import kotlin.collections.ArrayList

class FragmentEvents : Fragment(), TabLayout.OnTabSelectedListener, ClickListener {

    private lateinit var tabLayout: TabLayout
    private lateinit var rvCategories: RecyclerView
    private lateinit var rvEvents: RecyclerView
    private lateinit var fabAddEvent: FloatingActionButton

    private var categories = ArrayList<String>()
    private lateinit var selectedCategory: String
    private var selectedCategoryView: View? = null

    private lateinit var container: Context

    companion object {
        const val TODAY_TAB = 0
        const val ON_WEEK_TAB = 1
        const val WEEKEND_TAB = 2
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_events, container, false)

        this.container = container!!.context
        bindViews(view)
        setupTabLayout()
        setupCategoriesRecyclerView()
        setupEventsRecyclerView()

        return view
    }

    private fun bindViews(view: View) {
        tabLayout = view.find(R.id.fragment_events_tab_layout)
        rvCategories = view.find(R.id.fragment_events_rv_categories)
        rvEvents = view.find(R.id.fragment_events_rv_events)
        fabAddEvent = view.find(R.id.fragment_events_fab_add_event)
    }

    private fun setupTabLayout() {
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_title_events_today)))
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_title_events_on_week)))
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_title_events_weekend)))
        tabLayout.addOnTabSelectedListener(this)
    }

    private fun setupCategoriesRecyclerView() {
        //FETCH CATEGORIES??? --- STATIC CATEGORIES???

        categories = ArrayList<String>()
        categories.add("Todos")
        categories.add("Musica")
        categories.add("Deportes")
        categories.add("Gastronomía")
        categories.add("Cultura")
        categories.add("Social")
        categories.add("Familia")

        selectedCategory = categories[0]

        rvCategories.adapter = AdapterRecyclerViewCategories(categories, this)
        val lmCategories = LinearLayoutManager(container)
        lmCategories.orientation = LinearLayoutManager.HORIZONTAL
        rvCategories.layoutManager = lmCategories
    }

    private fun setupEventsRecyclerView() {
        //FETCH EVENTS

        val hours = ArrayList<String>()
        hours.add("10:00 pm")
        val dates = ArrayList<EventDate>()
        dates.add(EventDate("12/12/2019", hours))

        val event = Event("Aerosmith concert", Location("Auditorio Telmex"), "", "",
            dates, ArrayList(), "")

        val events = ArrayList<Event>()
        events.add(event)
        events.add(event)
        events.add(event)
        events.add(event)
        events.add(event)

        rvEvents.adapter = AdapterRecyclerViewEvents(events, this)
        rvEvents.layoutManager = LinearLayoutManager(container)
    }

    //LISTENERS

    override fun onClick(view: View, index: Int) {
        when (view.parent) {
            rvCategories -> {
                if (selectedCategory != categories[index]) {
                    selectedCategory = categories[index]
                    if (selectedCategoryView != null) {
                        selectedCategoryView?.item_category_iv_category_icon?.background =
                            container.getDrawable(R.drawable.background_dark_gray_circle_category)
                    }
                    view.item_category_iv_category_icon.background =
                        container.getDrawable(R.drawable.background_white_circle_category)
                    selectedCategoryView = view
                }
            } rvEvents -> {
                startActivity<ActivityEventDetails>()
                //Toast.makeText(container, "Event!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        when (tab!!.position) {
            TODAY_TAB -> {

            }
            ON_WEEK_TAB -> {

            }
            WEEKEND_TAB -> {

            }
        }
    }
    override fun onTabReselected(tab: TabLayout.Tab?) {}
    override fun onTabUnselected(tab: TabLayout.Tab?) {}
}
