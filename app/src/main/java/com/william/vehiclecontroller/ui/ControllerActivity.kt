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
import org.jetbrains.anko.async
import java.io.IOException
import java.net.DatagramPacket
import java.util.*

// WiFi modules import
import java.net.DatagramSocket
import java.net.InetSocketAddress

class ControllerActivity: AppCompatActivity() {

    companion object {
        // var id: UUID = UUID.randomUUID()
        // WiFi Stuff =====================
        private const val port = 2390
        private val IP = InetSocketAddress("10.200.76.61", port)
        var UDPSocket: DatagramSocket? = null
        // ==============================
        lateinit var progress: ProgressDialog
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
    }

    // Going to have to make this async (maybe)
    private fun sendCommand(data: ControllerData) {
        val byteArray = (data.angle.toString() + "-" + data.strength.toString()).toByteArray()
        if (UDPSocket != null) {
            try {
                UDPSocket!!.send(DatagramPacket(byteArray, 255))
                Log.i("Task Completed", "UDP Packet sent...")
            } catch (exception: IOException) {
                exception.printStackTrace()
            }
        }
    }

    private fun disconnect() {
        try {
            if (UDPSocket != null) {
                UDPSocket!!.close()
                UDPSocket = null
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
                if (UDPSocket == null || !isConnected) {
                    UDPSocket = DatagramSocket(port)
                    UDPSocket!!.connect(IP)
                    if (!UDPSocket!!.isConnected) { Log.i("WiFi Error", "We've got a problem...") }
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