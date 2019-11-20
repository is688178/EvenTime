package com.example.eventime.activities.activities

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.find
import android.app.DatePickerDialog
import java.util.*
import android.app.TimePickerDialog
import android.content.Intent
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventime.R
import com.example.eventime.activities.adapters.AdapterRecyclerViewHours
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.collections.ArrayList
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.eventime.activities.messages.Message
import com.example.eventime.activities.messages.ValidationError
import com.example.eventime.activities.utils.DateHourUtils


class ActivitySelectDateHours : AppCompatActivity(), View.OnClickListener {

    private lateinit var tvDate: TextView
    private lateinit var rvHours: RecyclerView
    private lateinit var btnAddHour: ImageButton
    private lateinit var fabSave: FloatingActionButton

    private var dates = ArrayList<Calendar>()
    //private var hours = ArrayList<String>()
    private var hours = ArrayList<Calendar>()
    private lateinit var adapterRecyclerViewHours: AdapterRecyclerViewHours

    /*private var cDay = currentDay
    private var cMonth = currentMonth
    private var cYear = currentYear*/

    companion object {
        const val DATE = "date"
        const val HOURS = "hours"
        const val DATES = "dates"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_date_hours)

        bindViews()
        setClickListeners()
        setupRecyclerViewDates()

        val extras = intent.extras
        if (extras != null) {
            val datesStr = extras.getString(DATES)!!
            //datesStr.split(",").toCollection(dates)
            dates = DateHourUtils.splitDatesToArrayList(datesStr)
        }

        /*var date = LocalDate.now()
        var cDay = date.dayOfMonth
        var cMonth = date.monthValue - 1
        var cYear = date.year*/
        val cal = Calendar.getInstance()

        while (!setDate(cal, false)) {
            //CHECK MONTH AND YEAR
            //increaseDate()
            cal.add(Calendar.DAY_OF_MONTH, 1)
            /*date = date.plusDays(1)
            cDay = date.dayOfMonth
            cMonth = date.monthValue - 1
            cYear = date.year*/
        }
    }

    private fun bindViews() {
        tvDate = find(R.id.activity_select_date_hours_tv_date)
        rvHours = find(R.id.activity_select_date_hours_rv_hours)
        btnAddHour = find(R.id.activity_select_date_hours_btn_add_hour)
        fabSave = find(R.id.activity_select_date_hours_fab_save)
    }

    /*private fun setupToolbar() {
        val toolbar = Toolbar(this)
        toolbar.setTitleTextColor(resources.getColor(R.color.colorWhite))
        toolbar.setBackgroundColor(resources.getColor(R.color.colorWhite))
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }*/

    private fun setClickListeners() {
        tvDate.setOnClickListener(this)
        btnAddHour.setOnClickListener(this)
        fabSave.setOnClickListener(this)
    }

    private fun setupRecyclerViewDates() {
        adapterRecyclerViewHours = AdapterRecyclerViewHours(hours)
        rvHours.adapter = adapterRecyclerViewHours
        rvHours.layoutManager = LinearLayoutManager(this)

        val itemTouchHelperCallback = object: ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                hours.removeAt(viewHolder.adapterPosition)
                adapterRecyclerViewHours.notifyDataSetChanged()
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(rvHours)
    }

    private fun showDatePickerDialog() {
        val c = Calendar.getInstance()
        var dayToSet = c.get(Calendar.DAY_OF_MONTH)
        var monthToSet = c.get(Calendar.MONTH)
        var yearToSet = c.get(Calendar.YEAR)

        val dateStr = tvDate.text.toString()
        if (dateStr.isNotEmpty()) {
            //val values = DateHourUtils.unformatDate(date)!!
            val date = DateHourUtils.createDateFromString(dateStr)
            /*dayToSet = values[DateHourUtils.DAY]!!
            monthToSet = values[DateHourUtils.MONTH]!!
            yearToSet = values[DateHourUtils.YEAR]!!*/


            dayToSet = date.get(Calendar.DAY_OF_MONTH)
            monthToSet = date.get(Calendar.MONTH) - 1
            yearToSet = date.get(Calendar.YEAR)
        }

        val dialog = DatePickerDialog(this,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                val cal = Calendar.getInstance()
                cal.set(year, month, dayOfMonth)
                setDate(cal, true)
            },yearToSet, monthToSet, dayToSet
        )
        dialog.datePicker.minDate = System.currentTimeMillis()
        dialog.show()
    }

    private fun setDate(dateToSet: Calendar, showMessage: Boolean) : Boolean {
        //VALIDATE IF HOUR ALREADY EXISTS
        dates.forEach{date ->
            Log.d("FOREACH", "asd")
            if (DateHourUtils.compareDates(date, dateToSet) == DateHourUtils.SAME_DATE) {
                if (showMessage)
                    Message.showValidationErrorMessage(this, ValidationError.DATE_ALREADY_EXISTS)
                return false
            }
        }
        tvDate.text = DateHourUtils.formatDateToShowFormat(dateToSet)
        return true
    }

    private fun showHourPickerDialog() {
        val c = Calendar.getInstance()
        val currentHour = c.get(Calendar.HOUR_OF_DAY)
        val currentMinute = c.get(Calendar.MINUTE)

        TimePickerDialog(this,
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                val time = Calendar.getInstance()
                time.set(Calendar.HOUR_OF_DAY, hourOfDay)
                time.set(Calendar.MINUTE, minute)
                //setHour(DateHourUtils.formatHourToShowFormat(hourOfDay, minute))
                //setHour(DateHourUtils.formatHourToShowFormat(time))
                setHour(time)
                adapterRecyclerViewHours.notifyDataSetChanged()
            }, currentHour, currentMinute, false
        ).show()
    }

    private fun setHour(hourToSet: Calendar) {
        //VALIDATE IF HOUR ALREADY EXISTS
        hours.forEach{hour ->
            if (DateHourUtils.compareHours(hourToSet, hour) == DateHourUtils.SAME_HOUR) {
                Message.showValidationErrorMessage(this, ValidationError.HOUR_ALREADY_EXISTS)
                return
            }
        }
        hours.add(hourToSet)
    }

    private fun answerRequest() {
        if (hours.isEmpty()) {
            Message.showValidationErrorMessage(this, ValidationError.SET_AL_LEAST_ONE_HOUR)
        } else {
            val hoursStr = DateHourUtils.joinHoursToString(hours, false)//hours.joinToString()
            val returnIntent = Intent()
            returnIntent.putExtra(DATE, tvDate.text)
            returnIntent.putExtra(HOURS, hoursStr)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            tvDate.id -> {
                showDatePickerDialog()
            } btnAddHour.id -> {
                showHourPickerDialog()
            } fabSave.id -> {
                answerRequest()
            }
        }
    }
}
