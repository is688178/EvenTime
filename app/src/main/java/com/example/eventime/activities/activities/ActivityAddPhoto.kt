package com.example.eventime.activities.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import com.example.eventime.R
import com.parse.ParseFile
import com.parse.ParseQuery
import com.parse.ParseUser
import org.jetbrains.anko.find
import org.jetbrains.anko.startActivity
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

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
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_GALLERY -> {
                    val imageUri = data?.data
                    try {
                        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                        mIVPhoto.setImageBitmap(bitmap)
                        saveImageProfile(bitmap)
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
                addProfileImage(parseFile, ParseUser.getCurrentUser().objectId)
            }
        } else {
            Log.v(LOG_TAG, "Bitmap NULL")
        }
    }

    private fun addProfileImage(parseFile: ParseFile, objectId: String?) {
        return attemptAttachingProfileImageToUser(objectId, parseFile)
    }

    private fun attemptAttachingProfileImageToUser(objectId: String?, parseFile: ParseFile) {
        val userQuery = ParseQuery.getQuery<ParseUser>("_User")
        if (objectId != null) {
            userQuery.getInBackground(objectId) { user, error ->
                if (error == null) {
                    // object return successfully
                    user.put("image", parseFile)
                    user.saveInBackground {
                        //"it" represents the ParseException
                        if (it == null) {
                            Log.v(LOG_TAG, "success")
                        } else {
                            Log.e(LOG_TAG, "Failed to add image to parse database. Error message: ${error?.message} Error code ${error?.code}")
                        }
                    }
                } else {
                    Log.e(LOG_TAG, error.message)
                }
            }
        }
    }

    fun provideParseImageFile(bitmap: Bitmap): ParseFile {
        val stream = ByteArrayOutputStream()
        bitmap.putInto(stream)
        val byteArray = stream.toByteArray()
        return ParseFile("profile.jpg", byteArray)
    }

    /**
     * Write a compressed version of a bitmap to the specified outputstream.
     *
     * @receiver a [Bitmap] to be saved to the outputstream
     * @param stream outputStream to write the compressed data.
     * @return true if successfully compressed to the specified stream.
     */
    fun Bitmap.putInto(stream: OutputStream): Boolean {
        return compress(Bitmap.CompressFormat.PNG, 100, stream)
    }

    /** Save image to Disk*/
    private fun saveImageProfile(bitmap: Bitmap? = null): String {
        var outputStream: FileOutputStream? = null
        val profileImage = provideProfileImageFile()
        try {
            outputStream = FileOutputStream(profileImage)
            if (bitmap != null) {
                //save the bitmap to disk
                writeBitmap(bitmap, outputStream)
            }
        } catch (exception: Exception) {
            // If the file exists but is a directory rather than a regular file
            // does not exist but cannot be created, or cannot be opened for any other reason
            Log.v(LOG_TAG, exception.message)
        } catch (exception: NullPointerException) {
            // FileName is null
            Log.e(LOG_TAG, exception.message)
        } finally {
            try {
                outputStream?.close()
            } catch (exception: Exception) {
                //I/O error occurs.
                Log.v(LOG_TAG, exception.message)
            }
        }
        return profileImage.absolutePath
    }

    private fun provideProfileImageFile(): File {
        val fileDirectory = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val fileName = "profile.jpg"
        return File(fileDirectory, fileName)
    }

    /**
     * Write a compressed version of a bitmap to the specified outputstream.
     *
     * @param bitmap a [Bitmap] to be saved to the outputstream
     * @param stream   The outputstream to write the compressed data.
     * @return true if successfully compressed to the specified stream.
     * */
    fun writeBitmap(bitmap: Bitmap, outputStream: OutputStream): Boolean {
        val quality = 100
        //The compress method writes a compressed version of the bitmap to our outputStream.
        return bitmap.compress(Bitmap.CompressFormat.PNG, quality, outputStream)
    }


}