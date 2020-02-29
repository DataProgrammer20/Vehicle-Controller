package com.william.vehiclecontroller.ui

import android.app.ProgressDialog
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.william.vehiclecontroller.R
import com.william.vehiclecontroller.data.ControllerData
import kotlinx.android.synthetic.main.controller_layout.*
import java.io.IOException
import java.net.*

class ControllerActivity: AppCompatActivity() {

    companion object {
        private const val port = 2390
        private val IPByteAddress = "10.200.79.254".toByteArray()
        private val address = InetAddress.getByAddress(IPByteAddress)!!
        private val IPSocketAddress = InetSocketAddress("10.200.79.254", port)
        var UDPSocket: DatagramSocket? = null
        lateinit var progress: ProgressDialog
        var isConnected: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.controller_layout)
        val manager = DeviceManager(this)
        manager.execute()

        left_joystick.setOnMoveListener { angle, strength -> manager.sendCommand(ControllerData(angle, strength)) }
        right_joystick.setOnMoveListener { angle, strength -> manager.sendCommand(ControllerData(angle, strength)) }
        control_disconnect.setOnClickListener { manager.disconnect() }
    }

    private class DeviceManager(private val context: Context): AsyncTask<Void, Void, String>() {
        private var connectSuccess: Boolean = true

        fun sendCommand(data: ControllerData) {
            val byteArray = (data.angle.toString() + "-" + data.strength.toString()).toByteArray()
            if (UDPSocket != null) {
                try {
                    val packet = DatagramPacket(byteArray, 255, address, port)
                    UDPSocket!!.send(packet)
                    Log.i("Task Completed", "UDP Packet sent...")
                } catch (exception: IOException) {
                    exception.printStackTrace()
                }
            }
        }

        fun disconnect() {
            try {
                if (UDPSocket != null) {
                    UDPSocket!!.close()
                    UDPSocket = null
                    isConnected = false
                }
            } catch (exception: IOException) {
                exception.printStackTrace()
            }
        }

        override fun onPreExecute() {
            super.onPreExecute()
            progress = ProgressDialog.show(context, "Connecting...", "Please wait")
        }

        override fun doInBackground(vararg p0: Void?): String? {
            try {
                if (UDPSocket == null || !isConnected) {
                    UDPSocket = DatagramSocket(port)
                    UDPSocket!!.connect(IPSocketAddress)
                    Log.i("WiFi status", "We are trying to connect...")
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
                Log.i("Error", "Failed to connect to the device")
            } else {
                isConnected = true
                Log.i("success","Successfully connected to the device")
            }
            progress.dismiss()
        }
    }
}