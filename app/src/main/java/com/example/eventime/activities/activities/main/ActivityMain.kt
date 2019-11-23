package com.example.eventime.activities.activities.main

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.example.eventime.R
import com.example.eventime.activities.activities.ActivityLogin
import com.example.eventime.activities.config.SESSION_ID_KEY
import com.example.eventime.activities.config.SHARED_PREFERENCES
import com.example.eventime.activities.fragments.FragmentCalendar
import com.example.eventime.activities.fragments.FragmentEvents
import com.example.eventime.activities.fragments.FragmentProfile
import com.example.eventime.activities.fragments.FragmentSuggestedEvents
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.parse.ParseUser
import org.jetbrains.anko.find
import org.jetbrains.anko.startActivity
import kotlin.collections.ArrayList

class ActivityMain : AppCompatActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener,
    LogoutListener {

    private lateinit var bttmNav: BottomNavigationView

    private var fragments = ArrayList<Fragment>()
    private var currentFragment =
        EVENTS_FRAGMENT

    companion object {
        const val EVENTS_FRAGMENT = 0
        const val AGENDA_FRAGMENT = 1
        const val SUGESTED_EVENTS_FRAGMENT = 2
        const val PROFILE_FRAGMENT = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportActionBar?.hide() //hide the title bar
        setContentView(R.layout.activity_main)

        bttmNav = find(R.id.activity_main_bttm_nav)
        bttmNav.setOnNavigationItemSelectedListener(this)

        fragments.add(FragmentEvents())
        fragments.add(FragmentCalendar())
        fragments.add(FragmentSuggestedEvents())
        fragments.add(FragmentProfile())
        //add fragments to array

        setFragment()

        /*val date = Calendar.getInstance()
        date.set(2018, 11, 21)
        val eventObj = ParseObject("EventDate")
        eventObj.put("date", date.time)
        eventObj.put("startDate", true)
        eventObj.saveInBackground()*/

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
            }
            R.id.action_main_show_agenda -> {
                AGENDA_FRAGMENT
            }
            R.id.action_main_show_sugested_events -> {
                SUGESTED_EVENTS_FRAGMENT
            }
            R.id.action_main_show_profile -> {
                PROFILE_FRAGMENT
            }
            else -> {
                EVENTS_FRAGMENT
            }
        }

        setFragment()//item)

        return true
    }

    override fun logout() {
        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(SESSION_ID_KEY, "")
        editor.apply()

        ParseUser.logOut()

        startActivity<ActivityLogin>()
        finish()
    }
}

interface LogoutListener {
    fun logout()
}