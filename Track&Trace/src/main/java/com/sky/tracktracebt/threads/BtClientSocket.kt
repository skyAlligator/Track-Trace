package com.sky.tracktracebt.threads

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Handler
import com.sky.tracktracebt.ignoreException
import java.io.IOException
import java.util.*

/**
 * Created by Sky_Alligator on 8/26/2017.
 * 12:47 AM
 */
class BtClientSocket(bTAdapter: BluetoothAdapter, bTDevice: BluetoothDevice, mUUID: UUID, val connectListener: (socket: BluetoothSocket?, btDeviceName: String) -> Unit) : Thread("Client Thread") {
    private var btSocket: BluetoothSocket? = null

    init {
        val handler: Handler = Handler()
        try {
            val socket = bTDevice.createRfcommSocketToServiceRecord(mUUID)
            bTAdapter.cancelDiscovery()
            socket.connect()
            handler.post {
                connectListener(socket, bTDevice.name)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            closeSocket()
            handler.post {
                connectListener(null, "Unable to create a Socket")
            }
        }
        start()
    }

    fun closeSocket() {
        ignoreException {
            btSocket?.close()
        }
    }
}