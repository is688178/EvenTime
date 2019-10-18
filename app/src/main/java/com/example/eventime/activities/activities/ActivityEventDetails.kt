package com.example.eventime.activities.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventime.R
import com.example.eventime.activities.adapters.AdapterRecyclerViewComments
import com.example.eventime.activities.adapters.AdapterSimpleItemHorizontal
import com.example.eventime.activities.beans.Comment
import com.example.eventime.activities.beans.Person
import com.example.eventime.activities.listeners.ClickListener
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.jetbrains.anko.find

class ActivityEventDetails : AppCompatActivity(), ClickListener {

    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var collapsingToolbar: CollapsingToolbarLayout
    private lateinit var tvDescription: TextView
    private lateinit var tvLocation: TextView
    private lateinit var rvDates: RecyclerView
    private lateinit var rvHours: RecyclerView
    private lateinit var rvComments: RecyclerView

    private lateinit var selectedDate: String
    private var selectedDateView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_details)

        bindViews()
        setupToolbar()
        setupDatesRecyclerView()
        setupHoursRecyclerView()
        setupCommentsRecyclerView()
    }

    private fun bindViews() {
        collapsingToolbar = find(R.id.activity_event_details_collapsing_toolbar)
        toolbar = find(R.id.activity_event_details_toolbar)
        tvDescription = find(R.id.activity_event_details_tv_event_description)
        tvLocation = find(R.id.activity_event_details_tv_event_location)
        rvDates = find(R.id.activity_event_details_rv_dates)
        rvHours = find(R.id.activity_event_details_rv_hours)
        rvComments = find(R.id.activity_event_details_rv_comments)
    }

    private fun setupToolbar() {
        collapsingToolbar.setCollapsedTitleTextColor(resources.getColor(R.color.colorWhite))
        collapsingToolbar.setExpandedTitleColor(resources.getColor(R.color.colorWhite))
        //collapsingToolbar.setStatusBarScrimColor(resources.getColor(R.color.color_black))
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Aerosmith concert"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupDatesRecyclerView() {
        val dates = ArrayList<String>()
        dates.add("12/12/2019")
        dates.add("12/12/2019")
        dates.add("12/12/2019")
        dates.add("12/12/2019")
        dates.add("12/12/2019")

        rvDates.adapter = AdapterSimpleItemHorizontal(dates, this)
        val lmDates = LinearLayoutManager(this)
        lmDates.orientation = LinearLayoutManager.HORIZONTAL
        rvDates.layoutManager = lmDates
    }

    private fun setupHoursRecyclerView() {
        val hours = ArrayList<String>()
        hours.add("10:00 pm")
        hours.add("10:00 pm")
        hours.add("10:00 pm")

        rvHours.adapter = AdapterSimpleItemHorizontal(hours, this)
        val lmHours = LinearLayoutManager(this)
        lmHours.orientation = LinearLayoutManager.HORIZONTAL
        rvHours.layoutManager = lmHours
    }

    private fun setupCommentsRecyclerView() {
        val comment = Comment(Person("Antonio", "Santana", ""), 3,
            "12/04/2019", "")
        val comments = ArrayList<Comment>()
        comments.add(comment)
        comments.add(comment)
        comments.add(comment)
        comments.add(comment)
        comments.add(comment)

        rvComments.adapter = AdapterRecyclerViewComments(comments, this)
        rvComments.layoutManager = LinearLayoutManager(this)
    }

    private fun showAlertDialogConfimation(index: Int) {
        val alertDialog = MaterialAlertDialogBuilder(this)
            .setView(R.layout.alert_dialog_confirm_event_assistance)
            .show()

        /*alertDialog.find<TextView>(R.id.alert_dialog_confirm_event_assistance_tv_event_name).text = "Aerosmith concert"
        alertDialog.find<TextView>(R.id.alert_dialog_confirm_event_assistance_tv_event_date).text = "12/12/2019"
        alertDialog.find<TextView>(R.id.alert_dialog_confirm_event_assistance_tv_event_hour).text = "10:00 pm"*/
    }

    //LISTENERS
    override fun onClick(view: View, index: Int) {
        when (view.parent) {
            rvDates -> {
                Toast.makeText(this, "DATES LISTENER", Toast.LENGTH_LONG).show()
            } rvHours ->{
                showAlertDialogConfimation(index)
            }
        }
    }
}
