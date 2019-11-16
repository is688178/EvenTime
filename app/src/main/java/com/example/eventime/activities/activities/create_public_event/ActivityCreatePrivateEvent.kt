package com.example.eventime.activities.activities.create_public_event

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventime.R
import com.example.eventime.activities.activities.ActivitySelectDateHours
import com.example.eventime.activities.adapters.AdapterRecyclerViewCreateEventDatesHours
import com.example.eventime.activities.beans.Category
import com.example.eventime.activities.beans.Event
import com.example.eventime.activities.beans.EventDate
import com.example.eventime.activities.beans.Location
import com.example.eventime.activities.listeners.ClickListener
import com.example.eventime.activities.messages.Message
import com.example.eventime.activities.messages.ValidationError
import com.example.eventime.activities.utils.DateHourUtils
import com.example.eventime.activities.utils.ParseFileConvert
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import org.jetbrains.anko.find
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ActivityCreatePrivateEvent : AppCompatActivity(), View.OnClickListener,
    DialogInterface.OnClickListener,
    ContractCreatePublicEvent.View {

    private lateinit var clRoot: CoordinatorLayout
    private lateinit var collapsingToolbar: CollapsingToolbarLayout
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var fabAddPhoto: FloatingActionButton
    private lateinit var etName: EditText
    private lateinit var etCategoriesSp: EditText
    private lateinit var etDescription: EditText
    private lateinit var tilLocation: TextInputLayout
    private lateinit var etLocation: EditText
    private lateinit var rvDates: RecyclerView
    private lateinit var adapterEventDatesHours: AdapterRecyclerViewCreateEventDatesHours
    private lateinit var btnAddDate: ImageButton

    //private var categories: Array<String> = arrayOf("Deportes", "Musica", "Gastronomia")
    private lateinit var categories: ArrayList<Category>

    private lateinit var event: Event
    private lateinit var eventCategory: Category
    private lateinit var location: Location
    private var dates = ArrayList<EventDate>()
    private var photo: Bitmap? = null
    private lateinit var values: HashMap<String, String>

    private val presenter = PresenterCreatePublicEvent(this)

    companion object {
        const val NAME = "name"
        const val DESCRIPTION = "description"
        const val LOCATION_NAME = "locationName"
        const val CATEGORY = "category"

        const val SELECT_HOUR_REQUEST = 1000
        const val AUTOCOMPLETE_REQUEST = 1100
        const val PICK_PHOTO_REQUEST = 1200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_public_event)

        bindViews()
        setupToolbar()
        setOnClickListeners()
        setupRecyclerViewDates()
        presenter.fetchCategories()
    }

    private fun bindViews() {
        clRoot = find(R.id.activity_create_public_event_cl_root)
        collapsingToolbar = find(R.id.activity_create_public_event_collapsing_toolbar)
        toolbar = find(R.id.activity_create_public_event_toolbar)
        fabAddPhoto = find(R.id.activity_create_public_event_fab_add_photo)
        etName = find(R.id.activity_create_public_event_et_name)
        etCategoriesSp = find(R.id.activity_create_public_event_et_categories_sp)
        etDescription = find(R.id.activity_create_public_event_et_description)
        tilLocation = find(R.id.activity_create_public_event_til_location)
        etLocation = find(R.id.activity_create_public_event_et_location)
        rvDates = find(R.id.activity_create_public_event_rv_dates)
        btnAddDate = find(R.id.activity_create_public_event_btn_add_date)
    }

    private fun setupToolbar() {
        collapsingToolbar.setCollapsedTitleTextColor(
            ContextCompat.getColor(
                applicationContext,
                R.color.colorWhite
            )
        )
        collapsingToolbar.setExpandedTitleColor(
            ContextCompat.getColor(
                applicationContext,
                R.color.colorWhite
            )
        )
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setOnClickListeners() {
        fabAddPhoto.setOnClickListener(this)
        etCategoriesSp.setOnClickListener(this)
        etLocation.setOnClickListener(this)
        btnAddDate.setOnClickListener(this)
    }

    private fun setupRecyclerViewDates() {
        adapterEventDatesHours =
            AdapterRecyclerViewCreateEventDatesHours(dates, object : ClickListener {
                override fun onClick(view: View, index: Int) {
                    Toast.makeText(view.context, "Click Date", Toast.LENGTH_SHORT).show()
                }
            })
        rvDates.adapter = adapterEventDatesHours
        rvDates.layoutManager = LinearLayoutManager(this)

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                dates.removeAt(viewHolder.adapterPosition)
                adapterEventDatesHours.notifyDataSetChanged()
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(rvDates)
    }

    private fun showCategoriesAlertDialog() {
        val categoriesStr = categories.map { category ->
            category.name
        }
        MaterialAlertDialogBuilder(this)
            .setItems(categoriesStr.toTypedArray(), this)
            .show()
    }

    private fun hideKeyBoardClearFocus(view: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
        clRoot.clearFocus()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_create_public_event, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_create_public_event_save -> {
                //SAVE EVENT
                if (validateValues()) {
                    Toast.makeText(this, "save", Toast.LENGTH_LONG).show()
                    event = Event(
                        null,
                        values[NAME]!!,
                        location,
                        photo,
                        values[DESCRIPTION]!!,
                        dates,
                        Calendar.getInstance(),
                        eventCategory,
                        false,
                        null,
                        null,
                        null,
                        null
                    )
                    if (photo != null)
                        event.parseFileImage = ParseFileConvert.provideParseImageFile(photo!!)
                    presenter.saveEvent(event)
                    finish()
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun validateValues(): Boolean {
        values = getValues()

        if (emptyValues(values) || dates.isEmpty()) {
            Message.showValidationErrorMessage(this, ValidationError.MISSING_DATA)
            return false
        }

        return true
    }

    private fun getValues(): HashMap<String, String> {
        val values = HashMap<String, String>()
        values[NAME] = etName.text.toString()
        values[CATEGORY] = etCategoriesSp.text.toString()
        values[DESCRIPTION] = etDescription.text.toString()
        values[LOCATION_NAME] = etLocation.text.toString()
        return values
    }

    private fun emptyValues(values: HashMap<String, String>): Boolean {
        for (value in values) {
            if (value.value.isEmpty()) {
                return true
            }
        }

        return false
    }

    override fun returnToMainAfterSaveEvent() {
        //-------------------------------------
    }

    override fun fillCategoriesField(categories: ArrayList<Category>) {
        this.categories = categories
        eventCategory = categories[0]
        etCategoriesSp.setText(eventCategory.name)
    }

    //LISTENERS

    override fun onClick(view: View?) {
        when (view!!.id) {
            fabAddPhoto.id -> {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                startActivityForResult(intent, PICK_PHOTO_REQUEST)
            }
            etCategoriesSp.id -> {
                showCategoriesAlertDialog()
                hideKeyBoardClearFocus(view)
            }
            etLocation.id -> {
                if (!Places.isInitialized()) {
                    Places.initialize(applicationContext, getString(R.string.google_maps_key))
                }

                val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
                val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                    .build(this)
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST)
            }
            btnAddDate.id -> {
                //val datesStr = dates.map { date -> date.date }
                //val datesStrJoin = datesStr.joinToString()
                val datesC = dates.map { eventDate ->
                    eventDate.date
                }
                val datesStrJoin = DateHourUtils.joinDatesToString(ArrayList(datesC))
                val intent = Intent(this, ActivitySelectDateHours::class.java)
                if (datesStrJoin.isNotEmpty())
                    intent.putExtra(ActivitySelectDateHours.DATES, datesStrJoin)
                startActivityForResult(intent, SELECT_HOUR_REQUEST)
            }
        }
    }

    override fun onClick(view: DialogInterface?, index: Int) {
        eventCategory = categories[index]
        etCategoriesSp.setText(categories[index].name)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                SELECT_HOUR_REQUEST -> {
                    val extras = data.extras
                    if (extras != null) {
                        val dateStr = extras.getString(ActivitySelectDateHours.DATE)!!
                        val hoursStr = extras.getString(ActivitySelectDateHours.HOURS)!!
                        val date = DateHourUtils.createDateFromString(dateStr)
                        val hours = DateHourUtils.splitHoursToArrayList(hoursStr)

                        dates.add(EventDate(null, date, false, hours))
                        adapterEventDatesHours.notifyDataSetChanged()
                    }
                }
                AUTOCOMPLETE_REQUEST -> {
                    val extras = data.extras
                    if (extras != null) {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        etLocation.setText(place.name)
                        location = Location(
                            place.name!!,
                            place.latLng!!.latitude,
                            place.latLng!!.longitude
                        )
                    }
                }
                PICK_PHOTO_REQUEST -> {
                    val imageUri = data.data
                    val inpStream = contentResolver.openInputStream(imageUri!!)
                    val inpStream2 = contentResolver.openInputStream(imageUri)
                    collapsingToolbar.background = Drawable.createFromStream(inpStream, "")
                    photo = BitmapFactory.decodeStream(inpStream2)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}