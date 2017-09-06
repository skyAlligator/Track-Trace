package com.sky.tracktracebt.fragments

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sky.tracktracebt.FragmentBackPress
import com.sky.tracktracebt.activities.Activity_Mom

/**
 * Created by Sky_Alligator on 3/30/2017.
 * 7:01 PM
 */
abstract class FragmentMom(val layout: Int) : Fragment(), FragmentBackPress {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View {
        return inflater.inflate(layout, container, false)
    }

    override fun onResume() {
        super.onResume()
        (activity as Activity_Mom).backListener = this
    }

    override fun onPause() {
        super.onPause()
        (activity as Activity_Mom).backListener = null
    }
}