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
import com.william.vehiclecontroller.R
import kotlinx.android.synthetic.main.select_device_layout.*
import org.jetbrains.anko.toast

class SelectDeviceActivity : AppCompatActivity() {

    private var bluetoothAdapter: BluetoothAdapter? = null
    // ========================================================
    private var bluetoothReceiver: BroadcastReceiver? = null
    private val discoveredDeviceList: ArrayList<String> = ArrayList()
    // ========================================================
    //private lateinit var pairedDevices: Set<BluetoothDevice>
    private val requestEnableBluetooth = 1

    companion object {
        const val ADDRESS: String = "device_address"
        private val discoveredDevices: ArrayList<BluetoothDevice> = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_device_layout)

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

        // Create Bluetooth BroadCast receiver, and filter Intents
        // ========================================================
        bluetoothReceiver = BluetoothBroadcastReceiver()
        val intentFilter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        this.registerReceiver(bluetoothReceiver, intentFilter)
        // ========================================================

        // Start discovery process
        bluetoothAdapter!!.startDiscovery()

        // Will want to changes this to accommodate discovery (original)
        // Edit: Refresh the discovered device list
        select_device_refresh.setOnClickListener { listDiscoveredDevices()/*pairedDeviceList()*/ }
        // =================================
    }

    // ========================================================
    private class BluetoothBroadcastReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent!!.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                val device: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)  //.getParcelableArrayExtra(BluetoothDevice.EXTRA_DEVICE)
                discoveredDevices.add(device)
            }
        }
    }

    // ========================================================
    // List's the discovered devices found by the BroadCast receiver
    private fun listDiscoveredDevices() {
        discoveredDevices.mapTo(discoveredDeviceList, { it.name + " - " + it.address})
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, discoveredDeviceList)
        select_device_list.adapter = adapter
        select_device_list.onItemClickListener = AdapterView.OnItemClickListener {
                _, _, position, _  ->
            val device: BluetoothDevice = discoveredDevices[position]
            val address: String = device.address
            val intent = Intent(this, ControllerActivity::class.java)
            intent.putExtra(ADDRESS, address)
            startActivity(intent)
        }
    }
    // ========================================================

//    private fun pairedDeviceList() {
//        pairedDevices = bluetoothAdapter!!.bondedDevices
//        val list: ArrayList<BluetoothDevice> = ArrayList()
//        if (pairedDevices.isNotEmpty()) {
//            for (device: BluetoothDevice in pairedDevices) {
//                list.add(device)
//                Log.i("Device", "" + device)
//            }
//        } else {
//            toast("No paired Bluetooth devices found")
//        }
//        val nameList: ArrayList<String> = ArrayList()
//        list.mapTo(nameList, { it.name })
//        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, nameList)
//        select_device_list.adapter = adapter
//        select_device_list.onItemClickListener = AdapterView.OnItemClickListener {
//                _, _, position, _  ->
//                    val device: BluetoothDevice = list[position]
//                    val address: String = device.address
//
//                    val intent = Intent(this, ControllerActivity::class.java)
//                    intent.putExtra(ADDRESS, address)
//                    startActivity(intent)
//        }
//    }

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
