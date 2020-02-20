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
import com.william.vehiclecontroller.data.ControllerData
import kotlinx.android.synthetic.main.controller_layout.*
import java.io.IOException
import java.net.DatagramPacket
import java.util.*

// WiFi modules import
import java.net.DatagramSocket
import java.net.InetSocketAddress

class ControllerActivity: AppCompatActivity() {

    companion object {
        var id: UUID = UUID.randomUUID()

        // WiFi Stuff
        private const val port = 2390
        private val IP = InetSocketAddress("10.200.76.61", port)
        var UDPSocket = DatagramSocket(port)
        // =================

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

        left_joystick.setOnMoveListener { angle, strength -> sendCommand(ControllerData(angle, strength)) }
        right_joystick.setOnMoveListener { angle, strength -> sendCommand(ControllerData(angle, strength)) }
        control_disconnect.setOnClickListener { disconnect() }

        // WiFi Stuff
        UDPSocket.connect(IP)
        if (!UDPSocket.isConnected) {
            Log.i("WiFi Error", "We've got a problem...")
        }
        val byteArray = "hi".toByteArray()
        UDPSocket.send(DatagramPacket(byteArray, 255))
        UDPSocket.close()
        // =============
    }

    // Going to have to make this async (maybe)
    private fun sendCommand(data: ControllerData) {
        val dataString = (data.angle.toString() + "-" + data.strength.toString()).toByteArray()
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket!!.outputStream.write(dataString)
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
                Log.i("Error", "Could not connect to device")
            } else {
                isConnected = true
            }
            progress.dismiss()
        }
    }
}