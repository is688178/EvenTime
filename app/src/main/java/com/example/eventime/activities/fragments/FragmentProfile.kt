package com.example.eventime.activities.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.eventime.R
import com.example.eventime.activities.activities.LogoutListener

class FragmentProfile : Fragment(), View.OnClickListener {
    private lateinit var listener: LogoutListener
    private lateinit var container: Context

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
        view.findViewById<Button>(R.id.fragment_profile_btn_logout).setOnClickListener(this)
        this.container = container!!.context

        return view
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.fragment_profile_btn_logout -> {
                listener.logout()
            }
        }
    }
}