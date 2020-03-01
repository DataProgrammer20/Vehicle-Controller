package com.william.vehiclecontroller.ui

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.william.vehiclecontroller.R
import com.william.vehiclecontroller.data.ControllerData
import kotlinx.android.synthetic.main.controller_layout.*
import java.io.IOException
import java.lang.Exception
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.DatagramPacket
import java.net.DatagramSocket

class ControllerActivity: AppCompatActivity() {

    companion object {
        private const val port = 2390
        private lateinit var ipAddressString: String
        private lateinit var address: InetAddress
        private lateinit var IPSocketAddress: InetSocketAddress
        private var UDPSocket: DatagramSocket? = null
        private lateinit var progress: ProgressDialog
        private var isConnected: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.controller_layout)
            ipAddressString = intent.getStringExtra(SelectDeviceActivity.ipAddress)
            address = InetAddress.getByName(ipAddressString)
            IPSocketAddress = InetSocketAddress(ipAddressString, port)
            val manager = DeviceManager(this)
            manager.execute()

            left_joystick.setOnMoveListener { angle, strength -> sendCommand(ControllerData(angle, strength)) }
            right_joystick.setOnMoveListener { angle, strength -> sendCommand(ControllerData(angle, strength)) }
            control_disconnect.setOnClickListener { disconnect() }
        } catch(exception: Exception) {
            exception.printStackTrace()
        }
    }

    private fun sendCommand(data: ControllerData) {
        val byteBuffer = (data.angle.toString() + "-" + data.strength.toString()).toByteArray()
        if (UDPSocket != null) {
            try {
                val packet = DatagramPacket(byteBuffer, byteBuffer.size, address, port)
                UDPSocket!!.send(packet)
                Log.i("Packet Status", "UDP Packet sent")
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
                val intent = Intent(this, SelectDeviceActivity::class.java)
                startActivity(intent)
            }
        } catch (exception: IOException) {
            exception.printStackTrace()
        }
    }

    private class DeviceManager(private val context: Context): AsyncTask<Void, Void, String>() {
        private var connectSuccess: Boolean = true

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