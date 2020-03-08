package com.william.vehiclecontroller.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.william.vehiclecontroller.R
import kotlinx.android.synthetic.main.select_device_layout.*
import java.lang.Exception

class SelectDeviceActivity : AppCompatActivity() {

    //private val discoveredDeviceList: ArrayList<String> = ArrayList()

    companion object {
        const val ipAddress: String = "ip_address"
        //private val discoveredDevices: ArrayList<BluetoothDevice> = ArrayList()
    }

    // Broadcast receiver, probably don't need this either
//    private class BluetoothBroadcastReceiver: BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent?) {
//            val action = intent!!.action
//            if (BluetoothDevice.ACTION_FOUND == action) {
//                val device: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
//                Log.i("Device", "device found: $device")
//                discoveredDevices.add(device)
//            }
//            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED == action) {
//                Log.i("Discovery", "Starting Bluetooth discovery")
//            }
//            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED == action) {
//                Log.i("Discovery", "Finishing Bluetooth discovery")
//            }
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.select_device_layout)

//            val sniffer = NetworkSniffer(this)
//            sniffer.execute()
//            bluetoothReceiver = BluetoothBroadcastReceiver()
//            val intentFilter = IntentFilter()
//            intentFilter.addAction(BluetoothDevice.ACTION_FOUND)
//            intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
//            intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
//            this.registerReceiver(bluetoothReceiver, intentFilter)
//            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
//            if (bluetoothAdapter == null) {
//                toast("This device does not support Bluetooth")
//                return
//            }
//            if (!bluetoothAdapter!!.isEnabled) {
//                val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//                startActivityForResult(enableBluetoothIntent, requestEnableBluetooth)
//            }
//            if (bluetoothAdapter!!.isDiscovering) {
//                bluetoothAdapter!!.cancelDiscovery()
//            }
            //pairedDevicesList()
            //discoveredDevicesList()
            //select_device_refresh.setOnClickListener { pairedDevicesList() }
            //select_device_scan.setOnClickListener { discoveredDevicesList() }
            //bluetoothAdapter!!.startDiscovery()
            connect.setOnClickListener { createConnection() }

        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private fun createConnection() {
        val text = editText.text.toString()
        val intent = Intent(this, ControllerActivity::class.java)
        intent.putExtra(ipAddress, text)
        startActivity(intent)
    }

//    // List's the discovered devices found by the BroadCast receiver
//    private fun discoveredDevicesList() {
//        val adapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_list_item_1, discoveredDeviceList)
//        if (discoveredDeviceList.size > 0) {
//            for (item in discoveredDeviceList) {
//                discoveredDeviceList.remove(item)
//            }
//        }
//        if (discoveredDevices.isEmpty()) {
//            discoveredDeviceList.add("No Bluetooth devices discovered")
//            select_device_discovery_list.adapter = adapter
//        } else {
//            //listDevices(select_device_discovery_list, discoveredDevices, discoveredDeviceList, adapter)
//        }
//    }

//    // We will still use this...
//    private fun listDevices(
//        view: ListView,
//        fromList: ArrayList<BluetoothDevice>,
//        toList: ArrayList<String>,
//        adapter: ArrayAdapter<String>) {
//        try {
//            fromList.mapTo(toList, { it.name + " - " + it.address })
//            view.adapter = adapter
//            view.onItemClickListener =
//                AdapterView.OnItemClickListener { _, _, position, _ ->
//                    val device: BluetoothDevice = fromList[position]
//                    val address: String = device.address
//                    // This is where we start the new intent and activity
//                    bluetoothAdapter!!.cancelDiscovery()
//                    val intent = Intent(this, ControllerActivity::class.java)
//                    //intent.putExtra(ADDRESS, address)
//                    startActivity(intent)
//                }
//        } catch (exception: Exception) {
//            exception.printStackTrace()
//        }
//    }
}