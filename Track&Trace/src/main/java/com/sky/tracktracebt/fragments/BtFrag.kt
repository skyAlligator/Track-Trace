package com.sky.tracktracebt.fragments

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.sky.tracktracebt.*
import com.sky.tracktracebt.threads.BtClient
import com.sky.tracktracebt.threads.BtConnection
import kotlinx.android.synthetic.main.common_action_bar.*
import kotlinx.android.synthetic.main.frag_bt.*
import kotlinx.android.synthetic.main.row_bt_device_btfrag.view.*
import java.util.*


/**
 * Created by Sky_Alligator on 8/25/2017.
 * 5:30 PM
 */
class BtFrag : FragmentMom(R.layout.frag_bt) {

    private val UUID_SECURE_MOBILE = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66")
    private val UUID_SECURE_KIT = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    val btDevices = ArrayList<BtDevice>()
    val btAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    var listAdapter: CommonAdapter<BtDevice>? = null
    var btConnection: BtConnection? = null
    var btClient: BtClient? = null

    override fun onBackPressed() {
        fragmentManager.popBackStack()
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        setTitleBar("Find Device", enableBack = true) {
            fragmentManager.popBackStack()
        }

        setDiscoverable()
        registerBtDiscoveryListener()
        registerBtPairReceiver()
        setupListV()
        refreshBtDevices()

        activity.ui_actionBar_refreshV.visible()
        activity.ui_actionBar_refreshV.setOnClickListener {
            if (!btAdapter.isEnabled)
                showSnackMessage("Bluetooth disabled, please turn it ON")
            else if (!btAdapter.isDiscovering)
                refreshBtDevices()
        }
    }

    private fun refreshBtDevices() {
        btDevices.clear()
        addPairedDevicesToList()
        btAdapter.startDiscovery()
    }

    private fun registerBtPairReceiver() {
        val intent = IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        activity.registerReceiver(devicePairReceiver, intent)
    }

    private fun setDiscoverable() {
        val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
        startActivity(discoverableIntent)
    }

    private fun registerBtDiscoveryListener() {
        val filter = IntentFilter()
        filter.addAction(BluetoothDevice.ACTION_FOUND)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        activity.registerReceiver(btDiscoveryReceiver, filter)
    }

    private fun setupListV() {
        ui_bt_deviceListV.layoutManager = LinearLayoutManager(activity)
        ui_bt_deviceListV.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        ui_bt_deviceListV.itemAnimator = DefaultItemAnimator()

        sortByPairedDevices(btDevices)

        val adapter = CommonAdapter(R.layout.row_bt_device_btfrag, btDevices) { view, device ->
            view.ui_row_btFrag_DeviceNameTV.text = device.name
            view.ui_row_btFrag_DeviceAddressTV.text = device.deviceHardwareAddress
            view.ui_row_btFrag_pairedTV.text = if (device.paired) "Paired" else "Not Paired"
            view.ui_row_btFrag_transferImgV.gone()
            setItemListeners(view, device)
        }
        listAdapter = adapter
        ui_bt_deviceListV.adapter = adapter
    }

    private fun setItemListeners(view: View, device: BtDevice) {
        view.setOnClickListener {
            if (device.paired) {
                showSnackMessage("Creating socket connection")

                btClient = BtClient(btAdapter, device.btDevice, UUID_SECURE_KIT,
                        { message ->
                            showSnackMessage(message)
                        },
                        { socket ->
                            view.ui_row_btFrag_transferImgV.visible()
                            view.ui_row_btFrag_transferImgV.setOnClickListener {
                                createBtConnectionFromSocket(socket)
                                btConnection?.send(buildJson().toByteArray())
                            }
                        })
            } else
                pairDevice(device.btDevice)
        }
        view.setOnLongClickListener {
            if (device.paired) unPairDevice(device.btDevice)
            else showSnackMessage("Device already UnPaired")
            true
        }
    }

    private fun buildJson(): String {
        val status =
                if (AppData.loginRole == AppData.SUPPLY_MANAGER || AppData.loginRole == AppData.FLEET_MANAGER)
                    "Active"
                else
                    "Delivered"

        return """
            {
              "ownerrole": "${AppData.receiverData!!.role}",
              "ownername": "${AppData.receiverData!!.actualName}",
              "ownerid": "${AppData.receiverData!!.id}",
              "currentid": "${AppData.loginOwnerId}",
              "currentname": "${AppData.loginActualName}",
              "currentrole": "${AppData.loginRole}",
              "containerstatus": "$status",
            }
            """
    }


    private fun createBtConnectionFromSocket(s: BluetoothSocket) {
        btConnection = BtConnection(s) { streamType, message ->
            when (streamType) {
                StreamType.RECEIVE -> {
//                    toast("Received -> " + message)
                    log("Received -> " + message)
                    btConnection?.closeConnection()
                    if (message.toLowerCase().contains("success"))
                        addLinearFrag(TransferSuccessFrag())
                    else
                        addLinearFrag(TransferFailureFrag())

                }
                StreamType.SEND -> {
//                    toast("Sent -> " + message)
                    log("Sent -> " + message)
                }
                StreamType.ERROR -> {
//                    toast("Error -> " + message)
                    log("Error -> " + message)
                    btConnection?.closeConnection()
                }
                StreamType.EXCEPTION -> {
                    showSnackMessage(message)
                }
            }
        }
    }

    private fun sortByPairedDevices(btDevices: ArrayList<BtDevice>) {
        btDevices.sortBy { it.paired }
    }

    private fun addPairedDevicesToList() {
        btAdapter.bondedDevices.mapTo(btDevices) {
            log("${it.name} paired added")
            BtDevice(btDevice = it, name = it.name, deviceHardwareAddress = it.address, paired = true)
        }
        listAdapter?.updateList(btDevices)
    }

    private fun pairDevice(device: BluetoothDevice) {
        ignoreException {
            val method = device.javaClass.getMethod("createBond")
            method.invoke(device)
        }
    }

    private fun unPairDevice(device: BluetoothDevice) {
        ignoreException {
            val method = device.javaClass.getMethod("removeBond")
            method.invoke(device)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity.unregisterReceiver(btDiscoveryReceiver)
        activity.unregisterReceiver(devicePairReceiver)
        btConnection?.closeConnection()
        btClient?.closeSocket()
    }

    private val btDiscoveryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            when (intent.action) {
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    activity.ui_actionBar_progressBarV.visible()
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    activity.ui_actionBar_progressBarV.gone()
                }
                BluetoothDevice.ACTION_FOUND -> {
                    val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    if (device == null || device.name == null || device.address == null) return
                    if (btDevices.find { it.deviceHardwareAddress == device.address } !== null) return

                    btDevices.add(BtDevice(btDevice = device, name = device.name, deviceHardwareAddress = device.address, paired = false))
                    sortByPairedDevices(btDevices)
                    listAdapter?.updateList(btDevices)
                    log("${device.name} added")
                }
            }
        }
    }

    private val devicePairReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action

            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED == action) {
                val state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR)
                val prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR)

                when {
                    state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING -> showSnackMessage("Device Paired")
                    state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED -> showSnackMessage("Device UnPaired")
                }
                refreshBtDevices()
            }
        }
    }

}