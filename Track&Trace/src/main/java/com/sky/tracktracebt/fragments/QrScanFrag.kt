package com.sky.tracktracebt.fragments

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import com.sky.tracktracebt.*
import kotlinx.android.synthetic.main.alert_sterilise_failure.view.*
import kotlinx.android.synthetic.main.alert_sterilise_success.view.*
import kotlinx.android.synthetic.main.common_action_bar.*
import kotlinx.android.synthetic.main.frag_qrscan.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.enabled
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread

/**
 * Created by Sky_Alligator on 3/15/2017.
 * 11:09 AM
 */

class QrScanFrag(val transfer: Boolean) : FragmentMom(R.layout.frag_qrscan) {

    constructor() : this(transfer = false) {
    }

    override fun onBackPressed() {
        if (transfer)
            fragmentManager.popBackStack()
        else
            activity.finish()
    }

    var qrData: QrData? = null

    override fun onViewCreated(recyclerV: View?, savedInstanceState: Bundle?) {
        setViewVisible(false, ui_actionBar_qrFlashImgV)
        if (transfer) {
            setTitleBar("Scan & Transfer", true) {
                fragmentManager.popBackStack()
            }
            ui_qrscan_titleV.text = "SCAN QR CODE TO TRANSFER OWNERSHIP"
        } else {
            setTitleBar("Sterilise Kit", false)
            ui_qrscan_titleV.text = "SCAN QR CODE TO STERILISE KIT"
        }

        if (transfer)
            ui_qrScan_TransferBtn.text = "TRANSFER"
        else
            ui_qrScan_TransferBtn.text = "STERILISE"

        ui_qrScan_TransferBtn.setOnClickListener li@ {
            if (!internetConnected(activity)) {
                toast("No Internet Connection")
                return@li
            }
            val text = ui_qrScanDetailsTv.text.toString()

            if (text.isNullOrEmpty() || qrData == null) {
                toast("Invalid Code")
                return@li
            }
            ui_qrScan_TransferBtn.enabled = false
            setViewVisible(true, ui_qrscan_progV)

            if (transfer) callTransferApi()
            else callSterilizeApi()
        }
    }

    private fun callSterilizeApi() {
        doAsync {
            apiCallSterilization(kitID = qrData!!.kitID,
                    assetType = qrData!!.kitType,
                    assetModel = qrData!!.assetModel,
                    firmwareVersion = qrData!!.firmwareVersion,
                    buildNumber = qrData!!.buildNumber,
                    buildBy = qrData!!.buildBy)

            { success, message ->
                uiThread {
                    updateAfterResponse()
                    if (success && message.contains("Kit is Sterilized", true)) steriliseSuccessAlert()
                    else if (success) simpleAlert(activity, message) { restartQrScan() }
                    else steriliseFailAlert()
                }
            }
        }
    }

    private fun callTransferApi() {
        doAsync {
            apiCallTransfer(kitID = qrData!!.kitID,
                    receiverRole = AppData.receiverData!!.role,
                    receiverName = AppData.receiverData!!.actualName,
                    assetType = qrData!!.kitType,
                    assetModel = qrData!!.assetModel,
                    firmwareVersion = qrData!!.firmwareVersion,
                    buildNumber = qrData!!.buildNumber,
                    buildBy = qrData!!.buildBy,
                    receiverId = AppData.receiverData!!.id)

            { _, success, message ->
                uiThread {
                    updateAfterResponse()
                    if (success && message.contains("Tampered", true)) addLinearFrag(TransferFailureFrag())
                    else if (success && message.contains("Ownership Transferred", true)) addLinearFrag(TransferSuccessFrag())
                    else if (success) simpleAlert(activity, message) { restartQrScan() }
                    else simpleAlert(activity, "Server Error, Unable to Transfer") { restartQrScan() }

                }
            }
        }
    }

    private fun updateAfterResponse() {
        ui_qrScan_TransferBtn.enabled = true
        setViewVisible(false, ui_qrscan_progV)
        AppData.kitID = qrData!!.kitID
        AppData.kitType = qrData!!.kitType
    }

    private fun validateQrcode(qrText: String) {
        qrData = parseQrCode(qrText)
        if (qrData == null) {
            toast("Invalid Code")
            restartQrScan()
        } else {
            val replace = qrText.replace("{", "").replace("}", "").replace("\"", "").replace(",", "")
            ui_qrScanDetailsTv.text = replace
        }
    }

    private fun restartQrScan() {
        if (transfer) {
            fragmentManager.popBackStack()
            addLinearFrag(QrScanFrag(transfer))
        } else
            fragmentManager.beginTransaction().replace(R.id.ui_hometab_fragContainerV, QrScanFrag(false)).commit()
    }

    private fun steriliseSuccessAlert() {
        val builder = AlertDialog.Builder(activity)
        val alertV = activity.layoutInflater.inflate(R.layout.alert_sterilise_success, null)
        builder.setView(alertV)

        val dialog = builder.create()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        alertV.ui_alert_steri_succ_kitIdV.text = AppData.kitID
        alertV.ui_alert_steri_succ_kitTypeV.text = AppData.kitType
        alertV.ui_alert_steri_succ_OkayV.setOnClickListener {
            dialog.dismiss()
            navigateToHome(activity)
        }
        dialog.show()
    }

    private fun steriliseFailAlert() {
        val builder = AlertDialog.Builder(activity)
        val alertV = activity.layoutInflater.inflate(R.layout.alert_sterilise_failure, null)
        builder.setView(alertV)

        val dialog = builder.create()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        alertV.ui_alert_steri_fail_kitIdV.text = AppData.kitID
        alertV.ui_alert_steri_fail_kitTypeV.text = AppData.kitType
        alertV.ui_alert_steri_fail_cancelV.setOnClickListener {
            dialog.dismiss()
            navigateToHome(activity)
        }
        alertV.ui_alert_steri_fail_tryAgainV.setOnClickListener {
            dialog.dismiss()
            fragmentManager.beginTransaction().replace(R.id.ui_hometab_fragContainerV, QrScanFrag(false)).commit()
        }
        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        ui_qrScanDetailsTv.text = ""
    }
}
