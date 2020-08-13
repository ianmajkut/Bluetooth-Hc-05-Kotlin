package com.example.proyectobluetoothautokotlin
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler

import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.control_layout.*
import java.io.IOException
import java.util.*


class ControlActivity: AppCompatActivity(){

    companion object {
        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var m_bluetoothSocket: BluetoothSocket? = null
        lateinit var m_progress: ProgressDialog
        lateinit var m_bluetoothAdapter: BluetoothAdapter
        var m_isConnected: Boolean = false
        lateinit var m_address: String
    }

    private var number=0;
    private lateinit var mHandler: Handler
    private lateinit var mRunnable:Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.control_layout)
        m_address = intent.getStringExtra(MainActivity.EXTRA_ADDRESS)

        ConnectToDevice(this).execute()

        mHandler=Handler()
        buttonPress()

        /*
        imageButton.setOnTouchListener(object: View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when(event?.action){
                    MotionEvent.ACTION_DOWN ->

                    sendCommand("1")
                }
                return v?.onTouchEvent(event)?:true
            }
        })

/*
        imageButton.setOnLongClickListener{
            sendCommand("1")
            true
        }

 */
        imageButton2.setOnTouchListener(object: View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when(event?.action){
                    MotionEvent.ACTION_DOWN -> sendCommand("2")
                }
                return v?.onTouchEvent(event)?:true
            }
        })

        imageButton3.setOnTouchListener(object: View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when(event?.action){
                    MotionEvent.ACTION_DOWN -> sendCommand("3")
                }
                return v?.onTouchEvent(event)?:true
            }
        })

        imageButton4.setOnTouchListener(object: View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when(event?.action){
                    MotionEvent.ACTION_DOWN -> sendCommand("0")
                }
                return v?.onTouchEvent(event)?:true
            }
        })

        /*
        imageButton2.setOnLongClickListener {
            sendCommand("2")
            true
        }
        imageButton3.setOnLongClickListener {
            sendCommand("3")
            true
        }
        imageButton4.setOnLongClickListener{
            sendCommand("0")
            true
        }*/
        control_disconnect.setOnClickListener{ disconnect() }

         */
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun buttonPress() {
        imageButton.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    mRunnable = Runnable {
                        Toast.makeText(this, "Apretaste el botón", Toast.LENGTH_SHORT).show()
                        sendCommand("1")
                        mHandler.postDelayed(mRunnable, 100)
                    }
                    mHandler.postDelayed(mRunnable, 0)
                    true
                }

                MotionEvent.ACTION_UP -> {
                    Toast.makeText(this, "Dejaste de apretar el botón", Toast.LENGTH_SHORT).show()
                    mHandler.removeCallbacks(mRunnable)
                    true
                }
                else -> true
            }

        }

        imageButton2.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    mRunnable = Runnable {
                        Toast.makeText(this, "Apretaste el botón", Toast.LENGTH_SHORT).show()
                        sendCommand("2")
                        mHandler.postDelayed(mRunnable, 100)
                    }
                    mHandler.postDelayed(mRunnable, 0)
                    true
                }

                MotionEvent.ACTION_UP -> {
                    Toast.makeText(this, "Dejaste de apretar el botón", Toast.LENGTH_SHORT).show()
                    mHandler.removeCallbacks(mRunnable)
                    true
                }
                else -> true
            }

        }

        imageButton3.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    mRunnable = Runnable {
                        Toast.makeText(this, "Apretaste el botón", Toast.LENGTH_SHORT).show()
                        sendCommand("3")
                        mHandler.postDelayed(mRunnable, 100)
                    }
                    mHandler.postDelayed(mRunnable, 0)
                    true
                }

                MotionEvent.ACTION_UP -> {
                    Toast.makeText(this, "Dejaste de apretar el botón", Toast.LENGTH_SHORT).show()
                    mHandler.removeCallbacks(mRunnable)
                    true
                }
                else -> true
            }

        }

        imageButton4.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    mRunnable = Runnable {
                        Toast.makeText(this, "Apretaste el botón", Toast.LENGTH_SHORT).show()
                        sendCommand("0")
                        mHandler.postDelayed(mRunnable, 100)
                    }
                    mHandler.postDelayed(mRunnable, 0)
                    true
                }

                MotionEvent.ACTION_UP -> {
                    Toast.makeText(this, "Dejaste de apretar el botón", Toast.LENGTH_SHORT).show()
                    mHandler.removeCallbacks(mRunnable)
                    true
                }
                else -> true
            }

        }

        control_disconnect.setOnClickListener{ disconnect() }

    }

    private fun sendCommand(input: String){
        if (m_bluetoothSocket != null){
            try {
                m_bluetoothSocket!!.outputStream.write(input.toByteArray())
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
    }
    private fun disconnect(){
        if (m_bluetoothSocket != null){
            try {
                m_bluetoothSocket!!.close()
                m_bluetoothSocket = null
                m_isConnected = false
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
        finish()
    }
    private class ConnectToDevice(c: Context):AsyncTask<Void, Void, String>(){
        private var connectSuccess: Boolean = true
        private val context: Context

        init {
            this.context = c
        }

        override fun onPreExecute() {
            super.onPreExecute()
            m_progress = ProgressDialog.show(context, "Connecting...", "please wait")
        }

        override fun doInBackground(vararg p0: Void?): String? {
            try {
                if(m_bluetoothSocket == null || !m_isConnected){
                    m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    val device: BluetoothDevice = m_bluetoothAdapter.getRemoteDevice(m_address)
                    m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(m_myUUID)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    m_bluetoothSocket!!.connect()
                }
            }catch (e: IOException) {
                connectSuccess = false
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if(!connectSuccess){
                Log.i("data","couldn't connect")
            }else{
                m_isConnected = true
            }
            m_progress.dismiss()
        }
    }
}