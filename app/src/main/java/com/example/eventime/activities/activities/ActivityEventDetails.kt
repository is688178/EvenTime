package com.example.eventime.activities.activities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventime.activities.adapters.AdapterRecyclerViewComments
import com.example.eventime.activities.adapters.AdapterSimpleItemHorizontal
import com.example.eventime.activities.beans.*
import com.example.eventime.activities.listeners.ClickListener
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.jetbrains.anko.find
import android.graphics.drawable.Drawable
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.example.eventime.R

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

    private lateinit var event: Event
    private lateinit var dates: ArrayList<EventDate>
    private lateinit var hours: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_details)

        bindViews()
        val extras = intent.extras
        if (extras != null) {
            val event = extras.getString("eventName")!!
            setValues(event)
        }

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

    private fun setValues(eventName: String) {
        val hours = ArrayList<String>()
        hours.add("10:00 pm")
        val dates = ArrayList<EventDate>()
        dates.add(EventDate("12/12/2019", hours))

        val event1 = Event("Aerosmith concert", Location("Auditorio Telmex"), R.drawable.concert, "Es un concierto",
            dates, ArrayList(), "Musica")

        val event2 = Event("Exposición de arte", Location("Casa de la cultura"), R.drawable.concert, "Exposición de pinturas",
            dates, ArrayList(), "Cultural")

        val event3 = Event("Feria de la birria", Location("Centro"), R.drawable.concert2, "Birria de la buena!",
            dates, ArrayList(), "Gastronomia")

        this.event = when (eventName) {
            event1.name -> {
                event1
            } event2.name-> {
                event2
            } else -> {
                event3
            }
        }

        collapsingToolbar.title = event.name

        //collapsingToolbar.background = drawableFromURL(event.image)
        collapsingToolbar.background = getDrawable(event.image)
        tvDescription.text = event.description
        tvLocation.text = event.location.name

        this.dates = dates
        this.hours = hours
    }
/*
    @Throws(java.net.MalformedURLException::class, java.io.IOException::class)
    fun drawableFromURL(url: String): Drawable {
        val yourURLStr = "http://host.com
        val url = java.net.URL(yourURLStr)
        val connection = url.openConnection() as HttpURLConnection
        connection.setRequestProperty("User-agent", "Mozilla/4.0")

        connection.connect()
        val input = connection.inputStream

        return Drawable.createFromStream(input, "skdlsk")
    }*/

    private fun setupDatesRecyclerView() {
        /*val dates = ArrayList<String>()
        dates.add("12/12/2019")
        dates.add("12/12/2019")
        dates.add("12/12/2019")
        dates.add("12/12/2019")
        dates.add("12/12/2019")*/

        val dates = ArrayList<String>()
        this.dates.forEach {
            dates.add(it.date)
        }

        rvDates.adapter = AdapterSimpleItemHorizontal(dates, this)
        val lmDates = LinearLayoutManager(this)
        lmDates.orientation = LinearLayoutManager.HORIZONTAL
        rvDates.layoutManager = lmDates
    }

    private fun setupHoursRecyclerView() {
        /*val hours = ArrayList<String>()
        hours.add("10:00 pm")
        hours.add("10:00 pm")
        hours.add("10:00 pm")*/

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