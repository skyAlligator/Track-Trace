package com.sky.tracktracebt.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import com.sky.tracktracebt.AppData
import com.sky.tracktracebt.R
import com.sky.tracktracebt.fragments.AlertsFrag
import com.sky.tracktracebt.fragments.BtFrag
import com.sky.tracktracebt.fragments.OwnersListFrag
import com.sky.tracktracebt.fragments.TransferHistoryFrag
import kotlinx.android.synthetic.main.activity_tabhome.*
import org.jetbrains.anko.toast

/**
 * Created by Sky_Alligator on 4/5/2017.
 * 1:44 PM
 */
class HomeTabActivity : Activity_Mom(R.layout.activity_tabhome) {
    private val CAMERA_REQUEST = 3320

    enum class TabType {TRANSFER, HISTORY, STERILIZE, ALERT }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var tabSelected: TabType = TabType.HISTORY

        ui_tab_transferV.setOnClickListener {
            if (tabSelected == TabType.TRANSFER) return@setOnClickListener
            tabSelected = TabType.TRANSFER

            tabSelection(ui_tab_transfer_iconV)
            fragmentManager.beginTransaction().replace(R.id.ui_hometab_fragContainerV, OwnersListFrag()).commit()
        }
        ui_tab_historyV.setOnClickListener {
            if (tabSelected == TabType.HISTORY) return@setOnClickListener
            tabSelected = TabType.HISTORY

            tabSelection(ui_tab_history_iconV)
            fragmentManager.beginTransaction().replace(R.id.ui_hometab_fragContainerV, TransferHistoryFrag()).commit()
        }
        ui_tab_sterilizeV.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this@HomeTabActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@HomeTabActivity, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST)
            } else {
                if (tabSelected == TabType.STERILIZE) return@setOnClickListener
                tabSelected = TabType.STERILIZE

                fragmentManager.beginTransaction().replace(R.id.ui_hometab_fragContainerV, BtFrag()).commit()
                tabSelection(ui_tab_sterilize_iconV)
            }
        }
        ui_tab_alertsV.setOnClickListener {
            if (tabSelected == TabType.ALERT) return@setOnClickListener
            tabSelected = TabType.ALERT

            tabSelection(ui_tab_alerts_iconV)
            fragmentManager.beginTransaction().replace(R.id.ui_hometab_fragContainerV, AlertsFrag()).commit()
        }
        if (!AppData.loginRole.contains("manufacturer", true)) ui_tab_sterilizeV.visibility = View.GONE
        ui_tab_transferV.performClick()
    }

    override fun onStart() {
        super.onStart()
        AppData.receiverData = null
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == CAMERA_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED) toast("permission granted to scan QR code")
        else toast("permission denied to scan QR code")
    }

    private fun tabSelection(selectedV: View) {
        val vArr = listOf(ui_tab_transfer_iconV, ui_tab_history_iconV, ui_tab_sterilize_iconV, ui_tab_alerts_iconV)
        for (v in vArr) v.isSelected = v == selectedV
    }
}

