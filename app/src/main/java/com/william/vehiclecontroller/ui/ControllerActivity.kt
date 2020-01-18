package com.william.vehiclecontroller.ui

import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.william.vehiclecontroller.R
import kotlinx.android.synthetic.main.controller_layout.*
import java.io.IOException
import java.util.*

class ControllerActivity: AppCompatActivity() {

    companion object {
        var id: UUID = UUID.randomUUID()
        var bluetoothSocket: BluetoothSocket? = null
        lateinit var progress: ProgressDialog
        lateinit var bluetoothAdapter: BluetoothAdapter
        var isConnected: Boolean = false
        lateinit var address: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.controller_layout)
        address = intent.getStringExtra(SelectDeviceActivity.ADDRESS)
        ConnectToDevice(this).execute()

        control_left.setOnClickListener { sendCommand("left_command") }
        control_right.setOnClickListener { sendCommand("right_command") }
        control_disconnect.setOnClickListener { disconnect() }
    }

    // Going to have to make this async
    private fun sendCommand(input: String) {
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket!!.outputStream.write(input.toByteArray())
            } catch (exception: IOException) {
                exception.printStackTrace()
            }
        }
    }

    private fun disconnect() {
        try {
            if (bluetoothSocket != null) {
                bluetoothSocket!!.close()
                bluetoothSocket = null
                isConnected = false
            }
        } catch (exception: IOException) {
            exception.printStackTrace()
        }
        finish()
    }

    private class ConnectToDevice(private val context: Context): AsyncTask<Void, Void, String>() {
        private var connectSuccess: Boolean = true

        override fun onPreExecute() {
            super.onPreExecute()
            progress = ProgressDialog.show(context, "Connecting...", "Please wait")
        }

        override fun doInBackground(vararg p0: Void?): String? {
            try {
                if (bluetoothSocket == null || !isConnected) {
                    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    val device: BluetoothDevice = bluetoothAdapter.getRemoteDevice(address)
                    bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(id)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    bluetoothSocket!!.connect()
                }
            } catch (exception: IOException) {
                connectSuccess = false
                exception.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (!connectSuccess) {
                Log.i("data", "Could not connect to device")
            } else {
                isConnected = true
            }
            progress.dismiss()
        }
    }
}