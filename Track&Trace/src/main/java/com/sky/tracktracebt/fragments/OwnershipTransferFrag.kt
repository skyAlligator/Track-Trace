package com.sky.tracktracebt.fragments


import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import com.sky.tracktracebt.AppData
import com.sky.tracktracebt.R
import com.sky.tracktracebt.addLinearFrag
import com.sky.tracktracebt.setTitleBar

import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.frag_ownertransfer.*
import org.jetbrains.anko.toast

/**
 * Created by Sky_Alligator on 3/9/2017.
 * 11:45 PM
 */
class OwnershipTransferFrag : FragmentMom(R.layout.frag_ownertransfer) {

    override fun onBackPressed() {
        activity.finish()
    }

    private val ACCESS_FINE = 230
    private val ACCESS_COARSE = 233

    override fun onViewCreated(recyclerV: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(recyclerV, savedInstanceState)
        setTitleBar("Ownership Transfer", enableBack = true) {
            activity.finish()
        }

        Picasso.with(activity).load("file:///android_asset/imgs/${AppData.loginActualName}.jpg").into(ui_senderOwnerImgV)
        ui_senderOwnerNameTv.text = AppData.loginActualName
        ui_senderOwnerNoTv.text = AppData.loginPhone

        val receiver = AppData.receiverData!!
        Picasso.with(activity).load("file:///android_asset/imgs/${receiver.actualName}.jpg").into(ui_receiverOwnerImgV)
        ui_receiverOwnerNameTv.text = receiver.actualName
        ui_receiverOwnerNoTv.text = receiver.phone
        ui_receiverCateTv.text = receiver.role

//        when (receiver.role) {
//            AppData.FLEET_MANAGER -> ui_receiverCateTv.text = AppData.FLEET_MANAGER
//            AppData.OPERATIONS_MANAGER -> ui_receiverCateTv.text = AppData.OPERATIONS_MANAGER
//            AppData.PURCHASING_MANAGER -> ui_receiverCateTv.text = AppData.PURCHASING_MANAGER
//        }

        ui_receiverContactV.setOnClickListener {
            activity.finish()
        }

        ui_scanNtransferBtn.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ->
                    ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), ACCESS_FINE)
                ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ->
                    ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), ACCESS_COARSE)
                else ->
                    addLinearFrag(BtFrag())
            }
//            else addLinearFrag(QrScanFrag(true))
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        if (requestCode == CAMERA_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED) addLinearFrag(QrScanFrag(true))
        when {
            requestCode == ACCESS_FINE && grantResults[0] == PackageManager.PERMISSION_GRANTED -> toast("Permission Granted")
            requestCode == ACCESS_COARSE && grantResults[0] == PackageManager.PERMISSION_GRANTED -> toast("Permission Granted")
            else -> toast("permission denied, Try again!")
        }
    }
}