package com.example.eventime.activities.activities.eventDetails

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.example.eventime.activities.adapters.AdapterRecyclerViewComments
import com.example.eventime.activities.beans.*
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.jetbrains.anko.find
import com.example.eventime.R
import com.example.eventime.activities.utils.DateHourUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*
import kotlin.collections.ArrayList

class ActivityEventDetails : AppCompatActivity(), ContractEventDetails.View, View.OnClickListener {

    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var collapsingToolbar: CollapsingToolbarLayout
    private lateinit var tvDescription: TextView
    private lateinit var tvLocation: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvHour: TextView
    private lateinit var rvComments: RecyclerView
    private lateinit var tvNoComments: TextView
    private lateinit var fabAttend: FloatingActionButton

    private val presenter = PresenterEventDetails(this)

    private lateinit var event: Event


    private lateinit var eventId: String
    private var eventDateId = ""
    private lateinit var eventDate : Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_details)

        bindViews()
        setListeners()
        val extras = intent.extras
        if (extras != null) {
            eventId = extras.getString("eventId")!!
            eventDateId = extras.getString("eventDateId")!!
            eventDate = DateHourUtils.createDateFromString(extras.getString("date")!!)
            val eventHour = DateHourUtils.createHourFromString(extras.getString("hour")!!)



            //REMOVE LATER
            eventDate.set(Calendar.HOUR_OF_DAY, eventHour.get(Calendar.HOUR_OF_DAY))
            eventDate.set(Calendar.MINUTE, eventHour.get(Calendar.MINUTE))
            //--------------


            presenter.fetchEvent(eventId)
        }

        setupToolbar()
    }

    private fun bindViews() {
        collapsingToolbar = find(R.id.activity_event_details_collapsing_toolbar)
        toolbar = find(R.id.activity_event_details_toolbar)
        tvDescription = find(R.id.activity_event_details_tv_event_description)
        tvLocation = find(R.id.activity_event_details_tv_event_location)
        tvDate = find(R.id.activity_event_details_tv_event_date)
        tvHour = find(R.id.activity_event_details_tv_event_hour)
        rvComments = find(R.id.activity_event_details_rv_comments)
        tvNoComments = find(R.id.activity_event_details_tv_no_comments)
        fabAttend = find(R.id.activity_event_details_fab_attend_event)
    }

    private fun setListeners() {
        fabAttend.setOnClickListener(this)
    }

    private fun setupToolbar() {
        collapsingToolbar.setCollapsedTitleTextColor(ContextCompat.getColor(this, R.color.colorWhite))
        collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(this, R.color.colorWhite))
        //collapsingToolbar.setStatusBarScrimColor(resources.getColor(R.color.color_black))
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Aerosmith concert"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupCommentsRecyclerView(comments: ArrayList<Comment>) {
        rvComments.adapter = AdapterRecyclerViewComments(comments)
        rvComments.layoutManager = LinearLayoutManager(this)
    }

    private fun showAlertDialogConfimation() {
        val alertDialog = MaterialAlertDialogBuilder(this)
            .setView(R.layout.alert_dialog_confirm_event_assistance)
            .show()

        alertDialog.find<TextView>(R.id.alert_dialog_confirm_event_assistance_tv_event_name).text = event.name
        alertDialog.find<TextView>(R.id.alert_dialog_confirm_event_assistance_tv_event_date).text =
            DateHourUtils.formatDateToMonthNameShowFormat(event.dates[0].date)
        alertDialog.find<TextView>(R.id.alert_dialog_confirm_event_assistance_tv_event_hour).text =
            DateHourUtils.formatHourToShowFormat(event.dates[0].date)
    }

    //VIEW INTERFACE IMPLEMENTATION


    override fun showEvent(event: Event) {
        this.event = event
        //REMOVE LATER
        val eventDate = EventDate(
            eventDateId,
            eventDate,
            false,
            ArrayList()
        )
        event.dates.add(eventDate)
        //---------

        collapsingToolbar.title = event.name

        Glide
            .with(this)
            .load(event.parseFileImage?.url)
            .into(object : CustomViewTarget<CollapsingToolbarLayout, Drawable>(collapsingToolbar) {
                override fun onLoadFailed(errorDrawable: Drawable?) {}
                override fun onResourceCleared(placeholder: Drawable?) {}
                override fun onResourceReady(resource: Drawable,transition: Transition<in Drawable>?) {
                    collapsingToolbar.background = resource
                }
            })

        tvDescription.text = event.description
        tvLocation.text = event.location!!.name

        val date = event.dates[0].date
        tvDate.text = DateHourUtils.formatDateToMonthNameShowFormat(date)
        tvHour.text = DateHourUtils.formatHourToShowFormat(date)
        if (event.comments != null && event.comments.isNotEmpty()) {
            setupCommentsRecyclerView(event.comments)
        } else {
            rvComments.visibility = View.GONE
            tvNoComments.visibility = View.VISIBLE
        }
    }


    //LISTENERS

    override fun onClick(view: View?) {
        when(view?.id) {
            fabAttend.id -> {
                showAlertDialogConfimation()
            }
        }
    }
}