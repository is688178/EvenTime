package com.example.eventime.activities.activities.eventDetails

import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import com.example.eventime.activities.adapters.AdapterRecyclerViewComments
import com.example.eventime.activities.beans.*
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.jetbrains.anko.find
import com.example.eventime.R
import com.example.eventime.activities.utils.DateHourUtils
import com.example.eventime.activities.utils.ParseFileConvert
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.parse.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.InputStream
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
    private lateinit var etComment: EditText
    private lateinit var btnComment: Button
    private lateinit var adapterComments: AdapterRecyclerViewComments
    private lateinit var fabAttend: FloatingActionButton

    private val presenter = PresenterEventDetails(this)

    private lateinit var event: Event
    private lateinit var eventId: String
    private var eventDateId = ""
    private lateinit var eventDate: Calendar

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
        etComment = find(R.id.activity_event_details_tiet_comments)
        btnComment = find(R.id.activity_event_details_btn_public_comment)
    }

    private fun setListeners() {
        fabAttend.setOnClickListener(this)
        btnComment.setOnClickListener(this)
    }

    private fun setupToolbar() {
        collapsingToolbar.setCollapsedTitleTextColor(ContextCompat.getColor(this, R.color.colorWhite))
        collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(this, R.color.colorWhite))
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Aerosmith concert"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupCommentsRecyclerView(comments: ArrayList<Comment>) {
        adapterComments = AdapterRecyclerViewComments(comments)
        rvComments.adapter = adapterComments
        rvComments.layoutManager = LinearLayoutManager(this)

        rvComments.visibility = View.VISIBLE
        tvNoComments.visibility = View.GONE
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

        alertDialog.find<TextView>(R.id.alert_dialog_confirm_event_assistance_tv_cancel).setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.find<Button>(R.id.alert_dialog_confirm_event_assistance_btn_confirm).setOnClickListener {
            savePublicAsPrivateEvent()
            alertDialog.dismiss()
        }

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
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
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

    private fun saveAndPublicComment() {
        val strComment = etComment.text.toString()
        val cal = Calendar.getInstance()
        val user = ParseUser.getCurrentUser()

        val queryEvent = ParseQuery.getQuery<ParseObject>("Event")
        queryEvent.whereEqualTo("objectId", eventId)
        queryEvent.getFirstInBackground { parseEvent, e ->
            if (e == null) {
                val parseObjectComment = ParseObject("Comment")
                parseObjectComment.put("description", strComment)
                parseObjectComment.put("date", cal.time)
                parseObjectComment.put("Person", user)
                parseObjectComment.put("Event", parseEvent)

                doAsync {
                    parseObjectComment.saveInBackground(object : SaveCallback {
                        override fun done(e: ParseException?) {
                            if (e == null) {
                                Log.d("COMENTARIO GUARDADO", "MSG")

                                //Clear textView
                                etComment.setText("")
                                refreshRecyclerViewComments()

                            } else
                                Log.e("ERROR SAVE PARSE", e.toString())
                        }
                    })
                }
            } else {
                Log.e("ERROR SAVING COMMENT", e.message.toString())
            }
        }
    }

    private fun refreshRecyclerViewComments() {
        val queryEvent = ParseQuery.getQuery<ParseObject>("Event")
        queryEvent.whereEqualTo("objectId", eventId)
        queryEvent.getFirstInBackground { parseEvent, e ->
            if (e == null) {
                doAsync {
                    val queryComments = ParseQuery.getQuery<ParseObject>("Comment")
                    queryComments.orderByDescending("date")
                    queryComments.whereEqualTo("Event", parseEvent)
                    queryComments.include("Person")
                    queryComments.findInBackground { objectsComments, e ->
                        if (e == null) {
                            val comments = ArrayList<Comment>()
                            for (oc in objectsComments) {
                                val user = oc["Person"] as ParseUser
                                val parseImage = user["image"] as ParseFile
                                val bitmap = BitmapFactory.decodeStream(parseImage.dataStream)
                                val person = Person(user.objectId, user.username, "", bitmap, parseImage)
                                val calendar = Calendar.getInstance()
                                calendar.time = oc["date"] as Date
                                val comment = Comment(oc.objectId, person, 5, calendar, oc["description"] as String)

                                comments.add(comment)
                            }
                            uiThread {
                                setupCommentsRecyclerView(comments)
/*                                if (adapterComments == null)

                                else
                                    adapterComments.updateData(comments)*/
                            }
                        }
                    }

                }
            } else {
                Log.e("ERROR SAVING COMMENT", e.message.toString())
            }
        }


    }


    private fun savePublicAsPrivateEvent() {
        val parseObjectEvent = ParseObject("Event")
        parseObjectEvent.put("locationName", event.location!!.name)
        parseObjectEvent.put("private", true)
        parseObjectEvent.put("name", event.name)
        parseObjectEvent.put("Person", ParseUser.getCurrentUser())

        val imageStream: InputStream = event.parseFileImage!!.dataStream
        val bitmap = BitmapFactory.decodeStream(imageStream)
        val parseFile = ParseFileConvert.provideParseImageFile(bitmap)
        parseObjectEvent.put("image", parseFile)

        val location = event.location as Location

        val geoPoint = ParseGeoPoint(location.latitude, location.longitude)
        parseObjectEvent.put("location", geoPoint)

        parseObjectEvent.put("description", event.description)

        doAsync {
            parseObjectEvent.saveInBackground(object : SaveCallback {
                override fun done(e: ParseException?) {
                    if (e == null) {
                        var date = eventDate.time

                        val parseObjectEventDateStart = ParseObject("EventDate")
                        parseObjectEventDateStart.put("date", date)
                        parseObjectEventDateStart.put("Event", parseObjectEvent)
                        parseObjectEventDateStart.put("startDate", true)
                        parseObjectEventDateStart.saveInBackground(object : SaveCallback {
                            override fun done(e: ParseException?) {
                                if (e != null)
                                    Log.e("ERROR SAVE StartDate", e.message.toString())
                            }
                        })
                        Thread.sleep(1000)

                        date.time = date.time + 1
                        val parseObjectEventDateEnd = ParseObject("EventDate")
                        parseObjectEventDateEnd.put("date", date)
                        parseObjectEventDateEnd.put("Event", parseObjectEvent)
                        parseObjectEventDateEnd.put("startDate", false)
                        parseObjectEventDateEnd.saveInBackground(object : SaveCallback {
                            override fun done(e: ParseException?) {
                                if (e != null)
                                    Log.e("ERROR SAVE EndDate", e.message.toString())
                            }
                        })
                    } else
                        Log.d("ERROR SAVE PARSE", e.toString())
                }
            })
        }
    }


    //LISTENERS

    override fun onClick(view: View?) {
        when (view?.id) {
            fabAttend.id -> {
                showAlertDialogConfimation()
            }
            btnComment.id -> {
                saveAndPublicComment()
            }
        }
    }
}