package com.sky.tracktracebt.fragments

import android.os.Bundle
import android.view.View
import com.sky.tracktracebt.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.frag_trans_success.*

/**
 * Created by Sky_Alligator on 4/18/2017.
 * 11:32 PM
 */

class TransferSuccessFrag : FragmentMom(R.layout.frag_trans_success), FragmentBackPress {

    override fun onBackPressed() {
        //do nothing
    }

    override fun onViewCreated(recyclerV: View?, savedInstanceState: Bundle?) {
        val message =
                if (AppData.loginRole == AppData.SUPPLY_MANAGER || AppData.loginRole == AppData.FLEET_MANAGER)
                    "Transferred"
                else
                    "Delivered"

        setTitleBar("$message Successfully", false) { }
        ui_successResult_statusTv.text = message + " to"

        ui_successResultReceiverNameTv.text = AppData.receiverData?.actualName
        ui_successResultReceiverNumberTv.text = AppData.receiverData?.phone
        Picasso.with(activity)
                .load("file:///android_asset/imgs/${AppData.receiverData?.actualName}.jpg")
                .error(R.drawable.jasonmomoa)
                .into(ui_successResultReceiverImgV)

        ui_success_resultDoneBtn.setOnClickListener {
            removeTransferFragments()
            activity.finish()
        }
    }
}