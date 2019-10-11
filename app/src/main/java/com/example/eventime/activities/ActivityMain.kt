package com.example.eventime.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.example.eventime.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.jetbrains.anko.find

class ActivityMain: AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var bttmNav: BottomNavigationView

    private var fragments = ArrayList<Fragment>()
    private var currentFragment = EVENTS_FRAGMENT

    companion object {
        const val EVENTS_FRAGMENT = 0
        const val AGENDA_FRAGMENT = 1
        const val SUGESTED_EVENTS_FRAGMENT = 2
        const val PROFILE_FRAGMENT = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bttmNav = find(R.id.activity_main_bttm_nav)
        bttmNav.setOnNavigationItemSelectedListener(this)

        fragments.add(FragmentEvents())
        //add fragments to array

        setFragment()
    }

    private fun setFragment(/*item: MenuItem*/) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.activity_main_fl_content, fragments[currentFragment])
            .commit()

        //item.isChecked = true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        currentFragment = when (item.itemId) {
            R.id.action_main_show_events -> {
                EVENTS_FRAGMENT
            } /*R.id.action_main_show_agenda -> {
                AGENDA_FRAGMENT
            } R.id.action_main_show_sugested_events -> {
                SUGESTED_EVENTS_FRAGMENT
            } R.id.action_main_show_profile -> {
                PROFILE_FRAGMENT
            } */else -> {
                EVENTS_FRAGMENT
            }
        }

        setFragment()//item)

        return true
    }
}
