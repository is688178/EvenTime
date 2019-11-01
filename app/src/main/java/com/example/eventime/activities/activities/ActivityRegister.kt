package com.example.eventime.activities.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.eventime.R
import com.example.eventime.activities.config.SESSION_ID_KEY
import com.example.eventime.activities.config.SHARED_PREFERENCES
import org.jetbrains.anko.find
import org.jetbrains.anko.startActivity
import com.parse.*


class ActivityRegister : AppCompatActivity() {
    private lateinit var mFullName: EditText
    private lateinit var mEmail: EditText
    private lateinit var mPassword: EditText
    private lateinit var mPassword2: EditText
    private lateinit var mRegister: Button

    companion object {
        const val FULL_NAME = "FULL_NAME"
        const val EMAIL = "EMAIL"
        const val PASSWORD = "PASSWORD"
        const val PASSWORD2 = "PASSWORD2"
        private val LOG_TAG = ActivityRegister::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportActionBar?.hide() //hide the title bar
        setContentView(R.layout.activity_register)

        mFullName = find(R.id.activity_register_tiet_username)
        mEmail = find(R.id.activity_register_tiet_email)
        mPassword = find(R.id.activity_register_tiet_password)
        mPassword2 = find(R.id.activity_register_tiet_password2)
        mRegister = find(R.id.activity_register_btn_register)

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

    private fun onClickRegisterTry() {
        Log.v(LOG_TAG, "start onClickRegisterTry")

        val strUser = mFullName.text.toString().trim()
        val strEmail = mEmail.text.toString().trim()
        val strPassword = mPassword.text.toString().trim()
        val strPassword2 = mPassword2.text.toString().trim()

        when {
            (strUser.isEmpty() || strEmail.isEmpty() || strPassword.isEmpty() || strPassword2.isEmpty()) -> {
                val title = "Error de registro de usuario"
                val message = "Al menos un campo esta vacío"
                errorAlertDialog(title, message)
                return
            }


            (strPassword != strPassword2) -> {
                val title = "Error de registro de usuario"
                val message = "Las contraseñas no concuerdan"
                errorAlertDialog(title, message)
                return
            }

            !isValidEmail(strEmail) ->{
                val title = "Error de registro de usuario"
                val message = "Patron de correo inválido"
                errorAlertDialog(title, message)
                return
            }

            !isValidPassword(strPassword) -> {
                val title = "Error de registro de usuario"
                val message = "Contraseña debil: se requieren 6 a 14 caracteres, numeros, letras mayusculas y minusculas (ambas)"
                errorAlertDialog(title, message)
                return
            }
        }


        val parseUser = ParseUser()
        parseUser.apply {
            username = strUser
            email = strEmail
            setPassword(strPassword)
        }

        parseUser.signUpInBackground { error ->
            if (error == null) {
                //Sign up successful
                Log.d("PARSE", "Sign up successful user: $strUser")
                saveSessionToken(parseUser.sessionToken)
                startActivity<ActivityAddPhoto>()
            } else {
                Log.e(
                    "DEBUG PARSE",
                    "Failed to complete sign up process. Error message: ${error.message} Error code ${error.code}"
                )
                val title = "Error de Registro en Parse"
                val message = "Favor de ingresar todos los campos y revisar tu conexión a internet."
                errorAlertDialog(title, message)
            }
        }


    }

    private fun errorAlertDialog(title: String, message: String) {
        val builder = AlertDialog.Builder(this)

        // Set the alert dialog title
        builder.setTitle(title)

        // Display a message on alert dialog
        builder.setMessage(message)

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

    private fun isValidEmail(email: String): Boolean {
        //A valid e-mail address pattern
        val emailPattern = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
        val emailRegex = Regex(emailPattern)
        return emailRegex.matches(email)
    }

    private fun isValidPassword(password: String): Boolean {
        //6 to 14 character password requiring numbers and both lowercase and uppercase letters
        val passwordPattern = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,14}\$"

        //6 to 10 character password
        //val passwordPattern = "^(?=.*[a-z]).{6,10}\$"
        val passwordRegex = Regex(passwordPattern)
        return passwordRegex.matches(password)
    }

    private fun saveSessionToken(sessionToken: String) {
        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(SESSION_ID_KEY, sessionToken)
        editor.apply()
    }
}
