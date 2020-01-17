package com.william.vehiclecontroller.ui

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
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
    private lateinit var pairedDevices: Set<BluetoothDevice>
    private val requestEnableBluetooth = 1

    companion object {
        const val ADDRESS: String = "device_address"
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

        select_device_refresh.setOnClickListener{ pairedDeviceList() }
    }

    private fun pairedDeviceList() {
        pairedDevices = bluetoothAdapter!!.bondedDevices
        val list: ArrayList<BluetoothDevice> = ArrayList()
        if (pairedDevices.isNotEmpty()) {
            for (device: BluetoothDevice in pairedDevices) {
                list.add(device)
                Log.i("Device", "" + device)
            }
        } else {
            toast("No paired Bluetooth devices found")
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        select_device_list.adapter = adapter
        select_device_list.onItemClickListener = AdapterView.OnItemClickListener {
                _, _, position, _  ->
            val device: BluetoothDevice = list[position]
            val address: String = device.address

            val intent = Intent(this, ControllerActivity::class.java)
            intent.putExtra(ADDRESS, address)
            startActivity(intent)
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
