package com.sky.tracktracebt.threads

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Handler
import android.widget.Toast
import com.sky.tracktracebt.JavaUtil
import com.sky.tracktracebt.ignoreException
import java.io.IOException
import java.util.*


/**
 * Created by Sky_Alligator on 8/26/2017.
 * 12:47 AM
 */
class BtClient(private val bTAdapter: BluetoothAdapter, private val bTDevice: BluetoothDevice,
               val mUUID: UUID, private val messageListener: (message: String) -> Unit,
               private val connectListener: (socket: BluetoothSocket) -> Unit) : Thread("Client Thread") {

    private var btSocket: BluetoothSocket? = null
    private val handler = Handler()
    private var runThread = true

    init {
        start()
    }

    override fun run() {
        if (runThread) {
            runThread = false
            try {
                bTAdapter.cancelDiscovery()
                updateMessage("Discovery Cancelled")
                val socket = JavaUtil.connect(bTDevice)
//                val socket = bTDevice.createRfcommSocketToServiceRecord(mUUID)
                updateMessage("Socket Created")
                socket.connect()
                updateMessage("Socket Connected")
                btSocket = socket
                handler.post {
                    connectListener(socket)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                closeSocket()
                updateMessage("Unable to create a Socket")
            }
            runThread = true
        }
    }

    fun closeSocket() {
        ignoreException {
            btSocket?.close()
        }
    }

    private fun updateMessage(message: String) {
        handler.post {
            messageListener(message)
        }
    }
}