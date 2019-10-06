package com.example.eventime.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.eventime.R
import com.parse.ParseInstallation
import com.parse.ParseUser
import org.jetbrains.anko.find
import org.jetbrains.anko.startActivity

class ActivityLogin : AppCompatActivity() {
    private lateinit var mTextEmail: EditText
    private lateinit var mTextPassword: EditText
    private lateinit var mButtonLogin: Button
    private lateinit var mTextRegister: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportActionBar?.hide() //hide the title bar
        setContentView(R.layout.activity_login)

        mTextEmail = find(R.id.activity_login_tiet_email)
        mTextPassword = find(R.id.activity_login_tiet_password)
        mButtonLogin = find(R.id.activity_login_btn_login)
        mTextRegister = find(R.id.activity_login_tv_register)

        mButtonLogin.setOnClickListener {
            val strEmail = mTextEmail.text.toString().trim()
            val strPassword = mTextPassword.text.toString().trim()

            ParseUser.logInInBackground(strEmail, strPassword) { _, error ->
                if (error == null) {
                    //Sign up successful
                    Log.d("PARSE", "Log in successful email: $strEmail")
                    startActivity<ActivityMain>()
                } else {
                    //There was an error,
                    Log.e(
                        "DEBUG PARSE",
                        "Failed to complete log in process. Error message: ${error.message} Error code ${error.code}"
                    )
                    val builder = AlertDialog.Builder(this)

                    // Set the alert dialog title
                    builder.setTitle("Error de Inicio de Sesión")

                    // Display a message on alert dialog
                    builder.setMessage("Favor de comprobar su Email/Contraseña")

                    // Set a positive button and its click listener on alert dialog
                    builder.setPositiveButton("OK"){dialog, which ->
                        Toast.makeText(this,"Revisa y vuelve a presionar el boton....",Toast.LENGTH_SHORT).show()
                    }

                    // Finally, make the alert dialog using builder
                    val dialog: AlertDialog = builder.create()

                    // Display the alert dialog on app interface
                    dialog.show()
                }
            }

        }

        mTextRegister.setOnClickListener {
            startActivity<ActivityRegister>()
        }

        // Save the current Installation to Back4App
        ParseInstallation.getCurrentInstallation().saveInBackground()
    }
}
