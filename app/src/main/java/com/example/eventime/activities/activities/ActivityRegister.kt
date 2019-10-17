package com.example.eventime.activities.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.eventime.R

class ActivityRegister : AppCompatActivity() {
    private lateinit var mFullName: EditText
    private lateinit var mEmail: EditText
    private lateinit var mPassword: EditText
    private lateinit var mPassword2: EditText
    private lateinit var mPhoto: Button
    private lateinit var mRegister: Button

    companion object {
        const val FULL_NAME = "FULL_NAME"
        const val EMAIL = "EMAIL"
        const val PASSWORD = "PASSWORD"
        const val PASSWORD2 = "PASSWORD2"
        private const val REQUEST_IMAGE_GALLERY = 5

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportActionBar?.hide() //hide the title bar
        setContentView(R.layout.activity_register)

        mFullName = find(R.id.activity_register_tiet_username)
        mEmail = find(R.id.activity_register_tiet_email)
        mPassword = find(R.id.activity_register_tiet_password)
        mPassword2 = find(R.id.activity_register_tiet_password2)
        mPhoto = find(R.id.activity_register_btn_photo)
        mRegister = find(R.id.activity_register_btn_register)

        mPhoto.setOnClickListener { dispatchOpenGalleryIntent() }

        mRegister.setOnClickListener { onClickRegisterTry() }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        val fullName = savedInstanceState.getString(FULL_NAME)
        val email = savedInstanceState.getString(EMAIL)
        val password = savedInstanceState.getString(PASSWORD)
        val password2 = savedInstanceState.getString(PASSWORD2)

        mFullName.setText(fullName)
        mEmail.setText(email)
        mPassword.setText(password)
        mPassword2.setText(password2)
    }

    fun onClickRegisterTry() {

        val strUser = mFullName.text.toString().trim()
        val strEmail = mEmail.text.toString().trim()
        val strPassword = mPassword.text.toString().trim()
        val strPassword2 = mPassword2.text.toString().trim()
        val strNonMatch = strPassword != strPassword2

        if (strUser.isEmpty() || strEmail.isEmpty() || strPassword.isEmpty() || strPassword2.isEmpty()) {
            logInEmptyAlertDialog()
            return
        } else if (strNonMatch) {
            logInPasswordAlertDialog()
            return
        }

        val parseUser = ParseUser()
        parseUser.apply {
            setUsername(strUser)
            setEmail(strEmail)
            setPassword(strPassword)
        }

        parseUser.signUpInBackground { error ->
            if (error == null) {
                //Sign up successful
                Log.d("PARSE", "Sign up successful user: $strUser")
                startActivity<ActivityMain>()
            } else {
                //There was an error,
                //networkState.postValue(NetworkState(Status.ERROR, error))
                Log.e(
                    "DEBUG PARSE",
                    "Failed to complete sign up process. Error message: ${error.message} Error code ${error.code}"
                )
                logInEmptyAlertDialog()
            }
        }
    }

    fun logInEmptyAlertDialog() {
        val builder = AlertDialog.Builder(this)

        // Set the alert dialog title
        builder.setTitle("Error de Inicio de Sesión")

        // Display a message on alert dialog
        builder.setMessage("Favor de ingresar todos los campos.")

        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton("OK") { _, _ ->
            Toast.makeText(this, "Revisa y vuelve a presionar el boton...", Toast.LENGTH_SHORT)
                .show()
        }

        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()

        // Display the alert dialog on app interface
        dialog.show()
    }

    private fun logInPasswordAlertDialog() {
        val builder = AlertDialog.Builder(this)

        // Set the alert dialog title
        builder.setTitle("Error de Inicio de Sesión")

        // Display a message on alert dialog
        builder.setMessage("Las contraseñas no son iguales.")

        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton("OK") { dialog, which ->
            Toast.makeText(this, "Revisa y vuelve a presionar el boton...", Toast.LENGTH_SHORT)
                .show()
        }

        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()

        // Display the alert dialog on app interface
        dialog.show()
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
}
