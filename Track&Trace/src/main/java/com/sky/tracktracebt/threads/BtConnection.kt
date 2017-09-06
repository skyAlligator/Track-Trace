package com.sky.tracktracebt.threads

import android.bluetooth.BluetoothSocket
import android.os.Handler
import com.sky.tracktracebt.StreamType
import com.sky.tracktracebt.ignoreException
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


/**
 * Created by Sky_Alligator on 8/25/2017.
 * 11:10 PM
 */
class BtConnection(socket: BluetoothSocket, val connectionListener: (StreamType, String) -> Unit) : Thread("Connection Thread") {

    private var inStream: InputStream? = null
    private var outStream: OutputStream? = null
    private val handler: Handler = Handler()
    private var runThread: Boolean = true

    init {
        try {
            inStream = socket.inputStream
            outStream = socket.outputStream
        } catch (e: IOException) {
            e.printStackTrace()
            postOnUi(StreamType.EXCEPTION, "Unable to make a connection with Device")
        }
        start()
    }


    override fun run() {
        val buffer = ByteArray(1024)
        var bytes: Int
        try {
            while (runThread) {
                val ins = inStream ?: throw Exception("InputStream is null")
                bytes = ins.read(buffer)
                postOnUi(StreamType.RECEIVE, String(buffer, 0, bytes))
            }
        } catch (e: Exception) {
            if (!runThread) {
                e.printStackTrace()
                postOnUi(StreamType.ERROR, e.toString())
            }
        }
    }

    fun send(bytes: ByteArray) {
        try {
            val outS = outStream ?: throw Exception("OutputStream is null")
            outS.write(bytes)
            postOnUi(StreamType.SEND, String(bytes))
        } catch (e: Exception) {
            e.printStackTrace()
            postOnUi(StreamType.ERROR, e.toString())
        }
    }

    fun closeConnection() {
        runThread = false
        ignoreException {
            inStream?.close()
        }
        ignoreException {
            outStream?.close()
        }
    }

    fun postOnUi(type: StreamType, message: String) {
        handler.post {
            connectionListener(type, message)
        }
    }
}