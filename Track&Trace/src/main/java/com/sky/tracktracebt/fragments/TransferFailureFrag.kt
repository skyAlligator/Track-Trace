package com.sky.tracktracebt.fragments

import android.os.Bundle
import android.view.View
import com.sky.tracktracebt.AppData
import com.sky.tracktracebt.R
import com.sky.tracktracebt.insertDB
import com.sky.tracktracebt.setTitleBar
import kotlinx.android.synthetic.main.frag_trans_failure.*

/**
 * Created by Sky_Alligator on 5/25/2017.
 * 7:20 PM
 */
class TransferFailureFrag : FragmentMom(R.layout.frag_trans_failure) {

    override fun onBackPressed() {
        fragmentManager.popBackStack()
    }

    override fun onViewCreated(recyclerV: View?, savedInstanceState: Bundle?) {
        setTitleBar("Ownership Transfer Unsuccessful", false)
//        insertDB(activity, AppData.kitID, AppData.kitType, false, "Kit Tampered")
        ui_failResult_TryAgainBtn.setOnClickListener {
            fragmentManager.popBackStack()
        }
    }
}