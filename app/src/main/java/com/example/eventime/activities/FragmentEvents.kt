package com.example.eventime.activities


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.eventime.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.find


class FragmentEvents : Fragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var rvCategories: RecyclerView
    private lateinit var viewPager: ViewPager
    private lateinit var fabAddEvent: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_events, container, false)

        bindViews(view)
        setupViewPager()
        setupCategoriesRecyclerView()

        return view
    }

    private fun bindViews(view: View) {
        tabLayout = view.find(R.id.fragment_events_tab_layout)
        rvCategories = view.find(R.id.fragment_events_rv_categories)
        viewPager = view.find(R.id.fragment_events_view_pager)
        fabAddEvent = view.find(R.id.fragment_events_fab_add_event)
    }

    private fun setupViewPager() {
        val fragments = ArrayList<Fragment>()
        fragments.add(FragmentEventsAux())
        fragments.add(FragmentEventsAux())
        fragments.add(FragmentEventsAux())

        val viewPagerAdapter = ViewPagerAdapterFragmentEvents(activity!!.supportFragmentManager)
        viewPagerAdapter.addFragment(fragments[0], getString(R.string.tab_title_events_today))
        viewPagerAdapter.addFragment(fragments[1], getString(R.string.tab_title_events_on_week))
        viewPagerAdapter.addFragment(fragments[2], getString(R.string.tab_title_events_weekend))
        viewPager.adapter = viewPagerAdapter

        tabLayout.setupWithViewPager(viewPager)
    }

    private fun setupCategoriesRecyclerView() {
        //FETCH CATEGORIES??? --- STATIC CATEGORIES???

        val categories = ArrayList<String>()
        categories.add("Todos")
        categories.add("Musica")
        categories.add("Deportes")
        categories.add("Gastronomía")
        categories.add("Cultura")
        categories.add("Social")
        categories.add("Familia")

        rvCategories.adapter = AdapterRecyclerViewCategories(categories)
        val lmCategories = LinearLayoutManager(activity)
        lmCategories.orientation = LinearLayoutManager.HORIZONTAL
        rvCategories.layoutManager = lmCategories
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        //SAVE SELECTED TAB
    }
}
