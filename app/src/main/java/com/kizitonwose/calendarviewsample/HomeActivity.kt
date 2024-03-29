package com.kizitonwose.calendarviewsample

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventime.R
import kotlinx.android.synthetic.main.home_activity.*


class HomeActivity : AppCompatActivity() {

    private val examplesAdapter = HomeOptionsAdapter {
        val instance = when (it.titleRes) {
            R.string.example_3_title -> Example3Fragment()
            else -> throw IllegalArgumentException()
        }
        supportFragmentManager.beginTransaction()
            .run {
                return@run setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
            }
            .add(R.id.homeContainer, instance, instance.javaClass.simpleName)
            .addToBackStack(instance.javaClass.simpleName)
            .commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)
        setSupportActionBar(homeToolbar)
        //examplesRv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        examplesRv.adapter = examplesAdapter
        examplesRv.addItemDecoration(DividerItemDecoration(this, RecyclerView.VERTICAL))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> onBackPressed().let { true }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
