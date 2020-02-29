package com.william.vehiclecontroller.ui

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.william.vehiclecontroller.R
import kotlinx.android.synthetic.main.select_device_layout.*
import org.jetbrains.anko.toast
import java.lang.Exception

class SelectDeviceActivity : AppCompatActivity() {

    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothReceiver: BroadcastReceiver? = null
    private val discoveredDeviceList: ArrayList<String> = ArrayList()
    private lateinit var pairedDevices: Set<BluetoothDevice>
    private val requestEnableBluetooth = 1

    companion object {
        const val ADDRESS: String = "device_address"
        private val discoveredDevices: ArrayList<BluetoothDevice> = ArrayList()
    }

    private class BluetoothBroadcastReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent!!.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                val device: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                Log.i("Device", "device found: $device")
                discoveredDevices.add(device)
            }
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED == action) {
                Log.i("Discovery", "Starting Bluetooth discovery")
            }
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED == action) {
                Log.i("Discovery", "Finishing Bluetooth discovery")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.select_device_layout)

            Log.i("notify:", "Hello? 1")
            bluetoothReceiver = BluetoothBroadcastReceiver()
            val intentFilter = IntentFilter()
            intentFilter.addAction(BluetoothDevice.ACTION_FOUND)
            intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
            this.registerReceiver(bluetoothReceiver, intentFilter)

            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            if (bluetoothAdapter == null) {
                toast("This device does not support Bluetooth")
                return
            }
            if (!bluetoothAdapter!!.isEnabled) {
                val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBluetoothIntent, requestEnableBluetooth)
            }
            if (bluetoothAdapter!!.isDiscovering) {
                bluetoothAdapter!!.cancelDiscovery()
            }

            pairedDevicesList()
            discoveredDevicesList()
            select_device_refresh.setOnClickListener { pairedDevicesList() }
            select_device_scan.setOnClickListener { discoveredDevicesList() }

            bluetoothAdapter!!.startDiscovery()
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    // List's the discovered devices found by the BroadCast receiver
    private fun discoveredDevicesList() {
        val adapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_list_item_1, discoveredDeviceList)
        if (discoveredDeviceList.size > 0) {
            for (item in discoveredDeviceList) {
                discoveredDeviceList.remove(item)
            }
        }
        if (discoveredDevices.isEmpty()) {
            discoveredDeviceList.add("No Bluetooth devices discovered")
            select_device_discovery_list.adapter = adapter
        } else {
            listDevices(select_device_discovery_list, discoveredDevices, discoveredDeviceList, adapter)
        }
    }

    private fun pairedDevicesList() {
        val nameList: ArrayList<String> = ArrayList()
        val list: ArrayList<BluetoothDevice> = ArrayList()
        val adapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_list_item_1, nameList)
        pairedDevices = bluetoothAdapter!!.bondedDevices
        if (pairedDevices.isNotEmpty()) {
            for (device: BluetoothDevice in pairedDevices) {
                list.add(device)
            }
        } else {
            nameList.add("No Bluetooth devices paired")
            toast("No paired Bluetooth devices found")
        }
        listDevices(select_device_paired_list, list, nameList, adapter)
    }

    private fun listDevices(
        view: ListView,
        fromList: ArrayList<BluetoothDevice>,
        toList: ArrayList<String>,
        adapter: ArrayAdapter<String>) {
        try {
            fromList.mapTo(toList, { it.name + " - " + it.address })
            view.adapter = adapter
            view.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, position, _ ->
                    val device: BluetoothDevice = fromList[position]
                    val address: String = device.address
                    // This is where we start the new intent and activity
                    val intent = Intent(this, ControllerActivity::class.java)
                    intent.putExtra(ADDRESS, address)
                    startActivity(intent)
                }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestEnableBluetooth) {
            if (resultCode == Activity.RESULT_OK) {
                if (bluetoothAdapter!!.isEnabled) {
                    toast("Bluetooth has been enabled")
                } else {
                    toast("Bluetooth has been disabled")
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                toast("Bluetooth enabling has been canceled")
            }
        }
    }
}