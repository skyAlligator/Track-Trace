package com.sky.tracktracebt

import android.bluetooth.BluetoothDevice

/**
 * Created by Sky_Alligator on 3/9/2017.
 * 8:49 PM
 */
data class ReceiverData(val id: String, val userName: String, val actualName: String, val role: String, val phone: String, val img: String) {
    override fun toString() = actualName
}

data class History(val login_name: String, val login_type: String, val kit_id: String, val kit_type: String,
                   val receiver_name: String, val receiver_type: String, val success: String, val response: String, val timestamp: String)

data class QrData(
        val kitID: String,
        val kitType: String,
        val assetModel: String,
        val firmwareVersion: String,
        val buildNumber: String,
        val buildBy: String,
        val manufacture: String,
        val lastSterilized: String,
        val version: String,
        val kitStatus: String
)

data class BtDevice(val btDevice: BluetoothDevice, val name: String, val deviceHardwareAddress: String, val paired: Boolean)