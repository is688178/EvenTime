package com.example.eventime.activities.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import com.example.eventime.R
import com.parse.ParseFile
import com.parse.ParseQuery
import com.parse.ParseUser
import org.jetbrains.anko.find
import org.jetbrains.anko.startActivity
import java.io.*

class ActivityAddPhoto : AppCompatActivity() {
    private lateinit var mBtnAddPhoto: Button
    private lateinit var mIVPhoto: ImageView
    private lateinit var mBtnNextBtn: Button

    companion object {
        private const val REQUEST_IMAGE_GALLERY = 5
        private val LOG_TAG = ActivityRegister::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportActionBar?.hide() //hide the title bar
        setContentView(R.layout.activity_add_photo)

        mBtnAddPhoto = find(R.id.activity_photo_btn_add_photo)
        mIVPhoto = find(R.id.activity_photo_iv)
        mBtnNextBtn = find(R.id.activity_photo_btn_next)

        mBtnAddPhoto.setOnClickListener { dispatchOpenGalleryIntent() }
        mBtnNextBtn.setOnClickListener { startActivity<ActivityMain>() }
    }

    /**
     * Opens an image picker activity, default is Android's image gallery.
     **/
    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    private fun dispatchOpenGalleryIntent() {
        val packageManager = this.packageManager
        Intent(Intent.ACTION_PICK).also { intent ->
            intent.type = "image/*"
            // Ensure that there's a gallery activity to handle the intent
            intent.resolveActivity(packageManager).also {
                startActivityForResult(intent, REQUEST_IMAGE_GALLERY)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                REQUEST_IMAGE_GALLERY -> {
                    try {
                        val imageStream: InputStream =
                            this.contentResolver.openInputStream(data.data!!)!!
                        val bitmap = BitmapFactory.decodeStream(imageStream)
                        mIVPhoto.setImageBitmap(bitmap)
                        saveProfileImageToParseDatabase(bitmap)
                    } catch (e: Exception) {
                        Log.e(LOG_TAG, "Failed to set Save ProfileImage : $e")
                    }
                }
            }
        }
    }

    private fun saveProfileImageToParseDatabase(bitmap: Bitmap?) {
        if (bitmap != null) {
            val parseFile = provideParseImageFile(bitmap)
            if (ParseUser.getCurrentUser().objectId != null) {

                val objectId = ParseUser.getCurrentUser().objectId
                val userQuery = ParseQuery.getQuery<ParseUser>("_User")
                if (objectId != null) {

                    userQuery.getInBackground(objectId) { user, error ->

                        if (error == null) {
                            user.put("image", parseFile)
                            user.saveInBackground {
                                //"it" : ParseException
                                if (it == null)
                                    Log.v(LOG_TAG, "success")
                                else
                                    Log.e(
                                        LOG_TAG,
                                        "Failed to add image to parse database. Error message: ${error?.message} Error code ${error?.code}"
                                    )
                            }
                        } else
                            Log.e(LOG_TAG, "${error.message}")
                    }

                }
            }
        } else
            Log.v(LOG_TAG, "Bitmap NULL")
    }

    private fun provideParseImageFile(bitmap: Bitmap): ParseFile {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()
        return ParseFile("profile.jpg", byteArray)
    }


}