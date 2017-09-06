package com.sky.tracktracebt.activities

import android.os.Bundle
import com.sky.tracktracebt.R
import com.sky.tracktracebt.addLinearFrag
import com.sky.tracktracebt.fragments.OwnershipTransferFrag


/**
 * Created by Sky_Alligator on 3/31/2017.
 * 8:00 PM
 */
class LinearActivity : Activity_Mom(R.layout.activity_linear) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addLinearFrag(OwnershipTransferFrag())
    }
}