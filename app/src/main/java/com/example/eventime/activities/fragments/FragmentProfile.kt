package com.example.eventime.activities.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventime.R
import com.example.eventime.activities.activities.main.LogoutListener
import com.example.eventime.activities.adapters.AdapterPublicEvent
import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import org.jetbrains.anko.find

class FragmentProfile : Fragment(), View.OnClickListener {
    private lateinit var listener: LogoutListener
    private lateinit var container: Context
    private lateinit var mEmail: TextView
    private lateinit var mUserName: TextView
    private lateinit var mImageView: ImageView
    private lateinit var mCloseSession: Button
    private lateinit var mRecyclerView: RecyclerView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as LogoutListener
        } catch (error: ClassCastException) {
            Log.e("FragmentProfile", "The activity must implement LogoutListener, $error")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        this.container = container!!.context

        mEmail = view.find(R.id.fragment_profile_tv_email)
        mUserName = view.find(R.id.fragment_profile_tv_user_name)
        mCloseSession = view.find(R.id.fragment_profile_btn_logout)
        mImageView = view.find(R.id.ic_user)
        mRecyclerView = view.find(R.id.fragment_profile_rv_userCreatedEvents)

        serUserImage()
        setUserName()
        setUserEmail()
        setUserCreatedEvents(view)
        mCloseSession.setOnClickListener(this)

        return view
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.fragment_profile_btn_logout -> {
                listener.logout()
            }
        }
    }

    private fun serUserImage() {
        if (ParseUser.getCurrentUser() != null) {
            try {
                val parseFile: ParseFile = ParseUser.getCurrentUser().get("image") as ParseFile
                Glide.with(this).load(parseFile.url).into(mImageView)
            } catch (exception: Exception) {
                Log.e("DEBUG User Name", exception.message.toString())
            }
        }
    }

    private fun setUserName() {
        if (ParseUser.getCurrentUser() != null) {
            try {
                mUserName.text = ParseUser.getCurrentUser().username
            } catch (exception: Exception) {
                Log.e("DEBUG User Name", exception.message.toString())
            }
        }
    }

    private fun setUserEmail() {
        if (ParseUser.getCurrentUser() != null) {
            try {
                mEmail.text = ParseUser.getCurrentUser().email
            } catch (exception: Exception) {
                Log.e("DEBUG User Email", exception.message.toString())
            }
        }
    }

    private fun setUserCreatedEvents(view: View) {
        val query = ParseQuery.getQuery<ParseObject>("EventDate")
        query.include("Event")
        query.findInBackground { objects, _ ->

            // Removing event not created by the user
            val userId = ParseUser.getCurrentUser().objectId
            val eventsToRemove = arrayListOf<ParseObject>()

            for (o in objects) {
                val event = o["Event"] as ParseObject
                val eventUser = event["Person"] as ParseUser
                val eventIsPrivate = event["private"] as Boolean
                if (eventUser.objectId != userId || eventIsPrivate)
                    eventsToRemove.add(o)
            }

            for (o in eventsToRemove)
                objects.remove(o)

            mRecyclerView.adapter = AdapterPublicEvent(objects)
            mRecyclerView.layoutManager = LinearLayoutManager(view.context)
        }

    }


}