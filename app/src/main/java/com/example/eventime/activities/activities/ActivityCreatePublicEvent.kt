package com.example.eventime.activities.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventime.R
import com.example.eventime.activities.adapters.AdapterRecyclerViewCreateEventDatesHours
import com.example.eventime.activities.listeners.ClickListener
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.jetbrains.anko.find

class ActivityCreatePublicEvent : AppCompatActivity(), View.OnClickListener {

    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var collapsingToolbar: CollapsingToolbarLayout
    private lateinit var fabAddPhoto: FloatingActionButton
    private lateinit var etName: EditText
    private lateinit var etDescription: EditText
    private lateinit var etLocation: EditText
    private lateinit var rvDates: RecyclerView
    private lateinit var btnAddDate: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_public_event)

        bindViews()
        setupToolbar()
        setOnClickListeners()
        setupRecyclerViewDates()
    }

    private fun bindViews() {
        collapsingToolbar = find(R.id.activity_create_public_event_collapsing_toolbar)
        toolbar = find(R.id.activity_create_public_event_toolbar)
        fabAddPhoto = find(R.id.activity_create_public_event_fab_add_photo)
        etName = find(R.id.activity_create_public_event_et_name)
        etDescription = find(R.id.activity_create_public_event_et_description)
        etLocation = find(R.id.activity_create_public_event_et_location)
        rvDates = find(R.id.activity_create_public_event_rv_dates)
        btnAddDate = find(R.id.activity_create_public_event_btn_add_date)
    }

    private fun setupToolbar() {
        collapsingToolbar.setCollapsedTitleTextColor(resources.getColor(R.color.colorWhite))
        collapsingToolbar.setExpandedTitleColor(resources.getColor(R.color.colorWhite))
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setOnClickListeners() {
        fabAddPhoto.setOnClickListener(this)
        btnAddDate.setOnClickListener(this)
    }

    private fun setupRecyclerViewDates() {
        val dates = ArrayList<String>()
        /*dates.add("12/12/2019")
        dates.add("12/12/2019")
        dates.add("12/12/2019")
        dates.add("12/12/2019")
        dates.add("12/12/2019")
        rvDates.adapter = AdapterRecyclerViewCreateEventDatesHours(dates, object: ClickListener {
            override fun onClick(view: View, index: Int) {
                Toast.makeText(view.context, "Click Date", Toast.LENGTH_SHORT).show()
            }
        })
        rvDates.layoutManager = LinearLayoutManager(this)*/
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_create_public_event, menu
        )
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_create_public_event_save -> {
                //SAVE EVENT
                finish()
            }
        }

        return super.onOptionsItemSelected(item)
    }


    //LISTENERS

    override fun onClick(view: View?) {
        when (view!!.id) {
            fabAddPhoto.id -> {
                Toast.makeText(this, "Agregar foto", Toast.LENGTH_SHORT).show()
            } btnAddDate.id -> {

            Toast.makeText(this, "Agregar fecha", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
