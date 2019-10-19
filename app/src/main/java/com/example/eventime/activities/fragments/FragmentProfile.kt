package com.example.eventime.activities.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class FragmentProfile : Fragment(){

    private lateinit var container: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(com.example.eventime.R.layout.fragment_profile, container, false)
        this.container = container!!.context

        return view
    }
}